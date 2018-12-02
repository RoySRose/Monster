package org.monster.worker;

import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.Region;
import org.monster.board.StrategyBoard;
import org.monster.build.initialProvider.BlockingEntrance.BlockingEntrance;
import org.monster.common.LagObserver;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.CommandUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.bootstrap.GameManager;
import org.monster.bootstrap.Monster;
import org.monster.micro.Minerals;
import org.monster.micro.constant.MicroConfig;

import java.util.List;

/// 일꾼 유닛들의 상태를 관리하고 컨트롤하는 class
public class WorkerManager extends GameManager {

    private static WorkerManager instance = new WorkerManager();
    /// 각 Worker 에 대한 WorkerJob 상황을 저장하는 자료구조 객체
    private WorkerData workerData = new WorkerData();
    /// 일꾼 중 한명을 Repair Worker 로 정해서, 전체 수리 대상을 하나씩 순서대로 수리합니다
    private Unit currentRepairWorker = null;
    private boolean scvIsOut = false;

    /// static singleton 객체를 리턴합니다
    public static WorkerManager Instance() {
        return instance;
    }

    /// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 업데이트하고, 일꾼 유닛들이 자원 채취 등 임무 수행을 하도록 합니다
    public void update() {
        if (TimeUtils.beforeTime(0, 30) || TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER6, 0), LagObserver.managerRotationSize())) {
            defaltMineralInfo();
            updateWorkerStatus();
        }

        if (TimeUtils.beforeTime(0, 30) || TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER6, 1), LagObserver.managerRotationSize())) {
            handleGasWorkers();
            handleIdleWorkers();
            handleMineralWorkers();
            // cc재배치는 cc를 기준으로 반복문 돈다. (max는 3으로 생각하다.)
            handleMoveWorkers();
            //handleRepairWorkers();
        }
    }

    public void updateWorkerStatus() {
        scvIsOut = false;

        // Drone 은 건설을 위해 isConstructing = true 상태로 건설장소까지 이동한 후,
        // 잠깐 getBuildType() == none 가 되었다가, isConstructing = true, isMorphing =
        // true 가 된 후, 건설을 시작한다
        // for each of our Workers
        for (Unit worker : workerData.getWorkers()) {
            if (!worker.isCompleted()) {
                continue;
            }
            // 게임상에서 worker가 isIdle 상태가 되었으면 (새로 탄생했거나, 그전 임무가 끝난 경우),
            // WorkerData 도 Idle 로 맞춘 후, handleGasWorkers, handleIdleWorkers 등에서
            // 새 임무를 지정한다
            if (worker.isIdle()) {
                // workerData 에서 Build / Move / Scout 로 임무지정한 경우, worker 는 즉 임무
                // 수행 도중 (임무 완료 전) 에 일시적으로 isIdle 상태가 될 수 있다
                if ((workerData.getWorkerJob(worker) != WorkerData.WorkerJob.Build)
                        && (workerData.getWorkerJob(worker) != WorkerData.WorkerJob.Move)
                        && (workerData.getWorkerJob(worker) != WorkerData.WorkerJob.Scout)) {

                    workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
                }
            }

            // if its job is gas
            if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Gas) {
                Unit refinery = workerData.getWorkerResource(worker);

                // if the refinery doesn't exist anymore (파괴되었을 경우)
                if (refinery == null || !refinery.exists() || refinery.getHitPoints() <= 0
                        || worker.isGatheringMinerals()) {
                    workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
                }
            }

            // if its job is repair
            if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Repair) {
                Unit repairTargetUnit = workerData.getWorkerRepairUnit(worker);


                // 대상이 파괴되었거나, 수리가 다 끝난 경우

                if (repairTargetUnit == null || !repairTargetUnit.exists()
                        || repairTargetUnit.getHitPoints() == repairTargetUnit.getType().maxHitPoints()) {
                    workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);

                } else if (repairTargetUnit.getType() == UnitType.Terran_Wraith) {
                    if (repairTargetUnit.isMoving() || worker.getOrder() != bwapi.Order.Repair) {
                        workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
                    }
                }

            }
            //TODO diasbled
