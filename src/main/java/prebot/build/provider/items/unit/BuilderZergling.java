package prebot.build.provider.items.unit;

import bwapi.Unit;
import bwapi.UnitType;
import prebot.build.prebot1.BuildManager;
import prebot.build.prebot1.ConstructionManager;
import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;
import prebot.common.constant.CommonCode;
import prebot.common.util.UnitUtils;
import prebot.main.Monster;
import prebot.strategy.StrategyIdea;

import java.util.List;

public class BuilderZergling extends DefaultBuildableItem {


    public BuilderZergling(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;

    }

    @Override
    public boolean checkInitialBuild() {
        return true;
    }
}
