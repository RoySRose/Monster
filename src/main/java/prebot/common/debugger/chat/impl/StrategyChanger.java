package prebot.common.debugger.chat.impl;

import bwapi.Race;
import prebot.common.debugger.chat.ChatExecuter;
import prebot.strategy.InformationManager;
import prebot.strategy.StrategyIdea;
import prebot.strategy.constant.EnemyStrategy;
import prebot.strategy.constant.EnemyStrategyOptions;

public class StrategyChanger extends ChatExecuter {

    public static boolean stopStrategiestForDebugging = false;

    public StrategyChanger(char type) {
        super(type);
    }

    @Override
    public void execute(String option) {
        Race enemyRace = InformationManager.Instance().enemyRace;
        String inputStrategyName = (enemyRace.toString() + "_" + option).replaceAll(" ", "_").toUpperCase();

        for (EnemyStrategy enemyStrategy : EnemyStrategy.values()) {
            if (enemyStrategy.name().toUpperCase().equals(inputStrategyName)) {
                StrategyIdea.currentStrategy = enemyStrategy;
                applyDetailValue(enemyStrategy);
                stopStrategiestForDebugging = true;
                return;
            }
        }
        stopStrategiestForDebugging = false;
    }

    // StrategyAnalyseManager에서 copy
    private void applyDetailValue(EnemyStrategy currentStrategy) {
        StrategyIdea.factoryRatio = currentStrategy.factoryRatio;
        StrategyIdea.upgrade = currentStrategy.upgrade;
        StrategyIdea.marineCount = currentStrategy.marineCount;

        // addOn option
        if (currentStrategy.addOnOption != null) {
            StrategyIdea.addOnOption = currentStrategy.addOnOption;
        }

        // air unit count
        if (currentStrategy.expansionOption != null) {
            StrategyIdea.expansionOption = currentStrategy.expansionOption;
            if (currentStrategy.expansionOption == EnemyStrategyOptions.ExpansionOption.TWO_STARPORT) {
                StrategyIdea.wraithCount = 4;
                StrategyIdea.valkyrieCount = 0;
            } else if (currentStrategy.expansionOption == EnemyStrategyOptions.ExpansionOption.ONE_STARPORT) {
                StrategyIdea.wraithCount = 0;
                StrategyIdea.valkyrieCount = 2;
            } else {
                StrategyIdea.wraithCount = 0;
                StrategyIdea.valkyrieCount = 0;
            }
        }

        if (currentStrategy.buildTimeMap != null) {
            StrategyIdea.buildTimeMap = currentStrategy.buildTimeMap;
        }
    }
};