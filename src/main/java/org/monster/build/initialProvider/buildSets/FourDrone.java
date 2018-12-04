package org.monster.build.initialProvider.buildSets;


import bwapi.UnitType;

/// 봇 프로그램 설정
public class FourDrone extends BaseBuild {


    public FourDrone() {

        /*2스타 레이스*/
        queueBuild(true, UnitType.Zerg_Spawning_Pool);
        queueBuild(true, UnitType.Zerg_Zergling, UnitType.Zerg_Zergling, UnitType.Zerg_Zergling);
        queueBuild(true, UnitType.Zerg_Overlord);
    }

}

