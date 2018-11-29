package org.monster.common.util;

import bwta.Chokepoint;

public class ChokePointUtils {

    public static Chokepoint myFirstChoke() {
        return BaseInfoCollector.Instance().firstChokePoint.get(PlayerUtils.myRace());
    }

    public static Chokepoint enemyFirstChoke() {
        return BaseInfoCollector.Instance().firstChokePoint.get(PlayerUtils.enemyRace());
    }

    public static Chokepoint mySecondChoke() {
        return BaseInfoCollector.Instance().secondChokePoint.get(PlayerUtils.myRace());
    }

    public static Chokepoint enemySecondChoke() {
        return BaseInfoCollector.Instance().secondChokePoint.get(PlayerUtils.enemyRace());
    }
}