package org.monster.strategy.analyse.terran;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.RegionType;
import org.monster.strategy.constant.EnemyStrategy;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;
import org.monster.strategy.manage.EnemyStrategyAnalyzer;

import java.util.List;

public class RefineryAnalyser extends UnitAnalyser {

    public RefineryAnalyser() {
        super(UnitType.Terran_Refinery);
    }

    @Override
    public void analyse() {
        fastRefinery();
    }

    private void fastRefinery() {
        List<UnitInfo> found = found(RegionType.ENEMY_BASE);
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            int mechanicGasFrame = EnemyStrategy.TERRAN_MECHANIC.buildTimeMap.frame(UnitType.Terran_Refinery, 25);
            if (buildFrame < mechanicGasFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.REFINERY_FAST);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.REFINERY_LATE);
            }
        } else {
            int gasLastCheckFrame = EnemyStrategyAnalyzer.Instance().lastCheckFrame(EnemyStrategyAnalyzer.LastCheckLocation.GAS);
            int mechanicGasFrame = EnemyStrategy.TERRAN_MECHANIC.buildTimeMap.frame(UnitType.Terran_Refinery, 25);
            if (gasLastCheckFrame > mechanicGasFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.NO_REFINERY);
            }
        }
    }
}
