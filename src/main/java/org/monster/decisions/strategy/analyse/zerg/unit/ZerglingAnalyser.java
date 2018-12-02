package org.monster.decisions.strategy.analyse.zerg.unit;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.PositionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;
import org.monster.decisions.strategy.manage.EnemyBuildTimer;

import java.util.List;

public class ZerglingAnalyser extends UnitAnalyser {

    public ZerglingAnalyser() {
        super(UnitType.Zerg_Zergling);
    }

    @Override
    public void analyse() {
        List<UnitInfo> found = found();
        if (found.isEmpty()) {
            return;
        }
        fastPoolByZergling(found);
        fastZerglingCount(found);
    }

    private void fastPoolByZergling(List<UnitInfo> found) {
        if (ClueManager.Instance().containsClueType(Clue.ClueType.POOL_ASSUME)) {
            return;
        }
        int fiveDroneFrame = EnemyStrategy.ZERG_5DRONE.buildTimeMap.frame(UnitType.Zerg_Spawning_Pool, 5);
        int nineDroneFrame = EnemyStrategy.ZERG_9DRONE.buildTimeMap.frame(UnitType.Zerg_Spawning_Pool, 5);
        int overPoolFrame = EnemyStrategy.ZERG_OVERPOOL.buildTimeMap.frame(UnitType.Zerg_Spawning_Pool, 20); // 오버풀,11풀,12풀
        int buildToZerglingFrame = UnitType.Zerg_Spawning_Pool.buildTime() + UnitType.Zerg_Zergling.buildTime();

        CommonCode.RegionType foundRegionType = PositionUtils.positionToRegionType(found.get(0).getLastPosition());
        int movedFrame = 0;
        if (foundRegionType == CommonCode.RegionType.ENEMY_BASE) {
            movedFrame = 0;
        } else if (foundRegionType == CommonCode.RegionType.ENEMY_FIRST_EXPANSION) {
            movedFrame = 5 * TimeUtils.SECOND;
        } else if (foundRegionType == CommonCode.RegionType.MY_FIRST_EXPANSION
                || foundRegionType == CommonCode.RegionType.MY_BASE) {
            movedFrame = baseToBaseFrame(UnitType.Zerg_Zergling) + 5 * TimeUtils.SECOND;
        } else {
            movedFrame = 10 * TimeUtils.SECOND; // 발견위치로 더 상세하게 판단 가능
        }

        int foundFrame = found.get(0).getUpdateFrame();
        if (foundFrame < fiveDroneFrame + buildToZerglingFrame + movedFrame) {
            ClueManager.Instance().addClueInfo(Clue.ClueInfo.POOL_5DRONE);
        } else if (foundFrame < nineDroneFrame + buildToZerglingFrame + movedFrame) {
            ClueManager.Instance().addClueInfo(Clue.ClueInfo.POOL_9DRONE_UNDER);
        } else if (foundFrame < overPoolFrame + buildToZerglingFrame + movedFrame) {
            ClueManager.Instance().addClueInfo(Clue.ClueInfo.POOL_OVERPOOL_UNDER);
        }
    }

    private void fastZerglingCount(List<UnitInfo> found) {
        if (found.isEmpty()) {
            return;
        }

        int poolExpectFrame = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Zerg_Spawning_Pool);
        int fastZerglingFrame = poolExpectFrame + UnitType.Zerg_Spawning_Pool.buildTime() + UnitType.Zerg_Zergling.buildTime() + 5 * TimeUtils.SECOND;
        int secondFastZerglingFrame = fastZerglingFrame + UnitType.Zerg_Zergling.buildTime() + 5 * TimeUtils.SECOND;
        int thirdFastZerglingFrame = secondFastZerglingFrame + UnitType.Zerg_Overlord.buildTime() + UnitType.Zerg_Zergling.buildTime() + 5 * TimeUtils.SECOND;

        if (found.size() >= 10) {
            int lastUnitFoundFrame = lastUnitFoundFrame(found, 10);
            if (lastUnitFoundFrame < thirdFastZerglingFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_OVERTEN_ZERGLING);
            }
        } else if (found.size() >= 8) {
            int lastUnitFoundFrame = lastUnitFoundFrame(found, 8);
            if (lastUnitFoundFrame < secondFastZerglingFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_EIGHT_ZERGLING);
            }
        } else if (found.size() >= 6) {
            int lastUnitFoundFrame = lastUnitFoundFrame(found, 6);
            if (lastUnitFoundFrame < fastZerglingFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_SIX_ZERGLING);
            }
        }
    }
}
