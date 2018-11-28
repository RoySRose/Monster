package org.monster.micro.control.airforce;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.WeaponType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.constant.CommonCode.EnemyUnitFindRange;
import org.monster.common.util.CommandUtils;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.PositionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.main.Monster;
import org.monster.micro.MicroDecision;
import org.monster.micro.MicroDecisionMaker;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.control.Control;
import org.monster.micro.targeting.WraithTargetCalculator;
import org.monster.board.StrategyBoard;
import org.monster.strategy.manage.AirForceManager;
import org.monster.strategy.manage.AirForceTeam;
import org.monster.strategy.manage.PositionFinder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

@Deprecated
public class AirForceControl extends Control {


    //TODO sample
    @Override
    public void control(Collection<Unit> airunits, Collection<UnitInfo> euiList) {
        if (airunits.isEmpty()) {
            return;
        }

        // 팀 단위로 wraithList가 세팅되어야 한다.
        int memberId = airunits.iterator().next().getID();
        AirForceTeam airForceTeam = AirForceManager.Instance().airForTeamOfUnit(memberId);
        if (airForceTeam.leaderUnit == null) {
            System.out.println(memberId + "'s airSquad has no leader. member.size=" + airForceTeam.memberList.size());
            return;
        }
        if (AirForceManager.Instance().getTargetPositions().isEmpty()) {
            System.out.println("AirForceManager targetPositions is empty");
            return;
        }

//		if (true) {

        int enemySize = UnitUtils.getEnemyUnitInfoList(EnemyUnitFindRange.ALL).size();
        if (TimeUtils.afterTime(10, 0) && enemySize <= 3) {
            findRat(airunits);
            return;
        }
        if (Monster.Broodwar.self().supplyUsed() > 300 && UnitUtils.getEnemyUnitInfoList(CommonCode.EnemyUnitFindRange.ALL).size() <= 3 && UnitUtils.enemyAirUnitPower() == 0) {
            for (Unit airunit : airunits) {
                CommandUtils.attackMove(airunit, StrategyBoard.mainPosition);
            }
            return;
        }


        MicroDecisionMaker decisionMaker = new MicroDecisionMaker(new WraithTargetCalculator());

        // 결정: 도망(FLEE_FROM_UNIT), 공격(ATTACK_UNIT), 카이팅(KITING_UNIT), 클로킹(CHANGE_MODE), 이동(ATTACK_POSITION)
        // 결정상세(공격, 카이팅, 이동시): 공격(ATTACK_UNIT), 뭉치기(UNITE), 카이팅(KITING_UNIT), 이동(ATTACK_POSITION)
        MicroDecision decision = null;
        MicroDecision decisionDetail = null;

        boolean applyDefenseModeFlee = false;
        if (AirForceManager.Instance().isAirForceDefenseMode()) {
            if (airForceTeam.repairCenter == null) {
                if (dangerousOutOfMyRegion(airForceTeam.leaderUnit)) {
                    if (StrategyBoard.mainSquadMode.isAttackMode) {
                        applyDefenseModeFlee = StrategyBoard.mainSquadCenter.getDistance(airForceTeam.leaderUnit) > StrategyBoard.mainSquadCoverRadius + 250;
                        if (!applyDefenseModeFlee) {
                            Set<UnitInfo> killerInRadius = UnitUtils.getCompleteEnemyInfosInRadiusForAir(airForceTeam.leaderUnit.getPosition(), 200, UnitUtils.wraithKillerUnitType());
                            applyDefenseModeFlee = !killerInRadius.isEmpty();
                        }

                    } else {
                        applyDefenseModeFlee = true;
                    }
                }
                if (applyDefenseModeFlee) {
                    decision = MicroDecision.fleeFromUnit(airForceTeam.leaderUnit, null);

                    // apply airforce decision
                    Position airFleePosition = PositionFinder.Instance().baseFirstChokeMiddlePosition();
                    Position airDrivingPosition = airDrivingPosition(airForceTeam, airFleePosition, MicroConfig.Angles.AIR_FORCE_FREE);
                    airForceTeam.leaderOrderPosition = airDrivingPosition;
                }
            }
        }

        if (!applyDefenseModeFlee) {
            if (!airForceTeam.retreating()) {
                decision = decisionMaker.makeDecisionForAirForce(airForceTeam, euiList, AirForceManager.Instance().getStrikeLevel());
            } else {
                decision = MicroDecision.fleeFromUnit(airForceTeam.leaderUnit, null);
            }

            if (decision.type == MicroDecision.MicroDecisionType.ATTACK_UNIT || decision.type == MicroDecision.MicroDecisionType.KITING_UNIT) {
                decisionDetail = decisionMaker.makeDecisionForAirForceMovingDetail(airForceTeam, Arrays.asList(decision.eui), false);
            } else if (decision.type == MicroDecision.MicroDecisionType.ATTACK_POSITION) { // 목적지 이동시 준수할 세부사항
                boolean movingAttack = true;
                if (airForceTeam.repairCenter != null) {
                    movingAttack = false;
                }
                decisionDetail = decisionMaker.makeDecisionForAirForceMovingDetail(airForceTeam, euiList, movingAttack);
            }

            // System.out.println("decision: " + decision + " / " + decisionDetail);
            this.applyAirForceDecision(airForceTeam, decision, decisionDetail);
        }


        if (decisionDetail != null) {
            // 공격 쿨타임
            if (decisionDetail.type == MicroDecision.MicroDecisionType.ATTACK_UNIT) {
                // ATTACK_UNIT, KITING_UNIT 동일
                if (decision.type == MicroDecision.MicroDecisionType.ATTACK_UNIT || decision.type == MicroDecision.MicroDecisionType.KITING_UNIT) {
                    for (Unit airunit : airunits) {
                        CommandUtils.attackUnit(airunit, decisionDetail.eui.getUnit());
                    }
                }
                // 지나가면서 한대씩 때리기
                else if (decision.type == MicroDecision.MicroDecisionType.ATTACK_POSITION) {
                    for (Unit airunit : airunits) {
                        airforceRightClick(airunit, decisionDetail.eui.getUnit());
                    }
                }
            }
            // 뭉치기
            else if (decisionDetail.type == MicroDecision.MicroDecisionType.UNITE) {
                for (Unit airunit : airunits) {
                    airforceRightClick(airunit, airForceTeam.leaderUnit.getPosition());
                }
            }
            // 전진 카이팅, 카이팅
            else if (decisionDetail.type == MicroDecision.MicroDecisionType.ATTACK_POSITION || decisionDetail.type == MicroDecision.MicroDecisionType.KITING_UNIT) {
                if (airForceTeam.repairCenter != null) {
                    Position insidePosition = PositionFinder.Instance().commandCenterInsidePosition(airForceTeam.repairCenter);
                    for (Unit airunit : airunits) {
                        if (!MicroUtils.isBeingHealed(airunit)) {
                            if (airunit.getDistance(insidePosition) < 100) {
                                if (MicroUtils.timeToRandomMove(airunit) && TimeUtils.elapsedFrames(airunit.getLastCommandFrame()) > 8 * TimeUtils.SECOND) {
                                    Position randomPosition = PositionUtils.randomPosition(insidePosition, 100);
                                    CommandUtils.attackMove(airunit, randomPosition);
                                }
                            } else {
                                airforceRightClick(airunit, airForceTeam.leaderOrderPosition);
                            }
                        }
                    }
                } else {
                    for (Unit airunit : airunits) {
                        airforceRightClick(airunit, airForceTeam.leaderOrderPosition);
                    }
                }
            }

        } else if (decision.type == MicroDecision.MicroDecisionType.FLEE_FROM_UNIT) { // 도망
            for (Unit airunit : airunits) {
                airforceRightClick(airunit, airForceTeam.leaderOrderPosition);
            }

        }
//		else if (decision.type == DecisionType.CHANGE_MODE) { // 클로킹
//			if (airForceTeam.cloakingMode) {
//				airForceTeam.decloak();
//				for (Unit airunit : airunits) {
//					if (airunit.getType() == UnitType.Terran_Wraith) {
//						airunit.decloak();
//					}
//				}
//			} else {
//				airForceTeam.cloak();
//				for (Unit airunit : airunits) {
//					if (airunit.getType() == UnitType.Terran_Wraith) {
//						airunit.cloak();
//					}
//				}
//			}
//		}
    }

