package org.monster.strategy.analyse.protoss;

import bwapi.UnitType;
import org.monster.decisionMakers.decisionTypes.EnemyStrategy;
import org.monster.common.UnitInfo;

import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;

import java.util.List;

public class RoboticsSupportAnalyser extends UnitAnalyser {

    public RoboticsSupportAnalyser() {
        super(UnitType.Protoss_Robotics_Support_Bay);
    }

    @Override
    public void analyse() {
        fastRoboSupport();
    }

    private void fastRoboSupport() {
        if (ClueManager.Instance().containsClueType(Clue.ClueType.FAST_ROBO_SUPPORT)) {
            return;
        }

        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            int fastRoboSupportFrame = EnemyStrategy.PROTOSS_ROBOTICS_REAVER.buildTimeMap.frame(UnitType.Protoss_Robotics_Support_Bay, 30);

            if (buildFrame < fastRoboSupportFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.ROBO_SUPPORT_FAST);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.ROBO_SUPPORT_FOUND);
            }
        }
    }

}
