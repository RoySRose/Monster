package org.monster.micro.control.factory;

import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.Chokepoint;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.CommandUtils;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.PositionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.main.Monster;
import org.monster.micro.FleeOption;
import org.monster.micro.KitingOption;
import org.monster.micro.MicroDecision;
import org.monster.micro.MicroDecisionMakerPrebot1;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.constant.MicroConfig.MainSquadMode;
import org.monster.micro.control.Control;
import org.monster.board.StrategyBoard;
import org.monster.strategy.manage.PositionFinder;
import org.monster.strategy.manage.TankPositionManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Deprecated
public class TankControl extends Control {

    private static final int ENOUGH_BACKUP_VULTURE_AND_GOLIATH = 7;
    private static final int POSITION_TO_SIEGE_ARRIVE_DISTANCE = 0;
    private static final int SIEGE_MODE_RANGE_MARGIN_DISTANCE = 5;

    private boolean hasEnoughBackUpUnitToSiege = false;
    private int siegeModeSpreadRadius;
    private int saveUnitLevel;
    private Collection<UnitInfo> flyingEnemisInfos;

    private Position mainPosition;

    public void setSaveUnitLevel(int saveUnitLevel) {
        this.saveUnitLevel = saveUnitLevel;
    }

    public void setMainPosition(Position mainPosition) {
        this.mainPosition = mainPosition;
    }

    @Override
    public void control(Collection<Unit> unitList, Collection<UnitInfo> euiList) {
        if (TimeUtils.before(StrategyBoard.findRatFinishFrame)) {
            findRat(unitList);
            return;
        }

        List<Unit> vultureAndGoliath = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Vulture, UnitType.Terran_Goliath);
        this.hasEnoughBackUpUnitToSiege = vultureAndGoliath.size() > ENOUGH_BACKUP_VULTURE_AND_GOLIATH;
        this.siegeModeSpreadRadius = StrategyBoard.mainSquadCoverRadius;
        if (StrategyBoard.campType == PositionFinder.CampType.INSIDE
                || StrategyBoard.campType == PositionFinder.CampType.FIRST_CHOKE
                || StrategyBoard.campType == PositionFinder.CampType.EXPANSION) {
            siegeModeSpreadRadius = (int) (siegeModeSpreadRadius * 0.4);
        }

        this.flyingEnemisInfos = MicroUtils.filterFlyingTargetInfos(euiList);

        List<Unit> tankModeList = new ArrayList<>();
        List<Unit> siegeModeList = new ArrayList<>();

        List<Integer> leaderGroupIds = new ArrayList<>();
        if (StrategyBoard.mainSquadMode.isAttackMode && unitList.size() >= 3) {
            int leaderGroupMaxSize = unitList.size() / 3;
            Unit leaderOfUnit = UnitUtils.leaderOfUnit(unitList);
            if (leaderOfUnit != null) {

                Position nearChokePosition = null;
                Chokepoint nearestChoke = BWTA.getNearestChokepoint(leaderOfUnit.getPosition());
                if (nearestChoke.getWidth() < 250) {
                    nearChokePosition = nearestChoke.getCenter();
                }
                if (nearChokePosition == null || nearChokePosition.getDistance(leaderOfUnit.getPosition()) > 180) {
                    leaderGroupIds.add(leaderOfUnit.getID());
                }
                for (Unit unit : unitList) {
                    if (leaderGroupIds.size() > leaderGroupMaxSize) {
                        break;
                    }
                    if (unit.getID() == leaderOfUnit.getID() || unit.getDistance(leaderOfUnit) > 220) {
                        continue;
                    }

                    if (nearChokePosition == null || nearChokePosition.getDistance(unit.getPosition()) > 180) {
                        leaderGroupIds.add(unit.getID());
                    }
                }
            }
        }

        for (Unit unit : unitList) {
            if (skipControl(unit)) {
                continue;
            }
            if (unit.isSieged()) {
                siegeModeList.add(unit);
            } else {
                tankModeList.add(unit);
            }
        }

