package org.monster.build.base;

import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import org.monster.common.LagObserver;
import org.monster.common.util.CommandUtils;
import org.monster.common.util.InfoUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.RegionUtils;
import org.monster.common.util.TilePositionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.main.GameManager;
import org.monster.main.Monster;
import org.monster.worker.WorkerManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ConstructionManager extends GameManager {

    private static ConstructionManager instance = new ConstructionManager();
    private Vector<ConstructionTask> constructionQueue = new Vector<ConstructionTask>();
    private Set<Integer> cancelBuildingIds = new HashSet<>();

    private int reservedMinerals = 0;
    private int reservedGas = 0;

    public static ConstructionManager Instance() {
        return instance;
    }

    public void addCancelBuildingId(Integer buildingId) {
        cancelBuildingIds.add(buildingId);
    }

    public void addConstructionTask(UnitType type, TilePosition desiredPosition) {
        if (type == UnitType.None || type == UnitType.Unknown) {
            return;
        }
        if (desiredPosition == TilePosition.None || desiredPosition == TilePosition.Invalid || desiredPosition == TilePosition.Unknown) {
            return;
        }

        ConstructionTask b = new ConstructionTask(type, desiredPosition);
        b.setStatus(ConstructionTask.ConstructionStatus.Unassigned.ordinal());

        // reserve resources
        reservedMinerals += type.mineralPrice();
        reservedGas += type.gasPrice();

        constructionQueue.add(b);
//		//FileUtils.appendTextToFile("log.txt", "\n addConstructionTask :: " + b.getType() + " :: reservedMinerals :: " + reservedMinerals  + " :: reservedGas :: " + reservedGas );
    }


    public void cancelConstructionTask(ConstructionTask b) {
        reservedMinerals -= b.getType().mineralPrice();
        reservedGas -= b.getType().gasPrice();

        cancelConstructionTaskDoNotReturnResources(b);
    }

    public void cancelConstructionTaskDoNotReturnResources(UnitType type, TilePosition desiredPosition) {
        ConstructionTask b = new ConstructionTask(type, desiredPosition);
        b.setFinalPosition(desiredPosition);
        if (constructionQueue.contains(b)) {

            if (b.getConstructionWorker() != null) {
                WorkerManager.Instance().setIdleWorker(b.getConstructionWorker());
            }
            if (b.getFinalPosition() != null) {

                int width = b.getType().tileWidth();
                int height = b.getType().tileHeight();

                ConstructionPlaceFinder.Instance().freeTiles(b.getFinalPosition(), width, height, b.getType());
            }
            constructionQueue.remove(b);
        }
    }

    public void cancelConstructionTaskDoNotReturnResources(ConstructionTask b) {
        if (constructionQueue.contains(b)) {

            if (b.getConstructionWorker() != null) {
                WorkerManager.Instance().setIdleWorker(b.getConstructionWorker());
            }
            if (b.getFinalPosition() != null) {

                int width = b.getType().tileWidth();
                int height = b.getType().tileHeight();
                ConstructionPlaceFinder.Instance().freeTiles(b.getFinalPosition(), width, height, b.getType());
            }
            constructionQueue.remove(b);
        }
    }

    public void removeCompletedConstructionTasks(final Vector<ConstructionTask> toRemove) {
        for (ConstructionTask b : toRemove) {
            if (constructionQueue.contains(b)) {
                constructionQueue.remove(b);
            }
        }
    }

    public void update() {

        if (TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER5, 0), LagObserver.managerRotationSize())) {
            validateWorkersAndBuildings();
            //haltConstructionBuildings();
            assignWorkersToUnassignedBuildings();
            checkForStartedConstruction();
            constructAssignedBuildings();
        }

        if (TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER5, 1), LagObserver.managerRotationSize())) {
            checkForDeadTerranBuilders();
            checkForCompletedBuildings();
            checkForDeadlockConstruction();
            checkConstructionBuildings();
        }
    }

    public void haltConstructionBuildings() {
        for (ConstructionTask b : constructionQueue) {
            // if a terran building whose worker died mid construction,
            // send the right click command to the buildingUnit to resume construction
            if (b.getStatus() == ConstructionTask.ConstructionStatus.UnderConstruction.ordinal()) {

                if (b.getBuildingUnit().isCompleted()) continue;
                Unit worker = b.getConstructionWorker();
                if (worker != null) {
                    if (worker.isUnderAttack() && worker.getHitPoints() < 20) {
                        worker.haltConstruction();
                        WorkerManager.Instance().setIdleWorker(b.getConstructionWorker());
                        b.setConstructionWorker(null);
                    }
                }
            }
        }
    }

    /// 건설 진행 도중 (공격을 받아서) 건설하려던 건물이 파괴된 경우, constructionQueue 에서 삭제합니다
    public void validateWorkersAndBuildings() {
        Vector<ConstructionTask> toRemove = new Vector<ConstructionTask>();

        for (ConstructionTask b : constructionQueue) {
            if (b.getStatus() == ConstructionTask.ConstructionStatus.UnderConstruction.ordinal()) {
                if (b.getBuildingUnit() == null || !b.getBuildingUnit().getType().isBuilding() || b.getBuildingUnit().getHitPoints() <= 0 || !b.getBuildingUnit().exists()) {
                    System.out.println("Construction Failed case . remove ConstructionTask " + b.getType());
                    toRemove.add(b);

                    if (b.getConstructionWorker() != null) {
                        WorkerManager.Instance().setIdleWorker(b.getConstructionWorker());
                    }
                }
            }
        }

        removeCompletedConstructionTasks(toRemove);
    }

    /// 건설 진행상태가 Unassigned 인 ConstructionTask 에 대해 건설 위치 및 건설 일꾼을 지정하고, 건설 진행상태를 Assigned 로 변경합니다
    public void assignWorkersToUnassignedBuildings() {
        // for each building that doesn't have a builder, assign one
        for (ConstructionTask b : constructionQueue) {
            if (b.getStatus() != ConstructionTask.ConstructionStatus.Unassigned.ordinal()) {
                continue;
            }

//			System.out.println( "assignWorkersToUnassignedBuildings start :: " + b.getType());

            // 건설 일꾼이 Unassigned 인 상태에서 getBuildLocationNear 로 건설할 위치를 다시 정합니다. . Assigned
//	        //FileUtils.appendTextToFile("log.txt", "\n assignWorkersToUnassignedBuildings relocationTilePosition :: " + b.getType() + " :: " + b.getDesiredPosition());
//	        //FileUtils.appendTextToFile(b.getType(), "log.txt", "\n assignWorkersToUnassignedBuildings relocationTilePosition :: " + b.getType() + " :: " + b.getDesiredPosition());
            TilePosition relocationTilePosition = ConstructionPlaceFinder.Instance().getBuildLocationNear(b.getType(), b.getDesiredPosition());

            //System.out.println( "ConstructionPlaceFinder Selected Location : " + testLocation.x + "," + testLocation.y );

            if (!TilePositionUtils.isValidTilePosition(relocationTilePosition)) {
                // 지금 건물 지을 장소를 전혀 찾을 수 없게 된 경우는,
                // desiredPosition 주위에 다른 건물/유닛들이 있게 되었거나, Pylon 이 파괴되었거나, Creep 이 없어진 경우이고,
                // 대부분 다른 건물/유닛들이 있게된 경우이므로 다음 frame 에서 다시 지을 곳을 탐색합니다
//	        	System.out.println(b.getType().toString() + "'s relocationTilePosition not found. lastWorkerId=" + b.getLastConstructionWorkerID() + ", desiredPosition=" + b.getDesiredPosition().toPosition());
                System.out.println(b.getType().toString() + "'s relocationTilePosition not found. lastWorkerId=" + b.getLastConstructionWorkerID() + ", desiredPosition=" + b.getDesiredPosition() + ", relocationTilePosition=" + relocationTilePosition);

//				//FileUtils.appendTextToFile("log.txt", "\n "+b.getType().toString() + "'s relocationTilePosition is not valid. lastWorkerId=" + b.getLastConstructionWorkerID() + ", desiredPosition=" + b.getDesiredPosition() + ", relocationTilePosition=" + relocationTilePosition);


//                if (b.getType().equals(UnitType.Terran_Supply_Depot) || b.getType().equals(UnitType.Terran_Barracks) || b.getType().equals(UnitType.Terran_Academy)) {
//                    relocationTilePosition = BuildManager.Instance().getDesiredPosition(b.getType(), TilePosition.None, BuildOrderItem.SeedPositionStrategy.NextSupplePoint);
//                } else if (b.getType().equals(UnitType.Terran_Command_Center)) {
//                    relocationTilePosition = BuildManager.Instance().getDesiredPosition(b.getType(), TilePosition.None, BuildOrderItem.SeedPositionStrategy.NextExpansionPoint);
//                } else {
                    relocationTilePosition = BuildManager.Instance().getDesiredPosition(b.getType(), TilePosition.None, BuildOrderItem.SeedPositionStrategy.MainBaseLocation);
//                }
                if (relocationTilePosition == TilePosition.None) {
                    System.out.println(" relocationTilePosition recalculate desiredPosition None");
//					//FileUtils.appendTextToFile("log.txt", "\n relocationTilePosition recalculate desiredPosition None");
                    continue;
                } else {
                    for (BaseLocation baseLocation : BWTA.getBaseLocations()) {
                        if (TilePositionUtils.equals(relocationTilePosition, baseLocation.getTilePosition())) {
                            relocationTilePosition = BuildManager.Instance().getDesiredPosition(b.getType(), relocationTilePosition, BuildOrderItem.SeedPositionStrategy.NoLocation);
                            break;
                        }
                    }

                    if (relocationTilePosition == TilePosition.None) continue;

                    System.out.println(" relocationTilePosition recalculate desiredPosition ok :: " + relocationTilePosition.getX() + "," + relocationTilePosition.getY());
//					//FileUtils.appendTextToFile("log.txt", "\n relocationTilePosition recalculate desiredPosition ok :: " + relocationTilePosition.getX() + ","+ relocationTilePosition.getY());
                }
            }

            //System.out.println("assignWorkersToUnassignedBuildings - chooseConstuctionWorkerClosest for " + b.getType() + " to worker near " + testLocation.getX() + "," + testLocation.getY());

            // grab a worker unit from WorkerManager which is closest to this final position
            Unit workerToAssign = WorkerManager.Instance().chooseConstuctionWorkerClosestTo(b.getType(), relocationTilePosition, true, b.getLastConstructionWorkerID());
            if (workerToAssign == null) {
                System.out.println("No worker to assing " + b.getType().toString() + ". lastWorkerId=" + b.getLastConstructionWorkerID() + ", desiredPosition=" + b.getDesiredPosition().toPosition());
                System.out.println(b.getType().toString() + relocationTilePosition + ", " + b.getLastConstructionWorkerID());
                continue;
            }
            //System.out.println("set ConstuctionWorker " + workerToAssign.getID());

            b.setConstructionWorker(workerToAssign);
            b.setFinalPosition(relocationTilePosition);
            b.setStatus(ConstructionTask.ConstructionStatus.Assigned.ordinal());

            int width = b.getType().tileWidth();
            int height = b.getType().tileHeight();

            // reserve this building's space
            ConstructionPlaceFinder.Instance().reserveTiles(relocationTilePosition, width, height, b.getType());
            b.setLastConstructionWorkerID(b.getConstructionWorker().getID());
        }
    }

    /// 건설 진행상태가 Assigned 인 ConstructionTask 에 대해,<br>
    /// 건설이 시작되기 전에 일꾼이 죽었으면 건설 진행상태를 Unassigned 로 변경하고<br>
    /// 건설 장소가 unexplored 이면 건설 일꾼을 해당 장소로 이동시키고<br>
    /// 건설 일꾼에게 build 명령을 안내렸으면 건설 일꾼에게 build 명령을 내리고<br>
    /// 건설 일꾼이 건설을 실행하지 않는 상태가 되었으면 건설 일꾼을 해제하고 건설 진행상태를 Unassigned 로 변경합니다
    public void constructAssignedBuildings() {
        for (ConstructionTask b : constructionQueue) {
            if (b.getStatus() != ConstructionTask.ConstructionStatus.Assigned.ordinal()) {
                continue;
            }

            // 일꾼에게 build 명령을 내리기 전에는 isConstructing = false 이다
            // 아직 탐색하지 않은 곳에 대해서는 build 명령을 내릴 수 없다
            // 일꾼에게 build 명령을 내리면, isConstructing = true 상태가 되어 이동을 하다가
            // build 를 실행할 수 없는 상황이라고 판단되면 isConstructing = false 상태가 된다
            // build 를 실행할 수 있으면, 프로토스 / 테란 종족의 경우 일꾼이 build 를 실행하고
            // 저그 종족 건물 중 Extractor 건물이 아닌 다른 건물의 경우 일꾼이 exists = true, isConstructing = true, isMorphing = true 가 되고, 일꾼 ID 가 건물 ID가 된다
            // 저그 종족 건물 중 Extractor 건물의 경우 일꾼이 exists = false, isConstructing = true, isMorphing = true 가 된 후, 일꾼 ID 가 건물 ID가 된다.
            //                  Extractor 건물 빌드를 도중에 취소하면, 새로운 ID 를 가진 일꾼이 된다

            // 일꾼이 Assigned 된 후, UnderConstruction 상태로 되기 전, 즉 일꾼이 이동 중에 일꾼이 죽은 경우, 건물을 Unassigned 상태로 되돌려 일꾼을 다시 Assign 하도록 합니다
            if (b.getConstructionWorker() == null || b.getConstructionWorker().exists() == false || b.getConstructionWorker().getHitPoints() <= 0) {
                // 저그 종족 건물 중 Extractor 건물의 경우 일꾼이 exists = false 이지만 isConstructing = true 가 되므로, 일꾼이 죽은 경우가 아니다
                if (b.getType() == UnitType.Zerg_Extractor && b.getConstructionWorker() != null && b.getConstructionWorker().isConstructing() == true) {
                    continue;
                }

                //System.out.println( "unassign " + b.type.getName() + " worker " + b.constructionWorker.getID() + ", because it is not exists" );

                // Unassigned 된 상태로 되돌린다
                WorkerManager.Instance().setIdleWorker(b.getConstructionWorker());

                int width = b.getType().tileWidth();
                int height = b.getType().tileHeight();

                ConstructionPlaceFinder.Instance().freeTiles(b.getFinalPosition(), width, height, b.getType());
                // free the previous location in reserved
                b.setConstructionWorker(null);
                b.setBuildCommandGiven(false);
                b.setFinalPosition(TilePosition.None);
                b.setStatus(ConstructionTask.ConstructionStatus.Unassigned.ordinal());
            }
            // if that worker is not currently constructing
            // 일꾼이 build command 를 받으면 isConstructing = true 가 되고 건설을 하기위해 이동하는데,
            // isConstructing = false 가 되었다는 것은, build command 를 수행할 수 없어 게임에서 해당 임무가 취소되었다는 것이다
            else if (b.getConstructionWorker().isConstructing() == false) {
                // if we haven't explored the build position, first we mush go there
                // 한번도 안가본 곳에는 build 커맨드 자체를 지시할 수 없으므로, 일단 그곳으로 이동하게 합니다
                if (!isBuildingPositionExplored(b)) {
                    CommandUtils.move(b.getConstructionWorker(), b.getFinalPosition().toPosition());
                } else if (b.isBuildCommandGiven() == false) {
                    //System.out.println(b.getType() + " build commanded to " + b.getConstructionWorker().getID() + ", buildCommandGiven true " );

                    // build command
                    b.getConstructionWorker().build(b.getType(), b.getFinalPosition());

                    WorkerManager.Instance().setConstructionWorker(b.getConstructionWorker(), b.getType());

                    // set the buildCommandGiven flag to true
                    b.setBuildCommandGiven(true);
                    b.setLastBuildCommandGivenFrame(TimeUtils.getFrame());
                    b.setLastConstructionWorkerID(b.getConstructionWorker().getID());
                }
                // if this is not the first time we've sent this guy to build this
                // 일꾼에게 build command 를 주었지만, 도중에 자원이 미달하게 되었거나, 해당 장소에 다른 유닛들이 있어서 건설을 시작 못하게 되거나, Pylon 이나 Creep 이 없어진 경우 등이 발생할 수 있다
                // 이 경우, 해당 일꾼의 build command 를 해제하고, 건물 상태를 Unassigned 로 바꿔서, 다시 건물 위치를 정하고, 다른 일꾼을 지정하는 식으로 처리합니다
                else {
                    if (TimeUtils.getFrame() - b.getLastBuildCommandGivenFrame() > 24) {

                        //System.out.println(b.getType() + " (" + b.getFinalPosition().getX() + "," + b.getFinalPosition().getY() + ") buildCommandGiven . but now Unassigned" );

                        // tell worker manager the unit we had is not needed now, since we might not be able
                        // to get a valid location soon enough
                        WorkerManager.Instance().setIdleWorker(b.getConstructionWorker());

                        // free the previous location in reserved
                        int width = b.getType().tileWidth();
                        int height = b.getType().tileHeight();

                        ConstructionPlaceFinder.Instance().freeTiles(b.getFinalPosition(), width, height, b.getType());

                        // nullify its current builder unit
                        b.setConstructionWorker(null);

                        // nullify its current builder unit
                        b.setFinalPosition(TilePosition.None);

                        // reset the build command given flag
                        b.setBuildCommandGiven(false);

                        // add the building back to be assigned
                        b.setStatus(ConstructionTask.ConstructionStatus.Unassigned.ordinal());
                    }
                }
            }
        }
    }

    /// 건설이 시작되면, 해당 ConstructionTask 의 건설 진행상태를 UnderConstruction 으로 변경하고<br>
    /// 저그 및 프로토스 종족의 경우 건설 일꾼을 해제합니다
    public void checkForStartedConstruction() {
        // for each building unit which is being constructed
        for (Unit buildingThatStartedConstruction : Monster.Broodwar.self().getUnits()) {
            // filter out units which aren't buildings under construction
            if (!buildingThatStartedConstruction.getType().isBuilding() || !buildingThatStartedConstruction.isBeingConstructed()) {
                continue;
            }

            // check all our building status objects to see if we have a match and if we do, update it
            for (ConstructionTask b : constructionQueue) {
                if (b.getStatus() != ConstructionTask.ConstructionStatus.Assigned.ordinal()) {
                    continue;
                }

                // check if the positions match.  Worker just started construction.
                if (b.getFinalPosition().getX() == buildingThatStartedConstruction.getTilePosition().getX() && b.getFinalPosition().getY() == buildingThatStartedConstruction.getTilePosition().getY()) {
                    //System.out.println( "Construction " + b.getType() + " started at " + b.getFinalPosition().getX() + "," + b.getFinalPosition().getY() );

                    // the resources should now be spent, so unreserve them
                    reservedMinerals -= buildingThatStartedConstruction.getType().mineralPrice();
                    reservedGas -= buildingThatStartedConstruction.getType().gasPrice();

                    // flag it as started and set the buildingUnit
                    b.setUnderConstruction(true);

                    b.setBuildingUnit(buildingThatStartedConstruction);

                    // if we are zerg, make the buildingUnit null since it's morphed or destroyed
                    // Extractor 의 경우 destroyed 되고, 그외 건물의 경우 morphed 된다
                    if (Monster.Broodwar.self().getRace() == Race.Zerg) {
                        b.setConstructionWorker(null);
                    }
                    // if we are protoss, give the worker back to worker manager
                    else if (Monster.Broodwar.self().getRace() == Race.Protoss) {
                        WorkerManager.Instance().setIdleWorker(b.getConstructionWorker());
                        b.setConstructionWorker(null);
                    }

                    // free this space
                    int width = b.getType().tileWidth();
                    int height = b.getType().tileHeight();

                    ConstructionPlaceFinder.Instance().freeTiles(b.getFinalPosition(), width, height, b.getType());

                    // put it in the under construction vector
                    b.setStatus(ConstructionTask.ConstructionStatus.UnderConstruction.ordinal());

                    // only one building will match
                    break;
                }
            }
        }
    }

    /// 테란의 경우 건설 진행상태가 UnderConstruction 이지만 건설 일꾼이 죽은 경우, 다른 건설 일꾼을 지정해서 건설이 속행되도록 합니다<br>
    /// 테란은 건설을 시작한 후, 건설 도중에 일꾼이 죽을 수 있습니다. 이 경우, 건물에 대해 다시 다른 SCV를 할당합니다<br>
    /// 참고로, 프로토스 / 저그는 건설을 시작하면 일꾼 포인터를 null 로 만들기 때문에 (constructionWorker = null) 건설 도중에 죽은 일꾼을 신경쓸 필요 없습니다
    public void checkForDeadTerranBuilders() {
        if (Monster.Broodwar.self().getRace() != Race.Terran) {
            return;
        }

        if (Monster.Broodwar.self().completedUnitCount(UnitType.Terran_SCV) <= 0) {
            return;
        }

        // for each of our buildings under construction
        for (ConstructionTask b : constructionQueue) {
            // if a terran building whose worker died mid construction,
            // send the right click command to the buildingUnit to resume construction
            if (b.getStatus() == ConstructionTask.ConstructionStatus.UnderConstruction.ordinal()) {

                if (b.getBuildingUnit().isCompleted()) continue;

                Unit workerToAssign;

                if (b.getConstructionWorker() == null || b.getConstructionWorker().exists() == false || b.getConstructionWorker().getHitPoints() <= 0) {
                    // grab a worker unit from WorkerManager which is closest to this final position
                    workerToAssign = WorkerManager.Instance().chooseConstuctionWorkerClosestTo(b.getType(), b.getFinalPosition(), true, b.getLastConstructionWorkerID());
                    if (workerToAssign != null) {
                        b.setConstructionWorker(workerToAssign);
                        b.setBuildCommandGiven(true);
                        b.setLastBuildCommandGivenFrame(TimeUtils.getFrame());
                        b.setLastConstructionWorkerID(b.getConstructionWorker().getID());
                    }
                } else {
                    workerToAssign = b.getConstructionWorker();
                }

                if (workerToAssign != null) {
                    //System.out.println("set ConstuctionWorker " + workerToAssign.getID());
                    CommandUtils.rightClick(b.getConstructionWorker(), b.getBuildingUnit());
                }
            }
        }
    }

    /// 건설이 완료된 ConstructionTask 를 삭제하고,<br>
    /// 테란 종족의 경우 건설 일꾼을 해제합니다
    public void checkForCompletedBuildings() {
        Vector<ConstructionTask> toRemove = new Vector<ConstructionTask>();

        // for each of our buildings under construction
        for (ConstructionTask b : constructionQueue) {
            if (b.getStatus() != ConstructionTask.ConstructionStatus.UnderConstruction.ordinal()) {
                continue;
            }

            // if the unit has completed
            if (b.getBuildingUnit().isCompleted()) {
                //System.out.println("Construction " + b.getType() + " completed at " + b.getFinalPosition().getX() + "," + b.getFinalPosition().getY());

                // if we are terran, give the worker back to worker manager
                if (Monster.Broodwar.self().getRace() == Race.Terran) {
                    WorkerManager.Instance().setIdleWorker(b.getConstructionWorker());
                }

                // remove this unit from the under construction vector
                toRemove.add(b);
            }
        }

        removeCompletedConstructionTasks(toRemove);
    }

    /// 건설 데드락을 체크하고, 해결합니다
    public void checkForDeadlockConstruction() {
        Vector<ConstructionTask> toCancel = new Vector<ConstructionTask>();
        for (ConstructionTask b : constructionQueue) {
            if (b.getStatus() != ConstructionTask.ConstructionStatus.UnderConstruction.ordinal()) {
                // BuildManager가 판단했을때 Construction 가능조건이 갖춰져서 ConstructionManager의 ConstructionQueue 에 들어갔는데,
                // 선행 건물이 파괴되서 Construction을 수행할 수 없게 되었거나,
                // 일꾼이 다 사망하는 등 게임상황이 바뀌어서, 계속 ConstructionQueue 에 남아있게 되는 dead lock 상황이 됩니다
                // 선행 건물을 BuildQueue에 추가해넣을지, 해당 ConstructionQueueItem 을 삭제할지 전략적으로 판단해야 합니다
                UnitType unitType = b.getType();
                UnitType producerType = b.getType().whatBuilds().first;
                final Map<UnitType, Integer> requiredUnits = unitType.requiredUnits();
                Region desiredPositionRegion = BWTA.getRegion(b.getDesiredPosition());

                boolean isDeadlockCase = false;

                // 건물을 생산하는 유닛이나, 유닛을 생산하는 건물이 존재하지 않고, 건설 예정이지도 않으면 dead lock
                if (BuildManager.Instance().isProducerWillExist(producerType) == false) {
                    isDeadlockCase = true;
                }

                // Refinery 건물의 경우, 건물 지을 장소를 찾을 수 없게 되었거나, 건물 지을 수 있을거라고 판단했는데 이미 Refinery 가 지어져있는 경우, dead lock
                if (!isDeadlockCase && unitType == InfoUtils.getRefineryBuildingType(PlayerUtils.myRace())) {
                    boolean hasAvailableGeyser = true;

                    TilePosition testLocation;
                    if (b.getFinalPosition() != TilePosition.None && b.getFinalPosition() != TilePosition.Invalid && b.getFinalPosition().isValid()) {
                        testLocation = b.getFinalPosition();
                    } else {
                        testLocation = ConstructionPlaceFinder.Instance().getBuildLocationNear(b.getType(), b.getDesiredPosition());
                    }

                    // Refinery 를 지으려는 장소를 찾을 수 없으면 dead lock
                    if (testLocation == TilePosition.None || testLocation == TilePosition.Invalid || testLocation.isValid() == false) {
                        //System.out.println("Construction Dead lock case . Cann't find place to construct " + b.getType());
                        hasAvailableGeyser = false;
                    } else {
                        // Refinery 를 지으려는 장소에 Refinery 가 이미 건설되어 있다면 dead lock
                        for (Unit u : Monster.Broodwar.getUnitsOnTile(testLocation)) {
                            if (u.getType().isRefinery() && u.exists()) {
                                hasAvailableGeyser = false;
                                break;
                            }
                        }
                        if (hasAvailableGeyser == false) {
                            //System.out.println("Construction Dead lock case . Refinery Building was built already at " + testLocation.getX() + ", " + testLocation.getY());
                        }
                    }

                    if (hasAvailableGeyser == false) {
                        isDeadlockCase = true;
                    }
                }

                // 정찰결과 혹은 전투결과, 건설 장소가 아군 점령 Region 이 아니고, 적군이 점령한 Region 이 되었으면 일반적으로는 현실적으로 dead lock 이 된다
                // (포톤캐논 러시이거나, 적군 점령 Region 근처에서 테란 건물 건설하는 경우에는 예외일테지만..)
                if (!isDeadlockCase
                        && !RegionUtils.myOccupiedRegions().contains(desiredPositionRegion)
                        && RegionUtils.enemyOccupiedRegions().contains(desiredPositionRegion)) {
                    isDeadlockCase = true;
                }

                // 선행 건물/유닛이 있는데
                if (!isDeadlockCase && requiredUnits.size() > 0) {
                    Iterator<UnitType> it = requiredUnits.keySet().iterator();
                    while (it.hasNext()) {
                        UnitType requiredUnitType = it.next();

                        if (requiredUnitType != UnitType.None) {

                            // 선행 건물 / 유닛이 존재하지 않고, 생산 중이지도 않고
                            if (Monster.Broodwar.self().completedUnitCount(requiredUnitType) == 0
                                    && Monster.Broodwar.self().incompleteUnitCount(requiredUnitType) == 0) {
                                // 선행 건물이 건설 예정이지도 않으면 dead lock
                                if (requiredUnitType.isBuilding()) {
                                    if (ConstructionManager.Instance().getConstructionQueueItemCount(requiredUnitType, null) == 0) {
                                        isDeadlockCase = true;
                                    }
                                }
                            }
                        }
                    }
                }

                if (isDeadlockCase) {
                    System.out.println("deadlock cancel at conQ :: " + b.getType() + " :: required :: " + requiredUnits.toString());
                    toCancel.add(b);
                }
            }
        }

        for (ConstructionTask i : toCancel) {
            System.out.println("cacnel type=" + i.getType() + ", desiredPosition=" + i.getDesiredPosition());
//			//FileUtils.appendTextToFile("log.txt", "\n cacnel type= "+ i.getType() + ", desiredPosition=" + i.getDesiredPosition() + " :: " + Prebot.Broodwar.getFrameCount());
//			cancelConstructionTask(i.getType(), i.getDesiredPosition());
            cancelConstructionTask(i);
        }
    }

    public void checkConstructionBuildings() {
        if (Monster.Broodwar.self().getRace() != Race.Terran) {
            return;
        }

        for (Unit unit : Monster.Broodwar.self().getUnits()) {
            // 건설중인 건물의 경우 공격 받고 있고 에너지가 100밑이면 건설 취소
            if (!unit.getType().isBuilding()) {
                continue;
            }

            boolean cancelConstruction = false;
            if (cancelBuildingIds.contains(unit.getID())) {
                cancelConstruction = true;
            } else if (unit.isConstructing() && unit.isUnderAttack() && unit.getHitPoints() < 100 || unit.getHitPoints() < unit.getType().maxHitPoints() * 0.1) {
                cancelConstruction = true;
            }

            if (cancelConstruction) {
                cancelBuildingIds.remove(unit.getID());
                unit.cancelConstruction();
                cancelConstructionTaskDoNotReturnResources(unit.getType(), unit.getTilePosition());
            }


        }
    }

    // COMPLETED
    public boolean isEvolvedBuilding(UnitType type) {
        if (type == UnitType.Zerg_Sunken_Colony ||
                type == UnitType.Zerg_Spore_Colony ||
                type == UnitType.Zerg_Lair ||
                type == UnitType.Zerg_Hive ||
                type == UnitType.Zerg_Greater_Spire) {
            return true;
        }
        return false;
    }

    public boolean isBuildingPositionExplored(final ConstructionTask b) {
        TilePosition tile = b.getFinalPosition();

        // for each tile where the building will be built
        for (int x = 0; x < b.getType().tileWidth(); ++x) {
            for (int y = 0; y < b.getType().tileHeight(); ++y) {
                if (!Monster.Broodwar.isExplored(tile.getX() + x, tile.getY() + y)) {
                    return false;
                }
            }
        }

        return true;
    }

    /// Construction 을 위해 예비해둔 Mineral 숫자를 리턴합니다
    public int getReservedMinerals() {
        return reservedMinerals;
    }

    /// Construction 을 위해 예비해둔 Gas 숫자를 리턴합니다
    public int getReservedGas() {
        return reservedGas;
    }

    /// constructionQueue 내 ConstructionTask 갯수를 리턴합니다
    public Vector<UnitType> buildingsQueued() {
        Vector<UnitType> buildingsQueued = new Vector<>();

        for (final ConstructionTask b : constructionQueue) {
            if (b.getStatus() == ConstructionTask.ConstructionStatus.Unassigned.ordinal() || b.getStatus() == ConstructionTask.ConstructionStatus.Assigned.ordinal()) {
                buildingsQueued.add(b.getType());
            }
        }

        return buildingsQueued;
    }

    /// constructionQueue 내 ConstructionTask 갯수를 리턴합니다<br>
    /// queryTilePosition 을 입력한 경우, 위치간 거리까지도 고려합니다
    public int getConstructionQueueItemCount(UnitType queryType, TilePosition queryTilePosition) {
        // queryTilePosition 을 입력한 경우, 거리의 maxRange. 타일단위
        int maxRange = 16;

        Position queryTilePositionPoint = null;
        if (queryTilePosition == null) {
            queryTilePositionPoint = Position.None;
        } else {
            queryTilePositionPoint = queryTilePosition.toPosition();
        }

        int count = 0;
        for (ConstructionTask b : constructionQueue) {
            if (b.getType() == queryType) {
                if (queryType.isBuilding() && queryTilePosition != TilePosition.None) {
                    if (queryTilePositionPoint.getDistance(b.getDesiredPosition().toPosition()) <= maxRange) {
                        count++;
                    }
                } else {
                    count++;
                }
            }
        }

        return count;
    }

    public int getConstructionQueueItemCountNear(UnitType queryType, TilePosition queryTilePosition, int range) {
        // queryTilePosition 을 입력한 경우, 거리의 maxRange. 타일단위
        int maxRange = range;

        TilePosition queryTilePositionPoint = null;
        queryTilePositionPoint = queryTilePosition.toPosition().toTilePosition();


        int count = 0;
        for (ConstructionTask b : constructionQueue) {
            if (b.getType() == queryType) {
                if (queryType.isBuilding()) {
                    if (queryTilePositionPoint.getDistance(b.getDesiredPosition().toPosition().toTilePosition()) <= maxRange) {
                        count++;
                    }
                }
            }
        }

        return count;
    }


    public Vector<ConstructionTask> getConstructionQueue() {
        return constructionQueue;
    }
}