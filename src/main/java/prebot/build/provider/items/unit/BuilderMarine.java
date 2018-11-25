package prebot.build.provider.items.unit;

import bwapi.Unit;
import bwapi.UnitType;
import prebot.build.prebot1.BuildManager;
import prebot.build.prebot1.ConstructionManager;
import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;
import prebot.common.constant.CommonCode;
import prebot.main.Monster;
import prebot.common.util.UnitUtils;
import prebot.strategy.StrategyIdea;

import java.util.List;

//EXAMPLE
@Deprecated
public class BuilderMarine extends DefaultBuildableItem {

    public boolean liftChecker = false;

    public BuilderMarine(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        List<Unit> barracks = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Barracks);
        for (Unit unit : barracks) {
            if (!unit.isLifted()) {
                liftChecker = true;
            }
        }

        if (!liftChecker) {
            return false;
        }

        int nowMarine = Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Marine);

//    	마린이 2마리가 생산된 상태에서 팩토리가 없다면 팩토리 먼저
        if (nowMarine == 2 && UnitUtils.getUnitCount(CommonCode.UnitFindRange.ALL_AND_CONSTRUCTION_QUEUE, UnitType.Terran_Factory) == 0) {
            return false;
        }

        if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Terran_Marine, null)
                + ConstructionManager.Instance().getConstructionQueueItemCount(UnitType.Terran_Marine, null) > 0) {
            return false;
        }

        if (nowMarine < StrategyIdea.marineCount) {
            setHighPriority(true);
            return true;
        }

        return false;

    }

    @Override
    public boolean checkInitialBuild() {
        return true;
    }
}
