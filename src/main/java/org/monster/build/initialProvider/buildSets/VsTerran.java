package org.monster.build.initialProvider.buildSets;

import bwapi.UnitType;

public class VsTerran {
    public VsTerran() {
        BaseBuild.queueBuild(true, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
        BaseBuild.queueBuild(false, UnitType.Terran_Supply_Depot);
        BaseBuild.queueBuild(true, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
        BaseBuild.queueBuild(true, UnitType.Terran_Barracks);
        BaseBuild.queueBuild(true, UnitType.Terran_Refinery);
        BaseBuild.queueBuild(true, UnitType.Terran_Supply_Depot); // 정찰을 막기 위해 SCV와 순서를 바꿈
        BaseBuild.queueBuild(true, UnitType.Terran_SCV);
        BaseBuild.queueBuild(true, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
        BaseBuild.queueBuild(true, UnitType.Terran_Factory);
        BaseBuild.queueBuild(false, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
    }

}