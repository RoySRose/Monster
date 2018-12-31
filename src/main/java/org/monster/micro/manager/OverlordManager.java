package org.monster.micro.manager;

import org.monster.micro.TacticsManager;

public class OverlordManager extends TacticsManager {
	
	private static OverlordManager instance = new OverlordManager();

	public static OverlordManager getInstance() {
		return instance;
	}

	@Override
	protected void update() {
		// TODO Auto-generated method stub
		
	}

}
