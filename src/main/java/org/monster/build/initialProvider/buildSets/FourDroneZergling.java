package org.monster.build.initialProvider.buildSets;


import bwapi.UnitType;

public class FourDroneZergling {

    public FourDroneZergling() {
        BaseBuild.queueBuild(true, UnitType.Zerg_Spawning_Pool);
        BaseBuild.queueBuild(true, UnitType.Zerg_Zergling, UnitType.Zerg_Zergling, UnitType.Zerg_Zergling);
        BaseBuild.queueBuild(true, UnitType.Zerg_Overlord);
    }
}