    // 결정: 도망(FLEE_FROM_UNIT), 공격(ATTACK_UNIT), 카이팅(KITING_UNIT), 클로킹(CHANGE_MODE), 이동(ATTACK_POSITION)
    // 결정상세(공격, 카이팅, 이동시): 공격(ATTACK_UNIT), 뭉치기(UNITE), 카이팅(KITING_UNIT), 이동(ATTACK_POSITION)
    private void applyAirForceDecision(AirForceTeam airForceTeam, MicroDecision decision, MicroDecision decisionDetail) {
        Position airDrivingPosition = null;

        if (decision.type == MicroDecision.MicroDecisionType.ATTACK_UNIT || decision.type == MicroDecision.MicroDecisionType.KITING_UNIT) {
            if (wraithKitingType(decision.eui)) {
                if (decisionDetail.type == MicroDecision.MicroDecisionType.KITING_UNIT) { // 카이팅 후퇴
                    if (AirForceManager.Instance().isAirForceDefenseMode()) {
                        Position airFleePosition = PositionFinder.Instance().baseFirstChokeMiddlePosition();
                        airDrivingPosition = airDrivingPosition(airForceTeam, airFleePosition, MicroConfig.Angles.AIR_FORCE_FREE);

                    } else {
                        // airDrivingPosition = airDrivingPosition(airForceTeam, AirForceManager.Instance().getRetreatPosition(), airForceTeam.driveAngle); // kiting flee
                        Position airFleePosition = airFeePosition(airForceTeam, decision.eui);
                        airDrivingPosition = airDrivingPosition(airForceTeam, airFleePosition, MicroConfig.Angles.AIR_FORCE_FREE);
                    }

                } else {
                    if (decision.type == MicroDecision.MicroDecisionType.ATTACK_UNIT) { // 제자리 공격
                        airDrivingPosition = airForceTeam.leaderUnit.getPosition();
                    } else if (decision.type == MicroDecision.MicroDecisionType.KITING_UNIT) { // 따라가서 공격
                        Unit enemyInSight = UnitUtils.unitInSight(decision.eui);
                        if (enemyInSight == null) {
                            airDrivingPosition = decision.eui.getLastPosition();
                        } else {
                            Unit leaderUnit = airForceTeam.leaderUnit;
                            double distanceToAttack = leaderUnit.getDistance(enemyInSight) - UnitType.Terran_Wraith.airWeapon().maxRange();
                            int catchTime = (int) (distanceToAttack / UnitType.Terran_Wraith.topSpeed());
                            if (catchTime > 0) {
                                airDrivingPosition = decision.eui.getLastPosition();
                            } else {
                                Position airFleePosition = airFeePosition(airForceTeam, decision.eui);
                                airDrivingPosition = airDrivingPosition(airForceTeam, airFleePosition, airForceTeam.driveAngle);
                            }
                        }
                    }
                }

            } else {
                if (decision.type == MicroDecision.MicroDecisionType.ATTACK_UNIT) {
                    airDrivingPosition = airForceTeam.leaderUnit.getPosition();
                } else if (decision.type == MicroDecision.MicroDecisionType.KITING_UNIT) {
                    airDrivingPosition = decision.eui.getLastPosition();
                }
            }

        } else if (decision.type == MicroDecision.MicroDecisionType.ATTACK_POSITION) {
            if (airForceTeam.repairCenter != null) {
                Position repairCenter = PositionFinder.Instance().commandCenterInsidePosition(airForceTeam.repairCenter);
                airDrivingPosition = airDrivingPosition(airForceTeam, repairCenter, airForceTeam.driveAngle);

            } else {
                airDrivingPosition = airDrivingPosition(airForceTeam, airForceTeam.getTargetPosition(), airForceTeam.driveAngle);
                if (airForceTeam.leaderUnit.getPosition().getDistance(airForceTeam.getTargetPosition()) < 150) {
                    airForceTeam.changeTargetIndex();
                }
            }

        } else if (decision.type == MicroDecision.MicroDecisionType.FLEE_FROM_UNIT) {
            airForceTeam.retreat(decision.eui);
            Position airFleePosition = airFeePosition(airForceTeam, airForceTeam.fleeEui);
            airDrivingPosition = airDrivingPosition(airForceTeam, airFleePosition, airForceTeam.driveAngle);
        }

        airForceTeam.leaderOrderPosition = airDrivingPosition;
    }

//	private Unit closestAssistant(Unit wraith, UnitInfo eui) {
//		List<Unit> assistUnitList = null;
//		if (eui.getType().isFlyer()) {
//			assistUnitList = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Missile_Turret, UnitType.Terran_Goliath);
//		} else {
//			assistUnitList = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Vulture, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Goliath);
//		}
//		Unit closeAssistant = UnitUtils.getClosestUnitToPosition(assistUnitList, wraith.getPosition());
//		if (closeAssistant == null || MicroUtils.isInWeaponRange(closeAssistant, eui)) { // 도와줄 아군이 없거나 이미 근처에 있는 경우
//			return null;
//		}
//		
//		return closeAssistant;
//	}

