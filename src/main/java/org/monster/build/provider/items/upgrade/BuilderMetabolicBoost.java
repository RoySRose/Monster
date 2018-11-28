package org.monster.build.provider.items.upgrade;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;

public class BuilderMetabolicBoost extends DefaultBuildableItem {

    //zergling zpeed?
    public BuilderMetabolicBoost(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
