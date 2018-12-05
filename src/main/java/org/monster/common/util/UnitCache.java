package org.monster.common.util;

import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import org.monster.bootstrap.Monster;
import org.monster.build.base.ConstructionManager;
import org.monster.build.base.ConstructionTask;
import org.monster.common.MapGrid;
import org.monster.common.UnitInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/// 유닛 리스트를 부하가 걸리지 않도록 관리
public class UnitCache implements InfoCollector {

    private static UnitCache unitCache = new UnitCache();
    // 아군 UnitType 발견 여부
    private Map<UnitType, Boolean> selfUnitDiscovered = new HashMap<>();
    private Map<UnitType, Boolean> selfCompleteUnitDiscovered = new HashMap<>();
    // 아군 모든 UnitType 리스트
    private List<Unit> selfCompleteUnitList = new ArrayList<>(); /// 아군 완성 유닛 리스트
    private List<Unit> selfIncompleteUnitList = new ArrayList<>(); /// 아군 미완성 유닛 리스트
    private List<ConstructionTask> selfConstructionTaskList = new ArrayList<>(); /// 아군 건설큐의 건물 리스트
    // 아군 UnitType별 맵
    private Map<UnitType, List<Unit>> selfAllUnitByTypeMap = new HashMap<>(); /// 아군 모든 유닛
    private Map<UnitType, List<Unit>> selfCompleteUnitByTypeMap = new HashMap<>(); /// 아군 완성 유닛
    private Map<UnitType, List<Unit>> selfIncompleteUnitByTypeMap = new HashMap<>(); /// 아군 미완성 유닛
    private Map<UnitType, List<ConstructionTask>> selfConstructionTaskMap = new HashMap<>(); /// 아군 건설큐의 건물
    // 적군  UnitType 발견 여부
    private Map<UnitType, Boolean> enemyUnitDiscovered = new HashMap<>();
    private Map<UnitType, Boolean> enemyCompleteUnitDiscovered = new HashMap<>();
    //TODO completed, incompleted enemey unit list 가 필요할까?
    // 적군 맵
    private Map<Integer, UnitInfo> enemyAllUnitInfoMap = new HashMap<>(); /// 적군 모든 유닛정보
    // 적군 UnitType별 맵
    private Map<UnitType, List<UnitInfo>> enemyAllUnitInfoByTypeMap = new HashMap<>(); /// 적군 모든 유닛정보
    private Map<UnitType, List<UnitInfo>> enemyVisibleUnitInfoByTypeMap = new HashMap<>(); /// 보이는 적군 유닛정보
    private int supplyUsed;
    private Vector<Integer> badUnitstoRemove = new Vector<Integer>();
    private Game Broodwar;

