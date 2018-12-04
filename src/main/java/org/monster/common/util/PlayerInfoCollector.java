package org.monster.common.util;

import bwapi.*;

public class PlayerInfoCollector implements InfoCollector {

    /*Info*/
    protected static Player selfPlayer;
    protected static Player enemyPlayer;
    protected static Player neutralPlayer;
    protected static Race selfRace;
    protected static Race enemyRace;
    private static PlayerInfoCollector instance = new PlayerInfoCollector();
    private Game Broodwar;

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

    protected int supplyUsedSelf(){
        return Broodwar.self().supplyUsed();
    }
    protected int supplyTotalSelf(){
        return Broodwar.self().supplyTotal();
    }
    protected int supplyUsedEnemy(){
        return Broodwar.enemy().supplyUsed();
    }
    protected int supplyTotalEnemy(){
        return Broodwar.enemy().supplyTotal();
    }
    protected int mineralSelf(){
        return Broodwar.enemy().minerals();
    }
    protected int mineralEnemy(){
        return Broodwar.enemy().minerals();
    }
    protected int gasSelf(){
        return Broodwar.enemy().gas();
    }
    protected int gasEnemy(){
        return Broodwar.enemy().gas();
    }
    protected int getDamageFrom(UnitType unitType1, UnitType unitType2){
        return Broodwar.getDamageFrom(unitType1, unitType2);
    }

    protected boolean isVisible(TilePosition tilePosition) {
        return Broodwar.isVisible(tilePosition);
    }
    protected int getLatency() {
        return Broodwar.getLatency();
    }

    protected void printf(String msg) {
        Broodwar.printf(msg);
    }

}
