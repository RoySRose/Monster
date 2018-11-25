package prebot.build.provider.items.unit;

import bwapi.UnitType;
import prebot.build.provider.DefaultBuildableItem;
import prebot.build.provider.FactoryUnitSelector;
import prebot.common.MetaType;
import prebot.main.Monster;
import prebot.common.util.UnitUtils;
import prebot.strategy.StrategyIdea;
import prebot.strategy.constant.EnemyStrategyOptions;

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

//        	if(RespondToStrategy.Instance().once_tank) {
//        		setBlocking(true);
//        		RespondToStrategy.Instance().once_tank = false;
//        	}
            if (!UnitUtils.myUnitDiscovered(UnitType.Terran_Siege_Tank_Tank_Mode)
                    && StrategyIdea.factoryRatio.weight == EnemyStrategyOptions.FactoryRatio.Weight.TANK) {
                setHighPriority(true);
            }
            return true;

        }
        return false;
    }
}
