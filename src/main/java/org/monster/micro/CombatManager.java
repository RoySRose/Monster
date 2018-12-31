package org.monster.micro;

import org.monster.bootstrap.GameManager;

/**
 * @author jw
 *
 */
public class CombatManager extends GameManager {

	private static CombatManager instance = new CombatManager();
    public static CombatManager Instance() {
        return instance;
    }
	
	private Tactics tactics;

	@Override
	public void update() {
		updateCurrentTactics();
		tactics.updateTactics();
	}

	private void updateCurrentTactics() {
		if (tactics == null) {
			tactics = TacticsComposition.DEFAULT_TACTICS;
		}
	}
}
