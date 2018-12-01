package org.monster.decisions.strategy.analyse.protoss;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;

import java.util.List;

public class TemplarArchAnalyser extends UnitAnalyser {

    public TemplarArchAnalyser() {
        super(UnitType.Protoss_Templar_Archives);
    }

    @Override
    public void analyse() {
        fastTemplarArch();
    }

    private void fastTemplarArch() {
        if (ClueManager.Instance().containsClueType(Clue.ClueType.FAST_TEMPLAR_ARCH)) {
            return;
        }

        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            int fastTemplarArchFrame = EnemyStrategy.PROTOSS_FAST_DARK.buildTimeMap.frame(UnitType.Protoss_Templar_Archives, 30);

            if (buildFrame < fastTemplarArchFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.TEMPLAR_ARCH_FAST);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.TEMPLAR_ARCH_FOUND);
            }
        }
    }

}
