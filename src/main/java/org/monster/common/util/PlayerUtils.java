package org.monster.common.util;

import bwapi.Player;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.UnitType;

public class PlayerUtils {

    public static Player myPlayer() {
        return PlayerInfoCollector.Instance().selfPlayer;
    }

    public static Player enemyPlayer() {
        return PlayerInfoCollector.Instance().enemyPlayer;
    }

    public static Race myRace() {
        return PlayerInfoCollector.Instance().selfRace;
    }

    public static Race enemyRace() {
        return PlayerInfoCollector.Instance().enemyRace;
    }

    public static Player neutralPlayer() {
        return PlayerInfoCollector.Instance().neutralPlayer;
    }

    //TODO 처리하기
    public static boolean hasEnoughResource(UnitType unitType) {
        return hasMoreResourceThan(unitType.mineralPrice(), unitType.gasPrice());
    }

    public static boolean hasMoreResourceThan(int mineralNeed, int gasNeed) {
        return mineralNeed <= myPlayer().minerals() && gasNeed <= myPlayer().gas();
    }

    public static boolean isVisible(TilePosition tilePosition){
        return PlayerInfoCollector.Instance().isVisible(tilePosition);
    }

    public static int getLatency(){
        return PlayerInfoCollector.Instance().getLatency();
    }

    public static int supplyUsedSelf(){
        return PlayerInfoCollector.Instance().supplyUsedSelf();
    }
    public static int supplyTotalSelf(){
        return PlayerInfoCollector.Instance().supplyTotalSelf();
    }
    public static int supplyUsedEnemy(){
        return PlayerInfoCollector.Instance().supplyUsedEnemy();
    }
    public static int supplyTotalEnemy(){
        return PlayerInfoCollector.Instance().supplyTotalEnemy();
    }
    public static int mineralSelf(){
        return PlayerInfoCollector.Instance().mineralSelf();
    }
    public static int mineralEnemy(){
        return PlayerInfoCollector.Instance().mineralEnemy();
    }
    public static int gasSelf(){
        return PlayerInfoCollector.Instance().gasSelf();
    }
    public static int gasEnemy(){
        return PlayerInfoCollector.Instance().gasEnemy();
    }
    public static int getDamageFrom(UnitType unitType1, UnitType unitType2){
        return PlayerInfoCollector.Instance().getDamageFrom(unitType1, unitType2);
    }
    public static void printf(String msg){
        PlayerInfoCollector.Instance().printf(msg);
    }
}
