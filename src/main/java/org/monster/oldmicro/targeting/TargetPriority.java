package org.monster.oldmicro.targeting;


import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;

import java.util.HashMap;
import java.util.Map;

//참고표 : http://kin.naver.com/qna/detail.nhn?d1id=2&dirId=2020401&docId=40566552
//TODO NEED RESET!
public class TargetPriority {

    private static final int PRIORITY_MAX = 200;
    private static final int PRIORITY_GAP = 10;
    private static final Map<UnitType, Map<UnitType, Integer>> PRIORITY_MAP = new HashMap<>();

    static {

        Map<UnitType, Integer> vulturePriorityMap = new HashMap<>();
        Map<UnitType, Integer> tankModePriorityMap = new HashMap<>();
        Map<UnitType, Integer> siegeModePriorityMap = new HashMap<>();
        Map<UnitType, Integer> goliathPriorityMap = new HashMap<>();
        Map<UnitType, Integer> wraithPriorityMap = new HashMap<>();
        Map<UnitType, Integer> marinePriorityMap = new HashMap<>();

        // 벌처 vs 테란
        int inputPriority = PRIORITY_MAX;
        vulturePriorityMap.put(UnitType.Terran_Vulture_Spider_Mine, inputPriority);
        vulturePriorityMap.put(UnitType.Terran_Ghost, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Terran_Marine, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Terran_Medic, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Terran_Firebat, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Terran_SCV, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Terran_Vulture, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Terran_Siege_Tank_Siege_Mode, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Terran_Siege_Tank_Tank_Mode, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Terran_Goliath, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap = inputBuildingPriority(Race.Terran, vulturePriorityMap, inputPriority - PRIORITY_GAP);

        // 벌처 vs 플토
        inputPriority = PRIORITY_MAX;
        vulturePriorityMap.put(UnitType.Protoss_High_Templar, inputPriority);
        vulturePriorityMap.put(UnitType.Protoss_Dark_Templar, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Protoss_Archon, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Protoss_Probe, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Protoss_Zealot, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Protoss_Reaver, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Protoss_Dragoon, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Protoss_Dark_Archon, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap = inputBuildingPriority(Race.Protoss, vulturePriorityMap, inputPriority - PRIORITY_GAP);

        // 벌처 vs 저그
        inputPriority = PRIORITY_MAX;
        vulturePriorityMap.put(UnitType.Zerg_Infested_Terran, inputPriority);
        vulturePriorityMap.put(UnitType.Zerg_Defiler, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Zerg_Lurker, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Zerg_Zergling, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Zerg_Drone, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Zerg_Hydralisk, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Zerg_Ultralisk, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Zerg_Broodling, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Zerg_Larva, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Zerg_Lurker_Egg, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap.put(UnitType.Zerg_Egg, inputPriority -= PRIORITY_GAP);
        vulturePriorityMap = inputBuildingPriority(Race.Zerg, vulturePriorityMap, inputPriority - PRIORITY_GAP);


        // 탱크 vs 테란
        inputPriority = PRIORITY_MAX;
        tankModePriorityMap.put(UnitType.Terran_Vulture_Spider_Mine, inputPriority);
        tankModePriorityMap.put(UnitType.Terran_Siege_Tank_Siege_Mode, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Terran_Siege_Tank_Tank_Mode, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Terran_Goliath, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Terran_Vulture, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Terran_Ghost, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Terran_Marine, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Terran_Medic, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Terran_Firebat, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Terran_SCV, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap = inputBuildingPriority(Race.Terran, tankModePriorityMap, inputPriority - PRIORITY_GAP);

        // 탱크 vs 플토
        inputPriority = PRIORITY_MAX;
        tankModePriorityMap.put(UnitType.Protoss_Reaver, inputPriority);
        tankModePriorityMap.put(UnitType.Protoss_High_Templar, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Protoss_Dark_Templar, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Protoss_Dragoon, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Protoss_Archon, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Protoss_Zealot, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Protoss_Dark_Archon, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Protoss_Probe, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap = inputBuildingPriority(Race.Protoss, tankModePriorityMap, inputPriority - PRIORITY_GAP);

        // 탱크 vs 저그
        inputPriority = PRIORITY_MAX;
        tankModePriorityMap.put(UnitType.Zerg_Infested_Terran, inputPriority);
        tankModePriorityMap.put(UnitType.Zerg_Defiler, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Zerg_Ultralisk, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Zerg_Lurker, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Zerg_Hydralisk, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Zerg_Zergling, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Zerg_Drone, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Zerg_Broodling, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Zerg_Lurker_Egg, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Zerg_Egg, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap.put(UnitType.Zerg_Larva, inputPriority -= PRIORITY_GAP);
        tankModePriorityMap = inputBuildingPriority(Race.Zerg, tankModePriorityMap, inputPriority - PRIORITY_GAP);


        // 시즈 vs 테란
        inputPriority = PRIORITY_MAX;
        siegeModePriorityMap.put(UnitType.Terran_Siege_Tank_Siege_Mode, inputPriority);
        siegeModePriorityMap.put(UnitType.Terran_Siege_Tank_Tank_Mode, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Terran_Goliath, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Terran_Vulture, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Terran_Vulture_Spider_Mine, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Terran_Ghost, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Terran_Marine, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Terran_Medic, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Terran_Firebat, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Terran_SCV, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap = inputBuildingPriority(Race.Terran, siegeModePriorityMap, inputPriority - PRIORITY_GAP);

        // 시즈 vs 플토
        inputPriority = PRIORITY_MAX;
        siegeModePriorityMap.put(UnitType.Protoss_Reaver, inputPriority);
        siegeModePriorityMap.put(UnitType.Protoss_High_Templar, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Protoss_Dark_Templar, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Protoss_Dragoon, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Protoss_Archon, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Protoss_Zealot, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Protoss_Dark_Archon, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Protoss_Probe, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap = inputBuildingPriority(Race.Protoss, siegeModePriorityMap, inputPriority - PRIORITY_GAP);

        // 시즈 vs 저그
        inputPriority = PRIORITY_MAX;
        siegeModePriorityMap.put(UnitType.Zerg_Infested_Terran, inputPriority);
        siegeModePriorityMap.put(UnitType.Zerg_Defiler, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Zerg_Ultralisk, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Zerg_Lurker, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Zerg_Hydralisk, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Zerg_Zergling, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Zerg_Lurker_Egg, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Zerg_Drone, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Zerg_Egg, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Zerg_Larva, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap.put(UnitType.Zerg_Broodling, inputPriority -= PRIORITY_GAP);
        siegeModePriorityMap = inputBuildingPriority(Race.Zerg, siegeModePriorityMap, inputPriority - PRIORITY_GAP);


        // 골리앗 vs 테란
        inputPriority = PRIORITY_MAX;
        goliathPriorityMap.put(UnitType.Terran_Vulture_Spider_Mine, inputPriority);
        goliathPriorityMap.put(UnitType.Terran_Ghost, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Wraith, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Battlecruiser, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Dropship, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Siege_Tank_Siege_Mode, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Siege_Tank_Tank_Mode, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Goliath, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Marine, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Medic, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Firebat, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Vulture, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Science_Vessel, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_SCV, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Terran_Valkyrie, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap = inputBuildingPriority(Race.Terran, goliathPriorityMap, inputPriority - PRIORITY_GAP);

        // 골리앗 vs 플토
        inputPriority = PRIORITY_MAX;
        goliathPriorityMap.put(UnitType.Protoss_Arbiter, inputPriority);
        goliathPriorityMap.put(UnitType.Protoss_Carrier, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_Scout, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_High_Templar, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_Dark_Templar, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_Reaver, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_Shuttle, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_Archon, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_Zealot, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_Dragoon, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_Probe, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_Dark_Archon, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_Corsair, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Protoss_Interceptor, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap = inputBuildingPriority(Race.Protoss, goliathPriorityMap, inputPriority - PRIORITY_GAP);

        // 골리앗 vs 저그
        inputPriority = PRIORITY_MAX;
        goliathPriorityMap.put(UnitType.Zerg_Guardian, inputPriority);
        goliathPriorityMap.put(UnitType.Zerg_Infested_Terran, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Zergling, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Mutalisk, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Defiler, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Lurker, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Scourge, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Hydralisk, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Ultralisk, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Queen, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Devourer, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Drone, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Overlord, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Broodling, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Lurker_Egg, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Larva, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap.put(UnitType.Zerg_Egg, inputPriority -= PRIORITY_GAP);
        goliathPriorityMap = inputBuildingPriority(Race.Zerg, goliathPriorityMap, inputPriority - PRIORITY_GAP);

        // 레이스 vs 테란
        inputPriority = PRIORITY_MAX;
        wraithPriorityMap.put(UnitType.Terran_Wraith, inputPriority);
        wraithPriorityMap.put(UnitType.Terran_SCV, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap.put(UnitType.Terran_Siege_Tank_Siege_Mode, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap.put(UnitType.Terran_Siege_Tank_Tank_Mode, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap.put(UnitType.Terran_Vulture, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap.put(UnitType.Terran_Vulture_Spider_Mine, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap.put(UnitType.Terran_Firebat, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap.put(UnitType.Terran_Medic, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap.put(UnitType.Terran_Ghost, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap.put(UnitType.Terran_Marine, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap.put(UnitType.Terran_Firebat, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap.put(UnitType.Terran_Vulture, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap.put(UnitType.Terran_Goliath, inputPriority -= PRIORITY_GAP);
        wraithPriorityMap = inputBuildingPriority(Race.Terran, wraithPriorityMap, inputPriority - PRIORITY_GAP);


        // 마린 vs 테란
        inputPriority = PRIORITY_MAX;
        marinePriorityMap.put(UnitType.Terran_Marine, inputPriority -= PRIORITY_GAP);
        marinePriorityMap.put(UnitType.Terran_SCV, inputPriority -= PRIORITY_GAP);
        marinePriorityMap.put(UnitType.Terran_Refinery, inputPriority -= PRIORITY_GAP);

        // 마린 vs 플토
        inputPriority = PRIORITY_MAX;
        marinePriorityMap.put(UnitType.Protoss_Zealot, inputPriority -= PRIORITY_GAP);
        marinePriorityMap.put(UnitType.Protoss_Dragoon, inputPriority -= PRIORITY_GAP);
        marinePriorityMap.put(UnitType.Protoss_Photon_Cannon, inputPriority -= PRIORITY_GAP);
        marinePriorityMap.put(UnitType.Protoss_Probe, inputPriority -= PRIORITY_GAP);
        marinePriorityMap.put(UnitType.Protoss_Assimilator, inputPriority -= PRIORITY_GAP);

        // 마린 vs 저그
        inputPriority = PRIORITY_MAX;
        marinePriorityMap.put(UnitType.Zerg_Zergling, inputPriority -= PRIORITY_GAP);
        marinePriorityMap.put(UnitType.Zerg_Drone, inputPriority -= PRIORITY_GAP);
        marinePriorityMap.put(UnitType.Zerg_Extractor, inputPriority -= PRIORITY_GAP);
        marinePriorityMap.put(UnitType.Zerg_Mutalisk, inputPriority -= PRIORITY_GAP);

        // 맵 준비 끝
        PRIORITY_MAP.put(UnitType.Terran_Vulture, vulturePriorityMap);
        PRIORITY_MAP.put(UnitType.Terran_Siege_Tank_Tank_Mode, tankModePriorityMap);
        PRIORITY_MAP.put(UnitType.Terran_Siege_Tank_Siege_Mode, siegeModePriorityMap);
        PRIORITY_MAP.put(UnitType.Terran_Goliath, goliathPriorityMap);
        PRIORITY_MAP.put(UnitType.Terran_Wraith, wraithPriorityMap);
        PRIORITY_MAP.put(UnitType.Terran_Marine, marinePriorityMap);
    }

    public static int getPriority(Unit unit, Unit target) {
        int priority = TargetPriority.getPriority(unit.getType(), target.getType());

        // 마인 상태체크 테스트 코드
//		if (target.getType() == UnitType.Terran_Vulture_Spider_Mine) { // && target.isMoving()
//			if (!target.isBurrowed()) {
//				Monster.Broodwar.sendText("Spider_Mine is unBurrowed");
//			}
//			if (target.isDetected()) {
//				Monster.Broodwar.sendText("Spider_Mine is dectected");
//			}
//		}

        // 수리중이거나 건설중인 SCV
        if (target.getType() == UnitType.Terran_SCV && (target.isConstructing() || target.isRepairing())) {
            priority += 15;
        }

        return priority;
    }

    public static int getPriority(UnitType attacker, UnitType target) {
        Map<UnitType, Integer> map = PRIORITY_MAP.get(attacker);
        if (map == null) {
//			Monster.Broodwar.sendText("priority map is null");
            return 0;
        }

        Integer priority = map.get(target);
        if (priority == null) { // 지정되지 않은 건물 등
            return 10;
        }
        return priority;
    }
	
	/*
	private static Map<UnitType, Integer> inputBuildingPriorityWraith(Race race, Map<UnitType, Integer> priorityMap, int startPriority) {
		if (race == Race.Terran) {
			priorityMap.put(UnitType.Terran_Supply_Depot, 		startPriority);
			priorityMap.put(UnitType.Terran_Armory,				startPriority -= PRIORITY_GAP);
			priorityMap.put(UnitType.Terran_Factory,			startPriority -= PRIORITY_GAP);
			priorityMap.put(UnitType.Terran_Starport,			startPriority -= PRIORITY_GAP);
			priorityMap.put(UnitType.Terran_Missile_Turret,		startPriority -= PRIORITY_GAP);
			priorityMap.put(UnitType.Terran_Bunker,				startPriority -= PRIORITY_GAP);
			
		}
		return priorityMap;
	}
	*/

    private static Map<UnitType, Integer> inputBuildingPriority(Race race, Map<UnitType, Integer> priorityMap, int startPriority) {
        if (race == Race.Terran) {
            priorityMap.put(UnitType.Terran_Bunker, startPriority);
            priorityMap.put(UnitType.Terran_Missile_Turret, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Terran_Factory, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Terran_Supply_Depot, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Terran_Starport, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Terran_Armory, startPriority -= PRIORITY_GAP);

        } else if (race == Race.Protoss) {
            priorityMap.put(UnitType.Protoss_Photon_Cannon, startPriority);
            priorityMap.put(UnitType.Protoss_Pylon, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Protoss_Gateway, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Protoss_Templar_Archives, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Protoss_Stargate, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Protoss_Nexus, startPriority -= PRIORITY_GAP);

        } else if (race == Race.Zerg) {
            priorityMap.put(UnitType.Zerg_Nydus_Canal, startPriority);
            priorityMap.put(UnitType.Zerg_Sunken_Colony, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Zerg_Creep_Colony, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Zerg_Spore_Colony, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Zerg_Spire, startPriority -= PRIORITY_GAP);
            priorityMap.put(UnitType.Zerg_Spawning_Pool, startPriority -= PRIORITY_GAP);
        }
        return priorityMap;
    }

}