        executeSiegeMode(siegeModeList, euiList, leaderGroupIds);
        executeTankMode(tankModeList, euiList);
    }

    private void executeSiegeMode(List<Unit> siegeModeList, Collection<UnitInfo> euiList, List<Integer> leaderGroupIds) {
//		DecisionMaker decisionMaker = new DecisionMaker(new DefaultTargetCalculator());

        for (Unit siege : siegeModeList) {
//			Decision decision = decisionMaker.makeDecisionForSiegeMode(siege, euiList);
            MicroDecision decision = MicroDecisionMakerPrebot1.makeDecisionForSiegeMode(siege, euiList, siegeModeList, saveUnitLevel);
            if (decision.type == MicroDecision.MicroDecisionType.ATTACK_UNIT) {
                CommandUtils.attackUnit(siege, decision.eui.getUnit());

            } else if (decision.type == MicroDecision.MicroDecisionType.STOP) {
                siege.stop();

            } else if (decision.type == MicroDecision.MicroDecisionType.HOLD) {
                CommandUtils.holdPosition(siege);

            } else if (decision.type == MicroDecision.MicroDecisionType.CHANGE_MODE) {
                if (siege.canUnsiege() && !leaderGroupIds.contains(siege.getID())) {
                    CommandUtils.unsiege(siege);
                }

            } else if (decision.type == MicroDecision.MicroDecisionType.ATTACK_POSITION) { // NO ENEMY
                if (!TankPositionManager.Instance().isProperPositionToSiege(siege.getPosition(), true)) {
                    CommandUtils.unsiege(siege);
                    TankPositionManager.Instance().siegeModeReservedMap.remove(siege.getID());
                } else {
                    int distance = siege.getDistance(mainPosition);
                    if (PlayerUtils.enemyRace() == Race.Terran) { // 테란전용 go
                        if (distance > MicroConfig.Tank.SIEGE_MODE_MAX_RANGE) {
                            siege.unsiege();
                        }

                    } else {
                        if (distance > siegeModeSpreadRadius) {
                            if (siege.canUnsiege() && !leaderGroupIds.contains(siege.getID())) {
                                CommandUtils.unsiege(siege);
                                TankPositionManager.Instance().siegeModeReservedMap.remove(siege.getID());
                            }
                        }
                    }

                }
            }
        }
    }

    private void executeTankMode(List<Unit> tankModeList, Collection<UnitInfo> euiList) {
//		DecisionMaker decisionMaker = new DecisionMaker(new DefaultTargetCalculator());
        FleeOption fOption = new FleeOption(StrategyBoard.campPositionSiege, false, MicroConfig.Angles.NARROW);
        KitingOption kOption = new KitingOption(fOption, KitingOption.CoolTimeAttack.COOLTIME_ALWAYS);

        for (Unit tank : tankModeList) {
            if (!StrategyBoard.mainSquadMode.isAttackMode) {
                Position positionToSiege = TankPositionManager.Instance().getSiegeModePosition(tank.getID());
                if (positionToSiege == null && dangerousOutOfMyRegion(tank)) {
                    CommandUtils.move(tank, mainPosition);
                    continue;
                }
            }

            MicroDecision decision = MicroDecisionMakerPrebot1.makeDecisionPrebot1(tank, euiList, flyingEnemisInfos, saveUnitLevel);
//			System.out.println(decision);
            if (decision.type == MicroDecision.MicroDecisionType.FLEE_FROM_UNIT) {
                MicroUtils.flee(tank, decision.eui.getLastPosition(), fOption);

            } else if (decision.type == MicroDecision.MicroDecisionType.KITING_UNIT) {
                if (MicroUtils.isRemovableEnemySpiderMine(tank, decision.eui)) {
                    MicroUtils.holdControlToRemoveMine(tank, decision.eui.getLastPosition(), fOption);

                } else {
                    if (shouldSiege(tank, decision.eui)) {
                        CommandUtils.siege(tank);
                    } else {
                        MicroUtils.kiting(tank, decision.eui, kOption);
                    }
                }

            } else if (decision.type == MicroDecision.MicroDecisionType.ATTACK_POSITION) {

//				if (TankPositionManager.Instance().isProperPositionToSiege(siege.getPosition())) {
//					CommandUtils.unsiege(siege);
//					TankPositionManager.Instance().siegeModeReservedMap.remove(siege.getID());
//				}

                if (PlayerUtils.enemyRace() == Race.Terran) { // 테란전용 go
                    int distToOrder = tank.getDistance(mainPosition);
                    if (distToOrder <= MicroConfig.Tank.SIEGE_MODE_MAX_RANGE) {
                        if (tank.canSiege() && TankPositionManager.Instance().isProperPositionToSiege(tank.getPosition(), true)) { // orderPosition의 둘러싼 대형을 만든다.
                            if (StrategyBoard.mainSquadMode != MainSquadMode.NO_MERCY) {
                                tank.siege();
                            } else {
                                Position randomPosition = PositionUtils.randomPosition(tank.getPosition(), MicroConfig.RANDOM_MOVE_DISTANCE);
                                CommandUtils.attackMove(tank, randomPosition);
                            }
                        } else {
                            if (MicroUtils.timeToRandomMove(tank)) {
                                Position randomPosition = PositionUtils.randomPosition(tank.getPosition(), MicroConfig.RANDOM_MOVE_DISTANCE);
                                CommandUtils.attackMove(tank, randomPosition);
                            }
                        }
                    } else {
                        CommandUtils.attackMove(tank, mainPosition);
                    }

                } else {
                    boolean arrived = MicroUtils.arrivedToPosition(tank, mainPosition);
                    Position positionToSiege = TankPositionManager.Instance().getSiegeModePosition(tank.getID());
                    if (arrived && positionToSiege == null) {
                        positionToSiege = TankPositionManager.Instance().findPositionToSiegeAndReserve(mainPosition, tank, siegeModeSpreadRadius);
                    }

                    if (positionToSiege != null) {
                        int stayCnt = TankPositionManager.Instance().isSiegeStayCnt(tank);

                        if (tank.getDistance(positionToSiege) <= POSITION_TO_SIEGE_ARRIVE_DISTANCE && TankPositionManager.Instance().isProperPositionToSiege(tank.getPosition(), true)) {
                            CommandUtils.siege(tank);
                            TankPositionManager.Instance().siegePositionMap.remove(tank.getID());
                        } else if (stayCnt > 100) {
                            CommandUtils.siege(tank);
                            TankPositionManager.Instance().siegePositionMap.remove(tank.getID());
                        } else {
                            CommandUtils.attackMove(tank, positionToSiege);
                        }
                    } else {
                        if (arrived) {
                            if (MicroUtils.timeToRandomMove(tank)) {
                                Position randomPosition = PositionUtils.randomPosition(tank.getPosition(), MicroConfig.RANDOM_MOVE_DISTANCE);
                                CommandUtils.attackMove(tank, randomPosition);
                            }

                        } else {
                            if (tank.getGroundWeaponCooldown() > 25) { // UnitType.Terran_Siege_Tank_Tank_Mode.groundWeapon().damageCooldown() = 37
                                tank.move(mainPosition);
                            } else {
                                CommandUtils.attackMove(tank, mainPosition);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean shouldSiege(Unit tank, UnitInfo eui) {
        if (!tank.canSiege()) {
            return false;
        }

        // 렉 있으면 그냥 퉁퉁포로 조지기
//		if (LagObserver.groupsize() > 20 && eui.getType().groundWeapon().maxRange() < UnitType.Terran_Siege_Tank_Tank_Mode.groundWeapon().maxRange()) {
//			return false;
//		}

        if (eui.getType() == UnitType.Terran_Siege_Tank_Tank_Mode || eui.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {

            int distanceToTarget;
            Unit enemy = UnitUtils.unitInSight(eui);
            if (enemy != null) {
                distanceToTarget = tank.getDistance(enemy);
            } else {
                distanceToTarget = tank.getDistance(eui.getLastPosition());
            }

            if (saveUnitLevel == 0 && distanceToTarget <= MicroConfig.Tank.SIEGE_MODE_MAX_RANGE + 5) {
                return true;
            } else if (saveUnitLevel == 1 && distanceToTarget <= MicroConfig.Tank.SIEGE_MODE_SIGHT + 80.0) {
                return true;
            } else if (saveUnitLevel == 2 && distanceToTarget <= MicroConfig.Tank.SIEGE_MODE_MAX_RANGE + MicroConfig.Common.BACKOFF_DIST_SIEGE_TANK + 10) {
                return true;
            }

        } else {
            if (StrategyBoard.mainSquadMode == MainSquadMode.NO_MERCY) {
                return false;
            }

            if (eui.getType().isWorker()) {
                return false;
            }
            if (MicroUtils.isMeleeUnit(eui.getType())) {
                if (!hasEnoughBackUpUnitToSiege || Monster.Broodwar.self().supplyUsed() > 380) {
                    return false;
                }
            }

            int distanceToTarget = tank.getDistance(eui.getLastPosition());
            int siegeModeDistance;
            if (eui.getType().isBuilding()) {
                siegeModeDistance = MicroConfig.Tank.SIEGE_MODE_SIGHT;
            } else {
                siegeModeDistance = MicroConfig.Tank.SIEGE_MODE_MAX_RANGE + SIEGE_MODE_RANGE_MARGIN_DISTANCE;
            }

            if (distanceToTarget <= siegeModeDistance) { // TankPositionManager.Instance().isProperPositionToSiege(tank.getPosition(), false)
                return true;
            } else {
                if (!eui.getType().isBuilding()) {
                    List<Unit> siegeModeTanks = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, tank.getPosition(), MicroConfig.Tank.SIEGE_LINK_DISTANCE, UnitType.Terran_Siege_Tank_Siege_Mode);
                    for (Unit siegeModeTank : siegeModeTanks) {
                        if (tank.getID() == siegeModeTank.getID()) {
                            continue;
                        }
                        if (siegeModeTank.getDistance(eui.getLastPosition()) <= MicroConfig.Tank.SIEGE_MODE_MAX_RANGE + 5 && TankPositionManager.Instance().isProperPositionToSiege(tank.getPosition(), false)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
