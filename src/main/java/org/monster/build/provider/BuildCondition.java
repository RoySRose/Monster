package org.monster.build.provider;

import bwapi.TilePosition;
import org.monster.build.base.SeedPositionStrategy;
//import prebot.common.util.*;

public class BuildCondition {

    public boolean blocking = false;
    public boolean highPriority = false;
    public SeedPositionStrategy seedPositionStrategy = SeedPositionStrategy.MainBaseLocation;
    public TilePosition tilePosition = TilePosition.None;
	/*public static boolean blocking=false;
    public static boolean highPriority=false;
    public static BuildOrderItem.SeedPositionStrategy seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.NoLocation;
    public static TilePosition tilePosition = TilePosition.None;*/


    public BuildCondition() {
    	/*this.blocking = false;
        this.highPriority = false;
        this.seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.NoLocation;
        this.tilePosition = TilePosition.None;*/
    }

    public BuildCondition(boolean blocking, boolean highPriority, SeedPositionStrategy seedPositionStrategy, TilePosition tilePostition) {
        this.blocking = blocking;
        this.highPriority = highPriority;
        this.seedPositionStrategy = seedPositionStrategy;
        this.tilePosition = tilePostition;
    }
    
    /*public BuildCondition(boolean blockingVar, boolean highPriorityVar, BuildOrderItem.SeedPositionStrategy seedPositionStrategyVar, TilePosition tilePostitionVar) {
        blocking = blockingVar;
        highPriority = highPriorityVar;
        seedPositionStrategy = seedPositionStrategyVar;
        tilePosition = tilePostitionVar;
    }*/


}
