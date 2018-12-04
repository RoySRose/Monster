package org.monster.decisions.strategy.action.impl;

import bwapi.UnitType;
import org.monster.board.StrategyBoard;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.decisions.constant.EnemyStrategyOptions;
import org.monster.decisions.strategy.action.Action;
import org.monster.bootstrap.Monster;

/**
 * 메카닉 테란 가스 조절
 */
public class GasAdjustmentMechanic extends Action {

    private boolean gasAjustmentFinshed = false;

    @Override
    public boolean exitCondition() {
        if (gasAjustmentFinshed || TimeUtils.afterTime(5, 0)) {
            StrategyBoard.gasAdjustment = false;
            StrategyBoard.gasAdjustmentWorkerCount = 0;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void action() {
        StrategyBoard.gasAdjustment = true;

        int adjustGasWorkerCount = 0;
        int workerCount = UnitUtils.getUnitCount(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_SCV);
        if (workerCount > 8) {
            adjustGasWorkerCount = 3;
            if (!UnitUtils.myUnitDiscovered(UnitType.Terran_Factory)) {
                if (PlayerUtils.gasSelf() >= 92) {
                    adjustGasWorkerCount = 1;
                }
            } else {
                if (StrategyBoard.expansionOption == EnemyStrategyOptions.ExpansionOption.ONE_FACTORY) {
                    if (UnitUtils.getUnitCount(CommonCode.UnitFindStatus.ALL, UnitType.Terran_Command_Center) < 2) {
                        if (PlayerUtils.gasSelf() <= 250) {
                            adjustGasWorkerCount = 2;
                        } else {
                            adjustGasWorkerCount = 1;
                        }
                    } else {
                        gasAjustmentFinshed = true;
                    }
                } else {
                    if (UnitUtils.getUnitCount(CommonCode.UnitFindStatus.ALL, UnitType.Terran_Command_Center) < 2) {
                        if (PlayerUtils.gasSelf() <= 100) {
                            adjustGasWorkerCount = 3;
                        } else {
                            adjustGasWorkerCount = 2;
                        }
                    } else {
                        gasAjustmentFinshed = true;
                    }
                }
            }
        }
        StrategyBoard.gasAdjustmentWorkerCount = adjustGasWorkerCount;
    }
}