    public static UnitCache getCurrentCache() {
        return unitCache;
    }
    ////////////////////// 아군 유닛 리스트 관련

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
    }

    @Override
    public void update() {
        clearAll();
        putSelfData();
        //putEnemyData();
        removeBadUnits();
        updateDiscoveredMap();

        //TODO issue 11?
        cacheToUnmodifiable();
    }

    private void cacheToUnmodifiable() {
    }

    protected Map<UnitType, Boolean> selfUnitDiscovered() {
        return selfUnitDiscovered;
    }

    protected Map<UnitType, Boolean> selfCompleteUnitDiscovered() {
        return selfCompleteUnitDiscovered;
    }

    protected int allCount(UnitType unitType) {
        if (unitType == UnitType.AllUnits) {
            return Broodwar.self().getUnits().size();
        } else {
            return selfAllUnitByTypeMap.getOrDefault(unitType, new ArrayList<>()).size();
        }
    }

    protected List<Unit> allUnits(UnitType unitType) {
        if (unitType == UnitType.AllUnits) {
            return Broodwar.self().getUnits();
        } else {
            return selfAllUnitByTypeMap.getOrDefault(unitType, new ArrayList<>());
        }
    }

    protected int completeCount(UnitType unitType) {
        if (unitType == UnitType.AllUnits) {
            return selfCompleteUnitList.size();
        } else {
            return selfCompleteUnitByTypeMap.getOrDefault(unitType, new ArrayList<>()).size();
        }
    }

    protected List<Unit> completeUnits(UnitType unitType) {
        if (unitType == UnitType.AllUnits) {
            return selfCompleteUnitList;
        } else {
            return selfCompleteUnitByTypeMap.getOrDefault(unitType, new ArrayList<>());
        }
    }

    protected int incompleteCount(UnitType unitType) {
        if (unitType == UnitType.AllUnits) {
            return selfIncompleteUnitList.size();
        } else {
            return selfIncompleteUnitByTypeMap.getOrDefault(unitType, new ArrayList<>()).size();
        }
    }

    protected List<Unit> incompleteUnits(UnitType unitType) {
        if (unitType == UnitType.AllUnits) {
            return selfIncompleteUnitList;
        } else {
            return selfIncompleteUnitByTypeMap.getOrDefault(unitType, new ArrayList<>());
        }
    }

    protected int underConstructionCount(UnitType unitType) {
        if (unitType == UnitType.AllUnits) {
            return selfConstructionTaskList.size();
        } else {
            return selfConstructionTaskMap.getOrDefault(unitType, new ArrayList<>()).size();
        }
    }

    //TODO 필요한가?
    // 실제 construction queue의 사이즈보다 유닛리스트의 수가 적을 수 있다.(건설시작 전인 빌딩이 있을수 있으므로)
    protected List<Unit> underConstructionUnits(UnitType unitType) {
        List<Unit> unitList = new ArrayList<>();
        List<ConstructionTask> tasks;
        if (unitType == UnitType.AllUnits) {
            tasks = selfConstructionTaskList;
        } else {
            tasks = selfConstructionTaskMap.get(unitType);
        }

        for (ConstructionTask task : tasks) {
            if (task.getBuildingUnit() != null) {
                unitList.add(task.getBuildingUnit());
            }
        }
        return unitList;
    }

    ////////////////////// 적군 유닛정보 리스트 관련

    protected Map<UnitType, Boolean> enemyUnitDiscovered() {
        return enemyUnitDiscovered;
    }

    protected Map<UnitType, Boolean> enemyCompleteUnitDiscovered() {
        return enemyCompleteUnitDiscovered;
    }

    protected int enemyAllCount(UnitType unitType) {
        if (unitType == UnitType.AllUnits) {
            return enemyAllUnitInfoMap.size();
        } else {
            return enemyAllUnitInfoByTypeMap.getOrDefault(unitType, new ArrayList<>()).size();
        }
    }

    protected List<UnitInfo> enemyAllUnits(UnitType unitType) {
        if (unitType == UnitType.AllUnits) {
            return new ArrayList<>(enemyAllUnitInfoMap.values());
        } else {
            return enemyAllUnitInfoByTypeMap.getOrDefault(unitType, new ArrayList<>());
        }
    }

    protected int enemyVisibleCount(UnitType unitType) {
        if (unitType == UnitType.AllUnits) {
            return Broodwar.enemy().allUnitCount();
        } else {
            return Broodwar.enemy().allUnitCount(unitType);
        }
    }

    protected List<Unit> getEnemyUnitList() {
        return Broodwar.enemy().getUnits();
    }

    protected List<UnitInfo> enemyVisibleUnits(UnitType unitType) {
        if (unitType == UnitType.AllUnits) {
            return new ArrayList(enemyAllUnitInfoMap.values());
        } else {
            if (enemyVisibleUnitInfoByTypeMap.size() == 0) {
                //create enemyVisibleUnitInfoByTypeMap at first request in frame
                for (Unit unit : Broodwar.enemy().getUnits()) {
                    List<UnitInfo> unitInfoList = enemyVisibleUnitInfoByTypeMap.getOrDefault(unit.getType(), new ArrayList<>());
                    UnitInfo unitInfo = new UnitInfo(unit);
                    unitInfoList.add(unitInfo);

                    enemyVisibleUnitInfoByTypeMap.put(unit.getType(), unitInfoList);
                }
            }
            return new ArrayList<>(enemyVisibleUnitInfoByTypeMap.getOrDefault(unitType, new ArrayList<>()));
        }
    }

    private void clearAll() {
        selfCompleteUnitList.clear();
        selfIncompleteUnitList.clear();
        selfConstructionTaskList.clear();

        selfAllUnitByTypeMap.clear();
        selfCompleteUnitByTypeMap.clear();
        selfIncompleteUnitByTypeMap.clear();
        selfConstructionTaskMap.clear();

//        enemyAllUnitInfoList.clear();
//        enemyVisibileUnitInfoList.clear();
//        enemyInvisibleUnitInfoList.clear();
//
//        enemyAllUnitInfoByTypeMap.clear();
//        enemyVisibleUnitInfoByTypeMap.clear();
//        enemyInvisibleUnitInfoByTypeMap.clear();
    }

    private void putSelfData() {
        for (Unit unit : Broodwar.self().getUnits()) {
            if (unit == null) {
                continue;
            }

            List<Unit> allUnitList = selfAllUnitByTypeMap.getOrDefault(unit.getType(), new ArrayList<>());
            List<Unit> completeUnitList = selfCompleteUnitByTypeMap.getOrDefault(unit.getType(), new ArrayList<>());
            List<Unit> incompleteUnitList = selfIncompleteUnitByTypeMap.getOrDefault(unit.getType(), new ArrayList<>());

            allUnitList.add(unit);
            if (unit.isCompleted()) {
                completeUnitList.add(unit);
                selfCompleteUnitList.add(unit);
            } else {
                incompleteUnitList.add(unit);
                selfIncompleteUnitList.add(unit);
            }
            selfAllUnitByTypeMap.put(unit.getType(), allUnitList);
            selfCompleteUnitByTypeMap.put(unit.getType(), completeUnitList);
            selfIncompleteUnitByTypeMap.put(unit.getType(), incompleteUnitList);
        }
        Vector<ConstructionTask> constructionTaskvector = ConstructionManager.Instance().getConstructionQueue();
        for (ConstructionTask constructionTask : constructionTaskvector) {
            List<ConstructionTask> constructionTaskList = selfConstructionTaskMap.getOrDefault(constructionTask.getType(), new ArrayList<ConstructionTask>());
            selfConstructionTaskList.add(constructionTask);
            constructionTaskList.add(constructionTask);
            selfConstructionTaskMap.put(constructionTask.getType(), constructionTaskList);
        }
    }

