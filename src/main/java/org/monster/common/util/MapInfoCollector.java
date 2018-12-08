package org.monster.common.util;

import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import org.monster.common.util.internal.GameMap;

import java.util.ArrayList;
import java.util.List;

public class MapInfoCollector implements InfoCollector {

    private static MapInfoCollector instance = new MapInfoCollector();
    protected static MapInfoCollector Instance() {
        return instance;
    }

    private Game Broodwar;

    protected GameMap map;

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

    private void updateMapSpecificInformation() {

        GameMap gameMap;
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
    }


    protected List<Unit> getMinerals() {
        return Broodwar.getMinerals();
    }
    protected List<Unit> getStaticMinerals() {
        return Broodwar.getStaticMinerals();
    }
    protected List<Unit> getStaticGeysers() {
        return Broodwar.getStaticGeysers();
    }

    protected List<Unit> getGeyser() {
        return Broodwar.getGeysers();
    }

    protected boolean isExplored(TilePosition tilePosition) {
        return Broodwar.isExplored(tilePosition);
    }

    protected boolean isExplored(int x, int y) {
        return Broodwar.isExplored(x, y);
    }

    protected boolean isBuildable(TilePosition tilePosition, boolean includeBuilding) {
        return Broodwar.isBuildable(tilePosition, includeBuilding);
    }

    protected boolean isBuildable(TilePosition tilePosition) {
        return Broodwar.isBuildable(tilePosition);
    }

    protected int mapHeight() {
        return Broodwar.mapHeight();
    }

    protected int mapWidth() {
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

    protected boolean isVisible(TilePosition tilePosition) {
        return Broodwar.isVisible(tilePosition);
    }
}
