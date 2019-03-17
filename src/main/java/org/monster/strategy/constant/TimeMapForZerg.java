package org.monster.strategy.constant;

import bwapi.UnitType;

public class TimeMapForZerg {

    public static EnemyStrategyOptions.BuildTimeMap ZERG_5DRONE() {
        return new EnemyStrategyOptions.BuildTimeMap()
                .put(UnitType.Zerg_Spawning_Pool, 0, 45)
                .put(UnitType.Zerg_Lair, 10, 0);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_9DRONE() {
        return new EnemyStrategyOptions.BuildTimeMap()
                .put(UnitType.Zerg_Spawning_Pool, 1, 15)
                .put(UnitType.Zerg_Overlord, 1, 35)
                .put(UnitType.Zerg_Hatchery, 2, 25)
                .put(UnitType.Zerg_Lair, 3, 40);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_9DRONE_GAS() {
        return new EnemyStrategyOptions.BuildTimeMap()
                .put(UnitType.Zerg_Spawning_Pool, 1, 15)
                .put(UnitType.Zerg_Extractor, 1, 20)
                .put(UnitType.Zerg_Overlord, 1, 30)
                .put(UnitType.Zerg_Lair, 2, 40);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_9DRONE_GAS_DOUBLE() {
        return new EnemyStrategyOptions.BuildTimeMap()
                .put(UnitType.Zerg_Spawning_Pool, 1, 15)
                .put(UnitType.Zerg_Extractor, 1, 20)
                .put(UnitType.Zerg_Overlord, 1, 30)
                .put(UnitType.Zerg_Hatchery, 3, 20)
                .put(UnitType.Zerg_Lair, 3, 50);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_OVERPOOL() {
        return new EnemyStrategyOptions.BuildTimeMap()
                .put(UnitType.Zerg_Overlord, 0, 55)
                .put(UnitType.Zerg_Spawning_Pool, 1, 20)
                .put(UnitType.Zerg_Hatchery, 2, 5)
                .put(UnitType.Zerg_Lair, 3, 30);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_OVERPOOL_GAS() {
        return new EnemyStrategyOptions.BuildTimeMap()
                .put(UnitType.Zerg_Overlord, 0, 55)
                .put(UnitType.Zerg_Spawning_Pool, 1, 20)
                .put(UnitType.Zerg_Extractor, 1, 30)
                .put(UnitType.Zerg_Lair, 2, 45);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_OVERPOOL_GAS_DOUBLE() {
        return new EnemyStrategyOptions.BuildTimeMap()
                .put(UnitType.Zerg_Overlord, 0, 55)
                .put(UnitType.Zerg_Spawning_Pool, 1, 20)
                .put(UnitType.Zerg_Extractor, 1, 30)
                .put(UnitType.Zerg_Hatchery, 3, 0)
                .put(UnitType.Zerg_Lair, 3, 25);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_2HAT_GAS() {
        return new EnemyStrategyOptions.BuildTimeMap()
                .put(UnitType.Zerg_Overlord, 0, 55)
                .put(UnitType.Zerg_Hatchery, 1, 50)
                .put(UnitType.Zerg_Spawning_Pool, 2, 5)
                .put(UnitType.Zerg_Extractor, 2, 20)
                .put(UnitType.Zerg_Lair, 3, 5);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_3HAT() {
        return new EnemyStrategyOptions.BuildTimeMap()
                .put(UnitType.Zerg_Overlord, 0, 55)
                .put(UnitType.Zerg_Hatchery, 1, 50)
                .put(UnitType.Zerg_Spawning_Pool, 2, 5)
                .put(UnitType.Zerg_Hatchery, 2, 50)
                .put(UnitType.Zerg_Extractor, 3, 0)
                .put(UnitType.Zerg_Lair, 3, 50);
    }

    public static EnemyStrategyOptions.BuildTimeMap HYDRA_ALL_IN() {
        return new EnemyStrategyOptions.BuildTimeMap().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.ZERG_FAST_ALL_IN)
                .put(UnitType.Zerg_Spawning_Pool, 1, 15)
                .put(UnitType.Zerg_Extractor, 1, 20)
                .put(UnitType.Zerg_Overlord, 1, 30)
                .put(UnitType.Zerg_Lair, 2, 40);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_FAST_MUTAL() {
        return new EnemyStrategyOptions.BuildTimeMap();
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_FAST_LURKER() {
        return new EnemyStrategyOptions.BuildTimeMap().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DETECT_IMPORTANT);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_FAST_1HAT_LURKER() {
        return new EnemyStrategyOptions.BuildTimeMap().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DETECT_IMPORTANT, EnemyStrategyOptions.BuildTimeMap.Feature.ZERG_FAST_ALL_IN);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_NO_LAIR_LING() {
        return new EnemyStrategyOptions.BuildTimeMap().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.NO_LAIR);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_NO_LAIR_HYDRA() {
        return new EnemyStrategyOptions.BuildTimeMap().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.NO_LAIR
                , EnemyStrategyOptions.BuildTimeMap.Feature.DEFENSE_FRONT, EnemyStrategyOptions.BuildTimeMap.Feature.ZERG_FAST_ALL_IN);
    }

    public static EnemyStrategyOptions.BuildTimeMap ZERG_LAIR_MIXED() {
        return new EnemyStrategyOptions.BuildTimeMap();
    }

}
