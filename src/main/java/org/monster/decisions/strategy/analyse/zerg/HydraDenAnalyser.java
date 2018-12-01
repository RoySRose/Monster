package org.monster.decisions.strategy.analyse.zerg;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;
import org.monster.decisions.strategy.manage.EnemyBuildTimer;

import java.util.List;

public class HydraDenAnalyser extends UnitAnalyser {

    public HydraDenAnalyser() {
        super(UnitType.Zerg_Hydralisk_Den);
    }

    @Override
    public void analyse() {
        if (ClueManager.Instance().containsClueType(Clue.ClueType.HYDRADEN)) {
            return;
        }

        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int lairBuildFrame = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Zerg_Lair);
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            if (buildFrame < lairBuildFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.HYDRADEN_BEFORE_LAIR_START);
            } else if (buildFrame < lairBuildFrame + UnitType.Zerg_Lair.buildTime()) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.HYDRADEN_BEFORE_LAIR_COMPLETE);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.HYDRADEN);
            }
        }
    }

}
