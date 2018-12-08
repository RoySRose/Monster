package org.monster.decisions.strategy.analyse.protoss;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.RegionType;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;
import org.monster.decisions.strategy.manage.StrategyAnalyseManager;

import java.util.List;

public class AssimilatorAnalyser extends UnitAnalyser {

    public AssimilatorAnalyser() {
        super(UnitType.Protoss_Assimilator);
    }

    @Override
    public void analyse() {
        fastAssimilator();
    }

    private void fastAssimilator() {
        List<UnitInfo> found = found(RegionType.ENEMY_BASE);
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            int oneGateCoreGasFrame = EnemyStrategy.PROTOSS_1GATE_CORE.buildTimeMap.frame(UnitType.Protoss_Assimilator, 25);
            if (buildFrame < oneGateCoreGasFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.ASSIMILATOR_FAST);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.ASSIMILATOR_LATE);
            }
        } else {
            int gasLastCheckFrame = StrategyAnalyseManager.Instance().lastCheckFrame(StrategyAnalyseManager.LastCheckLocation.GAS);
            int oneGateCoreGasFrame = EnemyStrategy.PROTOSS_1GATE_CORE.buildTimeMap.frame(UnitType.Protoss_Assimilator, 25);
            if (gasLastCheckFrame > oneGateCoreGasFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.NO_ASSIMILATOR);
            }
        }
    }

}
