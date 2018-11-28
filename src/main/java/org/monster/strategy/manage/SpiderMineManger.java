package org.monster.strategy.manage;

import bwapi.Position;
import bwapi.Race;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;
import bwta.Region;
import org.monster.board.StrategyBoard;
import org.monster.common.LagObserver;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.BaseLocationUtils;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.InfoUtils;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.PositionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.common.util.internal.IConditions;
import org.monster.common.util.internal.MapSpecificInformation;
import org.monster.decisions.constant.EnemyStrategyOptions;
import org.monster.main.Monster;
import org.monster.micro.PositionReserveInfo;
import org.monster.micro.constant.MicroConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpiderMineManger {

    private static final int RESV_EXPIRE_FRAME = 24 * 3;
    private static final int MINE_REMOVE_TANK_DIST = 150;
    private static final int MAX_MINE_COUNT = 150;
//	private static final int MINE_BETWEEN_DIST = 50;
    private static final List<Position> GOOD_POSITIONS = new ArrayList<>(); // 마인 심기 좋은 지역
    private static final List<Position> GOOD_POSITIONS_LATE = new ArrayList<>(); // 마인 심기 좋은 지역
    private static SpiderMineManger instance = new SpiderMineManger();
    private Map<Integer, PositionReserveInfo> mineRemoveMap = new HashMap<>(); // key : spider mine id
    private Map<Integer, PositionReserveInfo> mineReservedMap = new HashMap<>(); // key : vulture id
    private boolean initialized = false;
    private boolean secondInitialized = false;
    private int mineInNextExpansionFrame = CommonCode.UNKNOWN;
    private SpiderMineManger() {
    }

    public static SpiderMineManger Instance() {
        return instance;
    }

    // goodPositions 단계적으로 변화. ex) 초반에는 세번째, 네번째 멀티, 그 후에는 점차 증가
    public boolean init() {
        if (!MicroConfig.Upgrade.hasResearched(TechType.Spider_Mines)) {
            return false;
        }

        List<BaseLocation> otherBases = BaseUtils.enemyOtherExpansions();
        Position myReadyToAttackPos = InfoUtils.myReadyToPosition();
        Chokepoint mySecondChoke = InfoUtils.mySecondChoke();

        Position enemyReadyToAttackPos = InfoUtils.enemyReadyToPosition();
        BaseLocation enemyFirstExpansion = BaseUtils.enemyFirstExpansion();
        Chokepoint enemySecondChoke = InfoUtils.enemySecondChoke();

        if (!otherBases.isEmpty() && myReadyToAttackPos != null && mySecondChoke != null
                && enemyReadyToAttackPos != null && enemyFirstExpansion != null && enemySecondChoke != null) {
            List<BaseLocation> myExpansions = this.getMyExpansionBaseLocation();

            // 3rd 멀티지역
            for (BaseLocation base : otherBases) {
                if (!myExpansions.contains(base)) {
                    if (base.getGroundDistance(enemyFirstExpansion) > 2500) {
                        GOOD_POSITIONS_LATE.add(base.getPosition());
                    } else {
                        GOOD_POSITIONS.add(base.getPosition());
                    }
                }
            }
//			System.out.println("good position      : " + GOOD_POSITIONS);
//			System.out.println("late good position : " + GOOD_POSITIONS_LATE);

            // 공격준비지역
//			GOOD_POSITIONS.add(myReadyToAttackPos);
//			GOOD_POSITIONS.add(mySecondChoke.getCenter());

            GOOD_POSITIONS.add(enemyReadyToAttackPos);
            GOOD_POSITIONS.add(enemySecondChoke.getCenter());
            GOOD_POSITIONS.add(enemyFirstExpansion.getPosition());

            return true;
        }
        return false;
    }

    private boolean secondInit() {
        GOOD_POSITIONS.addAll(GOOD_POSITIONS_LATE);
        System.out.println("late good position added : " + GOOD_POSITIONS_LATE);
        return true;
    }

    public void update() {
        if (!initialized) {
            initialized = init();
            return;
        }
        if (!secondInitialized && UnitUtils.activatedCommandCenterCount() >= 2) {
            secondInitialized = secondInit();
        }

        updateMineReservedMap(); // 만료 매설 만료시간 관리
        updateMineRemoveMap(); // 만료 제거 만료시간 관리
        updateVulturePolicy(); // 벌처 정책 관리
    }

    private void updateVulturePolicy() {
        if (!initialized) {
            return;
        }

        int vultureCount = UnitUtils.getUnitCount(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Vulture);
        int mineCount = UnitUtils.getUnitCount(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Vulture_Spider_Mine);

        int mineNumPerPosition = Math.min(vultureCount / 3 + 1, 8);
        if (StrategyBoard.buildTimeMap.featureEnabled(EnemyStrategyOptions.BuildTimeMap.Feature.DEFENSE_FRONT)) {
            mineNumPerPosition += 2;
        } else if (mineCount > MAX_MINE_COUNT) {
            mineNumPerPosition = 1;
        }

        SpiderMineManger.MinePositionLevel mLevel = SpiderMineManger.MinePositionLevel.NOT_MY_OCCUPIED;
        if (StrategyBoard.buildTimeMap.featureEnabled(EnemyStrategyOptions.BuildTimeMap.Feature.DEFENSE_DROP)
                || StrategyBoard.buildTimeMap.featureEnabled(EnemyStrategyOptions.BuildTimeMap.Feature.DETECT_IMPORTANT)) {
            mLevel = SpiderMineManger.MinePositionLevel.ANYWHERE;
        }
        StrategyBoard.watcherMinePositionLevel = mLevel;
        StrategyBoard.spiderMineNumberPerPosition = mineNumPerPosition;
        StrategyBoard.spiderMineNumberPerGoodPosition = vultureCount < 15 ? 1 : 2;
        int checkerCount = Math.min(vultureCount / 8, MicroConfig.Vulture.CHECKER_MAX_COUNT);
        if (UnitUtils.getUnitCount(UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_Goliath, UnitType.Terran_Wraith) >= 5 && checkerCount == 0) {
            checkerCount = 1;
        }
        StrategyBoard.checkerMaxNumber = checkerCount;
    }

    private void updateMineReservedMap() {
        List<Integer> expiredList = new ArrayList<>();
        for (Integer vultureId : mineReservedMap.keySet()) {
            PositionReserveInfo mineReserved = mineReservedMap.get(vultureId);
            if (TimeUtils.elapsedFrames(mineReserved.reservedFrame) > RESV_EXPIRE_FRAME) {
                expiredList.add(vultureId);
            }
        }

        for (Integer vultureId : expiredList) {
            mineReservedMap.remove(vultureId);
        }
    }

    private void updateMineRemoveMap() {
        List<Integer> expiredRemoveList = new ArrayList<>();
        for (Integer spiderMineId : mineRemoveMap.keySet()) {
            PositionReserveInfo removeReserved = mineRemoveMap.get(spiderMineId);
            if (TimeUtils.elapsedFrames(removeReserved.reservedFrame) > RESV_EXPIRE_FRAME) {
                expiredRemoveList.add(spiderMineId);
            }
        }
        for (Integer spiderMineId : expiredRemoveList) {
            mineRemoveMap.remove(spiderMineId);
        }

        // 탱크 주변 마인 (테란 상대로는 제거 안함)
        if (PlayerUtils.enemyRace() != Race.Terran) {
            List<Unit> siegeList = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Siege_Tank_Siege_Mode);
            for (Unit siegeTank : siegeList) {
                List<Unit> nearMineList = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, siegeTank.getPosition(), MINE_REMOVE_TANK_DIST, UnitType.Terran_Vulture_Spider_Mine);
                for (Unit mine : nearMineList) {
                    if (mineRemoveMap.get(mine.getID()) == null) {
                        mineRemoveMap.put(mine.getID(), new PositionReserveInfo(mine.getID(), mine.getPosition(), TimeUtils.getFrame()));
                    }
                }
            }
            int mineRemoveDist = MINE_REMOVE_TANK_DIST;
            if (TimeUtils.beforeTime(9, 0)) {
                mineRemoveDist = MINE_REMOVE_TANK_DIST / 2;
            }
            for (Unit siegeTank : siegeList) {
                List<Unit> nearMineList = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, siegeTank.getPosition(), mineRemoveDist, UnitType.Terran_Vulture_Spider_Mine);
                for (Unit mine : nearMineList) {
                    if (mineRemoveMap.get(mine.getID()) == null) {
                        mineRemoveMap.put(mine.getID(), new PositionReserveInfo(mine.getID(), mine.getPosition(), TimeUtils.getFrame()));
                    }
                }
            }
        }

        if (TimeUtils.afterTime(9, 0)) {
            // 다음 확장지역의 스파이더 마인
            if (TimeUtils.elapsedFrames(mineInNextExpansionFrame) > 20 * TimeUtils.SECOND) {

                //TODO expansion logic 변경으로인해 주석. 어차피 불필요한 클래스
//                BaseLocation nextExpansion = InformationManager.Instance().getNextExpansionLocation();
//                if (nextExpansion != null) {
//                    List<Unit> spiderMines = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, nextExpansion.getPosition(), 300, UnitType.Terran_Vulture_Spider_Mine);
//                    for (Unit mine : spiderMines) {
//                        mineRemoveMap.put(mine.getID(), new PositionReserveInfo(mine.getID(), mine.getPosition(), TimeUtils.getFrame() + 20 * TimeUtils.SECOND)); // 20초 이상 길게 유지
//                    }
//                }

                List<BaseLocation> otherExpansions = BaseUtils.enemyOtherExpansions();
                for (BaseLocation base : otherExpansions) {
                    boolean centerExist = false;
                    List<Unit> centerList = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, base.getPosition(), 300, UnitType.Terran_Command_Center);
                    for (Unit center : centerList) {
                        if (center.getDistance(base.getPosition()) < 500) {
                            centerExist = true;
                            break;
                        }
                    }

                    if (centerExist) {
                        List<Unit> nearMineList = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, base.getPosition(), 300, UnitType.Terran_Vulture_Spider_Mine);
                        for (Unit mine : nearMineList) {
                            if (mineRemoveMap.get(mine.getID()) == null) {
                                mineRemoveMap.put(mine.getID(), new PositionReserveInfo(mine.getID(), mine.getPosition(), TimeUtils.getFrame() + 20 * TimeUtils.SECOND));
                            }
                        }
                    }
                }

                mineInNextExpansionFrame = TimeUtils.elapsedFrames();
            }

            // 급해서 본진에 박은 마인 제거
            if (LagObserver.groupsize() <= 10) {
                if (StrategyBoard.watcherMinePositionLevel == SpiderMineManger.MinePositionLevel.NOT_MY_OCCUPIED) {
                    if (InfoUtils.euiListInBase() != null && InfoUtils.euiListInBase().isEmpty()) {
                        List<Unit> spiderMineList = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Vulture_Spider_Mine);

                        Region myBaseRegion = BWTA.getRegion(BaseUtils.myMainBase().getPosition());
                        for (Unit spiderMine : spiderMineList) {
                            Region mineRegion = BWTA.getRegion(spiderMine.getPosition());
                            if (myBaseRegion == mineRegion) {
                                mineRemoveMap.put(spiderMine.getID(), new PositionReserveInfo(spiderMine.getID(), spiderMine.getPosition(), TimeUtils.getFrame()));
                            }
                        }
                    }
                }
            }
        }
    }

    public Position getPositionReserved(Unit vulture) {
        if (!initialized || vulture.getSpiderMineCount() <= 0) {
            return null;
        }
        PositionReserveInfo mineReserved = mineReservedMap.get(vulture.getID());
        if (mineReserved != null) {
            return mineReserved.position;
        } else {
            return null;
        }
    }

    public Unit mineToRemove(Unit vulture) {
        if (!initialized) {
            return null;
        }
        for (Integer mineId : mineRemoveMap.keySet()) {
            PositionReserveInfo removeReserved = mineRemoveMap.get(mineId);

            if (vulture.getDistance(removeReserved.position) < UnitType.Terran_Vulture.groundWeapon().maxRange()) {
                Unit spiderMine = Monster.Broodwar.getUnit(removeReserved.unitId);
                if (UnitUtils.isValidUnit(spiderMine)) {
                    return spiderMine;
                }
            }
        }
        return null;
    }

    public Position reserveSpiderMine(Unit vulture, MinePositionLevel minePositionLevel) {
        if (!initialized || vulture.getSpiderMineCount() == 0) {
            return null;
        }

        if (SpiderMineManger.MinePositionLevel.ONLY_GOOD_POSITION.equals(minePositionLevel)) {
            Position position = positionToMineOnlyGoodPosition(vulture, StrategyBoard.spiderMineNumberPerGoodPosition);
            if (position != null) {
                mineReservedMap.put(vulture.getID(), new PositionReserveInfo(vulture.getID(), position, TimeUtils.getFrame()));
            }
            return position;
        } else {

            int mineNumberPerPosition = StrategyBoard.spiderMineNumberPerPosition;

            boolean defaultMineNumber = true;
            if (PlayerUtils.enemyRace() == Race.Terran) {
                if (vulture.getDistance(BaseUtils.enemyFirstExpansion()) < 800 || vulture.getDistance(InfoUtils.enemyThirdRegion()) < 500) {
                    defaultMineNumber = false;
                    mineNumberPerPosition = Math.min(StrategyBoard.spiderMineNumberPerPosition * 3, 10);
                } else if (vulture.getDistance(InfoUtils.myThirdRegion()) < 800) {
                    defaultMineNumber = false;
                    mineNumberPerPosition = 0; //Math.max(StrategyBoard.spiderMineNumberPerPosition, 1);
                }
            }

            if (defaultMineNumber) {
                boolean vultureInMyOccupied = false;
                Region vultureRegion = BWTA.getRegion(vulture.getPosition());
                for (BaseLocation occupiedBase : BaseUtils.myOccupiedBases()) {
                    if (vultureRegion == BWTA.getRegion(occupiedBase.getPosition())) {
                        vultureInMyOccupied = true;
                        break;
                    }
                }
                if (vultureInMyOccupied) {
                    if (SpiderMineManger.MinePositionLevel.NOT_MY_OCCUPIED.equals(minePositionLevel)) {
                        return null;
                    } else {
                        mineNumberPerPosition = Math.min(StrategyBoard.spiderMineNumberPerPosition, 2); // 자신의 진영이라면 최대 2개
                    }
                }
            }


            Position position = positionToMineNearPosition(vulture, vulture.getPosition(), mineNumberPerPosition);
            if (position != null) {
                mineReservedMap.put(vulture.getID(), new PositionReserveInfo(vulture.getID(), position, TimeUtils.getFrame()));
            }
            return position;
        }
    }

    private Position positionToMineOnlyGoodPosition(Unit vulture, int mineNumberPerPosition) {
        Position positionToMine = null;
        Position nearestGoodPosition = PositionUtils.getClosestPositionToPosition(GOOD_POSITIONS, vulture.getPosition(), 200.0);
        if (nearestGoodPosition != null) {
            positionToMine = positionToMineNearPosition(vulture, nearestGoodPosition, mineNumberPerPosition);
        }
        return positionToMine;
    }

    private Position positionToMineNearPosition(Unit vulture, Position position, int mineNumberPerPosition) {
        Position positionToMine = null;
        List<Unit> unitsOnTile = Monster.Broodwar.getUnitsOnTile(position.toTilePosition());
        if (unitsOnTile.isEmpty()) {
            positionToMine = findMinePosition(position, MicroConfig.Vulture.MINE_EXACT_RADIUS, mineNumberPerPosition);
        }
        if (positionToMine == null) {
            positionToMine = findMinePosition(position, MicroConfig.Vulture.MINE_SPREAD_RADIUS, mineNumberPerPosition);
        }
        return positionToMine;
    }

    // position 기준으로 radius 범위내에 mineNumberPerPosition 숫자만큼 마인이 매설되어야 할때 마인매설 위치를 리턴
    private Position findMinePosition(Position position, int radius, int mineNumberPerPosition) {
        List<Unit> spiderMinesCount = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, position, radius, UnitType.Terran_Vulture_Spider_Mine);
        int reservedCount = numOfMineReserved(position, radius);
        if (spiderMinesCount.size() + reservedCount >= mineNumberPerPosition) {
            return null;
        }

        for (int i = 0; i < 3; i++) {
            Position minePosition = PositionUtils.randomPosition(position, radius);
            if (noProblemToMine(minePosition) && MicroUtils.isSafePlace(minePosition)) { // 문제없다면 없다면 매설
                return minePosition;
            }
        }
        return null;
    }

    private int numOfMineReserved(Position position, int radius) {
        int reservedMineNum = 0;
        for (PositionReserveInfo minReserved : mineReservedMap.values()) {
            if (minReserved.position.getDistance(position) <= radius) {
                reservedMineNum++;
            }
        }
        return reservedMineNum;
    }

    private boolean noProblemToMine(Position positionToMine) {
        // 마인을 심을 수 있는 장소가 아니다.
        if (!PositionUtils.isValidGroundPosition(positionToMine)) {
            return false;
        }

        // 아미 가까운 곳에 마인 매설이예약되었다.
        // for (MineReserved mineReserved : mineReservedMap.values()) {
        // if (position.getDistance(mineReserved.positionToMine) <= MicroConfig.Vulture.MINE_BETWEEN_DIST) {
        // return false;
        // }
        // }

        // 해당 지역에 마인이 매설되어 있다.
        // int exactPosMineNum = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, position, MicroConfig.Vulture.MINE_EXACT_RADIUS, UnitType.Terran_Vulture_Spider_Mine).size();
        // int overlapMine = PlayerUtils.enemyRace() == Race.Terran ? 2 : 1;
        // if (exactPosMineNum >= overlapMine) {
        // return false;
        // }

        // 해당 지역에 아군 시즈탱크, 컴셋 스테이션, SCV 등이 있다면 금지
        List<Unit> units = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, positionToMine, MINE_REMOVE_TANK_DIST,
                UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_SCV, UnitType.Terran_Comsat_Station);
        if (!units.isEmpty()) {
            return false;
        }

        // 첫번째 확장지역 마인매설 금지 처리
        if (positionToMine.getDistance(BaseUtils.myFirstExpansion()) < MINE_REMOVE_TANK_DIST) {
            return false;
        }

        return true;
    }

    public List<BaseLocation> getMyExpansionBaseLocation() {
        List<BaseLocation> bases = new ArrayList<>();

        BaseLocation expansion1 = null;//InformationManager.Instance().getSecondStartPosition();

        //TODO disable
        //List<BaseLocation> addbase = InformationManager.Instance().getFutureCloseButFarFromEnemyLocation();
        //bases.addAll(addbase);
        bases.add(expansion1);

        int times = 2;
        if (InfoUtils.mapInformation().getMap().equals(MapSpecificInformation.GameMap.CIRCUITBREAKER)) {
            times = 3;
        }
        for (int i = 0; i < times; i++) {
            BaseLocation nearBase = nearestToExpansionBase(bases);
            if (nearBase != null) {
                bases.add(nearBase);
            }
        }

        return bases;
    }

    // 우리 base 근처의 base에는 마인을 매설하지 않는다.(꼭 확장을 하지 않더라도 적 기지에 마인을 심는 것이 이득이므로)
    private BaseLocation nearestToExpansionBase(List<BaseLocation> nextExpansionBases) {
        BaseLocation myBase = BaseUtils.myMainBase();
        BaseLocation myFirstExpansion = BaseUtils.myFirstExpansion();
        BaseLocation enemyBase = BaseUtils.enemyMainBase();
        BaseLocation enemyFirstExpansion = BaseUtils.enemyFirstExpansion();

        return BaseLocationUtils.getGroundClosestBaseToPosition(BWTA.getBaseLocations(), myFirstExpansion, new IConditions.BaseCondition() {
            @Override
            public boolean correspond(BaseLocation base) {
                return !nextExpansionBases.contains(base) && !base.equals(myBase) && !base.equals(myFirstExpansion)
                        && !base.equals(enemyBase) && !base.equals(enemyFirstExpansion);
            }
        });
    }

    public enum MinePositionLevel {
        ANYWHERE,
        NOT_MY_OCCUPIED,
        ONLY_GOOD_POSITION
    }


