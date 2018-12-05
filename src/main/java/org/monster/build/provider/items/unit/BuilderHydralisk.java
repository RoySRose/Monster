package org.monster.build.provider.items.unit;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;

public class BuilderHydralisk extends DefaultBuildableItem {


    public BuilderHydralisk(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;

    }

    @Override
    public boolean isInitialBuildFinshed() {
        return true;
    }
}
