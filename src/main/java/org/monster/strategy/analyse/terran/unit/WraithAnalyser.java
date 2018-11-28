package org.monster.strategy.analyse.terran.unit;

import bwapi.UnitType;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.decisionMakers.decisionTypes.EnemyStrategy;
import org.monster.common.UnitInfo;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.manage.ClueManager;

import java.util.List;

public class WraithAnalyser extends UnitAnalyser {

    public WraithAnalyser() {
        super(UnitType.Terran_Wraith);
    }

    @Override
    public void analyse() {
        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            if (found.size() >= 3) {
                int lastFoundFrame = lastUnitFoundFrame(found, 3);
                int wraithInMyRegionFrame = wraithInMyRegionFrame(3);
                if (lastFoundFrame < wraithInMyRegionFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_THREE_WRAITH);
                }
            }
            if (found.size() >= 2) {
                int lastFoundFrame = lastUnitFoundFrame(found, 2);
                int wraithInMyRegionFrame = wraithInMyRegionFrame(2);
                if (lastFoundFrame < wraithInMyRegionFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_TWO_WRAITH);
                }
            } else {
                int lastFoundFrame = lastUnitFoundFrame(found, 1);
                int wraithInMyRegionFrame = wraithInMyRegionFrame(1);
                if (lastFoundFrame < wraithInMyRegionFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_ONE_WRAITH);
                }
            }

            if (!ClueManager.Instance().containsClueType(Clue.ClueType.FAST_WRAITH)) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.WRAITH_FOUND);
            }
        }
    }

    private int wraithInMyRegionFrame(int enemyCount) {
        int starportCompleteFrame = EnemyStrategy.TERRAN_2STAR.buildTimeMap.frame(UnitType.Terran_Starport, 10) + UnitType.Terran_Starport.buildTime();
        int nWraithCompleteFrame = starportCompleteFrame + UnitType.Terran_Wraith.buildTime() * (enemyCount / 2 + 1);
        return nWraithCompleteFrame + baseToBaseFrame(UnitType.Terran_Wraith);
    }
}
