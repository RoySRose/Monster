package org.monster.common.util.internal;

import bwapi.Unit;
import bwta.BaseLocation;

public class IConditions {
    public interface UnitCondition {
        public boolean correspond(Unit unit);
    }

    public interface BaseCondition {
        public boolean correspond(BaseLocation base);
    }
}
