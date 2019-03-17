package org.monster.strategy.analyse.protoss;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.strategy.constant.EnemyStrategy;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;

import java.util.List;

public class RoboticsAnalyser extends UnitAnalyser {

    public RoboticsAnalyser() {
        super(UnitType.Protoss_Robotics_Facility);
    }

    @Override
    public void analyse() {
        fastRobo();
    }

    private void fastRobo() {
        if (ClueManager.Instance().containsClueType(Clue.ClueType.FAST_ROBO)) {
            return;
        }

        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            int fastRoboFrame = EnemyStrategy.PROTOSS_ROBOTICS_REAVER.buildTimeMap.frame(UnitType.Protoss_Robotics_Facility, 30);

            if (buildFrame < fastRoboFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.ROBO_FAST);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.ROBO_FOUND);
            }
        }
    }

}
