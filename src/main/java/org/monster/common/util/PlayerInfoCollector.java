package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;
import bwapi.Race;

public class PlayerInfoCollector implements InfoCollector{

    private static PlayerInfoCollector instance = new PlayerInfoCollector();
    public static PlayerInfoCollector Instance() {
        return instance;
    }
    Game Broodwar;

    /*Info*/
    protected static Player selfPlayer;
    protected static Player enemyPlayer;
    protected static Player neutralPlayer;

    protected static Race selfRace;
    protected static Race enemyRace;

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
