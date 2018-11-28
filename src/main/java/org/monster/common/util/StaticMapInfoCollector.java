package org.monster.common.util;

import bwapi.Game;
import bwta.BWTA;
import bwta.BaseLocation;
import org.monster.common.util.internal.MapSpecificInformation;

import java.util.ArrayList;
import java.util.List;

public class StaticMapInfoCollector implements InfoCollector{

    private static StaticMapInfoCollector instance = new StaticMapInfoCollector();
    protected static StaticMapInfoCollector Instance() {
        return instance;
    }
    Game Broodwar;

    /*Info*/
    protected MapSpecificInformation mapSpecificInformation;

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
        // name으로 map 판단
        MapSpecificInformation.GameMap gameMap = MapSpecificInformation.GameMap.UNKNOWN;
        String mapName = Broodwar.mapFileName().toUpperCase();
        if (mapName.matches(".*CIRCUIT.*")) {
            gameMap = MapSpecificInformation.GameMap.CIRCUITBREAKER;
        } else if (mapName.matches(".*SPIRIT.*")) {
            gameMap = MapSpecificInformation.GameMap.FIGHTING_SPIRITS;
        } else {
            gameMap = MapSpecificInformation.GameMap.UNKNOWN;
        }

        List<BaseLocation> startingBase = new ArrayList<>();
        for (BaseLocation base : BWTA.getStartLocations()) {
            if (base.isStartLocation()) {
                startingBase.add(base);
            }
        }

        MapSpecificInformation mapInfo = new MapSpecificInformation();
        mapInfo.setMap(gameMap);
        mapInfo.setStartingBaseLocation(startingBase);

        this.mapSpecificInformation = mapInfo;
    }

}
