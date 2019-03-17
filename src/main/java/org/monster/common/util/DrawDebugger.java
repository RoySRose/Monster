package org.monster.common.util;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;

public class DrawDebugger implements InfoCollector {

    private static DrawDebugger instance = new DrawDebugger();
    protected static DrawDebugger Instance() {
        return instance;
    }

    private Game Broodwar;

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
    }

    @Override
    public void update() {
        /**
         * no need?
         */
    }

    protected void drawCircleMap(Position lastPosition, int i, Color color, boolean b) {
        Broodwar.drawCircleMap(lastPosition, i, color, b);
    }
}
