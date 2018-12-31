package org.monster.micro;

import java.util.HashSet;
import java.util.Set;

import org.monster.common.UnitInfo;

import bwapi.Unit;

/**
 * 스쿼드
 * 
 * @author jw
 *
 */
public abstract class Squad {
	
	private String squadName;
	private AssignPolicy assignPolicy;
	private ControlPolicy controlPolicy;
	
	private Set<Unit> units = new HashSet<>();
	private Set<UnitInfo> enemies = new HashSet<>();
	
	protected Squad(String squadName, AssignPolicy assignPolicy, ControlPolicy controlPolicy) {
		this.squadName = squadName;
		this.assignPolicy = assignPolicy;
		this.controlPolicy = controlPolicy;
	}

	protected void execute() {
		units.stream().forEach(unit -> {
			controlPolicy.getControl(unit.getType()).execute(unit, enemies);
		});
	}

	public String getSquadName() {
		return squadName;
	}

	public AssignPolicy getAssignPolicy() {
		return assignPolicy;
	}

	public ControlPolicy getControlPolicy() {
		return controlPolicy;
	}

	public Set<Unit> getUnits() {
		return units;
	}

	public void setUnits(Set<Unit> units) {
		this.units = units;
	}
}
