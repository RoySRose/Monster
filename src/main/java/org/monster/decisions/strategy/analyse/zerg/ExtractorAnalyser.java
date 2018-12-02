package org.monster.decisions.strategy.analyse.zerg;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;
import org.monster.decisions.strategy.manage.StrategyAnalyseManager;

import java.util.List;

public class ExtractorAnalyser extends UnitAnalyser {

    public ExtractorAnalyser() {
        super(UnitType.Zerg_Extractor);
    }

    @Override
    public void analyse() {
        int nineDroneFrame = EnemyStrategy.ZERG_9DRONE_GAS.buildTimeMap.frame(UnitType.Zerg_Extractor, 10);
        int overPoolFrame = EnemyStrategy.ZERG_OVERPOOL_GAS.buildTimeMap.frame(UnitType.Zerg_Extractor, 20);
        int doubleHatchFrame = EnemyStrategy.ZERG_2HAT_GAS.buildTimeMap.frame(UnitType.Zerg_Extractor, 20);

        List<UnitInfo> found = found(CommonCode.RegionType.ENEMY_BASE);
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            if (buildFrame < nineDroneFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.EXTRACTOR_9DRONE);
            } else if (buildFrame < overPoolFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.EXTRACTOR_OVERPOOL);
            } else if (buildFrame < doubleHatchFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.EXTRACTOR_2HAT);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.EXTRACTOR_LATE);
            }
        } else {
            int gasLastCheckFrame = StrategyAnalyseManager.Instance().lastCheckFrame(StrategyAnalyseManager.LastCheckLocation.GAS);
            if (gasLastCheckFrame > doubleHatchFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.NO_EXTRACTOR);
            }
        }
    }

}
