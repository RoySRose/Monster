package org.monster.strategy.analyse;

import bwapi.Race;
import bwapi.UnitType;
import org.monster.board.StrategyBoard;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.strategy.constant.EnemyStrategy;
import org.monster.strategy.constant.EnemyStrategyOptions;
import org.monster.strategy.manage.ClueManager;
import org.monster.strategy.manage.EnemyBuildTimer;

public abstract class Strategist {
    private int phase = 1;
    private UnitType phase01keyUnitType;

    public Strategist(UnitType phase01keyUnitType) {
        this.phase01keyUnitType = phase01keyUnitType;
    }

    public int getPhase() {
        return phase;
    }

    public EnemyStrategy strategyToApply() {
        switch (phaseSwitch()) {
            case 1:
                return StrategyBoard.startStrategy = strategyPhase01();
            case 2:
                return strategyPhase02();
            default:
                return strategyPhase03();
        }
    }

    protected abstract EnemyStrategy strategyPhase01();

    protected abstract EnemyStrategy strategyPhase02();

    protected abstract EnemyStrategy strategyPhase03();

    protected int phaseSwitch() {
        if (phase == 1 && phase01End()) {
            phase++;
        } else if (phase == 2 && phase02End()) {
            phase++;
        }
        return phase;
    }

    private boolean phase01End() {

        if (PlayerUtils.enemyRace() == Race.Protoss) {
            if (TimeUtils.afterTime(3, 30)) {
                return true;
            }
        } else if (PlayerUtils.enemyRace() == Race.Zerg) {
            if (TimeUtils.afterTime(3, 45)) {
                return true;
            }
        } else if (PlayerUtils.enemyRace() == Race.Terran) {
            if (TimeUtils.afterTime(3, 30)) {
                return true;
            }
        }

        int buildTimeExpect = EnemyBuildTimer.Instance().getBuildStartFrameExpect(phase01keyUnitType);
        return buildTimeExpect != CommonCode.UNKNOWN && TimeUtils.after(buildTimeExpect + phase01keyUnitType.buildTime());
    }

    private boolean phase02End() {
        if (TimeUtils.after(8 * TimeUtils.MINUTE)) {
            System.out.println("TIME IS UP");
            return true;
        }

        if (!StrategyBoard.currentStrategy.missionTypeList.isEmpty()) {
            for (EnemyStrategyOptions.Mission.MissionType missionType : StrategyBoard.currentStrategy.missionTypeList) {
                if (!EnemyStrategyOptions.Mission.complete(missionType)) {
                    return false;
                }
            }
//			System.out.println("MISSION COMPLETE");
            return true;

        } else {
            // default mission
            if (UnitUtils.getCompletedUnitCount(UnitType.Terran_Command_Center) < 2) {
                return false;
            }

            if (UnitUtils.myFactoryUnitSupplyCount() + UnitUtils.myWraithUnitSupplyCount() < 32) {
                return false;
            }
//			System.out.println("DEFAULT MISSION COMPLETE");
            return true;

        }
    }

    public boolean hasInfo(Clue.ClueInfo info) {
        return ClueManager.Instance().containsClueInfo(info);
    }

    public boolean hasType(Clue.ClueType type) {
        return ClueManager.Instance().containsClueType(type);
    }

    public boolean hasAnyInfo(Clue.ClueInfo... infos) {
        for (Clue.ClueInfo info : infos) {
            if (hasInfo(info)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllInfo(Clue.ClueInfo... infos) {
        for (Clue.ClueInfo info : infos) {
            if (!hasInfo(info)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAnyType(Clue.ClueType... types) {
        for (Clue.ClueType type : types) {
            if (hasType(type)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllType(Clue.ClueType... types) {
        for (Clue.ClueType type : types) {
            if (!hasType(type)) {
                return false;
            }
        }
        return true;
    }
}
