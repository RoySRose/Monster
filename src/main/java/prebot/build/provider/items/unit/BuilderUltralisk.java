package prebot.build.provider.items.unit;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

public class BuilderUltralisk extends DefaultBuildableItem {


    public BuilderUltralisk(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;

    }

    @Override
    public boolean checkInitialBuild() {
        return true;
    }
}
