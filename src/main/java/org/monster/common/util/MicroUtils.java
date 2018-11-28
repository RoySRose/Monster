package org.monster.common.util;

import bwapi.DamageType;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitSizeType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwapi.WeaponType;
import org.monster.common.LagObserver;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.main.Monster;
import org.monster.micro.FleeOption;
import org.monster.micro.KitingOption;
import org.monster.micro.MirrorBugFixed;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.targeting.TargetFilter;
import org.monster.strategy.manage.AirForceManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MicroUtils {

    private static final Map<Integer, Integer> UNIT_HEALED_FRAME = new HashMap<>();

    private static final int WRAITH_MOVE_DISTANCE_48_FRAMES = moveDistancePerFrame(UnitType.Terran_Wraith, 48); // 2.0초간 움직이는 거리
    private static final int WRAITH_MOVE_DISTANCE_24_FRAMES = moveDistancePerFrame(UnitType.Terran_Wraith, 24); // 1.0초간 움직이는 거리
    private static final int WRAITH_MOVE_DISTANCE_12_FRAMES = moveDistancePerFrame(UnitType.Terran_Wraith, 12); // 0.5초간 움직이는 거리

    private static final int AIR_DRIVING_PRE_EXPECT_SECS = 0;
    private static final Map<UnitType, Integer> RISK_RADIUS_MAP = new HashMap<>();

    public static Position airFleePosition(Position startPosition, Position targetPosition) {
        double fleeRadian = oppositeDirectionRadian(startPosition, targetPosition);
        int riskRadius = getRiskRadius(UnitType.Terran_Wraith);
//		for (int moveDistance = WRAITH_MOVE_DISTANCE_48_FRAMES; moveDistance > 10; moveDistance = (int) (moveDistance * 0.7)) {
        return airLowestRiskPosition(startPosition, MicroConfig.Angles.AIR_FORCE_FREE, fleeRadian, WRAITH_MOVE_DISTANCE_24_FRAMES * 2, riskRadius);
    }

    private static Position airLowestRiskPosition(Position startPosition, int[] angles, double standRadian, int moveDistance, int riskRadius) {
        Position bestPosition = Position.None;
        int minimumRisk = CommonCode.INT_MAX;

        double moveRadian = 0.0d;
        Position candiPosition = Position.None;
        int riskOfCandiPosition = 0;

        for (Integer angle : angles) {
            moveRadian = rotate(standRadian, angle);
            candiPosition = getMovePosition(startPosition, moveRadian, moveDistance);
            if (!PositionUtils.isValidPosition(candiPosition)) {
                continue;
            }

            riskOfCandiPosition = airRiskOfPosition(UnitType.Terran_Wraith, candiPosition, riskRadius);
            if (riskOfCandiPosition < minimumRisk) {
                bestPosition = candiPosition;
                minimumRisk = riskOfCandiPosition;
            }
        }
        return bestPosition;
    }

    /// 레이쓰 전용
    public static Position airDrivingPosition(Position startPosition, Position endPosition, int[] driveAngle, boolean avoidEnemyUnit) {
        if (startPosition.getDistance(endPosition) <= WRAITH_MOVE_DISTANCE_48_FRAMES) {
            return endPosition; // 목표위치에 도착한 경우 짧게 찍는다.
        }

        double radianToMovePosition = oppositeDirectionRadian(endPosition, startPosition);
        double moveRadian = 0.0d;
        Position candiPosition = Position.None;

        double minimumDistance = CommonCode.DOUBLE_MAX;
        double minimumDistanceRadian = 0.0d;

        // 목표지점까지의 최소거리를 찾는다.
        for (int angle : driveAngle) {
            moveRadian = rotate(radianToMovePosition, angle);
            candiPosition = getMovePosition(startPosition, moveRadian, WRAITH_MOVE_DISTANCE_48_FRAMES);
            if (!PositionUtils.isValidPosition(candiPosition)) {
                continue;
            }
            Set<UnitInfo> enemyDefTowerList = UnitUtils.getCompleteEnemyInfosInRadiusForAir(candiPosition, AirForceManager.AIR_FORCE_SAFE_DISTANCE, UnitUtils.enemyAirDefenseUnitType());
            if (!enemyDefTowerList.isEmpty()) {
                continue;
            }
            if (avoidEnemyUnit) {
                Set<UnitInfo> enemyAirWeaponList = UnitUtils.getCompleteEnemyInfosInRadiusForAir(candiPosition, AirForceManager.AIR_FORCE_SAFE_DISTANCE2, UnitUtils.wraithKillerUnitType());
                if (!enemyAirWeaponList.isEmpty()) {
                    continue;
                }
            }

//			double distance = getDistanceAfterSeconds(candiPosition, endPosition, AIR_DRIVING_PRE_EXPECT_SECS);
            double distance = startPosition.getDistance(endPosition);
            if (distance < minimumDistance) {
                minimumDistance = distance;
                minimumDistanceRadian = moveRadian;
            }
        }

        if (minimumDistance == CommonCode.DOUBLE_MAX) { // 위치를 찾지 못함
            return null;
        }

        // 목표지점까지의 세부각도를 찾는다.
        double realMoveRadian = minimumDistanceRadian;
        double adjustedRadian = 0.0d;
        for (int angle : MicroConfig.Angles.AIR_FORCE_DRIVE_DETAIL) {
            adjustedRadian = rotate(minimumDistanceRadian, angle);
            candiPosition = getMovePosition(startPosition, adjustedRadian, WRAITH_MOVE_DISTANCE_12_FRAMES);
            if (!PositionUtils.isValidPosition(candiPosition)) {
                continue;
            }
            Set<UnitInfo> enemyDefTowerList = UnitUtils.getCompleteEnemyInfosInRadiusForAir(candiPosition, AirForceManager.AIR_FORCE_SAFE_DISTANCE, UnitUtils.enemyAirDefenseUnitType());
            if (enemyDefTowerList.isEmpty()) {
                realMoveRadian = adjustedRadian;
                break;
            }
            if (avoidEnemyUnit) {
                Set<UnitInfo> enemyAirWeaponList = UnitUtils.getCompleteEnemyInfosInRadiusForAir(candiPosition, AirForceManager.AIR_FORCE_SAFE_DISTANCE2, UnitUtils.wraithKillerUnitType());
                if (!enemyAirWeaponList.isEmpty()) {
                    continue;
                }
            }
        }
        Position airDrivingPosition = getMovePosition(startPosition, realMoveRadian, WRAITH_MOVE_DISTANCE_48_FRAMES);
        if (!PositionUtils.isValidPosition(airDrivingPosition)) {
            airDrivingPosition = getMovePosition(startPosition, realMoveRadian, WRAITH_MOVE_DISTANCE_24_FRAMES);
        }
        if (!PositionUtils.isValidPosition(airDrivingPosition)) {
            airDrivingPosition = getMovePosition(startPosition, realMoveRadian, WRAITH_MOVE_DISTANCE_12_FRAMES);
        }
        return airDrivingPosition;
    }

    public static boolean combatEnemyType(UnitType unitType) {
        if (unitType.isBuilding()) {
            if (unitType.canAttack() || unitType == UnitType.Terran_Bunker || unitType == UnitType.Zerg_Creep_Colony) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean airEnemyType(UnitType unitType) {
        return unitType.airWeapon() != WeaponType.None
                || unitType == UnitType.Terran_Medic
                || unitType == UnitType.Terran_Science_Vessel
                || unitType == UnitType.Zerg_Defiler
                || unitType == UnitType.Protoss_Carrier
                || unitType == UnitType.Protoss_High_Templar;
    }

    private static int airRiskOfPosition(UnitType myUnitType, Position movePosition, int radius) {
        int risk = 0;
        List<Unit> unitsInRadius = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.ALL, movePosition, radius);
        for (Unit unit : unitsInRadius) {
            if (unit.getPlayer() == Monster.Broodwar.enemy()) { // 적군인 경우
                int damage = Monster.Broodwar.getDamageFrom(unit.getType(), myUnitType);
                if (damage > 0) {
                    if (unit.getType().isBuilding()) {
                        risk += 30;
                    } else {
                        risk = 10;
                    }
                }

            } else if (unit.getPlayer() == Monster.Broodwar.self()) { // 아군인 경우, united값에 따라 좋은지 싫은지 판단을 다르게 한다.
                if (unit.getType() == UnitType.Terran_Missile_Turret) {
                    risk += 20;
                } else {
                    risk += 5;
                }
            }
        }
        return risk;
    }

    private static double getDistanceAfterSeconds(Position startPosition, Position endPosition, int seconds) {
        if (seconds == 0) {
            return startPosition.getDistance(endPosition);
        }

        double radianToMovePosition = oppositeDirectionRadian(endPosition, startPosition);
        double moveRadian = 0.0d;
        Position candiPosition = Position.None;

        double minimumDistance = CommonCode.DOUBLE_MAX;

        for (int angle : MicroConfig.Angles.AIR_FORCE_DRIVE) {
            moveRadian = rotate(radianToMovePosition, angle);
            candiPosition = getMovePosition(startPosition, moveRadian, WRAITH_MOVE_DISTANCE_48_FRAMES);
            if (!PositionUtils.isValidPosition(candiPosition)) {
                continue;
            }
            Set<UnitInfo> enemyDefTowerList = UnitUtils.getCompleteEnemyInfosInRadiusForAir(candiPosition, 0, UnitUtils.enemyAirDefenseUnitType());
            if (!enemyDefTowerList.isEmpty()) {
                continue;
            }
            double distance = getDistanceAfterSeconds(candiPosition, endPosition, seconds - 1);
            if (distance < minimumDistance) {
                minimumDistance = distance;
            }
        }
        return minimumDistance;
    }

    public static void flee(Unit fleeUnit, Position targetPosition, FleeOption fOption) {
        fleeUnit.rightClick(getFleePosition(fleeUnit, targetPosition, fOption));
    }

    public static void fleeScout(Unit fleeUnit, Position targetPosition, FleeOption fOption) {
        fleeUnit.rightClick(getFleeScoutPosition(fleeUnit, targetPosition, fOption));
    }

    public static void kiting(Unit rangedUnit, UnitInfo targetInfo, KitingOption kOption) {
        if (LagObserver.groupsize() > 20) {
            CommandUtils.attackMove(rangedUnit, targetInfo.getLastPosition());
            return;
        }

        if (UnitUtils.unitInSight(targetInfo) == null) {
            kitingInvisible(rangedUnit, targetInfo, kOption);
        } else {
            kiting(rangedUnit, targetInfo.getUnit(), kOption);
        }
    }

    public static void holdControlToRemoveMine(Unit rangedUnit, Position minePosition, FleeOption fOption) {
        if (rangedUnit.getGroundWeaponCooldown() <= 10) {
            rangedUnit.holdPosition();
        } else {
            MicroUtils.flee(rangedUnit, minePosition, fOption);
        }
    }

    public static void kiting(Unit rangedUnit, Unit targetUnit, KitingOption kOption) {
        if (!killedByNShot(rangedUnit, targetUnit, 1) && killedByNShot(targetUnit, rangedUnit, 2)) {
            kOption.cooltimeAlwaysAttack = KitingOption.CoolTimeAttack.KEEP_SAFE_DISTANCE;
            kOption.fOption.united = false;
            kOption.fOption.angles = MicroConfig.Angles.WIDE;
        } else if (groundUnitFreeKiting(rangedUnit)) {
            kOption.fOption.united = false;
            kOption.fOption.angles = MicroConfig.Angles.WIDE;
        }
        if (!targetUnit.isDetected()) {
            kOption.cooltimeAlwaysAttack = KitingOption.CoolTimeAttack.KEEP_SAFE_DISTANCE;
        }

        boolean timeToAttack = timeToAttack(rangedUnit, targetUnit, kOption.cooltimeAlwaysAttack);
        if (timeToAttack) {
            CommandUtils.attackUnit(rangedUnit, targetUnit);
        } else {
            int approachKitingDistance = forwardKitingTargetDistance(rangedUnit, targetUnit);
            //마린은 저글링 러쉬떄 안전을 위해 거리 조금 늘림
			/*if(approachKitingDistance != CommonCode.NONE && rangedUnit.getType() ==  UnitType.Terran_Marine){
				approachKitingDistance += 5;
			}*/
            if (approachKitingDistance != CommonCode.NONE && rangedUnit.getDistance(targetUnit) >= approachKitingDistance) {
                CommandUtils.attackMove(rangedUnit, targetUnit.getPosition());
            } else {
                flee(rangedUnit, targetUnit.getPosition(), kOption.fOption);
            }
        }
    }

    public static void kitingInvisible(Unit rangedUnit, UnitInfo targetInfo, KitingOption kOption) {
        WeaponType weapon = getWeapon(rangedUnit.getType(), targetInfo.getType());
        if (weapon == WeaponType.None || rangedUnit.getDistance(targetInfo.getLastPosition()) > weapon.maxRange() + 50) {
            CommandUtils.attackMove(rangedUnit, targetInfo.getLastPosition());
        } else {
            flee(rangedUnit, targetInfo.getLastPosition(), kOption.fOption);
        }
    }


    public static void BlockingKiting(Unit rangedUnit, UnitInfo targetInfo, KitingOption kOption, Position safePosition) {
        if (UnitUtils.unitInSight(targetInfo) == null) {
            BlockingKitingInvisible(rangedUnit, targetInfo, kOption, safePosition);
        } else {
            Blockingkiting(rangedUnit, targetInfo.getUnit(), kOption, safePosition);
        }
    }

    public static void BlockingKitingInvisible(Unit rangedUnit, UnitInfo targetInfo, KitingOption kOption, Position safePosition) {
        WeaponType weapon = getWeapon(rangedUnit.getType(), targetInfo.getType());
        if (weapon == WeaponType.None || rangedUnit.getDistance(targetInfo.getLastPosition()) > weapon.maxRange() + 50) {
            CommandUtils.attackMove(rangedUnit, safePosition);
        } else {
            //flee(rangedUnit, safePosition, kOption.fOption);
            //CommandUtils.move(rangedUnit,safePosition);
            rangedUnit.move(safePosition);
        }
    }


    public static void Blockingkiting(Unit rangedUnit, Unit targetUnit, KitingOption kOption, Position safePosition) {
        //if (!killedByNShot(rangedUnit, targetUnit, 1) && killedByNShot(targetUnit, rangedUnit, 2)) {
        kOption.cooltimeAlwaysAttack = KitingOption.CoolTimeAttack.KEEP_SAFE_DISTANCE;
        kOption.fOption.united = false;
        kOption.fOption.angles = MicroConfig.Angles.WIDE;
        //}
        if (timeToAttack(rangedUnit, targetUnit, kOption.cooltimeAlwaysAttack)) {
            CommandUtils.attackUnit(rangedUnit, targetUnit);
        } else {
            //CommandUtils.move(rangedUnit,safePosition);
            rangedUnit.move(safePosition);
        }
    }


    private static boolean timeToAttack(Unit rangedUnit, Unit targetUnit, KitingOption.CoolTimeAttack cooltimeAttack) {

        // attackUnit, target 각각의 지상/공중 무기를 선택
        WeaponType attackUnitWeapon = targetUnit.isFlying() ? rangedUnit.getType().airWeapon() : rangedUnit.getType().groundWeapon();
        WeaponType targetWeapon = rangedUnit.isFlying() ? targetUnit.getType().airWeapon() : targetUnit.getType().groundWeapon();

        // 일꾼의 공격력은 강하지 않다.
        if (targetUnit.getType().isWorker() && !rangedUnit.isUnderAttack()) {
            return true;
        }

        // 벌처는 벌처에게 카이팅하지 않는다.
        if (rangedUnit.getType() == UnitType.Terran_Vulture) {
            if (targetUnit.getType() == UnitType.Terran_Vulture) {
                return true;
            }
        }
        // 벌처가 아닌경우, 자신보다 보다 긴 사정거리를 가진 적에게 카이팅은 무의미하다.
        else if (Monster.Broodwar.self().weaponMaxRange(attackUnitWeapon) <= Monster.Broodwar.enemy().weaponMaxRange(targetWeapon)) {
            return true;
        }

        int cooltime = rangedUnit.isStartingAttack() ? attackUnitWeapon.damageCooldown() // // 쿨타임시간(frame)
                : (targetUnit.isFlying() ? rangedUnit.getAirWeaponCooldown() : rangedUnit.getGroundWeaponCooldown());
        double distanceToAttack = rangedUnit.getDistance(targetUnit) - Monster.Broodwar.self().weaponMaxRange(attackUnitWeapon); // 공격하기 위해 이동해야 하는 거리(pixel)
        int catchTime = (int) (distanceToAttack / rangedUnit.getType().topSpeed()); // 상대를 잡기위해 걸리는 시간 (frame) = 거리(pixel) / 속도(pixel per frame)
        if (!targetUnit.isDetected() && UnitUtils.availableScanningCount() == 0) {
            catchTime -= TimeUtils.SECOND;
        } else {
            if (targetUnit.getType() == UnitType.Zerg_Lurker || targetUnit.getType() == UnitType.Protoss_Dark_Templar) { // 다크를 죽여버린다.
                cooltimeAttack = KitingOption.CoolTimeAttack.COOLTIME_ALWAYS_IN_RANGE;
            }
        }

        // 상대가 때리기 위해 거리를 좁히거나 벌려야 하는 경우(coolTime <= catchTime)
        if (cooltime <= catchTime + Monster.Broodwar.getLatency() * 2) { // 명령에 대한 지연시간(latency)을 더한다. ex) LAN(UDP) : 5
//			System.out.println("#################################");
//			System.out.println("vulture id " + rangedUnit.getID() + ": " + cooltime + " <= " + catchTime + " + " + Prebot.Broodwar.getLatency() * 2);
//			System.out.println("distanceToAttack = " + distanceToAttack);
//			System.out.println("getDistance = " + rangedUnit.getDistance(targetUnit));
//			System.out.println("weaponMaxRange = " + Prebot.Broodwar.self().weaponMaxRange(attackUnitWeapon));
//			System.out.println("#################################");
            return true;
        }

        // TODO 테스트 필요. 사정거리를 유지
        if (cooltimeAttack == KitingOption.CoolTimeAttack.COOLTIME_ALWAYS_IN_RANGE) {
            if (!rangedUnit.isInWeaponRange(targetUnit)) {
                return true;
            }
        }
        // 쿨타임이 되었을 때 항시 공격할 것인가
        return cooltimeAttack.coolTimeAlwaysAttack && cooltime == 0;
    }

    private static int forwardKitingTargetDistance(Unit rangedUnit, Unit targetUnit) {
        if (targetUnit.getType().isBuilding()) { // 해처리 라바때문에 마인 폭사함
            return 70;

        } else if (targetUnit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {
            return 1;

        } else if (targetUnit.getType() == UnitType.Protoss_Carrier || targetUnit.getType() == UnitType.Zerg_Overlord) {
            return 50;
        }
        return CommonCode.NONE;
    }

    private static boolean groundUnitFreeKiting(Unit rangedUnit) {
        List<Unit> nearUnits = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, rangedUnit.getPosition(), (int) (rangedUnit.getType().topSpeed() * rangedUnit.getType().groundWeapon().damageCooldown() * 0.8));
        boolean freeKiting = true;
        int myGroundUnitCount = 0;
        for (Unit unit : nearUnits) {
            if (unit.getType().isWorker() || unit.isFlying() || unit.getType().isBuilding()) {
                continue;
            }
            if (++myGroundUnitCount > 2) {
                freeKiting = false;
                break;
            }
        }
        return freeKiting;
    }

    private static Position getFleePosition(Unit fleeUnit, Position targetPosition, FleeOption fOption) {
        double fleeRadian = oppositeDirectionRadian(fleeUnit.getPosition(), targetPosition);
        Position fleePosition = Position.None;
        int moveDistanceOneSec = moveDistancePerFrame(fleeUnit, TimeUtils.SECOND); // 1초간 움직이는 거리
        int riskRadius = getRiskRadius(fleeUnit.getType());

        for (int moveDistance = moveDistanceOneSec; moveDistanceOneSec > 10; moveDistanceOneSec = (int) (moveDistanceOneSec * 0.7)) {
            fleePosition = lowestRiskPosition(fleeUnit, fOption, fleeRadian, moveDistance, riskRadius);
            if (fleePosition != Position.None) {
                break;
            }
        }
        return PositionUtils.isValidPosition(fleePosition) ? fleePosition : fOption.goalPosition;
    }

    private static Position getFleeScoutPosition(Unit fleeUnit, Position targetPosition, FleeOption fOption) {
        double fleeRadian = targetDirectionRadian(fleeUnit.getPosition(), targetPosition) + 90.0;
        Position fleePosition = Position.None;
        int moveDistanceOneSec = moveDistancePerFrame(fleeUnit, TimeUtils.SECOND); // 1초간 움직이는 거리
        int riskRadius = getRiskRadius(fleeUnit.getType());

        for (int moveDistance = moveDistanceOneSec; moveDistanceOneSec > 10; moveDistanceOneSec = (int) (moveDistanceOneSec * 0.7)) {
            fleePosition = lowestRiskPosition(fleeUnit, fOption, fleeRadian, moveDistance, riskRadius);
            if (fleePosition != Position.None) {
                break;
            }
        }
        return PositionUtils.isValidPosition(fleePosition) ? fleePosition : fOption.goalPosition;
    }

    private static int getRiskRadius(UnitType unitType) {
        if (RISK_RADIUS_MAP.isEmpty()) {
            RISK_RADIUS_MAP.put(UnitType.Terran_Vulture, MicroConfig.Flee.RISK_RADIUS_VULTURE);
            RISK_RADIUS_MAP.put(UnitType.Terran_Siege_Tank_Tank_Mode, MicroConfig.Flee.RISK_RADIUS_TANK);
            RISK_RADIUS_MAP.put(UnitType.Terran_Goliath, MicroConfig.Flee.RISK_RADIUS_GOLIATH);
            RISK_RADIUS_MAP.put(UnitType.Terran_Wraith, MicroConfig.Flee.RISK_RADIUS_WRAITH);
            RISK_RADIUS_MAP.put(UnitType.Terran_Science_Vessel, MicroConfig.Flee.RISK_RADIUS_VESSEL);
            RISK_RADIUS_MAP.put(UnitType.Terran_SCV, MicroConfig.Flee.RISK_RADIUS_DEFAULT);
        }

        Integer riskRadius = RISK_RADIUS_MAP.get(unitType);
        if (riskRadius == null) {
            riskRadius = MicroConfig.Flee.RISK_RADIUS_DEFAULT;
        }
        return riskRadius;

    }

    /// 반대 방향의 각도(radian)
    public static double oppositeDirectionRadian(Position myPosition, Position targetPosition) {
        return Math.atan2(myPosition.getY() - targetPosition.getY(), myPosition.getX() - targetPosition.getX());
    }

    /// 정방향 각도(radian)
    public static double targetDirectionRadian(Position myPosition, Position targetPosition) {
        return Math.atan2(targetPosition.getY() - myPosition.getY(), targetPosition.getX() - myPosition.getX());
    }

    private static Position lowestRiskPosition(Unit unit, FleeOption fOption, double standRadian, int moveDistance, int riskRadius) {
        Position bestPosition = Position.None;
        int minimumRisk = CommonCode.INT_MAX;
        int distFromBestToGoal = CommonCode.INT_MAX;

        double moveRadian = 0.0d;
        Position candiPosition = Position.None;
        int riskOfCandiPosition = 0;
        int distFromCandiToGoal = 0;

        for (Integer angle : fOption.angles) {
            moveRadian = rotate(standRadian, angle);
            candiPosition = getMovePosition(unit.getPosition(), moveRadian, moveDistance);
            if (!PositionUtils.isValidPositionToMove(candiPosition, unit)) {
                continue;
            }

            distFromCandiToGoal = candiPosition.getApproxDistance(fOption.goalPosition);
            riskOfCandiPosition = riskOfPosition(unit.getType(), candiPosition, riskRadius, fOption.united);

            if (riskOfCandiPosition < minimumRisk || (riskOfCandiPosition == minimumRisk && distFromCandiToGoal < distFromBestToGoal)) {
                bestPosition = candiPosition;
                distFromBestToGoal = distFromCandiToGoal;
                minimumRisk = riskOfCandiPosition;
            }
        }
        return bestPosition;
    }

    /// sourcePosition에서 moveRadian의 각으로 moveDistance만큼 떨어진 포지션
    public static Position getMovePosition(Position sourcePosition, double moveRadian, int moveDistance) {
        int x = (int) (moveDistance * Math.cos(moveRadian));
        int y = (int) (moveDistance * Math.sin(moveRadian));
        return new Position(sourcePosition.getX() + x, sourcePosition.getY() + y);
    }

    /// * 참조사이트: http://yc0345.tistory.com/45
    /// 공식: radian = (π / 180) * 각도
    /// -> 각도 = (radian * 180) / π
    /// -> 회원 radian = (π / 180) * ((radian * 180) / π + 회전각)
    public static double rotate(double radian, int angle) {
        return (Math.PI / 180) * ((radian * 180 / Math.PI) + angle);
    }

    private static int riskOfPosition(UnitType myUnitType, Position movePosition, int radius, boolean united) {
        int risk = 0;
        List<Unit> unitsInRadius = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.ALL, movePosition, radius);
        for (Unit unit : unitsInRadius) {
            if (unit.getPlayer() == Monster.Broodwar.enemy()) { // 적군인 경우
                if (Monster.Broodwar.getDamageFrom(unit.getType(), myUnitType) > 0) { // 적군이 공격할 수 있으면 위험하겠지
                    if (unit.getType().isBuilding()) { // 건물이 공격할 수 있으면 진짜 위험한거겠지
                        risk += 20;
                    } else if (!unit.getType().isFlyer()) { // 날아다니지 않으면 길막까지 하니까
                        risk += 15;
                    } else if (unit.getType().isWorker()) { // 일꾼은 그다지 위험하지 않다고 본다.
                        risk += 2;
                    } else { // 날아다니면 길막은 하지 않으니까
                        risk += 5;
                    }
                } else { // 적군이 공격할 수 없을 때
                    if (unit.getType().isBuilding()) {
                        risk += 3;
                    } else if (!unit.getType().isFlyer()) {
                        risk += 2;
                    } else {
                        risk += 1;
                    }
                }

            } else if (unit.getPlayer() == Monster.Broodwar.self()) { // 아군인 경우, united값에 따라 좋은지 싫은지 판단을 다르게 한다.
                if (!unit.getType().isFlyer()) {
                    risk += united ? -2 : 2;
                } else {
                    risk += united ? -1 : 1;
                }

            } else { // 중립(미네랄, 가스 등)
                risk += 1;
            }
        }
        return risk;
    }

    private static int moveDistancePerFrame(Unit fleeUnit, int frame) {
        double unitSpeed1 = fleeUnit.getPlayer().topSpeed(fleeUnit.getType());
        //double unitSpeed2 = fleeUnit.getType().topSpeed(); // TODO 업그레이드 시 unitSpeed1, unitSpeed2가 차이가 있는지
        return (int) (unitSpeed1 * frame); // frame의 시간동안 몇 pixel 이동 가능한지
    }

    private static int moveDistancePerFrame(UnitType unitType, int frame) {
        double unitSpeed1 = unitType.topSpeed();
        return (int) (unitSpeed1 * frame); // frame의 시간동안 몇 pixel 이동 가능한지
    }

    //	public static int requiredShotToKill(Unit attackUnit, Unit targetUnit) { // TODO 추후 메서드 검증 및 업그레이드 필요
    public static boolean killedByNShot(Unit attackUnit, Unit targetUnit, int shot) {
        UnitType attackerType = attackUnit.getType();
        UnitType targetType = targetUnit.getType();

        int numberOfAttack = shot;
        if (attackerType == UnitType.Protoss_Zealot || attackerType == UnitType.Terran_Goliath && targetType.isFlyer()) {
            numberOfAttack *= 2;
        }
        int damageExpected = Monster.Broodwar.getDamageFrom(attackerType, targetType, attackUnit.getPlayer(), targetUnit.getPlayer()) * numberOfAttack;

        int targetHitPoints = targetUnit.getHitPoints();
        if (targetType.regeneratesHP()) {
            targetHitPoints += 1;
        }

        if (targetType.maxShields() == 0) {
            return damageExpected >= targetHitPoints;
        }

        int spareDamage = damageExpected - targetHitPoints;
        if (spareDamage < 0) {
            return false;
        }

        int targetShields = targetUnit.getShields();
        if (targetShields == 0) {
            return true;
        }
        targetShields += (targetUnit.getPlayer().getUpgradeLevel(UpgradeType.Protoss_Plasma_Shields) * numberOfAttack);

        return toShieldDamage(spareDamage, attackUnit, targetUnit) > targetShields;
    }

    private static int toShieldDamage(int damage, Unit attackUnit, Unit targetUnit) {
        DamageType explosionType = MirrorBugFixed.getDamageType(attackUnit.getType(), targetUnit);
        UnitSizeType targetUnitSize = MirrorBugFixed.getUnitSize(targetUnit.getType());

        if (explosionType == DamageType.Explosive) {
            if (targetUnitSize == UnitSizeType.Small) {
                return damage * 2;
            } else if (targetUnitSize == UnitSizeType.Medium) {
                return damage * 4 / 3;
            }
        } else if (explosionType == DamageType.Concussive) {
            if (targetUnitSize == UnitSizeType.Medium) {
                return damage * 2;
            } else if (targetUnitSize == UnitSizeType.Large) {
                return damage * 4;
            }
        }
        return damage;
    }

    public static boolean isRemovableEnemySpiderMine(Unit unit, UnitInfo eui) {
        Unit target = UnitUtils.unitInSight(eui);
        if (target == null) {
            return false;
        }

        return target.getType() == UnitType.Terran_Vulture_Spider_Mine && unit.isInWeaponRange(target);
    }

    // (지상유닛 대상) position의 적의 사정거리에서 안전한 지역인지 판단한다.
    public static boolean isSafePlace(Position position) {
        Set<UnitInfo> euiList = UnitUtils.getEnemyUnitInfosInRadiusForGround(position, 0);

        for (UnitInfo ui : euiList) {
            if (ui.getType().isWorker() || !typeCanAttackGround(ui.getType())) {
                continue;
            }

            double distanceToNearEnemy = position.getDistance(ui.getLastPosition());
            WeaponType nearEnemyWeapon = ui.getType().groundWeapon();
            int enemyWeaponMaxRange = Monster.Broodwar.enemy().weaponMaxRange(nearEnemyWeapon);
            double enemyTopSpeed = Monster.Broodwar.enemy().topSpeed(ui.getType());
            double backOffDist = ui.getType().isBuilding() ? MicroConfig.Common.BACKOFF_DIST_DEF_TOWER : 0.0;

            if (distanceToNearEnemy <= enemyWeaponMaxRange + enemyTopSpeed * 24 + backOffDist) {
                return false;
            }
        }

        return true;
    }

    private static boolean typeCanAttackGround(UnitType attacker) {
        return attacker.groundWeapon() != WeaponType.None ||
                attacker == UnitType.Terran_Bunker ||
                attacker == UnitType.Protoss_Carrier ||
                attacker == UnitType.Protoss_Reaver;
    }

    public static boolean isMeleeUnit(UnitType unitType) {
        return !unitType.isWorker() && unitType.groundWeapon().maxRange() <= MicroConfig.Tank.SIEGE_MODE_MIN_RANGE; // 시즈모드 최소사정거리 안의 공격범위는 melee 유닛으로 판단
    }

    public static boolean isRangeUnit(UnitType unitType) {
        return unitType.groundWeapon().maxRange() > UnitType.Zerg_Zergling.groundWeapon().maxRange(); // 시즈모드 최소사정거리 안의 공격범위는 melee 유닛으로 판단
    }

    public static boolean arrivedToPosition(Unit unit, Position position) {
        int sightRange = unit.getType().sightRange();
        int distance = unit.getDistance(position);

        return sightRange >= distance;
    }

    public static boolean timeToRandomMove(Unit unit) {
        return !isBeingHealed(unit) && (unit.isIdle() || unit.isBraking());
    }

    public static boolean isBeingHealed(Unit unit) {
        if (unit.isBeingHealed()) {
            UNIT_HEALED_FRAME.put(unit.getID(), TimeUtils.elapsedFrames());
            return true;
        }

        Integer healedFrame = UNIT_HEALED_FRAME.get(unit.getID());
        return healedFrame != null && TimeUtils.elapsedSeconds(healedFrame) < 1;
    }

    public static boolean canAttack(Unit myUnit, UnitInfo eui) {
        WeaponType weaponType = WeaponType.None;
        Unit enemy = UnitUtils.unitInSight(eui);
        if (enemy != null) {
            weaponType = getWeapon(myUnit, enemy);
        } else {
            weaponType = getWeapon(myUnit.getType(), eui.getType());
        }
        return weaponType != WeaponType.None;
    }

    public static WeaponType getWeapon(Unit attacker, Unit target) {
        return target.isFlying() ? attacker.getType().airWeapon() : attacker.getType().groundWeapon();
    }

    public static WeaponType getWeapon(UnitType attacker, UnitType target) {
        return target.isFlyer() ? attacker.airWeapon() : attacker.groundWeapon();
    }

    public static boolean isInWeaponRange(Unit myUnit, UnitInfo eui) {
        Unit enemy = UnitUtils.unitInSight(eui);
        if (enemy != null) {
            return myUnit.isInWeaponRange(enemy);
        } else {
            int enemyUnitDistance = myUnit.getDistance(eui.getLastPosition());
            int weaponMaxRange = Monster.Broodwar.enemy().weaponMaxRange(eui.getType().airWeapon());
            return enemyUnitDistance <= weaponMaxRange;
        }
    }

    public static int totalComsatCount() {
        int count = 0;
        List<Unit> comsatList = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Comsat_Station);
        for (Unit comsat : comsatList) {
            if (comsat.getEnergy() >= 50) {
                count++;
            }
        }
        return count;
    }

    public static boolean exposedByEnemy(Unit myUnit, Collection<UnitInfo> enemiesInfo) {
        for (UnitInfo ei : enemiesInfo) {
            if (myUnit.getDistance(ei.getLastPosition()) <= ei.getType().sightRange()) {
                return true;
            }
        }
        return false;
    }

    public static Set<UnitInfo> filterFlyingTargetInfos(Collection<UnitInfo> targetInfos) {
        Set<UnitInfo> newTargetInfos = new HashSet<>();
        for (UnitInfo targetInfo : targetInfos) {
            Unit target = UnitUtils.unitInSight(targetInfo);

            if (target != null) {
                if (!UnitUtils.isCompleteValidUnit(target)) {
                    continue;
                }
                if (target.isFlying()) {
                    newTargetInfos.add(targetInfo);
                }
            } else {
                UnitType enemyUnitType = targetInfo.getType();
                if (enemyUnitType.isFlyer()) {
                    newTargetInfos.add(targetInfo);
                }
            }
        }
        return newTargetInfos;
    }

    public static Set<UnitInfo> filterTargetInfos(Collection<UnitInfo> euiList, int targetFilter) {
        Set<UnitInfo> newTargetInfos = new HashSet<>();
        for (UnitInfo eui : euiList) {
            if (!TargetFilter.excludeByFilter(eui, targetFilter)) {
                newTargetInfos.add(eui);
            }
        }
        return newTargetInfos;
    }

}
