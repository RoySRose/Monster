package org.monster.build.provider;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import org.monster.build.base.BuildManager;
import org.monster.build.base.BuildOrderItem;
import org.monster.build.initialProvider.InitialBuildProvider;
import org.monster.common.MetaType;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.UnitUtils;
import org.monster.bootstrap.Monster;

import java.util.List;

//import prebot.common.util.UnitUtils;

public abstract class DefaultBuildableItem implements BuildableItem {

    public final MetaType metaType;
    private final UnitType producerOfUnit;
    BuildCondition buildCondition;
    private int recoverItemCount = -1;

    public DefaultBuildableItem(MetaType metaType) {
        buildCondition = new BuildCondition(false, false, BuildOrderItem.SeedPositionStrategy.NoLocation, TilePosition.None);
//    	buildCondition = new BuildCondition(false, false, BuildOrderItem.SeedPositionStrategy.MainBaseLocation, TilePosition.None);
        this.metaType = metaType;
        //setProducerOfUnit();
        if (metaType.isUnit() && (!metaType.getUnitType().isBuilding() || metaType.getUnitType().isAddon())) {
            producerOfUnit = metaType.getUnitType().whatBuilds().first;
        } else {
            producerOfUnit = null;
        }
    }

    //5개의 set 함수가 조건을 결정. 기본은 default, set 했을때는 해당 조건 반영
    //4개는 build condition 안에 들어가면 될듯
    public final void setBlocking(boolean blocking) {
        this.buildCondition.blocking = blocking;
    }

    public final void setHighPriority(boolean highPriority) {
        this.buildCondition.highPriority = highPriority;
    }

    public final void setSeedPositionStrategy(BuildOrderItem.SeedPositionStrategy seedPositionStrategy) {
        this.buildCondition.seedPositionStrategy = seedPositionStrategy;
    }

    public final void setTilePosition(TilePosition tilePosition) {
        this.buildCondition.tilePosition = tilePosition;
    }

    //이놈만 유닛 변경 있을때만 확인해 주면 될듯
    public void setRecoverItemCount(int recoverItemCountVar) {
        this.recoverItemCount = recoverItemCount;
    }

    private final void build() {
        if (!metaType.isUnit() &&
                (buildCondition.seedPositionStrategy != BuildOrderItem.SeedPositionStrategy.NoLocation
                        || buildCondition.tilePosition != TilePosition.None)) {
            System.out.println("Only UnitType can have position attribute");
        }
        //when blocking is false check resource
        if (!buildCondition.blocking) {
            if (metaType.mineralPrice() <= Monster.Broodwar.self().minerals() && metaType.gasPrice() <= Monster.Broodwar.self().gas()) {
                setBuildQueue();
            }
        } else {
            setBuildQueue();
        }
    }

    private final void setBuildQueue() {
        if (buildCondition.highPriority) {
            if (buildCondition.seedPositionStrategy != BuildOrderItem.SeedPositionStrategy.NoLocation) {
                BuildManager.Instance().buildQueue.queueAsHighestPriority(metaType, buildCondition.seedPositionStrategy, buildCondition.blocking);
            } else if (buildCondition.tilePosition != TilePosition.None) {
                BuildManager.Instance().buildQueue.queueAsHighestPriority(metaType, buildCondition.tilePosition, buildCondition.blocking);
            } else {
                BuildManager.Instance().buildQueue.queueAsHighestPriority(metaType, buildCondition.blocking);
            }
        } else {
            if (buildCondition.seedPositionStrategy != BuildOrderItem.SeedPositionStrategy.NoLocation) {
                BuildManager.Instance().buildQueue.queueAsLowestPriority(metaType, buildCondition.seedPositionStrategy, buildCondition.blocking);
            } else if (buildCondition.tilePosition != TilePosition.None) {
                BuildManager.Instance().buildQueue.queueAsLowestPriority(metaType, buildCondition.tilePosition, buildCondition.blocking);
            } else {
                BuildManager.Instance().buildQueue.queueAsLowestPriority(metaType, buildCondition.blocking);
            }
        }
    }

    public final void process() {
        setDefaultConditions();
        if (satisfyBasicConditions()) {
            if (activateRecovery()) {
                build();
            } else {
                if (buildCondition()) {
                    build();
                }
            }
        }
    }

    public final boolean activateRecovery() {


        if (!metaType.isUnit()) {
            return false;
        }
        if (recoverItemCount == -1) {
            return false;
        }

        return recoverItemCount > getCurrentItemCount();
    }

    protected int getCurrentItemCount() {
        int currentItemCount = BuildManager.Instance().buildQueue.getItemCount(metaType.getUnitType()) +
                Monster.Broodwar.self().allUnitCount(metaType.getUnitType());
        return currentItemCount;
    }

    private final void setDefaultConditions() {
        this.buildCondition.blocking = false;
        this.buildCondition.highPriority = false;
        this.buildCondition.seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.NoLocation;
//        this.buildCondition.seedPositionStrategy = BuildOrderItem.SeedPositionStrategy.MainBaseLocation;
        this.buildCondition.tilePosition = TilePosition.None;
    }

    private final boolean satisfyBasicConditions() {
        //이니셜빌드는 끝났어야 한다.
        if (!checkInitialBuild()) {
            return false;
        }
        //For units check supply
        if (!supplySpaceAvailable()) {
            return false;
        }
        //For units check Producers
        if (!producerOfUnitAvailable()) {
            return false;
        }

        return true;
    }

    public boolean checkInitialBuild() {
        return InitialBuildProvider.Instance().getAdaptStrategyStatus() != InitialBuildProvider.AdaptStrategyStatus.BEFORE;
    }

    private final boolean supplySpaceAvailable() {
        int supplyMargin = Monster.Broodwar.self().supplyTotal() - Monster.Broodwar.self().supplyUsed();
        int metaTypeSupplyCount = metaType.supplyRequired();
        return metaTypeSupplyCount <= supplyMargin;
    }

    private final boolean producerOfUnitAvailable() {
        if (!metaType.isUnit() || metaType.getUnitType().isBuilding()) {
            return true;
        }
        List<Unit> producerList = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, producerOfUnit);
        for (Unit producer : producerList) {
            if (!producer.isTraining() && !producer.isConstructing() && !producer.isResearching() && !producer.isUpgrading()) {
                return true;
            }
        }
        return false;
    }

    public int validMineralCountNearDepot(Unit commandCenter) {
        if (!UnitUtils.isValidUnit(commandCenter)) {
            return 0;
        }

        int mineralsNearDepot = 0;
        for (Unit mineral : Monster.Broodwar.neutral().getUnits()) {
            if (mineral.getType() != UnitType.Resource_Mineral_Field) {
                continue;
            }
            if (mineral.getDistance(commandCenter) < 450 && mineral.getResources() > 200) {
                mineralsNearDepot++;
            }
        }
        return mineralsNearDepot;
    }


}
