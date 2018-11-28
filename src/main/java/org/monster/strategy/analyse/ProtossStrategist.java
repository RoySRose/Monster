package org.monster.strategy.analyse;

import bwapi.UnitType;
import org.monster.board.StrategyBoard;
import org.monster.common.util.UnitUtils;
import org.monster.decisionMakers.constant.EnemyStrategyOptions;
import org.monster.decisionMakers.decisionTypes.EnemyStrategy;

public class ProtossStrategist extends Strategist {

    public ProtossStrategist() {
        super(UnitType.Protoss_Cybernetics_Core);
    }

    @Override
    protected EnemyStrategy strategyPhase01() {
        if (hasType(Clue.ClueType.FAST_NEXSUS)) {
            if (hasInfo(Clue.ClueInfo.NEXSUS_FASTEST_DOUBLE)) {
                if (hasAnyType(Clue.ClueType.FAST_FORGE, Clue.ClueType.FAST_CANNON)) {
                    return EnemyStrategy.PROTOSS_FORGE_DOUBLE;
                } else {
                    return EnemyStrategy.PROTOSS_DOUBLE;
                }

            } else if (hasInfo(Clue.ClueInfo.NEXSUS_FAST_DOUBLE)) {
                if (hasAnyType(Clue.ClueType.FAST_FORGE, Clue.ClueType.FAST_CANNON)) {
                    return EnemyStrategy.PROTOSS_FORGE_DOUBLE;
                } else {
                    return EnemyStrategy.PROTOSS_GATE_DOUBLE;
                }
            }
        }

        if (hasInfo(Clue.ClueInfo.CORE_FAST)) {
            return EnemyStrategy.PROTOSS_1GATE_CORE;
        }

        if (hasInfo(Clue.ClueInfo.GATE_FAST_TWO)) {
            return EnemyStrategy.PROTOSS_2GATE;
        }

        // 2게이트 예측
        if (hasInfo(Clue.ClueInfo.GATE_TWO) && hasAnyInfo(Clue.ClueInfo.ASSIMILATOR_LATE, Clue.ClueInfo.NO_ASSIMILATOR)) {
            return EnemyStrategy.PROTOSS_2GATE;
        }
        if (hasInfo(Clue.ClueInfo.GATE_FAST_ONE) && hasAnyInfo(Clue.ClueInfo.ASSIMILATOR_LATE, Clue.ClueInfo.NO_ASSIMILATOR)) {
            return EnemyStrategy.PROTOSS_2GATE;
        }
        if (hasAllInfo(Clue.ClueInfo.GATE_NOT_FOUND, Clue.ClueInfo.NO_ASSIMILATOR)) {
            return EnemyStrategy.PROTOSS_2GATE_CENTER;
        }

        // 포지 더블 예측
        if (hasAllInfo(Clue.ClueInfo.FORGE_FAST_IN_EXPANSION, Clue.ClueInfo.NO_ASSIMILATOR)) {
            return EnemyStrategy.PROTOSS_FORGE_DOUBLE;
        }

        // 본진 캐논으로 시작하는 초보 프로토스
        if (hasInfo(Clue.ClueInfo.FORGE_FAST_IN_BASE)) {
            return EnemyStrategy.PROTOSS_FORGE_DEFENSE;
        }

        // 날빌 예상
        if (hasInfo(Clue.ClueInfo.CANNON_FAST_SOMEWHERE)) {
            return EnemyStrategy.PROTOSS_FORGE_CANNON_RUSH;
        }

        return EnemyStrategy.PROTOSS_INIT;
    }

