package org.monster.common.util;

import bwapi.Game;
import bwapi.TechType;
import bwapi.UpgradeType;
import org.monster.main.Monster;

public class UpgradeInfoCollector implements InfoCollector {

    private static UpgradeInfoCollector instance = new UpgradeInfoCollector();

    protected static UpgradeInfoCollector Instance() {
        return instance;
    }

    private Game Broodwar;

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
    }

    @Override
    public void update() {
    }

    protected static boolean selfIsResearched(TechType techType) {
        return Monster.Broodwar.self().hasResearched(techType);
    }

    protected static int selfUpgradedLevel(UpgradeType upgradeType) {
        return Monster.Broodwar.self().getUpgradeLevel(upgradeType);
    }

    protected static boolean enemyIsResearched(TechType techType) {
        return Monster.Broodwar.enemy().hasResearched(techType);
    }

    protected static int enemyUpgradedLevel(UpgradeType upgradeType) {
        return Monster.Broodwar.enemy().getUpgradeLevel(upgradeType);
    }

}
