package org.monster.build.provider.items.tech;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;

public class BuilderConsume extends DefaultBuildableItem {

    public BuilderConsume(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
