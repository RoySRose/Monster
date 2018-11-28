package org.monster.strategy.analyse.terran.unit;

import bwapi.UnitType;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.manage.ClueManager;

import java.util.List;

public class MarineAnalyser extends UnitAnalyser {

    public MarineAnalyser() {
        super(UnitType.Terran_Marine);
    }

    @Override
    public void analyse() {
        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int minimumUpdateFrame = CommonCode.INT_MAX;
            for (UnitInfo eui : found) {
                if (eui.getUpdateFrame() < minimumUpdateFrame) {
                    minimumUpdateFrame = eui.getUpdateFrame();
                }
            }
            if (found.size() >= 6) {
                int lastFoundFrame = lastUnitFoundFrame(found, 6);
                int marineInMyRegionFrame = marineInMyRegionFrame(6);
                if (lastFoundFrame < marineInMyRegionFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_SIX_MARINE);
                }
            }
            if (found.size() >= 4) {
                int lastFoundFrame = lastUnitFoundFrame(found, 4);
                int marineInMyRegionFrame = marineInMyRegionFrame(4);
                if (lastFoundFrame < marineInMyRegionFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_FOUR_MARINE);
                }
            } else if (found.size() >= 2) {
                int lastFoundFrame = lastUnitFoundFrame(found, 2);
                int marineInMyRegionFrame = marineInMyRegionFrame(2);
                if (lastFoundFrame < marineInMyRegionFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_TWO_MARINE);
                }
            }
        }
    }

    private int marineInMyRegionFrame(int enemyCount) {
        int barracksFrame = EnemyStrategy.TERRAN_2BARRACKS.buildTimeMap.frame(UnitType.Terran_Barracks, 10) + UnitType.Terran_Factory.buildTime();
        int nMarineCompleteFrame = barracksFrame + UnitType.Terran_Marine.buildTime() * (enemyCount / 2 + 1);
        return nMarineCompleteFrame;
    }
}
