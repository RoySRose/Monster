package org.monster.common.util;

import bwapi.Game;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UpgradeType;

public class UpgradeInfoCollector implements InfoCollector {

    private static UpgradeInfoCollector instance = new UpgradeInfoCollector();
    private Game Broodwar;

    protected static UpgradeInfoCollector Instance() {
        return instance;
    }

    protected boolean selfIsResearched(TechType techType) {
        return Broodwar.self().hasResearched(techType);
    }

    protected int selfUpgradedLevel(UpgradeType upgradeType) {
        return Broodwar.self().getUpgradeLevel(upgradeType);
    }

    protected boolean enemyIsResearched(TechType techType) {
        return Broodwar.enemy().hasResearched(techType);
    }

    protected int enemyUpgradedLevel(UpgradeType upgradeType) {
        return Broodwar.enemy().getUpgradeLevel(upgradeType);
    }

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
    }

    @Override
    public void update() {
    }

    protected boolean selfISResearching(TechType techType) {
        return Broodwar.self().isResearching(techType);
    }

    protected boolean selfIsUpgrading(UpgradeType upgradeType) {
        return Broodwar.self().isUpgrading(upgradeType);
    }

    protected boolean canResearch(TechType techType, Unit unit) {
        return Broodwar.canResearch(techType, unit);
    }

    protected boolean canUpgrade(UpgradeType upgradeType, Unit unit) {
        return Broodwar.canUpgrade(upgradeType, unit);
    }

    protected int getSelfMaxUpgradeLevel(UpgradeType upgradeType) {
        return Broodwar.self().getMaxUpgradeLevel(upgradeType);
    }
    protected int getEnemyMaxUpgradeLevel(UpgradeType upgradeType) {
        return Broodwar.enemy().getMaxUpgradeLevel(upgradeType);
    }
}
