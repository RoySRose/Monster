package org.monster.micro.control.airforce;

import java.util.Set;

import org.monster.common.UnitInfo;
import org.monster.micro.Control;
import org.monster.micro.manager.OverlordManager;

import bwapi.Unit;

public class OverlordControl extends Control {
	
	private OverlordManager overlordManager = OverlordManager.getInstance();

	@Override
	protected void execute(Unit unit, Set<UnitInfo> enemies) {
	}

}
