package org.monster.build.provider.items.building;

import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;

public class BuilderEvolutionChamber extends DefaultBuildableItem {

    public BuilderEvolutionChamber(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        return false;
    }


}
