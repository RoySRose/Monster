package org.monster.build.provider;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import org.monster.build.provider.items.building.BuilderHatchery;
import org.monster.build.provider.items.building.BuilderSpawningPool;
import org.monster.build.provider.items.tech.BuilderConsume;
import org.monster.build.provider.items.unit.BuilderDrone;
import org.monster.build.provider.items.unit.BuilderZergling;
import org.monster.build.provider.items.upgrade.BuilderMetabolicBoost;
import org.monster.common.LagObserver;
import org.monster.common.MetaType;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.TimeUtils;
import org.monster.bootstrap.GameManager;
import org.monster.common.util.UpgradeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildQueueProvider extends GameManager {

    private static BuildQueueProvider instance = new BuildQueueProvider();
    List<BuildableItem> buildableList = new ArrayList<>();
    ResearchSelector researchSelector;
    UpgradeSelector upgradeSelector;
    FactoryUnitSelector factoryUnitSelector;
    private Map<Integer, Integer> notOperatingFactoryTime = new HashMap<>();
    private Map<UpgradeType, Integer> upgradeStartMap = new HashMap<>();

    public BuildQueueProvider() {

        //TODO 저그용으로 업데이트 필요
        researchSelector = new ResearchSelector();
        upgradeSelector = new UpgradeSelector();
        factoryUnitSelector = new FactoryUnitSelector();


        /*Drone*/
        buildableList.add(new BuilderDrone(new MetaType(UnitType.Zerg_Drone)));
        /*Units*/
        buildableList.add(new BuilderZergling(new MetaType(UnitType.Zerg_Zergling)));


        /*Building*/
        buildableList.add(new BuilderHatchery(new MetaType(UnitType.Zerg_Hatchery)));
        buildableList.add(new BuilderSpawningPool(new MetaType(UnitType.Zerg_Spawning_Pool)));

        /*upgrade*/
        buildableList.add(new BuilderMetabolicBoost(new MetaType(UpgradeType.Metabolic_Boost)));
//        buildableList.add(ionThrusters);
//        buildableList.add(terranShipPlating);
//        buildableList.add(terranShipWeapons);
//        buildableList.add(terranVehiclePlating);
//        buildableList.add(terranVehicleWeapons);

        /*tech*/
        buildableList.add(new BuilderConsume(new MetaType(TechType.Consume)));

    }

    /// static singleton 객체를 리턴합니다
    public static BuildQueueProvider Instance() {
        return instance;
    }

    public void startUpgrade(UpgradeType upgradeType) {
        upgradeStartMap.put(upgradeType, TimeUtils.getFrame());
    }

    public int upgradeRemainingFrame(UpgradeType upgradeType) {
        if (!UpgradeUtils.selfIsUpgrading(upgradeType)) {
            return CommonCode.UNKNOWN;
        }
        Integer startFrame = upgradeStartMap.get(upgradeType);
        if (startFrame == null) {
            return CommonCode.UNKNOWN;
        }
        return upgradeType.upgradeTime() - TimeUtils.getFrame(startFrame);
    }


    public void update() {
        if (TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER3, 0), LagObserver.managerRotationSize())) {
            researchSelector.select();
            upgradeSelector.select();
            factoryUnitSelector.select();
            for (BuildableItem buildableItem : buildableList) {
                buildableItem.process();
            }
        }

        //TODO executeCombatUnitTrainingBlocked necessary?
//        if (TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER3, 1), LagObserver.managerRotationSize())) {
//            executeCombatUnitTrainingBlocked();
//        }
    }

