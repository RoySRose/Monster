package org.monster.common.util;

import bwapi.Game;
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


}
