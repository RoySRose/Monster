package org.monster.debugger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.monster.board.StrategyBoard;
import org.monster.bootstrap.GameManager;
import org.monster.build.base.BuildManager;
import org.monster.build.base.BuildOrderItem;
import org.monster.build.base.ConstructionManager;
import org.monster.build.base.ConstructionPlaceFinder;
import org.monster.build.base.ConstructionTask;
import org.monster.build.provider.BuildQueueProvider;
import org.monster.common.LagObserver;
import org.monster.common.MapGrid;
import org.monster.common.MetaType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.constant.EnemyUnitVisibleStatus;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.ChokeUtils;
import org.monster.common.util.DrawingUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitTypeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.debugger.chat.impl.UxDrawConfig;
import org.monster.micro.CombatManager;
import org.monster.micro.MicroDecision;
import org.monster.micro.squad.Squad;
import org.monster.micro.squad.WatcherSquad;
import org.monster.strategy.StrategyManager;
import org.monster.strategy.constant.EnemyStrategy;
import org.monster.strategy.constant.StrategyCode;
import org.monster.strategy.manage.AirForceManager;
import org.monster.strategy.manage.AirForceTeam;
import org.monster.strategy.manage.ClueManager;
import org.monster.strategy.manage.EnemyBuildTimer;
import org.monster.strategy.manage.EnemyStrategyAnalyzer;
import org.monster.worker.Minerals;
import org.monster.worker.WorkerData;
import org.monster.worker.WorkerManager;

import bwapi.Bullet;
import bwapi.BulletType;
import bwapi.Color;
import bwapi.Force;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;
import bwta.Polygon;
import bwta.Region;

/// 봇 프로그램 개발의 편의성 향상을 위해 게임 화면에 추가 정보들을 표시하는 class<br>
/// 여러 Manager 들로부터 정보를 조회하여 Screen 혹은 Map 에 정보를 표시합니다
public class PreBotUXManager {

	private static PreBotUXManager instance = new PreBotUXManager();

	private final int dotRadius = 2;
	public Unit leader = null;
	public int uxOption = 0;
	private boolean hasSavedBWTAInfo = false;
	private int[][] blue = null;
	private int[][] cyan = null;
	private int[][] orange = null;
	private int[][] purple = null;
	private List<Position> yellow = new ArrayList<Position>();
	private List<Position> green1 = new ArrayList<Position>();
	private List<Position> green2 = new ArrayList<Position>();
	private List<Position> red1 = new ArrayList<Position>();
	private List<Position> red2 = new ArrayList<Position>();
	private String bulletTypeName = "";
	private String tempUnitName = "";
	protected Game Broodwar;
	private int L = UxDrawConfig.posMap.get("L");
	private int M = UxDrawConfig.posMap.get("M");
	private int R = UxDrawConfig.posMap.get("R");
	private UxDrawConfig uxDrawConfig;

	public ArrayList<UxDrawConfig>[] drawStrategyListOrigin = new ArrayList[9];
	public HashMap<Integer, ArrayList<UxDrawConfig>> drawStrategyListMap = new HashMap<Integer, ArrayList<UxDrawConfig>>();
	public ArrayList<UxDrawConfig> drawStrategyLeftList = new ArrayList<UxDrawConfig>();
	public ArrayList<UxDrawConfig> drawStrategyMidList = new ArrayList<UxDrawConfig>();
	public ArrayList<UxDrawConfig> drawStrategyRightList = new ArrayList<UxDrawConfig>();

	private UnitType factorySelected = UnitType.None;

	private Map<Integer, MicroDecision> decisionListForUx = new HashMap<>();

	public PreBotUXManager() {
	}

	public void onStart(Game Broodwar) {
		for (int i = 0; i < drawStrategyListOrigin.length; i++) {
			drawStrategyListOrigin[i] = new ArrayList<UxDrawConfig>();
		}
		this.Broodwar = Broodwar;
	}

	// 0..1 : default
	// 2..3 : custom

	/// static singleton 객체를 리턴합니다
	public static PreBotUXManager Instance() {
		return instance;
	}

	public void setUxOption(int uxOption) {
		this.uxOption = uxOption;
	}

	public int getUxOption() {
		return uxOption;
	}

	public void update() {
		ScreenUx.showDisplay();
	}

