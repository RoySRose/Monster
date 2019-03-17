package org.monster.common.util;

import bwta.Chokepoint;

public class ChokeUtils {

    public static Chokepoint myFirstChoke() {
        return ChokeInfoCollector.Instance().firstChokePoint.get(PlayerUtils.myPlayer());
    }

    public static Chokepoint enemyFirstChoke() {
        return ChokeInfoCollector.Instance().firstChokePoint.get(PlayerUtils.enemyPlayer());
    }

    public static Chokepoint mySecondChoke() {
        return ChokeInfoCollector.Instance().secondChokePoint.get(PlayerUtils.myPlayer());
    }

    public static Chokepoint enemySecondChoke() {
        return ChokeInfoCollector.Instance().secondChokePoint.get(PlayerUtils.enemyPlayer());
    }


}