//    public void executeCombatUnitTrainingBlocked() {
//
//        if (PlayerUtils.supplyTotalSelf() - PlayerUtils.supplyUsedSelf() < 4) {
//            return;
//        }
//        if (PlayerUtils.supplyUsedSelf() > 392) {
//            return;
//        }
//        if (PlayerUtils.mineralSelf() < 500) {
//            return;
//        }
//        BuildOrderQueue tempbuildQueue = BuildManager.Instance().getBuildQueue();
//        if (tempbuildQueue.isEmpty()) {
//            return;
//        }
//        List<Unit> factories = UnitUtils.getCompletedUnitList(UnitType.Terran_Factory);
//        if (factories.isEmpty()) {
//            return;
//        }
//
//        // 팩토리를 일정 시간 이상 가동되지 않았을 때를 비가동 팩토리로 본다.
//        List<Unit> notOperatingFactories = new ArrayList<>();
//        for (Unit factory : factories) {
//            if (factory.isTraining()) {
//                if (notOperatingFactoryTime.containsKey(factory.getID())) {
//                    notOperatingFactoryTime.remove(factory.getID());
//                }
//                continue;
//            }
//
//            Integer notOperatingFrame = notOperatingFactoryTime.get(factory.getID());
//            if (notOperatingFrame == null) {
//                notOperatingFactoryTime.put(factory.getID(), TimeUtils.getFrame());
//            } else {
//                if (TimeUtils.elapsedSeconds(notOperatingFrame) >= 3) {
//                    notOperatingFactories.add(factory);
//                }
//            }
//        }
//
//        if (notOperatingFactories.isEmpty()) {
//            return;
//        }
//
////		System.out.println("notOperatingFactories.size() = " + notOperatingFactories.size());
//
//        boolean goliathInTheQueue = false;
//        boolean tankInTheQueue = false;
//
//        BuildOrderItem blockingItem = tempbuildQueue.getHighestPriorityItem();
//        while (true) {
//            if (blockingItem.metaType.isUnit()) {
//                UnitType unitType = blockingItem.metaType.getUnitType();
//                if (unitType == UnitType.Terran_Goliath) {
//                    goliathInTheQueue = true;
//                } else if (blockingItem.metaType.getUnitType() == UnitType.Terran_Siege_Tank_Tank_Mode) {
//                    tankInTheQueue = true;
//
//                } else if (unitType == UnitType.Terran_Supply_Depot
//                        || unitType == UnitType.Terran_Missile_Turret
//                        || unitType == UnitType.Terran_Vulture
//                        || unitType == UnitType.Terran_SCV) {
////						|| unitType.isAddon()) {
//                    return;
//                }
//            }
//            if (blockingItem.blocking || !tempbuildQueue.canSkipCurrentItem()) {
//                break;
//            }
//
//            tempbuildQueue.skipCurrentItem();
//            blockingItem = tempbuildQueue.getItem();
//        }
//
//        boolean isArmoryExists = UnitUtils.getCompletedUnitCount(UnitType.Terran_Armory) > 0;
//        boolean vultureInTheQueue = tempbuildQueue.getItemCount(UnitType.Terran_Vulture) > 0;
//
//        int totVulture = UnitUtils.getUnitCount(UnitType.Terran_Vulture);
//        int totTank = UnitUtils.getUnitCount(UnitType.Terran_Siege_Tank_Tank_Mode) + UnitUtils.getUnitCount(UnitType.Terran_Siege_Tank_Siege_Mode);
//        int totGoliath = UnitUtils.getUnitCount(UnitType.Terran_Goliath);
//
//        int vultureratio = StrategyBoard.factoryRatio.vulture;
//        int tankratio = StrategyBoard.factoryRatio.tank;
//        int goliathratio = StrategyBoard.factoryRatio.goliath;
//        int wgt = StrategyBoard.factoryRatio.weight;
//
//        for (Unit factory : notOperatingFactories) {
//            if (factory.isTraining()) {
//                continue;
//            }
//
//            if (blockingItem.metaType.isUnit()) {
//                if (blockingItem.metaType.getUnitType() == UnitType.Terran_Machine_Shop) {
//                    if (factory.getAddon() == null) {
//                        continue;
//                    }
//                } else if (blockingItem.metaType.getUnitType() == UnitType.Terran_Siege_Tank_Tank_Mode) {
//                    if (factory.getAddon() != null && !factory.getAddon().isCompleted()) {
//                        continue;
//                    }
//                } else if (blockingItem.metaType.getUnitType() == UnitType.Terran_Goliath) {
//                    if (isArmoryExists) {
//                        break;
//                    }
//                }
//            }
//
//            UnitType selected = FactoryUnitSelector.chooseunit(vultureratio, tankratio, goliathratio, wgt, totVulture, totTank, totGoliath);
//
//            if (blockingItem.metaType.isUnit() && blockingItem.metaType.getUnitType() != selected) {
//                if (selected == UnitType.Terran_Siege_Tank_Tank_Mode && !tankInTheQueue && factory.getAddon() != null && factory.getAddon().isCompleted()) {
//                    int mineralNeed = blockingItem.metaType.mineralPrice() + selected.mineralPrice();
//                    int gasNeed = blockingItem.metaType.gasPrice() + selected.gasPrice();
//                    if (PlayerUtils.hasMoreResourceThan(mineralNeed, gasNeed)) {
////						System.out.println("block tank provided");
//                        BuildManager.Instance().buildQueue.queueAsHighestPriority(selected, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
//                        return;
//                    }
//                } else if (selected == UnitType.Terran_Goliath && !goliathInTheQueue && isArmoryExists) {
//                    int mineralNeed = blockingItem.metaType.mineralPrice() + selected.mineralPrice();
//                    int gasNeed = blockingItem.metaType.gasPrice() + selected.gasPrice();
//                    if (PlayerUtils.hasMoreResourceThan(mineralNeed, gasNeed)) {
////						System.out.println("block goliath provided");
//                        BuildManager.Instance().buildQueue.queueAsHighestPriority(selected, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
//                        return;
//                    }
//                }
//
//            }
//
//            if (!vultureInTheQueue) {
//                int mineralNeed = selected.mineralPrice();
//                if (PlayerUtils.gasSelf() < 250) {
//                    mineralNeed = 75;
//                }
//                mineralNeed = blockingItem.metaType.mineralPrice() + mineralNeed;
//                if (PlayerUtils.hasMoreResourceThan(mineralNeed, 0)) {
//                    if (factory.isConstructing() || ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Terran_Machine_Shop, null) != 0) {
//                        continue;
//                    }
//                    // if(selected == UnitType.Terran_Goliath && isarmoryexists == false){
//                    // continue;
//                    // }
////					System.out.println("block vulture provided");
//                    BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Vulture, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, false);
//                    return;
//                }
//            }
//        }
//    }


}
