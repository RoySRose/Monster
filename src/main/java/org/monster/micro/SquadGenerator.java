package org.monster.micro;

import java.util.ArrayList;
import java.util.List;

/**
 * 스쿼드 제네레이터
 * 
 * @author jw
 *
 */
public abstract class SquadGenerator {
	
	protected String squadName;
	protected AssignPolicy assignPolicy;
	protected ControlPolicy controlPolicy;

	protected List<Squad> squads = new ArrayList<>();
	
	protected SquadGenerator(String squadName, AssignPolicy assignPolicy, ControlPolicy controlPolicy) {
		this.squadName = squadName;
		this.assignPolicy = assignPolicy;
		this.controlPolicy = controlPolicy;
	}
	
	protected abstract void generate();
}
