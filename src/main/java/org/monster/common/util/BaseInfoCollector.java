package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.constant.EnemyUnitVisibleStatus;
import org.monster.common.constant.RegionType;
import org.monster.common.util.internal.IConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseInfoCollector implements InfoCollector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static BaseInfoCollector instance = new BaseInfoCollector();
    protected static BaseInfoCollector Instance() {
        return instance;
    }

    private Game Broodwar;
    private Player selfPlayer;
    private Player enemyPlayer;
    private EnemyBaseLocator enemyBaseLocator = new EnemyBaseLocator();;
    private ChokeInfoCollector chokeInfoCollector;
    private RegionInfoCollector regionInfoCollector;

    protected Map<Player, BaseLocation> mainBaseLocation = new HashMap();
    protected Map<Player, Boolean> mainBaseLocationChanged = new HashMap();
    protected Map<Player, List<BaseLocation>> occupiedBaseLocations = new HashMap();
    protected Map<Player, BaseLocation> firstExpansionLocation = new HashMap();
    protected List<BaseLocation> islandBaseLocations = new ArrayList();
    //TODO needed?
    protected List<BaseLocation> otherExpansionLocations = new ArrayList();

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
        this.selfPlayer = Broodwar.self();
        this.enemyPlayer = Broodwar.enemy();
        this.chokeInfoCollector = ChokeInfoCollector.Instance();
        this.regionInfoCollector = RegionInfoCollector.Instance();

        this.occupiedBaseLocations.put(selfPlayer, new ArrayList<>());
        this.occupiedBaseLocations.put(enemyPlayer, new ArrayList<>());

        updateIslandBases();
        initilizeBaseLocations();

        //TODO
        //updateChokePointAndExpansionLocation();
    }

    private void updateIslandBases() {
        for (BaseLocation targetBaseLocation : BWTA.getBaseLocations()) {
            if (targetBaseLocation.isIsland()) {
                islandBaseLocations.add(targetBaseLocation);
            }
        }
    }

    private void initilizeBaseLocations() {
        mainBaseLocation.put(selfPlayer, BWTA.getStartLocation(selfPlayer));
        mainBaseLocationChanged.put(selfPlayer, new Boolean(true));
        mainBaseLocation.put(enemyPlayer, null);
        mainBaseLocationChanged.put(enemyPlayer, new Boolean(false));
    }

    @Override
    public void update() {
        findEnemyBaseLocation();
        checkMainBases();
        checkMainBases();
        initializeAndUpdateOccupiedBase();
    }

    private void findEnemyBaseLocation() {
        enemyBaseLocator.search();
    }

    private void checkMainBases() {
        checkMainBaseIfDestroyed(selfPlayer);
        checkMainBaseIfDestroyed(enemyPlayer);

        updateInfoByMainBaseChange(selfPlayer);
        updateInfoByMainBaseChange(enemyPlayer);
    }

    private void checkMainBaseIfDestroyed(Player player) {

        if (mainBaseLocation.get(player) != null) {

            TilePosition baseTile = mainBaseLocation.get(player).getTilePosition();
            if (player == PlayerUtils.enemyPlayer() && !Broodwar.isExplored(baseTile)) {
                return;
            }
            if (existsPlayerBuildingInRegion(BWTA.getRegion(baseTile), player) == false) {
                repickMainBase(player);
            }
        }
    }

    private void repickMainBase(Player player) {
        for (BaseLocation location : occupiedBaseLocations.get(player)) {
            if (existsPlayerBuildingInRegion(BWTA.getRegion(location.getTilePosition()), player)) {
                mainBaseLocation.put(player, location);
                mainBaseLocationChanged.put(player, new Boolean(true));
                break;
            }
        }
    }

    private void updateInfoByMainBaseChange(Player player) {

        if (mainBaseLocationChanged.get(player).booleanValue() == true) {

            if (mainBaseLocation.get(player) != null) {
                BaseLocation myMainBaseLocation = mainBaseLocation.get(player);
                logger.debug(player.getName() + "'s mains base changed" + myMainBaseLocation.getTilePosition());

                //TODO 뒷마당 있는 맵에서는 vector 로 처리하거나, center air distance 추가 필요
                BaseLocation myFirstExpansionLocation = findClosestBase(myMainBaseLocation);
                firstExpansionLocation.put(player, myFirstExpansionLocation);

                chokeInfoCollector.updateClosestChokePoints(player, myMainBaseLocation);
                regionInfoCollector.calculateThirdRegion(player);

                //TODO 이게 정말 필요한것인가?
                //this.updateOtherExpansionLocation(myMainBaseLocation);
            }
            mainBaseLocationChanged.put(player, new Boolean(false));
            TimeInfoCollector.Instance().clearBaseToBaseFrame();
        }
    }

    private boolean existsPlayerBuildingInRegion(Region region, Player player) {
        // invalid regions aren't considered the same, but they will both be null
        if (region == null || player == null) {
            return false;
        }

        if (player == PlayerUtils.myPlayer()) {
            for (Unit unit : UnitUtils.getUnitList()) {
                if (unit.getType().isBuilding()) {

                    // Terran 종족의 Lifted 건물의 경우, BWTA.getRegion 결과가 null 이다
                    if (BWTA.getRegion(unit.getPosition()) == null)
                        continue;

                    if (BWTA.getRegion(unit.getPosition()) == region) {
                        return true;
                    }
                }
            }
        }

        if (player == PlayerUtils.enemyPlayer()) {
            for (UnitInfo unitInfo : UnitUtils.getEnemyUnitInfoList()) {

                if (unitInfo.getType().isBuilding()) {

                    //TODO 아래 로직으로 감지 안될거 같아서 isFlying 추가후 테스트 못함.
                    // Terran 종족의 Lifted 건물의 경우, BWTA.getRegion 결과가 null 이다
                    if (unitInfo.isFlying() && BWTA.getRegion(unitInfo.getLastPosition()) == null)
                        continue;

                    if (BWTA.getRegion(unitInfo.getLastPosition()) == region) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasBuildingAroundBaseLocation(BaseLocation baseLocation, Player player) {
        return hasBuildingAroundBaseLocation(baseLocation, player, 10);
    }

    protected boolean hasBuildingAroundBaseLocation(BaseLocation baseLocation, Player player, int radius) {

        // invalid regions aren't considered the same, but they will both be null
        if (baseLocation == null) {
            return false;
        }

        if (radius > 10) {
            logger.error("radius 10 is almost everything on the screen");
            radius = 10;
        }

        if (player == PlayerUtils.myPlayer()) {
            for (Unit ui : UnitUtils.getUnitList()) {
                if (ui.getType().isBuilding()) {

                    TilePosition buildingPosition = ui.getTilePosition();

                    if (BWTA.getRegion(buildingPosition) != BWTA.getRegion(baseLocation.getTilePosition())) {
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

                    if (BWTA.getRegion(buildingPosition) != BWTA.getRegion(baseLocation.getTilePosition())) {
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

    private void initializeAndUpdateOccupiedBase() {
        if (occupiedBaseLocations.get(selfPlayer) != null) {
            occupiedBaseLocations.get(selfPlayer).clear();
        }
        if (occupiedBaseLocations.get(enemyPlayer) != null) {
            occupiedBaseLocations.get(enemyPlayer).clear();
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
    }

    protected BaseLocation getClosestBaseFromPosition(List<BaseLocation> baseList, Position position) {
        return getClosestBaseFromPosition(baseList, position, new IConditions.BaseCondition() {
            @Override
            public boolean correspond(BaseLocation base) {
                return true;
            }
        });
    }

    /**
     * baseList 중 position에 가장 가까운 base 리턴
     */
    private BaseLocation getClosestBaseFromPosition(List<BaseLocation> baseList, Position position, IConditions.BaseCondition baseCondition) {
        if (baseList.size() == 0) {
            return null;
        }

        if (!PositionUtils.isValidPosition(position)) {
            return baseList.get(0);
        }

        BaseLocation closestBase = null;
        double closestDist = CommonCode.DOUBLE_MAX;

        for (BaseLocation base : baseList) {
            if (!baseCondition.correspond(base)) {
                continue;
            }
            double dist = base.getPosition().getDistance(position);
            if (dist > 0 && dist < closestDist) {
                closestBase = base;
                closestDist = dist;
            }
        }
        return closestBase;
    }

    /**
     * baseList 중 position에 가장 가까운 base 리턴
     */
    protected BaseLocation getGroundClosestBaseFromPosition(List<BaseLocation> baseList, BaseLocation fromBase, IConditions.BaseCondition baseCondition) {
        if (baseList.size() == 0) {
            return null;
        }

        BaseLocation closestBase = null;
        double closestDist = CommonCode.DOUBLE_MAX;

        for (BaseLocation base : baseList) {
            if (equals(base, fromBase)) {
                closestBase = base;
                break;
            }
            if (!baseCondition.correspond(base)) {
                continue;
            }
            double dist = base.getGroundDistance(fromBase);
            if (dist > 0 && dist < closestDist) {
                closestBase = base;
                closestDist = dist;
            }
        }
        return closestBase;
    }

    /**
     * baseList 중 position에 가장 먼 base 리턴
     */
    protected BaseLocation getGroundFarthestBaseFromPosition(List<BaseLocation> baseList, BaseLocation fromBase, IConditions.BaseCondition baseCondition) {
        if (baseList.size() == 0) {
            return null;
        }

        BaseLocation farthestBase = null;
        double farthestDistance = 0;

        for (BaseLocation base : baseList) {
            if (!baseCondition.correspond(base)) {
                continue;
            }
            double dist = base.getGroundDistance(fromBase);
            if (dist > 0 && dist > farthestDistance) {
                farthestBase = base;
                farthestDistance = dist;
            }
        }
        return farthestBase;
    }

    protected boolean equals(BaseLocation a, BaseLocation b) {
        if (a.getTilePosition().equals(b.getTilePosition())) {
            return true;
        } else {
            return false;
        }
    }

    private class EnemyBaseLocator {

        private boolean enemyStartLocationFound;
        private Set<TilePosition> exploredStartLocations;

        public EnemyBaseLocator() {
            enemyStartLocationFound = false;
            exploredStartLocations = new HashSet<>();
        }

        public void search() {
            if (enemyStartLocationFound) {
                return;
            }

            BaseLocation enemyMainBase = locateEnemyMainBase();
            if (enemyMainBase != null) {
                logger.debug("[Looking Enemy Base] Setting EnemyBase");
                updateEnemyMainBaseInfo(enemyMainBase);
            }
        }

        private BaseLocation locateEnemyMainBase() {
            // an unexplored base location holder
            BaseLocation unexplored = null;

            BaseLocation myBaseLocation = mainBaseLocation.get(selfPlayer);
            for (BaseLocation startLocation : BWTA.getStartLocations()) {

                if (myBaseLocation.getTilePosition() != startLocation.getTilePosition() && existsPlayerBuildingInRegion(BWTA.getRegion(startLocation.getTilePosition()), enemyPlayer)) {
                    logger.debug("[Looking Enemy Base] Found EnemyBase : Enemy Building in Region");
                    return startLocation;
                }

                if (!exploredStartLocations.contains(startLocation.getTilePosition()) && Broodwar.isExplored(startLocation.getTilePosition())) {
                    exploredStartLocations.add(startLocation.getTilePosition());
                    // exploredStartLocations++;
                    logger.debug("[Looking Enemy Base] Expect EnemyBase : explored Base increment to {}", exploredStartLocations);
                } else {
                    // otherwise set it as unexplored base
                    unexplored = startLocation;
                }
            }

            // if we've explored every start location except one, it's the enemy
            if (!enemyStartLocationFound && exploredStartLocations.size() == (BWTA.getStartLocations().size() - 1)) {
                logger.debug("[Looking Enemy Base] Expect EnemyBase : Last base not explored");
                return unexplored;
            }

            // 끝까지 상대 location 못 찾았을때
            if (TimeUtils.afterTime(4, 0)) {
                BaseLocation expectBase = null;

                BaseLocation enemyBaseExpected = predict();
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
                    logger.debug("[Looking Enemy Base] Expect EnemyBase : assume since it's too late");
                    return expectBase;
                }
            }

            return null;
        }

        private void updateEnemyMainBaseInfo(BaseLocation startLocation) {
            enemyStartLocationFound = true;
            mainBaseLocation.put(enemyPlayer, startLocation);
            mainBaseLocationChanged.put(enemyPlayer, new Boolean(true));
        }

        //TODO 구조가 마음에 안드네
        private BaseLocation predict() {
            BaseLocation predictedByBuilding = predictedByBuilding();
            if (predictedByBuilding != null) {
                return predictedByBuilding;
            }

            BaseLocation predictedByScout = predictedByUnit();
            if (predictedByScout != null) {
                return predictedByScout;
            }

            BaseLocation rand = null;
            for (BaseLocation startLocation : BWTA.getStartLocations()) {
                if (BaseUtils.equals(startLocation, BaseUtils.myMainBase())) {
                    continue;
                }
                if (Broodwar.isExplored(startLocation.getTilePosition())) {
                    continue;
                }
                rand = startLocation;
            }
            return rand;
        }

        private BaseLocation predictedByBuilding() {
            BaseLocation baseExpected = null;

            for (BaseLocation startLocation : BWTA.getStartLocations()) {
                if (BaseUtils.equals(startLocation, BaseUtils.myMainBase())) {
                    continue;
                }
                if (Broodwar.isExplored(startLocation.getTilePosition())) {
                    continue;
                }

                baseExpected = searchWithBuilding(startLocation, UnitTypeUtils.getBasicResourceDepotBuildingType(PlayerUtils.enemyRace()));
                if (baseExpected == null) {
                    baseExpected = searchWithBuilding(startLocation, UnitTypeUtils.getBasicDefenseBuildingType(PlayerUtils.enemyRace()));
                }
                if (baseExpected == null) {
                    baseExpected = searchWithBuilding(startLocation, UnitTypeUtils.getBasicProduceBuildingType(PlayerUtils.enemyRace()));
                }
                if (baseExpected == null) {
                    baseExpected = searchWithBuilding(startLocation, UnitTypeUtils.getBasicSupplyBuildingType(PlayerUtils.enemyRace()));
                }
            }

            return baseExpected;
        }

        private BaseLocation searchWithBuilding(BaseLocation startLocation, UnitType searchType) {

            BaseLocation baseExpected = null;

            int minimumDistance = CommonCode.INT_MAX;

            for (UnitInfo unitInfo : UnitUtils.getEnemyUnitInfoList(searchType)) {
                if (!unitInfo.getType().isBuilding()) {
                    continue;
                }
                if (unitInfo.getLastPosition().getDistance(CommonCode.CENTER_POS) < 500) {
                    continue;
                }

                int dist = PositionUtils.getGroundDistance(unitInfo.getLastPosition(), startLocation.getPosition());
                if (dist < minimumDistance) {
                    baseExpected = startLocation;
                    minimumDistance = dist;
                }
            }
            return baseExpected;
        }

        private BaseLocation predictedByUnit() {
            int scoutLimitFrames;
            if (PlayerUtils.enemyRace() == Race.Protoss) {
                scoutLimitFrames = TimeUtils.timeToFrames(1, 25); // 9파일런 서치 한번에 왔을때 약 1분 20초
            } else if (PlayerUtils.enemyRace() == Race.Zerg) {
                scoutLimitFrames = TimeUtils.timeToFrames(2, 35); // 9드론 서치 한번에 왔을때 약 2분 30초
            } else {
                scoutLimitFrames = TimeUtils.timeToFrames(2, 0);
            }

            if (TimeUtils.after(scoutLimitFrames)) {
                return null;
            }

            List<UnitInfo> euiList = UnitUtils.getEnemyUnitInfoList(EnemyUnitVisibleStatus.ALL
                    , UnitType.Protoss_Probe, UnitType.Zerg_Drone, UnitType.Terran_SCV, UnitType.Zerg_Overlord
                    , UnitType.Zerg_Zergling, UnitType.Protoss_Zealot, UnitType.Terran_Marine);

            if (euiList.isEmpty()) {
                return null;
            }

            Position scoutPosition = euiList.get(0).getLastPosition();
            RegionType regionType = PositionUtils.positionToRegionType(scoutPosition);


            if (regionType != RegionType.MY_BASE && regionType != RegionType.MY_FIRST_EXPANSION) {
                return null;
            }

            Position fromPosition = BaseUtils.myMainBase().getPosition();
            BaseLocation closestBase = null;
            int minimumDistance = CommonCode.INT_MAX;
            BaseLocation myBase = BaseUtils.myMainBase();
            for (BaseLocation startLocation : BWTA.getStartLocations()) {
                if (startLocation.getTilePosition().equals(myBase.getTilePosition())) {
                    continue;
                }
                if (Broodwar.isExplored(startLocation.getTilePosition())) {
                    continue;
                }

                int groundDistance = PositionUtils.getGroundDistance(startLocation.getPosition(), fromPosition);
                if (groundDistance < minimumDistance) {
                    closestBase = startLocation;
                    minimumDistance = groundDistance;
                }
            }
            return closestBase;
        }
    }

//    public void updateOtherExpansionLocation(BaseLocation baseLocation) {
//
//        final BaseLocation myBase = mainBaseLocation.get(selfPlayer);
//        final BaseLocation myFirstExpansion = firstExpansionLocation.get(selfPlayer);
//
//        final BaseLocation enemyBase = mainBaseLocation.get(enemyPlayer);
//        final BaseLocation enemyFirstExpansion = firstExpansionLocation.get(enemyPlayer);
//
//        if (myBase == null || myFirstExpansion == null || enemyBase == null || enemyFirstExpansion == null) {
//            return;
//        }
//
//        otherExpansionLocations.clear();
//
//        Set<TilePosition> tileSet = new HashSet<>();
//        tileSet.add(myBase.getTilePosition());
//        tileSet.add(myFirstExpansion.getTilePosition());
//        tileSet.add(enemyBase.getTilePosition());
//        tileSet.add(enemyFirstExpansion.getTilePosition());
//
//        for (BaseLocation base : BWTA.getBaseLocations()) {
//            // BaseLocation을 equal로 비교하면 오류가 있을 수 있다.
//            if (tileSet.contains(base.getTilePosition())) {
//                System.out.println(tileSet + " skiped");
//                continue;
//            }
//            if (base.minerals() < 1000) {
//                System.out.println(tileSet + " skiped(mineral)");
//                continue;
//            }
//            otherExpansionLocations.add(base);
//        }
//    }
}
