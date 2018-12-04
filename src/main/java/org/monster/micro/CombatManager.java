package org.monster.micro;

import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import org.monster.board.StrategyBoard;
import org.monster.common.LagObserver;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.RegionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.common.util.internal.IConditions;
import org.monster.debugger.BigWatch;
import org.monster.decisions.strategy.manage.VultureTravelManager;
import org.monster.bootstrap.GameManager;
import org.monster.bootstrap.Monster;
import org.monster.micro.compute.GuerillaScore;
import org.monster.micro.compute.VultureFightPredictor;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.squad.AirForceSquad;
import org.monster.micro.squad.BuildingSquad;
import org.monster.micro.squad.CheckerSquad;
import org.monster.micro.squad.EarlyDefenseSquad;
import org.monster.micro.squad.GuerillaSquad;
import org.monster.micro.squad.MainAttackSquad;
import org.monster.micro.squad.MultiDefenseSquad;
import org.monster.micro.squad.ScvScoutSquad;
import org.monster.micro.squad.SpecialSquad;
import org.monster.micro.squad.Squad;
import org.monster.micro.squad.WatcherSquad;
import org.monster.micro.targeting.TargetFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombatManager extends GameManager {

    private static CombatManager instance = new CombatManager();
    public SquadData squadData = new SquadData();
    private LagObserver logObserver = new LagObserver();

    // int combatStatus = 0; // 0: 평시

    public static CombatManager Instance() {
        return instance;
    }

    public void onStart() {
        // assign 되지 않는 유닛을 체크하기 위한 Squad
        // IdleSquad idleSquad = new IdleSquad(0, 0);
        // squadData.addSquad(idleSquad);
        // squadData.activateSquad(idleSquad.getSquadName());

        // SCV + 마린
        // * 포지션: campPosition
        // * 초반 수비(정찰일꾼 견제, 일꾼러시, 가스러시, 파일런러시, 포톤러시, 초반 저글링/마린/질럿 등)
        EarlyDefenseSquad earlyDefenseSquad = new EarlyDefenseSquad();
        squadData.addSquad(earlyDefenseSquad);

        // (마린) + 탱크 + 골리앗
        // * 본진 수비 및 적 공격
        // * 정찰 SCV와 연계된 벙커링 (TBD)
        MainAttackSquad mainAttackSquad = new MainAttackSquad();
        squadData.addSquad(mainAttackSquad);

        // 감시 벌처
        // * 적 감시 및 견제, 적 공격, 마인매설(NOT_MY_OCCUPIED OR ANYWHERE)
        WatcherSquad watcherSquad = new WatcherSquad();
        squadData.addSquad(watcherSquad);

        // 정찰 벌처
        // * 적 진영 정찰, 마인매설(ONLY_GOOD_POSITION)
        CheckerSquad checkerSquad = new CheckerSquad();
        squadData.addSquad(checkerSquad);

        // 초반 정찰용 SCV
        // * 적 base 탐색, 일꾼견제
        // * 12드론 앞마당시 벙커링 (TBD)
        ScvScoutSquad scvScoutSquad = new ScvScoutSquad();
        squadData.addSquad(scvScoutSquad);

        // 레이쓰 특공대
        AirForceSquad airForceSquad = new AirForceSquad();
        squadData.addSquad(airForceSquad);

        // 개별 유닛 - 베슬, 드랍십
        SpecialSquad specialSquad = new SpecialSquad();
        squadData.addSquad(specialSquad);

        // 배럭, 컴셋 등 빌딩
        BuildingSquad buildingSquad = new BuildingSquad();
        squadData.addSquad(buildingSquad);
    }

    public void update() {
        logObserver.start();

        combatUnitArrangement();
        squadExecution();

        logObserver.observe();
    }

    private void combatUnitArrangement() {
        BigWatch.start("combatUnitArrangement");

        // 팩토리
        updateSquadDefault(MicroConfig.SquadInfo.MAIN_ATTACK); // 탱크, 골리앗, 발키리
        updateTankDefenseSquad(); // 탱크

        updateSquadDefault(MicroConfig.SquadInfo.AIR_FORCE); // 레이스
        updateSquadDefault(MicroConfig.SquadInfo.SPECIAL); // 베슬
        updateSquadDefault(MicroConfig.SquadInfo.BUILDING); // 빌딩, 컴셋

        // SCV유형별 구분
        updateSquadDefault(MicroConfig.SquadInfo.EARLY_DEFENSE); // SCV
        updateSquadDefault(MicroConfig.SquadInfo.SCV_SCOUT); // SCV

        // 벌처유형별 구분
        updateSquadDefault(MicroConfig.SquadInfo.WATCHER); // 벌처
        updateSquadDefault(MicroConfig.SquadInfo.CHECKER); // 벌처
        updateGuerillaSquad(); // 벌처

        BigWatch.record("combatUnitArrangement");
    }

    private void squadExecution() {
        Squad mainSquad = squadData.getSquadMap().get(MicroConfig.SquadInfo.MAIN_ATTACK.squadName);
        mainSquad.findEnemiesAndExecuteSquad();

        for (Squad squad : squadData.getSquadMap().values()) {
            squad.findEnemiesAndExecuteSquad(); // squad 유닛 명령 지시
        }
    }

    private void updateSquadDefault(MicroConfig.SquadInfo squadInfo) {
        Squad squad = squadData.getSquad(squadInfo.squadName);

        for (Unit invalidUnit : squad.invalidUnitList()) {
            squadData.exclude(invalidUnit);
        }

        if (PlayerUtils.supplyUsedSelf() < 100 || TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER7, 0), LagObserver.managerRotationSize())) {
            List<Unit> squadTypeUnitList = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, squad.getUnitTypes());

            List<Unit> assignableUnitList = new ArrayList<>();
            for (Unit unit : squadTypeUnitList) {
                if (squad.want(unit)) {
                    assignableUnitList.add(unit);
                }
            }
            List<Unit> recruitUnitList = squad.recruit(assignableUnitList);
            for (Unit recuitUnit : recruitUnitList) {
                squadData.assign(recuitUnit, squad);
            }
        }
    }

    private void updateTankDefenseSquad() {
        if (BaseUtils.enemyMainBase() == null) {
            return;
        }

        int mainSquadTankCount = 0;
        Squad mainSquad = squadData.getSquad(MicroConfig.SquadInfo.MAIN_ATTACK.squadName);
        for (Unit unit : mainSquad.unitList) {
            if (unit.getType() == UnitType.Terran_Siege_Tank_Tank_Mode || unit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {
                mainSquadTankCount++;
            }
        }

        int defenseTankCount = 0;
        Set<Integer> defenseTankIdSet = new HashSet<>();
        List<Squad> squadList = squadData.getSquadList(MicroConfig.SquadInfo.MULTI_DEFENSE_.squadName);

        if (mainSquadTankCount <= 4) {
            for (Squad defenseSquad : squadList) {
                squadData.removeSquad(defenseSquad.getSquadName());
            }
            return;
        }

        if (mainSquadTankCount <= 5) {
            return;
        }

        if (StrategyBoard.mainSquadMode == MicroConfig.MainSquadMode.NO_MERCY) {
            for (Squad defenseSquad : squadList) {
                squadData.removeSquad(defenseSquad.getSquadName());
            }
            return;
        }

        for (Squad defenseSquad : squadList) {
            MultiDefenseSquad squad = (MultiDefenseSquad) defenseSquad;
            if (squad.getType() == MultiDefenseSquad.DefenseType.CENTER_DEFENSE) {
                Unit commandCenter = squad.getCommandCenter();
                if (!UnitUtils.isValidUnit(commandCenter)) {
                    squadData.removeSquad(defenseSquad.getSquadName());
                    continue;
                }

            } else if (squad.getType() == MultiDefenseSquad.DefenseType.REGION_OCCUPY) {
                if (squad.tankFull()) {
                    squadData.removeSquad(defenseSquad.getSquadName()); // 공격으로 전환
                    continue;
                }
            }

            for (Unit invalidUnit : defenseSquad.invalidUnitList()) {
                squadData.exclude(invalidUnit);
            }
            defenseTankCount += defenseSquad.unitList.size();
            for (Unit unit : defenseSquad.unitList) {
                defenseTankIdSet.add(unit.getID());
            }
        }


        if (!TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER7, 0), LagObserver.managerRotationSize())) {
            return;
        }

        if (!StrategyBoard.mainSquadCrossBridge) {
            return;
        }

        if (PlayerUtils.enemyRace() == Race.Terran) {
            if (defenseTankCount > mainSquadTankCount * 0.4) {
                return;
            }
        } else {
            if (defenseTankCount > mainSquadTankCount * 0.2) {
                return;
            }
        }

        // create defense squad
        List<Unit> commandCenters = UnitUtils.getUnitList(CommonCode.UnitFindStatus.ALL, UnitType.Terran_Command_Center);

        Region baseRegion = BWTA.getRegion(BaseUtils.myMainBase().getPosition());
        Region expansionRegion = BWTA.getRegion(BaseUtils.myFirstExpansion().getPosition());

        for (Unit commandCenter : commandCenters) {
            Region centerRegion = BWTA.getRegion(commandCenter.getPosition());
            Race enemyRace = PlayerUtils.enemyRace();
            if (enemyRace == Race.Zerg) {
                // 본진빼고 배치
                if (centerRegion == baseRegion) {
                    continue;
                }
            } else if (enemyRace == Race.Protoss) {
                // 본진과 expansion만 배치
                if (centerRegion != baseRegion && centerRegion != expansionRegion) {
                    continue;
                }
            } else if (enemyRace == Race.Terran) {
                // 본진과 expansion 빼고 배치. 드랍십 발견하며 배치
                if (!UnitUtils.enemyUnitDiscovered(UnitType.Terran_Dropship)) {
                    if (centerRegion == baseRegion || centerRegion == expansionRegion) {
                        continue;
                    }
                }
            }

            String squadName = MicroConfig.SquadInfo.MULTI_DEFENSE_.squadName + "U" + commandCenter.getID();
            Squad defenseSquad = squadData.getSquad(squadName);
            // 게릴라 스쿼드 생성(포지션 별)
            if (defenseSquad == null) {
                defenseSquad = new MultiDefenseSquad(commandCenter);
                squadData.addSquad(defenseSquad);
            }
        }


        if (PlayerUtils.enemyRace() == Race.Terran) {
            Region enemyRegion = BWTA.getRegion(BaseUtils.enemyMainBase().getPosition());
            Set<Region> occupiedRegions = RegionUtils.myOccupiedRegions();

            for (BaseLocation base : BWTA.getStartLocations()) {
                Region region = BWTA.getRegion(base.getPosition());
                if (region == baseRegion || enemyRegion == baseRegion) {
                    continue;
                }
                if (occupiedRegions.contains(region)) {
                    String squadName = MicroConfig.SquadInfo.MULTI_DEFENSE_.squadName + "P" + base.getPosition();
                    Squad regionDefenseSquad = squadData.getSquad(squadName);

                    if (regionDefenseSquad == null) {
                        regionDefenseSquad = new MultiDefenseSquad(base.getPosition());
                        squadData.addSquad(regionDefenseSquad);
                    }
                }
            }
        }

        // assign units
        List<Squad> commandSquadList = squadData.getSquadList(MicroConfig.SquadInfo.MULTI_DEFENSE_.squadName + "U");
        if (assign(commandSquadList, defenseTankIdSet)) {
            return;
        }
        List<Squad> regionSquadList = squadData.getSquadList(MicroConfig.SquadInfo.MULTI_DEFENSE_.squadName + "P");
        assign(regionSquadList, defenseTankIdSet);
    }

    private boolean assign(List<Squad> updatedSquadList, Set<Integer> defenseTankIdSet) {
        List<Unit> tankList = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode);

        for (Squad defenseSquad : updatedSquadList) {
            MultiDefenseSquad squad = (MultiDefenseSquad) defenseSquad;
            if (squad.alreadyDefenseUnitAssigned()) {
                continue;
            }
            if (squad.tankFull()) {
                continue;
            }

            if (PlayerUtils.enemyRace() != Race.Terran && squad.getType() == MultiDefenseSquad.DefenseType.CENTER_DEFENSE) {
                Unit comandCenter = squad.getCommandCenter();
                Set<UnitInfo> euis = UnitUtils.getEnemyUnitInfosInRadius(TargetFilter.UNFIGHTABLE, comandCenter.getPosition(), UnitType.Terran_Command_Center.sightRange(), true, true);
                if (!euis.isEmpty()) {
                    continue;
                }
            }

            Unit closestTank = UnitUtils.getClosestUnitToPosition(tankList, BaseUtils.myMainBase().getPosition(), new IConditions.UnitCondition() {
                @Override
                public boolean correspond(Unit unit) {
                    return !defenseTankIdSet.contains(unit.getID());
                }
            });
            squadData.assign(closestTank, squad);
            squad.setDefenseUnitAssignedFrame(TimeUtils.elapsedFrames());
            return true;
        }
        return false;
    }

    private void updateGuerillaSquad() {

        if (TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER7, 0), LagObserver.managerRotationSize())) {
            double maxRatio = StrategyBoard.mainSquadMode.maxGuerillaVultureRatio;
            if (StrategyBoard.nearGroundEnemyPosition != null) {
                maxRatio = 0.0d;
            } else {
                if (PlayerUtils.enemyRace() == Race.Terran && StrategyBoard.mainSquadCrossBridge) {
                    maxRatio = 0.5;
                }
            }

            int gurillaFailFrame = VultureTravelManager.Instance().getGurillaFailFrame();
            if (TimeUtils.elapsedFrames(gurillaFailFrame) < 10 * TimeUtils.SECOND) {
                maxRatio = 0.0d;
            }

            int vultureCount = UnitUtils.getUnitCount(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Vulture);
            int maxCount = (int) (vultureCount * maxRatio);

            List<Unit> assignableVultures = new ArrayList<>();
            List<Unit> squadTypeUnitList = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Vulture);
            for (Unit unit : squadTypeUnitList) {
                Squad unitSqaud = squadData.getSquad(unit);
                if (unitSqaud instanceof GuerillaSquad) {
                    continue;
                }

                assignableVultures.add(unit);
                if (assignableVultures.size() >= maxCount) {
                    break;
                }
            }

            // 새 게릴라 스퀴드
            newGuerillaSquad(assignableVultures);
        }

        // 게릴라 임무해제
        List<Squad> guerillaSquads = squadData.getSquadList(MicroConfig.SquadInfo.GUERILLA_.squadName);
        for (Squad squad : guerillaSquads) {
            for (Unit invalidUnit : squad.invalidUnitList()) {
                squadData.exclude(invalidUnit);
            }
            if (removeGuerilla((GuerillaSquad) squad)) {
                squadData.removeSquad(squad.getSquadName());
            }
        }
    }

    private void newGuerillaSquad(List<Unit> assignableVultures) {
        BaseLocation bestGuerillaSite = VultureTravelManager.Instance().getBestGuerillaSite(assignableVultures);
        if (bestGuerillaSite == null) {
            return;
        }

        // 안개속의 적들을 상대로 계산해서 게릴라 타깃이 가능한지 확인한다.
        Set<UnitInfo> euiList = UnitUtils.getAllEnemyUnitInfosInRadiusForGround(bestGuerillaSite.getPosition(), MicroConfig.Vulture.GEURILLA_ENEMY_RADIUS);
        int enemyPower = VultureFightPredictor.powerOfEnemiesByUnitInfo(euiList);
        int vulturePower = VultureFightPredictor.powerOfWatchers(assignableVultures);
        if (vulturePower < enemyPower) {
            return;
        }

        String squadName = MicroConfig.SquadInfo.GUERILLA_.squadName + "P" + bestGuerillaSite.getPosition().toString();
        Squad guerillaSquad = squadData.getSquad(squadName);
        // 게릴라 스쿼드 생성(포지션 별)
        if (guerillaSquad == null) {
            guerillaSquad = new GuerillaSquad(bestGuerillaSite.getPosition());
            squadData.addSquad(guerillaSquad);
        }
        // 게릴라 유닛이 남아있지 않다면 할당한다.
        if (guerillaSquad.unitList.isEmpty()) {
            for (Unit assignableVulture : assignableVultures) {
                squadData.assign(assignableVulture, guerillaSquad);
                int squadPower = VultureFightPredictor.powerOfWatchers(guerillaSquad.unitList);
                if (squadPower > enemyPower + MicroConfig.Vulture.GEURILLA_EXTRA_ENEMY_POWER) {
                    break; // 충분한 파워
                }
            }
        }
        VultureTravelManager.Instance().guerillaStart(squadName);
    }

    private boolean removeGuerilla(GuerillaSquad squad) {
        if (squad.unitList.isEmpty()) {
            VultureTravelManager.Instance().setGurillaFailFrame(TimeUtils.elapsedFrames());
            return true;
        }

        // 게릴라 지역에 적군이 없다.
        if (PlayerUtils.isVisible(squad.getTargetPosition().toTilePosition())) {
            Set<UnitInfo> euiList = UnitUtils.getAllEnemyUnitInfosInRadiusForGround(squad.getTargetPosition(), MicroConfig.Vulture.GEURILLA_ENEMY_RADIUS);
            if (euiList.isEmpty()) {
                return true;
            }

            // 일꾼이 없는 경우
            List<Unit> workers = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.ENEMY, squad.getTargetPosition(), MicroConfig.Vulture.GEURILLA_ENEMY_RADIUS, UnitType.Terran_SCV, UnitType.Protoss_Probe, UnitType.Zerg_Drone);
            if (workers.isEmpty()) {
                int vulturePower = VultureFightPredictor.powerOfWatchers(squad.unitList);
                int enemyPower = VultureFightPredictor.powerOfEnemiesByUnitInfo(euiList);
                if (vulturePower < enemyPower - 50) { // 질것 같으면 후퇴
                    System.out.println("remove guerialla - retreat");
                    return true;
                }

                int guerillaScore = GuerillaScore.guerillaScoreByUnitInfo(euiList); // 이득볼게 없으면 후퇴
                if (guerillaScore <= 0) {
                    System.out.println("remove guerialla - nothing to do");
                    return true;
                }
            }
        }
        return false;
    }
}
