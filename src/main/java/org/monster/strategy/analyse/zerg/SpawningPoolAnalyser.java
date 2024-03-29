package org.monster.strategy.analyse.zerg;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.RegionType;
import org.monster.strategy.constant.EnemyStrategy;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;
import org.monster.strategy.manage.EnemyStrategyAnalyzer;

import java.util.List;

public class SpawningPoolAnalyser extends UnitAnalyser {

    public SpawningPoolAnalyser() {
        super(UnitType.Zerg_Spawning_Pool);
    }

    @Override
    public void analyse() {
        fastPool();
    }

    private void fastPool() {
        if (ClueManager.Instance().containsClueType(Clue.ClueType.POOL)) {
            return;
        }

        int fiveDroneFrame = EnemyStrategy.ZERG_5DRONE.buildTimeMap.frame(UnitType.Zerg_Spawning_Pool, 5);
        int nineDroneFrame = EnemyStrategy.ZERG_9DRONE.buildTimeMap.frame(UnitType.Zerg_Spawning_Pool, 5);
        int overPoolFrame = EnemyStrategy.ZERG_OVERPOOL.buildTimeMap.frame(UnitType.Zerg_Spawning_Pool, 20); // 오버풀,11풀,12풀
        int doubleFrame = EnemyStrategy.ZERG_2HAT_GAS.buildTimeMap.frame(UnitType.Zerg_Spawning_Pool, 10);

        List<UnitInfo> found = found(RegionType.ENEMY_BASE);
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            if (buildFrame < fiveDroneFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.POOL_5DRONE);
            } else if (buildFrame < nineDroneFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.POOL_9DRONE);
            } else if (buildFrame < overPoolFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.POOL_OVERPOOL);
            } else if (buildFrame < doubleFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.POOL_2HAT);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.POOL_2HAT); // 아주 늦은 스포닝풀
            }

        } else {
            int baseLastCheckFrame = EnemyStrategyAnalyzer.Instance().lastCheckFrame(EnemyStrategyAnalyzer.LastCheckLocation.BASE);
            if (baseLastCheckFrame > doubleFrame) { // 더블 타이밍 지남
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.LATE_POOL);
            }
        }
    }

}
