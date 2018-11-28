package org.monster.common.util;

import bwapi.TechType;
import bwapi.UpgradeType;

public class UpgradeUtils {

    public static boolean selfISResearched(TechType techType) {
        return UpgradeInfoCollector.selfIsResearched(techType);
    }

    public static boolean selfIsUpgraded(UpgradeType upgradeType) {
        return UpgradeInfoCollector.selfUpgradedLevel(upgradeType) > 0;
    }

    public static int selfUpgradedLevel(UpgradeType upgradeType) {
        return UpgradeInfoCollector.selfUpgradedLevel(upgradeType);
    }

    public static boolean enemyISResearched(TechType techType) {
        return UpgradeInfoCollector.enemyIsResearched(techType);
    }

    public static boolean enemyIsUpgraded(UpgradeType upgradeType) {
        return UpgradeInfoCollector.selfUpgradedLevel(upgradeType) > 0;
    }

    public static int enemyUpgradedLevel(UpgradeType upgradeType) {
        return UpgradeInfoCollector.enemyUpgradedLevel(upgradeType);
    }

}
