package org.monster.strategy.analyse.zerg;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.RegionType;
import org.monster.strategy.constant.EnemyStrategy;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;
import org.monster.strategy.manage.EnemyStrategyAnalyzer;

import java.util.List;

public class HatcheryAnalyser extends UnitAnalyser {

    public HatcheryAnalyser() {
        super(UnitType.Zerg_Hatchery);
    }

    @Override
    public void analyse() {
        analyseStartingHatch();
        analyse3Hat();
    }

    private void analyse3Hat() {
        if (ClueManager.Instance().containsClueInfo(Clue.ClueInfo.DOUBLE_HATCH_3HAT)) {
            return;
        }

        List<UnitInfo> found = found();

        if (found.size() == 3) {
            int latestHatchFrame = 0;
            for (UnitInfo eui : found) {
                int buildFrame = buildStartFrameDefaultJustBefore(eui);
                if (buildFrame > latestHatchFrame) {
                    latestHatchFrame = buildFrame;
                }
            }

            int thirdHathBuildFrame = EnemyStrategy.ZERG_3HAT.buildTimeMap.frameOfIndex(UnitType.Zerg_Hatchery, 1, 20);
            if (latestHatchFrame < thirdHathBuildFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.DOUBLE_HATCH_3HAT);
            }
        }

    }

    private void analyseStartingHatch() {
        if (ClueManager.Instance().containsClueType(Clue.ClueType.DOUBLE_HATCH)) {
            return;
        }

        int doubleFrame = EnemyStrategy.ZERG_2HAT_GAS.buildTimeMap.frame(UnitType.Zerg_Hatchery, 10);
        int overPoolFrame = EnemyStrategy.ZERG_OVERPOOL.buildTimeMap.frame(UnitType.Zerg_Hatchery, 10);
        int nineDroneFrame = EnemyStrategy.ZERG_9DRONE.buildTimeMap.frame(UnitType.Zerg_Hatchery, 10);

        List<UnitInfo> found = found(RegionType.ENEMY_FIRST_EXPANSION);
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));

            if (buildFrame < doubleFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.DOUBLE_HATCH_12HAT);
            } else if (buildFrame < overPoolFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.DOUBLE_HATCH_OVERPOOL);
            } else if (buildFrame < nineDroneFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.DOUBLE_HATCH_9DRONE);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.DOUBLE_HATCH_LATE);
            }

        } else {
            int expansionLastCheckFrame = EnemyStrategyAnalyzer.Instance().lastCheckFrame(EnemyStrategyAnalyzer.LastCheckLocation.FIRST_EXPANSION);
            if (expansionLastCheckFrame > nineDroneFrame) { // 더블 타이밍 지남
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.DOUBLE_HATCH_LATE);
            }

            found = found(RegionType.ENEMY_BASE);
            if (found.size() >= 2) {
                int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
                if (buildFrame < nineDroneFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.TWIN_HATCH);
                }
            }
        }
    }

}
