package org.monster.decisions.strategy.analyse.protoss.unit;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;

import java.util.List;

public class DarkTemplarAnalyser extends UnitAnalyser {

    public DarkTemplarAnalyser() {
        super(UnitType.Protoss_Dark_Templar);
    }

    @Override
    public void analyse() {
        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int minimumUpdateFrame = found.get(0).getUpdateFrame();
            int fastDarkTemplarFrame = EnemyStrategy.PROTOSS_FAST_DARK.buildTimeMap.frame(UnitType.Protoss_Templar_Archives, 30)
                    + UnitType.Protoss_Templar_Archives.buildTime() + UnitType.Protoss_Dark_Templar.buildTime();
            int fastDarkTemplarFrameInMyBaseFrame = fastDarkTemplarFrame + baseToBaseFrame(UnitType.Protoss_Zealot);
            if (minimumUpdateFrame < fastDarkTemplarFrameInMyBaseFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_DARK);
            }
        }
    }

}
