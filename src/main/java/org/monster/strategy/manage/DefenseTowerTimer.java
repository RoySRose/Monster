package org.monster.strategy.manage;

import bwapi.Race;
import org.monster.common.util.PlayerUtils;

//TODO not in use, how about in the future?
@Deprecated
public class DefenseTowerTimer {

    private static DefenseTowerTimer instance = new DefenseTowerTimer();

    public static DefenseTowerTimer Instance() {
        return instance;
    }

    public void update() {
        if (PlayerUtils.enemyRace() == Race.Protoss) {

        } else if (PlayerUtils.enemyRace() == Race.Zerg) {

        } else if (PlayerUtils.enemyRace() == Race.Terran) {

        } else {

        }
    }

}
