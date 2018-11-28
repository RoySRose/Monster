package org.monster.micro.targeting;

import bwapi.Unit;
import org.monster.common.UnitInfo;

@Deprecated
public abstract class TargetScoreCalculator {
    public abstract int calculate(Unit unit, UnitInfo eui);
}
