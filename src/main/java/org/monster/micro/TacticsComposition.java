package org.monster.micro;

import java.util.Arrays;

import org.monster.micro.control.airforce.MutaliskControl;
import org.monster.micro.control.airforce.OverlordControl;
import org.monster.micro.squad.DefaultSquad;
import org.monster.micro.squadgenerator.DefenseSquadGenerator;
import org.monster.micro.temporaryutil.HashMapBuilder;
import org.monster.micro.temporaryutil.TemporaryUtils;

import bwapi.UnitType;

/**
 * tactics 구성
 * 
 * @author jw
 *
 */
public class TacticsComposition {
	
	public static final Tactics DEFAULT_TACTICS = new TacticsBuilder()
			.squad(DefaultSquad.class, "main"
					, new AssignPolicy(5, TemporaryUtils.getAllZergUnitTypes())
					, new ControlPolicy())
			.squad(DefaultSquad.class, "overlord"
					, new AssignPolicy(4, Arrays.asList(UnitType.Zerg_Overlord))
					, new ControlPolicy(new HashMapBuilder<UnitType, Control>()
							.put(UnitType.Zerg_Overlord, new OverlordControl()).build()))
			.squad(DefaultSquad.class, "spire-unit"
					, new AssignPolicy(4, Arrays.asList(UnitType.Zerg_Mutalisk))
					, new ControlPolicy(new HashMapBuilder<UnitType, Control>()
							.put(UnitType.Zerg_Mutalisk, new MutaliskControl()).build()))
			.squadGenerator(DefenseSquadGenerator.class, "defense"
					, new AssignPolicy(3, TemporaryUtils.getAllZergUnitTypes())
					, new ControlPolicy())
			.getTactics();
	
}
