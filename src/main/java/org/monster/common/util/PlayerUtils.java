package org.monster.common.util;

import bwapi.Player;
import bwapi.Race;
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

    //TODO 처리하기
    public static boolean hasEnoughResource(UnitType unitType) {
        return hasMoreResourceThan(unitType.mineralPrice(), unitType.gasPrice());
    }

    public static boolean hasMoreResourceThan(int mineralNeed, int gasNeed) {
        return mineralNeed <= myPlayer().minerals() &&  gasNeed <= myPlayer().gas();
    }
}
