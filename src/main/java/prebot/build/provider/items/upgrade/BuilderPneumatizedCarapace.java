package prebot.build.provider.items.upgrade;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

public class BuilderPneumatizedCarapace extends DefaultBuildableItem {

    //Overload Speed
    public BuilderPneumatizedCarapace(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
