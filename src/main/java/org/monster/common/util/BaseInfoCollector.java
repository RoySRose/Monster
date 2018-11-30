package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;
import bwta.Region;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseInfoCollector implements InfoCollector {

    private static BaseInfoCollector instance = new BaseInfoCollector();

    public static BaseInfoCollector Instance() {
        return instance;
    }

    Game Broodwar;


    protected Map<Player, BaseLocation> mainBaseLocation = new HashMap();
    protected Map<Player, Boolean> mainBaseLocationChanged = new HashMap();
    /// 해당 Player가 점령하고 있는 Region 이 있는 BaseLocation<br>
    /// 건물 여부를 기준으로 파악하기 때문에 부적절하게 판단할수도 있습니다
    protected Map<Player, List<BaseLocation>> occupiedBaseLocations = new HashMap();
    protected Map<Player, BaseLocation> firstExpansionLocation = new HashMap();
    protected List<BaseLocation> islandBaseLocations = new ArrayList();

    protected Map<Player, Chokepoint> firstChokePoint = new HashMap();
    protected Map<Player, Chokepoint> secondChokePoint = new HashMap();

    protected Map<Player, Region> thirdRegion = new HashMap();
    protected Map<Player, Set<Region>> occupiedRegions = new HashMap();

    protected BaseLocation enemyBaseExpected; // 적base 예상 지점

    //TODO needed?
    protected List<BaseLocation> otherExpansionLocations = new ArrayList();

    private Player selfPlayer;
    private Player enemyPlayer;

    private EnemyBaseFinder enemyBaseFinder = new EnemyBaseFinder(Broodwar);;

    ChokeInfoCollector chokeInfoCollector = ChokeInfoCollector.Instance();
//    ChokeInfoCollector chokeInfoCollector = ChokeInfoCollector.Instance();

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
        selfPlayer = Broodwar.self();
        enemyPlayer = Broodwar.enemy();

        initialize();

        updateIslandBases();
        updateMyMainBase();

        mainBaseLocation.put(enemyPlayer, null);
        mainBaseLocationChanged.put(enemyPlayer, new Boolean(false));

        //TODO
        //updateChokePointAndExpansionLocation();
    }

    private void initialize(){
        occupiedBaseLocations.put(selfPlayer, new ArrayList<>());
        occupiedBaseLocations.put(enemyPlayer, new ArrayList<>());
    }

    private void updateMyMainBase() {
        mainBaseLocation.put(selfPlayer, BWTA.getStartLocation(selfPlayer));
        mainBaseLocationChanged.put(selfPlayer, new Boolean(true));
        occupiedBaseLocations.get(selfPlayer).add(mainBaseLocation.get(selfPlayer));

        checkChangesOfBase(selfPlayer);
    }

    private void checkChangesOfBase(Player player) {

        if (player == PlayerUtils.enemyPlayer()) {
//            this.updateReadyToAttackPosition();
//            this.updateOtherExpansionLocation(enemySourceBaseLocation);
        }
        if (mainBaseLocationChanged.get(player).booleanValue() == true) {

            if (mainBaseLocation.get(player) != null) {
                BaseLocation myMainBaseLocation = mainBaseLocation.get(player);
                System.out.println("* my MainBase changed" + myMainBaseLocation.getTilePosition());

                //TODO 뒷마당 있는 맵에서는 vector 로 처리하거나, center air distance 추가 필요
                BaseLocation myFirstExpansionLocation = findClosestBase(myMainBaseLocation);
                firstExpansionLocation.put(player, myFirstExpansionLocation);

                chokeInfoCollector.updateClosestChokePoints(player, myMainBaseLocation);
                calculateThirdRegion(player);

                //this.updateOtherExpansionLocation(myMainBaseLocation);
            }
            mainBaseLocationChanged.put(player, new Boolean(false));
            TimeInfoCollector.Instance().clearBaseToBaseFrame();
        }
    }

    private void calculateThirdRegion(Player player) {
        double radian = MicroUtils.targetDirectionRadian(
                firstExpansionLocation.get(player).getPosition(),
                secondChokePoint.get(player).getCenter());
        Region myThirdRegion = BWTA.getRegion(
                MicroUtils.getMovePosition(secondChokePoint.get(player).getCenter(), radian, 100));
        thirdRegion.put(player, myThirdRegion);
    }

    protected BaseLocation findClosestBase(BaseLocation sourceBase) {

        int tempDistance;
        int closestDistance = CommonCode.INT_MAX;
        BaseLocation closestFirstExpansion = null;
        for (BaseLocation targetBase : BWTA.getBaseLocations()) {
            if (targetBase.getTilePosition().equals(sourceBase.getTilePosition()))
                continue;

            tempDistance = PositionUtils.getGroundDistance(sourceBase.getPosition(), targetBase.getPosition());

            if (tempDistance < closestDistance && tempDistance > 0) {
                closestDistance = tempDistance;
                closestFirstExpansion = targetBase;
            }
        }
        return closestFirstExpansion;
    }

    private void updateIslandBases() {
        for (BaseLocation targetBaseLocation : BWTA.getBaseLocations()) {
            if (targetBaseLocation.isIsland()) {
                islandBaseLocations.add(targetBaseLocation);
            }
        }
    }


    @Override
    public void update() {
        updateBaseLocationInfo();

        //TODO 위치가 맞을지
        updateChokePointAndExpansionLocation();
        //TODO 위치가 맞을지
        updateChokePointAndExpansionLocationEnemy();
        checkChangesOfBases();
        //TODO 위와 같이 나눔
        //updateChokePointAndExpansionLocation();
    }

    private void checkChangesOfBases() {
        checkChangesOfBase(selfPlayer);
        checkChangesOfBase(enemyPlayer);
    }


    private void initializeOccupiedBaseNRegion() {
        if (occupiedRegions.get(selfPlayer) != null) {
            occupiedRegions.get(selfPlayer).clear();
        }
        if (occupiedRegions.get(enemyPlayer) != null) {
            occupiedRegions.get(enemyPlayer).clear();
        }
        if (occupiedBaseLocations.get(selfPlayer) != null) {
            occupiedBaseLocations.get(selfPlayer).clear();
        }
        if (occupiedBaseLocations.get(enemyPlayer) != null) {
            occupiedBaseLocations.get(enemyPlayer).clear();
        }
    }


    public void updateBaseLocationInfo() {
        initializeOccupiedBaseNRegion();

        // enemy 의 startLocation을 아직 모르는 경우
        if (mainBaseLocation.get(enemyPlayer) == null) {
            findEnemyBaseLocation();
        }

        // update occupied base location
        // 어떤 Base Location 에는 아군 건물, 적군 건물 모두 혼재해있어서 동시에 여러 Player 가 Occupy 하고
        // 있는 것으로 판정될 수 있다
        for (BaseLocation baseLocation : BWTA.getBaseLocations()) {
            if (hasBuildingAroundBaseLocation(baseLocation, enemyPlayer)) {
                occupiedBaseLocations.get(enemyPlayer).add(baseLocation);
            }

            if (hasBuildingAroundBaseLocation(baseLocation, selfPlayer)) {
                occupiedBaseLocations.get(selfPlayer).add(baseLocation);
            }
        }

        if (mainBaseLocation.get(enemyPlayer) != null) {

            // 적 MainBaseLocation 업데이트 로직 버그 수정
            // 적군의 빠른 앞마당 건물 건설 + 아군의 가장 마지막 정찰 방문의 경우,
            // enemy의 mainBaseLocations를 방문안한 상태에서는 건물이 하나도 없다고 판단하여 mainBaseLocation 을 변경하는
            // 현상이 발생해서 enemy의 mainBaseLocations을 실제 방문했었던 적이 한번은 있어야 한다라는 조건 추가.
            TilePosition enemyBaseTile = mainBaseLocation.get(enemyPlayer).getTilePosition();
            if (Broodwar.isExplored(enemyBaseTile)) {
                if (existsPlayerBuildingInRegion(BWTA.getRegion(enemyBaseTile), enemyPlayer) == false) {
                    System.out.println("not exist in enemy region. tile=" + enemyBaseTile);

                    // 적이 차지한 곳 중에서 빌딩이 있는 지역을 enemyBase로 설정
                    BaseLocation enemyBaseLocation = null;
                    for (BaseLocation loaction : occupiedBaseLocations.get(enemyPlayer)) {
                        if (existsPlayerBuildingInRegion(BWTA.getRegion(loaction.getTilePosition()), enemyPlayer)) {
                            enemyBaseLocation = loaction;
                            break;
                        }
                    }

                    // 방문한 적이 없는 starting location으로 설정 (최초 정찰이 실패하여 적 지역 예측했을 때)
                    if (enemyBaseLocation == null) {
                        for (BaseLocation loaction : BWTA.getStartLocations()) {
                            if (!Broodwar.isExplored(loaction.getTilePosition())) {
                                enemyBaseLocation = loaction;
                                break;
                            }
                        }
                    }

                    if (enemyBaseLocation != null) {
                        mainBaseLocation.put(enemyPlayer, enemyBaseLocation);
                        mainBaseLocationChanged.put(enemyPlayer, new Boolean(true));
                    }
                }
            }
        }

        // self의 mainBaseLocations에 대해, 그곳에 있는 건물이 모두 파괴된 경우
        // _occupiedBaseLocations 중에서 _mainBaseLocations 를 선정한다
        if (mainBaseLocation.get(selfPlayer) != null) {
            if (existsPlayerBuildingInRegion(BWTA.getRegion(mainBaseLocation.get(selfPlayer).getTilePosition()),
                    selfPlayer) == false) {
                for (BaseLocation location : occupiedBaseLocations.get(selfPlayer)) {
                    if (existsPlayerBuildingInRegion(BWTA.getRegion(location.getTilePosition()), selfPlayer)) {
                        mainBaseLocation.put(selfPlayer, location);
                        //secondStartPosition = null;
                        mainBaseLocationChanged.put(selfPlayer, new Boolean(true));
                        break;
                    }
                }
            }
        }

        for (UnitInfo ui : UnitUtils.getEnemyUnitInfoList()) {

            if (ui.getType().isBuilding()) {
                updateOccupiedRegions(BWTA.getRegion(ui.getLastPosition().toTilePosition()),
                        Broodwar.enemy());
            }
        }

        for (Unit ui : UnitUtils.getUnitList()) {
            if (ui.getType().isBuilding()) {
                if (UnitUtils.isCompleteValidUnit(ui) && ui.isLifted()
                        && (ui.getType() == UnitType.Terran_Barracks
                        || ui.getType() == UnitType.Terran_Engineering_Bay)) {
                    continue;
                }

                updateOccupiedRegions(BWTA.getRegion(ui.getPosition().toTilePosition()),
                        Broodwar.self());
            }
        }
    }

    private void findEnemyBaseLocation() {
        // how many start locations have we explored
        int exploredStartLocations = 0;
        boolean enemyStartLocationFound = false;

        // an unexplored base location holder
        BaseLocation unexplored = null;

        Region myRegion = BWTA.getRegion(mainBaseLocation.get(selfPlayer).getPosition());
        for (BaseLocation startLocation : BWTA.getStartLocations()) {
            Region startLocationRegion = BWTA.getRegion(startLocation.getTilePosition());
            if (myRegion != startLocationRegion && existsPlayerBuildingInRegion(startLocationRegion, enemyPlayer)) {
                if (enemyStartLocationFound == false) {
                    enemyStartLocationFound = true;
                    mainBaseLocation.put(enemyPlayer, startLocation);
                    mainBaseLocationChanged.put(enemyPlayer, new Boolean(true));
                }
            }

            if (Broodwar.isExplored(startLocation.getTilePosition())) {
                // if it's explored, increment
                exploredStartLocations++;
            } else {
                // otherwise set it as unexplored base
                unexplored = startLocation;
            }
        }

        // if we've explored every start location except one, it's the enemy
        if (!enemyStartLocationFound && exploredStartLocations == (BWTA.getStartLocations().size() - 1)) {
            mainBaseLocation.put(enemyPlayer, unexplored);
            mainBaseLocationChanged.put(enemyPlayer, new Boolean(true));
            // C++ : _occupiedBaseLocations[_enemy].push_back(unexplored);
            if (occupiedBaseLocations.get(enemyPlayer) == null) {
                occupiedBaseLocations.put(enemyPlayer, new ArrayList<BaseLocation>());
            }
            occupiedBaseLocations.get(enemyPlayer).add(unexplored);
        }

        // 끝까지 상대 location 못 찾았을때
        if (TimeUtils.afterTime(4, 0)) {
            BaseLocation expectBase = null;

            BaseLocation enemyBaseExpected = enemyBaseFinder.find();
            if (enemyBaseExpected != null) {
                expectBase = enemyBaseExpected;
            } else {
                BaseLocation myBase = BaseUtils.myMainBase();
                for (BaseLocation startLocation : BWTA.getStartLocations()) {
                    if (startLocation.getTilePosition().equals(myBase.getTilePosition())) {
                        continue;
                    }
                    if (Broodwar.isExplored(startLocation.getTilePosition())) {
                        continue;
                    }
                    expectBase = startLocation;
                    break;
                }
            }

            if (expectBase != null) {
                mainBaseLocation.put(enemyPlayer, expectBase);
                mainBaseLocationChanged.put(enemyPlayer, new Boolean(true));
            }
        }
    }

    /// 해당 Region 에 해당 Player의 건물이 존재하는지 리턴합니다
    public boolean existsPlayerBuildingInRegion(Region region, Player player) {
        // invalid regions aren't considered the same, but they will both be null
        if (region == null || player == null) {
            return false;
        }

        if (player == PlayerUtils.myPlayer()) {
            for (Unit ui : UnitUtils.getUnitList()) {
                if (ui.getType().isBuilding()) {

                    // Terran 종족의 Lifted 건물의 경우, BWTA.getRegion 결과가 null 이다
                    if (BWTA.getRegion(ui.getPosition()) == null)
                        continue;

                    if (BWTA.getRegion(ui.getPosition()) == region) {
                        return true;
                    }
                }
            }
        }

        if (player == PlayerUtils.enemyPlayer()) {
            for (UnitInfo ui : UnitUtils.getEnemyUnitInfoList()) {

                if (ui.getType().isBuilding()) {

                    // Terran 종족의 Lifted 건물의 경우, BWTA.getRegion 결과가 null 이다
                    if (BWTA.getRegion(ui.getLastPosition()) == null)
                        continue;

                    if (BWTA.getRegion(ui.getLastPosition()) == region) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /// 해당 BaseLocation 주위 10타일 반경 내에 player의 건물이 존재하는지 리턴합니다
    /// @param baseLocation 대상 BaseLocation
    /// @param player 아군 / 적군
    public boolean hasBuildingAroundBaseLocation(BaseLocation baseLocation, Player player) {
        return hasBuildingAroundBaseLocation(baseLocation, player, 10);
    }

    public boolean hasBuildingAroundBaseLocation(BaseLocation baseLocation, Player player, int radius) {

        // invalid regions aren't considered the same, but they will both be null
        if (baseLocation == null) {
            return false;
        }
        // 반지름 10 (TilePosition 단위) 이면 거의 화면 가득이다
        if (radius > 10) {
            radius = 10;
        }


        if (player == PlayerUtils.myPlayer()) {
            for (Unit ui : UnitUtils.getUnitList()) {
                if (ui.getType().isBuilding()) {

                    TilePosition buildingPosition = ui.getTilePosition();

                    if (BWTA.getRegion(buildingPosition) != BWTA.getRegion(baseLocation.getTilePosition())) { // basicbot
                        // 1.2
                        continue;
                    }

                    if (buildingPosition.getX() >= baseLocation.getTilePosition().getX() - radius
                            && buildingPosition.getX() <= baseLocation.getTilePosition().getX() + radius
                            && buildingPosition.getY() >= baseLocation.getTilePosition().getY() - radius
                            && buildingPosition.getY() <= baseLocation.getTilePosition().getY() + radius) {
                        return true;
                    }
                }
            }
        }

        if (player == PlayerUtils.enemyPlayer()) {
            for (UnitInfo ui : UnitUtils.getEnemyUnitInfoList()) {

                if (ui.getType().isBuilding()) {

                    TilePosition buildingPosition = ui.getLastPosition().toTilePosition();

                    if (BWTA.getRegion(buildingPosition) != BWTA.getRegion(baseLocation.getTilePosition())) { // basicbot
                        // 1.2
                        continue;
                    }

                    if (buildingPosition.getX() >= baseLocation.getTilePosition().getX() - radius
                            && buildingPosition.getX() <= baseLocation.getTilePosition().getX() + radius
                            && buildingPosition.getY() >= baseLocation.getTilePosition().getY() - radius
                            && buildingPosition.getY() <= baseLocation.getTilePosition().getY() + radius) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void updateChokePointAndExpansionLocation() {

        Position Center = new Position(2048, 2048);
        if (mainBaseLocationChanged.get(selfPlayer).booleanValue() == true) {

            if (mainBaseLocation.get(selfPlayer) != null) {
                BaseLocation sourceBaseLocation = mainBaseLocation.get(selfPlayer);
                System.out.println("* my base changed" + sourceBaseLocation.getTilePosition());

                firstChokePoint.put(selfPlayer, BWTA.getNearestChokepoint(sourceBaseLocation.getTilePosition()));

                double tempDistance;
                double closestDistance = 1000000000;
                for (BaseLocation targetBaseLocation : BWTA.getBaseLocations()) {
                    if (targetBaseLocation.getTilePosition().equals(mainBaseLocation.get(selfPlayer).getTilePosition()))
                        continue;

                    tempDistance = PositionUtils.getGroundDistance(sourceBaseLocation.getPosition(), targetBaseLocation.getPosition());

                    if (tempDistance < closestDistance && tempDistance > 0) {
                        closestDistance = tempDistance;
                        firstExpansionLocation.put(selfPlayer, targetBaseLocation);
                    }
                }

                closestDistance = 1000000000;
                for (Chokepoint chokepoint : BWTA.getChokepoints()) {
                    if (chokepoint.getCenter().equals(firstChokePoint.get(selfPlayer).getCenter()))
                        continue;

                    tempDistance = PositionUtils.getGroundDistance(sourceBaseLocation.getPosition(),
                            chokepoint.getPoint()) * 1.1;
                    tempDistance += PositionUtils.getGroundDistance(Center, chokepoint.getPoint());
//					tempDistance = BWTA.getGroundDistance(sourceBaseLocation.getTilePosition(), chokepoint.getCenter().toTilePosition()); //욱스가 주석 남기라고 함
                    if (tempDistance < closestDistance && tempDistance > 0) {
                        closestDistance = tempDistance;
                        secondChokePoint.put(selfPlayer, chokepoint);

                        double radian = MicroUtils.targetDirectionRadian(
                                firstExpansionLocation.get(selfPlayer).getPosition(),
                                secondChokePoint.get(selfPlayer).getCenter());
                        Region myThirdRegion = BWTA.getRegion(
                                MicroUtils.getMovePosition(secondChokePoint.get(selfPlayer).getCenter(), radian, 100));
                        thirdRegion.put(selfPlayer, myThirdRegion);
                    }
                }
                updateOtherExpansionLocation(sourceBaseLocation);
            }
            mainBaseLocationChanged.put(selfPlayer, new Boolean(false));
        }




    }

    public void updateOtherExpansionLocation(BaseLocation baseLocation) {

        final BaseLocation myBase = mainBaseLocation.get(selfPlayer);
        final BaseLocation myFirstExpansion = firstExpansionLocation.get(selfPlayer);

        final BaseLocation enemyBase = mainBaseLocation.get(enemyPlayer);
        final BaseLocation enemyFirstExpansion = firstExpansionLocation.get(enemyPlayer);

        if (myBase == null || myFirstExpansion == null || enemyBase == null || enemyFirstExpansion == null) {
            return;
        }

        otherExpansionLocations.clear();

        Set<TilePosition> tileSet = new HashSet<>();
        tileSet.add(myBase.getTilePosition());
        tileSet.add(myFirstExpansion.getTilePosition());
        tileSet.add(enemyBase.getTilePosition());
        tileSet.add(enemyFirstExpansion.getTilePosition());

        for (BaseLocation base : BWTA.getBaseLocations()) {
            // BaseLocation을 equal로 비교하면 오류가 있을 수 있다.
            if (tileSet.contains(base.getTilePosition())) {
                System.out.println(tileSet + " skiped");
                continue;
            }
            if (base.minerals() < 1000) {
                System.out.println(tileSet + " skiped(mineral)");
                continue;
            }
            otherExpansionLocations.add(base);
        }

//		System.out.println("tileSet: " + tileSet);
//		System.out.println("otherExpansionLocations: " + otherExpansionLocations);
    }

    public void updateChokePointAndExpansionLocationEnemy() {

        if (mainBaseLocationChanged.get(enemyPlayer).booleanValue() == true) {

            if (mainBaseLocation.get(enemyPlayer) != null && mainBaseLocation.get(selfPlayer) != null) {
                BaseLocation enemySourceBaseLocation = mainBaseLocation.get(enemyPlayer);
                BaseLocation mySourceBaseLocation = mainBaseLocation.get(selfPlayer);
                System.out.println("* enemy base changed" + enemySourceBaseLocation.getTilePosition());

                firstChokePoint.put(enemyPlayer, BWTA.getNearestChokepoint(enemySourceBaseLocation.getTilePosition()));

                double tempDistance;
                double closestDistance = 1000000000;
                for (BaseLocation targetBaseLocation : BWTA.getBaseLocations()) {
                    if (targetBaseLocation.getTilePosition()
                            .equals(mainBaseLocation.get(enemyPlayer).getTilePosition()))
                        continue;

                    tempDistance = PositionUtils.getGroundDistance(enemySourceBaseLocation.getPosition(),
                            targetBaseLocation.getPosition());
                    if (tempDistance < closestDistance && tempDistance > 0) {
                        closestDistance = tempDistance;
                        firstExpansionLocation.put(enemyPlayer, targetBaseLocation);
                    }
                }

                closestDistance = 1000000000;
                for (Chokepoint chokepoint : BWTA.getChokepoints()) {
                    if (chokepoint.getCenter().equals(firstChokePoint.get(enemyPlayer).getCenter()))
                        continue;

                    tempDistance = PositionUtils.getGroundDistance(enemySourceBaseLocation.getPosition(),
                            chokepoint.getPoint()) * 1.1;
                    tempDistance += PositionUtils.getGroundDistance(CommonCode.Center, chokepoint.getPoint());
                    if (tempDistance < closestDistance && tempDistance > 0) {
                        closestDistance = tempDistance;
                        secondChokePoint.put(enemyPlayer, chokepoint);

                        double radian = MicroUtils.targetDirectionRadian(
                                firstExpansionLocation.get(enemyPlayer).getPosition(),
                                secondChokePoint.get(enemyPlayer).getCenter());
                        Region enemyThirdRegion = BWTA.getRegion(
                                MicroUtils.getMovePosition(secondChokePoint.get(enemyPlayer).getCenter(), radian, 100));
                        thirdRegion.put(enemyPlayer, enemyThirdRegion);
                    }
                }

                double tempDistanceFromSelf;
                double tempDistanceFromEnemy;
                double tempDistanceForHunter = 0;
                closestDistance = 1000000000;
                for (Chokepoint chokepoint : BWTA.getChokepoints()) {
                    tempDistanceFromSelf = PositionUtils.getGroundDistance(mySourceBaseLocation.getPosition(),
                            chokepoint.getPoint());
                    tempDistanceFromEnemy = PositionUtils.getGroundDistance(enemySourceBaseLocation.getPosition(),
                            chokepoint.getPoint());
//						tempDistance = BWTA.getGroundDistance(sourceBaseLocation.getTilePosition(), chokepoint.getCenter().toTilePosition()); //욱스가 주석 남기라고 함
                    if (tempDistanceForHunter < closestDistance && tempDistanceFromEnemy - tempDistanceFromSelf > 0) {
                        closestDistance = tempDistanceForHunter;
                    }
                }

                //TODO needed?
                //this.updateReadyToAttackPosition();
                this.updateOtherExpansionLocation(enemySourceBaseLocation);

                //TODO needed?
                // this.updateMySecondBaseLocation();
            }
            mainBaseLocationChanged.put(enemyPlayer, new Boolean(false));
        }
    }



    public void updateOccupiedRegions(Region region, Player player) {
        // if the region is valid (flying buildings may be in null regions)
        if (region != null) {
            // add it to the list of occupied regions
            if (occupiedRegions.get(player) == null) {
                occupiedRegions.put(player, new HashSet<Region>());
            }
            occupiedRegions.get(player).add(region);
        }
    }

}