	protected void drawUxInfo() {
		// TODO Auto-generated method stub
		int y = 10;
		// setting
		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "",
				"/*****************************************************************************", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", "*", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "",
				"* 1) UX Type Change		: d + num			ex) d1=?, d0=prebot1 display", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "",
				"* 2) Change Strategy	: $ + Strategy Name	ex) $ INIT", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "",
				"* 3) add Var 			: a + pos + class + var 	ex) a L(R/M) strategyBoard startStrategy",
				UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "",
				"* 4) minus Var 			: m + var  			ex) m startStrategy", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", "*", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "",
				"*****************************************************************************///", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);
	}

	public void clearList() {
		// TODO Auto-generated method stub
		drawStrategyLeftList.clear();
		drawStrategyMidList.clear();
		drawStrategyRightList.clear();
	}

	private void drawDecision() {
		for (Integer unitId : decisionListForUx.keySet()) {
			Unit unit = Broodwar.getUnit(unitId);
			MicroDecision decision = decisionListForUx.get(unitId);
			Broodwar.drawTextMap(unit.getPosition(), UxColor.CHAR_YELLOW + decision.toString());
			if (decision.eui != null) {
				Broodwar.drawLineMap(unit.getPosition(), decision.eui.getLastPosition(), Color.Yellow);
			}
		}
	}

	public void addDecisionListForUx(Unit unit, MicroDecision decision) {
		decisionListForUx.put(unit.getID(), decision);
	}

	public void clearDecisionListForUx() {
		decisionListForUx.clear();
	}

	protected void drawTimer() {
		char battleColor = UxColor.CHAR_WHITE;
		if (StrategyBoard.initiated) {
			battleColor = UxColor.CHAR_RED;
		}
		uxDrawConfig = UxDrawConfig.newInstanceStringType("M",
				"", StrategyBoard.mainSquadMode.toString() + ": "
						+ DrawingUtils.framesToTimeString(TimeUtils.getFrame()) + "(" + TimeUtils.getFrame() + ")",
				battleColor);
		drawStrategyMidList.add(uxDrawConfig);

		char apmColor = UxColor.CHAR_WHITE;
		int apm = Broodwar.getAPM();
		if (apm > 3000) {
			apmColor = UxColor.CHAR_RED;
		} else if (apm > 2000) {
			apmColor = UxColor.CHAR_YELLOW;
		} else if (apm > 1000) {
			apmColor = UxColor.CHAR_GREEN;
		} else {
			apmColor = UxColor.CHAR_WHITE;
		}
		uxDrawConfig = UxDrawConfig.newInstanceStringType("R", "APM : ", Broodwar.getAPM(), apmColor);
		drawStrategyRightList.add(uxDrawConfig);
	}

	protected void drawEnemyBuildTimer() {

		Map<UnitType, Integer> buildTimeExpectMap = EnemyBuildTimer.Instance().buildTimeExpectMap;
		Map<UnitType, Integer> buildTimeMinimumMap = EnemyBuildTimer.Instance().buildTimeMinimumMap;
		Set<UnitType> buildTimeCertain = EnemyBuildTimer.Instance().buildTimeCertain;

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "engine Build Frame",
				DrawingUtils.framesToTimeString(StrategyBoard.engineeringBayBuildStartFrame), UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "turret Build Frame",
				DrawingUtils.framesToTimeString(StrategyBoard.turretBuildStartFrame), UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "turret Need Frame",
				DrawingUtils.framesToTimeString(StrategyBoard.turretNeedFrame), UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "darkTemplarInMyBaseFrame",
				DrawingUtils.framesToTimeString(EnemyBuildTimer.Instance().darkTemplarInMyBaseFrame),
				UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "reaverInMyBaseFrame",
				DrawingUtils.framesToTimeString(EnemyBuildTimer.Instance().reaverInMyBaseFrame), UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "mutaliskInMyBaseFrame",
				DrawingUtils.framesToTimeString(EnemyBuildTimer.Instance().mutaliskInMyBaseFrame), UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "lurkerInMyBaseFrame",
				DrawingUtils.framesToTimeString(EnemyBuildTimer.Instance().lurkerInMyBaseFrame), UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		for (UnitType unitType : buildTimeExpectMap.keySet()) {
			Integer buildTimeExpect = buildTimeExpectMap.get(unitType);
			if (buildTimeExpect != null && buildTimeExpect != CommonCode.UNKNOWN) {
				String expect = DrawingUtils.framesToTimeString(buildTimeExpect);
				String minimum = "";
				Integer buildMinimum = buildTimeMinimumMap.get(unitType);
				if (buildMinimum != null && buildMinimum != CommonCode.UNKNOWN) {
					minimum = DrawingUtils.framesToTimeString(buildMinimum);
				}
				uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", unitType + " : " + expect + " - min: "
						+ minimum + " (" + buildTimeCertain.contains(unitType) + ")", UxColor.CHAR_WHITE);
				drawStrategyLeftList.add(uxDrawConfig);
			}
		}

	}

	protected void drawDebugginUxMenu() {
		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", "1. Default Information", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", "2. Strategy Information", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", "3. Position Finder Test", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", "4. Air Micro Test", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", "5. Unit Bast To Base", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", "", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);
		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", "", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);
	}

	// 게임 개요 정보를 Screen 에 표시합니다
	public void drawGameInformationOnScreen() {
		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "Current Strategy", StrategyBoard.currentStrategy.name(),
				UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		String history = "";
		for (int i = StrategyBoard.strategyHistory.size() - 1; i >= 0; i--) {
			if (i == StrategyBoard.strategyHistory.size() - 3) {
				history = "... " + history;
				break;
			} else {
				history = StrategyBoard.strategyHistory.get(i).name() + " -> " + history;
			}
		}

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "MYKillScore", Broodwar.self().getKillScore(),
				UxColor.CHAR_RED);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "MYRazingScore", Broodwar.self().getRazingScore(),
				UxColor.CHAR_RED);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "EnemyKillScore", Broodwar.enemy().getKillScore(),
				UxColor.CHAR_PURPLE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "EnemyRazingScore", Broodwar.enemy().getRazingScore(),
				UxColor.CHAR_PURPLE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "Reserved Resource",
				ConstructionManager.Instance().getReservedMinerals() + " / "
						+ ConstructionManager.Instance().getReservedGas(),
				UxColor.CHAR_TEAL);
		drawStrategyLeftList.add(uxDrawConfig);

	}

	/// Players 정보를 Screen 에 표시합니다
	public void drawPlayers() {
		for (Player p : Broodwar.getPlayers()) {
			Broodwar.sendText("Player [" + p.getID() + "]: " + p.getName() + " is in force: " + p.getForce().getName());
		}
	}

	/// Player 들의 팀 (Force) 들의 정보를 Screen 에 표시합니다
	public void drawForces() {
		for (Force f : Broodwar.getForces()) {
			Broodwar.sendText("Force " + f.getName() + " has the following players:");
			for (Player p : f.getPlayers()) {
				Broodwar.sendText("  - Player [" + p.getID() + "]: " + p.getName());
			}
		}
	}

	/// Unit 의 HitPoint 등 추가 정보를 Map 에 표시합니다
	public void drawUnitExtendedInformationOnMap() {
		int verticalOffset = -10;

		for (UnitInfo ui : UnitUtils.getEnemyUnitInfoList()) {

			UnitType type = ui.getType();
			int hitPoints = ui.getLastHealth();
			int shields = ui.getLastShields();

			Position pos = ui.getLastPosition();

			int left = pos.getX() - type.dimensionLeft();
			int right = pos.getX() + type.dimensionRight();
			int top = pos.getY() - type.dimensionUp();
			int bottom = pos.getY() + type.dimensionDown();

			// 적 유닛이면 주위에 박스 표시
			if (!Broodwar.isVisible(ui.getLastPosition().toTilePosition())) {
				Broodwar.drawBoxMap(new Position(left, top), new Position(right, bottom), Color.Grey, false);
				Broodwar.drawTextMap(new Position(left + 3, top + 4), ui.getType().toString());
			}

			// 유닛의 HitPoint 남아있는 비율 표시
			if (!type.isResourceContainer() && type.maxHitPoints() > 0) {
				double hpRatio = (double) hitPoints / (double) type.maxHitPoints();

				Color hpColor = Color.Green;
				if (hpRatio < 0.66)
					hpColor = Color.Orange;
				if (hpRatio < 0.33)
					hpColor = Color.Red;

				int ratioRight = left + (int) ((right - left) * hpRatio);
				int hpTop = top + verticalOffset;
				int hpBottom = top + 4 + verticalOffset;

				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Grey, true);
				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(ratioRight, hpBottom), hpColor, true);
				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Black, false);

				int ticWidth = 3;

				for (int i = left; i < right - 1; i += ticWidth) {
					Broodwar.drawLineMap(new Position(i, hpTop), new Position(i, hpBottom), Color.Black);
				}
			}

			// 유닛의 Shield 남아있는 비율 표시
			if (!type.isResourceContainer() && type.maxShields() > 0) {
				double shieldRatio = (double) shields / (double) type.maxShields();

				int ratioRight = left + (int) ((right - left) * shieldRatio);
				int hpTop = top - 3 + verticalOffset;
				int hpBottom = top + 1 + verticalOffset;

				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Grey, true);
				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(ratioRight, hpBottom), Color.Blue, true);
				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Black, false);

				int ticWidth = 3;

				for (int i = left; i < right - 1; i += ticWidth) {
					Broodwar.drawLineMap(new Position(i, hpTop), new Position(i, hpBottom), Color.Black);
				}
			}
		}

		// draw neutral units and our units
		for (Unit unit : Broodwar.getAllUnits()) {
			if (unit.getPlayer() == PlayerUtils.enemyPlayer()) {
				continue;
			}

			final Position pos = unit.getPosition();

			int left = pos.getX() - unit.getType().dimensionLeft();
			int right = pos.getX() + unit.getType().dimensionRight();
			int top = pos.getY() - unit.getType().dimensionUp();
			int bottom = pos.getY() + unit.getType().dimensionDown();

			// Monster.game.drawBoxMap(BWAPI.Position(left, top),
			// BWAPI.Position(right, bottom), Color.Grey, false);

			// 유닛의 HitPoint 남아있는 비율 표시
			if (!unit.getType().isResourceContainer() && unit.getType().maxHitPoints() > 0) {
				double hpRatio = (double) unit.getHitPoints() / (double) unit.getType().maxHitPoints();

				Color hpColor = Color.Green;
				if (hpRatio < 0.66)
					hpColor = Color.Orange;
				if (hpRatio < 0.33)
					hpColor = Color.Red;

				int ratioRight = left + (int) ((right - left) * hpRatio);
				int hpTop = top + verticalOffset;
				int hpBottom = top + 4 + verticalOffset;

				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Grey, true);
				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(ratioRight, hpBottom), hpColor, true);
				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), hpColor.Black, false);

				int ticWidth = 3;

				for (int i = left; i < right - 1; i += ticWidth) {
					Broodwar.drawLineMap(new Position(i, hpTop), new Position(i, hpBottom), Color.Black);
				}
			}

			// 유닛의 Shield 남아있는 비율 표시
			if (!unit.getType().isResourceContainer() && unit.getType().maxShields() > 0) {
				double shieldRatio = (double) unit.getShields() / (double) unit.getType().maxShields();

				int ratioRight = left + (int) ((right - left) * shieldRatio);
				int hpTop = top - 3 + verticalOffset;
				int hpBottom = top + 1 + verticalOffset;

				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Grey, true);
				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(ratioRight, hpBottom), Color.Blue, true);
				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Black, false);

				int ticWidth = 3;

				for (int i = left; i < right - 1; i += ticWidth) {
					Broodwar.drawLineMap(new Position(i, hpTop), new Position(i, hpBottom), Color.Black);
				}
			}

			// Mineral / Gas 가 얼마나 남아있는가
			if (unit.getType().isResourceContainer() && unit.getInitialResources() > 0) {
				double mineralRatio = (double) unit.getResources() / (double) unit.getInitialResources();

				int ratioRight = left + (int) ((right - left) * mineralRatio);
				int hpTop = top + verticalOffset;
				int hpBottom = top + 4 + verticalOffset;

				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Grey, true);
				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(ratioRight, hpBottom), Color.Cyan, true);
				Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Black, false);

				int ticWidth = 3;

				for (int i = left; i < right - 1; i += ticWidth) {
					Broodwar.drawLineMap(new Position(i, hpTop), new Position(i, hpBottom), Color.Black);
				}
			}
		}
	}

	/// BWTA 라이브러리에 의한 Map 분석 결과 정보를 Map 에 표시합니다
	public void drawBWTAResultOnMap() {
		int blueCount = 0;
		int cyanCount = 0;
		int orangeCount = 0;
		// int purpleCount = 0;

		if (hasSavedBWTAInfo == false) {
			for (BaseLocation baseLocation : BWTA.getBaseLocations()) {
				blueCount++;
				// purpleCount++;
				for (Unit unit : baseLocation.getStaticMinerals()) {
					cyanCount++;
				}
				for (Unit unit : baseLocation.getGeysers()) {
					orangeCount++;
				}

			}

			blue = new int[blueCount][4];
			int blueIndex = 0;
			cyan = new int[cyanCount][2];
			int cyanIndex = 0;
			orange = new int[orangeCount][4];
			int orangeIndex = 0;

			// purple = new int[purpleCount][4];
			// int purpleIndex = 0;

			for (BaseLocation baseLocation : BWTA.getBaseLocations()) {
				TilePosition p = baseLocation.getTilePosition();
				Position c = baseLocation.getPosition();

				blue[blueIndex][0] = p.getX() * 32;
				blue[blueIndex][1] = p.getY() * 32;
				blue[blueIndex][2] = p.getX() * 32 + 4 * 32;
				blue[blueIndex][3] = p.getY() * 32 + 3 * 32;
				blueIndex++;

				// purple[purpleIndex][0] = (p.getX()+4) * 32;
				// purple[purpleIndex][1] = (p.getY()+1) * 32;
				// purple[purpleIndex][2] = (p.getX()+4) * 32 + 2 * 32;
				// purple[purpleIndex][3] = (p.getY()+1) * 32 + 2 * 32;
				// purpleIndex++;

				// draw a circle at each mineral patch
				// C++ : for (BWAPI.Unitset.iterator j =
				// (*i).getStaticMinerals().begin(); j !=
				// (*i).getStaticMinerals().end(); j++)
				for (Unit unit : baseLocation.getStaticMinerals()) {
					Position q = unit.getInitialPosition();
					cyan[cyanIndex][0] = q.getX();
					cyan[cyanIndex][1] = q.getY();
					cyanIndex++;
				}

				// draw the outlines of vespene geysers
				// C++ : for (BWAPI.Unitset.iterator j =
				// (*i).getGeysers().begin(); j != (*i).getGeysers().end(); j++)
				for (Unit unit : baseLocation.getGeysers()) {
					TilePosition q = unit.getInitialTilePosition();
					orange[orangeIndex][0] = q.getX() * 32;
					orange[orangeIndex][1] = q.getY() * 32;
					orange[orangeIndex][2] = q.getX() * 32 + 4 * 32;
					orange[orangeIndex][3] = q.getY() * 32 + 2 * 32;
					orangeIndex++;
				}

				// if this is an island expansion, draw a yellow circle around
				// the base location
				if (baseLocation.isIsland()) {
					yellow.add(c);
				}
			}

			// we will iterate through all the regions and draw the polygon
			// outline of it in green.
			// C++ : for (std.set<BWTA.Region*>.const_iterator r =
			// BWTA.getRegions().begin(); r != BWTA.getRegions().end(); r++)
			for (Region region : BWTA.getRegions()) {
				Polygon p = region.getPolygon();
				for (int j = 0; j < p.getPoints().size(); j++) {
					green1.add(p.getPoints().get(j));
					green2.add(p.getPoints().get((j + 1) % p.getPoints().size()));
				}
			}

			// we will visualize the chokepoints with red lines
			// C++ : for (std.set<BWTA.Region*>.const_iterator r =
			// BWTA.getRegions().begin(); r != BWTA.getRegions().end(); r++)
			for (Region region : BWTA.getRegions()) {
				// C++ : for (std.set<BWTA.Chokepoint*>.const_iterator c =
				// (*r).getChokepoints().begin(); c !=
				// (*r).getChokepoints().end(); c++)
				for (Chokepoint Chokepoint : region.getChokepoints()) {
					red1.add(Chokepoint.getSides().first);
					red2.add(Chokepoint.getSides().second);
				}
			}
			hasSavedBWTAInfo = true;

			// System.out.println(blueCount + " " + cyanCount + " " +
			// orangeCount + " " + yellowCount + " " + greenCount + " " +
			// redCount);
		}

		if (hasSavedBWTAInfo) {
			for (int i1 = 0; i1 < blue.length; i1++) {
				Broodwar.drawBoxMap(blue[i1][0], blue[i1][1], blue[i1][2], blue[i1][3], Color.Blue);
			}
			// for(int i1=0 ; i1<purple.length ; i1++)
			// {
			// Prebot.Broodwar.drawBoxMap(purple[i1][0], purple[i1][1],
			// purple[i1][2], purple[i1][3], Color.Purple);
			// }
			for (int i2 = 0; i2 < cyan.length; i2++) {
				Broodwar.drawCircleMap(cyan[i2][0], cyan[i2][1], 30, Color.Cyan);
			}
			for (int i3 = 0; i3 < orange.length; i3++) {
				Broodwar.drawBoxMap(orange[i3][0], orange[i3][1], orange[i3][2], orange[i3][3], Color.Orange);
			}
			for (int i4 = 0; i4 < yellow.size(); i4++) {
				Broodwar.drawCircleMap(yellow.get(i4), 80, Color.Yellow);
			}
			for (int i5 = 0; i5 < green1.size(); i5++) {
				Broodwar.drawLineMap(green1.get(i5), green2.get(i5), Color.Green);
			}
			for (int i6 = 0; i6 < red1.size(); i6++) {
				Broodwar.drawLineMap(red1.get(i6), red2.get(i6), Color.Red);
			}
		}

		// ChokePoint, BaseLocation 을 텍스트로 표시
		if (ChokeUtils.myFirstChoke() != null) {
			Broodwar.drawTextMap(BaseUtils.myMainBase().getPosition(), "My MainBaseLocation");
		}
		if (ChokeUtils.myFirstChoke() != null) {
			Broodwar.drawTextMap(ChokeUtils.myFirstChoke().getCenter(), "My First ChokePoint");
		}
		if (ChokeUtils.mySecondChoke() != null) {
			Broodwar.drawTextMap(ChokeUtils.mySecondChoke().getCenter(), "My Second ChokePoint");
		}
		if (BaseUtils.myFirstExpansion() != null) {
			Broodwar.drawTextMap(BaseUtils.myFirstExpansion().getPosition(), "My First ExpansionLocation");
		}

		// if
		// (UnitTypeUtils.enemyFirstChoke().getFirstChokePoint(PlayerUtils.enemyPlayer())
		// != null) {
		// Broodwar.drawTextMap(BaseUtils.enemyMainBase().getPosition(), "Enemy
		// MainBaseLocation");
		// }
		// if (UnitTypeUtils.getFirstChokePoint(PlayerUtils.enemyPlayer()) !=
		// null) {
		// Broodwar.drawTextMap(InformationManager.Instance().getFirstChokePoint(PlayerUtils.enemyPlayer()).getCenter(),
		// "Enemy First ChokePoint");
		// }
		if (ChokeUtils.enemySecondChoke() != null) {
			Broodwar.drawTextMap(ChokeUtils.enemySecondChoke().getCenter(), "Enemy Second ChokePoint");
		}
		if (BaseUtils.enemyFirstExpansion() != null) {
			Broodwar.drawTextMap(BaseUtils.enemyFirstExpansion().getPosition(), "Enemy First ExpansionLocation");
		}
	}

	/// Tile Position 그리드를 Map 에 표시합니다
	public void drawMapGrid() {
		int cellSize = MapGrid.Instance().getCellSize();
		int mapWidth = MapGrid.Instance().getMapWidth();
		int mapHeight = MapGrid.Instance().getMapHeight();
		int rows = MapGrid.Instance().getRows();
		int cols = MapGrid.Instance().getCols();

		for (int i = 0; i < cols; i++) {
			Broodwar.drawLineMap(i * cellSize, 0, i * cellSize, mapHeight, Color.Blue);
		}

		for (int j = 0; j < rows; j++) {
			Broodwar.drawLineMap(0, j * cellSize, mapWidth, j * cellSize, Color.Blue);
		}

		for (int r = 0; r < rows; r += 2) {
			for (int c = 0; c < cols; c += 2) {
				Broodwar.drawTextMap(c * 32, r * 32, c + "," + r);
			}
		}
	}

	/// BuildOrderQueue 를 Screen 에 표시합니다
	public void drawBuildOrderQueueOnScreen() {

		Deque<BuildOrderItem> buildQueue = BuildManager.Instance().buildQueue.getQueue();
		int itemCount = 0;

		Object[] tempQueue = buildQueue.toArray();

		for (int i = 0; i < tempQueue.length; i++) {
			BuildOrderItem currentItem = (BuildOrderItem) tempQueue[i];
			// Broodwar.drawTextScreen(x, y + 10 + (itemCount * 10),
			// currentItem.blocking + " " + UxColor.CHAR_WHITE +
			// currentItem.metaType.getName());
			UxDrawConfig uxDrawConfig;
			uxDrawConfig = UxDrawConfig.newInstanceStringType("M", "",
					currentItem.blocking + " " + currentItem.metaType.getName(), UxColor.CHAR_WHITE);
			drawStrategyMidList.add(uxDrawConfig);
			itemCount++;
			if (itemCount >= 24)
				break;
		}
	}

	/// Build 진행 상태를 Screen 에 표시합니다
	public void drawBuildStatusOnScreen() {
		// 건설 / 훈련 중인 유닛 진행상황 표시
		Vector<Unit> unitsUnderConstruction = new Vector<Unit>();
		for (Unit unit : Broodwar.self().getUnits()) {
			if (unit != null && unit.isBeingConstructed()) {
				unitsUnderConstruction.add(unit);
			}
		}

		// sort it based on the time it was started
		Object[] tempArr = unitsUnderConstruction.toArray();
		// Arrays.sort(tempArr);
		unitsUnderConstruction = new Vector<Unit>();
		for (int i = 0; i < tempArr.length; i++) {
			unitsUnderConstruction.add((Unit) tempArr[i]);
		}
		// C++ : std.sort(unitsUnderConstruction.begin(),
		// unitsUnderConstruction.end(), CompareWhenStarted());

		// Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + " <Build
		// Status>");

		uxDrawConfig = UxDrawConfig.newInstanceStringType("R", "", " <Build Status>", UxColor.CHAR_WHITE);
		drawStrategyRightList.add(uxDrawConfig);

		for (Unit unit : unitsUnderConstruction) {
			UnitType t = unit.getType();
			if (t == UnitType.Zerg_Egg) {
				t = unit.getBuildType();
			}
			uxDrawConfig = UxDrawConfig.newInstanceStringType("R", "",
					"" + t + " (" + unit.getRemainingBuildTime() + ")", UxColor.CHAR_WHITE);
			drawStrategyRightList.add(uxDrawConfig);
		}

		// Tech Research 표시

		// Upgrade 표시
	}

	/// Construction 을 하기 위해 예약해둔 Tile 들을 Map 에 표시합니다
	public void drawReservedBuildingTilesOnMap() {
		boolean[][] reserveMap = ConstructionPlaceFinder.Instance().getReserveMap();
		if (reserveMap.length > 0 && reserveMap[0] != null && reserveMap[0].length > 0) {
			int rwidth = reserveMap.length;
			int rheight = reserveMap[0].length;

			for (int x = 0; x < rwidth; ++x) {
				for (int y = 0; y < rheight; ++y) {
					if (reserveMap[x][y]) {
						int x1 = x * 32 + 8;
						int y1 = y * 32 + 8;
						int x2 = (x + 1) * 32 - 8;
						int y2 = (y + 1) * 32 - 8;

						Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Yellow, false);
					}
				}
			}
		}
	}

	/// Construction 을 하지 못하는 Tile 들을 Map 에 표시합니다
	public void drawTilesToAvoidOnMap() {
		int y = 0;
		int x = 0;

		for (y = 0; y < 128; y++) {
			for (x = 0; x < 128; x++) {
				if (ConstructionPlaceFinder.Instance().getTilesToAvoid(x, y)) {
					int x1 = x * 32 + 8;
					int y1 = y * 32 + 8;
					int x2 = (x + 1) * 32 - 8;
					int y2 = (y + 1) * 32 - 8;

					Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Orange, false);
				}
				if (ConstructionPlaceFinder.Instance().getTilesToAvoidAbsolute(x, y)) {
					int x1 = x * 32 + 8;
					int y1 = y * 32 + 8;
					int x2 = (x + 1) * 32 - 8;
					int y2 = (y + 1) * 32 - 8;

					Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Purple, false);
				}
				if (ConstructionPlaceFinder.Instance().getTilesToAvoidSupply(x, y)) {
					int x1 = x * 32 + 8;
					int y1 = y * 32 + 8;
					int x2 = (x + 1) * 32 - 8;
					int y2 = (y + 1) * 32 - 8;

					Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Red, false);
				}
			}
		}
	}

	/// ConstructionQueue 를 Screen 에 표시합니다
	public void drawConstructionQueueOnScreenAndMap() {
		uxDrawConfig = UxDrawConfig.newInstanceStringType("M", "", " <Construction Status>", UxColor.CHAR_WHITE);
		drawStrategyMidList.add(uxDrawConfig);

		Vector<ConstructionTask> constructionQueue = ConstructionManager.Instance().getConstructionQueue();

		for (final ConstructionTask b : constructionQueue) {
			if (b.getStatus() == ConstructionTask.ConstructionStatus.Unassigned.ordinal()) {
				uxDrawConfig = UxDrawConfig.newInstanceStringType("M", "", "" + b.getType() + " - No Worker",
						UxColor.CHAR_WHITE);
				drawStrategyMidList.add(uxDrawConfig);
			} else if (b.getStatus() == ConstructionTask.ConstructionStatus.Assigned.ordinal()) {
				if (b.getConstructionWorker() == null) {
					uxDrawConfig = UxDrawConfig.newInstanceStringType("M", "", b.getType() + " - Assigned Worker Null",
							UxColor.CHAR_WHITE);
					drawStrategyMidList.add(uxDrawConfig);
				} else {
					uxDrawConfig = UxDrawConfig.newInstanceStringType("M", "",
							b.getType() + " - Assigned Worker " + b.getConstructionWorker().getID() + ", Position ("
									+ b.getFinalPosition().getX() + "," + b.getFinalPosition().getY() + ")",
							UxColor.CHAR_WHITE);
					drawStrategyMidList.add(uxDrawConfig);
				}

				int x1 = b.getFinalPosition().getX() * 32;
				int y1 = b.getFinalPosition().getY() * 32;
				int x2 = (b.getFinalPosition().getX() + b.getType().tileWidth()) * 32;
				int y2 = (b.getFinalPosition().getY() + b.getType().tileHeight()) * 32;

				Broodwar.drawLineMap(b.getConstructionWorker().getPosition().getX(),
						b.getConstructionWorker().getPosition().getY(), (x1 + x2) / 2, (y1 + y2) / 2, Color.Orange);
				Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Red, false);
			} else if (b.getStatus() == ConstructionTask.ConstructionStatus.UnderConstruction.ordinal()) {
				uxDrawConfig = UxDrawConfig.newInstanceStringType("M", "", "" + b.getType() + " - Under Construction",
						UxColor.CHAR_WHITE);
				drawStrategyMidList.add(uxDrawConfig);
			}
		}
	}

	public void drawMineralIdOnMap() {
		for (Unit unit : Broodwar.getStaticMinerals()) {

			Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5,
					"" + UxColor.CHAR_WHITE + unit.getID());
		}
	}

	/// Unit 의 Id 를 Map 에 표시합니다
	public void drawUnitIdOnMap() {
		for (Unit unit : Broodwar.self().getUnits()) {
			if (unit.getType().isBuilding()) {
				Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5,
						"" + UxColor.CHAR_WHITE + unit.getID());
				Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 25, "" + UxColor.CHAR_WHITE
						+ unit.getTilePosition().getX() + " / " + unit.getTilePosition().getY());
			} else {
				Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5,
						"" + UxColor.CHAR_WHITE + unit.getID());
			}

		}
		for (Unit unit : Broodwar.enemy().getUnits()) {
			Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5,
					"" + UxColor.CHAR_WHITE + unit.getID());
		}
	}

	/// Worker Unit 의 자원채취 현황을 Map 에 표시합니다
	public void drawWorkerMiningStatusOnMap() {
		WorkerData workerData = WorkerManager.Instance().getWorkerData();

		for (Unit worker : workerData.getWorkers()) {
			if (worker == null)
				continue;

			Position pos = worker.getTargetPosition();

			Broodwar.drawTextMap(worker.getPosition().getX(), worker.getPosition().getY() - 5,
					"" + UxColor.CHAR_WHITE + workerData.getJobCode(worker));

			Broodwar.drawLineMap(worker.getPosition().getX(), worker.getPosition().getY(), pos.getX(), pos.getY(),
					Color.Cyan);

		}
	}

	/// Unit 의 Target 으로 잇는 선을 Map 에 표시합니다
	public void drawUnitTargetOnMap() {
		for (Unit unit : Broodwar.self().getUnits()) {
			if (unit != null && unit.isCompleted() && !unit.getType().isBuilding() && !unit.getType().isWorker()) {
				Unit targetUnit = unit.getTarget();
				if (targetUnit != null && targetUnit.getPlayer() != Broodwar.self()) {
					Broodwar.drawCircleMap(unit.getPosition(), dotRadius, Color.Red, true);
					Broodwar.drawCircleMap(targetUnit.getTargetPosition(), dotRadius, Color.Red, true);
					Broodwar.drawLineMap(unit.getPosition(), targetUnit.getTargetPosition(), Color.Red);
				} else if (unit.isMoving()) {
					Broodwar.drawCircleMap(unit.getPosition(), dotRadius, Color.Orange, true);
					Broodwar.drawCircleMap(unit.getTargetPosition(), dotRadius, Color.Orange, true);
					Broodwar.drawLineMap(unit.getPosition(), unit.getTargetPosition(), Color.Orange);
				}

			}
		}
	}

	/// Bullet 을 Map 에 표시합니다 <br>
	/// Cloaking Unit 의 Bullet 표시에 쓰입니다
	public void drawBulletsOnMap() {
		for (Bullet b : Broodwar.getBullets()) {
			Position p = b.getPosition();
			double velocityX = b.getVelocityX();
			double velocityY = b.getVelocityY();

			if (b.getType() == BulletType.Acid_Spore)
				bulletTypeName = "Acid_Spore";
			else if (b.getType() == BulletType.Anti_Matter_Missile)
				bulletTypeName = "Anti_Matter_Missile";
			else if (b.getType() == BulletType.Arclite_Shock_Cannon_Hit)
				bulletTypeName = "Arclite_Shock_Cannon_Hit";
			else if (b.getType() == BulletType.ATS_ATA_Laser_Battery)
				bulletTypeName = "ATS_ATA_Laser_Battery";
			else if (b.getType() == BulletType.Burst_Lasers)
				bulletTypeName = "Burst_Lasers";
			else if (b.getType() == BulletType.C_10_Canister_Rifle_Hit)
				bulletTypeName = "C_10_Canister_Rifle_Hit";
			else if (b.getType() == BulletType.Consume)
				bulletTypeName = "Consume";
			else if (b.getType() == BulletType.Corrosive_Acid_Shot)
				bulletTypeName = "Corrosive_Acid_Shot";
			else if (b.getType() == BulletType.Dual_Photon_Blasters_Hit)
				bulletTypeName = "Dual_Photon_Blasters_Hit";
			else if (b.getType() == BulletType.EMP_Missile)
				bulletTypeName = "EMP_Missile";
			else if (b.getType() == BulletType.Ensnare)
				bulletTypeName = "Ensnare";
			else if (b.getType() == BulletType.Fragmentation_Grenade)
				bulletTypeName = "Fragmentation_Grenade";
			else if (b.getType() == BulletType.Fusion_Cutter_Hit)
				bulletTypeName = "Fusion_Cutter_Hit";
			else if (b.getType() == BulletType.Gauss_Rifle_Hit)
				bulletTypeName = "Gauss_Rifle_Hit";
			else if (b.getType() == BulletType.Gemini_Missiles)
				bulletTypeName = "Gemini_Missiles";
			else if (b.getType() == BulletType.Glave_Wurm)
				bulletTypeName = "Glave_Wurm";
			else if (b.getType() == BulletType.Halo_Rockets)
				bulletTypeName = "Halo_Rockets";
			else if (b.getType() == BulletType.Invisible)
				bulletTypeName = "Invisible";
			else if (b.getType() == BulletType.Longbolt_Missile)
				bulletTypeName = "Longbolt_Missile";
			else if (b.getType() == BulletType.Melee)
				bulletTypeName = "Melee";
			else if (b.getType() == BulletType.Needle_Spine_Hit)
				bulletTypeName = "Needle_Spine_Hit";
			else if (b.getType() == BulletType.Neutron_Flare)
				bulletTypeName = "Neutron_Flare";
			else if (b.getType() == BulletType.None)
				bulletTypeName = "None";
			else if (b.getType() == BulletType.Optical_Flare_Grenade)
				bulletTypeName = "Optical_Flare_Grenade";
			else if (b.getType() == BulletType.Particle_Beam_Hit)
				bulletTypeName = "Particle_Beam_Hit";
			else if (b.getType() == BulletType.Phase_Disruptor)
				bulletTypeName = "Phase_Disruptor";
			else if (b.getType() == BulletType.Plague_Cloud)
				bulletTypeName = "Plague_Cloud";
			else if (b.getType() == BulletType.Psionic_Shockwave_Hit)
				bulletTypeName = "Psionic_Shockwave_Hit";
			else if (b.getType() == BulletType.Psionic_Storm)
				bulletTypeName = "Psionic_Storm";
			else if (b.getType() == BulletType.Pulse_Cannon)
				bulletTypeName = "Pulse_Cannon";
			else if (b.getType() == BulletType.Queen_Spell_Carrier)
				bulletTypeName = "Queen_Spell_Carrier";
			else if (b.getType() == BulletType.Seeker_Spores)
				bulletTypeName = "Seeker_Spores";
			else if (b.getType() == BulletType.STA_STS_Cannon_Overlay)
				bulletTypeName = "STA_STS_Cannon_Overlay";
			else if (b.getType() == BulletType.Subterranean_Spines)
				bulletTypeName = "Subterranean_Spines";
			else if (b.getType() == BulletType.Sunken_Colony_Tentacle)
				bulletTypeName = "Sunken_Colony_Tentacle";
			else if (b.getType() == BulletType.Unknown)
				bulletTypeName = "Unknown";
			else if (b.getType() == BulletType.Yamato_Gun)
				bulletTypeName = "Yamato_Gun";

			// 아군 것이면 녹색, 적군 것이면 빨간색
			Broodwar.drawLineMap(p, new Position(p.getX() + (int) velocityX, p.getY() + (int) velocityY),
					b.getPlayer() == Broodwar.self() ? Color.Green : Color.Red);
			if (b.getType() != null) {
				Broodwar.drawTextMap(p,
						(b.getPlayer() == Broodwar.self() ? "" + UxColor.CHAR_TEAL : "" + UxColor.CHAR_RED)
								+ bulletTypeName);
			}
		}
	}

	protected void drawSquadUnitTagMap() {
		// draw neutral units and our units
		for (Squad squad : CombatManager.Instance().squadData.getSquadMap().values()) {
			Color color = UxColor.SQUAD_COLOR.get(squad.getClass());
			if (color == null) {
				continue;
			}
			String squadName = squad.getSquadName();

			StrategyCode.SmallFightPredict smallFightPredict = null;
			if (squad instanceof WatcherSquad) {
				smallFightPredict = ((WatcherSquad) squad).getSmallFightPredict();
			}

			if (squadName.length() > 4) {
				squadName = squadName.substring(0, 4);
			}

			for (Unit unit : squad.unitList) {
				Broodwar.drawCircleMap(unit.getPosition(), 10, color);
				Broodwar.drawTextMap(unit.getPosition().getX() - 20, unit.getPosition().getY() - 30, squadName);
				if (smallFightPredict != null && smallFightPredict == StrategyCode.SmallFightPredict.BACK) {
					Broodwar.drawTextMap(unit.getPosition().getX() - 20, unit.getPosition().getY() - 15,
							UxColor.CHAR_RED + smallFightPredict.toString());
				}
			}

		}
	}

	/*
	 * private void drawSquadInfoOnMap() { /// ConstructionQueue 를 Screen 에
	 * 표시합니다 uxDrawConfig =
	 * UxDrawConfig.newInstanceObjectType("L","<Squad Name>","   <Unit Size>"
	 * ,UxColor.CHAR_WHITE); drawStrategyLeftList.add(uxDrawConfig);
	 * 
	 * uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","" + "*" +
	 * "SCV","   "+ UnitUtils.getUnitCount(UnitFindStatus.COMPLETE,
	 * UnitType.Terran_SCV),UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig);
	 * 
	 * for (Squad squad :
	 * CombatManager.Instance().squadData.getSquadMap().values()) { Color
	 * squadColor = UxColor.SQUAD_COLOR.get(squad.getClass()); String squadName
	 * = ""; if (squadColor != null) { squadName = "" +
	 * UxColor.COLOR_TO_CHARACTER.get(squadColor) + squad.getSquadName(); } else
	 * { squadName = "*" + squad.getSquadName(); } String unitIds = " ... "; for
	 * (Unit unit : squad.unitList) { unitIds = unitIds + unit.getID() + "/"; }
	 * uxDrawConfig = UxDrawConfig.newInstanceObjectType("L",squadName,"   "+ ""
	 * + squad.unitList.size() + unitIds,UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig); } }
	 */

	protected void drawManagerTimeSpent() {
		List<GameManager> gameManagers = Arrays.asList(
				// InformationManager.Instance(),
				StrategyManager.Instance(), MapGrid.Instance(), BuildManager.Instance(), BuildQueueProvider.Instance(),
				ConstructionManager.Instance(), WorkerManager.Instance(), CombatManager.Instance()
		// AttackDecisionMaker.Instance()
		);

		for (GameManager gameManager : gameManagers) {
			char drawColor = UxColor.CHAR_WHITE;
			if (gameManager.getRecorded() > 10L) {
				drawColor = UxColor.CHAR_TEAL;
			} else if (gameManager.getRecorded() > 30L) {
				drawColor = UxColor.CHAR_RED;
			}
			uxDrawConfig = UxDrawConfig.newInstanceStringType("R", gameManager.getClass().getSimpleName(),
					gameManager.getRecorded(), UxColor.CHAR_PURPLE);
			drawStrategyRightList.add(uxDrawConfig);
		}

		uxDrawConfig = UxDrawConfig.newInstanceMethodType("R", "* group size", LagObserver.class, "groupsize",
				UxColor.CHAR_WHITE);
		drawStrategyRightList.add(uxDrawConfig);
		uxDrawConfig = UxDrawConfig.newInstanceMethodType("R", "* manager rotation size", LagObserver.class,
				"managerRotationSize", UxColor.CHAR_WHITE);
		drawStrategyRightList.add(uxDrawConfig);
	}

	/*
	 * private void drawBigWatch() { Map<String, Long> resultTimeMap =
	 * BigWatch.getResultTimeMap(); Map<String, Long> recordTimeMap =
	 * BigWatch.getRecordTimeMap();
	 * 
	 * List<String> tags = new ArrayList<>(recordTimeMap.keySet());
	 * Collections.sort(tags);
	 * 
	 * for (String tag : tags) { Long resultTime = resultTimeMap.get(tag);
	 * resultTime = resultTime == null ? 0L : resultTime; Long recordTime =
	 * recordTimeMap.get(tag);
	 * 
	 * char drawColor = UxColor.CHAR_WHITE; if (recordTime > 10L) { drawColor =
	 * UxColor.CHAR_TEAL; } else if (recordTime > 30L) { drawColor =
	 * UxColor.CHAR_RED; } uxDrawConfig =
	 * UxDrawConfig.newInstanceObjectType("L","",tag + " : " + resultTime +
	 * " / " + drawColor + recordTime,UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig); } }
	 */

	protected void drawPathData() {
		for (Unit depot : UnitUtils.getUnitList(UnitType.Terran_Command_Center)) {
			List<Minerals> mineralsList = WorkerData.depotMineral.get(depot);
			if (mineralsList == null) {
				return;
			}

			for (Minerals minr : mineralsList) {
				if (minr.mineralTrick != null) {
					Broodwar.drawCircleMap(minr.mineralUnit.getPosition().getX(), minr.mineralUnit.getPosition().getY(),
							4, Color.Blue, true);
					Broodwar.drawCircleMap(minr.mineralTrick.getPosition().getX(),
							minr.mineralTrick.getPosition().getY(), 4, Color.Purple, true);
				}
			}

			for (Minerals minr : WorkerData.depotMineral.get(depot)) {
				if (minr.posTrick != bwapi.Position.None) {
					Broodwar.drawCircleMap(minr.posTrick.getX(), minr.posTrick.getY(), 4, Color.Red, true);
					Broodwar.drawCircleMap(minr.mineralUnit.getPosition().getX(), minr.mineralUnit.getPosition().getY(),
							4, Color.Yellow, true);
				}
			}

		}

	}

	protected void drawStrategy() {
		String upgradeString = "";
		for (MetaType metaType : StrategyBoard.upgrade) {
			upgradeString += metaType.getName() + " > ";
		}

		Race enemyRace = PlayerUtils.enemyRace();
		EnemyStrategy strategy = StrategyBoard.currentStrategy;
		int phase = EnemyStrategyAnalyzer.Instance().getPhase();

		// setting
		UxDrawConfig uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "",
				"[" + strategy.name() + " ...(phase " + phase + ")]", UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "UPGRADE", upgradeString, UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "EXPANSION", StrategyBoard.expansionOption,
				UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "MISSION", strategy.missionTypeList, UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", ClueManager.Instance().getClueInfoList(),
				UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "", strategy.buildTimeMap, UxColor.CHAR_WHITE);
		drawStrategyLeftList.add(uxDrawConfig);

		for (EnemyStrategy enemyStrategy : EnemyStrategy.values()) {
			if (enemyStrategy.name().startsWith(enemyRace.toString().toUpperCase())) {
				uxDrawConfig = UxDrawConfig.newInstanceStringType("R", "", enemyStrategy.name(), UxColor.CHAR_YELLOW);
				drawStrategyRightList.add(uxDrawConfig);
			}
		}
	}

	private void drawEnemyAirDefenseRange() {
		List<UnitInfo> airDefenseEuiList = UnitUtils.getEnemyUnitInfoList(EnemyUnitVisibleStatus.ALL,
				UnitTypeUtils.enemyAirDefenseUnitType());
		for (UnitInfo eui : airDefenseEuiList) {
			if (eui.getType() == UnitType.Terran_Bunker) {
				Broodwar.drawCircleMap(eui.getLastPosition(),
						Broodwar.enemy().weaponMaxRange(UnitType.Terran_Marine.groundWeapon()) + 96, Color.White);
			} else {
				Broodwar.drawCircleMap(eui.getLastPosition(), eui.getType().airWeapon().maxRange(), Color.White);
			}
		}
		List<UnitInfo> wraithKillerEuiList = UnitUtils.getEnemyUnitInfoList(EnemyUnitVisibleStatus.ALL,
				UnitTypeUtils.wraithKillerUnitType());
		for (UnitInfo eui : wraithKillerEuiList) {
			Broodwar.drawCircleMap(eui.getLastPosition(), eui.getType().airWeapon().maxRange(), Color.Grey);
		}
	}

	private void drawAirForceInformation() {
		// wraith moving
		for (Unit unit : UnitUtils.getUnitList(UnitType.Zerg_Mutalisk)) {
			if (unit.isMoving()) {
				Broodwar.drawCircleMap(unit.getPosition(), dotRadius, Color.Orange, true);
				Broodwar.drawCircleMap(unit.getTargetPosition(), dotRadius, Color.Orange, true);
				Broodwar.drawLineMap(unit.getPosition(), unit.getTargetPosition(), Color.Orange);
			}
		}

		// target position
		List<Position> targetPositions = AirForceManager.Instance().getTargetPositions();
		for (int i = 0; i < targetPositions.size(); i++) {
			Broodwar.drawTextMap(targetPositions.get(i), "position#" + i);
		}

		// air force team
		int y = 190;
		Set<AirForceTeam> airForceTeamSet = new HashSet<>(AirForceManager.Instance().getAirForceTeamMap().values());
		List<AirForceTeam> airForceList = new ArrayList<>(airForceTeamSet);
		airForceList.sort(new Comparator<AirForceTeam>() {
			@Override
			public int compare(AirForceTeam a1, AirForceTeam a2) {
				int memberGap = a1.memberList.size() - a2.memberList.size();
				int idGap = a1.leaderUnit.getID() - a2.leaderUnit.getID();
				return memberGap * 100 + idGap;
			}
		});
		for (AirForceTeam airForceTeam : airForceList) {
			char color = UxColor.CHAR_WHITE;
			if (airForceTeam.repairCenter != null) {
				color = UxColor.CHAR_RED;
			}
			Position position = airForceTeam.leaderUnit.getPosition();
			Broodwar.drawTextMap(position.getX(), position.getY() - 10,
					color + "leader#" + airForceTeam.leaderUnit.getID());

			Position targetPosition = new Position(airForceTeam.getTargetPosition().getX(),
					airForceTeam.getTargetPosition().getY() - 10);
			Broodwar.drawTextMap(targetPosition, UxColor.CHAR_RED + "*" + airForceTeam.leaderUnit.getID());
			Broodwar.drawTextScreen(L, y += 15, "" + UxColor.CHAR_YELLOW + airForceTeam.toString());
		}

	}

	/*
	 * private void drawEnemyBaseToBaseTime() { uxDrawConfig =
	 * UxDrawConfig.newInstanceObjectType("L","","campPosition : " +
	 * StrategyBoard.campPosition + " / " +
	 * StrategyBoard.campType,UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig);
	 * 
	 * uxDrawConfig =
	 * UxDrawConfig.newInstanceObjectType("L","","mainPosition : " +
	 * StrategyBoard.mainPosition,UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig);
	 * 
	 * uxDrawConfig =
	 * UxDrawConfig.newInstanceObjectType("L","","watcherPosition : " +
	 * StrategyBoard.watcherPosition,UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig);
	 * 
	 * uxDrawConfig =
	 * UxDrawConfig.newInstanceObjectType("L","","mainSquadCenter : " +
	 * StrategyBoard.mainSquadCenter,UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig);
	 * 
	 * uxDrawConfig =
	 * UxDrawConfig.newInstanceObjectType("L","","enemyGroundSquadPosition : " +
	 * StrategyBoard.nearGroundEnemyPosition + " / " +
	 * StrategyBoard.enemyUnitStatus,UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig);
	 * 
	 * uxDrawConfig =
	 * UxDrawConfig.newInstanceObjectType("L","","enemyAirSquadPosition : " +
	 * StrategyBoard.nearAirEnemyPosition,UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig);
	 * 
	 * uxDrawConfig =
	 * UxDrawConfig.newInstanceObjectType("L","","enemyDropEnemyPosition : " +
	 * StrategyBoard.dropEnemyPosition,UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig);
	 * 
	 * Position enemyBasePosition = null; Position enemyExpansionPosition =
	 * null; if (BaseUtils.enemyMainBase() != null) { enemyBasePosition =
	 * BaseUtils.enemyMainBase().getPosition(); enemyExpansionPosition =
	 * BaseUtils.enemyMainBase().getPosition();
	 * 
	 * } uxDrawConfig =
	 * UxDrawConfig.newInstanceObjectType("L","","enemyMainBase : " +
	 * enemyBasePosition,UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig);
	 * 
	 * uxDrawConfig =
	 * UxDrawConfig.newInstanceObjectType("L","","enemyFirstExpansion : " +
	 * enemyExpansionPosition,UxColor.CHAR_WHITE);
	 * drawStrategyLeftList.add(uxDrawConfig); }
	 */

	protected void drawPositionInformation() {

		if (StrategyBoard.mainSquadLeaderPosition != null) {
			Broodwar.drawTextMap(DrawingUtils.positionAdjusted(StrategyBoard.mainSquadLeaderPosition, 0, -20),
					UxColor.CHAR_WHITE + "V");
		}
		if (StrategyBoard.campPosition.equals(StrategyBoard.mainPosition)) {
			Broodwar.drawTextMap(StrategyBoard.campPosition, UxColor.CHAR_ORANGE + "camp & main");
		} else {
			if (StrategyBoard.campPosition != null) {
				Broodwar.drawTextMap(StrategyBoard.campPosition, UxColor.CHAR_YELLOW + "camp");
			}
			if (StrategyBoard.mainPosition != null) {
				Broodwar.drawTextMap(DrawingUtils.positionAdjusted(StrategyBoard.mainPosition, 0, -10),
						UxColor.CHAR_RED + "main");
			}
		}
		if (StrategyBoard.campPositionSiege != null) {
			Broodwar.drawTextMap(StrategyBoard.campPositionSiege, UxColor.CHAR_YELLOW + "camp (siege)");
		}
		if (StrategyBoard.watcherPosition != null) {
			Broodwar.drawTextMap(DrawingUtils.positionAdjusted(StrategyBoard.watcherPosition, 0, -20),
					UxColor.CHAR_BLUE + "watcherPos");
		}
		if (StrategyBoard.mainSquadCenter != null) {
			Broodwar.drawTextMap(StrategyBoard.mainSquadCenter, "mainSqCntr");
			Broodwar.drawCircleMap(StrategyBoard.mainSquadCenter.getX(), StrategyBoard.mainSquadCenter.getY(),
					StrategyBoard.mainSquadCoverRadius, Color.Cyan);
		}
		if (StrategyBoard.nearGroundEnemyPosition != null) {
			Broodwar.drawTextMap(StrategyBoard.nearGroundEnemyPosition, UxColor.CHAR_RED + "nearEnemySq(Ground)");
			Broodwar.drawCircleMap(StrategyBoard.nearGroundEnemyPosition, 150, Color.Red);
		}
		if (StrategyBoard.nearAirEnemyPosition != null) {
			Broodwar.drawTextMap(StrategyBoard.nearAirEnemyPosition, UxColor.CHAR_RED + "nearEnemySq(Air)");
			Broodwar.drawCircleMap(StrategyBoard.nearAirEnemyPosition, 150, Color.Red);
		}
		if (StrategyBoard.dropEnemyPosition != null) {
			Broodwar.drawTextMap(StrategyBoard.dropEnemyPosition, UxColor.CHAR_RED + "dropEnemySq");
			Broodwar.drawCircleMap(StrategyBoard.dropEnemyPosition, 150, Color.Red);
		}
		if (StrategyBoard.totalEnemyCneterPosition != null) {
			Broodwar.drawTextMap(StrategyBoard.totalEnemyCneterPosition, "totalEnemySq");
			Broodwar.drawCircleMap(StrategyBoard.totalEnemyCneterPosition, 250, Color.Red);
		}
	}

	/*
	 * private void addDrawStrategyListOrigin() throws IllegalArgumentException,
	 * IllegalAccessException, ClassNotFoundException{ // TODO Auto-generated
	 * method stub if(!drawStrategyListOrigin[uxOption].isEmpty()){ UxDrawConfig
	 * uxDrawConfig; for (UxDrawConfig drwaConfig :
	 * drawStrategyListOrigin[uxOption]) {
	 * drwaConfig.setClazz(drwaConfig.getKey()); Class<?> c =
	 * drwaConfig.getClazz(); if(drwaConfig.getValue() != null){ Field fld =
	 * null; try{ Method method = c.getMethod("Instance"); Object obj =
	 * method.invoke(arg0, arg1); obj.getClass().getDeclaredField(name) fld =
	 * c.getDeclaredField(drwaConfig.getValue());
	 * 
	 * 
	 * if (!fld.isAccessible()) { fld.setAccessible(true); } } catch (Exception
	 * e) { System.out.println(e.getMessage()); continue; } uxDrawConfig =
	 * UxDrawConfig.newInstanceFieldType(fld.getName(),fld.get(drwaConfig.
	 * getValue()),drwaConfig.getColor());
	 * uxDrawConfig.setPos(drwaConfig.getPos()); if(drwaConfig.getPos() ==
	 * UxDrawConfig.posMap.get("L")){ drawStrategyLeftList.add(uxDrawConfig);
	 * }else if(drwaConfig.getPos() == UxDrawConfig.posMap.get("M")){
	 * drawStrategyMidList.add(uxDrawConfig); }else if(drwaConfig.getPos() ==
	 * UxDrawConfig.posMap.get("R")){ drawStrategyRightList.add(uxDrawConfig);
	 * }else{ drawStrategyLeftList.add(uxDrawConfig); } }
	 * 
	 * } } }
	 */

	// 600 * 300
	protected void drawStrategyList() {
		// TODO Auto-generated method stub
		int nextLy = 20, nextMy = 20, nextRy = 20;

		for (UxDrawConfig uxDraw : drawStrategyLeftList) {
			int lineCnt = 1;
			int fromIndex = -1;
			Broodwar.drawTextScreen(uxDraw.getPos(), nextLy, uxDraw.getColor() + uxDraw.getClassFieldName());
			while ((fromIndex = uxDraw.getClassFieldName().indexOf("\n", fromIndex + 1)) >= 0) {
				lineCnt++;
			}
			nextLy += (lineCnt * 12);
		}

		for (UxDrawConfig uxDraw : drawStrategyMidList) {
			int lineCnt = 1;
			int fromIndex = -1;
			Broodwar.drawTextScreen(uxDraw.getPos(), nextMy, uxDraw.getColor() + uxDraw.getClassFieldName());
			while ((fromIndex = uxDraw.getClassFieldName().indexOf("\n", fromIndex + 1)) >= 0) {
				lineCnt++;
			}
			nextMy += (lineCnt * 12);
		}

		for (UxDrawConfig uxDraw : drawStrategyRightList) {
			int lineCnt = 1;
			int fromIndex = -1;
			Broodwar.drawTextScreen(uxDraw.getPos(), nextRy, uxDraw.getClassFieldName());
			while ((fromIndex = uxDraw.getClassFieldName().indexOf("\n", fromIndex + 1)) >= 0) {
				lineCnt++;
			}
			nextRy += (lineCnt * 12);
		}

		if (TimeUtils.getFrame() % 7 == 0) {
			Broodwar.drawTextScreen(L, (nextLy + 24), " ");
			Broodwar.drawTextScreen(M, (nextMy + 24), " ");
			Broodwar.drawTextScreen(R, (nextRy + 24), " ");
		} else {
			Broodwar.drawTextScreen(L, (nextLy + 24), " : ");
			Broodwar.drawTextScreen(M, (nextMy + 24), " : ");
			Broodwar.drawTextScreen(R, (nextRy + 24), " : ");
		}
	}
}