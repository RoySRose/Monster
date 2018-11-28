package org.monster.build.provider.items.upgrade;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;

public class BuilderPneumatizedCarapace extends DefaultBuildableItem {

    //Overload Speed
    public BuilderPneumatizedCarapace(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