//            //입막 후 밖 scv가 있는지 확
//            if (InformationManager.Instance().isBlockingEnterance()) {
//                BaseLocation expansionBase = BaseUtils.myFirstExpansion();
//                double compareDistance = BlockingEntrance.Instance().second_supple.toPosition().getDistance(expansionBase.getPosition());
//                double scvDistance = worker.getPosition().getDistance(expansionBase.getPosition());
//                if (compareDistance > scvDistance + 100 && workerData.getWorkerJob(worker) != WorkerData.WorkerJob.Combat) {
//                    scvIsOut = true;
//                }
//            }
        }
    }

    public void handleGasWorkers() {
        for (Unit scv : UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_SCV)) {
            if (scv.isGatheringGas() && !scv.isCarryingGas()) {
                if (workerData.getWorkerJob(scv) != WorkerData.WorkerJob.Gas) {
                    workerData.setWorkerJob(scv, WorkerData.WorkerJob.Idle, (Unit) null);
                }
            }
        }

        for (Unit refinery : UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Refinery)) {
            // refinery 가 건설 completed 되었으면,
            // get the number of workers currently assigned to it
            int numAssigned = workerData.getNumAssignedWorkers(refinery);

            // if it's less than we want it to be, fill 'er up
            // 단점 : 미네랄 일꾼은 적은데 가스 일꾼은 무조건 3~4명인 경우 발생.
            WorkerData.WorkerJob preJob = WorkerData.WorkerJob.Minerals;
            WorkerData.WorkerJob postJob = WorkerData.WorkerJob.Gas;
            int numInsufficient = getAdjustedWorkersPerRefinery() - numAssigned;
            if (numInsufficient < 0) { // 가스조절이 필요
                numInsufficient *= -1;
                preJob = WorkerData.WorkerJob.Gas;
                postJob = WorkerData.WorkerJob.Minerals;
            }
            for (int i = 0; i < numInsufficient; ++i) {
                Unit jobChangeWorker = chooseWorkerToChangeGatherJob(preJob, refinery); // mineral
                // to
                // gas
                if (jobChangeWorker != null) {
                    if (postJob == WorkerData.WorkerJob.Gas) {
                        workerData.setWorkerJob(jobChangeWorker, postJob, refinery);
                    } else if (postJob == WorkerData.WorkerJob.Minerals) {
                        Unit resourceDepot = getClosestResourceDepotFromWorker(jobChangeWorker);
                        workerData.setWorkerJob(jobChangeWorker, postJob, resourceDepot);
                    }

                }
            }
        }
    }

    /**
     * 기본 가스일꾼 조절 : 최소 미네랄일꾼 7기 기준
     * <p>
     * [가스통 1개] 일꾼 00-09기 -> 가스일꾼 0기 / 일꾼 10-12기 -> 가스일꾼 1기 / 일꾼 13-15기 -> 가스일꾼 2기 / 일꾼 16-00기 -> 가스일꾼 3기
     * [가스통 2개] 일꾼 00-12기 -> 가스일꾼 0기 / 일꾼 13-18기 -> 가스일꾼 1기 / 일꾼 19-24기 -> 가스일꾼 2기 / 일꾼 25-00기 -> 가스일꾼 3기
     */
    private int getAdjustedWorkersPerRefinery() {
        if (StrategyBoard.gasAdjustment) {
            // specific count adjusted by action
            return StrategyBoard.gasAdjustmentWorkerCount;

        } else {
            int workerCount = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_SCV).size();
            if (workerCount >= 30) {
                int gasunbalance = Monster.Broodwar.self().gas() - Monster.Broodwar.self().minerals();
                if (gasunbalance > 2000) {
                    return 0;
                } else if (gasunbalance > 1000) {
                    return 1;
                } else if (gasunbalance > 500) {
                    return 2;
                } else {
                    return 3;
                }
            } else if (workerCount <= 7) {
                return 0;

            } else {
                int refineryCount = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Refinery).size();
                int workersPerRefinery = (int) (workerCount - 7) / (refineryCount * 3);

                if (workersPerRefinery > MicroConfig.WORKERS_PER_REFINERY) {
                    if (Monster.Broodwar.self().minerals() <= 200) {
                        if (Monster.Broodwar.self().gas() >= 350) {
                            return 2;
                        } else if (Monster.Broodwar.self().gas() >= 500) {
                            return 1;
                        }
                    }
                    return MicroConfig.WORKERS_PER_REFINERY;
                } else {
                    return workersPerRefinery;
                }
            }
        }
    }

    /// Mineral 일꾼 유닛들 중에서 Gas 임무를 수행할 일꾼 유닛을 정해서 리턴합니다<br>
    /// Idle 일꾼은 Build, Repair, Scout 등 다른 임무에 먼저 투입되어야 하기 때문에 Mineral 일꾼 중에서만
    /// 정합니다
    private Unit chooseWorkerToChangeGatherJob(WorkerData.WorkerJob workerJob, Unit refinery) {
        if (refinery == null)
            return null;

        Unit closestWorker = null;
        double closestDistance = 1000000000;

        for (Unit unit : workerData.getWorkers()) {
            if (!UnitUtils.isCompleteValidUnit(unit) || workerData.getWorkerJob(unit) != workerJob || !unit.canMove()
                    || !unit.isMoving()) { // 가스통 안에 있는 일꾼, 또는 자원을 채취하기 직전인 일꾼을
                // 사용하지 않기 위함
                continue;
            }

            if (unit.isCarryingMinerals() || unit.isCarryingGas()) {
                continue;
            }

            double distance = unit.getDistance(refinery);
            if (closestWorker == null || distance < closestDistance) {
                closestWorker = unit;
                closestDistance = distance;
            }
        }

        return closestWorker;
    }

    /// Idle 일꾼을 Mineral 일꾼으로 만듭니다
    public void handleIdleWorkers() {
        if (TimeUtils.getFrame() > 10000) {
            int k = 0;
            // for each of our workers
            for (Unit worker : workerData.getWorkers()) {
                if (worker == null)
                    continue;
                // if worker's job is idle
                if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Idle
                        || workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Default) {
                    // send it to the nearest mineral patch
                    setMineralWorker(worker);
                    k++;
                }
                if (k > 0) {
                    break;
                }
            }
        } else {
            for (Unit worker : workerData.getWorkers()) {
                if (worker == null)
                    continue;
                // if worker's job is idle
                if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Idle
                        || workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Default) {
                    // send it to the nearest mineral patch
                    setMineralWorker(worker);
                }
            }
        }
    }

    private void handleMineralWorkers() {
        for (Unit worker : workerData.getWorkers()) {
            if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Scout) {
                continue;
            }
            if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Combat) {
                continue;
            }
            if (!worker.isCompleted()) {
                continue;
            }

            Minerals tempMineral = workerData.workerMineralAssignment.get(worker.getID());

            if (tempMineral == null)
                continue;

            if (tempMineral.mineralTrick == null && tempMineral.posTrick == bwapi.Position.None) {
                if (!worker.isCarryingMinerals() && worker.getOrderTarget() != null) {
                    if (worker.getOrderTarget().getID() != tempMineral.mineralUnit.getID()) {
                        worker.gather(tempMineral.mineralUnit);
                    }
                }
                continue;
            }

            // scv currently working at the correct mineral patch
            if (worker.isCarryingMinerals()) {
                if (worker.getOrder() != bwapi.Order.ReturnMinerals) {
                    worker.returnCargo();
                }
                continue;
            }

            //미네랄 트릭
            if (tempMineral.mineralTrick != null) {
                if (worker.getDistance(tempMineral.mineralUnit) > 40) {
                    if (worker.getTarget() != null) {
                        if (worker.getTarget().getID() == tempMineral.mineralTrick.getID()) {
                            continue;
                        }
                    }
                    worker.gather(tempMineral.mineralTrick);
                    // Prebot.Broodwar.printf("Now changing to mineral patch");
                    // Prebot.Broodwar.printf("Gathering mineral");
                } else {
                    if (worker.getTarget() != null) {
                        if (worker.getTarget().getID() == tempMineral.mineralUnit.getID()) {
                            continue;
                        }
                    }
                    worker.gather(tempMineral.mineralUnit);
                    // Prebot.Broodwar.printf("Now changing to mineral patch");
                    // Prebot.Broodwar.printf("Gathering mineral");
                }
            }
            //미네랄 포지션 트릭(move 이슈로 일단 주석)
            /*
             * else if (tempMineral.posTrick != null) { if
             * (worker.getDistance(tempMineral.mineralUnit) > 40) { if
             * (worker.getTargetPosition() != null) { if
             * (worker.getTargetPosition() == tempMineral.posTrick) {
             * continue; } } worker.move(tempMineral.posTrick); //
             * Prebot.Broodwar.printf("Now changing to mineral patch"); //
             * Prebot.Broodwar.printf("Gathering mineral"); } else { if
             * (worker.getTarget() != null) { if (worker.getTarget().getID()
             * == tempMineral.mineralUnit.getID()) { continue; } }
             * worker.gather(tempMineral.mineralUnit); //
             * Prebot.Broodwar.printf("Now changing to mineral patch"); //
             * Prebot.Broodwar.printf("Gathering mineral"); } }
             */
        }
    }

    public void handleMoveWorkers() {
        // for each of our workers
        for (Unit worker : workerData.getWorkers()) {
            if (worker == null)
                continue;
            // if it is a move worker
            if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Move) {
                WorkerMoveData data = workerData.getWorkerMoveData(worker);

                // 목적지에 도착한 경우 이동 명령을 해제한다
                if (worker.getPosition().getDistance(data.getPosition()) < 4) {
                    setIdleWorker(worker);
                } else {
                    CommandUtils.move(worker, data.getPosition());
                }
            }
        }
    }

    public void handleRepairWorkers() {
        if (Monster.Broodwar.self().getRace() != Race.Terran) {
            return;
        }

        // 미네랄이 없어도 계속 고치려 하므로 mineral < 5 이하일땐 리턴.
        if (Monster.Broodwar.self().minerals() < 5) {
            return;
        }

        int repairWorkCnt = 0;
        int repairWraithWorkCnt = 0;

        int repairmax = 3;
        if (StrategyBoard.mainSquadMode.isAttackMode) {
            repairmax = 6;
        }
		/*if (repairWorkCnt > repairmax) {
			return;
		}*/

        for (Unit unit : Monster.Broodwar.self().getUnits()) {
            if (!unit.isCompleted()) {
                continue;
            }
            //일정 거리 이상은 수리하러 안감 - (base - 센터 거리 기준)
            repairWorkCnt = workerData.workerRepairMap.size();
            repairWraithWorkCnt = workerData.workerWraithRepairMap.size();

            // 건물의 경우 아무리 멀어도 무조건 수리. 일꾼 한명이 순서대로 수리
            // 나르는 건물 수리 안함.
            if (unit.getType().isBuilding() && unit.getHitPoints() < unit.getType().maxHitPoints()
                    && repairWorkCnt < repairmax) {
                if (unit.isFlying()) {
                    continue;
                }

                //건물은 본진과 앞마당만 수리
                if (!isReapirBuilding(unit)) {
                    continue;
                }
                Unit repairWorker = chooseRepairWorkerClosestTo(unit, 0);
                if (repairWorker != null) {
                    if (TimeUtils.beforeTime(10, 0)) {
                        if (unit.getType() == UnitType.Terran_Bunker
                                || unit.getType() == UnitType.Terran_Missile_Turret
                                || unit.getType() == UnitType.Terran_Comsat_Station) {
                            setRepairWorker(repairWorker, unit);
                        } else if ((unit.getType() == UnitType.Terran_Barracks
                                || unit.getType() == UnitType.Terran_Supply_Depot
                                || unit.getType() == UnitType.Terran_Command_Center)
                                && unit.getHitPoints() < unit.getType().maxHitPoints() * 0.9) {
                            setRepairWorker(repairWorker, unit);
                        }
                    } else if (unit.getType() != UnitType.Terran_Supply_Depot
                            && unit.getType() != UnitType.Terran_Armory
                            && unit.getType() != UnitType.Terran_Academy) {
                        setRepairWorker(repairWorker, unit);
                    }
                }
            }// 메카닉 유닛 (SCV, 시즈탱크, 레이쓰 등)의 경우 근처에 SCV가 있는 경우 수리. 일꾼 한명이 순서대로 수리
            else if (unit.getType() == UnitType.Terran_Wraith && repairWraithWorkCnt < repairmax
                    && unit.getHitPoints() < unit.getType().maxHitPoints()) {

                Unit repairWorker = chooseRepairWorkerClosestTo(unit, 0);
                if (repairWorker != null
                        && !repairWorker.isRepairing()
                        && unit.getDistance(repairWorker) < 200) {
                    setRepairWorker(repairWorker, unit);
                }
            } else if ((unit.getType() == UnitType.Terran_Goliath
                    || unit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode
                    || unit.getType() == UnitType.Terran_Siege_Tank_Tank_Mode
                    || unit.getType() == UnitType.Terran_Science_Vessel
                    || unit.getType() == UnitType.Terran_Valkyrie) && repairWorkCnt < repairmax
                    && unit.getHitPoints() < unit.getType().maxHitPoints()) {

                if (isReapirZone(unit)) {
                    continue;
                }
                Unit repairWorker = chooseRepairWorkerClosestTo(unit, 0);
                // SCV 는 수리 대상에서 제외. 전투 유닛만 수리하도록 한다
                if (repairWorker != null) {
                    setRepairWorker(repairWorker, unit);
                }
            }
        }
    }

    /// position 에서 가장 가까운 Mineral 혹은 Idle 혹은 Move 일꾼 유닛들 중에서 Repair 임무를 수행할 일꾼
    /// 유닛을 정해서 리턴합니다
    public Unit chooseRepairWorkerClosestTo(Unit unit, int maxRange) {
        Unit closestWorker = null;
        double closestDist = 100000000;

        // if (currentRepairWorker != null && currentRepairWorker.exists() &&
        // currentRepairWorker.getHitPoints() > 0 &&
        // unit.getType().isBuilding())
		/*if (Prebot.Broodwar.getFrameCount() <= 12000) {
			if (currentRepairWorker != null && currentRepairWorker.exists() && currentRepairWorker.getHitPoints() > 0
					&& unit.getType().isBuilding()
					&& (unit.getType() == UnitType.Terran_Barracks || unit.getType() == UnitType.Terran_Supply_Depot
							|| unit.getType() == UnitType.Terran_Bunker) == false) {
				return currentRepairWorker;
			}
		} else if (currentRepairWorker != null && currentRepairWorker.exists() && currentRepairWorker.getHitPoints() > 0
				&& unit.getType().isBuilding() && unit.getType() != UnitType.Terran_Bunker) {
			return currentRepairWorker;
		}*/

        // for each of our workers
        for (Unit worker : workerData.getWorkers()) {
            if (worker == null || !worker.isCompleted()) {
                continue;
            }
            if (worker.isCarryingMinerals() || worker.isGatheringGas()) {
                continue;
            }

            WorkerData.WorkerJob workerJob = workerData.getWorkerJob(worker);
            if (workerJob == WorkerData.WorkerJob.Minerals || workerJob == WorkerData.WorkerJob.Idle || workerJob == WorkerData.WorkerJob.Move) {
                double dist = worker.getDistance(unit);

                if (closestWorker == null || (dist < closestDist)) {
                    closestWorker = worker;
                    closestDist = dist;
                }
            }
        }
		/*if (currentRepairWorker == null || currentRepairWorker.exists() == false
				|| currentRepairWorker.getHitPoints() <= 0) {
			currentRepairWorker = closestWorker;
		}*/

        return closestWorker;
    }

    /// 해당 일꾼 유닛 unit 의 WorkerJob 값를 Mineral 로 변경합니다
    public void setMineralWorker(Unit unit) {
        if (unit == null)
            return;

        // check if there is a mineral available to send the worker to
        /// 해당 일꾼 유닛 unit 으로부터 가장 가까운 ResourceDepot 건물을 리턴합니다
        Unit depot = getClosestResourceDepotFromWorker(unit);
        // if there is a valid ResourceDepot (Command CENTER_POS, Nexus, Hatchery)
        if (depot != null) {
            // update workerData with the new job
            workerData.setWorkerJob(unit, WorkerData.WorkerJob.Minerals, depot);
        }
    }

    /*
     * se-min.park 멀티 일꾼 재분배 위해 추가.
     */
    /// 해당 일꾼 유닛 unit 의 WorkerJob 값를 Mineral 로 변경합니다
    public void setMineralWorker(Unit unit, Unit depot) {
        if (unit == null)
            return;

        // check if there is a mineral available to send the worker to
        /// 해당 일꾼 유닛 unit 으로부터 가장 가까운 ResourceDepot 건물을 리턴합니다
        // if there is a valid ResourceDepot (Command CENTER_POS, Nexus, Hatchery)
        if (depot != null && depot.isCompleted()) {
            // update workerData with the new job
            workerData.setWorkerJob(unit, WorkerData.WorkerJob.Minerals, depot);
        }
    }

    /// target 으로부터 가장 가까운 Mineral 일꾼 유닛을 리턴합니다
    public Unit getClosestMineralWorkerTo(Position target) {
        Unit closestUnit = null;
        double closestDist = 100000000;

        for (Unit unit : Monster.Broodwar.self().getUnits()) {
            if (unit.isCompleted() && unit.getHitPoints() > 0 && unit.exists() && unit.getType().isWorker()
                    && WorkerManager.Instance().isMineralWorker(unit)) {
                double dist = unit.getDistance(target);
                if (closestUnit == null || dist < closestDist) {
                    closestUnit = unit;
                    closestDist = dist;
                }
            }
        }

        return closestUnit;
    }

    /// 해당 일꾼 유닛 unit 으로부터 가장 가까운 ResourceDepot 건물을 리턴합니다

    public Unit getClosestResourceDepotFromWorker(Unit worker) {
        // BasicBot 1.1 Patch Start
        // ////////////////////////////////////////////////
        // 멀티 기지간 일꾼 숫자 리밸런싱이 잘 일어나도록 버그 수정
        if (worker == null)
            return null;

        Unit closestDepot = null;
        double closestDistance = 1000000000;

        // 완성된, 공중에 떠있지 않고 땅에 정착해있는, ResourceDepot 혹은 Lair 나 Hive로 변형중인 Hatchery
        // 중에서
        // 첫째로 미네랄 일꾼수가 꽉 차지않은 곳
        // 둘째로 가까운 곳을 찾는다
        for (Unit unit : UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Command_Center)) {
            if (unit == null)
                continue;

            if (unit.getType().isResourceDepot() && unit.isCompleted() && unit.isLifted() == false) {
                if (workerData.depotHasEnoughMineralWorkers(unit) == false) {
                    if (isCheckEnemy(unit) == true) {
                        continue;
                    }
                    double distance = unit.getDistance(worker);
                    if (closestDistance > distance) {
                        closestDepot = unit;
                        closestDistance = distance;
                    }
                }
            }
        }
        // 모든 ResourceDepot 이 다 일꾼수가 꽉 차있거나, 완성된 ResourceDepot 이 하나도 없고 건설중이라면,
        // ResourceDepot 주위에 미네랄이 남아있는 곳 중에서 가까운 곳이 선택되도록 한다
        if (closestDepot == null) {
            for (Unit unit : UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Command_Center)) {
                if (unit == null)
                    continue;

                if (isCheckEnemy(unit) == true) {
                    continue;
                }

                if (unit.getType().isResourceDepot()) {
                    if (workerData.getMineralsNearDepot(unit) > 0) {
                        double distance = unit.getDistance(worker);
                        if (closestDistance > distance) {
                            closestDepot = unit;
                            closestDistance = distance;
                        }
                    }
                }
            }
        }
        // 모든 ResourceDepot 주위에 미네랄이 하나도 없다면, 일꾼에게 가장 가까운 곳을 선택한다
        if (closestDepot == null) {
            for (Unit unit : UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Command_Center)) {
                if (unit == null)
                    continue;
                if (unit.getType().isResourceDepot()) {
                    double distance = unit.getDistance(worker);
                    if (closestDistance > distance) {
                        closestDepot = unit;
                        closestDistance = distance;
                    }
                }
            }
        }

        return closestDepot;
        // BasicBot 1.1 Patch End
        // //////////////////////////////////////////////////

    }

    // /// 해당 지역에 공격유닛이 잇는지 판단
    // public boolean isCheckEnemy(Unit depot)
    // {
    // int unitCnt = 0;
    // BaseLocation myBaseLocation =
    // BaseUtils.myMainBase();
    // //본진일때는 무조건 false
    // if (myBaseLocation == null ||
    // depot.getDistance(myBaseLocation.getPosition()) < 5 * Config.TILE_SIZE)
    // return false;
    // for (Unit unit : Monster.Broodwar.enemy().getUnits())
    // {
    // if(unit.isVisible() && unit.getDistance(depot) < 300 &&
    // unit.getType().canAttack()){
    // unitCnt++;
    //// commandUtil.move(currentScoutUnit, firstBuilding.getPosition());
    //// if(unitCnt > 8){
    // if(unitCnt > 3){
    // return true;
    // }
    // }
    // }
    // return false;
    // }

    public boolean isCheckEnemy(Unit depot) {
        if (depot.getTilePosition().getX() == BlockingEntrance.Instance().starting.getX()
                && depot.getTilePosition().getY() == BlockingEntrance.Instance().starting.getY()) {
            return false;
        }

        for (Unit enemy : UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.ENEMY, depot.getPosition(), 200)) {
            if (enemy.getType().canAttack()) {
                return true;
            }
        }
        return false;
    }

    /// 해당 일꾼 유닛 unit 의 WorkerJob 값를 Idle 로 변경합니다
    public void setIdleWorker(Unit unit) {
        if (unit == null)
            return;

        workerData.setWorkerJob(unit, WorkerData.WorkerJob.Idle, (Unit) null);
    }

    /// Mineral 일꾼 유닛들 중에서 Gas 임무를 수행할 일꾼 유닛을 정해서 리턴합니다<br>
    /// Idle 일꾼은 Build, Repair, Scout 등 다른 임무에 먼저 투입되어야 하기 때문에 Mineral 일꾼 중에서만
    /// 정합니다
    public Unit chooseGasWorkerFromMineralWorkers(Unit refinery) {
        if (refinery == null)
            return null;

        Unit closestWorker = null;
        double closestDistance = 100000000;

        for (Unit unit : workerData.getWorkers()) {
            if (unit == null)
                continue;

            if (unit.isCompleted() && workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Minerals
                    && !unit.isCarryingMinerals()) {
                double distance = unit.getDistance(refinery);
                if (closestWorker == null || (distance < closestDistance && unit.isCarryingMinerals() == false)) {
                    if (unit.isCarryingGas() == true) {
                        distance = 0;
                    }
                    closestWorker = unit;
                    closestDistance = distance;
                }
            }
        }

        return closestWorker;
    }

    public void setConstructionWorker(Unit worker, UnitType buildingType) {
        if (worker == null)
            return;

        workerData.setWorkerJob(worker, WorkerData.WorkerJob.Build, buildingType);
    }

    /// buildingPosition 에서 가장 가까운 Move 혹은 Idle 혹은 Mineral 일꾼 유닛들 중에서
    /// Construction 임무를 수행할 일꾼 유닛을 정해서 리턴합니다<br>
    /// Move / Idle Worker 중에서 먼저 선정하고, 없으면 Mineral Worker 중에서 선정합니다<br>
    /// 일꾼 유닛이 2개 이상이면, avoidWorkerID 에 해당하는 worker 는 선정하지 않도록 합니다<br>
    /// if setJobAsConstructionWorker is true (default), it will be flagged as a
    /// builder unit<br>
    /// if setJobAsConstructionWorker is false, we just want to see which worker
    /// will build a building
    public Unit chooseConstuctionWorkerClosestTo(UnitType buildingType, TilePosition buildingPosition,
                                                 boolean setJobAsConstructionWorker, int avoidWorkerID) {
        // variables to hold the closest worker of each type to the building
        Unit closestMovingWorker = null;
        Unit closestMiningWorker = null;
        double closestMovingWorkerDistance = 100000000;
        double closestMiningWorkerDistance = 100000000;

        // look through each worker that had moved there first
        for (Unit unit : workerData.getWorkers()) {
            if (unit == null)
                continue;

            // worker 가 2개 이상이면, avoidWorkerID 는 피한다
            if (workerData.getWorkers().size() >= 2 && avoidWorkerID != 0 && unit.getID() == avoidWorkerID)
                continue;

            // Move / Idle Worker
            if (unit.isCompleted() && (workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Move
                    || workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Idle)) {

                // if it is a new closest distance, set the pointer
                double distance = unit.getDistance(buildingPosition.toPosition());
                // 1.3 worker 에너지 20이이하면 다시 추출안한다.
                if (unit.getHitPoints() < 20) {
                    distance += 200;
                }

                if (closestMovingWorker == null || (distance < closestMovingWorkerDistance
                        && unit.isCarryingMinerals() == false && unit.isGatheringGas() == false)) {
                    if (BWTA.isConnected(unit.getTilePosition(), buildingPosition)) {
                        closestMovingWorker = unit;
                        closestMovingWorkerDistance = distance;
                    }
                }
            }

            // Move / Idle Worker 가 없을때, 다른 Worker 중에서 차출한다
            /*
             * se-min.park 가스가 미네랄보다 건설예정 위치에 가까울경우 gas 들고 있는 일꾼이 추출됨(로직에서
             * isGatheringGas false 처리 되어잇음에도 감...그래서 Gas 일꾼에서 안빼는걸로 변경)
             */
            if (unit.isCompleted() && (workerData.getWorkerJob(unit) != WorkerData.WorkerJob.Move
                    && workerData.getWorkerJob(unit) != WorkerData.WorkerJob.Idle
                    && workerData.getWorkerJob(unit) != WorkerData.WorkerJob.Gas
                    && workerData.getWorkerJob(unit) != WorkerData.WorkerJob.Build
                    && workerData.getWorkerJob(unit) != WorkerData.WorkerJob.Scout
                    && workerData.getWorkerJob(unit) != WorkerData.WorkerJob.Combat)) {
                // if it is a new closest distance, set the pointer
                double distance = unit.getDistance(buildingPosition.toPosition());
                if (closestMiningWorker == null || (distance < closestMiningWorkerDistance
                        && unit.isCarryingMinerals() == false
                        && unit.isGatheringGas() == false)) {
                    if (BWTA.isConnected(unit.getTilePosition(), buildingPosition)) {
                        closestMiningWorker = unit;
                        closestMiningWorkerDistance = distance;
                    }
                }
            }
        }
        // System.out.println("closestMovingWorker ; " +
        // closestMovingWorker.getID() + " closestMiningWorker L : " +
        // closestMiningWorker.getID());
        Unit chosenWorker = closestMovingWorker != null ? closestMovingWorker : closestMiningWorker;

        // if the worker exists (one may not have been found in rare cases)
        // 미네랄 or 가스 안옮기는 애로 설정
        if (chosenWorker != null && setJobAsConstructionWorker) {
            workerData.setWorkerJob(chosenWorker, WorkerData.WorkerJob.Build, buildingType);
        }

        return chosenWorker;
    }

    /// Mineral 혹은 Idle 일꾼 유닛들 중에서 Scout 임무를 수행할 일꾼 유닛을 정해서 리턴합니다
    public Unit getScoutWorker() {
        // for each of our workers
        for (Unit worker : workerData.getWorkers()) {
            if (worker == null) {
                continue;
            }
            if (worker.isCarryingMinerals()) {
                continue;
            }
            // if it is a scout worker
            if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Combat) {
                return worker;
            }
        }

        return null;
    }

    // sets a worker as a scout
    public void setScoutWorker(Unit worker) {
        if (worker == null)
            return;

        workerData.setWorkerJob(worker, WorkerData.WorkerJob.Scout, (Unit) null);
    }

    // get a worker which will move to a current location
    /// position 에서 가장 가까운 Mineral 혹은 Idle 일꾼 유닛들 중에서 Move 임무를 수행할 일꾼 유닛을 정해서
    // 리턴합니다
    public Unit chooseMoveWorkerClosestTo(Position p) {
        // set up the pointer
        Unit closestWorker = null;
        double closestDistance = 100000000;

        // for each worker we currently have
        for (Unit unit : workerData.getWorkers()) {
            if (unit == null)
                continue;

            // only consider it if it's a mineral worker
            if (unit.isCompleted() && workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Minerals) {
                // if it is a new closest distance, set the pointer
                double distance = unit.getDistance(p);
                if (closestWorker == null || (distance < closestDistance && unit.isCarryingMinerals() == false
                        && unit.isCarryingGas() == false)) {
                    if (unit.isCarryingMinerals()) {
                        continue;
                    }
                    closestWorker = unit;
                    closestDistance = distance;
                }
            }
        }

        // return the worker
        return closestWorker;
    }

    /// position 에서 가장 가까운 Mineral 혹은 Idle 일꾼 유닛들 중에서 Move 임무를 수행할 일꾼 유닛을 정해서
    /// 리턴합니다??????
    public void setMoveWorker(Unit worker, int mineralsNeeded, int gasNeeded, Position p) {
        // set up the pointer
        Unit closestWorker = null;
        double closestDistance = 100000000;

        // for each worker we currently have
        for (Unit unit : workerData.getWorkers()) {
            if (unit == null)
                continue;

            // only consider it if it's a mineral worker or idle worker
            if (unit.isCompleted() && (workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Minerals
                    || workerData.getWorkerJob(unit) == WorkerData.WorkerJob.Idle) && !unit.isCarryingMinerals()) {
                // if it is a new closest distance, set the pointer
                double distance = unit.getDistance(p);
                if (closestWorker == null || distance < closestDistance) {
                    closestWorker = unit;
                    closestDistance = distance;
                }
            }
        }

        if (closestWorker != null) {
            workerData.setWorkerJob(closestWorker, WorkerData.WorkerJob.Move, new WorkerMoveData(mineralsNeeded, gasNeeded, p));
        } else {
            // Monster.Broodwar.printf("Error, no worker found");
        }
    }

    /// 해당 일꾼 유닛으로부터 가장 가까운 적군 유닛을 리턴합니다
    public Unit getClosestEnemyUnitFromWorker(Unit worker) {
        if (worker == null)
            return null;

        Unit closestUnit = null;
        double closestDist = 10000;

        for (Unit unit : Monster.Broodwar.enemy().getUnits()) {
            double dist = unit.getDistance(worker);

            // if ((dist < 400) && (closestUnit == null || (dist <
            // closestDist)))
            if ((dist < 1000) && (closestUnit == null || (dist < closestDist))) {
                closestUnit = unit;
                closestDist = dist;
            }
        }

        return closestUnit;
    }

    /// 해당 일꾼 유닛에게 Combat 임무를 부여합니다
    public void setCombatWorker(Unit worker) {
        if (worker == null)
            return;

        workerData.setWorkerJob(worker, WorkerData.WorkerJob.Combat, (Unit) null);
    }

    /// 모든 Combat 일꾼 유닛에 대해 임무를 해제합니다
    public void stopCombat() {
        for (Unit worker : workerData.getWorkers()) {
            if (worker == null)
                continue;

            if (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Combat) {
                setMineralWorker(worker);
            }
        }
    }

    public void setRepairWorker(Unit worker, Unit unitToRepair) {
        workerData.setWorkerJob(worker, WorkerData.WorkerJob.Repair, unitToRepair);
    }

    public void stopRepairing(Unit worker) {
        workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
    }

    /// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 업데이트합니다
    public void onUnitMorph(Unit unit) {
        if (unit == null)
            return;

        if (unit.getType().isBuilding() && unit.getPlayer() == Monster.Broodwar.self()
                && unit.getPlayer().getRace() == Race.Zerg) {
            // 해당 worker 를 workerData 에서 삭제한다
            workerData.workerDestroyed(unit);
            rebalanceWorkers();
        }
    }

    /// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 업데이트합니다
    public void onUnitShow(Unit unit) {
        if (unit == null)
            return;

        // add the depot if it exists
        if (unit.getType().isResourceDepot() && unit.getPlayer() == Monster.Broodwar.self()) {
            workerData.addDepot(unit);
        }

        // add the worker
        if (unit.getType().isWorker() && unit.getPlayer() == Monster.Broodwar.self() && unit.getHitPoints() >= 0) {
            workerData.addWorker(unit);
        }

        if (unit.getType().isResourceDepot() && unit.getPlayer() == Monster.Broodwar.self()) {
            rebalanceWorkers();
        }
    }

    // onUnitComplete 메소드 추가
    /// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 업데이트합니다
    /// Terran_SCV, Protoss_Probe 유닛 훈련이 끝나서 탄생할 경우,
    /// Zerg_Drone 유닛이 탄생하는 경우,
    /// Zerg_Drone 유닛이 건물로 Morph 가 끝나서 건물이 완성되는 경우,
    /// Zerg_Drone 유닛의 Zerg_Extractor 건물로의 Morph 를 취소시켜서 Zerg_Drone 유닛이 새롭게 탄생하는
    // 경우
    /// 호출됩니다
    public void onUnitComplete(Unit unit) {
        if (unit == null)
            return;
        // ResourceDepot 건물이 신규 생성되면, 자료구조 추가 처리를 한 후, rebalanceWorkers 를 한다
        if (unit.getType().isResourceDepot() && unit.getPlayer() == Monster.Broodwar.self()) {
            workerData.addDepot(unit);
            // mineralPath(unit); //cc 완성되면 미네랄path 적용위해 호출 (본진에서 cc짓고 멀티 이동시 검토
            // 필요)
            rebalanceWorkers();
        }
        // 일꾼이 신규 생성되면, 자료구조 추가 처리를 한다.
        if (unit.getType().isWorker() && unit.getPlayer() == Monster.Broodwar.self() && unit.getHitPoints() >= 0) {
            workerData.addWorker(unit);
            rebalanceWorkers();
        }
    }

    // 일하고있는 resource depot 에 충분한 수의 mineral worker 들이 지정되어 있다면, idle 상태로 만든다
    // idle worker 에게 mineral job 을 부여할 때, mineral worker 가 부족한 resource depot
    // 으로 이동하게 된다
    public void rebalanceWorkers() {
        for (Unit worker : workerData.getWorkers()) {
            if (workerData.getWorkerJob(worker) != WorkerData.WorkerJob.Minerals) {
                continue;
            }

            Unit depot = workerData.getWorkerDepot(worker);

            if (depot != null && workerData.depotHasEnoughMineralWorkers(depot)) {
                workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
            } else if (depot == null) {
                workerData.setWorkerJob(worker, WorkerData.WorkerJob.Idle, (Unit) null);
            }
        }
    }

    /// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 업데이트합니다

    public void onUnitDestroy(Unit unit) {
        if (unit == null)
            return;
        // ResourceDepot 건물이 파괴되면, 자료구조 삭제 처리를 한 후, 일꾼들을 Idle 상태로 만들어
        // rebalanceWorkers 한 효과가 나게 한다
        if (unit.getType().isResourceDepot() && unit.getPlayer() == Monster.Broodwar.self()) {
            workerData.removeDepot(unit);
        }
        // 일꾼이 죽으면, 자료구조 삭제 처리를 한 후, rebalanceWorkers 를 한다
        if (unit.getType().isWorker() && unit.getPlayer() == Monster.Broodwar.self()) {
            workerData.workerDestroyed(unit);
            rebalanceWorkers();
        }
        // 미네랄을 다 채취하면 rebalanceWorkers를 한다
        if (unit.getType() == UnitType.Resource_Mineral_Field) {
            rebalanceWorkers();
        }
    }

    public boolean isMineralWorker(Unit worker) {
        if (worker == null)
            return false;

        return workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Minerals
                || workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Idle;
    }

    public boolean isCombatWorker(Unit worker) {
        if (worker == null)
            return false;

        return workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Combat || workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Idle;
    }

    public boolean isScoutWorker(Unit worker) {
        if (worker == null)
            return false;

        return (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Scout);
    }

    public boolean isConstructionWorker(Unit worker) {
        if (worker == null)
            return false;

        return (workerData.getWorkerJob(worker) == WorkerData.WorkerJob.Build);
    }

    public int getNumMineralWorkers() {
        return workerData.getNumMineralWorkers();
    }

    // 농봉상태 일꾼 유닛 숫자 리턴
    public int getNumNongBongMWorkers() {
        return workerData.getNumNongBongMWorkers();
    }

    public int getNumNongBongGWorkers() {
        return workerData.getNumNongBongGWorkers();
    }

    public int getNumNongBongGSWorkers() {
        return workerData.getNumNongBongGSWorkers();
    }

    /// idle 상태인 일꾼 유닛 unit 의 숫자를 리턴합니다
    public int getNumIdleWorkers() {
        return workerData.getNumIdleWorkers();
    }

    public int getNumGasWorkers() {
        return workerData.getNumGasWorkers();
    }

    /// 일꾼 유닛들의 상태를 저장하는 workerData 객체를 리턴합니다
    public WorkerData getWorkerData() {
        return workerData;
    }

    /*
     * 초기 미네랄 패스 위해 정보 (초기1회)
     */
    public void defaltMineralInfo() {
        for (Unit depot : UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Command_Center)) {
            mineralPath(depot);
        }
    }

    boolean isLeft(Position a, Position b, Position c) {
        return ((b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX())) >= 0;
    }

    public void mineralPath(Unit depot) {
        List<Minerals> minerals = workerData.getMineralPatchesNearDepot(depot);
        //CCtrick = BWAPI::Position( Comm  )

        Position ccPoint = depot.getPosition();
        Position topLeft = new Position(ccPoint.getX() - 32 * 10, ccPoint.getY() - 32 * 10);
        Position bottomRight = new Position(ccPoint.getX() + 32 * 10, ccPoint.getY() + 32 * 10);
        Position bottomLeft = new Position(ccPoint.getX() - 32 * 10, ccPoint.getY() + 32 * 10);
        Position topRight = new Position(ccPoint.getX() + 32 * 10, ccPoint.getY() - 32 * 10);

        for (Minerals minr : minerals) {
            boolean isUpLeft = false;
            boolean isBotLeft = false;
            //System.out.println("ccPoint : " + ccPoint.getPoint() + "minr.mineral.getPosition() : " +minr.mineral.getPosition());
            if (isLeft(topLeft, bottomRight, minr.mineralUnit.getPosition())) {
                isBotLeft = true;
            }
            if (isLeft(topRight, bottomLeft, minr.mineralUnit.getPosition())) {
                isUpLeft = true;
            }

            if (isBotLeft == true && isUpLeft == false) {
                minr.facing = "D";
            } else if (isBotLeft == true && isUpLeft == true) {
                minr.facing = "L";
            } else if (isBotLeft == false && isUpLeft == true) {
                minr.facing = "U";
            } else if (isBotLeft == false && isUpLeft == false) {
                minr.facing = "R";
            }
        }


        for (Minerals minr : minerals) {
            if ("D".equals(minr.facing)) { //top of mineral
                for (Minerals minrTemp : minerals) {
                    if (minr.unitId != minrTemp.unitId && minrTemp.mineralUnit.getDistance(minr.mineralUnit) <= 1 * 20
                            && minrTemp.mineralTrick == null
                            && "D".equals(minrTemp.facing)
                            && minrTemp.mineralUnit.getPosition().getY() < minr.mineralUnit.getPosition().getY()) {
                        minrTemp.mineralTrick = minr.mineralUnit;
                    }
                }
            } else if ("L".equals(minr.facing)) { //left  of mineral
                for (Minerals minrTemp : minerals) {
                    if (minr.unitId != minrTemp.unitId && minrTemp.mineralUnit.getDistance(minr.mineralUnit) <= 1 * 20
                            && minrTemp.mineralTrick == null
                            && "L".equals(minrTemp.facing)
                            && minrTemp.mineralUnit.getPosition().getX() > minr.mineralUnit.getPosition().getX()) {
                        minrTemp.mineralTrick = minr.mineralUnit;
                    }
                }
            } else if ("R".equals(minr.facing)) { //right  of mineral
                for (Minerals minrTemp : minerals) {
                    if (minr.unitId != minrTemp.unitId && minrTemp.mineralUnit.getDistance(minr.mineralUnit) <= 1 * 20
                            && minrTemp.mineralTrick == null
                            && "R".equals(minrTemp.facing)
                            && minrTemp.mineralUnit.getPosition().getX() < minr.mineralUnit.getPosition().getX()) {
                        minrTemp.mineralTrick = minr.mineralUnit;
                    }
                }
            } else if ("U".equals(minr.facing)) { //top of mineral
                for (Minerals minrTemp : minerals) {
                    if (minr.unitId != minrTemp.unitId && minrTemp.mineralUnit.getDistance(minr.mineralUnit) <= 1 * 20
                            && minrTemp.mineralTrick == null
                            && "U".equals(minrTemp.facing)
                            && minrTemp.mineralUnit.getPosition().getY() > minr.mineralUnit.getPosition().getY()) {
                        minrTemp.mineralTrick = minr.mineralUnit;
                    }
                }
            }
        }
        workerData.depotMineral.put(depot, minerals);
    }

    public boolean scvIsOutOfBase() {
        return scvIsOut;
    }

    public boolean isReapirZone(Unit unit) {
        Position myBase = BaseUtils.myMainBase().getPosition();
        double maxReparitDistance = myBase.getDistance(CommonCode.CENTER_POS);
        if (myBase.getDistance(unit) > maxReparitDistance) {
            return true;
        } else {
            return false;
        }
    }

    //public boolean isInsidePositionToBase(Position position) {
    public boolean isReapirBuilding(Unit building) {
        Region unitRegion = BWTA.getRegion(building.getPosition());
        Region baseRegion = BWTA.getRegion(BaseUtils.myMainBase().getPosition());

        Position expansionPosition = BaseUtils.myFirstExpansion().getPosition();
        Region expansionRegion = BWTA.getRegion(expansionPosition);

        if (unitRegion == baseRegion || unitRegion == expansionRegion) {
            return true;
        } else {
            return false;
        }
    }

}