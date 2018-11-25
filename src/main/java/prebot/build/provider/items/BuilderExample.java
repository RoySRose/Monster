package prebot.build.provider.items;

import prebot.build.provider.DefaultBuildableItem;
import prebot.common.MetaType;

//EXAMPLE
@Deprecated
public class BuilderExample extends DefaultBuildableItem {

    public BuilderExample(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {
        System.out.println("example build condition check");
        return true;
    }


}
