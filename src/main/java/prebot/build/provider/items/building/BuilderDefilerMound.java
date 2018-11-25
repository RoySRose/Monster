package prebot.build.provider.items.building;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

public class BuilderDefilerMound extends DefaultBuildableItem {

    public BuilderDefilerMound(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
