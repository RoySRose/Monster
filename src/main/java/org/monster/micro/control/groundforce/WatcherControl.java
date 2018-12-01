package org.monster.micro.control.groundforce;

import bwapi.Position;
import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.constant.CommonCode.UnitFindStatus;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.CommandUtils;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.PositionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.micro.FleeOption;
import org.monster.micro.KitingOption;
import org.monster.micro.MicroDecision;
import org.monster.micro.MicroDecisionMakerPrebot1;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.control.Control;
import org.monster.board.StrategyBoard;
import org.monster.strategy.manage.SpiderMineManger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/// MainSquad <-> 적 기지 or 주력병력 주둔지 이동하여 마인 매설 
public class WatcherControl extends Control {

    private Unit regroupLeader;
    private int saveUnitLevel;
    private Position avoidBunkerPosition;

    public void setRegroupLeader(Unit regroupLeader) {
        this.regroupLeader = regroupLeader;
    }

    public void setSaveUnitLevel(int saveUnitLevel) {
        this.saveUnitLevel = saveUnitLevel;
    }

    public void setAvoidBunkerPosition(Position avoidBunkerPosition) {
        this.avoidBunkerPosition = avoidBunkerPosition;
    }

    @Override
    public void control(Collection<Unit> unitList, Collection<UnitInfo> euiList) {
        if (TimeUtils.before(StrategyBoard.findRatFinishFrame)) {
            findRat(unitList);
            return;
        }

        Position fleePosition = StrategyBoard.mainSquadCenter;
        int coverRadius = StrategyBoard.mainSquadCoverRadius;
        if (PlayerUtils.enemyRace() == Race.Terran) {
            int tankCount = UnitUtils.getUnitCount(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode);
            if (tankCount >= 3 && StrategyBoard.mainSquadCrossBridge) {
                int watcherBackEnoughDistance = (int) (StrategyBoard.mainSquadCoverRadius * (1 + (Math.log(unitList.size()) * 0.3)));
                double radian = MicroUtils.targetDirectionRadian(fleePosition, BaseUtils.myMainBase().getPosition());
                fleePosition = MicroUtils.getMovePosition(fleePosition, radian, watcherBackEnoughDistance);

//				List<Unit> centers = UnitUtils.getUnitList(CommonCode.UnitFindStatus.ALL, UnitType.Terran_Command_Center);
//				Region myBaseRegion = BWTA.getRegion(BaseUtils.myMainBase().getPosition());
//				Region myExpansionRegion = BWTA.getRegion(BaseUtils.myFirstExpansion().getPosition());
//				for (Unit center : centers) {
//					Region centerRegion = BWTA.getRegion(center.getPosition());
//					if (centerRegion != myBaseRegion
//							|| centerRegion != myExpansionRegion) {
//						fleePosition = center.getPosition();
//						break;
//					}
//				}
            }

        } else {
            if (StrategyBoard.currentStrategy == EnemyStrategy.PROTOSS_FAST_DARK || StrategyBoard.currentStrategy == EnemyStrategy.ZERG_FAST_LURKER) {
                List<Unit> turretList = UnitUtils.getUnitList(CommonCode.UnitFindStatus.ALL, UnitType.Terran_Missile_Turret);
                Unit closeTurret = UnitUtils.getClosestUnitToPosition(turretList, StrategyBoard.mainSquadCenter);
                if (closeTurret != null) {
                    fleePosition = closeTurret.getPosition();
                    coverRadius = 50;
                }
            }
        }

        if (regroupLeader != null) {
            regroup(unitList, euiList, fleePosition, coverRadius);
        } else {
            fight(unitList, euiList, fleePosition, coverRadius);
        }
    }

