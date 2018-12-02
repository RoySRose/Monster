package org.monster.micro.squad;

import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import org.monster.board.StrategyBoard;
import org.monster.bootstrap.Monster;
import org.monster.common.UnitInfo;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.decisions.strategy.manage.PositionFinder;
import org.monster.micro.CombatManager;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.control.airforce.ValkyrieControl;
import org.monster.micro.control.groundforce.GoliathControl;
import org.monster.micro.control.groundforce.TankControl;
import org.monster.micro.targeting.TargetFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainAttackSquad extends Squad {

//	private Set<UnitInfo> euisNearUnit = new HashSet<>();
//	private Set<UnitInfo> euisNearBaseRegion = new HashSet<>();
//	
//	public Set<UnitInfo> getEuisNearUnit() {
//		return euisNearUnit;
//	}
//
//	public Set<UnitInfo> getEuisNearBaseRegion() {
//		return euisNearBaseRegion;
//	}

    private TankControl tankControl = new TankControl();
    private GoliathControl goliathControl = new GoliathControl();
    private ValkyrieControl valkyrieControl = new ValkyrieControl();

    public MainAttackSquad() {
        super(MicroConfig.SquadInfo.MAIN_ATTACK);
        setUnitType(UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_Goliath, UnitType.Terran_Valkyrie);
    }

    @Override
    public boolean want(Unit unit) {
        if (unit.getType() == UnitType.Terran_Siege_Tank_Tank_Mode || unit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {
            Squad squad = CombatManager.Instance().squadData.getSquad(unit);
            if (squad instanceof MultiDefenseSquad) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Unit> recruit(List<Unit> assignableUnitList) {
        return assignableUnitList;
    }

    @Override
    public void execute() {
        Map<UnitType, List<Unit>> unitListMap = UnitUtils.makeUnitListMap(unitList);
        List<Unit> tankList = new ArrayList<>();
        List<Unit> goliathList = new ArrayList<>();
        List<Unit> valkyrieList = new ArrayList<>();

        tankList.addAll(unitListMap.getOrDefault(UnitType.Terran_Siege_Tank_Tank_Mode, new ArrayList<Unit>()));
        tankList.addAll(unitListMap.getOrDefault(UnitType.Terran_Siege_Tank_Siege_Mode, new ArrayList<Unit>()));
        goliathList.addAll(unitListMap.getOrDefault(UnitType.Terran_Goliath, new ArrayList<Unit>()));
        valkyrieList.addAll(unitListMap.getOrDefault(UnitType.Terran_Valkyrie, new ArrayList<Unit>()));

        StrategyBoard.initiated = this.updateInitiatedFlag();
        int saveUnitLevel = this.saveUnitLevel(tankList, goliathList);
        int goliathSaveUnitLevel = Math.min(saveUnitLevel, 1);

        tankControl.setSaveUnitLevel(saveUnitLevel);
        tankControl.setMainPosition(StrategyBoard.mainSquadMode.isAttackMode ? StrategyBoard.mainPosition : StrategyBoard.campPositionSiege);
        goliathControl.setSaveUnitLevel(goliathSaveUnitLevel);

//		System.out.println(euiList);
        Set<UnitInfo> groundEuiList = MicroUtils.filterTargetInfos(euiList, TargetFilter.AIR_UNIT);
        if (!tankList.isEmpty()) {
            tankControl.controlIfUnitExist(tankList, groundEuiList);
        }
        goliathControl.controlIfUnitExist(goliathList, euiList);
        if (!valkyrieList.isEmpty()) {
            Set<UnitInfo> airEuiList = MicroUtils.filterTargetInfos(euiList, TargetFilter.GROUND_UNIT);
            valkyrieControl.controlIfUnitExist(valkyrieList, airEuiList);
        }
    }

    private boolean updateInitiatedFlag() {
        if (euiList.isEmpty()) {
            return false;
        }

        for (UnitInfo eui : euiList) {
            if (eui.getType() != UnitType.Terran_Vulture_Spider_Mine
                    && eui.getType() != UnitType.Zerg_Larva
                    && !eui.getType().isBuilding()
                    && !eui.getType().isWorker()
                    && !eui.getType().isFlyer()) {
                if (StrategyBoard.mainSquadCenter.getDistance(eui.getLastPosition()) < StrategyBoard.mainSquadCoverRadius) {
                    return true;
                }
            }
        }
        return false;
    }

    private int saveUnitLevel(List<Unit> tankList, List<Unit> goliathList) {
        int saveUnitLevel = 1; // 거리재기 전진
        if (PlayerUtils.enemyRace() == Race.Terran) {
            List<UnitInfo> closeTankEnemies = new ArrayList<>();
            for (UnitInfo eui : euiList) {
                if (eui.getType() == UnitType.Terran_Siege_Tank_Tank_Mode || eui.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {
                    closeTankEnemies.add(eui);
                }
            }
            if (closeTankEnemies.size() * 4 > tankList.size()) {
                // System.out.println("keep in line");
                saveUnitLevel = 2; // 안전거리 유지
            }
        }

        if (StrategyBoard.mainSquadMode == MicroConfig.MainSquadMode.NO_MERCY) { // strategy manager 판단
            saveUnitLevel = 0;
        } else { // combat manager 자체 판단
            if (PlayerUtils.enemyRace() != Race.Terran) {
                if (Monster.Broodwar.self().supplyUsed() >= 380) { // || pushLine) {
                    saveUnitLevel = 0;
                }
            }
        }
        return saveUnitLevel;
    }

    @Override
    public void findEnemies() {
        euiList.clear();

        UnitUtils.addEnemyUnitInfosInRadius(TargetFilter.UNFIGHTABLE | TargetFilter.LARVA_LURKER_EGG, euiList, StrategyBoard.mainSquadCenter, StrategyBoard.mainSquadCoverRadius + 50, true, false);

        if (StrategyBoard.mainSquadMode.isAttackMode) {
            for (Unit unit : unitList) {
                if (unit.getDistance(StrategyBoard.mainSquadCenter) > StrategyBoard.mainSquadCoverRadius + 50) {
                    UnitUtils.addEnemyUnitInfosInRadius(TargetFilter.UNFIGHTABLE | TargetFilter.LARVA_LURKER_EGG, euiList, unit.getPosition(), unit.getType().sightRange() + MicroConfig.COMMON_ADD_RADIUS, true, false);
                }
            }

        }

        if (!StrategyBoard.mainSquadMode.isAttackMode || PlayerUtils.enemyRace() == Race.Terran) {
            if (StrategyBoard.campType == PositionFinder.CampType.INSIDE) {
                euiList.addAll(UnitUtils.euiListInBase());
            } else if (StrategyBoard.campType == PositionFinder.CampType.FIRST_CHOKE || StrategyBoard.campType == PositionFinder.CampType.EXPANSION) {
                euiList.addAll(UnitUtils.euiListInBase());
                euiList.addAll(UnitUtils.euiListInExpansion());
            } else {
                euiList.addAll(UnitUtils.euiListInBase());
                euiList.addAll(UnitUtils.euiListInExpansion());
                euiList.addAll(UnitUtils.euiListInThirdRegion());
            }
        }

        if (TimeUtils.beforeTime(8, 0)) {
            UnitUtils.addEnemyUnitInfosInRadiusForGround(euiList, StrategyBoard.mainSquadCenter, StrategyBoard.mainSquadCoverRadius);
            List<Unit> myBuildings = UnitUtils.myBuildingsInMainSquadRegion();
            for (Unit building : myBuildings) {
                UnitUtils.addEnemyUnitInfosInRadiusForGround(euiList, building.getPosition(), building.getType().sightRange() + MicroConfig.COMMON_ADD_RADIUS);
            }
        }

//		System.out.println("###");
//		System.out.println(unitList);
//		System.out.println(euiList);
    }
}