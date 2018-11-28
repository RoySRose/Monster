package org.monster.build.provider.items.upgrade;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.build.provider.UpgradeSelector;
import org.monster.common.MetaType;

public class BuilderZergMissileAtttacks extends DefaultBuildableItem {

    //use selector if needed
    UpgradeSelector upgradeSelector;

    public BuilderZergMissileAtttacks(MetaType metaType, UpgradeSelector upgradeSelector) {
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
