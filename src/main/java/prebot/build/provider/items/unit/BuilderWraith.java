package prebot.build.provider.items.unit;

import bwapi.UnitType;
import prebot.build.prebot1.BuildManager;
import prebot.build.provider.DefaultBuildableItem;
import prebot.build.provider.StarportUnitSelector;
import prebot.common.MetaType;
import prebot.main.Monster;
import prebot.common.util.UnitUtils;
import prebot.strategy.StrategyIdea;

//EXAMPLE
@Deprecated
public class BuilderWraith extends DefaultBuildableItem {

    StarportUnitSelector starportUnitSelector;

    public BuilderWraith(MetaType metaType) {
        super(metaType);
//        this.starportUnitSelector = starportUnitSelector;
    }

    public final boolean buildCondition() {

        int maxWraithCnt = StrategyIdea.wraithCount;

        if (Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Starport) == 0) {
            return false;
        }

        if (Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Wraith) < maxWraithCnt) {
            // if(Prebot.Broodwar.self().deadUnitCount(UnitType.Terran_Wraith) < 5)
            if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Terran_Wraith, null) == 0) {
                if (!UnitUtils.myCompleteUnitDiscovered(UnitType.Terran_Wraith)) {
                    setHighPriority(true);
                }
                return true;
            }
        }
        return false;
    }
}
