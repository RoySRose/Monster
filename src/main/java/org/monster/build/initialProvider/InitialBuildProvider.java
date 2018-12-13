package org.monster.build.initialProvider;

import org.monster.bootstrap.GameManager;
import org.monster.build.initialProvider.buildSets.FourDroneZergling;
import org.monster.strategy.constant.EnemyStrategyOptions;

public class InitialBuildProvider extends GameManager {

    private static InitialBuildProvider instance = new InitialBuildProvider();

    public EnemyStrategyOptions.ExpansionOption nowStrategy;

    public static InitialBuildProvider Instance() {
        return instance;
    }

    public void onStart() {
        System.out.println("InitialBuildProvider onStart start");

        nowStrategy = null;

        /**
         * temporary
         */
        new FourDroneZergling();
//        if (PlayerUtils.enemyRace() == Race.Terran) {
//            new VsTerran();
//        } else if (PlayerUtils.enemyRace() == Race.Protoss) {
//            new VsProtoss();
//        } else {
//            new VsZerg();
//        }

        System.out.println("InitialBuildProvider onStart end");
    }

    @Override
    public void update() {

    }
}









