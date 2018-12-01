package org.monster.decisions.strategy.analyse.zerg.unit;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.TimeUtils;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;
import org.monster.decisions.strategy.manage.EnemyBuildTimer;

import java.util.List;

public class HydraliskAnalyser extends UnitAnalyser {

    public HydraliskAnalyser() {
        super(UnitType.Zerg_Hydralisk);
    }

    @Override
    public void analyse() {
        List<UnitInfo> found = found();
        if (found.isEmpty()) {
            return;
        }

        if (!found.isEmpty()) {
            int hydradenExpect = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Zerg_Hydralisk_Den);
            if (hydradenExpect == CommonCode.UNKNOWN) {
                return;
            }

            if (found.size() >= 5) {
                int lastUnitFoundFrame = lastUnitFoundFrame(found, 3);
                int fiveHydraliskInMyRegionFrame = hydradenExpect + UnitType.Zerg_Hydralisk_Den.buildTime()
                        + UnitType.Zerg_Hydralisk.buildTime() * 5 + baseToBaseFrame(UnitType.Zerg_Hydralisk) + 30 * TimeUtils.SECOND;
                if (lastUnitFoundFrame < fiveHydraliskInMyRegionFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FIVE_MANY_HYDRA);
                }

            } else if (found.size() >= 3) {
                int lastUnitFoundFrame = lastUnitFoundFrame(found, 3);
                int threeHydraliskInMyRegionFrame = hydradenExpect + UnitType.Zerg_Hydralisk_Den.buildTime()
                        + UnitType.Zerg_Hydralisk.buildTime() + baseToBaseFrame(UnitType.Zerg_Hydralisk) + 30 * TimeUtils.SECOND;
                if (lastUnitFoundFrame < threeHydraliskInMyRegionFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.THREE_MANY_HYDRA);
                }
            } else {
                int lastUnitFoundFrame = lastUnitFoundFrame(found, found.size());
                int hydraliskInMyRegionFrame = hydradenExpect + UnitType.Zerg_Hydralisk_Den.buildTime()
                        + UnitType.Zerg_Hydralisk.buildTime() + baseToBaseFrame(UnitType.Zerg_Hydralisk) + 15 * TimeUtils.SECOND;
                if (lastUnitFoundFrame < hydraliskInMyRegionFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_HYDRA);
                }
            }
        }
    }
}
