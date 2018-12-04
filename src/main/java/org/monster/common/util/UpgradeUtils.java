package org.monster.common.util;

import bwapi.TechType;
import bwapi.Unit;
import bwapi.UpgradeType;

public class UpgradeUtils {

    public static boolean selfISResearched(TechType techType) {
        return UpgradeInfoCollector.Instance().selfIsResearched(techType);
    }

    public static boolean selfIsUpgraded(UpgradeType upgradeType) {
        return UpgradeInfoCollector.Instance().selfUpgradedLevel(upgradeType) > 0;
    }

    public static int selfUpgradedLevel(UpgradeType upgradeType) {
        return UpgradeInfoCollector.Instance().selfUpgradedLevel(upgradeType);
    }

    public static boolean enemyISResearched(TechType techType) {
        return UpgradeInfoCollector.Instance().enemyIsResearched(techType);
    }

    public static boolean enemyIsUpgraded(UpgradeType upgradeType) {
        return UpgradeInfoCollector.Instance().selfUpgradedLevel(upgradeType) > 0;
    }

    public static int enemyUpgradedLevel(UpgradeType upgradeType) {
        return UpgradeInfoCollector.Instance().enemyUpgradedLevel(upgradeType);
    }

    public static boolean selfISResearching(TechType techType) {
        return UpgradeInfoCollector.Instance().selfISResearching(techType);
    }

    public static boolean selfIsUpgrading(UpgradeType upgradeType) {
        return UpgradeInfoCollector.Instance().selfIsUpgrading(upgradeType);
    }

    public static boolean canResearch(TechType techType, Unit unit) {
        return UpgradeInfoCollector.Instance().canResearch(techType, unit);
    }

    public static boolean canUpgrade(UpgradeType upgradeType, Unit unit) {
        return UpgradeInfoCollector.Instance().canUpgrade(upgradeType, unit);
    }

    public static int getSelfMaxUpgradeLevel(UpgradeType upgradeType) {
        return UpgradeInfoCollector.Instance().getSelfMaxUpgradeLevel(upgradeType);
    }
    public static int getEnemyMaxUpgradeLevel(UpgradeType upgradeType) {
        return UpgradeInfoCollector.Instance().getEnemyMaxUpgradeLevel(upgradeType);
    }
}
