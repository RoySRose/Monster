package org.monster.build.provider.items.upgrade;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;

public class BuilderAntennae extends DefaultBuildableItem {

    //Overload Sight
    public BuilderAntennae(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
