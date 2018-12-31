package org.monster.micro.squad;

import org.monster.micro.AssignPolicy;
import org.monster.micro.ControlPolicy;
import org.monster.micro.Squad;

public class DefaultSquad extends Squad {

	public DefaultSquad(String squadName, AssignPolicy assignPolicy, ControlPolicy controlPolicy) {
		super(squadName, assignPolicy, controlPolicy);
	}

}
