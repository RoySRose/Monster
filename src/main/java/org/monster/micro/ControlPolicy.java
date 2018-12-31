package org.monster.micro;

import java.util.HashMap;
import java.util.Map;

import org.monster.micro.control.airforce.OverlordControl;
import org.monster.micro.control.airforce.QueenControl;
import org.monster.micro.control.groundforce.DefaultAttackControl;

import bwapi.UnitType;

/**
 * 스쿼드의 유닛컨트롤 정책
 * 
 * @author jw
 *
 */
public class ControlPolicy {
	
	private Control defaultControl = new DefaultAttackControl();
	private Map<UnitType, Control> controlMap = new HashMap<>();
	
	public ControlPolicy() {
		this.controlMap.put(UnitType.Zerg_Overlord, new OverlordControl());
		this.controlMap.put(UnitType.Zerg_Queen, new QueenControl());
	}

	public ControlPolicy(Map<UnitType, Control> controlMap) {
		this.controlMap = controlMap;
	}
	
	public Map<UnitType, Control> getControlMap() {
		return controlMap;
	}

	public Control getControl(UnitType unitType) {
		return controlMap.getOrDefault(unitType, defaultControl);
	}
	
}
