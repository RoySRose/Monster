package org.monster.common.util;

import bwta.Region;

import java.util.Set;

public class RegionUtils {

    public static Set<Region> myOccupiedRegions() {
        return BaseInfoCollector.Instance().occupiedRegions.get(PlayerUtils.myRace());
    }

    public static Set<Region> enemyOccupiedRegions() {
        return BaseInfoCollector.Instance().occupiedRegions.get(PlayerUtils.enemyRace());
    }

}