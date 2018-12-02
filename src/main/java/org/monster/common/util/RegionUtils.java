package org.monster.common.util;

import bwta.Region;

import java.util.Set;

public class RegionUtils {

    public static Set<Region> myOccupiedRegions() {
        return RegionInfoCollector.Instance().occupiedRegions.get(PlayerUtils.myRace());
    }

    public static Set<Region> enemyOccupiedRegions() {
        return RegionInfoCollector.Instance().occupiedRegions.get(PlayerUtils.enemyRace());
    }

    public static Region myThirdRegion() {
        return RegionInfoCollector.Instance().thirdRegion.get(PlayerUtils.myPlayer());
    }

    public static Region enemyThirdRegion() {
        return RegionInfoCollector.Instance().thirdRegion.get(PlayerUtils.enemyPlayer());
    }
}