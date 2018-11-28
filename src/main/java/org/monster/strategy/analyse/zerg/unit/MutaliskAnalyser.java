package org.monster.strategy.analyse.zerg.unit;

import bwapi.UnitType;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.TimeUtils;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.manage.ClueManager;
import org.monster.strategy.manage.EnemyBuildTimer;

import java.util.List;

public class MutaliskAnalyser extends UnitAnalyser {

    public MutaliskAnalyser() {
        super(UnitType.Zerg_Mutalisk);
    }

    @Override
    public void analyse() {
        List<UnitInfo> found = found();
        if (found.isEmpty()) {
            return;
        }

        if (!found.isEmpty()) {
            int minimumUpdateFrame = found.get(0).getUpdateFrame();
            int spireBuildExpect = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Zerg_Spire);
            if (spireBuildExpect == CommonCode.UNKNOWN) {
                return;
            }

            int mutalInMyRegionFrame = spireBuildExpect + UnitType.Zerg_Spire.buildTime()
                    + UnitType.Zerg_Mutalisk.buildTime() + baseToBaseFrame(UnitType.Zerg_Mutalisk) + 15 * TimeUtils.SECOND;
            if (minimumUpdateFrame < mutalInMyRegionFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_MUTAL);
            }
        }
    }
}
