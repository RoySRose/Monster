package org.monster.build.provider.items.unit;

import bwapi.UnitType;
import org.monster.build.provider.DefaultBuildableItem;
import org.monster.build.provider.FactoryUnitSelector;
import org.monster.common.MetaType;
import org.monster.common.util.UnitUtils;
import org.monster.decisionMakers.constant.EnemyStrategyOptions;
import org.monster.board.StrategyBoard;

//EXAMPLE
@Deprecated
public class BuilderVulture extends DefaultBuildableItem {

    FactoryUnitSelector factoryUnitSelector;

    public BuilderVulture(MetaType metaType, FactoryUnitSelector factoryUnitSelector) {
        super(metaType);
        this.factoryUnitSelector = factoryUnitSelector;
    }

    public final boolean buildCondition() {

        if (StrategyBoard.addOnOption == EnemyStrategyOptions.AddOnOption.IMMEDIATELY && !UnitUtils.myUnitDiscovered(UnitType.Terran_Machine_Shop)) {
            return false;
        }


        if (factoryUnitSelector.getSelected().equals(metaType.getUnitType())) {
            return true;
        }

        return false;
    }
}


