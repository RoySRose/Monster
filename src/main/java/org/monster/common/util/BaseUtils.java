package org.monster.common.util;

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

    public static List<BaseLocation> getIslandBaseLocations() {
        return BaseInfoCollector.Instance().islandBaseLocations;
    }


    public static List<BaseLocation> enemyOtherExpansions() {
        //TODO disable
        return null;
//        return InformationManager.Instance().getOtherExpansionLocations();
    }

    public static boolean enemyFirstExpansionOccupied() {
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
