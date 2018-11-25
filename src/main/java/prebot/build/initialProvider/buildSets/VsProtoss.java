package prebot.build.initialProvider.buildSets;

import bwapi.TilePosition;
import bwapi.UnitType;

/// 봇 프로그램 설정
public class VsProtoss extends BaseBuild {

    public VsProtoss(TilePosition firstSupplyPos, TilePosition barrackPos, TilePosition secondSupplyPos, TilePosition factoryPos, TilePosition starport1
            , TilePosition starport2) {

        queueBuild(true, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
        queueBuild(false, UnitType.Terran_Supply_Depot, firstSupplyPos);
        queueBuild(true, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
        queueBuild(true, UnitType.Terran_Barracks, barrackPos);
        queueBuild(true, UnitType.Terran_Refinery);
        queueBuild(true, UnitType.Terran_SCV);
        queueBuild(true, UnitType.Terran_Supply_Depot, secondSupplyPos);
        queueBuild(true, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
        queueBuild(true, UnitType.Terran_Factory, factoryPos);
        queueBuild(false, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV, UnitType.Terran_SCV);
    }

}