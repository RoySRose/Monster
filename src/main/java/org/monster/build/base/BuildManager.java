package org.monster.build.base;

import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;
import bwta.Region;
import org.monster.bootstrap.Config;
import org.monster.bootstrap.GameManager;
import org.monster.build.constant.BuildConfig;
import org.monster.build.provider.BuildQueueProvider;
import org.monster.common.LagObserver;
import org.monster.common.MetaType;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BuildManager extends GameManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static BuildManager instance = new BuildManager();

    public BuildOrderQueue buildQueue = new BuildOrderQueue();
    public Boolean mainBaseLocationFull;
    public Boolean secondStartLocationFull;
    public Boolean firstChokePointFull;
    public Boolean firstExpansionLocationFull;
    public Boolean secondChokePointFull;
    public Boolean fisrtSupplePointFull;

    private BuildManagerFailureProtector failureProtector = new BuildManagerFailureProtector();

    public BuildManager() {
        mainBaseLocationFull = false;
        secondStartLocationFull = false;
        firstChokePointFull = false;
        firstExpansionLocationFull = false;
        secondChokePointFull = false;
        fisrtSupplePointFull = false;

    }

    public static BuildManager Instance() {
        return instance;
    }

    public void update() {

        if (!TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER4, 0), LagObserver.managerRotationSize())) {
            return;
        }

        if (buildQueue.isEmpty()) {
            return;
        }
        checkBuildOrderQueueDeadlockAndInsert();
        checkBuildOrderQueueDeadlockAndAndFixIt();

        if (buildQueue.isEmpty()) {
            return;
        }

        BuildOrderItem currentItem = buildQueue.getHighestPriorityItem();

        while (!buildQueue.isEmpty()) {
            if (failureProtector.isSuspended(currentItem.metaType)) {
                if (!buildQueue.canSkipCurrentItem()) {
                    break;
                }

                buildQueue.skipCurrentItem();
                currentItem = buildQueue.getItem();
                continue;
            }

            boolean isOkToRemoveQueue = true;

            Position seedPosition;
            if (currentItem.seedLocation != TilePosition.None && currentItem.seedLocation != TilePosition.Invalid
                    && currentItem.seedLocation != TilePosition.Unknown && currentItem.seedLocation.isValid()) {
                seedPosition = currentItem.seedLocation.toPosition();
            } else {
                seedPosition = getSeedPositionFromSeedLocationStrategy(currentItem.seedLocationStrategy);
            }
            Unit producer = getProducer(currentItem.metaType, seedPosition, currentItem.producerID);

            boolean canMake = false;

            if (producer != null) {
                canMake = canMakeNow(producer, currentItem.metaType);
            }
            // if we can make the current item, create it
            if (producer != null && canMake == true) {
                MetaType t = currentItem.metaType;

                if (t.isUnit()) {
                    if (t.getUnitType().isBuilding()) {

                        if (!t.getUnitType().isAddon()) {

                            TilePosition desiredPosition = getDesiredPosition(t.getUnitType(), currentItem.seedLocation, currentItem.seedLocationStrategy);

                            if (desiredPosition != TilePosition.None) {
                                logger.debug("push {} to ConstructionQueue with position {} ", t.getUnitType().toString(), desiredPosition);
                                ConstructionManager.Instance().addConstructionTask(t.getUnitType(), desiredPosition);
                            } else {
                                System.out.println("There is no place to construct :: " + currentItem.metaType.getUnitType() + " :: strategy :: " + currentItem.seedLocationStrategy);
                                if (currentItem.seedLocation != null)
                                    System.out.println(" seedPosition " + currentItem.seedLocation.getX() + "," + currentItem.seedLocation.getY());
                                if (desiredPosition != null)
                                    System.out.println(" desiredPosition " + desiredPosition.getX() + "," + desiredPosition.getY());

                                if (t.getUnitType() == UnitType.Terran_Supply_Depot || t.getUnitType() == UnitType.Terran_Academy || t.getUnitType() == UnitType.Terran_Armory) {
                                    desiredPosition = getDesiredPosition(t.getUnitType(), TilePosition.None, BuildOrderItem.SeedPositionStrategy.NextSupplePoint);
                                } else {
                                    desiredPosition = getDesiredPosition(t.getUnitType(), TilePosition.None, BuildOrderItem.SeedPositionStrategy.MainBaseLocation);
                                }

                                if (desiredPosition != TilePosition.None) {
                                    logger.debug("push {} to ConstructionQueue with recalculated position {} ", t.getUnitType().toString(), desiredPosition);
                                    ConstructionManager.Instance().addConstructionTask(t.getUnitType(), desiredPosition);
                                } else {
                                    System.out.println(" re calculate desiredPosition is null :: delete from quere");
                                    failureProtector.update(currentItem.metaType);
                                    isOkToRemoveQueue = true;
                                }
                            }
                        }
                    } else {
                        producer.train(t.getUnitType());
                    }
                } else if (t.isTech()) {
                    producer.research(t.getTechType());
                } else if (t.isUpgrade()) {
                    producer.upgrade(t.getUpgradeType());
                    if (t.getUpgradeType() == UpgradeType.Terran_Vehicle_Weapons) {
                        BuildQueueProvider.Instance().startUpgrade(t.getUpgradeType());
                    }
                }
                if (isOkToRemoveQueue) {
                    buildQueue.removeCurrentItem();
                }
                // don't actually loop around in here
                break;
            } else if (buildQueue.canSkipCurrentItem()) {
                // skip it and get the next one
                buildQueue.skipCurrentItem();
                currentItem = buildQueue.getItem();
            } else {
                break;
            }
        }
    }

    public Unit getProducer(MetaType t, Position closestTo, int producerID) {
        UnitType producerType = t.whatBuilds();

        List<Unit> candidateProducers = new ArrayList<Unit>();
        List<Unit> selectProducer = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE);

        for (Unit unit : selectProducer) {

            if (unit == null)
                continue;
            if (unit.getType() != producerType) {
                continue;
            }
            if (!unit.exists()) {
                continue;
            }
            if (!unit.isCompleted()) {
                continue;
            }
            if (unit.isTraining()) {
                continue;
            }
            if (!unit.isPowered()) {
                continue;
            }
            if (unit.isLifted()) {
                continue;
            }

            if (unit.isUpgrading() || unit.isResearching()) {
                continue;
            }

            if (producerID != -1 && unit.getID() != producerID) {
                continue;
            }

            if (t.isUnit()) {
                Pair<UnitType, Integer> ReqPair = null;

                Map<UnitType, Integer> requiredUnitsMap = t.getUnitType().requiredUnits();

                boolean able = true;

                if (requiredUnitsMap != null) {
                    Iterator<UnitType> it = requiredUnitsMap.keySet().iterator();

//                    while (it.hasNext()) {
//                        UnitType requiredType = it.next();
//                        if (requiredType.isAddon()) {
//                            if (unit.getAddon() == null || (unit.getAddon().getType() != requiredType)) {
//                                able = false;
//                            }
//                        }
//                    }
                }
                if (!able) continue;
            }
            candidateProducers.add(unit);
        }
        return getClosestUnitToPosition(candidateProducers, closestTo);
    }

    public Unit getClosestUnitToPosition(final List<Unit> units, Position closestTo) {
        if (units.size() == 0) {
            return null;
        }
        // if we don't care where the unit is return the first one we have
        if (closestTo == Position.None || closestTo == Position.Invalid || closestTo == Position.Unknown || closestTo.isValid() == false) {
            return units.get(0);
        }

        Unit closestUnit = null;
        double minDist = 1000000000;

        for (Unit unit : units) {
            if (unit == null)
                continue;

            double distance = unit.getDistance(closestTo);
            if (closestUnit == null || distance < minDist) {
                closestUnit = unit;
                minDist = distance;
            }
        }
        return closestUnit;
    }

    public boolean canMakeNow(Unit producer, MetaType t) {
        if (producer == null) {
            return false;
        }
        boolean canMake = hasEnoughResources(t);
        if (canMake) {
            if (t.isUnit()) {
                canMake = UnitUtils.canMake(t.getUnitType(), producer);
            } else if (t.isTech()) {
                canMake = UpgradeUtils.canResearch(t.getTechType(), producer);
            } else if (t.isUpgrade()) {
                canMake = UpgradeUtils.canUpgrade(t.getUpgradeType(), producer);
            }
        }

        return canMake;
    }

    public TilePosition getDesiredPosition(UnitType unitType, TilePosition seedPosition, BuildOrderItem.SeedPositionStrategy seedPositionStrategy) {
        TilePosition desiredPosition = null;

        mainBaseLocationFull = false;
        secondStartLocationFull = false;
        firstChokePointFull = false;
        firstExpansionLocationFull = false;
        secondChokePointFull = false;
        fisrtSupplePointFull = false;

        int count = 0;
        while (count < 15) {
            count++;
            if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.MainBaseLocation) {
                if (mainBaseLocationFull) {
                    seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.SecondChokePoint;
                }
            } else if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.FirstChokePoint) {
                if (firstChokePointFull) {
                    seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.SecondChokePoint;
                }
            } else if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.FirstExpansionLocation) {
                if (firstExpansionLocationFull) {
                    seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.SecondChokePoint;
                }
            } else if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.NextSupplePoint) {
                if (fisrtSupplePointFull) {
                    seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.MainBaseLocation;
                }
            }

            if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.SecondChokePoint) {
                if (secondChokePointFull) {
                    seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.SecondMainBaseLocation;
                }
            }
            if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.SecondMainBaseLocation) {
                if (secondStartLocationFull) {
                    seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.LastBuilingPoint;
                }
            }

            desiredPosition = ConstructionPlaceFinder.Instance().getBuildLocationWithSeedPositionAndStrategy(unitType, seedPosition, seedPositionStrategy);

            if (desiredPosition == null) {

                if (TilePositionUtils.isValidTilePosition(seedPosition)) {
                    break;
                }

                if (unitType == UnitType.Terran_Supply_Depot || unitType == UnitType.Terran_Academy || unitType == UnitType.Terran_Armory) {
                    if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.MainBaseLocation) {

                        seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.SecondMainBaseLocation;
                    } else if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.SecondMainBaseLocation) {
                        break;
                    }
                }
                if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.SeedPositionSpecified) {
                    break;
                }
                if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.getLastBuilingFinalLocation) {
                    break;
                }
                if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.LastBuilingPoint) {
                    seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.getLastBuilingFinalLocation;
                }
                if (seedPositionStrategy == BuildOrderItem.SeedPositionStrategy.NextExpansionPoint) {
                    break;
                }
            } else {
                break;
            }
        }
        return desiredPosition;
    }

    public int getAvailableMinerals() {
        return PlayerUtils.mineralSelf() - ConstructionManager.Instance().getReservedMinerals();
    }

    public int getAvailableGas() {
        return PlayerUtils.gasSelf() - ConstructionManager.Instance().getReservedGas();
    }

    public boolean hasEnoughResources(MetaType type) {
        if ((type.mineralPrice() <= getAvailableMinerals()) && (type.gasPrice() <= getAvailableGas())) {
            return true;
        }
        return false;
    }

    public BuildOrderQueue getBuildQueue() {
        return buildQueue;
    }

    private Position getSeedPositionFromSeedLocationStrategy(BuildOrderItem.SeedPositionStrategy seedLocationStrategy) {
        Position seedPosition = null;
        Chokepoint tempChokePoint;
        BaseLocation tempBaseLocation;
        TilePosition tempTilePosition = null;
        Region tempBaseRegion;
        int vx, vy;
        double d, theta;
        int bx, by;

        switch (seedLocationStrategy) {
            case MainBaseLocation:
                tempBaseLocation = BaseUtils.myMainBase();
                if (tempBaseLocation != null) {
                    seedPosition = tempBaseLocation.getPosition();
                }
                break;
            case MainBaseBackYard:
                tempBaseLocation = BaseUtils.myMainBase();
                tempChokePoint = ChokePointUtils.myFirstChoke();
                tempBaseRegion = BWTA.getRegion(tempBaseLocation.getPosition());

                if (tempBaseLocation != null && tempChokePoint != null) {

                    // BaseLocation 에서 ChokePoint 로의 벡터를 구한다
                    vx = tempChokePoint.getCenter().getX() - tempBaseLocation.getPosition().getX();
                    vy = (tempChokePoint.getCenter().getY() - tempBaseLocation.getPosition().getY()) * (-1);
                    d = Math.sqrt(vx * vx + vy * vy) * 0.5; // BaseLocation 와 ChokePoint 간 거리보다 조금 짧은 거리로 조정. BaseLocation가 있는 Region은 대부분 직사각형 형태이기 때문
                    theta = Math.atan2(vy, vx + 0.0001); // 라디안 단위

                    // cos(t+90), sin(t+180) 등 삼각함수 Trigonometric functions of allied angles 을 이용. y축에 대해서는 반대부호로 적용

                    // BaseLocation 에서 ChokePoint 반대쪽 방향의 Back Yard : 데카르트 좌표계에서 (cos(t+180) = -cos(t), sin(t+180) = -sin(t))
                    bx = tempBaseLocation.getTilePosition().getX() - (int) (d * Math.cos(theta) / BuildConfig.TILE_SIZE);
                    by = tempBaseLocation.getTilePosition().getY() + (int) (d * Math.sin(theta) / BuildConfig.TILE_SIZE);
                    tempTilePosition = new TilePosition(bx, by);

                    // 해당 지점이 같은 Region 에 속하고 Buildable 한 타일인지 확인
                    if (!tempTilePosition.isValid() || !StaticMapUtils.isBuildable(tempTilePosition, false) || tempBaseRegion != BWTA.getRegion(new Position(bx * BuildConfig.TILE_SIZE, by * BuildConfig.TILE_SIZE))) {

                        // BaseLocation 에서 ChokePoint 방향에 대해 오른쪽으로 90도 꺾은 방향의 Back Yard : 데카르트 좌표계에서 (cos(t-90) = sin(t),   sin(t-90) = - cos(t))
                        bx = tempBaseLocation.getTilePosition().getX() + (int) (d * Math.sin(theta) / BuildConfig.TILE_SIZE);
                        by = tempBaseLocation.getTilePosition().getY() + (int) (d * Math.cos(theta) / BuildConfig.TILE_SIZE);
                        tempTilePosition = new TilePosition(bx, by);

                        if (!tempTilePosition.isValid() || !StaticMapUtils.isBuildable(tempTilePosition, false)) {
                            // BaseLocation 에서 ChokePoint 방향에 대해 왼쪽으로 90도 꺾은 방향의 Back Yard : 데카르트 좌표계에서 (cos(t+90) = -sin(t),   sin(t+90) = cos(t))
                            bx = tempBaseLocation.getTilePosition().getX() - (int) (d * Math.sin(theta) / BuildConfig.TILE_SIZE);
                            by = tempBaseLocation.getTilePosition().getY() - (int) (d * Math.cos(theta) / BuildConfig.TILE_SIZE);
                            tempTilePosition = new TilePosition(bx, by);

                            if (!tempTilePosition.isValid() || !StaticMapUtils.isBuildable(tempTilePosition, false) || tempBaseRegion != BWTA.getRegion(new Position(bx * BuildConfig.TILE_SIZE, by * BuildConfig.TILE_SIZE))) {

                                // BaseLocation 에서 ChokePoint 방향 절반 지점의 Back Yard : 데카르트 좌표계에서 (cos(t),   sin(t))
                                bx = tempBaseLocation.getTilePosition().getX() + (int) (d * Math.cos(theta) / BuildConfig.TILE_SIZE);
                                by = tempBaseLocation.getTilePosition().getY() - (int) (d * Math.sin(theta) / BuildConfig.TILE_SIZE);
                                tempTilePosition = new TilePosition(bx, by);
                            }

                        }
                    }
                    if (tempTilePosition.isValid() == false
                            || StaticMapUtils.isBuildable(tempTilePosition, false) == false) {
                        seedPosition = tempTilePosition.toPosition();
                    } else {
                        seedPosition = tempBaseLocation.getPosition();
                    }
                }
                break;

            case FirstExpansionLocation:
                tempBaseLocation = BaseUtils.myFirstExpansion();
                if (tempBaseLocation != null) {
                    seedPosition = tempBaseLocation.getPosition();
                }
                break;

            case FirstChokePoint:
                tempChokePoint = ChokePointUtils.myFirstChoke();
                if (tempChokePoint != null) {
                    seedPosition = tempChokePoint.getCenter();
                }
                break;

            case SecondChokePoint:
                tempChokePoint = ChokePointUtils.mySecondChoke();
                if (tempChokePoint != null) {
                    seedPosition = tempChokePoint.getCenter();
                }
                break;
            default:
                break;
        }

        return seedPosition;
    }

    /// buildQueue 의 Dead lock 여부를 판단하기 위해, 가장 우선순위가 높은 BuildOrderItem 의 producer 가 존재하게될 것인지 여부를 리턴합니다
    public boolean isProducerWillExist(UnitType producerType) {
        boolean isProducerWillExist = true;

        if (UnitUtils.getCompletedUnitCount(producerType) == 0
                && UnitUtils.getIncompletedUnitCount(producerType) == 0) {
            if (producerType.isBuilding()) {
                if (ConstructionManager.Instance().getConstructionQueueItemCount(producerType, null) == 0) {
                    isProducerWillExist = false;
                }
            }
            //TODO 이거 필요한가?
            // producer 가 건물이 아닌 경우 : producer 가 생성될 예정인지 추가 파악
            // producerType : 일꾼. Larva. Hydralisk, Mutalisk
            else if (producerType == UnitType.Zerg_Larva) {
                if (UnitUtils.getCompletedUnitCount(UnitType.Zerg_Hatchery) == 0) {
                    isProducerWillExist = false;
                }
            }
//            else if (ConstructionManager.Instance().getConstructionQueueItemCount(producerType, null) == 0) {
//                isProducerWillExist = false;
//            }
        }

        return isProducerWillExist;
    }

    public void checkBuildOrderQueueDeadlockAndInsert() {

        BuildOrderQueue buildQueue = BuildManager.Instance().getBuildQueue();
        if (!buildQueue.isEmpty()) {
            BuildOrderItem currentItem = buildQueue.getHighestPriorityItem();

            // 건물이나 유닛의 경우
            if (currentItem.metaType.isUnit()) {
                UnitType unitType = currentItem.metaType.getUnitType();//TODO 가스가 필요한 건물이면서 현재 refinery 가 없으면 짓는다
                final Map<UnitType, Integer> requiredUnits = unitType.requiredUnits();

                Iterator<UnitType> it = requiredUnits.keySet().iterator();
                // 선행 건물/유닛이 있는데
                if (requiredUnits.size() > 0) {
                    while (it.hasNext()) {
                        UnitType requiredUnitType = it.next(); // C++ : u.first;
                        if (requiredUnitType != UnitType.None) {
                            if (UnitUtils.getCompletedUnitCount(requiredUnitType) == 0
                                    && UnitUtils.getIncompletedUnitCount(requiredUnitType) == 0) {
                                // 선행 건물이 건설 예정이지도 않으면 만들기
                                if (requiredUnitType.isBuilding()) {
                                    if (BuildManager.Instance().buildQueue.getItemCount(requiredUnitType) == 0
                                            && ConstructionManager.Instance().getConstructionQueueItemCount(requiredUnitType, null) == 0) {
                                        BuildManager.Instance().buildQueue.queueAsHighestPriority(new MetaType(requiredUnitType), true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public void checkBuildOrderQueueDeadlockAndAndFixIt() {
        // this will be true if any unit is on the first frame if it's training time remaining this can cause issues for the build order search system so don't plan a search on these frames
        boolean canPlanBuildOrderNow = true;
        for (final Unit unit : UnitUtils.getUnitList()) {
            if (unit.getRemainingTrainTime() == 0) {
                continue;
            }

            UnitCommand unitCommand = unit.getLastCommand();
            if (unitCommand != null) {

                UnitCommandType unitCommandType = unitCommand.getUnitCommandType();
                if (unitCommandType != UnitCommandType.None) {
                    if (unitCommand.getUnit() != null) {
                        UnitType trainType = unitCommand.getUnit().getType();
                        if (unit.getRemainingTrainTime() == trainType.buildTime()) {
                            canPlanBuildOrderNow = false;
                            break;
                        }
                    }
                }
            }

        }
        if (!canPlanBuildOrderNow) {
            return;
        }

        // BuildQueue 의 HighestPriority 에 있는 BuildQueueItem 이 skip 불가능한 것인데,
        // 선행조건이 충족될 수 없거나, 실)행이 앞으로도 계속 불가능한 경우, dead lock 이 발생한다
        // 선행 건물을 BuildQueue에 추가해넣을지, 해당 BuildQueueItem 을 삭제할지 전략적으로 판단해야 한다
        BuildOrderQueue buildQueue = BuildManager.Instance().getBuildQueue();
        if (!buildQueue.isEmpty()) {
            BuildOrderItem currentItem = buildQueue.getHighestPriorityItem();

            if (currentItem.blocking == true) {
                boolean isDeadlockCase = false;

                UnitType producerType = currentItem.metaType.whatBuilds();

                if (currentItem.metaType.isUnit()) {
                    UnitType unitType = currentItem.metaType.getUnitType();
                    TechType requiredTechType = unitType.requiredTech();
                    final Map<UnitType, Integer> requiredUnits = unitType.requiredUnits();

                    // 건물을 생산하는 유닛이나, 유닛을 생산하는 건물이 존재하지 않고, 건설 예정이지도 않으면 dead lock
                    if (isProducerWillExist(producerType) == false) {
                        isDeadlockCase = true;
                    }

                    // Refinery 건물의 경우, Refinery 가 건설되지 않은 Geyser가 있는 경우에만 가능
                    if (!isDeadlockCase && unitType == UnitTypeUtils.getRefineryBuildingType(PlayerUtils.myRace())) {

                        boolean hasAvailableGeyser = true;

                        // Refinery가 지어질 수 있는 장소를 찾아본다
                        TilePosition testLocation = getDesiredPosition(unitType, currentItem.seedLocation,
                                currentItem.seedLocationStrategy);

                        // Refinery 를 지으려는 장소를 찾을 수 없으면 dead lock
                        if (testLocation == TilePosition.None || testLocation == TilePosition.Invalid
                                || testLocation.isValid() == false) {
                            //System.out.println("Build Order Dead lock case . Cann't find place to construct " + unitType); // C++ : unitType.getName()
                            hasAvailableGeyser = false;
                        } else {
                            // Refinery 를 지으려는 장소에 Refinery 가 이미 건설되어 있다면 dead lock
                            for (Unit u : UnitUtils.getUnitsOnTile(testLocation)) {
                                if (u.getType().isRefinery() && u.exists()) {
                                    hasAvailableGeyser = false;
                                    break;
                                }
                            }
                        }

                        if (hasAvailableGeyser == false) {
                            isDeadlockCase = true;
                        }
                    }

                    // 선행 기술 리서치가 되어있지 않고, 리서치 중이지도 않으면 dead lock
                    if (!isDeadlockCase && requiredTechType != TechType.None) {
                        if (UpgradeUtils.selfISResearched(requiredTechType) == false) {
                            if (UpgradeUtils.selfISResearching(requiredTechType) == false) {
                                isDeadlockCase = true;
                            }
                        }
                    }

                    int getAddonPossibeCnt = 0;

                    if (currentItem.metaType.getUnitType().isAddon()) {
                        UnitType ProducerType = currentItem.metaType.getUnitType().whatBuilds().first;

                        for (Unit unit : UnitUtils.getUnitList()) {
                            if (ProducerType == unit.getType() && unit.isCompleted()) {

                                if (unit.canBuildAddon() == false) {
                                    continue;
                                }
                                getAddonPossibeCnt++;
                            }
                        }
                        if (getAddonPossibeCnt == 0) {
                            isDeadlockCase = true;
                        }
                    }

                    Iterator<UnitType> it = requiredUnits.keySet().iterator();
                    if (!isDeadlockCase && requiredUnits.size() > 0) {
                        while (it.hasNext()) {
                            UnitType requiredUnitType = it.next(); // C++ : u.first;
                            if (requiredUnitType != UnitType.None) {

                                if (UnitUtils.getCompletedUnitCount(requiredUnitType) == 0
                                        && UnitUtils.getIncompletedUnitCount(requiredUnitType) == 0) {
                                    if (requiredUnitType.isBuilding()) {
                                        if (ConstructionManager.Instance().getConstructionQueueItemCount(requiredUnitType, null) == 0) {
                                            isDeadlockCase = true;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 건물이 아닌 지상/공중 유닛인 경우, 서플라이가 400 꽉 찼으면 dead lock
                    if (!isDeadlockCase && !unitType.isBuilding() && PlayerUtils.supplyTotalSelf() == 400
                            && PlayerUtils.supplyUsedSelf() + unitType.supplyRequired() > 400) {
                        isDeadlockCase = true;
                    }

                    // 건물이 아닌 지상/공중 유닛인데, 서플라이가 부족하면 dead lock 상황이 되긴 하지만,
                    // 이 경우는 빌드를 취소하기보다는, StrategyManager 등에서 서플라이 빌드를 추가함으로써 풀도록 한다
                    if (!isDeadlockCase && !unitType.isBuilding()
                            && PlayerUtils.supplyUsedSelf() + unitType.supplyRequired() > PlayerUtils.supplyTotalSelf()) {
                        //isDeadlockCase = true;
                    }

//                  //Pylon 이 해당 지역 주위에 먼저 지어져야 하는데, Pylon 이 해당 지역 주위에 없고, 예정되어있지도 않으면 dead lock
//					if (!isDeadlockCase && unitType.isBuilding() && unitType.requiresPsi()
//							&& currentItem.seedLocationStrategy == BuildOrderItem.SeedPositionStrategy.SeedPositionSpecified) {
//
//						boolean hasFoundPylon = false;
//						List<Unit> ourUnits = Monster.Broodwar
//								.getUnitsInRadius(currentItem.seedLocation.toPosition(), 4 * Config.TILE_SIZE);
//
//						for (Unit u : ourUnits) {
//							if (u.getPlayer() == Monster.Broodwar.self() && u.getType() == UnitType.Protoss_Pylon) {
//								hasFoundPylon = true;
//							}
//						}
//
//						if (hasFoundPylon == false) {
//							isDeadlockCase = true;
//						}
//					}

//                  //Creep 이 해당 지역 주위에 Hatchery나 Creep Colony 등을 통해 먼저 지어져야 하는데, 해당 지역 주위에 지어지지 않고 있으면 dead lock
                    if (!isDeadlockCase && unitType.isBuilding() && unitType.requiresCreep()
                            && currentItem.seedLocationStrategy == BuildOrderItem.SeedPositionStrategy.SeedPositionSpecified) {
                        boolean hasFoundCreepGenerator = false;
                        List<Unit> ourUnits = UnitUtils.getUnitsInRadius(currentItem.seedLocation.toPosition(), 4 * Config.TILE_SIZE);

                        for (Unit u : ourUnits) {
                            if (u.getPlayer() == PlayerUtils.myPlayer() && (u.getType() == UnitType.Zerg_Hatchery
                                    || u.getType() == UnitType.Zerg_Lair || u.getType() == UnitType.Zerg_Hive
                                    || u.getType() == UnitType.Zerg_Creep_Colony
                                    || u.getType() == UnitType.Zerg_Sunken_Colony
                                    || u.getType() == UnitType.Zerg_Spore_Colony)) {
                                hasFoundCreepGenerator = true;
                            }
                        }

                        if (hasFoundCreepGenerator == false) {
                            isDeadlockCase = true;
                        }
                    }

                }
                // 테크의 경우, 해당 리서치를 이미 했거나, 이미 하고있거나, 리서치를 하는 건물 및 선행건물이 존재하지않고
                // 건설예정이지도 않으면 dead lock
                else if (currentItem.metaType.isTech()) {
                    TechType techType = currentItem.metaType.getTechType();
                    UnitType requiredUnitType = techType.requiredUnit();

                    if (UpgradeUtils.selfISResearched(techType)
                            || UpgradeUtils.selfISResearching(techType)) {
                        isDeadlockCase = true;
                    } else if (UnitUtils.getCompletedUnitCount(producerType) == 0
                            && UnitUtils.getIncompletedUnitCount(producerType) == 0) {
                        if (ConstructionManager.Instance().getConstructionQueueItemCount(producerType, null) == 0) {

                            if (!producerType.isAddon()) {
                                isDeadlockCase = true;
                            }
                        }
                    } else if (requiredUnitType != UnitType.None) {

                        if (UnitUtils.getCompletedUnitCount(requiredUnitType) == 0
                                && UnitUtils.getIncompletedUnitCount(requiredUnitType) == 0) {
                            if (ConstructionManager.Instance().getConstructionQueueItemCount(requiredUnitType,
                                    null) == 0) {
                                isDeadlockCase = true;
                            }
                        }
                    }
                }
                // 업그레이드의 경우, 해당 업그레이드를 이미 했거나, 이미 하고있거나, 업그레이드를 하는 건물 및 선행건물이
                // 존재하지도 않고 건설예정이지도 않으면 dead lock
                else if (currentItem.metaType.isUpgrade()) {
                    UpgradeType upgradeType = currentItem.metaType.getUpgradeType();
                    int maxLevel = UpgradeUtils.getSelfMaxUpgradeLevel(upgradeType);
                    int currentLevel = UpgradeUtils.selfUpgradedLevel(upgradeType);
                    UnitType requiredUnitType = upgradeType.whatsRequired();

                    if (currentLevel >= maxLevel || UpgradeUtils.selfIsUpgrading(upgradeType)) {
                        isDeadlockCase = true;
                    } else if (UnitUtils.getCompletedUnitCount(producerType) == 0
                            && UnitUtils.getIncompletedUnitCount(producerType) == 0) {
                        if (ConstructionManager.Instance().getConstructionQueueItemCount(producerType, null) == 0) {

                            // 업그레이드의 producerType이 Addon 건물인 경우, Addon 건물 건설이 시작되기 직전에는 getUnits, completedUnitCount, incompleteUnitCount 에서 확인할 수 없다 producerType의 producerType 건물에 의해 Addon 건물 건설이 시작되었는지까지 확인해야 한다
                            if (producerType.isAddon()) {

                                boolean isAddonConstructing = false;

                                UnitType producerTypeOfProducerType = producerType.whatBuilds().first;

                                if (producerTypeOfProducerType != UnitType.None) {

                                    for (Unit unit : UnitUtils.getUnitList()) {
                                        if (unit == null)
                                            continue;
                                        if (unit.getType() != producerTypeOfProducerType) {
                                            continue;
                                        }
                                        if (unit.isCompleted() && unit.isConstructing()
                                                && unit.getBuildType() == producerType) {
                                            isAddonConstructing = true;
                                            break;
                                        }
                                    }
                                }

                                if (isAddonConstructing == false) {
                                    isDeadlockCase = true;
                                }
                            } else {
                                isDeadlockCase = true;
                            }
                        }
                    } else if (requiredUnitType != UnitType.None) {
                        if (UnitUtils.getCompletedUnitCount(requiredUnitType) == 0
                                && UnitUtils.getIncompletedUnitCount(requiredUnitType) == 0) {
                            if (ConstructionManager.Instance().getConstructionQueueItemCount(requiredUnitType,
                                    null) == 0) {
                                isDeadlockCase = true;
                            }
                        }
                    }
                }

                if (!isDeadlockCase) {
                    // producerID 를 지정했는데, 해당 ID 를 가진 유닛이 존재하지 않으면 dead lock
                    if (currentItem.producerID != -1) {
                        boolean isProducerAlive = false;
                        for (Unit unit : UnitUtils.getUnitList()) {
                            if (unit != null && unit.getID() == currentItem.producerID && unit.exists() && unit.getHitPoints() > 0) {
                                isProducerAlive = true;
                                break;
                            }
                        }
                        if (isProducerAlive == false) {
                            isDeadlockCase = true;
                        }
                    }
                }

                if (isDeadlockCase) {
                    buildQueue.removeCurrentItem();
                }

            }
        }
    }

    public final boolean isBuildableTile(int x, int y) {
        TilePosition tp = new TilePosition(x, y);
        if (!tp.isValid()) {
            return false;
        }

        if (StaticMapUtils.isBuildable(tp, true) == false) {
            return false;
        }

        if (UnitUtils.getUnitsOnTile(tp).size() > 0) {
            return false;
        }
        return true;
    }
}