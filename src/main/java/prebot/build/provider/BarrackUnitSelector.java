package prebot.build.provider;

import bwapi.UnitType;

//EXAMPLE
@Deprecated
public class BarrackUnitSelector implements Selector<UnitType> {

    public boolean block;
    // BuildCondition buildCondition;
    public boolean highpriority;
    public boolean tileposition;
    public boolean seedposition;
    UnitType unitType;

    public final UnitType getSelected() {
        return unitType;
    }

    public final void select() {
        unitType = UnitType.None;
    }
}
