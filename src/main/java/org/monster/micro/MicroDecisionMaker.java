package org.monster.micro;

import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import org.monster.board.StrategyBoard;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.UnitTypeUtils;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.UnitUtils;
import org.monster.strategy.constant.StrategyCode;
import org.monster.strategy.constant.StrategyConfig;
import org.monster.strategy.manage.AirForceManager;
import org.monster.strategy.manage.AirForceTeam;
import org.monster.micro.compute.WraithFightPredictor;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.targeting.TargetScoreCalculator;
import org.monster.micro.targeting.WraithTargetCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MicroDecisionMaker {

    private static final int BACKOFF_DIST = 64;
    private static final int TOO_TOO_FAR_DISTANCE = 450;

    private TargetScoreCalculator targetScoreCalculator;

    public MicroDecisionMaker(TargetScoreCalculator targetScoreCalculator) {
        this.targetScoreCalculator = targetScoreCalculator;
    }

    public MicroDecision makeDecisionForSiegeMode(Unit myUnit, Collection<UnitInfo> euiList) {
        UnitInfo bestTargetUnitInfo = null;
        int highestScore = 0;

        Unit closeUndetectedEnemy = null;
        Unit tooCloseTarget = null;
        Unit tooFarTarget = null;
        int closestTooFarTargetDistance = CommonCode.INT_MAX;
        boolean targetInRangeButOutOfSight = false;

        for (UnitInfo eui : euiList) {
            if (!MicroUtils.canAttack(myUnit, eui)) {
                continue;
            }
            Unit enemyUnit = UnitUtils.enemyUnitInSight(eui);
            if (enemyUnit == null) {
                if (bestTargetUnitInfo == null) {
                    int distanceToTarget = myUnit.getDistance(eui.getLastPosition());
                    if (distanceToTarget <= MicroConfig.Tank.SIEGE_MODE_MAX_RANGE) {
                        bestTargetUnitInfo = eui;
                        targetInRangeButOutOfSight = true;
                    }
                }
                continue;
            }
            if (isCloseUndetectedGroundUnit(myUnit, eui)) {
                closeUndetectedEnemy = enemyUnit;
                continue;
            }

            int distanceToTarget = myUnit.getDistance(enemyUnit.getPosition());
            if (!myUnit.isInWeaponRange(enemyUnit)) { // 시즈 범위안에 타겟이 없을 경우 skip
                if (distanceToTarget < MicroConfig.Tank.SIEGE_MODE_MIN_RANGE) {
                    tooCloseTarget = enemyUnit;
                } else if (distanceToTarget > MicroConfig.Tank.SIEGE_MODE_MAX_RANGE) {
                    if (tooFarTarget == null || distanceToTarget < closestTooFarTargetDistance) {
                        tooFarTarget = enemyUnit;
                        closestTooFarTargetDistance = distanceToTarget;
                    }
                }
                continue;
            }

            int score = targetScoreCalculator.calculate(myUnit, eui);
            if (score > highestScore) {
                bestTargetUnitInfo = eui;
                highestScore = score;
                targetInRangeButOutOfSight = false;
            }
        }

        MicroDecision decision;
        if (bestTargetUnitInfo != null) {
            if (targetInRangeButOutOfSight || highestScore <= 0) {
                decision = MicroDecision.stop(myUnit);
            } else {
                decision = MicroDecision.attackUnit(myUnit, bestTargetUnitInfo);
            }
        } else {
            if (closeUndetectedEnemy != null || tooCloseTarget != null) {
                decision = MicroDecision.change(myUnit);
            } else if (tooFarTarget != null) {
                if (closestTooFarTargetDistance > TOO_TOO_FAR_DISTANCE) {
                    decision = MicroDecision.change(myUnit);
                } else {
                    decision = MicroDecision.hold(myUnit);
                }
            } else {
                decision = MicroDecision.attackPosition(myUnit);
            }
        }
        return decision;
    }

    public MicroDecision makeDecisionForAirForce(AirForceTeam airForceTeam, Collection<UnitInfo> euiList, int strikeLevel) {
        int airunitMemorySeconds = 3;
        if (strikeLevel < AirForceManager.StrikeLevel.SORE_SPOT) {
            airunitMemorySeconds = StrategyConfig.IGNORE_ENEMY_UNITINFO_SECONDS;
        }

        List<UnitInfo> euiListAirDefenseBuilding = new ArrayList<>();
        List<UnitInfo> euiListAirWeapon = new ArrayList<>();
        List<UnitInfo> euiListFeed = new ArrayList<>();
        List<UnitInfo> euiListDetector = new ArrayList<>();
        for (UnitInfo eui : euiList) {
            if (UnitUtils.ignorableEnemyUnitInfo(eui, airunitMemorySeconds)) { // 레이쓰는 기억력이 안좋다.
                continue;
            }

            boolean isAirDefenseBuilding = false;
            for (UnitType airDefenseUnitType : UnitTypeUtils.enemyAirDefenseUnitType()) {
                if (eui.getType() == airDefenseUnitType) {
                    isAirDefenseBuilding = true;
                    break;
                }
            }

            if (eui.isCompleted() || eui.getLastHealth() >= eui.getType().maxHitPoints() * 0.8) {
                if (isAirDefenseBuilding) {
                    euiListAirDefenseBuilding.add(eui);
                } else if (MicroUtils.airEnemyType(eui.getType())) {
                    euiListAirWeapon.add(eui);
                } else {
                    euiListFeed.add(eui);
                }
            } else {
                euiListFeed.add(eui);
            }

            if (eui.getType().isDetector()) {
                if (eui.getLastPosition().getDistance(airForceTeam.leaderUnit.getPosition()) > 500) {
                    continue;
                }
                euiListDetector.add(eui);
            }
        }

        // air driving 오차로 상대 건물 공격범위안으로 들어왔을 경우
        for (UnitInfo eui : euiListAirDefenseBuilding) {
            Unit enemyUnit = UnitUtils.enemyUnitInSight(eui);
            if (enemyUnit != null) {
                // 벙커 별도 처리
                if (enemyUnit.getType() == UnitType.Terran_Bunker) {
                    int range = PlayerUtils.enemyPlayer().weaponMaxRange(UnitType.Terran_Marine.groundWeapon()) + 64;// + AirForceManager.AIR_FORCE_SAFE_DISTANCE;
                    if (enemyUnit.getDistance(airForceTeam.leaderUnit) < range) {
                        return MicroDecision.fleeFromUnit(airForceTeam.leaderUnit, eui);
                    }
                } else {
                    if (enemyUnit.isInWeaponRange(airForceTeam.leaderUnit)) {
                        return MicroDecision.fleeFromUnit(airForceTeam.leaderUnit, eui);
                    }
                }
            }
        }

        if (airForceTeam.repairCenter != null) {
            return MicroDecision.attackPosition(airForceTeam.leaderUnit);
        }

        UnitInfo bestTargetInfo = null;

        if (AirForceManager.Instance().isAirForceDefenseMode()) {

            if (PlayerUtils.enemyRace() == Race.Zerg) {
                List<UnitInfo> euisEnemiesAndFeeds = new ArrayList<>();
                euisEnemiesAndFeeds.addAll(euiListAirWeapon);
                euisEnemiesAndFeeds.addAll(euiListFeed);
                bestTargetInfo = getBestTargetInfo(airForceTeam, euisEnemiesAndFeeds, euiListAirDefenseBuilding);
            } else {
                if (!euiListAirWeapon.isEmpty()) {
                    if (airForceTeam.cloakable()) {
                        return MicroDecision.change(airForceTeam.leaderUnit);
                    }
                    bestTargetInfo = getBestTargetInfo(airForceTeam, euiListAirWeapon, euiListAirDefenseBuilding);
                } else {
                    if (airForceTeam.uncloakable()) {
                        return MicroDecision.change(airForceTeam.leaderUnit);
                    }
                    bestTargetInfo = getBestTargetInfo(airForceTeam, euiListFeed, euiListAirDefenseBuilding);
                }
            }

        } else {
            if (!euiListAirWeapon.isEmpty()) {
                boolean detectUnitExist = !euiListDetector.isEmpty() || airForceTeam.damagedEffectiveFrame > 10; // 10 hitpoints reduced
                boolean cloakingBonus = airForceTeam.cloakingMode && !detectUnitExist;
                boolean mainSquadBonus = airForceTeam.leaderUnit.getDistance(StrategyBoard.mainSquadCenter) < 150;
                // System.out.println("cloakingBonus : " + cloakingBonus);

                StrategyCode.SmallFightPredict fightPredict = WraithFightPredictor.airForcePredictByUnitInfo(airForceTeam.memberList, euiListAirWeapon, cloakingBonus, mainSquadBonus);
                // System.out.println("fightPredict = " + fightPredict);
                if (fightPredict == StrategyCode.SmallFightPredict.ATTACK) {
                    bestTargetInfo = getBestTargetInfo(airForceTeam, euiListAirWeapon, euiListAirDefenseBuilding);

                } else {
                    if (euiListDetector.isEmpty() && !cloakingBonus && airForceTeam.cloakable()) {
                        StrategyCode.SmallFightPredict cloakingFightPredict = WraithFightPredictor.airForcePredictByUnitInfo(airForceTeam.memberList, euiListAirWeapon, true, mainSquadBonus);
                        if (cloakingFightPredict == StrategyCode.SmallFightPredict.ATTACK) {
                            return MicroDecision.change(airForceTeam.leaderUnit);
                        }
                    }

                    for (UnitInfo eui : euiListAirWeapon) {
                        Unit unitInSight = UnitUtils.enemyUnitInSight(eui);
                        if (unitInSight != null) {
                            if (isInWeaponRangeSafely(unitInSight, airForceTeam.leaderUnit)) {
                                return MicroDecision.fleeFromUnit(airForceTeam.leaderUnit, eui);
                            }
                        }
                    }
                    bestTargetInfo = getBestTargetInfo(airForceTeam, euiListFeed, euiListAirDefenseBuilding, AirForceManager.StrikeLevel.SORE_SPOT);
                }

            } else {
                bestTargetInfo = getBestTargetInfo(airForceTeam, euiListFeed, euiListAirDefenseBuilding);
            }
        }

        if (bestTargetInfo != null) {
            boolean bestTargetProtectedByBuilding = protectedByBuilding(bestTargetInfo, euiListAirDefenseBuilding);
            if (bestTargetProtectedByBuilding) {
                return MicroDecision.attackUnit(airForceTeam.leaderUnit, bestTargetInfo);
            } else {
                return MicroDecision.kitingUnit(airForceTeam.leaderUnit, bestTargetInfo);
            }
        } else {
            return MicroDecision.attackPosition(airForceTeam.leaderUnit);
        }
    }

    private boolean isInWeaponRangeSafely(Unit enemyUnit, Unit myUnit) {
        if (enemyUnit.isInWeaponRange(myUnit)) {
            return true;
        } else {
            // isInWeaponRange는 제외해도 괜찮다.
            int enemyUnitDistance = myUnit.getDistance(enemyUnit);
            int weaponMaxRange = PlayerUtils.enemyPlayer().weaponMaxRange(enemyUnit.getType().airWeapon()) + 30;

            if (enemyUnit.getType() == UnitType.Terran_Goliath) {
                weaponMaxRange += 300;
            }
            if (enemyUnitDistance < weaponMaxRange) {
                return true;
            }
        }
        return false;
    }

    private UnitInfo getBestTargetInfo(AirForceTeam airForceTeam, List<UnitInfo> euiListTarget, List<UnitInfo> euiListAirDefenseBuilding) {
        return getBestTargetInfo(airForceTeam, euiListTarget, euiListAirDefenseBuilding, AirForceManager.Instance().getStrikeLevel());
    }

    private UnitInfo getBestTargetInfo(AirForceTeam airForceTeam, List<UnitInfo> euiListTarget, List<UnitInfo> euiListAirDefenseBuilding, int strikeLevel) {
        UnitInfo bestTargetInfo = null;
        int highestFeedScore = 0;

        boolean inWeaponRange = false;
        boolean protectedByBuilding = false;
        for (UnitInfo eui : euiListTarget) {
            Unit enemyInSight = UnitUtils.enemyUnitInSight(eui);
            if (enemyInSight != null) {
                if (!enemyInSight.isDetected()) {
                    continue;
                }
                inWeaponRange = airForceTeam.isInAirForceWeaponRange(enemyInSight);
            }

            protectedByBuilding = protectedByBuilding(eui, euiListAirDefenseBuilding);
            if (!inWeaponRange && protectedByBuilding) { // 건물에 의해 보호받는 빌딩은 제외. 공격범위내에 있으면 예외
                continue;
            }

            WraithTargetCalculator wraithTargetCalculator = (WraithTargetCalculator) targetScoreCalculator;
            wraithTargetCalculator.setStrikeLevel(strikeLevel);
            wraithTargetCalculator.setAirForceDefenseMode(AirForceManager.Instance().isAirForceDefenseMode());

            int score = wraithTargetCalculator.calculate(airForceTeam.leaderUnit, eui);
            if (score > highestFeedScore) {
                bestTargetInfo = eui;
                highestFeedScore = score;
            }
        }
        return bestTargetInfo;
    }

    private boolean protectedByBuilding(UnitInfo eui, List<UnitInfo> euiListAirDefenseBuilding) {
        for (UnitInfo euiBuilding : euiListAirDefenseBuilding) {
            int buildingWeaponRange = euiBuilding.getType().airWeapon().maxRange() + 25;// + AirForceManager.AIR_FORCE_SAFE_DISTANCE;
            double distanceWithBuilding = eui.getLastPosition().getDistance(euiBuilding.getLastPosition());
            if (distanceWithBuilding < buildingWeaponRange) {
                return true;
            }
        }
        return false;
    }

    public MicroDecision makeDecisionForAirForceMovingDetail(AirForceTeam airForceTeam, Collection<UnitInfo> euiList, boolean movingAttack) {
        boolean allUnitCoolTimeReady = true;
        for (Unit airunit : airForceTeam.memberList) {
            if (airunit.getGroundWeaponCooldown() > 0) {
                allUnitCoolTimeReady = false;
                break;
            }
        }

        if (allUnitCoolTimeReady && airForceTeam.repairCenter == null) {
            UnitInfo targetInfo = null;
            int minimumLinearDistance = CommonCode.INT_MAX;
            for (UnitInfo eui : euiList) {
                if (eui.getType() == UnitType.Zerg_Larva || eui.getType() == UnitType.Zerg_Egg || eui.getType() == UnitType.Zerg_Lurker_Egg) {
                    continue;
                }

                Unit enemyUnit = UnitUtils.enemyUnitInSight(eui);
                if (enemyUnit == null) {
                    continue;
                }

//				// 대공능력이 있는 적이 있다면 이동시 공격을 하지 않는다.
//				// (만약 대공능력이 있는 적을 공격을 해야 한다면 이전 단계 판단에서 지정된다.)
//				if (eui.getType().airWeapon() != WeaponType.None) {
//					targetInfo = null;
//					break;
//				}

                if (movingAttack) { // 이동시 진행각도에 있는 적만 공격하고 싶으세요?
                    // 약 45도 이상 벌어진 적은 이동하며 공격하지 않음 (3.14 = 90도, 0.78 = 45도)
                    double leaderTargetRadian = MicroUtils.targetDirectionRadian(airForceTeam.leaderUnit.getPosition(), airForceTeam.getTargetPosition());
                    double leaderEnemyRadian = MicroUtils.targetDirectionRadian(airForceTeam.leaderUnit.getPosition(), enemyUnit.getPosition());
                    if (Math.abs(leaderTargetRadian - leaderEnemyRadian) > 0.78) {
//						System.out.println("wraith - targetposition : " + leaderTargetRadian);
//						System.out.println("wraith - " + eui.getType() + " : " + leaderEnemyRadian);
                        continue;
                    }

                    // 공격범위 내에 있어야 한다.
                    if (!airForceTeam.isInAirForceWeaponRange(enemyUnit)) {
                        continue;
                    }
                }
                // 최소 진행각도의 적을 타게팅하기 위함
                int distanceEnemyToLeader = enemyUnit.getDistance(airForceTeam.leaderUnit);
                int distanceEnemyToTargetPosition = enemyUnit.getDistance(airForceTeam.getTargetPosition());
                if (distanceEnemyToLeader + distanceEnemyToTargetPosition < minimumLinearDistance) {
                    targetInfo = eui;
                    minimumLinearDistance = distanceEnemyToLeader + distanceEnemyToTargetPosition;
                }
            }
            if (targetInfo != null) {
                return MicroDecision.attackUnit(airForceTeam.leaderUnit, targetInfo); // 유닛 공격
            }
        }

        // 공격할 적이 없을 경우 이동한다.
        // air force team이 너무 분산되어 있는 경우 모으도록 한다.
        int averageDistance = 0;
        int memberSize = airForceTeam.memberList.size();
        if (memberSize > 1) {
            int sumOfDistance = 0;
            for (Unit member : airForceTeam.memberList) {
                if (member.getID() != airForceTeam.leaderUnit.getID()) {
                    sumOfDistance += member.getDistance(airForceTeam.leaderUnit);
                }
            }
            averageDistance = sumOfDistance / (memberSize - 1);
        }
        if (averageDistance > 5) {
            return MicroDecision.unite(airForceTeam.leaderUnit); // 뭉치기
        } else {
            if (allUnitCoolTimeReady) {
                return MicroDecision.attackPosition(airForceTeam.leaderUnit); // 이동
            } else {
                return MicroDecision.kitingUnit(airForceTeam.leaderUnit, null); // 카이팅 eui 정보는 decision에 저장된게 있다.
            }
        }
    }

    public MicroDecision makeDecision(Unit myUnit, Collection<UnitInfo> euiList) {
        return makeDecision(myUnit, euiList, false);
    }

    public MicroDecision makeDecision(Unit myUnit, Collection<UnitInfo> euiList, boolean overwhelm) {
        UnitInfo bestTargetUnitInfo = null;
        int highestScore = 0;
        for (UnitInfo eui : euiList) {
            if (!MicroUtils.canAttack(myUnit, eui)) {
                continue;
            }
            if (!overwhelm && isCloseDangerousTarget(myUnit, eui)) {
                return MicroDecision.fleeFromUnit(myUnit, eui);
            }
            int score = targetScoreCalculator.calculate(myUnit, eui);
            if (score > highestScore) {
                bestTargetUnitInfo = eui;
                highestScore = score;
            }
        }

        MicroDecision decision;
        if (bestTargetUnitInfo != null) {
            decision = MicroDecision.kitingUnit(myUnit, bestTargetUnitInfo);
        } else {
            decision = MicroDecision.attackPosition(myUnit);
        }
        return decision;
    }

    private boolean isCloseDangerousTarget(Unit myUnit, UnitInfo eui) {
        boolean enemyIsComplete = eui.isCompleted();
        Position enemyPosition = eui.getLastPosition();
        UnitType enemyUnitType = eui.getType();

        Unit enemyUnit = UnitUtils.enemyUnitInSight(eui);
        if (UnitUtils.isValidUnit(enemyUnit)) {
            enemyIsComplete = enemyUnit.isCompleted();
            enemyPosition = enemyUnit.getPosition();
            enemyUnitType = enemyUnit.getType();
        }

        // 접근하면 안되는 유닛타입인지 판단 (성큰, 포톤캐논, 시즈탱크, 벙커)
        if (!enemyIsComplete || !isDangerousType(myUnit, enemyUnitType, enemyUnit)) {
            return false;
        }

        // 접근하면 안되는 거리인지 있는지 판단
        int distanceToNearEnemy = myUnit.getDistance(enemyPosition);
        int enemyWeaponRange = 0;

        if (enemyUnitType == UnitType.Terran_Bunker) {
            enemyWeaponRange = PlayerUtils.enemyPlayer().weaponMaxRange(UnitType.Terran_Marine.groundWeapon()) + 96;
        } else {
            if (myUnit.isFlying()) {
                enemyWeaponRange = PlayerUtils.enemyPlayer().weaponMaxRange(enemyUnitType.airWeapon());
            } else {
                enemyWeaponRange = PlayerUtils.enemyPlayer().weaponMaxRange(enemyUnitType.groundWeapon());
            }
        }
        return distanceToNearEnemy <= enemyWeaponRange + BACKOFF_DIST;
    }

    // 접근하면 안되는 적이 있는지 판단 (성큰, 포톤캐논, 시즈탱크, 벙커)
    private boolean isDangerousType(Unit myUnit, UnitType enemyUnitType, Unit enemyUnit) {
        if (myUnit.isFlying()) {
            UnitType[] enemyAirDefenseUnitType = UnitTypeUtils.enemyAirDefenseUnitType();
            for (UnitType airDefenseUnitType : enemyAirDefenseUnitType) {
                if (enemyUnitType == airDefenseUnitType) {
                    return true;
                }
            }
            return false;
            // || (marine, goliath, dragoon, archon, hydra..) {

        } else {
            return enemyUnitType == UnitType.Zerg_Sunken_Colony
                    || enemyUnitType == UnitType.Protoss_Photon_Cannon
                    || enemyUnitType == UnitType.Terran_Siege_Tank_Siege_Mode
                    || enemyUnitType == UnitType.Terran_Bunker
                    || (enemyUnitType == UnitType.Zerg_Lurker && enemyUnit != null && enemyUnit.isBurrowed() && !enemyUnit.isDetected());
            // || (saveUnitLevel >= 2 && allRangeUnitType(PreBot.Broodwar.enemy(), enemyUnitType))) {
        }
    }

    private boolean isCloseUndetectedGroundUnit(Unit myUnit, UnitInfo eui) {
        if (myUnit.isFlying()) {
            return false;
        }

        Unit enemyUnit = UnitUtils.enemyUnitInSight(eui);
        if (enemyUnit == null || enemyUnit.isDetected()) {
            return false;
        }

        if (eui.getType() == UnitType.Protoss_Dark_Templar) {
            return myUnit.getDistance(enemyUnit) < 150;
        } else if (eui.getType() == UnitType.Zerg_Lurker) {
            return enemyUnit.isInWeaponRange(myUnit);
        } else {
            return false;
        }
    }
}
