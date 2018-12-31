package org.monster.oldmicro.control.groundforce;//package org.monster.micro.control.factory;

import bwapi.Unit;
import org.monster.common.UnitInfo;
import org.monster.oldmicro.control.Control;

import java.util.Collection;

public class HydraControl extends Control {

    private int saveUnitLevel;

    public void setSaveUnitLevel(int saveUnitLevel) {
        this.saveUnitLevel = saveUnitLevel;
    }

    @Override
    public void control(Collection<Unit> unitList, Collection<UnitInfo> euiList) {

    }
}
