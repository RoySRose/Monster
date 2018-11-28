package org.monster.strategy.analyse.protoss;

import bwapi.UnitType;
import org.monster.decisionMakers.decisionTypes.EnemyStrategy;
import org.monster.common.UnitInfo;

import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;

import java.util.List;

public class AdunAnalyser extends UnitAnalyser {

    public AdunAnalyser() {
        super(UnitType.Protoss_Citadel_of_Adun);
    }

    @Override
    public void analyse() {
        fastAdun();
    }

    private void fastAdun() {
        if (ClueManager.Instance().containsClueType(Clue.ClueType.FAST_ADUN)) {
            return;
        }

        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            int fastAdunFrame = EnemyStrategy.PROTOSS_FAST_DARK.buildTimeMap.frame(UnitType.Protoss_Citadel_of_Adun, 30);

            if (buildFrame < fastAdunFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.ADUN_FAST);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.ADUN_FOUND);
            }
        }
    }

}
