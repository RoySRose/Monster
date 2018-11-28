package org.monster.build.provider.items.unit;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;


public class BuilderOverload extends DefaultBuildableItem {


    public BuilderOverload(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        //SupplyDepot 참고
        return false;

    }

    @Override
    public boolean checkInitialBuild() {
        return true;
    }
}
