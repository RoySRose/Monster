package prebot.build.provider.items.upgrade;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

public class BuilderAntennae extends DefaultBuildableItem {

    //Overload Sight
    public BuilderAntennae(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
