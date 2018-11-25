package prebot.build.provider.items.upgrade;

import bwapi.UnitType;
import prebot.build.base.BuildManager;
import prebot.build.provider.DefaultBuildableItem;
import prebot.build.provider.ResearchSelector;
import prebot.common.MetaType;
import prebot.main.Monster;

public class BuilderGroovedSpines extends DefaultBuildableItem {

    //예) BuilderMuscularAugments 와 BuilderGroovedSpines 중에 무엇을 먼저 선택하는놈이 필요하다면 researchSelector 만들고 이용
    ResearchSelector researchSelector;

    public BuilderGroovedSpines(MetaType metaType, ResearchSelector researchSelector) {
        super(metaType);
        this.researchSelector = researchSelector;
    }

    public final boolean buildCondition() {

        //if(researchSelector.getSelected().getUpgradeType().equals(metaType.getUpgradeType())) {
        if (Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Armory) == 0) {
            return false;
        }

        if (BuildManager.Instance().buildQueue.getItemCount(researchSelector.getSelected(), null) != 0) {
            return false;
        }
        if (researchSelector.getSelected() == null) {
            return false;
        }
        if (!researchSelector.getSelected().isUpgrade()) {
            return false;
        }
        if (researchSelector.getSelected().getUpgradeType() != metaType.getUpgradeType()) {
            return false;
        }
        if (Monster.Broodwar.self().isUpgrading(researchSelector.getSelected().getUpgradeType())) {
            return false;
        }

        if (researchSelector.currentResearched <= 2) {
            setBlocking(true);
            setHighPriority(true);
        }
        return true;
    }
}
