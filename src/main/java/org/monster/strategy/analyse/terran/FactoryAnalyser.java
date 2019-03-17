package org.monster.strategy.analyse.terran;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.strategy.constant.EnemyStrategy;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;
import org.monster.strategy.manage.EnemyStrategyAnalyzer;

import java.util.List;

public class FactoryAnalyser extends UnitAnalyser {

    public FactoryAnalyser() {
        super(UnitType.Terran_Factory);
    }

    @Override
    public void analyse() {
        fastFactory();
    }

    private void fastFactory() {
        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            if (found.size() >= 2) { // 팩토리 2개 이상
                int firstBuildFrame = buildStartFrameDefaultJustBefore(found.get(0));
                int secondBuildFrame = buildStartFrameDefaultJustBefore(found.get(1));
                int twoFacSecondFacFrame = EnemyStrategy.TERRAN_2FAC.buildTimeMap.frameOfIndex(UnitType.Terran_Factory, 1, 20);

                if (firstBuildFrame < twoFacSecondFacFrame && secondBuildFrame < twoFacSecondFacFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FACTORY_FAST_TWO);
                } else {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FACTORY_TWO);
                }

            } else if (found.size() == 1) { // 팩토리 1개
                int firstBuildFrame = buildStartFrameDefaultJustBefore(found.get(0));
                int mechanicFirstBarrackFrame = EnemyStrategy.TERRAN_2FAC.buildTimeMap.frame(UnitType.Terran_Factory, 15);
                if (firstBuildFrame < mechanicFirstBarrackFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FACTORY_FAST_ONE);
                } else {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FACTORY_ONE);
                }
            }
        } else {
            int firstBuildFrame = EnemyStrategy.TERRAN_2FAC.buildTimeMap.frame(UnitType.Terran_Barracks, 30);
            int baseLastCheckFrame = EnemyStrategyAnalyzer.Instance().lastCheckFrame(EnemyStrategyAnalyzer.LastCheckLocation.BASE);
            if (baseLastCheckFrame > firstBuildFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FACTORY_NOT_FOUND);
            }
        }
    }
}
