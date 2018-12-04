package org.monster.common.util;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

import java.util.HashMap;
import java.util.Map;

public class DrawDebugger implements InfoCollector {

    private static DrawDebugger instance = new DrawDebugger();
    private Game Broodwar;

    protected static DrawDebugger Instance() {
        return instance;
    }

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
    }

    @Override
    public void update() {
        /**
         * no need
         */
    }

    public void drawCircleMap(Position lastPosition, int i, Color color, boolean b) {
        Broodwar.drawCircleMap(lastPosition, i, color, b);
    }
}
