package org.monster.common.util;

import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;
import org.monster.common.util.internal.GameMap;

import java.util.ArrayList;
import java.util.List;

public class StaticMapInfoCollector implements InfoCollector {

    private static StaticMapInfoCollector instance = new StaticMapInfoCollector();
    /*Info*/
    Game Broodwar;

    protected GameMap map;
    protected List<BaseLocation> startingBaseLocation = new ArrayList<>();

    protected static StaticMapInfoCollector Instance() {
        return instance;
    }

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
        updateMapSpecificInformation();
    }

    @Override
    public void update() {
        /**
         * should be empty. Map itself can't change
         */
    }

    public void updateMapSpecificInformation() {

        GameMap gameMap = GameMap.UNKNOWN;
        String mapName = Broodwar.mapFileName().toUpperCase();
        if (mapName.matches(".*CIRCUIT.*")) {
            gameMap = GameMap.CIRCUITBREAKER;
        } else if (mapName.matches(".*SPIRIT.*")) {
            gameMap = GameMap.FIGHTING_SPIRITS;
        } else {
            gameMap = GameMap.UNKNOWN;
        }

        List<BaseLocation> startingBase = new ArrayList<>();
        for (BaseLocation base : BWTA.getStartLocations()) {
            if (base.isStartLocation()) {
                startingBase.add(base);
            }
        }
        map = gameMap;
        startingBaseLocation = startingBase;
    }


    protected List<Unit> getMinerals() {
        return Broodwar.getMinerals();
    }
    protected List<Unit> getStaticMinerals() {
        return Broodwar.getStaticMinerals();
    }
    public List<Unit> getStaticGeysers() {
        return Broodwar.getStaticGeysers();
    }
    protected List<Unit> getGeyser() {
        return Broodwar.getGeysers();
    }
    protected boolean isExplored(TilePosition tilePosition) {
        return Broodwar.isExplored(tilePosition);
    }
    protected boolean isExplored(int x, int y) {
        return Broodwar.isExplored(x,y);
    }
    protected boolean isBuildable(TilePosition tilePosition, boolean includeBuilding) {
        return Broodwar.isBuildable(tilePosition, includeBuilding);
    }
    protected boolean isBuildable(TilePosition tilePosition) {
        return Broodwar.isBuildable(tilePosition);
    }
    protected int mapHeight(){
        return Broodwar.mapHeight();
    }
    protected int mapWidth(){
        return Broodwar.mapWidth();
    }
    protected boolean isWalkable(int i, int j) {
        return Broodwar.isWalkable(i, j);
    }
    protected boolean canBuildHere(TilePosition tilePosition, UnitType unitType) {
        return Broodwar.canBuildHere(tilePosition, unitType);
    }
    protected boolean hasPath(Position from, Position to) {
        return Broodwar.hasPath(from, to);
    }
}
