package org.monster.build.provider.items.unit;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.board.StrategyBoard;
import org.monster.build.base.BuildManager;
import org.monster.build.base.ConstructionManager;
import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;
import org.monster.common.constant.UnitFindStatus;
import org.monster.common.util.UnitUtils;

import java.util.List;

//EXAMPLE
@Deprecated
public class BuilderMarine extends DefaultBuildableItem {

    public boolean liftChecker = false;

    public BuilderMarine(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        List<Unit> barracks = UnitUtils.getCompletedUnitList(UnitType.Terran_Barracks);
        for (Unit unit : barracks) {
            if (!unit.isLifted()) {
                liftChecker = true;
            }
        }

        if (!liftChecker) {
            return false;
        }

        int nowMarine = UnitUtils.getCompletedUnitCount(UnitType.Terran_Marine);

//    	마린이 2마리가 생산된 상태에서 팩토리가 없다면 팩토리 먼저
        if (nowMarine == 2 && UnitUtils.getUnitCount(UnitFindStatus.ALL_AND_CONSTRUCTION_QUEUE, UnitType.Terran_Factory) == 0) {
            return false;
        }

        if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Terran_Marine, null)
                + ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Terran_Marine, null) > 0) {
            return false;
        }

        if (nowMarine < StrategyBoard.marineCount) {
            setHighPriority(true);
            return true;
        }

        return false;

    }

    @Override
    public boolean isInitialBuildFinshed() {
        return true;
    }
}
