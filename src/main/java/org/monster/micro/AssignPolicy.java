package org.monster.micro;

import java.util.List;
import java.util.function.Function;

import org.monster.micro.temporaryutil.TemporaryUtils;

import bwapi.Unit;
import bwapi.UnitType;

/**
 * 스쿼드의 유닛배치 정책
 * 
 * @author jw
 *
 */
public class AssignPolicy {
	
	public class AssignLimitPolicy {
		private long maxUnitNumber; // 0 ... 1000
		private double maxUnitRatio; // 0 ... 1.0d
		private Function<Unit, Integer> pointCalc; // 높은 점수의 유닛 우선으로 배치한다.

		public AssignLimitPolicy(int maxUnitNumber, double maxUnitRatio, Function<Unit, Integer> pointCalc) {
			this.maxUnitNumber = maxUnitNumber;
			this.maxUnitRatio = maxUnitRatio;
			this.pointCalc = pointCalc;
		}

		public long getMaxUnitNumber() {
			return maxUnitNumber;
		}

		public double getMaxUnitRatio() {
			return maxUnitRatio;
		}

		public Integer assignPoint(Unit unit) {
			return pointCalc.apply(unit);
		}
	}

	private int priority; // 낮을 수록 우선순위가 높다. 0 ... 10
	private List<UnitType> assignUnitTypes; // 배치받을 유닛타입
	private boolean refreshBeforeAssign; // 배치실행시 이전에 배치받은 유닛들을 refresh 할것인지 여부
	private AssignLimitPolicy assignLimitPolicy; // 배치 제한정책
	
	public AssignPolicy() {
		this(5, TemporaryUtils.getAllZergUnitTypes());
	}
	
	public AssignPolicy(int priority, List<UnitType> assignUnitTypes) {
		this.priority = priority;
		this.assignUnitTypes = assignUnitTypes;
		this.assignLimitPolicy = null;
		this.refreshBeforeAssign = false;
	}
	
	public AssignPolicy(int priority, List<UnitType> assignUnitTypes, boolean refreshBeforeAssign, AssignLimitPolicy assignLimitPolicy) {
		this.priority = priority;
		this.assignUnitTypes = assignUnitTypes;
		this.refreshBeforeAssign = refreshBeforeAssign;
		this.assignLimitPolicy = assignLimitPolicy;
	}

	public int getPriority() {
		return priority;
	}

	public List<UnitType> getAssignUnitTypes() {
		return assignUnitTypes;
	}

	public boolean isRefreshBeforeAssign() {
		return refreshBeforeAssign;
	}

	public AssignLimitPolicy getAssignLimitPolicy() {
		return assignLimitPolicy;
	}
}
