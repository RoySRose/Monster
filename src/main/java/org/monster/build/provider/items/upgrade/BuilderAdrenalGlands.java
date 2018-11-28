package org.monster.build.provider.items.upgrade;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;

public class BuilderAdrenalGlands extends DefaultBuildableItem {

    //Zergling attack speed
    public BuilderAdrenalGlands(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
