package prebot.build.provider.items.unit;

import bwapi.UnitType;
import prebot.build.provider.DefaultBuildableItem;
import prebot.build.provider.FactoryUnitSelector;
import prebot.common.MetaType;
import prebot.common.util.UnitUtils;
import prebot.strategy.StrategyIdea;
import prebot.strategy.constant.EnemyStrategyOptions;

//EXAMPLE
@Deprecated
public class BuilderVulture extends DefaultBuildableItem {

    FactoryUnitSelector factoryUnitSelector;

    public BuilderVulture(MetaType metaType, FactoryUnitSelector factoryUnitSelector) {
        super(metaType);
        this.factoryUnitSelector = factoryUnitSelector;
    }

    public final boolean buildCondition() {

        if (StrategyIdea.addOnOption == EnemyStrategyOptions.AddOnOption.IMMEDIATELY && !UnitUtils.myUnitDiscovered(UnitType.Terran_Machine_Shop)) {
            return false;
        }


        if (factoryUnitSelector.getSelected().equals(metaType.getUnitType())) {
            return true;
        }

        return false;
    }
}


