package prebot.build.provider.items.upgrade;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

public class BuilderAdrenalGlands extends DefaultBuildableItem {

    //Zergling attack speed
    public BuilderAdrenalGlands(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
