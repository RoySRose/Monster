package org.monster.common.util;

import bwapi.Player;
import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.WeaponType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import org.monster.board.StrategyBoard;
import org.monster.build.base.BuildManager;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.internal.IConditions;
import org.monster.common.util.internal.SpecificValueCache;
import org.monster.decisions.constant.StrategyConfig;
import org.monster.decisions.strategy.manage.PositionFinder;
import org.monster.bootstrap.Monster;
import org.monster.micro.CombatManager;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.squad.Squad;
import org.monster.micro.targeting.TargetFilter;
import org.monster.worker.WorkerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UnitUtils {

    /**
     * 유효한 유닛인지 검사
     */
    public static boolean isValidUnit(Unit unit) {
        // unit.getHitPoints() > 0 || !unit.isDetected() 조건 제외
        return unit != null && unit.getType() != UnitType.Unknown && unit.exists() && unit.getPosition().isValid();
    }

    /**
     * 유효한 유닛인지 검사
     */
    public static boolean isCompleteValidUnit(Unit unit) {
        // unit.getHitPoints() > 0 || !unit.isDetected() 조건 제외
        return isValidUnit(unit) && unit.isCompleted();
    }

    /**
     * 시야에 있는 unitinfo이면 unit 정보 리턴
     */
    public static Unit unitInSight(UnitInfo eui) {
        Unit enemyUnit = Monster.Broodwar.getUnit(eui.getUnitID());
        if (UnitUtils.isValidUnit(enemyUnit)) {
            return enemyUnit;
        } else {
            return null;
        }
    }


    /**
     * 유닛 수 (constructionQueue가 포함된 검색인 경우, getUnitList.size()와 수가 다를 수 있다.)
     */
    public static int getUnitCount(UnitType... unitTypes) {
        int unitCount = 0;
        for (UnitType unitType : unitTypes) {
            unitCount += getUnitCount(CommonCode.UnitFindStatus.ALL, unitType);
        }
        return unitCount;
    }

    public static int getUnitCount(CommonCode.UnitFindStatus unitFindStatus, UnitType... unitTypes) {
        int unitCount = 0;
        for (UnitType unitType : unitTypes) {
            unitCount += getUnitCount(unitFindStatus, unitType);
        }
        return unitCount;
    }

    private static int getUnitCount(CommonCode.UnitFindStatus unitFindStatus, UnitType unitType) {
        switch (unitFindStatus) {
            case COMPLETE:
                return UnitCache.getCurrentCache().completeCount(unitType);
            case INCOMPLETE:
                return UnitCache.getCurrentCache().incompleteCount(unitType);
            case CONSTRUCTION_QUEUE:
                return UnitCache.getCurrentCache().underConstructionCount(unitType);
            case ALL:
                return UnitCache.getCurrentCache().allCount(unitType);
            case ALL_AND_CONSTRUCTION_QUEUE:
            default:
                if (unitType.isBuilding() && !unitType.isAddon()) {
                    return UnitCache.getCurrentCache().underConstructionCount(unitType) + UnitCache.getCurrentCache().completeCount(unitType);
                } else {
                    return UnitCache.getCurrentCache().incompleteCount(unitType) + UnitCache.getCurrentCache().completeCount(unitType);
                }

        }
    }

    public static List<Unit> getUnitList() {
        return UnitCache.getCurrentCache().allUnits(UnitType.AllUnits);
    }


    public static List<Unit> getUnitList(UnitType... unitTypes) {
        Set<Unit> unitSet = new HashSet<>();
        for (UnitType unitType : unitTypes) {
            unitSet.addAll(UnitCache.getCurrentCache().allUnits(unitType));
        }
        return new ArrayList<>(unitSet);
    }

    public static List<Unit> getUnitList(CommonCode.UnitFindStatus unitFindStatus) {
        return getUnitList(unitFindStatus, UnitType.AllUnits);
    }

    public static List<Unit> getUnitList(CommonCode.UnitFindStatus unitFindStatus, UnitType... unitTypes) {
        Set<Unit> unitSet = new HashSet<>();
        for (UnitType unitType : unitTypes) {
            unitSet.addAll(getUnitList(unitFindStatus, unitType));
        }
        return new ArrayList<>(unitSet);
    }

    private static List<Unit> getUnitList(CommonCode.UnitFindStatus unitFindStatus, UnitType unitType) {
        switch (unitFindStatus) {
            case COMPLETE:
                return UnitCache.getCurrentCache().completeUnits(unitType);
            case INCOMPLETE:
                return UnitCache.getCurrentCache().incompleteUnits(unitType);
            case CONSTRUCTION_QUEUE:
                return UnitCache.getCurrentCache().underConstructionUnits(unitType);
            case ALL:
            case ALL_AND_CONSTRUCTION_QUEUE:
            default:
                return UnitCache.getCurrentCache().allUnits(unitType);
        }
    }

    /**
     * 실제보유, 컨스트럭션큐, 빌드큐 포함 존재 여부
     */
//    public static boolean hasUnitOrWillBe(UnitType... unitTypes) {
//        if (getUnitCount(CommonCode.UnitFindStatus.ALL_AND_CONSTRUCTION_QUEUE, unitTypes) > 0) {
//            return true;
//        }
//        for (UnitType unitType : unitTypes) {
//            if (BuildManager.Instance().buildQueue.getItemCount(unitType) > 0) {
//                return true;
//            }
//        }
//        return false;
//    }
    public static int hasUnitOrWillBeCount(UnitType... unitTypes) {
        int unitCount = getUnitCount(CommonCode.UnitFindStatus.ALL_AND_CONSTRUCTION_QUEUE, unitTypes);
        for (UnitType unitType : unitTypes) {
            unitCount += BuildManager.Instance().buildQueue.getItemCount(unitType);
        }
        return unitCount;
    }

    /**
     * 적 유닛 리스트
     */
    private static int getEnemyUnitCount() {
        return UnitUtils.getEnemyUnitCount(UnitType.AllUnits);
    }

    public static int getEnemyUnitCount(UnitType... unitTypes) {
        int unitCount = 0;
        for (UnitType unitType : unitTypes) {
            unitCount += getEnemyUnitCount(unitType);
        }
        return unitCount;
    }

    private static int getEnemyUnitCount(UnitType unitType) {
        return UnitUtils.getEnemyUnitCount(unitType);
    }

    public static List<UnitInfo> getEnemyUnitInfoList(UnitType unitType) {
        return getEnemyUnitInfoList(CommonCode.EnemyUnitVisibleStatus.ALL, unitType);
    }

    public static List<UnitInfo> getEnemyUnitInfoList() {
        return getEnemyUnitInfoList(CommonCode.EnemyUnitVisibleStatus.ALL, UnitType.AllUnits);
    }

    public static List<UnitInfo> getEnemyUnitInfoList(CommonCode.EnemyUnitVisibleStatus enemyUnitVisibleStatus) {
        return getEnemyUnitInfoList(enemyUnitVisibleStatus, UnitType.AllUnits);
    }

    public static List<UnitInfo> getEnemyUnitInfoList(CommonCode.EnemyUnitVisibleStatus enemyUnitVisibleStatus, UnitType... unitTypes) {
        Set<UnitInfo> unitSet = new HashSet<>();
        for (UnitType unitType : unitTypes) {
            unitSet.addAll(getEnemyUnitInfoList(enemyUnitVisibleStatus, unitType));
        }
        return new ArrayList<>(unitSet);
    }

    private static List<UnitInfo> getEnemyUnitInfoList(CommonCode.EnemyUnitVisibleStatus enemyUnitVisibleStatus, UnitType unitType) {
        switch (enemyUnitVisibleStatus) {
            case VISIBLE:
                return UnitCache.getCurrentCache().enemyVisibleUnits(unitType);
            case ALL:
            default:
                return UnitCache.getCurrentCache().enemyAllUnits(unitType);
        }
    }

    public static void addEnemyUnitInfosInRadiusForEarlyDefense(Collection<UnitInfo> euis, Position position, int radius, UnitType... unitTypes) {
        addEnemyUnitInfosInRadius(TargetFilter.NO_FILTER, euis, position, radius, true, false, unitTypes);
    }

    public static void addEnemyUnitInfosInRadiusForGround(Collection<UnitInfo> euis, Position position, int radius, UnitType... unitTypes) {
        addEnemyUnitInfosInRadius(TargetFilter.UNFIGHTABLE, euis, position, radius, true, false, unitTypes);
    }

    public static void addEnemyUnitInfosInRadiusForAir(Collection<UnitInfo> euis, Position position, int radius, UnitType... unitTypes) {
        addEnemyUnitInfosInRadius(TargetFilter.UNFIGHTABLE, euis, position, radius, false, true, unitTypes);
    }

    public static void addEnemyUnitInfosInRadius(Collection<UnitInfo> euis, Position position, int radius, UnitType... unitTypes) {
        addEnemyUnitInfosInRadius(TargetFilter.NO_FILTER, euis, position, radius, true, true, unitTypes);
    }

    public static Set<UnitInfo> getEnemyUnitInfosInRadiusForGround(Position position, int radius, UnitType... unitTypes) {
        Set<UnitInfo> euis = new HashSet<>();
        addEnemyUnitInfosInRadius(TargetFilter.UNFIGHTABLE, euis, position, radius, true, false, unitTypes);
        return euis;
    }

    public static Set<UnitInfo> getAllEnemyUnitInfosInRadiusForGround(Position position, int radius, UnitType... unitTypes) {
        Set<UnitInfo> euis = new HashSet<>();
        addEnemyUnitInfosInRadius(TargetFilter.NO_FILTER, euis, position, radius, true, false, unitTypes);
        return euis;
    }

    public static Set<UnitInfo> getCompleteEnemyInfosInRadiusForAir(Position position, int radius, UnitType... unitTypes) {
        Set<UnitInfo> euis = new HashSet<>();
        addEnemyUnitInfosInRadius(TargetFilter.INCOMPLETE | TargetFilter.LARVA_LURKER_EGG | TargetFilter.UNFIGHTABLE, euis, position, radius, false, true, unitTypes);
        return euis;
    }

    public static Set<UnitInfo> getEnemyUnitInfosInRadius(int targetFilter, Position position, int radius, boolean addGroundWeoponRadius, boolean addAirWeoponRadius, UnitType... unitTypes) {
        Set<UnitInfo> euis = new HashSet<>();
        addEnemyUnitInfosInRadius(targetFilter, euis, position, radius, addGroundWeoponRadius, addAirWeoponRadius, unitTypes);
        return euis;
    }

    /**
     * position으로부터의 반경 radius이내에 있는 유닛정보를 enemyUnitInfoList에 세팅
     */
    public static void addEnemyUnitInfosInRadius(int targetFilter, Collection<UnitInfo> euis, Position position, int radius, boolean addGroundWeoponRadius, boolean addAirWeoponRadius, UnitType... unitTypes) {
        List<UnitInfo> values = null;
        if (unitTypes.length == 0) {
            values = UnitUtils.getEnemyUnitInfoList();
        } else {
            values = getEnemyUnitInfoList(CommonCode.EnemyUnitVisibleStatus.ALL, unitTypes);
        }

        for (UnitInfo eui : values) {
            if (euis.contains(eui)) {
                continue;
            }
            //TODO 기존 프리봇 TODO임. 이제는 확인 필요 -> unitinfo에 쓰레기 값 들어가는 오류 있음 information manager 확인필요 (아래는 임시 조치)
            if (eui.getUnitID() == 0 && eui.getType() == UnitType.None) {
                continue;
            }
            if (TargetFilter.excludeByFilter(eui, targetFilter)) {
//				String temp = ""; for (UnitType unitType : unitTypes) { temp += unitType.toString(); }
//				System.out.println(eui.getType() + " is filtered. targetFilter=" + targetFilter + "\n" + temp);
                continue;
            }
            if (ignorableEnemyUnitInfo(eui)) {
                continue;
            }

            int weaponRange = 0; // radius 안의 공격범위가 닿는 적까지 포함

            if (eui.getType() == UnitType.Terran_Bunker) {
                weaponRange = Monster.Broodwar.enemy().weaponMaxRange(UnitType.Terran_Marine.groundWeapon()) + 96;
            } else {
                if (addGroundWeoponRadius) {
                    if (eui.getType().groundWeapon() != WeaponType.None) {
                        weaponRange = Math.max(weaponRange, Monster.Broodwar.enemy().weaponMaxRange(eui.getType().groundWeapon()));
                    }
                }
                if (addAirWeoponRadius) {
                    if (eui.getType().airWeapon() != WeaponType.None) {
                        weaponRange = Monster.Broodwar.enemy().weaponMaxRange(eui.getType().airWeapon());
                    }
                }
            }

            if (eui.getLastPosition().getDistance(position) < radius + weaponRange) {
                euis.add(eui);
            }
        }
    }

    /**
     * position 근처의 유닛리스트를 리턴
     */
    public static List<Unit> getUnitsInRadius(CommonCode.PlayerRange playerRange, Position position, int radius) {
        return getUnitsInRadius(playerRange, position, radius, UnitType.AllUnits);
    }

    /**
     * position 근처의 유닛리스트를 리턴
     */
    public static List<Unit> getUnitsInRadius(CommonCode.PlayerRange playerRange, Position position, int radius, UnitType... unitTypes) {
        Player player = null;
        if (playerRange == CommonCode.PlayerRange.SELF) {
            player = Monster.Broodwar.self();
        } else if (playerRange == CommonCode.PlayerRange.ENEMY) {
            player = Monster.Broodwar.enemy();
        } else if (playerRange == CommonCode.PlayerRange.NEUTRAL) {
            player = Monster.Broodwar.neutral();
        }

        List<Unit> unitsInRadius = new ArrayList<>();
        for (Unit unit : Monster.Broodwar.getUnitsInRadius(position, radius)) {
            if (player != null && player != unit.getPlayer()) {
                continue;
            }
            boolean isSearchingUnitType = false;
            for (UnitType unitType : unitTypes) {
                if (unitType == UnitType.AllUnits || unit.getType() == unitType) {
                    isSearchingUnitType = true;
                    break;
                }
            }
            if (isSearchingUnitType) {
                unitsInRadius.add(unit);
            }
        }
        return unitsInRadius;
    }

    /**
     * position 근처의 유닛리스트를 리턴
     */
//    public static void addUnitsInRadius(Collection<Unit> units, CommonCode.PlayerRange playerRange, Position position, int radius) {
//        Player player = PlayerUtils.getPlayerByRange(playerRange);
//        for (Unit unit : Monster.Broodwar.getUnitsInRadius(position, radius)) {
//            if (player != null && player != unit.getPlayer()) {
//                continue;
//            }
//            units.add(unit);
//        }
//    }
    public static List<Unit> getUnitsInRegion(CommonCode.RegionType regionType, CommonCode.PlayerRange playerRange) {
        return getUnitsInRegion(regionType, playerRange, new IConditions.UnitCondition() {
            @Override
            public boolean correspond(Unit unit) {
                return true;
            }
        });
    }

    public static List<Unit> myBuildingsInMainSquadRegion() {
        List<Unit> totalBuildings = UnitUtils.getUnitList(CommonCode.UnitFindStatus.ALL
                , UnitType.Terran_Command_Center, UnitType.Terran_Supply_Depot, UnitType.Terran_Barracks, UnitType.Terran_Factory, UnitType.Terran_Starport
                , UnitType.Terran_Armory, UnitType.Terran_Academy, UnitType.Terran_Bunker, UnitType.Terran_Missile_Turret);

        List<Unit> buildingsInRegion = new ArrayList<>();
        Region baseRegion = PositionUtils.regionTypeToRegion(CommonCode.RegionType.MY_BASE);
        Region expansionRegion = PositionUtils.regionTypeToRegion(CommonCode.RegionType.MY_FIRST_EXPANSION);
        Region thirdRegion = PositionUtils.regionTypeToRegion(CommonCode.RegionType.MY_THIRD_REGION);

        for (Unit unit : totalBuildings) {
            if (UnitUtils.isValidUnit(unit)) {
                Region buildingRegion = BWTA.getRegion(unit.getPosition());
                if (buildingRegion == baseRegion
                        || buildingRegion == expansionRegion
                        || buildingRegion == thirdRegion) {
                    buildingsInRegion.add(unit);
                }
            }
        }
        return buildingsInRegion;
    }

    public static List<Unit> getUnitsInRegion(CommonCode.RegionType regionType, CommonCode.PlayerRange playerRange, IConditions.UnitCondition unitCondition) {
        List<Unit> totalUnits = null;
        if (playerRange == CommonCode.PlayerRange.SELF) {
            totalUnits = Monster.Broodwar.self().getUnits();
        } else if (playerRange == CommonCode.PlayerRange.ENEMY) {
            totalUnits = Monster.Broodwar.enemy().getUnits();
        } else if (playerRange == CommonCode.PlayerRange.NEUTRAL) {
            totalUnits = Monster.Broodwar.neutral().getUnits();
        } else {
            totalUnits = Monster.Broodwar.getAllUnits();
        }

        List<Unit> unitsInRegion = new ArrayList<>();
        Region region = PositionUtils.regionTypeToRegion(regionType);
        for (Unit unit : totalUnits) {
            if (UnitUtils.isValidUnit(unit) && unitCondition.correspond(unit)) {
                if (region == BWTA.getRegion(unit.getPosition())) {
                    unitsInRegion.add(unit);
                }
            }
        }
        return unitsInRegion;
    }

    /// 유닛타입의 아군 유닛이 하나 생산되었는지 여부
    public static boolean myUnitDiscovered(UnitType... unitTypes) {
        Map<UnitType, Boolean> selfUnitDiscoveredMap = UnitCache.getCurrentCache().selfUnitDiscovered();
        return unitDiscovered(selfUnitDiscoveredMap, unitTypes);
    }

    /// 유닛타입의 아군 유닛이 하나 생산되었는지 여부 (완성)
    public static boolean myCompleteUnitDiscovered(UnitType... unitTypes) {
        Map<UnitType, Boolean> selfUnitDiscoveredMap = UnitCache.getCurrentCache().selfCompleteUnitDiscovered();
        return unitDiscovered(selfUnitDiscoveredMap, unitTypes);
    }

    /// 유닛타입의 적 유닛이 하나 생산되었는지 여부
    public static boolean enemyUnitDiscovered(UnitType... unitTypes) {
        Map<UnitType, Boolean> enemyUnitDiscoveredMap = UnitCache.getCurrentCache().enemyUnitDiscovered();
        return unitDiscovered(enemyUnitDiscoveredMap, unitTypes);
    }

    public static boolean invisibleEnemyDiscovered() {
        Race enemyRace = PlayerUtils.enemyRace();
        if (enemyRace == Race.Protoss) {
            return enemyUnitDiscovered(UnitType.Protoss_Dark_Templar, UnitType.Protoss_Templar_Archives, UnitType.Protoss_Citadel_of_Adun, UnitType.Protoss_Arbiter, UnitType.Protoss_Arbiter_Tribunal);
        } else if (enemyRace == Race.Terran) {
            return enemyUnitDiscovered(UnitType.Terran_Wraith, UnitType.Terran_Starport, UnitType.Terran_Control_Tower, UnitType.Terran_Ghost, UnitType.Terran_Vulture_Spider_Mine);
        } else if (enemyRace == Race.Zerg) {
            return enemyUnitDiscovered(UnitType.Zerg_Lurker, UnitType.Zerg_Lurker_Egg);
        }
        return false;
    }

    /// 유닛타입의 적 유닛이 하나 생산되었는지 여부 (완성)
    public static boolean enemyCompleteUnitDiscovered(UnitType... unitTypes) {
        Map<UnitType, Boolean> enemyUnitDiscoveredMap = UnitCache.getCurrentCache().enemyCompleteUnitDiscovered();
        return unitDiscovered(enemyUnitDiscoveredMap, unitTypes);
    }

    private static boolean unitDiscovered(Map<UnitType, Boolean> unitDiscoveredMap, UnitType... unitTypes) {
        for (UnitType unitType : unitTypes) {
            Boolean discovered = unitDiscoveredMap.get(unitType);
            if (discovered != null && discovered == Boolean.TRUE) {
                return true;
            }
        }
        return false;
    }

    /**
     * 시야에서 사라진지 N초가 경과하여 무시할 수 있다고 판단되면 true 리턴
     */
    public static boolean ignorableEnemyUnitInfo(UnitInfo eui) {
        int ignoreSeconds;
        if (eui.getType() == UnitType.Terran_Siege_Tank_Siege_Mode || eui.getType() == UnitType.Terran_Siege_Tank_Tank_Mode) {
//			TilePosition tilePosition = eui.getLastPosition().toTilePosition();
//			TilePosition t1 = new TilePosition(tilePosition.getX()-2, tilePosition.getY()).makeValid();
//			TilePosition t2 = new TilePosition(tilePosition.getX()+2, tilePosition.getY()).makeValid();
//			TilePosition t3 = new TilePosition(tilePosition.getX(), tilePosition.getY()-2).makeValid();
//			TilePosition t4 = new TilePosition(tilePosition.getX(), tilePosition.getY()+2).makeValid();
//			if (Prebot.Broodwar.isVisible(t1) && Prebot.Broodwar.isVisible(t2) && Prebot.Broodwar.isVisible(t3) && Prebot.Broodwar.isVisible(t4)) {
//				ignoreSeconds = StrategyConfig.IGNORE_ENEMY_UNITINFO_SECONDS;
//			} else {
            ignoreSeconds = StrategyConfig.IGNORE_ENEMY_SIEGE_TANK_SECONDS;
//			}
        } else {
            ignoreSeconds = StrategyConfig.IGNORE_ENEMY_UNITINFO_SECONDS;
        }
        boolean ignorable = ignorableEnemyUnitInfo(eui, ignoreSeconds);
//		if (ignorable) {
//			if (eui.getType() == UnitType.Terran_Siege_Tank_Siege_Mode || eui.getType() == UnitType.Terran_Siege_Tank_Tank_Mode) {
//				System.out.println("ignorable : " + eui);
//			}
//		}
        return ignorable;
    }

    public static boolean ignorableEnemyUnitInfo(UnitInfo eui, int ignoreSeconds) {
        return !eui.getType().isBuilding() && TimeUtils.elapsedSeconds(eui.getUpdateFrame()) >= ignoreSeconds;
    }

    /**
     * 즉시 생산할 수 있는 상태인지 판단
     */
    public static boolean isProduceableImmediately(UnitType unitType) {
        // 생산할 수 있는 자원이 있어야 한다.
        if (Monster.Broodwar.self().minerals() < unitType.mineralPrice()
                && Monster.Broodwar.self().gas() < unitType.gasPrice()) {
            return false;
        }

        // 서플라이가 있어야 한다.
        if (unitType.supplyRequired() > 0) {
            int supplySpace = Monster.Broodwar.self().supplyTotal() - Monster.Broodwar.self().supplyUsed();
            if (supplySpace < unitType.supplyRequired()) {
                return false;
            }
        }

        return Monster.Broodwar.canMake(unitType);
    }

    /**
     * unitList 중 position에 가장 가까운 유닛 리턴
     */
    public static Unit getClosestUnitToPosition(Collection<Unit> unitList, Position position) {
        return getClosestUnitToPosition(unitList, position, new IConditions.UnitCondition() {
            @Override
            public boolean correspond(Unit unit) {
                return true;
            }
        });
    }

    /**
     * unitList 중 position에 가장 가까운 유닛타입 유닛 리턴
     */
    public static Unit getClosestUnitToPosition(Collection<Unit> unitList, Position position, final UnitType... unitTypes) {
        return getClosestUnitToPosition(unitList, position, new IConditions.UnitCondition() {
            @Override
            public boolean correspond(Unit unit) {
                for (UnitType unitType : unitTypes) {
                    if (unitType == unit.getType()) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static Unit getClosestUnitToPositionNotInMyBase(Collection<Unit> unitList, Position position, final UnitType... unitTypes) {
        return getClosestUnitToPosition(unitList, position, new IConditions.UnitCondition() {
            @Override
            public boolean correspond(Unit unit) {
                Region baseRegion = BWTA.getRegion(BaseUtils.myMainBase().getPosition());
                Region unitRegion = BWTA.getRegion(unit.getPosition());
                if (baseRegion == unitRegion) {
                    return false;
                }

                for (UnitType unitType : unitTypes) {
                    if (unitType == unit.getType()) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static Unit getClosestUnitToPositionNotStunned(Collection<Unit> unitList, Position position) {
        return getClosestUnitToPosition(unitList, position, new IConditions.UnitCondition() {
            @Override
            public boolean correspond(Unit unit) {
                return !unit.isStasised() && !unit.isLockedDown();
            }
        });
    }

    /**
     * unitList 중 position에 가장 가까운 미네랄 일꾼 리턴
     */
    public static Unit getClosestMineralWorkerToPosition(Collection<Unit> unitList, Position position) {
        return getClosestUnitToPosition(unitList, position, new IConditions.UnitCondition() {
            @Override
            public boolean correspond(Unit unit) {
                return unit.getType().isWorker() && WorkerManager.Instance().isMineralWorker(unit) && !unit.isCarryingMinerals();
            }
        });
    }

    public static Unit getClosestCombatWorkerToPosition(Collection<Unit> unitList, Position position) {
        return getClosestUnitToPosition(unitList, position, new IConditions.UnitCondition() {
            @Override
            public boolean correspond(Unit unit) {
                return unit.getType().isWorker() && WorkerManager.Instance().isCombatWorker(unit);
            }
        });
    }

    public static Unit getClosestUnitToPositionNotInSet(Collection<Unit> unitList, Position position, Set<Integer> unitIds) {
        return getClosestUnitToPosition(unitList, position, new IConditions.UnitCondition() {
            @Override
            public boolean correspond(Unit unit) {
                return !unitIds.contains(unit.getID());
            }
        });
    }

    public static Unit getClosestActivatedCommandCenter(Position position) {
        List<Unit> commandCenters = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Command_Center);
        return getClosestUnitToPosition(commandCenters, position, new IConditions.UnitCondition() {
            @Override
            public boolean correspond(Unit commandCenter) {
                return WorkerManager.Instance().getWorkerData().getNumAssignedWorkers(commandCenter) > 6;
            }
        });
    }

    public static Unit getFarthestCombatWorkerToPosition(Collection<Unit> unitList, Position position) {
        return getFarthestUnitToPosition(unitList, position, new IConditions.UnitCondition() {
            @Override
            public boolean correspond(Unit unit) {
                return unit.getType().isWorker() && WorkerManager.Instance().isCombatWorker(unit);
            }
        });
    }

    /**
     * unitList 중 position에 조건(unitCondition)에 부합하는 가장 가까운 유닛 리턴
     */
    public static Unit getClosestUnitToPosition(Collection<Unit> unitList, Position position, IConditions.UnitCondition unitCondition) {
        if (unitList.size() == 0) {
            return null;
        }
        if (!PositionUtils.isValidPosition(position)) {
            return unitList.iterator().next();
        }

        Unit closestUnit = null;
        double closestDist = CommonCode.DOUBLE_MAX;

        for (Unit unit : unitList) {
            if (!UnitUtils.isValidUnit(unit) || !unitCondition.correspond(unit)) {
                continue;
            }
            double dist = unit.getDistance(position);
            if (closestUnit == null || dist < closestDist) {
                closestUnit = unit;
                closestDist = dist;
            }
        }
        return closestUnit;
    }

    /**
     * unitList 중 position에 조건(unitCondition)에 부합하는 가장 가까운 유닛 리턴
     */
    private static Unit getFarthestUnitToPosition(Collection<Unit> unitList, Position position, IConditions.UnitCondition unitCondition) {
        if (unitList.size() == 0) {
            return null;
        }
        if (!PositionUtils.isValidPosition(position)) {
            return unitList.iterator().next();
        }

        Unit farthesetUnit = null;
        double farthestDist = 0.0;

        for (Unit unit : unitList) {
            if (!UnitUtils.isValidUnit(unit) || !unitCondition.correspond(unit)) {
                continue;
            }
            double dist = unit.getDistance(position);
            if (farthesetUnit == null || dist > farthestDist) {
                farthesetUnit = unit;
                farthestDist = dist;
            }
        }
        return farthesetUnit;
    }

    public static Map<UnitType, List<Unit>> makeUnitListMap(Collection<Unit> sourceUnitList) {
        Map<UnitType, List<Unit>> unitListMap = new HashMap<>();
        for (Unit unit : sourceUnitList) {
            List<Unit> unitList = unitListMap.get(unit.getType());
            if (unitList == null) {
                unitList = new ArrayList<>();
            }
            unitList.add(unit);
            unitListMap.put(unit.getType(), unitList);
        }
        return unitListMap;
    }

    /////////////////////////////////////////// 기본 제공 메서드 ////////////////////////////////////////////////////////////

    public static int myUnitSupplyCount(UnitType... unitTypes) {
        int totalCount = 0;
        for (UnitType unitType : unitTypes) {
            int unitCount = UnitUtils.getUnitCount(unitType);
            totalCount += unitCount * unitType.supplyRequired();
        }
        return totalCount;
    }

    public static Unit leaderOfUnit(Collection<Unit> unitList) {
        if (unitList == null || unitList.isEmpty()) {
            return null;
        }
        if (BaseUtils.enemyMainBase() == null) {
            return null;
        }

        Position goalPosition;
        if (StrategyBoard.mainSquadMode.isAttackMode || StrategyBoard.campType == PositionFinder.CampType.READY_TO) {
            boolean expansionOccupied = false;
            List<BaseLocation> enemyBases = BaseUtils.enemyOccupiedBases();
            for (BaseLocation enemyBase : enemyBases) {
                if (enemyBase.equals(BaseUtils.enemyFirstExpansion())) {
                    expansionOccupied = true;
                    break;
                }
            }
            if (expansionOccupied) {
                goalPosition = BaseUtils.enemyFirstExpansion().getPosition();
            } else {
                goalPosition = BaseUtils.enemyMainBase().getPosition();
            }

        } else {
            goalPosition = PositionUtils.myReadyToPosition();
        }

        Unit leader = UnitUtils.getClosestUnitToPositionNotInMyBase(unitList, goalPosition, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode);
        if (leader == null) {
            leader = UnitUtils.getClosestUnitToPosition(unitList, goalPosition, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode);
        }
        if (leader == null) {
            leader = UnitUtils.getClosestUnitToPosition(unitList, goalPosition, UnitType.Terran_Goliath);
        }
        return leader;
    }

    public static Position centerPositionOfUnit(Collection<Unit> unitList, Position leaderPosition, int limitDistance) {
        int count = 0;
        int x = 0;
        int y = 0;
        for (Unit unit : unitList) {
            if (!UnitUtils.isValidUnit(unit)) {
                continue;
            }
            if (unit.getDistance(leaderPosition) > limitDistance) {
                continue;
            }

            count++;
            x += unit.getPosition().getX();
            y += unit.getPosition().getY();
        }
        if (count > 0) {
            return new Position(x / count, y / count);
        } else {
            return null;
        }
    }

    public static Position centerPositionOfUnitInfo(Collection<UnitInfo> euiList, Position leaderPosition, int limitDistance) {
        int count = 0;
        int x = 0;
        int y = 0;
        for (UnitInfo eui : euiList) {
            Unit unitInSight = UnitUtils.unitInSight(eui);
            if (unitInSight == null) {
                continue;
            }

            if (unitInSight.getDistance(leaderPosition) > limitDistance) {
                continue;
            }

            count++;
            x += unitInSight.getPosition().getX();
            y += unitInSight.getPosition().getY();
        }
        if (count > 0) {
            return new Position(x / count, y / count);
        } else {
            return null;
        }
    }

    public static UnitType[] enemyAirDefenseUnitType() {
        if (PlayerUtils.enemyRace() == Race.Protoss) {
            return new UnitType[]{UnitType.Protoss_Photon_Cannon};
        } else if (PlayerUtils.enemyRace() == Race.Zerg) {
            return new UnitType[]{UnitType.Zerg_Spore_Colony};
        } else if (PlayerUtils.enemyRace() == Race.Terran) {
            return new UnitType[]{UnitType.Terran_Missile_Turret, UnitType.Terran_Bunker};
        } else {
            return new UnitType[]{};
        }
    }

    public static UnitType[] wraithKillerUnitType() {
        if (PlayerUtils.enemyRace() == Race.Protoss) {
            return new UnitType[]{UnitType.Protoss_Dragoon, UnitType.Protoss_Archon};
        } else if (PlayerUtils.enemyRace() == Race.Zerg) {
            return new UnitType[]{UnitType.Zerg_Hydralisk, UnitType.Zerg_Scourge};
        } else if (PlayerUtils.enemyRace() == Race.Terran) {
            return new UnitType[]{UnitType.Terran_Goliath};
        } else {
            return new UnitType[]{};
        }
    }

    public static int myWraithUnitSupplyCount() {
        int totalSupplyCount = UnitUtils.getUnitCount(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Wraith);
        return totalSupplyCount * 4; // 인구수 기준이므로
    }

    public static int myFactoryUnitSupplyCount() {
        Integer factorySupplyCount = SpecificValueCache.get(SpecificValueCache.ValueType.FACTORY_SUPPLY_COUNT, Integer.class);
        if (factorySupplyCount != null) {
            return factorySupplyCount;
        }
        int factoryUnitCount = UnitUtils.getUnitCount(CommonCode.UnitFindStatus.COMPLETE
                , UnitType.Terran_Vulture, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_Goliath);


        factorySupplyCount = factoryUnitCount * 4; // 인구수 기준이므로;
        SpecificValueCache.put(SpecificValueCache.ValueType.FACTORY_SUPPLY_COUNT, factorySupplyCount);
        return factorySupplyCount;
    }

    public static int activatedCommandCenterCount() {
        Integer activatedCommandCount = SpecificValueCache.get(SpecificValueCache.ValueType.ACTIVATED_COMMAND_COUNT, Integer.class);
        if (activatedCommandCount != null) {
//			System.out.println("cacahed data " + activatedCommandCount);
            return activatedCommandCount;
        }
        activatedCommandCount = 0;
        List<Unit> commandCenters = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Command_Center);
        for (Unit commandCenter : commandCenters) {
            if (WorkerManager.Instance().getWorkerData().getNumAssignedWorkers(commandCenter) > 6) {
                activatedCommandCount++;
            }
        }

        SpecificValueCache.put(SpecificValueCache.ValueType.ACTIVATED_COMMAND_COUNT, activatedCommandCount);
        return activatedCommandCount;
    }

    public static int availableScanningCount() {
        Integer availableScanningCount = SpecificValueCache.get(SpecificValueCache.ValueType.AVAILABLE_SCANNING_COUNT, Integer.class);
        if (availableScanningCount != null) {
            return availableScanningCount;
        }
        availableScanningCount = 0;
        List<Unit> comsatStations = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Comsat_Station);
        for (Unit comsatStation : comsatStations) {
            availableScanningCount += comsatStation.getEnergy() / 48; // 다크에게 컴셋 전 앞서 전진하게 위해 에너지를 2초 짧게 잡는다.
        }
        SpecificValueCache.put(SpecificValueCache.ValueType.AVAILABLE_SCANNING_COUNT, availableScanningCount);
        return availableScanningCount;
    }

    public static int enemyGroundUnitPower() {
        Integer enemyGroundUnitPower = SpecificValueCache.get(SpecificValueCache.ValueType.ENEMY_GROUND_UNIT_POWER, Integer.class);
        if (enemyGroundUnitPower != null) {
            return enemyGroundUnitPower;
        }
        enemyGroundUnitPower = 0;
        if (PlayerUtils.enemyRace() == Race.Zerg) {
            int hydraCount = UnitUtils.getEnemyUnitCount(UnitType.Zerg_Hydralisk);
            int lurkerCount = UnitUtils.getEnemyUnitCount(UnitType.Zerg_Lurker, UnitType.Zerg_Lurker_Egg);
            int ultraCount = UnitUtils.getEnemyUnitCount(UnitType.Zerg_Ultralisk);
            int defilerCount = UnitUtils.getEnemyUnitCount(UnitType.Zerg_Defiler);

            enemyGroundUnitPower = (hydraCount * 1) + (lurkerCount * 2) + (ultraCount * 5) + (defilerCount * 3);

        } else if (PlayerUtils.enemyRace() == Race.Protoss) {
            int zealotCount = UnitUtils.getEnemyUnitCount(UnitType.Protoss_Zealot);
            int dragoonCount = UnitUtils.getEnemyUnitCount(UnitType.Protoss_Dragoon);
            int darkCount = UnitUtils.getEnemyUnitCount(UnitType.Protoss_Dark_Templar);
            int highCount = UnitUtils.getEnemyUnitCount(UnitType.Protoss_High_Templar);
            int archonCount = UnitUtils.getEnemyUnitCount(UnitType.Protoss_Archon);

            enemyGroundUnitPower = zealotCount + dragoonCount + darkCount + highCount + archonCount;

        } else if (PlayerUtils.enemyRace() == Race.Terran) {
            int marineCount = UnitUtils.getEnemyUnitCount(UnitType.Terran_Marine);
            int vultureCount = UnitUtils.getEnemyUnitCount(UnitType.Terran_Vulture);
            int goliathCount = UnitUtils.getEnemyUnitCount(UnitType.Terran_Goliath);
            int tankCount = UnitUtils.getEnemyUnitCount(UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode);

            enemyGroundUnitPower = (marineCount * 1) + (vultureCount * 1) + (goliathCount * 2) + (tankCount * 3);
        }

        SpecificValueCache.put(SpecificValueCache.ValueType.ENEMY_GROUND_UNIT_POWER, enemyGroundUnitPower);
        return enemyGroundUnitPower;
    }

    public static int enemyAirUnitPower() {
        Integer enemyAirUnitPower = SpecificValueCache.get(SpecificValueCache.ValueType.ENEMY_AIR_UNIT_POWER, Integer.class);
        if (enemyAirUnitPower != null) {
            return enemyAirUnitPower;
        }
        enemyAirUnitPower = 0;
        if (PlayerUtils.enemyRace() == Race.Zerg) {
            int mutalCount = UnitUtils.getEnemyUnitCount(UnitType.Zerg_Mutalisk);
            int guardianCount = UnitUtils.getEnemyUnitCount(UnitType.Zerg_Guardian);

            enemyAirUnitPower = (mutalCount * 1) + (guardianCount * 4);

        } else if (PlayerUtils.enemyRace() == Race.Protoss) {
            int scoutCount = UnitUtils.getEnemyUnitCount(UnitType.Protoss_Scout);
            int carrierCount = UnitUtils.getEnemyUnitCount(UnitType.Protoss_Carrier);
            int arbiterCount = UnitUtils.getEnemyUnitCount(UnitType.Protoss_Arbiter);

            enemyAirUnitPower = (scoutCount * 1) + (carrierCount * 4) + (arbiterCount * 2);

        } else if (PlayerUtils.enemyRace() == Race.Terran) {
            int wraithCount = UnitUtils.getEnemyUnitCount(UnitType.Terran_Wraith);
//			int valkyrieCount = UnitUtils.getEnemyUnitCount(UnitType.Terran_Valkyrie);
//			int dropshipCount = UnitUtils.getEnemyUnitCount(UnitType.Terran_Dropship);
            int battleCount = UnitUtils.getEnemyUnitCount(UnitType.Terran_Battlecruiser);

            enemyAirUnitPower = wraithCount + (battleCount * 4);
        }

        SpecificValueCache.put(SpecificValueCache.ValueType.ENEMY_AIR_UNIT_POWER, enemyAirUnitPower);
        return enemyAirUnitPower;
    }

    public static int myDeadNumUnits(UnitType... unitTypes) {
        int numUnits = 0;
        for (UnitType unitType : unitTypes) {
            numUnits += Monster.Broodwar.self().deadUnitCount(unitType);
        }
        return numUnits;
    }


    public static int enemyDeadNumUnits(UnitType... unitTypes) {
        int numUnits = 0;
        for (UnitType unitType : unitTypes) {
            numUnits += Monster.Broodwar.enemy().deadUnitCount(unitType);
        }
        return numUnits;
    }

    public static List<UnitInfo> euiListInMyRegion(Region myRegion) {

        List<UnitInfo> euiListInMyRegion = UnitInRegionInfoCollector.Instance().getEuiListInMyRegion(myRegion);
        if (euiListInMyRegion == null) {
            euiListInMyRegion = new ArrayList<>();
        }

        return euiListInMyRegion;
    }

    public static Set<UnitInfo> euiListInBase() {
        return UnitInRegionInfoCollector.Instance().getEuisInMainBaseRegion();
    }

    public static Set<UnitInfo> euiListInExpansion() {
        return UnitInRegionInfoCollector.Instance().getEuisInExpansionRegion();
    }

    public static Set<UnitInfo> euiListInThirdRegion() {
        return UnitInRegionInfoCollector.Instance().getEuisInThirdRegion();
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

    public static int squadUnitSize(MicroConfig.SquadInfo squadInfo) {
        Squad squad = CombatManager.Instance().squadData.getSquad(squadInfo.squadName);
        if (squad != null && squad.unitList != null) {
            return squad.unitList.size();
        } else {
            return 0;
        }
    }
}