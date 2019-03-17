package org.monster.common.util;

import bwta.Region;

import java.util.Set;

public class RegionUtils {

    public static Set<Region> myOccupiedRegions() {
        return RegionInfoCollector.Instance().occupiedRegions.get(PlayerUtils.myPlayer());
    }

    public static Set<Region> enemyOccupiedRegions() {
        return RegionInfoCollector.Instance().occupiedRegions.get(PlayerUtils.enemyPlayer());
    }

    public static Region myThirdRegion() {
        return RegionInfoCollector.Instance().thirdRegion.get(PlayerUtils.myPlayer());
    }

    public static Region enemyThirdRegion() {
        return RegionInfoCollector.Instance().thirdRegion.get(PlayerUtils.enemyPlayer());
    }

    public static boolean equals(Region region1, Region region2) {
        if (region1 == null || region2 == null) {
            return false;
        }
        return region1.getCenter().equals(region2.getCenter());
    }

}