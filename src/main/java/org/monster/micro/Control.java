package org.monster.micro;

import java.util.Set;

import org.monster.common.UnitInfo;

import bwapi.Unit;

public abstract class Control {
	
	protected abstract void execute(Unit unit, Set<UnitInfo> enemies);
}
