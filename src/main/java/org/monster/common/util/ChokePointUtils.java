package org.monster.common.util;

import bwta.Chokepoint;

public class ChokePointUtils {

    public static Chokepoint myFirstChoke() {
        return ChokeInfoCollector.Instance().firstChokePoint.get(PlayerUtils.myRace());
    }

    public static Chokepoint enemyFirstChoke() {
        return ChokeInfoCollector.Instance().firstChokePoint.get(PlayerUtils.enemyRace());
    }

    public static Chokepoint mySecondChoke() {
        return ChokeInfoCollector.Instance().secondChokePoint.get(PlayerUtils.myRace());
    }

    public static Chokepoint enemySecondChoke() {
        return ChokeInfoCollector.Instance().secondChokePoint.get(PlayerUtils.enemyRace());
    }
}