package org.monster.common.util.internal;

import bwta.BaseLocation;

import java.util.ArrayList;
import java.util.List;

public class MapSpecificInformation {

    private GameMap map;
    private List<BaseLocation> startingBaseLocation = new ArrayList<>();

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public List<BaseLocation> getStartingBaseLocation() {
        return startingBaseLocation;
    }

    public void setStartingBaseLocation(List<BaseLocation> startingBaseLocation) {
        this.startingBaseLocation = startingBaseLocation;
    }

    public static enum GameMap {
        UNKNOWN, CIRCUITBREAKER, OVERWATCH,
        FIGHTING_SPIRITS, LOST_TEMPLE, THE_HUNTERS
    }
}