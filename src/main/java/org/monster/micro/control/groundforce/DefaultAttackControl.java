package org.monster.micro.control.groundforce;

import java.util.Set;

import org.monster.common.UnitInfo;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.ChokeUtils;
import org.monster.common.util.CommandUtils;
import org.monster.micro.Control;

import bwapi.Position;
import bwapi.Unit;
import bwta.BaseLocation;

public class DefaultAttackControl extends Control {

	@Override
	protected void execute(Unit unit, Set<UnitInfo> enemies) {
		Position attackPosition = null;
		BaseLocation enemyMainBase = BaseUtils.enemyMainBase();
		if (enemyMainBase != null) {
			attackPosition = enemyMainBase.getPosition();
		} else {
			attackPosition = ChokeUtils.myFirstChoke().getCenter();
		}
		
		CommandUtils.attackMove(unit, attackPosition);
	}

}