    private Position airDrivingPosition(AirForceTeam airForceTeam, Position goalPosition, int[] angle) {
        Position leaderPosition = airForceTeam.leaderUnit.getPosition();
        Position airDrivingPosition = MicroUtils.airDrivingPosition(leaderPosition, goalPosition, angle, true);
        if (airDrivingPosition == null) {
            airForceTeam.switchDriveAngle();
            airDrivingPosition = MicroUtils.airDrivingPosition(leaderPosition, goalPosition, angle, true);
        }
        if (airDrivingPosition == null) {
            airDrivingPosition = goalPosition;
        }
        return airDrivingPosition;
    }

    private Position airFeePosition(AirForceTeam airForceTeam, UnitInfo eui) {
        Position enemyPosition;
        Unit enemyUnit = Monster.Broodwar.getUnit(eui.getUnitID());
        if (enemyUnit != null) {
            enemyPosition = enemyUnit.getPosition();
        } else {
            enemyPosition = airForceTeam.fleeEui.getLastPosition();
        }
        Position airFleePosition = MicroUtils.airFleePosition(airForceTeam.leaderUnit.getPosition(), enemyPosition);
        if (airFleePosition == null) {
            airFleePosition = AirForceManager.Instance().getRetreatPosition();
        }
        return airFleePosition;
    }

    private boolean wraithKitingType(UnitInfo eui) {
        if (UnitUtils.enemyUnitDiscovered(UnitType.Terran_Goliath)) {
            return eui.getType() != UnitType.Terran_Wraith && eui.getType() != UnitType.Terran_Dropship;
        }
        return eui.getType().airWeapon() != WeaponType.None && eui.getType().airWeapon().maxRange() < UnitType.Terran_Wraith.groundWeapon().maxRange();
    }

    private void airforceRightClick(Unit airunit, Position position) {
        airunit.rightClick(position);
//		CommandUtils.fastestRightClick(airunit, position);
//		CommandUtils.rightClick(airunit, position);
    }

    private void airforceRightClick(Unit airunit, Unit target) {
        airunit.rightClick(target);
//		CommandUtils.fastestRightClick(airunit, target);
//		CommandUtils.rightClick(airunit, target);
    }
}
