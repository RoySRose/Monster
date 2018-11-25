package prebot.build.provider.items.unit;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;


public class BuilderOverload extends DefaultBuildableItem {


    public BuilderOverload(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        //SupplyDepot 참고
        return false;

    }

    @Override
    public boolean checkInitialBuild() {
        return true;
    }
}
