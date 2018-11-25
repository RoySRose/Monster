package prebot.build.provider.items.building;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

public class BuilderLair extends DefaultBuildableItem {

    public BuilderLair(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
