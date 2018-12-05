package org.monster.build.initialProvider.buildSets;

import bwapi.UnitType;

public class VsProtoss {
    public VsProtoss() {
        BaseBuild.queueBuild(true, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
        BaseBuild.queueBuild(false, UnitType.Terran_Supply_Depot);
        BaseBuild.queueBuild(true, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
        BaseBuild.queueBuild(true, UnitType.Terran_Barracks);
        BaseBuild.queueBuild(true, UnitType.Terran_Refinery);
        BaseBuild.queueBuild(true, UnitType.Terran_SCV);
        BaseBuild.queueBuild(true, UnitType.Terran_Supply_Depot);
        BaseBuild.queueBuild(true, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
        BaseBuild.queueBuild(true, UnitType.Terran_Factory);
        BaseBuild.queueBuild(false, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
    }

}