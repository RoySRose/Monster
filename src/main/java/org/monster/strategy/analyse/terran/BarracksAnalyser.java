package org.monster.strategy.analyse.terran;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.strategy.constant.EnemyStrategy;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;
import org.monster.strategy.manage.EnemyStrategyAnalyzer;

import java.util.List;

public class BarracksAnalyser extends UnitAnalyser {

    public BarracksAnalyser() {
        super(UnitType.Terran_Barracks);
    }

    @Override
    public void analyse() {
        fastBarracks();
    }

    private void fastBarracks() {
        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            if (found.size() >= 2) { // 배럭 2개 이상
                int firstBuildFrame = buildStartFrameDefaultJustBefore(found.get(0));
                int secondBuildFrame = buildStartFrameDefaultJustBefore(found.get(1));
                int twoBarrackSecondBarrackFrame = EnemyStrategy.TERRAN_2BARRACKS.buildTimeMap.frameOfIndex(UnitType.Terran_Barracks, 1, 20);
                int bbsSecondBarrackFrame = EnemyStrategy.TERRAN_BBS.buildTimeMap.frameOfIndex(UnitType.Terran_Barracks, 1, 20);

                if (firstBuildFrame < bbsSecondBarrackFrame && secondBuildFrame < bbsSecondBarrackFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.BARRACK_FASTEST_TWO);
                } else if (firstBuildFrame < twoBarrackSecondBarrackFrame && secondBuildFrame < twoBarrackSecondBarrackFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.BARRACK_FAST_TWO);
                } else {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.BARRACK_TWO);
                }

            } else if (found.size() == 1) { // 배럭 1개
                int firstBuildFrame = buildStartFrameDefaultJustBefore(found.get(0));
                int mechanicFirstBarrackFrame = EnemyStrategy.TERRAN_MECHANIC.buildTimeMap.frame(UnitType.Terran_Barracks, 15);
                if (firstBuildFrame < mechanicFirstBarrackFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.BARRACK_FAST_ONE);
                } else {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.BARRACK_ONE);
                }
            }
        } else {
            int mechanicFirstBarrackFrame = EnemyStrategy.TERRAN_MECHANIC.buildTimeMap.frame(UnitType.Terran_Barracks, 15);
            int baseLastCheckFrame = EnemyStrategyAnalyzer.Instance().lastCheckFrame(EnemyStrategyAnalyzer.LastCheckLocation.BASE);
            if (baseLastCheckFrame > mechanicFirstBarrackFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.BARRACK_NOT_FOUND);
            }
        }
    }
}
