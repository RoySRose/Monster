package org.monster.common.util;

import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;
import bwta.Chokepoint;
import bwta.Region;
import org.monster.common.UnitInfo;
import org.monster.common.util.internal.MapSpecificInformation;
import org.monster.main.Monster;
import org.monster.micro.CombatManager;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.squad.Squad;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class InfoUtils {



    public static Chokepoint myFirstChoke() {
        return InformationManager.Instance().getFirstChokePoint(Monster.Broodwar.self());
    }

    public static Chokepoint enemyFirstChoke() {
        return InformationManager.Instance().getFirstChokePoint(Monster.Broodwar.enemy());
    }

    public static Chokepoint mySecondChoke() {
        return InformationManager.Instance().getSecondChokePoint(Monster.Broodwar.self());
    }

    public static Chokepoint enemySecondChoke() {
        return InformationManager.Instance().getSecondChokePoint(Monster.Broodwar.enemy());
    }

    public static Position myReadyToPosition() {
        return InformationManager.Instance().getReadyToAttackPosition(Monster.Broodwar.self());
    }

    public static Position enemyReadyToPosition() {
        return InformationManager.Instance().getReadyToAttackPosition(Monster.Broodwar.enemy());
    }

    public static Unit myBaseGas() {
        if (BaseUtils.myMainBase() != null) {
            List<Unit> geysers = BaseUtils.myMainBase().getGeysers();
            if (geysers != null && !geysers.isEmpty()) {
                return geysers.get(0);
            }
        }
        return null;
    }

    public static Unit enemyBaseGas() {
        if (BaseUtils.enemyMainBase() != null) {
            List<Unit> geysers = BaseUtils.enemyMainBase().getGeysers();
            if (geysers != null && !geysers.isEmpty()) {
                return geysers.get(0);
            }
        }
        return null;
    }


    public static MapSpecificInformation mapInformation() {
        return StaticMapUtils.getMapSpecificInformation();
    }

    public static Vector<Position> getRegionVertices(BaseLocation base) {
        return InformationManager.Instance().getBaseRegionVerticesMap(base);
    }

    public static List<UnitInfo> euiListInMyRegion(Region myRegion) {
        List<UnitInfo> euiListInMyRegion = InformationManager.Instance().getEuiListInMyRegion(myRegion);
        if (euiListInMyRegion == null) {
            euiListInMyRegion = new ArrayList<>();
        }
        return euiListInMyRegion;
    }

    public static Set<UnitInfo> euiListInBase() {
        return InformationManager.Instance().getEuisInBaseRegion();
    }

    public static Set<UnitInfo> euiListInExpansion() {
        return InformationManager.Instance().getEuisInExpansionRegion();
    }

    public static Set<UnitInfo> euiListInThirdRegion() {
        return InformationManager.Instance().getEuisInThirdRegion();
    }

    public static Region myThirdRegion() {
        return InformationManager.Instance().getThirdRegion(Monster.Broodwar.self());
    }

    public static Region enemyThirdRegion() {
        return InformationManager.Instance().getThirdRegion(Monster.Broodwar.enemy());
    }


    public static int squadUnitSize(MicroConfig.SquadInfo squadInfo) {
        Squad squad = CombatManager.Instance().squadData.getSquad(squadInfo.squadName);
        if (squad != null && squad.unitList != null) {
            return squad.unitList.size();
        } else {
            return 0;
        }
    }

    // 해당 종족의 UnitType 중 ResourceDepot 기능을 하는 UnitType을 리턴합니다
    public static UnitType getBasicResourceDepotBuildingType(Race race) {
        if (race == Race.Protoss) {
            return UnitType.Protoss_Nexus;
        } else if (race == Race.Terran) {
            return UnitType.Terran_Command_Center;
        } else if (race == Race.Zerg) {
            return UnitType.Zerg_Hatchery;
        } else {
            return UnitType.None;
        }
    }

    // 해당 종족의 UnitType 중 Refinery 기능을 하는 UnitType을 리턴합니다
    public static UnitType getRefineryBuildingType(Race race) {
        if (race == Race.Protoss) {
            return UnitType.Protoss_Assimilator;
        } else if (race == Race.Terran) {
            return UnitType.Terran_Refinery;
        } else if (race == Race.Zerg) {
            return UnitType.Zerg_Extractor;
        } else {
            return UnitType.None;
        }
    }
}
