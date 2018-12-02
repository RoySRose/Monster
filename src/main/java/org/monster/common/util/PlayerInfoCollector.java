package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;
import bwapi.Race;

public class PlayerInfoCollector implements InfoCollector {

    /*Info*/
    protected static Player selfPlayer;
    protected static Player enemyPlayer;
    protected static Player neutralPlayer;
    protected static Race selfRace;
    protected static Race enemyRace;
    private static PlayerInfoCollector instance = new PlayerInfoCollector();
    Game Broodwar;

    public static PlayerInfoCollector Instance() {
        return instance;
    }

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;

        selfPlayer = Broodwar.self();
        enemyPlayer = Broodwar.enemy();
        neutralPlayer = Broodwar.neutral();
        selfRace = selfPlayer.getRace();
        enemyRace = enemyPlayer.getRace();

    }

    @Override
    public void update() {

    }

}
