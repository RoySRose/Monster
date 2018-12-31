package org.monster.oldmicro.squad;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.util.UnitUtils;
import org.monster.oldmicro.constant.MicroConfig;
import org.monster.oldmicro.control.building.ComsatControl;
import org.monster.oldmicro.control.building.EngineeringBayControl;
import org.monster.oldmicro.control.building.SporeColonyControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BuildingSquad extends Squad {

    //TODO 테란 건물 날리는 스쿼드였음. 저그로 할때 필요할까?
    private SporeColonyControl sporeColonyControl = new SporeColonyControl();

    private EngineeringBayControl engineeringBayControl = new EngineeringBayControl();
    private ComsatControl comsatControl = new ComsatControl();

    public BuildingSquad() {
        super(MicroConfig.SquadInfo.BUILDING);
        setUnitType(UnitType.Zerg_Spore_Colony);
    }

    @Override
    public boolean want(Unit unit) {
        return true;
    }

    @Override
    public List<Unit> recruit(List<Unit> assignableUnitList) {
        return assignableUnitList;
    }

    @Override
    public void execute() {
        Map<UnitType, List<Unit>> unitListMap = UnitUtils.makeUnitListMap(unitList);

        List<Unit> engineeringBayList = unitListMap.getOrDefault(UnitType.Zerg_Spore_Colony, new ArrayList<Unit>());
        sporeColonyControl.controlIfUnitExist(engineeringBayList, Collections.emptySet());

//        List<Unit> engineeringBayList = unitListMap.getOrDefault(UnitType.Terran_Engineering_Bay, new ArrayList<Unit>());
//        // List<UnitInfo> engineeringBayEuiList = findEnemies(engineeringBayList);
//        engineeringBayControl.controlIfUnitExist(engineeringBayList, Collections.emptySet());

//        List<Unit> combatList = unitListMap.getOrDefault(UnitType.Terran_Comsat_Station, new ArrayList<Unit>());
//        List<UnitInfo> comsatEuiList = UnitUtils.getEnemyUnitInfoList(EnemyUnitVisibleStatus.ALL,
//                UnitType.Protoss_Dark_Templar, UnitType.Protoss_Zealot, UnitType.Protoss_Dragoon, UnitType.Protoss_Observer,
//                UnitType.Zerg_Lurker, UnitType.Zerg_Zergling, UnitType.Zerg_Hydralisk,
//                UnitType.Terran_Wraith, UnitType.Terran_Ghost);

//        comsatControl.controlIfUnitExist(combatList, comsatEuiList);
    }

//	public List<UnitInfo> findEnemies(List<Unit> unitList) {
//		List<UnitInfo> euiList = new ArrayList<>();
//		for (Unit unit : unitList) {
//			UnitUtils.addEnemyUnitInfosInRadiusForAir(euiList, unit.getPosition(), unit.getType().sightRange() + MicroConfig.LARGE_ADD_RADIUS);
//		}
//		return euiList;
//	}

    @Override
    public void findEnemies() {
        // nothing
    }
}