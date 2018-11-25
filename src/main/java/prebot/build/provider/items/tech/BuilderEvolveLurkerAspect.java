package prebot.build.provider.items.tech;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

public class BuilderEvolveLurkerAspect extends DefaultBuildableItem {

    //hydra to lurker
    public BuilderEvolveLurkerAspect(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
