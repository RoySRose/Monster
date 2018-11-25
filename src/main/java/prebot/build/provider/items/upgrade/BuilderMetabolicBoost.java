package prebot.build.provider.items.upgrade;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

public class BuilderMetabolicBoost extends DefaultBuildableItem {

    //zergling zpeed?
    public BuilderMetabolicBoost(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