    @Override
    protected EnemyStrategy strategyPhase02() {
        if (hasAnyInfo(Clue.ClueInfo.TEMPLAR_ARCH_FAST, Clue.ClueInfo.ADUN_FAST, Clue.ClueInfo.FAST_DARK)) {
            if (hasAnyType(Clue.ClueType.FAST_ROBO, Clue.ClueType.FAST_SHUTTLE)) {
                return EnemyStrategy.PROTOSS_DARK_DROP;
            } else {
                return EnemyStrategy.PROTOSS_FAST_DARK;
            }
        }

        if (hasAnyType(Clue.ClueType.FAST_ROBO, Clue.ClueType.FAST_SHUTTLE)) {
            if (hasAnyInfo(Clue.ClueInfo.ROBO_SUPPORT_FAST, Clue.ClueInfo.FAST_REAVER)) {
                return EnemyStrategy.PROTOSS_ROBOTICS_REAVER;
            } else if (hasAnyInfo(Clue.ClueInfo.OBSERVERTORY_FAST, Clue.ClueInfo.FAST_OBSERVER)) {
                return EnemyStrategy.PROTOSS_ROBOTICS_OB_DRAGOON;
            } else {
                return EnemyStrategy.PROTOSS_DARK_DROP;
            }
        }

        if (StrategyBoard.startStrategy.buildTimeMap.featureEnabled(EnemyStrategyOptions.BuildTimeMap.Feature.DOUBLE) || hasAnyInfo(Clue.ClueInfo.NEXSUS_FAST_DOUBLE, Clue.ClueInfo.NEXSUS_FASTEST_DOUBLE)) {
            if (hasInfo(Clue.ClueInfo.FAST_FLEET_BEACON)) {
                return EnemyStrategy.PROTOSS_DOUBLE_CARRIER;
            } else if (hasAnyInfo(Clue.ClueInfo.STARGATE_DOUBLE_FAST)) {
                return EnemyStrategy.PROTOSS_DOUBLE_CARRIER;
            } else if (hasAnyType(Clue.ClueType.FAST_FORGE, Clue.ClueType.FAST_CANNON)) {
                return EnemyStrategy.PROTOSS_DOUBLE_CARRIER;
            } else {
                return EnemyStrategy.PROTOSS_DOUBLE_GROUND;
            }
        }

        if (StrategyBoard.startStrategy.buildTimeMap.featureEnabled(EnemyStrategyOptions.BuildTimeMap.Feature.TWOGATE)) {
            if (hasType(Clue.ClueType.FAST_CORE)) {
                return EnemyStrategy.PROTOSS_TWOGATE_TECH;
            } else {
                return EnemyStrategy.PROTOSS_HARDCORE_ZEALOT;
            }
        }

        if (hasAnyInfo(Clue.ClueInfo.STARGATE_ONEGATE_FAST, Clue.ClueInfo.FAST_FLEET_BEACON)) {
            return EnemyStrategy.PROTOSS_STARGATE;
        }

        if (hasAnyInfo(Clue.ClueInfo.DRAGOON_RANGE_FAST)) {
            return EnemyStrategy.PROTOSS_FAST_DRAGOON;
        }

        if (hasAnyInfo(Clue.ClueInfo.FAST_THREE_ZEALOT, Clue.ClueInfo.GATE_FAST_TWO)) {
            return EnemyStrategy.PROTOSS_HARDCORE_ZEALOT;
        }

        if (hasAnyType(Clue.ClueType.FAST_ROBO)) {
            if (hasAnyType(Clue.ClueType.FAST_ADUN, Clue.ClueType.FAST_TEMPLAR_ARCH)) {
                return EnemyStrategy.PROTOSS_DARK_DROP;
            } else if (hasAnyType(Clue.ClueType.FAST_ROBO_SUPPORT)) {
                return EnemyStrategy.PROTOSS_ROBOTICS_REAVER;
            } else if (hasAnyType(Clue.ClueType.FAST_OB)) {
                return EnemyStrategy.PROTOSS_ROBOTICS_OB_DRAGOON;
            }
        }

        if (hasAnyInfo(Clue.ClueInfo.NO_ASSIMILATOR, Clue.ClueInfo.ASSIMILATOR_LATE)) {
            if (hasInfo(Clue.ClueInfo.NEXSUS_NOT_DOUBLE)) {
                if (hasInfo(Clue.ClueInfo.ASSIMILATOR_LATE)) {
                    if (hasAnyInfo(Clue.ClueInfo.GATE_FAST_ONE, Clue.ClueInfo.GATE_ONE)) {
                        return EnemyStrategy.PROTOSS_FAST_DRAGOON;
                    } else if (hasAnyInfo(Clue.ClueInfo.GATE_FAST_TWO)) {
                        return EnemyStrategy.PROTOSS_TWOGATE_TECH;
                    }
                } else if (hasInfo(Clue.ClueInfo.NO_ASSIMILATOR)) {
                    return EnemyStrategy.PROTOSS_HARDCORE_ZEALOT;
                }
            } else if (hasType(Clue.ClueType.FAST_CORE)) {
                return EnemyStrategy.PROTOSS_FAST_DRAGOON;
            } else if (hasAnyInfo(Clue.ClueInfo.GATE_TWO)) {
                return EnemyStrategy.PROTOSS_HARDCORE_ZEALOT;
            } else {
                return EnemyStrategy.PROTOSS_DOUBLE_GROUND;
            }
        }

        return EnemyStrategy.PROTOSS_FAST_DARK;
    }

    @Override
    protected EnemyStrategy strategyPhase03() {

        boolean carrierExist = UnitUtils.enemyUnitDiscovered(UnitType.Protoss_Fleet_Beacon, UnitType.Protoss_Carrier);
        if (carrierExist) {
            int enemyGroundUnitPower = UnitUtils.enemyGroundUnitPower();
            if (enemyGroundUnitPower < 10) {
                return EnemyStrategy.PROTOSS_PROTOSS_AIR3;
            } else {
                return EnemyStrategy.PROTOSS_PROTOSS_AIR2;
            }
        }

        boolean airUnitExsit = UnitUtils.enemyUnitDiscovered(
                UnitType.Protoss_Arbiter_Tribunal, UnitType.Protoss_Stargate, UnitType.Protoss_Arbiter, UnitType.Protoss_Scout);

        if (airUnitExsit) {
            return EnemyStrategy.PROTOSS_PROTOSS_AIR1;
        } else {
            return EnemyStrategy.PROTOSS_GROUND;
        }
    }
}