//    public List<BaseLocation> getFutureCloseButFarFromEnemyLocation() {
//        List<BaseLocation> resultBase = new ArrayList<>();
//
//        BaseLocation base1 = null;
//        BaseLocation base2 = null;
//        BaseLocation mainBaseLocation = mainBaseLocations.get(selfPlayer);
//        BaseLocation enemyBaseLocation = mainBaseLocations.get(enemyPlayer);
//
//        BaseLocation firstExpansion = firstExpansionLocation.get(selfPlayer);
//
//
//        double firstExpansionToOccupied = 0;
//        double enemyBaseToOccupied = 0;
//        double closeFromMyExpansionButFarFromEnemy = 0;
//
//        double closestDistance = 1000000000;
//
//        for (BaseLocation base : BWTA.getBaseLocations()) {
//            if (base.isStartLocation())
//                continue;
//            if (base.getTilePosition().equals(mainBaseLocation.getTilePosition()))
//                continue;
//            if (base.getTilePosition().equals(enemyBaseLocation.getTilePosition()))
//                continue;
//            if (base.getTilePosition().equals(firstExpansion.getTilePosition()))
//                continue;
//            if (firstExpansionLocation.get(enemyPlayer) != null) {
//                if (base.getTilePosition().equals(firstExpansionLocation.get(enemyPlayer).getTilePosition()))
//                    continue;
//            }
//            if (base.getTilePosition().equals(secondStartPosition.getTilePosition()))
//                continue;
//
//            TilePosition findGeyser = ConstructionPlaceFinder.Instance()
//                    .getRefineryPositionNear(base.getTilePosition());
//            if (findGeyser != null) {
//                if (findGeyser.getDistance(base.getTilePosition()) * 32 > 300) {
//                    continue;
//                }
//            }
//
//
//            firstExpansionToOccupied = firstExpansion.getGroundDistance(base); // 내 앞마당 ~ 내 점령지역 지상거리
//            enemyBaseToOccupied = enemyBaseLocation.getGroundDistance(base); // 적 베이스 ~ 내 점령지역 지상거리
//            closeFromMyExpansionButFarFromEnemy = firstExpansionToOccupied - enemyBaseToOccupied;
//
//            if (closeFromMyExpansionButFarFromEnemy < closestDistance && firstExpansionToOccupied > 0) {
//                closestDistance = closeFromMyExpansionButFarFromEnemy;
//                base2 = base1;
//                base1 = base;
//            }
//        }
//
//        if (base2 == null) {
//            for (BaseLocation base : BWTA.getBaseLocations()) {
//                if (base.isStartLocation())
//                    continue;
//                if (base.getTilePosition().equals(mainBaseLocation.getTilePosition()))
//                    continue;
//                if (base.getTilePosition().equals(enemyBaseLocation.getTilePosition()))
//                    continue;
//                if (base.getTilePosition().equals(firstExpansion.getTilePosition()))
//                    continue;
//                if (firstExpansionLocation.get(enemyPlayer) != null) {
//                    if (base.getTilePosition().equals(firstExpansionLocation.get(enemyPlayer).getTilePosition()))
//                        continue;
//                }
//                if (base.getTilePosition().equals(secondStartPosition.getTilePosition()))
//                    continue;
//                if (base.getTilePosition().equals(base1.getTilePosition()))
//                    continue;
//
//                TilePosition findGeyser = ConstructionPlaceFinder.Instance()
//                        .getRefineryPositionNear(base.getTilePosition());
//                if (findGeyser != null) {
//                    if (findGeyser.getDistance(base.getTilePosition()) * 32 > 300) {
//                        continue;
//                    }
//                }
//
//                firstExpansionToOccupied = firstExpansion.getGroundDistance(base); // 내 앞마당 ~ 내 점령지역 지상거리
//                enemyBaseToOccupied = enemyBaseLocation.getGroundDistance(base); // 적 베이스 ~ 내 점령지역 지상거리
//                closeFromMyExpansionButFarFromEnemy = firstExpansionToOccupied - enemyBaseToOccupied;
//
//                if (closeFromMyExpansionButFarFromEnemy < closestDistance && firstExpansionToOccupied > 0) {
//                    closestDistance = closeFromMyExpansionButFarFromEnemy;
//                    base2 = base;
//                }
//            }
//        }
//
//        resultBase.add(base1);
//        resultBase.add(base2);
//
//        return resultBase;
//    }
}
