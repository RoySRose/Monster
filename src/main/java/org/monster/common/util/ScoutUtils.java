package org.monster.common.util;

import bwapi.Position;
import bwta.BaseLocation;

import java.util.Vector;

public class ScoutUtils {

    public static Vector<Position> getRegionVertices(BaseLocation base) {
        return ScoutInfoCollector.Instance().baseRegionVerticesMap.get(base.getPosition());
    }

    public static void calculateEnemyRegionVertices(BaseLocation base) {
        ScoutInfoCollector.Instance().calculateEnemyRegionVertices(base);
    }

}
