package org.monster.decisions.strategy.action;

public abstract class RaceAction {
//	extends Action {
//	
//	protected enum LastCheckType {
//		BASE, FIRST_EXPANSION, GAS
//	}
//
//	@Override
//	public boolean exitCondition() {
//		// 전략이 파악되었거나, 일정시간이 지날 때까지 전략을 알지 못하였다.
//		if (enemyStrategy != EnemyStrategy.UNKNOWN || TimeUtils.after(phasEndSec)) {
//			if (enemyStrategy == EnemyStrategy.UNKNOWN) {
//				setEnemyStrategy(enemyStrategyExpect, RaceActionManager.Instance().clues.toString());
//			}
//			StrategyBoard.strategyHistory.add(phaseIndex, enemyStrategy);
//			return true;
//			
//		} else {
//			return false;
//		}
//		
//	}
//
//	@Override
//	public void action() {
//		analyse();
//		StrategyBoard.enemyStrategy = enemyStrategyExpect;
//	}
//
//	protected abstract void analyse();
//	
//	protected int lastCheckFrame(LastCheckType lastCheckType) {
//		if (lastCheckType == LastCheckType.BASE) {
//			return RaceActionManager.Instance().lastCheckFrameBase;
//		} else if (lastCheckType == LastCheckType.FIRST_EXPANSION) {
//			return RaceActionManager.Instance().lastCheckFrameFirstExpansion;
//		} else if (lastCheckType == LastCheckType.GAS) {
//			return RaceActionManager.Instance().lastCheckFrameGas;
//		} else {
//			return CommonCode.NONE;
//		}
//	}
//	
//	protected boolean unknownEnemyStrategy() {
//		return enemyStrategy == EnemyStrategy.UNKNOWN;
//	}
//	
//	protected class FoundInfo {
//		public UnitType unitType;
//		public List<UnitInfo> euiList;
//
//		public FoundInfo(UnitType unitType, List<UnitInfo> euiList, int buildStartFrame) {
//			this.unitType = unitType;
//			this.euiList = euiList;
//		}
//	}
//
//	private EnemyStrategy enemyStrategyExpect = EnemyStrategy.UNKNOWN; // TODO diplay
//	private EnemyStrategy enemyStrategy = EnemyStrategy.UNKNOWN; // TODO diplay
//	
//	private Race race;
//	private int phasEndSec;
//	protected int phaseIndex;
//	
//	public Race getRace() {
//		return race;
//	}
//
//	public RaceAction(Race race, int phaseIndex, int phasEndSec) {
//		super();
//		this.race = race;
//		this.phaseIndex = phaseIndex;
//		this.phasEndSec = phasEndSec;
//	}
//	
//	protected boolean containsAll(Object... clues) {
//		for (Object clue : clues) {
//			if (!RaceActionManager.Instance().clues.contains(clue)) {
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	protected boolean containsAny(Object... clues) {
//		for (Object clue : clues) {
//			if (RaceActionManager.Instance().clues.contains(clue)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	protected void addClue(Object clue) {
//		RaceActionManager.Instance().clues.add(clue);
//	}
//	
//	protected void removeClue(Object clue) {
//		RaceActionManager.Instance().clues.remove(clue);
//	}
//	
//	protected int euiCountBefore(List<UnitInfo> euiList, int beforeFrame) {
//		return euiCountBeforeWhere(euiList, beforeFrame);
//	}
//	
//	protected FoundInfo getFoundInfo(UnitType unitType, RegionType... positionRegion) {
//		List<UnitInfo> euiList = RaceActionManager.Instance().foundEuiMap.get(unitType);
//		if (euiList == null || euiList.isEmpty()) {
//			return new FoundInfo(unitType, Collections.<UnitInfo>emptyList(), CommonCode.UNKNOWN);
//		} else {
//			int buildStartFrame = CommonCode.UNKNOWN;
//			if (unitType.isBuilding()) {
//				buildStartFrame = RaceActionManager.Instance().buildStartFrameMap.get(euiList.get(0).getUnitID());
//			}
//			if (positionRegion == null) {
//				return new FoundInfo(unitType, euiList, buildStartFrame);
//			} else {
//				List<UnitInfo> filtered = new ArrayList<>();
//				for (UnitInfo eui : euiList) {
//					CommonCode.RegionType enemyRegionType = PositionUtils.positionToRegionType(eui.getLastPosition());
//					for (CommonCode.RegionType regionType : positionRegion) {
//						if (regionType == enemyRegionType) {
//							filtered.add(eui); 
//						}
//					}
//				}
//				return new FoundInfo(unitType, filtered, buildStartFrame);
//			}
//		}
//	}
//	
//	protected FoundInfo getFoundInfo(UnitType unitType) {
//		List<UnitInfo> euiList = RaceActionManager.Instance().foundEuiMap.get(unitType);
//		if (euiList == null || euiList.isEmpty()) {
//			return new FoundInfo(unitType, Collections.<UnitInfo>emptyList(), CommonCode.UNKNOWN);
//		} else {
//			int buildStartFrame = CommonCode.UNKNOWN;
//			if (unitType.isBuilding()) {
//				buildStartFrame = RaceActionManager.Instance().buildStartFrameMap.get(euiList.get(0).getUnitID());
//			}
//			return new FoundInfo(unitType, euiList, buildStartFrame);
//		}
//	}
//	
//	protected int buildStart(FoundInfo foundInfo) {
//		return buildStart(foundInfo.euiList.get(0));
//	}
//	
//	protected int buildStart(UnitInfo eui) {
//		return RaceActionManager.Instance().buildStartFrameMap.get(eui.getUnitID());
//	}
//	
//	// 발견 당시에 막 건설완료됐다고 가정 (최대한 늦게 건설되었다고 가정)
//	protected int defaultBuildStartCompelteJustBefore(FoundInfo foundInfo) {
//		if (foundInfo.euiList.isEmpty()) {
//			return CommonCode.UNKNOWN;
//		} else {
//			return defaultBuildStartCompelteJustBefore(foundInfo.euiList.get(0));
//		}
//	}
//	
//	protected int defaultBuildStartCompelteJustBefore(UnitInfo eui) {
//		Integer buildStartFrame = RaceActionManager.Instance().buildStartFrameMap.get(eui.getUnitID());
//		if (buildStartFrame != null && buildStartFrame != CommonCode.UNKNOWN) {
//			return buildStartFrame;
//		} else {
//			return eui.getUpdateFrame() - eui.getType().buildTime();
//		}
//	}
//	
//	// 마지막 정찰시간에 건설시작됐다고 가정 (최대한 빠르게 건설되었다고 가정)
//	protected int defaultBuildStartLastCheck(FoundInfo foundInfo, LastCheckType lastCheckType) {
//		if (foundInfo.euiList.isEmpty()) {
//			return CommonCode.UNKNOWN;
//		} else {
//			return defaultBuildStartLastCheck(foundInfo.euiList.get(0), lastCheckType);
//		}
//	}
//	
//	protected int defaultBuildStartLastCheck(UnitInfo eui, LastCheckType lastCheckType) {
//		Integer buildStartFrame = RaceActionManager.Instance().buildStartFrameMap.get(eui.getUnitID());
//		if (buildStartFrame != null && buildStartFrame != CommonCode.UNKNOWN) {
//			return buildStartFrame;
//		} else {
//			return lastCheckFrame(lastCheckType) - eui.getType().buildTime(); 
//		}
//	}
//	
//	protected int euiCountBeforeWhere(List<UnitInfo> euiList, int beforeFrame, RegionType... regionTypes) {
//		int count = 0;
//		for (UnitInfo eui : euiList) {
//			if (eui.getUpdateFrame() > beforeFrame) {
//				continue;
//			}
//			
//			if (regionTypes == null) {
//				count++;
//			} else {
//				CommonCode.RegionType euiRegionType = PositionUtils.positionToRegionType(eui.getLastPosition());
//				for (CommonCode.RegionType regionType : regionTypes) {
//					if (euiRegionType == regionType) {
//						count++;
//						break;
//					}
//				}
//			}
//		}
//		return count;
//	}
//	
//	protected int baseToBaseFrame(UnitType unitType) {
//		if (BaseUtils.enemyFirstExpansion() == null) {
//			return 0;
//		}
//		
//		// 대략적인 firstExpansion <-> myExpansion 사이에 unitType이 이동하는데 걸리는 시간 리턴 (단위 frame)
//		
//		return 0;
//	}
//	
//	protected void setEnemyStrategyExpect(EnemyStrategy enemyStrategyExpect) {
//		if (this.enemyStrategyExpect != enemyStrategyExpect) {
//			this.enemyStrategyExpect = enemyStrategyExpect;
//			displayEnemyStrategy(enemyStrategyExpect, UxColor.CHAR_YELLOW, RaceActionManager.Instance().clues.toString());
//		}
//	}
//	
//	protected void setEnemyStrategy(EnemyStrategy enemyStrategy, String... messages) {
//		this.enemyStrategy = this.enemyStrategyExpect = enemyStrategy;
//		displayEnemyStrategy(enemyStrategy, UxColor.CHAR_WHITE, messages);
//	}
//
//	private void displayEnemyStrategy(EnemyStrategy enemyStrategy, char color, String... messages) {
//		Prebot.Broodwar.printf(color + "enemy stratey : " + enemyStrategy.name());
//		for (String message : messages) {
//			Prebot.Broodwar.printf(color + " - " + message);	
//		}
//	}
}
