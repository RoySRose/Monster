package org.monster.decisions.strategy.action.impl;

import bwapi.UnitType;
import org.monster.board.StrategyBoard;
import org.monster.common.constant.UnitFindStatus;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.decisions.strategy.action.Action;

/**
 * 111 가스조절
 */
public class GasAdjustment8Barrack111 extends Action {

    @Override
    public boolean exitCondition() {
        if (TimeUtils.elapsedSeconds() > 300) {
            StrategyBoard.gasAdjustment = false;
            StrategyBoard.gasAdjustmentWorkerCount = 0;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void action() {
        if (TimeUtils.elapsedSeconds() > 180) {
            StrategyBoard.gasAdjustment = true;
            StrategyBoard.gasAdjustmentWorkerCount = 2;
        } else {
            StrategyBoard.gasAdjustment = true;
            int workerCount = UnitUtils.getUnitCount(UnitFindStatus.COMPLETE, UnitType.Terran_SCV);
            if (workerCount < 8) {
                StrategyBoard.gasAdjustmentWorkerCount = 0;
            } else {
                if (UnitUtils.getUnitCount(UnitFindStatus.ALL_AND_CONSTRUCTION_QUEUE, UnitType.Terran_Factory) > 0 || PlayerUtils.gasSelf() >= 100) {
                    StrategyBoard.gasAdjustmentWorkerCount = 1;
                } else {
                    StrategyBoard.gasAdjustmentWorkerCount = 2;
                }
            }
        }

    }
}
