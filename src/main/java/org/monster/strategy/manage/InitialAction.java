package org.monster.strategy.manage;

import bwapi.Race;
import bwapi.UnitType;
import org.monster.common.util.PlayerUtils;
import org.monster.strategy.action.impl.GasAdjustmentMechanic;
import org.monster.strategy.action.impl.ScvScoutAfterBuild;

/**
 * 종족별 초기 전략을 불러온다.
 */
public class InitialAction {

    private static InitialAction instance = new InitialAction();
    private boolean terminated = false;
    private boolean assignedFirstScout = false;
    private InitialAction() {
    }

    public static InitialAction Instance() {
        return instance;
    }

    public void update() {
        setUpFirstScoutAndStrategy();
    }

    private void setUpFirstScoutAndStrategy() {
        if (terminated || PlayerUtils.enemyRace() == null) {
            return;
        }

        if (PlayerUtils.enemyRace() == Race.Protoss) {
            ActionManager.Instance().addAction(new GasAdjustmentMechanic());
            if (!assignedFirstScout) {
                ActionManager.Instance().addAction(new ScvScoutAfterBuild(UnitType.Terran_Supply_Depot, 0)); // 서플 완성후 출발
                assignedFirstScout = true;
            }
            StrategyAnalyseManager.Instance().setUp(Race.Protoss);
            terminated = true;

        } else if (PlayerUtils.enemyRace() == Race.Zerg) {
            ActionManager.Instance().addAction(new GasAdjustmentMechanic());
            if (!assignedFirstScout) {
                ActionManager.Instance().addAction(new ScvScoutAfterBuild(UnitType.Terran_Supply_Depot, UnitType.Terran_Supply_Depot.buildTime())); // 서플 시작후 출발
                assignedFirstScout = true;
            }
            StrategyAnalyseManager.Instance().setUp(Race.Zerg);
            terminated = true;

        } else if (PlayerUtils.enemyRace() == Race.Terran) {
            ActionManager.Instance().addAction(new GasAdjustmentMechanic());
            if (!assignedFirstScout) {
                ActionManager.Instance().addAction(new ScvScoutAfterBuild(UnitType.Terran_Supply_Depot, 0)); // 서플 완성후 출발
                assignedFirstScout = true;
            }
            StrategyAnalyseManager.Instance().setUp(Race.Terran);
            terminated = true;

        } else {
            if (!assignedFirstScout) {
                ActionManager.Instance().addAction(new ScvScoutAfterBuild(UnitType.Terran_Supply_Depot, 0)); // 서플 완성후 출발
                assignedFirstScout = true;
            }
        }
    }
}
