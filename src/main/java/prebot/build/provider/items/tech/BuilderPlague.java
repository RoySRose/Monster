package prebot.build.provider.items.tech;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

public class BuilderPlague extends DefaultBuildableItem {

    public BuilderPlague(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
