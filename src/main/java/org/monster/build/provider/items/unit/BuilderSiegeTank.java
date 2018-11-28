package org.monster.build.provider.items.unit;

import bwapi.UnitType;
import org.monster.build.provider.DefaultBuildableItem;
import org.monster.build.provider.FactoryUnitSelector;
import org.monster.common.MetaType;
import org.monster.common.util.UnitUtils;
import org.monster.decisionMakers.constant.EnemyStrategyOptions;
import org.monster.main.Monster;
import org.monster.board.StrategyBoard;

//EXAMPLE
@Deprecated
public class BuilderSiegeTank extends DefaultBuildableItem {

    FactoryUnitSelector factoryUnitSelector;

    public BuilderSiegeTank(MetaType metaType, FactoryUnitSelector factoryUnitSelector) {
        super(metaType);
        this.factoryUnitSelector = factoryUnitSelector;
    }

    public final boolean buildCondition() {

        if (Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Factory) == 0) {
            return false;
        }

        if (Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Machine_Shop) == 0) {
            return false;
        }

        if (factoryUnitSelector.getSelected().equals(metaType.getUnitType())) {

            if (!UnitUtils.myUnitDiscovered(UnitType.Terran_Siege_Tank_Tank_Mode)
                    && StrategyBoard.factoryRatio.weight == EnemyStrategyOptions.FactoryRatio.Weight.TANK) {
                setHighPriority(true);
            }
            return true;

        }
        return false;
    }
}
