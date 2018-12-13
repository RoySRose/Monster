package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;
import bwapi.Race;
import bwapi.UnitType;

public class PlayerInfoCollector implements InfoCollector {

    private static PlayerInfoCollector instance = new PlayerInfoCollector();
    protected static PlayerInfoCollector Instance() {
        return instance;
    }

    private Game Broodwar;

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

    protected int supplyUsedSelf() {
        return Broodwar.self().supplyUsed();
    }

    protected int supplyTotalSelf() {
        return Broodwar.self().supplyTotal();
    }

    protected int supplyUsedEnemy() {
        return Broodwar.enemy().supplyUsed();
    }

    protected int supplyTotalEnemy() {
        return Broodwar.enemy().supplyTotal();
    }

    protected int mineralSelf() {
        return Broodwar.self().minerals();
    }

    protected int mineralEnemy() {
        return Broodwar.enemy().minerals();
    }

    protected int gasSelf() {
        return Broodwar.self().gas();
    }

    protected int gasEnemy() {
        return Broodwar.enemy().gas();
    }

    protected int getDamageFrom(UnitType unitType1, UnitType unitType2) {
        return Broodwar.getDamageFrom(unitType1, unitType2);
    }

    protected int getLatency() {
        return Broodwar.getLatency();
    }

    protected void printf(String msg) {
        Broodwar.printf(msg);
    }

}
