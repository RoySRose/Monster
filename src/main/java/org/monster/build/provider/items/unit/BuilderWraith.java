package org.monster.build.provider.items.unit;

import bwapi.UnitType;
import org.monster.board.StrategyBoard;
import org.monster.build.base.BuildManager;
import org.monster.build.provider.DefaultBuildableItem;
import org.monster.build.provider.StarportUnitSelector;
import org.monster.common.MetaType;
import org.monster.common.util.UnitUtils;
import org.monster.bootstrap.Monster;

//EXAMPLE
@Deprecated
public class BuilderWraith extends DefaultBuildableItem {

    StarportUnitSelector starportUnitSelector;

    public BuilderWraith(MetaType metaType) {
        super(metaType);
//        this.starportUnitSelector = starportUnitSelector;
    }

    public final boolean buildCondition() {

        int maxWraithCnt = StrategyBoard.wraithCount;

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
