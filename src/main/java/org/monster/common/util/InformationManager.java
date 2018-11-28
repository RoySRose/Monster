//package org.monster.common.util;
//
//import bwapi.Player;
//import bwapi.Position;
//import bwapi.Race;
//import bwapi.TilePosition;
//import bwapi.Unit;
//import bwta.BWTA;
//import bwta.BaseLocation;
//import bwta.Chokepoint;
//import bwta.Polygon;
//import bwta.Region;
//import org.monster.common.UnitInfo;
//import org.monster.main.GameManager;
//import org.monster.main.Monster;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///// 게임 상황정보 중 일부를 자체 자료구조 및 변수들에 저장하고 업데이트하는 class<br>
///// 현재 게임 상황정보는 BWAPI::Broodwar 를 조회하여 파악할 수 있지만, 과거 게임 상황정보는 BWAPI::Broodwar 를 통해 조회가 불가능하기 때문에 InformationManager에서 별도 관리하도록 합니다<br>
///// 또한, BWAPI::Broodwar 나 BWTA 등을 통해 조회할 수 있는 정보이지만 전처리 / 별도 관리하는 것이 유용한 것도 InformationManager에서 별도 관리하도록 합니다
//public class InformationManager extends GameManager {
//    private static InformationManager instance = new InformationManager();
//
//    public Player selfPlayer; /// < 아군 Player
//    public Player enemyPlayer; /// < 적군 Player
//    public Race selfRace; /// < 아군 Player의 종족
//    public Race enemyRace; /// < 적군 Player의 종족
//
//
//    // private boolean EarlyDefenseNeeded;
//    // private boolean ScoutDefenseNeeded;
//    public int barrackStart;
//    public Unit firstBarrack;
//    public BaseLocation getExpansionLocation;
//    public TilePosition getLastBuildingLocation;
//    public TilePosition getLastBuildingFinalLocation;
//    private boolean ReceivingEveryMultiInfo;
//    //	private Unit enemyFirstGas;
//
//    //	private int MainBaseSuppleLimit;
//    // 입막시 방어 안전 위치
//    private Position safePosition;
///// 해당 Player의 주요 건물들이 있는 BaseLocation. <br>
///// 처음에는 StartLocation 으로 지정. mainBaseLocation 내 모든 건물이 파괴될 경우 재지정<br>
///// 건물 여부를 기준으로 파악하기 때문에 부적절하게 판단할수도 있습니다
//private Map<Player, BaseLocation> mainBaseLocations = new HashMap<Player, BaseLocation>();
///// 해당 Player의 mainBaseLocation 이 변경되었는가 (firstChokePoint, secondChokePoint,
///// firstExpansionLocation 를 재지정 했는가)
//private Map<Player, Boolean> mainBaseLocationChanged = new HashMap<Player, Boolean>();
///// 해당 Player가 점령하고 있는 Region 이 있는 BaseLocation<br>
///// 건물 여부를 기준으로 파악하기 때문에 부적절하게 판단할수도 있습니다
//private Map<Player, List<BaseLocation>> occupiedBaseLocations = new HashMap<Player, List<BaseLocation>>();
//    /// 해당 Player가 점령하고 있는 Region<br>
//    /// 건물 여부를 기준으로 파악하기 때문에 부적절하게 판단할수도 있습니다
//private Map<Player, Set<Region>> occupiedRegions = new HashMap<Player, Set<Region>>();
//    /// 해당 Player의 mainBaseLocation 에서 가장 가까운 ChokePoint
//private Map<Player, Chokepoint> firstChokePoint = new HashMap<Player, Chokepoint>();
//    /// 해당 Player의 mainBaseLocation 에서 가장 가까운 BaseLocation
//
//private Map<Player, BaseLocation> firstExpansionLocation = new HashMap<Player, BaseLocation>();
//private Map<Player, Region> thirdRegion = new HashMap<Player, Region>();
//    /// 해당 Player의 mainBaseLocation 에서 두번째로 가까운 (firstChokePoint가 아닌) ChokePoint<br>
//    /// 게임 맵에 따라서, secondChokePoint 는 일반 상식과 다른 지점이 될 수도 있습니다
//private Map<Player, Chokepoint> secondChokePoint = new HashMap<Player, Chokepoint>();
//    // 나머지 멀티 location
//    private List<BaseLocation> otherExpansionLocations = new ArrayList<BaseLocation>();
//    /// 센터 진출로
//    private Map<Player, Position> readyToAttackPosition = new HashMap<Player, Position>();
//    /// Player - UnitData(각 Unit 과 그 Unit의 UnitInfo 를 Map 형태로 저장하는 자료구조) 를 저장하는 자료구조
//    /// 객체
////    private Map<Player, UnitData> unitData = new HashMap<Player, UnitData>();
//private List<BaseLocation> islandBaseLocations = new ArrayList<BaseLocation>();
//    /// base location의 꼭지점 (정찰시 활용)
//
//    /// occupiedRegions에 존재하는 시야 상의 적 Unit 정보
//    private Map<Region, List<UnitInfo>> euiListInMyRegion = new HashMap<>();
//    private Set<UnitInfo> euisInBaseRegion = new HashSet<>();
//    private Set<UnitInfo> euisInExpansionRegion = new HashSet<>();
//    private Set<UnitInfo> euisInThirdRegion = new HashSet<>();
////private Map<Player, List<BaseLocation>> occupiedByCCBaseLocations = new HashMap<Player, List<BaseLocation>>();
//
//    public InformationManager() {
//        selfPlayer = Monster.Broodwar.self();
//        enemyPlayer = Monster.Broodwar.enemy();
//        selfRace = selfPlayer.getRace();
//        enemyRace = enemyPlayer.getRace();
//
//        ReceivingEveryMultiInfo = false;
//
//        barrackStart = -1;
//        firstBarrack = null;
//
//        safePosition = null;
//
//
////        unitData.put(selfPlayer, new UnitData());
////        unitData.put(enemyPlayer, new UnitData());
//
//occupiedBaseLocations.put(selfPlayer, new ArrayList<BaseLocation>());
////occupiedByCCBaseLocations.put(selfPlayer, new ArrayList<BaseLocation>());
//occupiedBaseLocations.put(enemyPlayer, new ArrayList<BaseLocation>());
//        occupiedRegions.put(selfPlayer, new HashSet<Region>());
//        occupiedRegions.put(enemyPlayer, new HashSet<Region>());
//
//mainBaseLocations.put(selfPlayer, BWTA.getStartLocation(Monster.Broodwar.self()));
//mainBaseLocationChanged.put(selfPlayer, new Boolean(true));
//
//occupiedBaseLocations.get(selfPlayer).add(mainBaseLocations.get(selfPlayer));
////occupiedByCCBaseLocations.get(selfPlayer).add(mainBaseLocations.get(selfPlayer));
//        if (mainBaseLocations.get(selfPlayer) != null) {
//            updateOccupiedRegions(BWTA.getRegion(mainBaseLocations.get(selfPlayer).getTilePosition()),
//                    Monster.Broodwar.self());
//        }
//
////		BaseLocation sourceBaseLocation = mainBaseLocation.get(selfPlayer);
//for (BaseLocation targetBaseLocation : BWTA.getBaseLocations()) {
////			if (!BWTA.isConnected(targetBaseLocation.getTilePosition(), sourceBaseLocation.getTilePosition())){
//if (targetBaseLocation.isIsland()) {
//    islandBaseLocations.add(targetBaseLocation);
//}
//}
//
//        mainBaseLocations.put(enemyPlayer, null);
//        mainBaseLocationChanged.put(enemyPlayer, new Boolean(false));
//
//        firstChokePoint.put(selfPlayer, null);
//        firstChokePoint.put(enemyPlayer, null);
//        firstExpansionLocation.put(selfPlayer, null);
//        firstExpansionLocation.put(enemyPlayer, null);
//        thirdRegion.put(selfPlayer, null);
//        thirdRegion.put(enemyPlayer, null);
//        secondChokePoint.put(selfPlayer, null);
//        secondChokePoint.put(enemyPlayer, null);
//
//        readyToAttackPosition.put(selfPlayer, null);
//        readyToAttackPosition.put(enemyPlayer, null);
//
//    }
//
//    /// static singleton 객체를 리턴합니다
//    public static InformationManager Instance() {
//        return instance;
//    }
//
//
//
//    /// Unit 및 BaseLocation, ChokePoint 등에 대한 정보를 업데이트합니다
//    public void update() {
//        updateEnemiesLocation();
//
//    }
//
//    /// occupiedRegions에 존재하는 시야 상의 적 Unit 정보
//    private void updateEnemiesLocation() {
//        euiListInMyRegion.clear();
//        euisInBaseRegion.clear();
//        euisInExpansionRegion.clear();
//        euisInThirdRegion.clear();
//
//        Set<Region> myRegionSet = occupiedRegions.get(selfPlayer);
//        for (Region region : myRegionSet) {
//            euiListInMyRegion.put(region, new ArrayList<UnitInfo>());
//        }
//
//        Region myBaseRegion = BWTA.getRegion(mainBaseLocations.get(selfPlayer).getPosition());
//        Region myExpansionRegion = BWTA.getRegion(firstExpansionLocation.get(selfPlayer).getPosition());
//        Region myThirdRegion = BWTA.getRegion(thirdRegion.get(selfPlayer).getCenter());
//
////        Map<Integer, UnitInfo> unitAndUnitInfoMap = unitData.get(enemyPlayer).getUnitAndUnitInfoMap();
//        for (UnitInfo eui : unitAndUnitInfoMap.values()) {
//            if (UnitUtils.ignorableEnemyUnitInfo(eui)) {
//                continue;
//            }
//            if (!PositionUtils.isValidPosition(eui.getLastPosition())) {
////				System.out.println("updateEnemiesInMyRegion. invalid eui=" + eui);
//                continue;
//            }
//
//            Region region = BWTA.getRegion(eui.getLastPosition());
//            if (region == null) {
//                continue;
//            }
//
//            if (myRegionSet.contains(region)) {
//                List<UnitInfo> euiList = euiListInMyRegion.get(region);
//                euiList.add(eui);
//                euiListInMyRegion.put(region, euiList);
//            }
//
//            if (region.equals(myBaseRegion)) {
//                euisInBaseRegion.add(eui);
//            } else if (region.equals(myExpansionRegion)) {
//                euisInExpansionRegion.add(eui);
//            } else if (region.equals(myThirdRegion)) {
//                euisInThirdRegion.add(eui);
//            }
//        }
//    }
//
//    public void updateOccupiedRegions(Region region, Player player) {
//        // if the region is valid (flying buildings may be in null regions)
//        if (region != null) {
//            // add it to the list of occupied regions
//            if (occupiedRegions.get(player) == null) {
//                occupiedRegions.put(player, new HashSet<Region>());
//            }
//            occupiedRegions.get(player).add(region);
//        }
//    }
//
//}