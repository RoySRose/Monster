package org.monster.common.util;

import bwapi.Player;
import bwapi.Position;
import bwta.BaseLocation;
import org.monster.common.util.internal.IConditions;
import org.monster.common.util.internal.SpecificValueCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BaseUtils {

    private static final Logger logger = LoggerFactory.getLogger(BaseUtils.class);

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
        return BaseInfoCollector.Instance().equals(a,b);
    }

    public static List<BaseLocation> otherExpansions() {
        logger.error("need setting for this method");
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

    public static BaseLocation getClosestBaseFromPosition(List<BaseLocation> baseList, Position position) {
        return BaseInfoCollector.Instance().getClosestBaseFromPosition(baseList, position);
    }

    /**
     * baseList 중 position에 가장 가까운 base 리턴
     */
    public static BaseLocation getGroundClosestBaseFromPosition(List<BaseLocation> baseList, BaseLocation fromBase, IConditions.BaseCondition baseCondition) {
        return BaseInfoCollector.Instance().getGroundClosestBaseFromPosition(baseList, fromBase, baseCondition);
    }

    /**
     * baseList 중 position에 가장 먼 base 리턴
     */
    public static BaseLocation getGroundFarthestBaseFromPosition(List<BaseLocation> baseList, BaseLocation fromBase, IConditions.BaseCondition baseCondition) {
        return BaseInfoCollector.Instance().getGroundFarthestBaseFromPosition(baseList, fromBase, baseCondition);
    }
}
