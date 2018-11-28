package org.monster.build.provider.items;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;

//EXAMPLE
@Deprecated
public class BuilderExample extends DefaultBuildableItem {

    public BuilderExample(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {
        System.out.println("example build condition check");
        return true;
    }


}
