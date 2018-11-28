package org.monster.strategy.analyse.protoss;

import bwapi.UnitType;
import org.monster.decisionMakers.decisionTypes.EnemyStrategy;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;

import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;
import org.monster.strategy.manage.EnemyBuildTimer;

import java.util.List;

public class CoreAnalyser extends UnitAnalyser {

    public CoreAnalyser() {
        super(UnitType.Protoss_Cybernetics_Core);
    }

    @Override
    public void analyse() {
        fastCore();
        fastDragoonRange();
    }

    // 빠른 코어 여부
    private void fastCore() {
        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            int normalCoreFrame = EnemyStrategy.PROTOSS_1GATE_CORE.buildTimeMap.frame(UnitType.Protoss_Cybernetics_Core, 20);

            if (buildFrame < normalCoreFrame) { // 생더블 확정
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.CORE_FAST);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.CORE_FOUND);
            }
        }
    }

    // 빠른 드라군 사업 여부
    private void fastDragoonRange() {
        int coreBuildStartFrameExpected = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Protoss_Cybernetics_Core);
        int fastDragoonRangeFrame = coreBuildStartFrameExpected + UnitType.Protoss_Cybernetics_Core.buildTime() + 25 * TimeUtils.SECOND;
        if (TimeUtils.after(fastDragoonRangeFrame)) {
            return;
        }

        List<UnitInfo> coreList = UnitUtils.getEnemyUnitInfoList(CommonCode.EnemyUnitFindRange.VISIBLE, UnitType.Protoss_Cybernetics_Core);
        if (!coreList.isEmpty() && coreList.get(0).getUnit().isUpgrading()) {
            ClueManager.Instance().addClueInfo(Clue.ClueInfo.DRAGOON_RANGE_FAST);
        }
    }

}
