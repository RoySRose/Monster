package org.monster.common.util;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.util.internal.GameMap;

import java.util.List;

public class MapUtils {

    public static GameMap getMap() {
        return MapInfoCollector.Instance().map;
    }

    public static List<Unit> getGeysers() {
        return MapInfoCollector.Instance().getGeyser();
    }

    public static List<Unit> getStaticGeysers() {
        return MapInfoCollector.Instance().getStaticGeysers();
    }

    public static List<Unit> getMinerals() {
        return MapInfoCollector.Instance().getMinerals();
    }

    public static List<Unit> getStaticMinerals() {
        return MapInfoCollector.Instance().getStaticMinerals();
    }

    public static boolean isExplored(TilePosition tilePosition) {
        return MapInfoCollector.Instance().isExplored(tilePosition);
    }

    public static boolean isExplored(int x, int y) {
        return MapInfoCollector.Instance().isExplored(x, y);
    }

    public static boolean hasPath(Position from, Position to) {
        return MapInfoCollector.Instance().hasPath(from, to);
    }

    public static boolean isWalkable(int i, int j) {
        return MapInfoCollector.Instance().isWalkable(i, j);
    }

    public static boolean isBuildable(TilePosition tilePosition, boolean includeBuilding) {
        return MapInfoCollector.Instance().isBuildable(tilePosition, includeBuilding);
    }

    public static boolean isBuildable(TilePosition tilePosition) {
        return MapInfoCollector.Instance().isBuildable(tilePosition);
    }

    public static boolean canBuildHere(TilePosition tilePosition, UnitType unitType) {
        return MapInfoCollector.Instance().canBuildHere(tilePosition, unitType);
    }

    public static int mapHeight() {
        return MapInfoCollector.Instance().mapHeight();
    }

    public static int mapWidth() {
        return MapInfoCollector.Instance().mapWidth();
    }

    public static boolean isVisible(TilePosition tilePosition) {
        return MapInfoCollector.Instance().isVisible(tilePosition);
    }
}
