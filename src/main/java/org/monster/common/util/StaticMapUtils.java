package org.monster.common.util;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
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

    public static List<Unit> getGeysers(){
        return StaticMapInfoCollector.Instance().getGeyser();
    }
    public static List<Unit> getStaticGeysers(){
        return StaticMapInfoCollector.Instance().getStaticGeysers();
    }

    public static List<Unit> getMinerals(){
        return StaticMapInfoCollector.Instance().getMinerals();
    }

    public static boolean isExplored(TilePosition tilePosition){
        return StaticMapInfoCollector.Instance().isExplored(tilePosition);
    }
    public static boolean isExplored(int x, int y){
        return StaticMapInfoCollector.Instance().isExplored(x,y);
    }
    public static boolean hasPath(Position from, Position to){
        return StaticMapInfoCollector.Instance().hasPath(from, to);
    }
    public static boolean isWalkable(int i, int j){
        return StaticMapInfoCollector.Instance().isWalkable(i,j);
    }
    public static boolean isBuildable(TilePosition tilePosition, boolean includeBuilding){
        return StaticMapInfoCollector.Instance().isBuildable(tilePosition, includeBuilding);
    }
    public static boolean isBuildable(TilePosition tilePosition){
        return StaticMapInfoCollector.Instance().isBuildable(tilePosition);
    }
    public static boolean canBuildHere(TilePosition tilePosition, UnitType unitType){
        return StaticMapInfoCollector.Instance().canBuildHere(tilePosition, unitType);
    }
    public static int mapHeight(){
        return StaticMapInfoCollector.Instance().mapHeight();
    }
    public static int mapWidth(){
        return StaticMapInfoCollector.Instance().mapWidth();
    }
}