    private void fight(Collection<Unit> unitList, Collection<UnitInfo> euiList, Position fleePosition, int coverRadius) {
        FleeOption fOption = new FleeOption(fleePosition, false, MicroConfig.Angles.WIDE);
        KitingOption kOption = new KitingOption(fOption, KitingOption.CoolTimeAttack.KEEP_SAFE_DISTANCE);

        FleeOption fOptionMainBattle = new FleeOption(fleePosition, true, MicroConfig.Angles.WIDE);
        KitingOption kOptionMainBattle = new KitingOption(fOptionMainBattle, KitingOption.CoolTimeAttack.COOLTIME_ALWAYS_IN_RANGE);

        List<Unit> otherMechanics = UnitUtils.getUnitList(UnitFindStatus.COMPLETE, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_Goliath);

        for (Unit unit : unitList) {
            if (skipControl(unit)) {
                continue;
            }

//			Decision decision = decisionMaker.makeDecision(unit, euiList, smallFightPredict == StrategyCode.SmallFightPredict.OVERWHELM);
            MicroDecision decision = MicroDecisionMakerPrebot1.makeDecisionPrebot1(unit, euiList, null, saveUnitLevel);

            if (decision.type == MicroDecision.MicroDecisionType.FLEE_FROM_UNIT) {
                MicroUtils.flee(unit, decision.eui.getLastPosition(), fOption);

            } else if (decision.type == MicroDecision.MicroDecisionType.KITING_UNIT) {
                if (spiderMineOrderIssue(unit)) {
                    continue;
                }
                Unit enemyUnit = UnitUtils.unitInSight(decision.eui);
                if (enemyUnit != null) {
                    if (MicroUtils.isRemovableEnemySpiderMine(unit, decision.eui)) {
                        MicroUtils.holdControlToRemoveMine(unit, decision.eui.getLastPosition(), fOption);

                    } else {
                        if (unit.getDistance(fleePosition) < coverRadius) {
                            if (otherMechanics.size() >= 10) {
                                MicroUtils.kiting(unit, decision.eui, kOptionMainBattle);
                            } else if (otherMechanics.size() >= 3) {
                                MicroUtils.kiting(unit, decision.eui, kOptionMainBattle);
                            } else {
                                MicroUtils.kiting(unit, decision.eui, kOption);
                            }
                        } else {
                            MicroUtils.kiting(unit, decision.eui, kOption);
                        }
                    }
                } else {
                    MicroUtils.kiting(unit, decision.eui, kOption);
                }

            } else if (decision.type == MicroDecision.MicroDecisionType.ATTACK_POSITION) {
                if (spiderMineOrderIssue(unit)) {
                    continue;
                }
                if (MicroUtils.arrivedToPosition(unit, StrategyBoard.watcherPosition)) {
                    if (avoidBunkerPosition != null) {
                        Position bunkerAvoidPosition = new Position(avoidBunkerPosition.getX(), avoidBunkerPosition.getY() + 80).makeValid();
                        CommandUtils.attackMove(unit, bunkerAvoidPosition);
                    } else {
                        if (MicroUtils.timeToRandomMove(unit)) {
                            Position randomPosition = PositionUtils.randomPosition(unit.getPosition(), MicroConfig.RANDOM_MOVE_DISTANCE);

                            boolean avoidExpansionLocation = false;
                            if (UnitUtils.getUnitCount(CommonCode.UnitFindStatus.ALL, UnitType.Terran_Command_Center) <= 1) {
                                BaseLocation myFirstExpansion = BaseUtils.myFirstExpansion();
                                if (randomPosition.getDistance(myFirstExpansion) < 200) {
                                    avoidExpansionLocation = true;
                                }
                            }
                            if (!avoidExpansionLocation) {
                                CommandUtils.attackMove(unit, randomPosition);
                            }
                        }
                    }

                } else {
                    CommandUtils.attackMove(unit, StrategyBoard.watcherPosition);
                }
            }
        }
    }

    /// 전방에 있는 벌처는 후퇴, 후속 벌처는 전진하여 squad유닛을 정비한다.
    private void regroup(Collection<Unit> unitList, Collection<UnitInfo> euiList, Position fleePosition, int coverRadius) {
        int regroupRadius = Math.min(UnitType.Terran_Vulture.sightRange() + unitList.size() * 80, 1000);
//		System.out.println("regroupRadius : " + regroupRadius);

        List<Unit> fightUnitList = new ArrayList<>();
        for (Unit unit : unitList) {
            if (skipControl(unit)) {
                continue;
            }
            if (unit.getDistance(fleePosition) < coverRadius * 7 / 10) {
                fightUnitList.add(unit);
                continue;
            }
            if (unit.getID() != regroupLeader.getID() && unit.getDistance(regroupLeader) > regroupRadius) {
                fightUnitList.add(unit);
                continue;
            }
            CommandUtils.move(unit, fleePosition);
        }

        if (!fightUnitList.isEmpty()) {
            fight(fightUnitList, euiList, fleePosition, coverRadius);
        }

    }

    private boolean spiderMineOrderIssue(Unit vulture) {
        Position positionToMine = SpiderMineManger.Instance().getPositionReserved(vulture);
        if (positionToMine == null) {
            positionToMine = SpiderMineManger.Instance().reserveSpiderMine(vulture, StrategyBoard.watcherMinePositionLevel);
        }
        if (positionToMine != null) {
            CommandUtils.useTechPosition(vulture, TechType.Spider_Mines, positionToMine);
            return true;
        }

        Unit spiderMineToRemove = SpiderMineManger.Instance().mineToRemove(vulture);
        if (spiderMineToRemove != null) {
            CommandUtils.attackUnit(vulture, spiderMineToRemove);
            return true;
        }

        return false;
    }
}