//    private void putEnemyData() {
//        UnitData enemyUnitData = InformationManager.Instance().getUnitData(Broodwar.enemy());
//        for (int unitId : enemyUnitData.getUnitAndUnitInfoMap().keySet()) {
//            UnitInfo unitInfo = enemyUnitData.getUnitAndUnitInfoMap().get(unitId);
//
//            List<UnitInfo> allUnitList = enemyAllUnitInfoByTypeMap.getOrDefault(unitInfo.getType(), new ArrayList<>());
//            List<UnitInfo> visibleUnitList = enemyVisibleUnitInfoByTypeMap.getOrDefault(unitInfo.getType(), new ArrayList<>());
//
//            allUnitList.add(unitInfo);
//            enemyAllUnitInfoList.add(unitInfo);
//            Unit enemy = Broodwar.getUnit(unitInfo.getUnitID());
//            if (enemy != null && enemy.getType() != UnitType.Unknown) {
//                visibleUnitList.add(unitInfo);
//                enemyVisibileUnitInfoList.add(unitInfo);
//            }
//
//            enemyAllUnitInfoByTypeMap.put(unitInfo.getType(), allUnitList);
//            enemyVisibleUnitInfoByTypeMap.put(unitInfo.getType(), visibleUnitList);
//        }
//    }


    protected void updateEnemyUnitInfo(Unit newUnit) {
        if (newUnit == null) {
            return;
        }
        if (newUnit.getPlayer() != PlayerUtils.enemyPlayer()) {
            return;
        }

        //TODO 이러는게 맞나? 실제 호출되는 타이밍이 보이거나 사라질때뿐인데. 만약에 지속적으로 보일때는?
        if (!enemyAllUnitInfoMap.containsKey(newUnit.getID())) {
            enemyAllUnitInfoMap.put(newUnit.getID(), new UnitInfo(newUnit));
            UnitInfo unitInfo = new UnitInfo(newUnit);

            List<UnitInfo> unitInfoList = enemyAllUnitInfoByTypeMap.getOrDefault(newUnit.getType(), new ArrayList<>());
            unitInfoList.add(unitInfo);

            enemyAllUnitInfoByTypeMap.put(newUnit.getType(), unitInfoList);
        } else {
            UnitInfo unitInfo = enemyAllUnitInfoMap.get(newUnit.getID());

            if (unitInfo.getUnitID() == newUnit.getID() && newUnit.getType() != unitInfo.getType()) {

                if (unitInfo.getType() != UnitType.None) {
                    removeUnitbyMorph(unitInfo, unitInfo.getType());

                    enemyAllUnitInfoMap.put(newUnit.getID(), new UnitInfo(newUnit));
                    List<UnitInfo> unitInfoList = enemyAllUnitInfoByTypeMap.getOrDefault(newUnit.getType(), new ArrayList<>());
                    unitInfoList.add(unitInfo);
                }
            }
        }
    }

    protected void destroyedUnitInfo(Unit destroyedUnit) {
        if (destroyedUnit == null) {
            return;
        }
        if (destroyedUnit.getPlayer() != PlayerUtils.enemyPlayer()) {
            return;
        }

//        if (enemyAllUnitInfoMap.containsKey(destroyedUnit.getID())) {
        enemyAllUnitInfoMap.remove(destroyedUnit.getID());

        List<UnitInfo> allUnitInfoList = enemyAllUnitInfoByTypeMap.get(destroyedUnit.getType());

        for (UnitInfo unitInfo : allUnitInfoList) {
            if (unitInfo.getUnitID() == destroyedUnit.getID()) {
                allUnitInfoList.remove(unitInfo);
                break;
            }
        }
    }

    protected void removeUnitbyMorph(UnitInfo morphedUnit, UnitType type) {
        if (morphedUnit == null) {
            return;
        }
        if (type == null) {
            return;
        }

        enemyAllUnitInfoMap.remove(morphedUnit.getUnitID());

        List<UnitInfo> unitInfoList = enemyAllUnitInfoByTypeMap.get(type);

        for (UnitInfo unitInfo : unitInfoList) {
            if (unitInfo.getUnitID() == morphedUnit.getUnitID()) {
                unitInfoList.remove(unitInfo);
                break;
            }
        }
    }

    private void updateDiscoveredMap() {
        for (Unit selfUnit : Broodwar.self().getUnits()) {
            if (selfUnit == null) {
                return;
            }
            UnitType unitType = selfUnit.getType();
            Boolean discovered = selfUnitDiscovered.get(unitType);
            if (discovered == null || discovered == Boolean.FALSE) {
                selfUnitDiscovered.put(unitType, Boolean.TRUE);
            }

            if (selfUnit.isCompleted()) {
                Boolean discoveredComplete = selfCompleteUnitDiscovered.get(unitType);
                if (discoveredComplete == null || discoveredComplete == Boolean.FALSE) {
                    selfCompleteUnitDiscovered.put(unitType, Boolean.TRUE);
                }
            }
        }

        for (Unit enemyUnit : Broodwar.enemy().getUnits()) {
            if (enemyUnit == null) {
                return;
            }
            UnitType unitType = enemyUnit.getType();
            Boolean discovered = enemyUnitDiscovered.get(unitType);
            if (discovered == null || discovered == Boolean.FALSE) {
                enemyUnitDiscovered.put(unitType, Boolean.TRUE);
            }

            if (enemyUnit.isCompleted()) {
                Boolean discoveredComplete = enemyCompleteUnitDiscovered.get(unitType);
                if (discoveredComplete == null || discoveredComplete == Boolean.FALSE) {
                    enemyCompleteUnitDiscovered.put(unitType, Boolean.TRUE);
                }
            }
        }
    }

    /// 포인터가 null 이 되었거나, 파괴되어 Resource_Vespene_Geyser로 돌아간 Refinery, 예전에는 건물이 있었던 걸로 저장해두었는데 지금은 파괴되어 없어진 건물 (특히, 테란의 경우 불타서 소멸한 건물) 데이터를 제거합니다
    private void removeBadUnits() {
        Iterator<Integer> it = enemyAllUnitInfoMap.keySet().iterator();

        while (it.hasNext()) {
            UnitInfo unitInfo = enemyAllUnitInfoMap.get(it.next());
            if (isBadUnitInfo(unitInfo)) {

                List<UnitInfo> allUnitInfoList = enemyAllUnitInfoByTypeMap.get(unitInfo.getType());

                if (unitInfo.getUnitID() == unitInfo.getUnitID()) {
                    allUnitInfoList.remove(unitInfo);
                }

                enemyAllUnitInfoMap.remove(unitInfo);
            }
        }
    }

    private final boolean isBadUnitInfo(final UnitInfo ui) {
        if (ui.getUnit() == null) {
            return false;
        }

        // Cull away any refineries/assimilators/extractors that were destroyed and reverted to vespene geysers
        if (ui.getUnit().getType() == UnitType.Resource_Vespene_Geyser) {
            return true;
        }

        if (ui.getType().isBuilding() && Broodwar.isVisible(ui.getLastPosition().getX() / 32, ui.getLastPosition().getY() / 32) && (!ui.getUnit().isTargetable() || !ui.getUnit().isVisible())) {
            if (MapGrid.Instance().scanAbnormalTime()) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    public void onUnitShow(Unit unit) {
        updateEnemyUnitInfo(unit);
    }

    public void onUnitHide(Unit unit) {
        updateEnemyUnitInfo(unit);
    }

    public void onUnitCreate(Unit unit) {
        updateEnemyUnitInfo(unit);
    }

    public void onUnitComplete(Unit unit) {
        updateEnemyUnitInfo(unit);
    }

    //TODO how about morph cancel?
    public void onUnitMorph(Unit unit) {
        updateEnemyUnitInfo(unit);
    }

    public void onUnitRenegade(Unit unit) {
        updateEnemyUnitInfo(unit);
    }

    /// Unit 에 대한 정보를 업데이트합니다 <br>
    /// 유닛이 파괴/사망한 경우, 해당 유닛 정보를 삭제합니다
    public void onUnitDestroy(Unit unit) {
        destroyedUnitInfo(unit);
    }

    protected Unit enemyUnitInSight(UnitInfo eui) {

        Unit enemyUnit = Broodwar.getUnit(eui.getUnitID());
        if (UnitUtils.isValidUnit(enemyUnit)) {
            return enemyUnit;
        } else {
            return null;
        }
    }

    protected Unit enemyUnitInSight(int euiId) {

        Unit enemyUnit = Broodwar.getUnit(euiId);
        if (UnitUtils.isValidUnit(enemyUnit)) {
            return enemyUnit;
        } else {
            return null;
        }
    }

    protected List<Unit> getUnitsOnTile(TilePosition tilePosition) {
        return  Broodwar.getUnitsOnTile(tilePosition);
    }

    protected boolean canMake(UnitType unitType, Unit unit) {
        return Broodwar.canMake(unitType, unit);
    }
    protected boolean canMake(UnitType unitType) {
        return Broodwar.canMake(unitType);
    }
    protected Unit getUnit(int unitId) {
        return Broodwar.getUnit(unitId);
    }
    protected int myDeadNumUnits(UnitType unitType) {
        return Broodwar.self().deadUnitCount(unitType);
    }

    protected int enemyDeadNumUnits(UnitType unitType) {
        return Broodwar.enemy().deadUnitCount(unitType);
    }
}