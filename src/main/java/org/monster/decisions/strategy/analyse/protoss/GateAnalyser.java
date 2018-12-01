package org.monster.decisions.strategy.analyse.protoss;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;
import org.monster.decisions.strategy.manage.StrategyAnalyseManager;

import java.util.List;

public class GateAnalyser extends UnitAnalyser {

    public GateAnalyser() {
        super(UnitType.Protoss_Gateway);
    }

    @Override
    public void analyse() {
        fastGateway();
    }

    private void fastGateway() {
        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            if (found.size() >= 2) { // 게이트 웨이 2개 이상
                int firstBuildFrame = buildStartFrameDefaultJustBefore(found.get(0));
                int secondBuildFrame = buildStartFrameDefaultJustBefore(found.get(1));
                int twoGateSecondGateFrame = EnemyStrategy.PROTOSS_2GATE.buildTimeMap.frameOfIndex(UnitType.Protoss_Gateway, 1, 20);

                if (firstBuildFrame < twoGateSecondGateFrame && secondBuildFrame < twoGateSecondGateFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.GATE_FAST_TWO);
                } else {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.GATE_TWO);
                }

            } else if (found.size() == 1) { // 게이트 웨이 1개
                int firstBuildFrame = buildStartFrameDefaultJustBefore(found.get(0));
                int twoGateFirstGateFrame = EnemyStrategy.PROTOSS_2GATE.buildTimeMap.frame(UnitType.Protoss_Gateway, 15);
                if (firstBuildFrame < twoGateFirstGateFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.GATE_FAST_ONE);
                } else {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.GATE_ONE);
                }
            }
        } else {
            int twoGateFirstGateFrame = EnemyStrategy.PROTOSS_2GATE.buildTimeMap.frame(UnitType.Protoss_Gateway, 20);
            int baseLastCheckFrame = StrategyAnalyseManager.Instance().lastCheckFrame(StrategyAnalyseManager.LastCheckLocation.BASE);
            if (baseLastCheckFrame > twoGateFirstGateFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.GATE_NOT_FOUND);
            }
        }
    }

}
