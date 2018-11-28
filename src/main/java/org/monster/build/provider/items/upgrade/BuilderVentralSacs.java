package org.monster.build.provider.items.upgrade;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;

public class BuilderVentralSacs extends DefaultBuildableItem {

    //overload drop
    public BuilderVentralSacs(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
