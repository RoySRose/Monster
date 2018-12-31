package org.monster.micro;

import java.util.HashMap;
import java.util.Map;

import org.monster.micro.control.airforce.OverlordControl;
import org.monster.micro.manager.OverlordManager;

/**
 * TacticsManagerMapping
 * 
 * @author jw
 *
 */
public class TacticsManagerMapping {
	
	private static Map<Class<? extends Control>, TacticsManager> controlMap = new HashMap<>();
	private static Map<Class<? extends SquadGenerator>, TacticsManager> squadGeneratorMap = new HashMap<>();
	
	static {
		controlMap.put(OverlordControl.class, OverlordManager.getInstance());
	}
	
	public static TacticsManager requiredManager(Control control) {
		return controlMap.get(control.getClass());
	}

	public static TacticsManager requiredManager(SquadGenerator squadGenerator) {
		return squadGeneratorMap.get(squadGenerator.getClass());
	}
}
