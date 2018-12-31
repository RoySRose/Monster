package org.monster.micro;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.monster.common.util.UnitUtils;
import org.monster.micro.AssignPolicy.AssignLimitPolicy;

import bwapi.Unit;
import bwapi.UnitType;

/**
 * tactics는 유닛 전체 컨트롤을 구성하는 세트이다.
 * (tactics는 squad, squad generator, tactics manager로 구성된다.)
 * 
 * 구성을 다르게 만들어 상황별 컨트롤을 구현하거나,
 * 개별 개발자가 다른 tactics 구성으로 새로운 시도를 자유롭게 해볼 수 있다.
 * 
 * @author jw
 *
 */
public class Tactics {

	private List<Squad> squads;
	private List<SquadGenerator> squadGenerators;
	private List<TacticsManager> tacticsManagers;
	
	public Tactics(List<Squad> squads, List<SquadGenerator> squadGenerators, List<TacticsManager> tacticsManagers) {
		this.squads = squads;
		this.squadGenerators = squadGenerators;
		this.tacticsManagers = tacticsManagers;
	}

	/**
	 * tactics manager 실행
	 * squad generator 실행하여 새로운 squad 생성 및 삭제
	 * 유닛들의 squad 배치
	 * squad 실행
	 */
	public void updateTactics() {
		this.tacticsManagers.forEach(TacticsManager::update);
		this.squadGenerators.forEach(SquadGenerator::generate);
		
		// 스쿼드 유닛을 배치 / 재배치
		// * squad list에 priority가 높은 순서로 들어가 있어야 한다.
		Set<Integer> otherSquadUnitIds = new HashSet<>();
		this.squadGenerators.forEach(squadGenerator -> squadGenerator.squads.forEach(squad -> updateSquad(otherSquadUnitIds, squad)));
		this.squads.forEach(squad -> updateSquad(otherSquadUnitIds, squad));
		
		// 스쿼드 실행 (실제 유닛에 대한 명령)
		this.squadGenerators.forEach(squadGenerator -> squadGenerator.squads.forEach(Squad::execute));
		this.squads.forEach(Squad::execute);
	}

	/**
	 * 유닛들을 squad로 배치한다.
	 * 
	 * @param otherSquadUnitIds 이미 배치된 유닛 ID (유닛은 여러 squad에 중복 배치 시킬 수 없다.)
	 * @param squad 유닛을 배치시킬 스쿼드
	 */
	private void updateSquad(Set<Integer> otherSquadUnitIds, Squad squad) {
		AssignPolicy assignPolicy = squad.getAssignPolicy(); // 유닛 배치정책
		AssignLimitPolicy assignLimitPolicy = assignPolicy.getAssignLimitPolicy(); // 유닛 배치 제한제책
		
		List<UnitType> assignUnitTypes = assignPolicy.getAssignUnitTypes();
		Set<Unit> squadOldUnits;
		Set<Integer> squadOldUnitIds;
		
		if (!assignPolicy.isRefreshBeforeAssign()) {
			// 스쿼드 refresh 하지 않는 경우, invalid 유닛, 변경된 유닛타입 유닛 제거
			squadOldUnits = squad.getUnits().stream()
					.filter(UnitUtils::isValidUnit)
					.filter(unit -> assignUnitTypes.contains(unit.getType()))
					.collect(Collectors.toSet());
			squadOldUnitIds = squadOldUnits.stream().map(Unit::getID).collect(Collectors.toSet());
			
		} else {
			squadOldUnits = Collections.emptySet();
			squadOldUnitIds = Collections.emptySet();
		}

		List<Unit> assignTypeUnits = UnitUtils.getCompletedUnitList(assignUnitTypes.toArray(new UnitType[] {})); // 배치될 수 있는 타입의 유닛들
		Set<Unit> squadNewUnits = assignTypeUnits.stream()
				.filter(unit -> !otherSquadUnitIds.contains(unit.getID()))
				.filter(unit -> !squadOldUnitIds.contains(unit.getID()))
				.collect(Collectors.toSet());

		Set<Unit> squadAssignableUnits = new HashSet<>();
		squadAssignableUnits.addAll(squadOldUnits);
		squadAssignableUnits.addAll(squadNewUnits);
		
		if (assignLimitPolicy != null) {
			Map<Integer, Integer> assignPointMap = new HashMap<>();
			squadAssignableUnits.stream().forEach(unit -> assignPointMap.put(unit.getID(), assignLimitPolicy.assignPoint(unit)));
			
			long maxUnitNumber = assignLimitPolicy.getMaxUnitNumber();
			long maxUnitNumberByRatio = (long) (assignTypeUnits.size() * assignLimitPolicy.getMaxUnitRatio());
			
			squadAssignableUnits = squadAssignableUnits.stream()
					.sorted((unit1, unit2) -> assignPointMap.get(unit2.getID()) - assignPointMap.get(unit1.getID()))
					.limit(Math.max(maxUnitNumber, maxUnitNumberByRatio))
					.collect(Collectors.toSet());
		}
		
		squad.setUnits(squadAssignableUnits);
		otherSquadUnitIds.addAll(squadAssignableUnits.stream().map(Unit::getID).collect(Collectors.toSet()));
	}
}
