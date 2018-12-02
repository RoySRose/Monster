package org.monster.common.util;

import bwta.BaseLocation;
import org.monster.common.util.internal.GameMap;

import java.util.List;

public class StaticMapUtils {

    public static GameMap getMap() {
        return StaticMapInfoCollector.Instance().map;
    }

    public static List<BaseLocation> getStartingBaseLocation() {
        return StaticMapInfoCollector.Instance().startingBaseLocation;
    }
}
