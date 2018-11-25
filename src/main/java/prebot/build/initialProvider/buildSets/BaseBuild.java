package prebot.build.initialProvider.buildSets;


import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.UnitType;
import bwapi.UpgradeType;
import prebot.build.base.BuildManager;
import prebot.build.base.BuildOrderItem;
import prebot.build.base.BuildOrderQueue;

public class BaseBuild {

    public static void queueBuild(boolean blocking, UnitType... types) {
        BuildOrderQueue bq = BuildManager.Instance().buildQueue;
        BuildOrderItem.SeedPositionStrategy defaultSeedPosition = BuildOrderItem.SeedPositionStrategy.MainBaseLocation;
        for (UnitType type : types) {
            bq.queueAsLowestPriority(type, defaultSeedPosition, blocking);
        }
    }

    public void queueBuild(boolean blocking, UnitType type, BuildOrderItem.SeedPositionStrategy seedPosition) {
        BuildOrderQueue bq = BuildManager.Instance().buildQueue;
        bq.queueAsLowestPriority(type, seedPosition, blocking);
    }

    public void queueBuild(boolean blocking, UnitType type, TilePosition tilePosition) {
        BuildOrderQueue bq = BuildManager.Instance().buildQueue;
//        System.out.println("tilePosition ==> " + tilePosition);
        if (tilePosition == TilePosition.None) {
//        	System.out.println("tilePosition is None");
            bq.queueAsLowestPriority(type, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, blocking);
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
