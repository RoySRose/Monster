package org.monster.build.initialProvider.buildSets;


import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;
import org.monster.build.base.BuildManager;
import org.monster.build.base.BuildOrderQueue;
import org.monster.build.base.SeedPositionStrategy;

public class BaseBuild {

    public static void queueBuild(boolean blocking, UnitType... types) {
        BuildOrderQueue bq = BuildManager.Instance().buildQueue;
        SeedPositionStrategy defaultSeedPosition = SeedPositionStrategy.MainBaseLocation;
        for (UnitType type : types) {
            bq.queueAsLowestPriority(type, defaultSeedPosition, blocking);
        }
    }

    public void queueBuild(boolean blocking, UnitType type, SeedPositionStrategy seedPosition) {
        BuildOrderQueue bq = BuildManager.Instance().buildQueue;
        bq.queueAsLowestPriority(type, seedPosition, blocking);
    }

    public void queueBuild(boolean blocking, UnitType type, TilePosition tilePosition) {
        BuildOrderQueue bq = BuildManager.Instance().buildQueue;
//        System.out.println("tilePosition ==> " + tilePosition);
        if (tilePosition == TilePosition.None) {
//        	System.out.println("tilePosition is None");
            bq.queueAsLowestPriority(type, SeedPositionStrategy.MainBaseLocation, blocking);
        } else {
            bq.queueAsLowestPriority(type, tilePosition, blocking);
        }
    }

    public void queueBuild(TechType type) {
        BuildOrderQueue bq = BuildManager.Instance().buildQueue;
        bq.queueAsLowestPriority(type);
    }

    public void queueBuild(UpgradeType type) {
        BuildOrderQueue bq = BuildManager.Instance().buildQueue;
        bq.queueAsLowestPriority(type);
    }
}
