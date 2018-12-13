package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;

public class PositionInfoCollector implements InfoCollector {

    private static PositionInfoCollector instance = new PositionInfoCollector();
    public static PositionInfoCollector Instance() {
        return instance;
    }

    private Game Broodwar;
    private Player selfPlayer;
    private Player enemyPlayer;
    private BaseInfoCollector baseInfoCollector;

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
        selfPlayer = Broodwar.self();
        enemyPlayer = Broodwar.enemy();
    }

    @Override
    public void update() {
    }
}
