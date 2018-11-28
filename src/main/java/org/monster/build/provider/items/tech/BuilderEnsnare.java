package org.monster.build.provider.items.tech;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;

public class BuilderEnsnare extends DefaultBuildableItem {

    public BuilderEnsnare(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
