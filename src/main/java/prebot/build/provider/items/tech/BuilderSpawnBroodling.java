package prebot.build.provider.items.tech;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

public class BuilderSpawnBroodling extends DefaultBuildableItem {

    public BuilderSpawnBroodling(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
