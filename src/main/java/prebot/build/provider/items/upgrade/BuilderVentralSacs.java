package prebot.build.provider.items.upgrade;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

public class BuilderVentralSacs extends DefaultBuildableItem {

    //overload drop
    public BuilderVentralSacs(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
