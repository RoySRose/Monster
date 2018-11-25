package prebot.build.provider.items.upgrade;

import prebot.build.provider.DefaultBuildableItem;
import prebot.build.provider.UpgradeSelector;
import prebot.common.MetaType;

public class BuilderZergMeleeAtttacks extends DefaultBuildableItem {

    //use selector if needed
    UpgradeSelector upgradeSelector;

    public BuilderZergMeleeAtttacks(MetaType metaType, UpgradeSelector upgradeSelector) {
        super(metaType);
        this.upgradeSelector = upgradeSelector;
    }

    public final boolean buildCondition() {

        if (upgradeSelector.getSelected().equals(metaType)) {
            return true;
        } else {
            return false;
        }
    }
}
