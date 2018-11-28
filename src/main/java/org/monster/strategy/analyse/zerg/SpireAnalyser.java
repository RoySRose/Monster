package org.monster.strategy.analyse.zerg;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.util.TimeUtils;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;
import org.monster.strategy.manage.EnemyBuildTimer;

import java.util.List;

public class SpireAnalyser extends UnitAnalyser {

    public SpireAnalyser() {
        super(UnitType.Zerg_Spire);
    }

    @Override
    public void analyse() {
        if (ClueManager.Instance().containsClueType(Clue.ClueType.SPIRE)) {
            return;
        }

        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int lairBuildFrame = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Zerg_Lair);
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            if (buildFrame < lairBuildFrame + UnitType.Zerg_Lair.buildTime() + 25 * TimeUtils.SECOND) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_SPIRE);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.SPIRE);
            }
        }
    }

}
