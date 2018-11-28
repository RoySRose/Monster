package org.monster.debugger.chat.impl;

import bwapi.Race;
import org.monster.board.StrategyBoard;
import org.monster.debugger.chat.ChatExecuter;
import org.monster.common.util.PlayerUtils;
import org.monster.decisions.constant.EnemyStrategyOptions;
import org.monster.decisions.constant.EnemyStrategy;

public class StrategyChanger extends ChatExecuter {

    public static boolean stopStrategiestForDebugging = false;

    public StrategyChanger(char type) {
        super(type);
    }

    @Override
    public void execute(String option) {
        Race enemyRace = PlayerUtils.enemyRace();
        String inputStrategyName = (enemyRace.toString() + "_" + option).replaceAll(" ", "_").toUpperCase();

        for (EnemyStrategy enemyStrategy : EnemyStrategy.values()) {
            if (enemyStrategy.name().toUpperCase().equals(inputStrategyName)) {
                StrategyBoard.currentStrategy = enemyStrategy;
                applyDetailValue(enemyStrategy);
                stopStrategiestForDebugging = true;
                return;
            }
        }
        stopStrategiestForDebugging = false;
    }

    // StrategyAnalyseManager에서 copy
    private void applyDetailValue(EnemyStrategy currentStrategy) {
        StrategyBoard.factoryRatio = currentStrategy.factoryRatio;
        StrategyBoard.upgrade = currentStrategy.upgrade;
        StrategyBoard.marineCount = currentStrategy.marineCount;

        // addOn option
        if (currentStrategy.addOnOption != null) {
            StrategyBoard.addOnOption = currentStrategy.addOnOption;
        }

        // air unit count
        if (currentStrategy.expansionOption != null) {
            StrategyBoard.expansionOption = currentStrategy.expansionOption;
            if (currentStrategy.expansionOption == EnemyStrategyOptions.ExpansionOption.TWO_STARPORT) {
                StrategyBoard.wraithCount = 4;
                StrategyBoard.valkyrieCount = 0;
            } else if (currentStrategy.expansionOption == EnemyStrategyOptions.ExpansionOption.ONE_STARPORT) {
                StrategyBoard.wraithCount = 0;
                StrategyBoard.valkyrieCount = 2;
            } else {
                StrategyBoard.wraithCount = 0;
                StrategyBoard.valkyrieCount = 0;
            }
        }

        if (currentStrategy.buildTimeMap != null) {
            StrategyBoard.buildTimeMap = currentStrategy.buildTimeMap;
        }
    }
};