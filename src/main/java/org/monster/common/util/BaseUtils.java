package org.monster.common.util;

import bwapi.Player;
import bwta.BaseLocation;
import org.monster.common.util.internal.SpecificValueCache;

import java.util.List;

public class BaseUtils {

    public static BaseLocation myMainBase() {
        return BaseInfoCollector.Instance().mainBaseLocation.get(PlayerUtils.myPlayer());
    }

    public static BaseLocation enemyMainBase() {
        return BaseInfoCollector.Instance().mainBaseLocation.get(PlayerUtils.enemyPlayer());
    }

    public static BaseLocation myFirstExpansion() {
        return BaseInfoCollector.Instance().firstExpansionLocation.get(PlayerUtils.myPlayer());
    }

    public static BaseLocation enemyFirstExpansion() {
        return BaseInfoCollector.Instance().firstExpansionLocation.get(PlayerUtils.enemyPlayer());
    }

    public static List<BaseLocation> myOccupiedBases() {
        return BaseInfoCollector.Instance().occupiedBaseLocations.get(PlayerUtils.myPlayer());
    }

    public static List<BaseLocation> enemyOccupiedBases() {
        return BaseInfoCollector.Instance().occupiedBaseLocations.get(PlayerUtils.enemyPlayer());
    }

    public static List<BaseLocation> getIslandBases() {
        return BaseInfoCollector.Instance().islandBaseLocations;
    }

    public static BaseLocation getClosestBase(BaseLocation sourceBase) {
        return BaseInfoCollector.Instance().findClosestBase(sourceBase);
    }

    public static boolean equals(BaseLocation a, BaseLocation b) {
        if (a.getTilePosition().equals(b.getTilePosition())) {
            return true;
        } else {
            return false;
        }
    }

    public static List<BaseLocation> otherExpansions() {
        //TODO disable
        return null;
//        return InformationManager.Instance().getOtherExpansionLocations();
    }

    public static boolean hasBuildingAroundBaseLocation(BaseLocation baseLocation, Player player) {
        return BaseInfoCollector.Instance().hasBuildingAroundBaseLocation(baseLocation, player, 10);
    }

    public static boolean hasBuildingAroundBaseLocation(BaseLocation baseLocation, Player player, int radius) {
        return BaseInfoCollector.Instance().hasBuildingAroundBaseLocation(baseLocation, player, radius);
    }

    public static boolean isEnemyFirstExpansionOccupied() {
        Boolean enemyFirstExpansionOccupied = SpecificValueCache.get(SpecificValueCache.ValueType.ENEMY_FIRST_EXPANSION_OCCUPIED, Boolean.class);
        if (enemyFirstExpansionOccupied != null) {
            return enemyFirstExpansionOccupied;
        }
        enemyFirstExpansionOccupied = Boolean.FALSE;
        BaseLocation enemyFirstExpansion = enemyFirstExpansion();
        if (enemyFirstExpansion != null) {
            for (BaseLocation base : BaseUtils.enemyOccupiedBases()) {
                if (base.equals(enemyFirstExpansion)) {
                    enemyFirstExpansionOccupied = Boolean.TRUE;
                    break;
                }
            }
        }
        SpecificValueCache.put(SpecificValueCache.ValueType.ENEMY_FIRST_EXPANSION_OCCUPIED, enemyFirstExpansionOccupied);
        return enemyFirstExpansionOccupied;
    }
}
