package org.monster.micro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.monster.micro.squad.DefaultSquad;
import org.monster.micro.squadgenerator.DefenseSquadGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 택틱스 builder
 * 
 * @author jw
 *
 */
public class TacticsBuilder {
	
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private List<Squad> squads = new ArrayList<>();
	private List<SquadGenerator> squadGenerators = new ArrayList<>();

	public TacticsBuilder squad(Class<? extends Squad> squadType, String squadName, AssignPolicy assignPolicy, ControlPolicy controlPolicy) {
		Squad squad;	
		if (squadType == DefaultSquad.class) {
			squad = new DefaultSquad(squadName, assignPolicy, controlPolicy);
		} else {
			logger.error("squad setting error");
			squad = new DefaultSquad(squadName, assignPolicy, controlPolicy);
		}
		squads.add(squad);
		return this;
	}

	public TacticsBuilder squadGenerator(Class<? extends SquadGenerator> squadGeneratorType, String squadName, AssignPolicy assignPolicy, ControlPolicy controlPolicy) {
		SquadGenerator squadGenerator;
		if (squadGeneratorType == DefenseSquadGenerator.class) {
			squadGenerator = new DefenseSquadGenerator(squadName, assignPolicy, controlPolicy);
		} else {
			logger.error("squad generator setting error");
			squadGenerator = new DefenseSquadGenerator(squadName, assignPolicy, controlPolicy);
		}
		squadGenerators.add(squadGenerator);
		return this;
	}

	public Tactics getTactics() {
		// squad 정렬 (우선순위 asc)
		squads.sort((squad1, squad2) -> squad1.getAssignPolicy().getPriority() - squad2.getAssignPolicy().getPriority());
		
		// 필요한 tactics manager 조회
		Set<TacticsManager> tacticsManagers = new HashSet<>();
		for (Squad squad : squads) {
			for (Control control : squad.getControlPolicy().getControlMap().values()) {
				TacticsManager tacticsManager = TacticsManagerMapping.requiredManager(control);
				if (tacticsManager != null) {
					tacticsManagers.add(tacticsManager);
				}
			}
		}
		for (SquadGenerator squadGenerator : squadGenerators) {
			TacticsManager tacticsManager = TacticsManagerMapping.requiredManager(squadGenerator);
			if (tacticsManager != null) {
				tacticsManagers.add(tacticsManager);
			}
		}
		
		// tactics 생성
		return new Tactics(squads, squadGenerators, new ArrayList<>(tacticsManagers));
	}
}
