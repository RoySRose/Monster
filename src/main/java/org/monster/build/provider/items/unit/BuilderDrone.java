package org.monster.build.provider.items.unit;

import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;
import org.monster.board.StrategyBoard;
import org.monster.build.base.BuildManager;
import org.monster.build.base.BuildOrderItem;
import org.monster.build.base.BuildOrderQueue;
import org.monster.build.initialProvider.BlockingEntrance.BlockingEntrance;
import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;
import org.monster.common.constant.UnitFindStatus;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.worker.WorkerManager;

import java.util.List;

//TODO need fix scv -> drone
public class BuilderDrone extends DefaultBuildableItem {

    public BuilderDrone(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        //TODO need fix scv -> drone

        if (PlayerUtils.supplyTotalSelf() - PlayerUtils.supplyUsedSelf() < 2) {
            return false;
        }
        if (PlayerUtils.mineralSelf() < 50) {
            return false;
        }


        List<Unit> commandCenters = UnitUtils.getUnitList(UnitFindStatus.ALL, UnitType.Terran_Command_Center);
        if (!StrategyBoard.EXOK) {
            if (UnitUtils.getCompletedUnitCount(UnitType.Terran_Command_Center) == 2) {
                Unit secondCommandCenter = null;

                for (Unit commandCenter : commandCenters) {
                    if (commandCenter.getTilePosition().getX() == BlockingEntrance.Instance().starting.getX()
                            && commandCenter.getTilePosition().getY() == BlockingEntrance.Instance().starting.getY()) {
                        continue;
                    }

                    secondCommandCenter = commandCenter;
                    break;
                }

                if (secondCommandCenter != null) {
                    BaseLocation temp = BaseUtils.myFirstExpansion();
                    if (secondCommandCenter.getTilePosition().getX() != temp.getTilePosition().getX()
                            || secondCommandCenter.getTilePosition().getY() != temp.getTilePosition().getY()) {
                        BuildOrderQueue tempbuildQueue = BuildManager.Instance().getBuildQueue();
                        BuildOrderItem checkItem = null;

                        if (!tempbuildQueue.isEmpty()) {
                            checkItem = tempbuildQueue.getHighestPriorityItem();
                            while (true) {
                                if (tempbuildQueue.canGetNextItem() == true) {
                                    tempbuildQueue.canGetNextItem();
                                } else {
                                    break;
                                }
                                tempbuildQueue.PointToNextItem();
                                checkItem = tempbuildQueue.getItem();

                                if (checkItem.metaType.isUnit() && checkItem.metaType.getUnitType() == UnitType.Terran_SCV) {
                                    tempbuildQueue.removeCurrentItem();
                                }
                            }
                        }
                    }
                }
            }
        }

        int tot_mineral_self = 0;
        for (Unit commandCenter : commandCenters) {
            int minerals = WorkerManager.Instance().getWorkerData().getMineralsNearDepot(commandCenter);
            if (minerals > 0) {
                if (!commandCenter.isCompleted()) {
                    minerals = minerals * commandCenter.getHitPoints() / 1500;
                }
                tot_mineral_self += minerals;
            }
        }

        int maxworkerCount = tot_mineral_self * 2 + 8 * UnitUtils.getCompletedUnitCount(UnitType.Terran_Command_Center);
        int workerCount = UnitUtils.getUnitCount(UnitType.Terran_SCV); // workerCount = 현재 일꾼 수 + 생산중인 일꾼 수
        // List CommandCenter = UnitUtils.getCompletedUnitList(UnitType.Terran_Command_Center);
        for (Unit commandcenter : UnitUtils.getCompletedUnitList(UnitType.Terran_Command_Center)) {
            if (commandcenter.isTraining()) {
                workerCount += commandcenter.getTrainingQueue().size();
            }
        }

        int nomorescv = 65;
        if (PlayerUtils.enemyRace() == Race.Terran) {
            nomorescv = 60;
        }
        // System.out.println("maxworkerCount: " + maxworkerCount);
        if (workerCount >= nomorescv || workerCount >= maxworkerCount) {
            return false;
        }

        for (Unit commandcenter : UnitUtils.getCompletedUnitList(UnitType.Terran_Command_Center)) {
            if (commandcenter.isTraining()) {
//				return false;
                continue;
            }

            BuildOrderQueue tempbuildQueue = BuildManager.Instance().getBuildQueue();
            BuildOrderItem checkItem = null;

            if (!tempbuildQueue.isEmpty()) {
                checkItem = tempbuildQueue.getHighestPriorityItem();
                while (true) {
                    if (checkItem.blocking) {
                        break;
                    }
                    if (checkItem.metaType.isUnit() && checkItem.metaType.getUnitType() == UnitType.Terran_SCV) {
                        return false;
                    }
                    if (tempbuildQueue.canSkipCurrentItem()) {
                        tempbuildQueue.skipCurrentItem();
                    } else {
                        break;
                    }
                    checkItem = tempbuildQueue.getItem();
                }
                if (checkItem.metaType.isUnit() && checkItem.metaType.getUnitType() == UnitType.Terran_SCV) {
                    return false;
                }
            }

            // System.out.println("checkItem: " + checkItem.metaType.getName());
            if (checkItem == null) {
                // BuildManager.Instance().buildQueue.queueAsHighestPriority(new MetaType(InformationManager.Instance().getWorkerType()), false);
                setHighPriority(true);
                return true;

            } else if (checkItem.metaType.isUnit()) {
                if (checkItem.metaType.getUnitType() == UnitType.Terran_Comsat_Station) {
                    return false;

                } else if (checkItem.metaType.getUnitType() != UnitType.Terran_SCV) {
                    if (workerCount < 4) {
                        // BuildManager.Instance().buildQueue.queueAsHighestPriority(new MetaType(InformationManager.Instance().getWorkerType()), false);
                        setHighPriority(true);
                        return true;
                    } else {
                        int checkgas = checkItem.metaType.getUnitType().gasPrice() - PlayerUtils.gasSelf();
                        if (checkgas < 0) {
                            checkgas = 0;
                        }
                        if (PlayerUtils.mineralSelf() > checkItem.metaType.getUnitType().mineralPrice() + 50 - checkgas) {
                            // BuildManager.Instance().buildQueue.queueAsHighestPriority(new MetaType(InformationManager.Instance().getWorkerType()), false);
                            setHighPriority(true);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean isInitialBuildFinshed() {
        return TimeUtils.afterTime(1, 40);
    }

}
