package org.monster.debugger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.monster.common.constant.UnitFindStatus;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.ChokeUtils;
import org.monster.common.util.DrawingUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.PositionUtils;
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

	private static final int JW = 2;
	private static final int KK = 3;
	
	private static PreBotUXManager instance = new PreBotUXManager();
	
	private final int dotRadius = 2;
    public Unit leader = null;
	private int uxOption = 0;
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
	private Game Broodwar;
	private Player player;
	private int L =  UxDrawConfig.posMap.get("L");
	private int M =  UxDrawConfig.posMap.get("M");
	private int R =  UxDrawConfig.posMap.get("R");
	private UxDrawConfig uxDrawConfig;

	public ArrayList<UxDrawConfig>[] drawStrategyListOrigin = new ArrayList[9];
    public HashMap<Integer, ArrayList<UxDrawConfig>> drawStrategyListMap  = new HashMap<Integer, ArrayList<UxDrawConfig>>();
    public ArrayList<UxDrawConfig> drawStrategyLeftList = new ArrayList<UxDrawConfig>();
    public ArrayList<UxDrawConfig> drawStrategyMidList = new ArrayList<UxDrawConfig>();
    public ArrayList<UxDrawConfig> drawStrategyRightList = new ArrayList<UxDrawConfig>();
    
    private UnitType factorySelected = UnitType.None;

    private Map<Integer, MicroDecision> decisionListForUx = new HashMap<>();
	    
	public PreBotUXManager() {
		initDisplay0();
		initDisplay1();
	}

	public void onStart(Game Broodwar) throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException {
    	for (int i = 0; i<drawStrategyListOrigin.length; i++) {
    		drawStrategyListOrigin[i] = new ArrayList<UxDrawConfig>();
		}
        this.Broodwar = Broodwar;
        player = Broodwar.self();
		System.out.println("player.getName() : " + player.getName());
        
    }

	private void initDisplay0() {
		Displayer displayer = new Displayer();
		displayers.add(displayer);
	}
	
	private void initDisplay1() {
		Displayer displayer = new Displayer();
		displayers.add(displayer);
	}
	
	// TestUxManager.getJwDisplayer();
	// TestUxManager.getDisplayer(1);
	// TestUxManager.getDisplayer(2);
	
	// 0..1 : default
	// 2..3 : custom
	private List<Displayer> displayers = new ArrayList<>();

	public Displayer getJwDisplayer() {
		return displayers.get(JW);
	}
	
	public Displayer getkKDisplayer() {
		return displayers.get(KK);
	}

	

	/// static singleton 객체를 리턴합니다
	public static PreBotUXManager Instance() {
		return instance;
	}
	
	public int option = 0;

	public void update() {
		
		if(player.getName().equals("staeminba")){
			getSMDisplay();
		}

	}
	
	public void setUxOption(int uxOption) {
        this.uxOption = uxOption;
    }
    
	public int getUxOption() {
        return uxOption;
    }
	
	
	

	 private void drawUxInfo() {
			// TODO Auto-generated method stub
	    	int y = 10;
	    	//setting
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","/*****************************************************************************",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","*",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","* 2) UX Type Change		: d + num			ex) d1=?, d0=prebot1 display",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","* 3) Change Strategy	: $ + Strategy Name	ex) $ INIT",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	//uxDrawConfig= new UxDrawConfig("* 4) 레이쓰 공격레벨 변경		: w + 숫자			ex) w1");
	    	//drawStrategyList.add(uxDrawConfig);
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","* 4) add Var 			: a + pos + class + var 	ex) a L(R/M) strategyBoard startStrategy",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","* 5) minus Var 			: m + var  			ex) m startStrategy",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	//uxDrawConfig= new UxDrawConfig("* 6) 디버깅 초기화");
	    	//drawStrategyList.add(uxDrawConfig);
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","*",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","*****************************************************************************///",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
		}

		private void clearList() {
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

	    private void drawTimer() {
	        char battleColor = UxColor.CHAR_WHITE;
	        if (StrategyBoard.initiated) {
	            battleColor = UxColor.CHAR_RED;
	        }
	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("M","",StrategyBoard.mainSquadMode.toString() + ": " + DrawingUtils.framesToTimeString(TimeUtils.getFrame()) + "(" + TimeUtils.getFrame() + ")",battleColor);
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
	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("R","","APM : " + Broodwar.getAPM(),apmColor);
	    	drawStrategyRightList.add(uxDrawConfig);
	    }

	    private void drawEnemyBuildTimer(){
	    	
	        Map<UnitType, Integer> buildTimeExpectMap = EnemyBuildTimer.Instance().buildTimeExpectMap;
	        Map<UnitType, Integer> buildTimeMinimumMap = EnemyBuildTimer.Instance().buildTimeMinimumMap;
	        Set<UnitType> buildTimeCertain = EnemyBuildTimer.Instance().buildTimeCertain;

	        int y = 20;
	        
	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","engine Build Frame",DrawingUtils.framesToTimeString(StrategyBoard.engineeringBayBuildStartFrame),UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","turret Build Frame",DrawingUtils.framesToTimeString(StrategyBoard.turretBuildStartFrame),UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","turret Need Frame",DrawingUtils.framesToTimeString(StrategyBoard.turretNeedFrame),UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","darkTemplarInMyBaseFrame",DrawingUtils.framesToTimeString(EnemyBuildTimer.Instance().darkTemplarInMyBaseFrame),UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","reaverInMyBaseFrame",DrawingUtils.framesToTimeString(EnemyBuildTimer.Instance().reaverInMyBaseFrame),UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","mutaliskInMyBaseFrame",DrawingUtils.framesToTimeString(EnemyBuildTimer.Instance().mutaliskInMyBaseFrame),UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","lurkerInMyBaseFrame",DrawingUtils.framesToTimeString(EnemyBuildTimer.Instance().lurkerInMyBaseFrame),UxColor.CHAR_WHITE);
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
	                uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","",unitType + " : " + expect + " - min: " + minimum + " (" + buildTimeCertain.contains(unitType) + ")",UxColor.CHAR_WHITE);
	    	    	drawStrategyLeftList.add(uxDrawConfig);
	            }
	        }
	    
	    	/*for(UxDrawConfig uxDraw : drawStrategyLeftList){
	    		int lineCnt = 1;
	    	    int fromIndex = -1;
	    	    while ((fromIndex = uxDraw.getClassFieldName().indexOf("\n", fromIndex + 1)) >= 0) {
	    	      lineCnt++;
	    	    }
	   			Broodwar.drawTextScreen(L, y, UxColor.CHAR_YELLOW + uxDraw.getClassFieldName());
	   			y += (lineCnt*12);
	    	}*/

	        
	    }

		private void drawDebugginUxMenu() {
			uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","1. Default Information",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","2. Strategy Information",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","3. Position Finder Test",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","4. Air Micro Test",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","5. Unit Bast To Base",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	uxDrawConfig= UxDrawConfig.newInstanceObjectType("L","","",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    }

	    // 게임 개요 정보를 Screen 에 표시합니다
	    public void drawGameInformationOnScreen(int x, int y) {
	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","Current Strategy",StrategyBoard.currentStrategy.name(),UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);

	        y = 10;

	        String history = "";
	        for (int i = StrategyBoard.strategyHistory.size() - 1; i >= 0; i--) {
	            if (i == StrategyBoard.strategyHistory.size() - 3) {
	                history = "... " + history;
	                break;
	            } else {
	                history = StrategyBoard.strategyHistory.get(i).name() + " -> " + history;
	            }
	        }

	        /*int vultureCount = UnitUtils.getUnitCount(UnitFindStatus.ALL, UnitType.Terran_Vulture);
	        int tankCount = UnitUtils.getUnitCount(UnitFindStatus.ALL, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode);
	        int goliathCount = UnitUtils.getUnitCount(UnitFindStatus.ALL, UnitType.Terran_Goliath);*/

//	        UnitType selected = BuildQueueProvider.Instance().getFactoryUnitSelector().getSelected();
//	        if (selected != UnitType.None) {
//	            factorySelected = selected;
//	        }


	   /*     Broodwar.drawTextScreen(x + 100, y + 5, UxColor.CHAR_TEAL + "" + vultureCount + "      " + tankCount + "        " + goliathCount);
	        Broodwar.drawTextScreen(x, y, "" + UxColor.CHAR_WHITE + StrategyBoard.factoryRatio + ", selected=" + factorySelected);
	        y += 11;

	        Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + "Wraith Count : ");
	        Broodwar.drawTextScreen(x + 75, y, "" + UxColor.CHAR_WHITE + StrategyBoard.wraithCount + " / " + UnitUtils.getUnitCount(UnitFindStatus.COMPLETE, UnitType.Terran_Wraith));
	        y += 11;

	        Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + "Valkyrie Count : ");
	        Broodwar.drawTextScreen(x + 75, y, "" + UxColor.CHAR_WHITE + StrategyBoard.valkyrieCount + " / " + UnitUtils.getUnitCount(UnitFindStatus.COMPLETE, UnitType.Terran_Valkyrie));
	        y += 11;*/

	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","MYKillScore",Broodwar.self().getKillScore(),UxColor.CHAR_RED);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","MYRazingScore",Broodwar.self().getRazingScore(),UxColor.CHAR_RED);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","EnemyKillScore",Broodwar.enemy().getKillScore(),UxColor.CHAR_PURPLE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	     	
	     	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","EnemyRazingScore",Broodwar.enemy().getRazingScore(),UxColor.CHAR_PURPLE);
	     	drawStrategyLeftList.add(uxDrawConfig);
	     	
	     	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","Reserved Resource",ConstructionManager.Instance().getReservedMinerals()+ " / " + ConstructionManager.Instance().getReservedGas(),UxColor.CHAR_TEAL);
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
	                if (hpRatio < 0.66) hpColor = Color.Orange;
	                if (hpRatio < 0.33) hpColor = Color.Red;

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

	            //Monster.game.drawBoxMap(BWAPI.Position(left, top), BWAPI.Position(right, bottom), Color.Grey, false);

	            // 유닛의 HitPoint 남아있는 비율 표시
	            if (!unit.getType().isResourceContainer() && unit.getType().maxHitPoints() > 0) {
	                double hpRatio = (double) unit.getHitPoints() / (double) unit.getType().maxHitPoints();

	                Color hpColor = Color.Green;
	                if (hpRatio < 0.66) hpColor = Color.Orange;
	                if (hpRatio < 0.33) hpColor = Color.Red;

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

	    /// UnitType 별 통계 정보를 Screen 에 표시합니다
	    public void drawUnitStatisticsOnScreen1(int x, int y) {
	        int currentY = y;

	        // 아군이 입은 피해 누적값
	        currentY += 10;

	        // 아군 모든 유닛 숫자 합계
	        //Broodwar.drawTextScreen(x, currentY,  white + " allUnitCount: " + UnitUtils.getUnitCount(UnitType.AllUnits));
	        //currentY += 10;

	        // 아군 건설/훈련 완료한 유닛 숫자 합계
	        //Broodwar.drawTextScreen(x, currentY,  white + " completedUnitCount: " + UnitUtils.getCompletedUnitCount(UnitType.AllUnits));
	        //currentY += 10;

	        // 아군 건설/훈련중인 유닛 숫자 합계
	        //Broodwar.drawTextScreen(x, currentY,  white + " incompleteUnitCount: " + Broodwar.self().incompleteUnitCount(UnitType.AllUnits));
	        //currentY += 10;

	        // 아군 유닛 파괴/사망 숫자 누적값
	        //Broodwar.drawTextScreen(x, currentY,  white + " deadUnitCount: " + Broodwar.self().deadUnitCount(UnitType.AllUnits));
	        //currentY += 10;

	        // 상대방 유닛을 파괴/사망 시킨 숫자 누적값
	        //Broodwar.drawTextScreen(x, currentY,  white + " killedUnitCount: " + Broodwar.self().killedUnitCount(UnitType.AllUnits));
	        //currentY += 10;

	        //Broodwar.drawTextScreen(x, currentY,  white + " UnitScore: " + Broodwar.self().getUnitScore());
	        //currentY += 10;
	        //Broodwar.drawTextScreen(x, currentY,  white + " RazingScore: " + Broodwar.self().getRazingScore());
	        //currentY += 10;
	        //Broodwar.drawTextScreen(x, currentY,  white + " BuildingScore: " + Broodwar.self().getBuildingScore());
	        //currentY += 10;
	        //Broodwar.drawTextScreen(x, currentY,  white + " KillScore: " + Broodwar.self().getKillScore());
	        //currentY += 10;
	    }


	    /// BWTA 라이브러리에 의한 Map 분석 결과 정보를 Map 에 표시합니다
	    public void drawBWTAResultOnMap() {
			/*//we will iterate through all the base locations, and draw their outlines.
			// C+ . for (std.set<BWTA.BaseLocation*>.const_iterator i = BWTA.getBaseLocations().begin(); i != BWTA.getBaseLocations().end(); i++)
			for(BaseLocation baseLocation : BWTA.getBaseLocations())
			{
				TilePosition p = baseLocation.getTilePosition();
				Position c = baseLocation.getPosition();

				//draw outline of Base location
				Broodwar.drawBoxMap(p.getX() * 32, p.getY() * 32, p.getX() * 32 + 4 * 32, p.getY() * 32 + 3 * 32, Color.Blue);

				//draw a circle at each mineral patch
				// C++ : for (BWAPI.Unitset.iterator j = (*i).getStaticMinerals().begin(); j != (*i).getStaticMinerals().end(); j++)
				for(Unit unit : baseLocation.getStaticMinerals())
				{
					Position q = unit.getInitialPosition();
					Broodwar.drawCircleMap(q.getX(), q.getY(), 30, Color.Cyan);
				}

				//draw the outlines of vespene geysers
				// C++ : for (BWAPI.Unitset.iterator j = (*i).getGeysers().begin(); j != (*i).getGeysers().end(); j++)
				for(Unit unit :baseLocation.getGeysers() )
				{
					TilePosition q = unit.getInitialTilePosition();
					Broodwar.drawBoxMap(q.getX() * 32, q.getY() * 32, q.getX() * 32 + 4 * 32, q.getY() * 32 + 2 * 32, Color.Orange);
				}

				//if this is an island expansion, draw a yellow circle around the base location
				if (baseLocation.isIsland())
				{
					Broodwar.drawCircleMap(c, 80, Color.Yellow);
				}
			}

			//we will iterate through all the regions and draw the polygon outline of it in green.
			// C++ : for (std.set<BWTA.Region*>.const_iterator r = BWTA.getRegions().begin(); r != BWTA.getRegions().end(); r++)
			for(Region region : BWTA.getRegions())
			{
				Polygon p = region.getPolygon();
				for (int j = 0; j<p.getPoints().size(); j++)
				{
					Position point1 = p.getPoints().get(j);
					Position point2 = p.getPoints().get((j + 1) % p.getPoints().size());
					Broodwar.drawLineMap(point1, point2, Color.Green);
				}
			}

			//we will visualize the chokepoints with red lines
			// C++ : for (std.set<BWTA.Region*>.const_iterator r = BWTA.getRegions().begin(); r != BWTA.getRegions().end(); r++)
			for(Region region : BWTA.getRegions())
			{
				// C++ : for (std.set<BWTA.Chokepoint*>.const_iterator c = (*r).getChokepoints().begin(); c != (*r).getChokepoints().end(); c++)
				for(Chokepoint Chokepoint : region.getChokepoints())
				{
					Position point1 = Chokepoint.getSides().first;
					Position point2 = Chokepoint.getSides().second;
					Broodwar.drawLineMap(point1, point2, Color.Red);
				}
			}*/
	        int blueCount = 0;
	        int cyanCount = 0;
	        int orangeCount = 0;
//			int purpleCount = 0;

	        if (hasSavedBWTAInfo == false) {
	            for (BaseLocation baseLocation : BWTA.getBaseLocations()) {
	                blueCount++;
//					purpleCount++;
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

//				purple = new int[purpleCount][4];
//				int purpleIndex = 0;

	            for (BaseLocation baseLocation : BWTA.getBaseLocations()) {
	                TilePosition p = baseLocation.getTilePosition();
	                Position c = baseLocation.getPosition();

	                blue[blueIndex][0] = p.getX() * 32;
	                blue[blueIndex][1] = p.getY() * 32;
	                blue[blueIndex][2] = p.getX() * 32 + 4 * 32;
	                blue[blueIndex][3] = p.getY() * 32 + 3 * 32;
	                blueIndex++;

//					purple[purpleIndex][0] = (p.getX()+4) * 32;
//					purple[purpleIndex][1] = (p.getY()+1) * 32;
//					purple[purpleIndex][2] = (p.getX()+4) * 32 + 2 * 32;
//					purple[purpleIndex][3] = (p.getY()+1) * 32 + 2 * 32;
//					purpleIndex++;

	                //draw a circle at each mineral patch
	                // C++ : for (BWAPI.Unitset.iterator j = (*i).getStaticMinerals().begin(); j != (*i).getStaticMinerals().end(); j++)
	                for (Unit unit : baseLocation.getStaticMinerals()) {
	                    Position q = unit.getInitialPosition();
	                    cyan[cyanIndex][0] = q.getX();
	                    cyan[cyanIndex][1] = q.getY();
	                    cyanIndex++;
	                }

	                //draw the outlines of vespene geysers
	                // C++ : for (BWAPI.Unitset.iterator j = (*i).getGeysers().begin(); j != (*i).getGeysers().end(); j++)
	                for (Unit unit : baseLocation.getGeysers()) {
	                    TilePosition q = unit.getInitialTilePosition();
	                    orange[orangeIndex][0] = q.getX() * 32;
	                    orange[orangeIndex][1] = q.getY() * 32;
	                    orange[orangeIndex][2] = q.getX() * 32 + 4 * 32;
	                    orange[orangeIndex][3] = q.getY() * 32 + 2 * 32;
	                    orangeIndex++;
	                }

	                //if this is an island expansion, draw a yellow circle around the base location
	                if (baseLocation.isIsland()) {
	                    yellow.add(c);
	                }
	            }

	            //we will iterate through all the regions and draw the polygon outline of it in green.
	            // C++ : for (std.set<BWTA.Region*>.const_iterator r = BWTA.getRegions().begin(); r != BWTA.getRegions().end(); r++)
	            for (Region region : BWTA.getRegions()) {
	                Polygon p = region.getPolygon();
	                for (int j = 0; j < p.getPoints().size(); j++) {
	                    green1.add(p.getPoints().get(j));
	                    green2.add(p.getPoints().get((j + 1) % p.getPoints().size()));
	                }
	            }

	            //we will visualize the chokepoints with red lines
	            // C++ : for (std.set<BWTA.Region*>.const_iterator r = BWTA.getRegions().begin(); r != BWTA.getRegions().end(); r++)
	            for (Region region : BWTA.getRegions()) {
	                // C++ : for (std.set<BWTA.Chokepoint*>.const_iterator c = (*r).getChokepoints().begin(); c != (*r).getChokepoints().end(); c++)
	                for (Chokepoint Chokepoint : region.getChokepoints()) {
	                    red1.add(Chokepoint.getSides().first);
	                    red2.add(Chokepoint.getSides().second);
	                }
	            }
	            hasSavedBWTAInfo = true;

//				System.out.println(blueCount + " " + cyanCount + " " + orangeCount + " " + yellowCount + " " + greenCount + " " + redCount);
	        }

	        if (hasSavedBWTAInfo) {
	            for (int i1 = 0; i1 < blue.length; i1++) {
	                Broodwar.drawBoxMap(blue[i1][0], blue[i1][1], blue[i1][2], blue[i1][3], Color.Blue);
	            }
//				for(int i1=0 ; i1<purple.length ; i1++)
//				{
//					Prebot.Broodwar.drawBoxMap(purple[i1][0], purple[i1][1], purple[i1][2], purple[i1][3], Color.Purple);
//				}
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

//	            if (UnitTypeUtils.enemyFirstChoke().getFirstChokePoint(PlayerUtils.enemyPlayer()) != null) {
//	                Broodwar.drawTextMap(BaseUtils.enemyMainBase().getPosition(), "Enemy MainBaseLocation");
//	            }
//	            if (UnitTypeUtils.getFirstChokePoint(PlayerUtils.enemyPlayer()) != null) {
//	                Broodwar.drawTextMap(InformationManager.Instance().getFirstChokePoint(PlayerUtils.enemyPlayer()).getCenter(), "Enemy First ChokePoint");
//	            }
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
	    public void drawBuildOrderQueueOnScreen(int x, int y) {
	        char initialFinishedColor;

	        Deque<BuildOrderItem> buildQueue = BuildManager.Instance().buildQueue.getQueue();
	        int itemCount = 0;

	        Object[] tempQueue = buildQueue.toArray();

	        for (int i = 0; i < tempQueue.length; i++) {
	            BuildOrderItem currentItem = (BuildOrderItem) tempQueue[i];
	            //Broodwar.drawTextScreen(x, y + 10 + (itemCount * 10), currentItem.blocking + " " + UxColor.CHAR_WHITE + currentItem.metaType.getName());
	            UxDrawConfig uxDrawConfig;
		        uxDrawConfig = UxDrawConfig.newInstanceObjectType("M","",currentItem.blocking + " " + currentItem.metaType.getName(),UxColor.CHAR_WHITE);
		    	drawStrategyMidList.add(uxDrawConfig);
	            itemCount++;
	            if (itemCount >= 24) break;
	        }
	    }

	    /// Build 진행 상태를 Screen 에 표시합니다
	    public void drawBuildStatusOnScreen(int x, int y)  {
	        // 건설 / 훈련 중인 유닛 진행상황 표시
	        Vector<Unit> unitsUnderConstruction = new Vector<Unit>();
	        for (Unit unit : Broodwar.self().getUnits()) {
	            if (unit != null && unit.isBeingConstructed()) {
	                unitsUnderConstruction.add(unit);
	            }
	        }

	        // sort it based on the time it was started
	        Object[] tempArr = unitsUnderConstruction.toArray();
	        //Arrays.sort(tempArr);
	        unitsUnderConstruction = new Vector<Unit>();
	        for (int i = 0; i < tempArr.length; i++) {
	            unitsUnderConstruction.add((Unit) tempArr[i]);
	        }
	        // C++ : std.sort(unitsUnderConstruction.begin(), unitsUnderConstruction.end(), CompareWhenStarted());

	        //Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + " <Build Status>");
	        
	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("R",""," <Build Status>",UxColor.CHAR_WHITE);
	    	drawStrategyRightList.add(uxDrawConfig);

	        int reps = unitsUnderConstruction.size() < 10 ? unitsUnderConstruction.size() : 10;

	        for (Unit unit : unitsUnderConstruction) {
	            y += 10;
	            UnitType t = unit.getType();
	            if (t == UnitType.Zerg_Egg) {
	                t = unit.getBuildType();
	            }
	            //Broodwar.drawTextScreen(x, y, "" + UxColor.CHAR_WHITE + t + " (" + unit.getRemainingBuildTime() + ")");
	            uxDrawConfig = UxDrawConfig.newInstanceObjectType("R","","" +  t + " (" + unit.getRemainingBuildTime() + ")",UxColor.CHAR_WHITE);
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
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("M",""," <Construction Status>",UxColor.CHAR_WHITE);
	        drawStrategyMidList.add(uxDrawConfig);

	        int yspace = 0;

	        Vector<ConstructionTask> constructionQueue = ConstructionManager.Instance().getConstructionQueue();

	        for (final ConstructionTask b : constructionQueue) {
	            String constructionState = "";

	            if (b.getStatus() == ConstructionTask.ConstructionStatus.Unassigned.ordinal()) {
	            	uxDrawConfig = UxDrawConfig.newInstanceObjectType("M","","" + b.getType() + " - No Worker",UxColor.CHAR_WHITE);
	    	    	drawStrategyMidList.add(uxDrawConfig);
	            } else if (b.getStatus() == ConstructionTask.ConstructionStatus.Assigned.ordinal()) {
	                if (b.getConstructionWorker() == null) {
	                	uxDrawConfig = UxDrawConfig.newInstanceObjectType("M","",b.getType() + " - Assigned Worker Null",UxColor.CHAR_WHITE);
		    	    	drawStrategyMidList.add(uxDrawConfig);
	                } else {
	                	uxDrawConfig = UxDrawConfig.newInstanceObjectType("M","",b.getType() + " - Assigned Worker " + b.getConstructionWorker().getID() + ", Position (" + b.getFinalPosition().getX() + "," + b.getFinalPosition().getY() + ")",UxColor.CHAR_WHITE);
	                	drawStrategyMidList.add(uxDrawConfig);
	                }

	                int x1 = b.getFinalPosition().getX() * 32;
	                int y1 = b.getFinalPosition().getY() * 32;
	                int x2 = (b.getFinalPosition().getX() + b.getType().tileWidth()) * 32;
	                int y2 = (b.getFinalPosition().getY() + b.getType().tileHeight()) * 32;

	                Broodwar.drawLineMap(b.getConstructionWorker().getPosition().getX(), b.getConstructionWorker().getPosition().getY(), (x1 + x2) / 2, (y1 + y2) / 2, Color.Orange);
	                Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Red, false);
	            } else if (b.getStatus() == ConstructionTask.ConstructionStatus.UnderConstruction.ordinal()) {
	            	uxDrawConfig = UxDrawConfig.newInstanceObjectType("M","","" + b.getType() + " - Under Construction",UxColor.CHAR_WHITE);
                	drawStrategyMidList.add(uxDrawConfig);
	            }
	            yspace++;
	        }
	    }

	    public void drawnextPoints() {

//			Position nextEX = InformationManager.Instance().getNextExpansionLocation().getPosition();
//			Position nextBuild = InformationManager.Instance().getLastBuildingLocation().toPosition();
//			Position lastBuild2 = InformationManager.Instance().getLastBuildingLocation2().toPosition();

//	        BaseLocation getExpansionLocation = InformationManager.Instance().getExpansionLocation;
//	        BaseLocation secondStartPosition = null;//InformationManager.Instance().getSecondStartPosition();
//	        TilePosition getLastBuildingLocation = InformationManager.Instance().getLastBuildingLocation;
//	        TilePosition getLastBuildingFinalLocation = InformationManager.Instance().getLastBuildingFinalLocation;
	//
	//
//	        if (secondStartPosition != null) {
//	            Broodwar.drawTextScreen(10, 120, "secondStartPosition: " + secondStartPosition.getTilePosition());
//	            Broodwar.drawTextMap(secondStartPosition.getPosition(), "secondStartPosition");
//	        } else {
//	            Broodwar.drawTextScreen(10, 120, "secondStartPosition: null");
//	        }
//	        if (getExpansionLocation != null) {
//	            Broodwar.drawTextScreen(10, 130, "getExpansionLocation: " + getExpansionLocation.getTilePosition());
//	            Broodwar.drawTextMap(getExpansionLocation.getPosition(), "nextEX");
//	        } else {
//	            Broodwar.drawTextScreen(10, 130, "getExpansionLocation: null");
//	        }
//	        if (getLastBuildingLocation != null) {
//	            Broodwar.drawTextScreen(10, 140, "getLastBuildingLocation: " + getLastBuildingLocation);
//	            Broodwar.drawTextMap(getLastBuildingLocation.toPosition(), "nextBuild");
//	        } else {
//	            Broodwar.drawTextScreen(10, 140, "getLastBuildingLocation: null");
//	        }
//	        if (getLastBuildingFinalLocation != null) {
//	            Broodwar.drawTextScreen(10, 150, "getLastBuildingFinalLocation: " + getLastBuildingFinalLocation);
//	            Broodwar.drawTextMap(getLastBuildingFinalLocation.toPosition(), "LastBuild");
//	        } else {
//	            Broodwar.drawTextScreen(10, 150, "getLastBuildingFinalLocation: null");
//	        }
	//
	//
//	        Broodwar.drawTextScreen(10, 160, "mainBaseLocationFull: " + BuildManager.Instance().mainBaseLocationFull);
//	        Broodwar.drawTextScreen(10, 170, "secondChokePointFull: " + BuildManager.Instance().secondChokePointFull);
//	        Broodwar.drawTextScreen(10, 180, "secondStartLocationFull: " + BuildManager.Instance().secondStartLocationFull);
//	        Broodwar.drawTextScreen(10, 190, "fisrtSupplePointFull: " + BuildManager.Instance().fisrtSupplePointFull);
	//
//	        Broodwar.drawTextScreen(10, 200, "myMainbaseLocation : " + BaseUtils.myMainBase().getTilePosition());
//	        Broodwar.drawTextScreen(10, 210, "enemyMainbaseLocation : " + BaseUtils.enemyMainBase().getTilePosition());

	    }


	    public void drawMineralIdOnMap() {
	        for (Unit unit : Broodwar.getStaticMinerals()) {

	            Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5, "" + UxColor.CHAR_WHITE + unit.getID());
	        }
	    }

	    /// Unit 의 Id 를 Map 에 표시합니다
	    public void drawUnitIdOnMap() {
	        for (Unit unit : Broodwar.self().getUnits()) {
	            if (unit.getType().isBuilding()) {
	                Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5, "" + UxColor.CHAR_WHITE + unit.getID());
	                Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 25, "" + UxColor.CHAR_WHITE + unit.getTilePosition().getX() + " / " + unit.getTilePosition().getY());
	            } else {
	                Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5, "" + UxColor.CHAR_WHITE + unit.getID());
	            }

	        }
	        for (Unit unit : Broodwar.enemy().getUnits()) {
	            Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5, "" + UxColor.CHAR_WHITE + unit.getID());
	        }
	    }

	    public void drawLeaderUnitOnMap() {

//			
//			if(leader!=null){
//				for (Unit unit : Broodwar.self().getUnits())
//				{
//					if(unit.getID() == leader.getID())
//					Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5, "" + blue + "LEADER");
//				}
//			}
	    }

	    /// Worker Unit 들의 상태를 Screen 에 표시합니다
	    public void drawWorkerStateOnScreen(int x, int y) {
	        WorkerData workerData = WorkerManager.Instance().getWorkerData();

	        Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + "<Workers : " + workerData.getNumMineralWorkers() + ">");

	        int yspace = 0;

	        for (Unit unit : workerData.getWorkers()) {
	            if (unit == null) continue;

	            // Mineral / Gas / Idle Worker 는 표시 안한다
	            if (workerData.getJobCode(unit) == 'M' || workerData.getJobCode(unit) == 'I' || workerData.getJobCode(unit) == 'G') {
	                continue;
	            }

	            Broodwar.drawTextScreen(x, y + 10 + ((yspace) * 10), UxColor.CHAR_WHITE + " " + unit.getID());

	            if (workerData.getJobCode(unit) == 'B') {
	                Broodwar.drawTextScreen(x + 30, y + 10 + ((yspace++) * 10), UxColor.CHAR_WHITE + " " + workerData.getJobCode(unit) + " " + unit.getBuildType() + " " + (unit.isConstructing() ? 'Y' : 'N') + " (" + unit.getTilePosition().getX() + ", " + unit.getTilePosition().getY() + ")");
	            } else {
	                Broodwar.drawTextScreen(x + 30, y + 10 + ((yspace++) * 10), UxColor.CHAR_WHITE + " " + workerData.getJobCode(unit));
	            }
	        }
	    }

	    /// ResourceDepot 별 Worker 숫자를 Map 에 표시합니다
	    public void drawWorkerCountOnMap() {
	        for (Unit depot : WorkerManager.Instance().getWorkerData().getDepots()) {
	            if (depot == null) continue;

	            int x = depot.getPosition().getX() - 64;
	            int y = depot.getPosition().getY() - 32;

	            Broodwar.drawBoxMap(x - 2, y - 1, x + 75, y + 14, Color.Black, true);
	            Broodwar.drawTextMap(x, y, UxColor.CHAR_WHITE + " Workers: " + WorkerManager.Instance().getWorkerData().getNumAssignedWorkers(depot));
	        }
	    }

	    /// Worker Unit 의 자원채취 현황을 Map 에 표시합니다
	    public void drawWorkerMiningStatusOnMap() {
	        WorkerData workerData = WorkerManager.Instance().getWorkerData();

	        for (Unit worker : workerData.getWorkers()) {
	            if (worker == null) continue;

	            Position pos = worker.getTargetPosition();

	            Broodwar.drawTextMap(worker.getPosition().getX(), worker.getPosition().getY() - 5, "" + UxColor.CHAR_WHITE + workerData.getJobCode(worker));

	            Broodwar.drawLineMap(worker.getPosition().getX(), worker.getPosition().getY(), pos.getX(), pos.getY(), Color.Cyan);

				/*
				// ResourceDepot ~ Worker 사이에 직선 표시
				BWAPI.Unit depot = workerData.getWorkerDepot(worker);
				if (depot) {
					Monster.game.drawLineMap(worker.getPosition().x, worker.getPosition().y, depot.getPosition().x, depot.getPosition().y, Color.Orange);
				}
				*/
	        }
	    }

	    /// 정찰 상태를 Screen 에 표시합니다
	    public void drawScoutInformation(int x, int y) {

	        // get the enemy base location, if we have one
	        BaseLocation enemyBaseLocation = BaseUtils.enemyMainBase();

	        if (enemyBaseLocation != null) {
	            Broodwar.drawTextScreen(x, y, "Enemy MainBaseLocation : (" + enemyBaseLocation.getTilePosition().getX() + ", " + enemyBaseLocation.getTilePosition().getY() + ")");
	        } else {
	            Broodwar.drawTextScreen(x, y, "Enemy MainBaseLocation : Unknown");
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

	            if (b.getType() == BulletType.Acid_Spore) bulletTypeName = "Acid_Spore";
	            else if (b.getType() == BulletType.Anti_Matter_Missile) bulletTypeName = "Anti_Matter_Missile";
	            else if (b.getType() == BulletType.Arclite_Shock_Cannon_Hit) bulletTypeName = "Arclite_Shock_Cannon_Hit";
	            else if (b.getType() == BulletType.ATS_ATA_Laser_Battery) bulletTypeName = "ATS_ATA_Laser_Battery";
	            else if (b.getType() == BulletType.Burst_Lasers) bulletTypeName = "Burst_Lasers";
	            else if (b.getType() == BulletType.C_10_Canister_Rifle_Hit) bulletTypeName = "C_10_Canister_Rifle_Hit";
	            else if (b.getType() == BulletType.Consume) bulletTypeName = "Consume";
	            else if (b.getType() == BulletType.Corrosive_Acid_Shot) bulletTypeName = "Corrosive_Acid_Shot";
	            else if (b.getType() == BulletType.Dual_Photon_Blasters_Hit) bulletTypeName = "Dual_Photon_Blasters_Hit";
	            else if (b.getType() == BulletType.EMP_Missile) bulletTypeName = "EMP_Missile";
	            else if (b.getType() == BulletType.Ensnare) bulletTypeName = "Ensnare";
	            else if (b.getType() == BulletType.Fragmentation_Grenade) bulletTypeName = "Fragmentation_Grenade";
	            else if (b.getType() == BulletType.Fusion_Cutter_Hit) bulletTypeName = "Fusion_Cutter_Hit";
	            else if (b.getType() == BulletType.Gauss_Rifle_Hit) bulletTypeName = "Gauss_Rifle_Hit";
	            else if (b.getType() == BulletType.Gemini_Missiles) bulletTypeName = "Gemini_Missiles";
	            else if (b.getType() == BulletType.Glave_Wurm) bulletTypeName = "Glave_Wurm";
	            else if (b.getType() == BulletType.Halo_Rockets) bulletTypeName = "Halo_Rockets";
	            else if (b.getType() == BulletType.Invisible) bulletTypeName = "Invisible";
	            else if (b.getType() == BulletType.Longbolt_Missile) bulletTypeName = "Longbolt_Missile";
	            else if (b.getType() == BulletType.Melee) bulletTypeName = "Melee";
	            else if (b.getType() == BulletType.Needle_Spine_Hit) bulletTypeName = "Needle_Spine_Hit";
	            else if (b.getType() == BulletType.Neutron_Flare) bulletTypeName = "Neutron_Flare";
	            else if (b.getType() == BulletType.None) bulletTypeName = "None";
	            else if (b.getType() == BulletType.Optical_Flare_Grenade) bulletTypeName = "Optical_Flare_Grenade";
	            else if (b.getType() == BulletType.Particle_Beam_Hit) bulletTypeName = "Particle_Beam_Hit";
	            else if (b.getType() == BulletType.Phase_Disruptor) bulletTypeName = "Phase_Disruptor";
	            else if (b.getType() == BulletType.Plague_Cloud) bulletTypeName = "Plague_Cloud";
	            else if (b.getType() == BulletType.Psionic_Shockwave_Hit) bulletTypeName = "Psionic_Shockwave_Hit";
	            else if (b.getType() == BulletType.Psionic_Storm) bulletTypeName = "Psionic_Storm";
	            else if (b.getType() == BulletType.Pulse_Cannon) bulletTypeName = "Pulse_Cannon";
	            else if (b.getType() == BulletType.Queen_Spell_Carrier) bulletTypeName = "Queen_Spell_Carrier";
	            else if (b.getType() == BulletType.Seeker_Spores) bulletTypeName = "Seeker_Spores";
	            else if (b.getType() == BulletType.STA_STS_Cannon_Overlay) bulletTypeName = "STA_STS_Cannon_Overlay";
	            else if (b.getType() == BulletType.Subterranean_Spines) bulletTypeName = "Subterranean_Spines";
	            else if (b.getType() == BulletType.Sunken_Colony_Tentacle) bulletTypeName = "Sunken_Colony_Tentacle";
	            else if (b.getType() == BulletType.Unknown) bulletTypeName = "Unknown";
	            else if (b.getType() == BulletType.Yamato_Gun) bulletTypeName = "Yamato_Gun";

	            // 아군 것이면 녹색, 적군 것이면 빨간색
	            Broodwar.drawLineMap(p, new Position(p.getX() + (int) velocityX, p.getY() + (int) velocityY), b.getPlayer() == Broodwar.self() ? Color.Green : Color.Red);
	            if (b.getType() != null) {
	                Broodwar.drawTextMap(p, (b.getPlayer() == Broodwar.self() ? "" + UxColor.CHAR_TEAL : "" + UxColor.CHAR_RED) + bulletTypeName);
	            }
	        }
	    }

	    private void drawSquadUnitTagMap() {
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
	                    Broodwar.drawTextMap(unit.getPosition().getX() - 20, unit.getPosition().getY() - 15, UxColor.CHAR_RED + smallFightPredict.toString());
	                }
	            }

//	            Map<Integer, Integer> checkerSiteMap = VultureTravelManager.Instance().getCheckerSiteMap2();
//	            List<BaseLocation> baseList = VultureTravelManager.Instance().getBaseLocationsCheckerOrdered();
//	            for (Integer checkerId : checkerSiteMap.keySet()) {
//	                Unit unit = Broodwar.getUnit(checkerId);
//	                if (UnitUtils.isValidUnit(unit)) {
//	                    Integer index = checkerSiteMap.get(checkerId);
//	                    if (index != null) {
//	                        Broodwar.drawTextMap(unit.getPosition().getX() - 20, unit.getPosition().getY() - 5, UxColor.CHAR_ORANGE + baseList.get(index).getPosition().toString());
//	                    }
	//
//	                }
//	            }
	        }
	    }

	    private void drawSquadInfoOnMap() {
	        /// ConstructionQueue 를 Screen 에 표시합니다
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","<Squad Name>","   <Unit Size>",UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","" + "*" + "SCV","   "+ UnitUtils.getUnitCount(UnitFindStatus.COMPLETE, UnitType.Terran_SCV),UxColor.CHAR_WHITE);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	        for (Squad squad : CombatManager.Instance().squadData.getSquadMap().values()) {
	            Color squadColor = UxColor.SQUAD_COLOR.get(squad.getClass());
	            String squadName = "";
	            if (squadColor != null) {
	            	squadName = "" + UxColor.COLOR_TO_CHARACTER.get(squadColor) + squad.getSquadName();
	            } else {
	            	squadName = "*" + squad.getSquadName();
	            }
	            String unitIds = " ... ";
	            for (Unit unit : squad.unitList) {
	                unitIds = unitIds + unit.getID() + "/";
	            }
	            uxDrawConfig = UxDrawConfig.newInstanceObjectType("L",squadName,"   "+ "" + squad.unitList.size() + unitIds,UxColor.CHAR_WHITE);
	            drawStrategyLeftList.add(uxDrawConfig);
	        }
	    }

	    private void drawManagerTimeSpent() {
	        List<GameManager> gameManagers = Arrays.asList(
	                //InformationManager.Instance(),
	                StrategyManager.Instance(),
	                MapGrid.Instance(),
	                BuildManager.Instance(),
	                BuildQueueProvider.Instance(),
	                ConstructionManager.Instance(),
	                WorkerManager.Instance(),
	                CombatManager.Instance()
//	                AttackDecisionMaker.Instance()
	        );


	        for (GameManager gameManager : gameManagers) {
	            char drawColor = UxColor.CHAR_WHITE;
	            if (gameManager.getRecorded() > 10L) {
	                drawColor = UxColor.CHAR_TEAL;
	            } else if (gameManager.getRecorded() > 30L) {
	                drawColor = UxColor.CHAR_RED;
	            }
	            uxDrawConfig = UxDrawConfig.newInstanceObjectType("R",gameManager.getClass().getSimpleName()+drawColor,gameManager.getRecorded(),UxColor.CHAR_PURPLE);
		     	drawStrategyRightList.add(uxDrawConfig);
	        }
	        
	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("R","","* group size: " + LagObserver.groupsize(),UxColor.CHAR_WHITE);
	     	drawStrategyRightList.add(uxDrawConfig);
	     	uxDrawConfig = UxDrawConfig.newInstanceObjectType("R","","* manager rotation size: " + LagObserver.managerRotationSize(),UxColor.CHAR_WHITE);
	     	drawStrategyRightList.add(uxDrawConfig);
	    }

	    private void drawBigWatch() {
	        Map<String, Long> resultTimeMap = BigWatch.getResultTimeMap();
	        Map<String, Long> recordTimeMap = BigWatch.getRecordTimeMap();

	        List<String> tags = new ArrayList<>(recordTimeMap.keySet());
	        Collections.sort(tags);

	        int currentY = 0;
	        for (String tag : tags) {
	            Long resultTime = resultTimeMap.get(tag);
	            resultTime = resultTime == null ? 0L : resultTime;
	            Long recordTime = recordTimeMap.get(tag);

	            char drawColor = UxColor.CHAR_WHITE;
	            if (recordTime > 10L) {
	                drawColor = UxColor.CHAR_TEAL;
	            } else if (recordTime > 30L) {
	                drawColor = UxColor.CHAR_RED;
	            }
	            uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","",tag + " : " + resultTime + " / " + drawColor + recordTime,UxColor.CHAR_WHITE);
		        drawStrategyLeftList.add(uxDrawConfig);
	        }
	    }

	    private void drawPathData() {
	        for (Unit depot : UnitUtils.getUnitList(UnitType.Terran_Command_Center)) {
	            List<Minerals> mineralsList = WorkerData.depotMineral.get(depot);
	            if (mineralsList == null) {
//					System.out.println("mineralsList is null.");
//					if (depot != null) {
//						System.out.println("depot=" + depot.getID() +  "" + depot.getPosition());
//					} else {
//						System.out.println("depot is null");
//					}
	                return;
	            }

	            for (Minerals minr : mineralsList) {
	                if (minr.mineralTrick != null) {
	                    Broodwar.drawCircleMap(minr.mineralUnit.getPosition().getX(), minr.mineralUnit.getPosition().getY(), 4, Color.Blue, true);
	                    Broodwar.drawCircleMap(minr.mineralTrick.getPosition().getX(), minr.mineralTrick.getPosition().getY(), 4, Color.Purple, true);
	                }
	            }


	            for (Minerals minr : WorkerData.depotMineral.get(depot)) {
	                if (minr.posTrick != bwapi.Position.None) {
	                    Broodwar.drawCircleMap(minr.posTrick.getX(), minr.posTrick.getY(), 4, Color.Red, true);
	                    Broodwar.drawCircleMap(minr.mineralUnit.getPosition().getX(), minr.mineralUnit.getPosition().getY(), 4, Color.Yellow, true);
	                }
	            }

	            //Broodwar->drawCircleMap(Minerals[0].posTrick.x(),Minerals[0].posTrick.y(),2,Colors::Purple,true);

	            //Prebot.Broodwar.drawCircleMap(Mineral.Instance().CCtrick.getX(),Mineral.Instance().CCtrick.getY(),2,Color.Brown,true);
	            //for(int i=0; i< MineralManager.Instance().minerals.size(); i++){
	            //Prebot.Broodwar.drawTextMap( minr.mineral.getPosition().getX(),minr.mineral.getPosition().getY(), "(" + (int)(MineralManager.Instance().minerals.get(i).MinToCC) + (int)(MineralManager.Instance().minerals.get(i).CCToMin)  + ")");
	            //}
	        }

	    }

	    private void drawStrategy(){
	    	String upgradeString = "";
	    	for (MetaType metaType : StrategyBoard.upgrade) {
	            upgradeString += metaType.getName() + " > ";
	        }
	    	
	    	int y = 10;
	    	Race enemyRace = PlayerUtils.enemyRace();
	    	EnemyStrategy strategy = StrategyBoard.currentStrategy;
	    	int phase = EnemyStrategyAnalyzer.Instance().getPhase();
	    	
	    	//setting
	    	UxDrawConfig uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","", "[" + strategy.name() + " ...(phase " + phase + ")]");
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","UPGRADE",upgradeString);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","EXPANSION",StrategyBoard.expansionOption);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","MISSION",strategy.missionTypeList);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","", ClueManager.Instance().getClueInfoList());
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","", strategy.buildTimeMap);
	    	drawStrategyLeftList.add(uxDrawConfig);
	    	
	    	
	    	
	    	
	        y = 10;
	        for (EnemyStrategy enemyStrategy : EnemyStrategy.values()) {
	            if (enemyStrategy.name().startsWith(enemyRace.toString().toUpperCase())) {
	            	uxDrawConfig = UxDrawConfig.newInstanceObjectType("R","",""+enemyStrategy.name(),UxColor.CHAR_YELLOW);
	    	    	drawStrategyRightList.add(uxDrawConfig);
	            }
	        }
	    }

		private void drawEnemyAirDefenseRange() {
	        List<UnitInfo> airDefenseEuiList = UnitUtils.getEnemyUnitInfoList(EnemyUnitVisibleStatus.ALL, UnitTypeUtils.enemyAirDefenseUnitType());
	        for (UnitInfo eui : airDefenseEuiList) {
	            if (eui.getType() == UnitType.Terran_Bunker) {
	                Broodwar.drawCircleMap(eui.getLastPosition(), Broodwar.enemy().weaponMaxRange(UnitType.Terran_Marine.groundWeapon()) + 96, Color.White);
	            } else {
	                Broodwar.drawCircleMap(eui.getLastPosition(), eui.getType().airWeapon().maxRange(), Color.White);
	            }
	        }
	        List<UnitInfo> wraithKillerEuiList = UnitUtils.getEnemyUnitInfoList(EnemyUnitVisibleStatus.ALL, UnitTypeUtils.wraithKillerUnitType());
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
	            Broodwar.drawTextMap(position.getX(), position.getY() - 10, color + "leader#" + airForceTeam.leaderUnit.getID());

	            Position targetPosition = new Position(airForceTeam.getTargetPosition().getX(), airForceTeam.getTargetPosition().getY() - 10);
	            Broodwar.drawTextMap(targetPosition, UxColor.CHAR_RED + "*" + airForceTeam.leaderUnit.getID());
	            Broodwar.drawTextScreen(L, y += 15, "" + UxColor.CHAR_YELLOW + airForceTeam.toString());
	        }
	    	
	  /*      Broodwar.drawTextScreen(L, y += 15, "Defense Mode? " + AirForceManager.Instance().isAirForceDefenseMode());
	        Broodwar.drawTextScreen(L, y += 15, "strike level=" + AirForceManager.Instance().getStrikeLevel());
	        Broodwar.drawTextScreen(L, y += 15, "total achievement=" + AirForceManager.Instance().getAchievementEffectiveFrame());
	        Broodwar.drawTextScreen(L, y += 15, "accumulated achievement=" + AirForceManager.Instance().getAccumulatedAchievement());
	        Broodwar.drawTextScreen(L, y += 15, "wraith count=" + StrategyBoard.wraithCount);*/
	    }

	    private void drawVulturePolicy() {
	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("R","","[vulture policy]",UxColor.CHAR_WHITE);
	    	drawStrategyRightList.add(uxDrawConfig);

	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("R","","checkerMaxNumber=" + StrategyBoard.checkerMaxNumber,UxColor.CHAR_WHITE);
	    	drawStrategyRightList.add(uxDrawConfig);

	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("R","","spiderMineNumberPerPosition=" + StrategyBoard.spiderMineNumberPerPosition,UxColor.CHAR_WHITE);
	        drawStrategyRightList.add(uxDrawConfig);

	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("R","","spiderMineNumberPerGoodPosition=" + StrategyBoard.spiderMineNumberPerGoodPosition,UxColor.CHAR_WHITE);
	        drawStrategyRightList.add(uxDrawConfig);

	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("R","","watcherMinePositionLevel=" + StrategyBoard.watcherMinePositionLevel,UxColor.CHAR_WHITE);
	        drawStrategyRightList.add(uxDrawConfig);
	    }

	    private void drawEnemyBaseToBaseTime() {
	    	uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","","campPosition : " + StrategyBoard.campPosition + " / " + StrategyBoard.campType,UxColor.CHAR_WHITE);
	        drawStrategyLeftList.add(uxDrawConfig);

	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","","mainPosition : " + StrategyBoard.mainPosition,UxColor.CHAR_WHITE);
	        drawStrategyLeftList.add(uxDrawConfig);
	        
	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","","watcherPosition : " + StrategyBoard.watcherPosition,UxColor.CHAR_WHITE);
	        drawStrategyLeftList.add(uxDrawConfig);

	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","","mainSquadCenter : " + StrategyBoard.mainSquadCenter,UxColor.CHAR_WHITE);
	        drawStrategyLeftList.add(uxDrawConfig);

	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","","enemyGroundSquadPosition : " + StrategyBoard.nearGroundEnemyPosition + " / " + StrategyBoard.enemyUnitStatus,UxColor.CHAR_WHITE);
	        drawStrategyLeftList.add(uxDrawConfig);
	        
	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","","enemyAirSquadPosition : " + StrategyBoard.nearAirEnemyPosition,UxColor.CHAR_WHITE);
	        drawStrategyLeftList.add(uxDrawConfig);

	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","","enemyDropEnemyPosition : " + StrategyBoard.dropEnemyPosition,UxColor.CHAR_WHITE);
	        drawStrategyLeftList.add(uxDrawConfig);

	        Position enemyBasePosition = null;
	        Position enemyExpansionPosition = null;
	        if (BaseUtils.enemyMainBase() != null) {
	            enemyBasePosition = BaseUtils.enemyMainBase().getPosition();
	            enemyExpansionPosition = BaseUtils.enemyMainBase().getPosition();

	        }
	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","","enemyMainBase : " + enemyBasePosition,UxColor.CHAR_WHITE);
	        drawStrategyLeftList.add(uxDrawConfig);

	        uxDrawConfig = UxDrawConfig.newInstanceObjectType("L","","enemyFirstExpansion : " + enemyExpansionPosition,UxColor.CHAR_WHITE);
	        drawStrategyLeftList.add(uxDrawConfig);
//	        if (StrategyBoard.enemyBaseExpected != null) {
//	            Broodwar.drawTextScreen(10, y += 15, "enemyMainBase (Expect) : " + StrategyBoard.enemyBaseExpected.getPosition());
//	        }
//			for (Entry<UnitType, Integer> unitType : InformationManager.Instance().baseToBaseUnit.entrySet()) {
//				Prebot.Broodwar.drawTextScreen(20, y += 10, "" + UxColor.CHAR_YELLOW + unitType.getKey() + " : " + unitType.getValue());
//			}
	    }

	    private void drawPositionInformation() {

	        if (StrategyBoard.mainSquadLeaderPosition != null) {
	            Broodwar.drawTextMap(DrawingUtils.positionAdjusted(StrategyBoard.mainSquadLeaderPosition, 0, -20), UxColor.CHAR_WHITE + "V");
	        }
	        if (StrategyBoard.campPosition.equals(StrategyBoard.mainPosition)) {
	            Broodwar.drawTextMap(StrategyBoard.campPosition, UxColor.CHAR_ORANGE + "camp & main");
	        } else {
	            if (StrategyBoard.campPosition != null) {
	                Broodwar.drawTextMap(StrategyBoard.campPosition, UxColor.CHAR_YELLOW + "camp");
	            }
	            if (StrategyBoard.mainPosition != null) {
	                Broodwar.drawTextMap(DrawingUtils.positionAdjusted(StrategyBoard.mainPosition, 0, -10), UxColor.CHAR_RED + "main");
	            }
	        }
	        if (StrategyBoard.campPositionSiege != null) {
	            Broodwar.drawTextMap(StrategyBoard.campPositionSiege, UxColor.CHAR_YELLOW + "camp (siege)");
	        }
	        if (StrategyBoard.watcherPosition != null) {
	            Broodwar.drawTextMap(DrawingUtils.positionAdjusted(StrategyBoard.watcherPosition, 0, -20), UxColor.CHAR_BLUE + "watcherPos");
	        }
	        if (StrategyBoard.mainSquadCenter != null) {
	            Broodwar.drawTextMap(StrategyBoard.mainSquadCenter, "mainSqCntr");
	            Broodwar.drawCircleMap(StrategyBoard.mainSquadCenter.getX(), StrategyBoard.mainSquadCenter.getY(), StrategyBoard.mainSquadCoverRadius, Color.Cyan);
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
	        if (PositionUtils.myReadyToPosition() != null) {
	            Broodwar.drawTextMap(PositionUtils.myReadyToPosition(), "myReadyTo");
	        }
	        if (PositionUtils.enemyReadyToPosition() != null) {
	            Broodwar.drawTextMap(PositionUtils.enemyReadyToPosition(), "enemyReadyTo");
	        }
//			if (VultureTravelManager.Instance().getTravelSites() != null) {
//				for (TravelSite site : VultureTravelManager.Instance().getTravelSites()) {
//					Broodwar.drawTextMap(site.baseLocation.getPosition(), "travel site\n" + site);
//				}
//			}
	    }

	    private void drawCCtoScvCount() {

	        int y = 100;
	        for (Unit depot : UnitUtils.getCompletedUnitList(UnitType.Terran_Command_Center)) {
	            // update workerData with the new job
	            Broodwar.drawTextScreen(R, y, "depot.getID() : " + depot.getID() + " cnt : " + WorkerData.depotWorkerCount.get(depot.getID()));
	            y += 10;
	        }
	    }

	    /// turret 건설 지점의 반경 표시
	    public void drawTurretMap() {
	        BaseLocation myBase = BaseUtils.myMainBase();
	        BaseLocation myFirstExpansion = BaseUtils.myFirstExpansion();
	        Chokepoint myFirstChoke = ChokeUtils.myFirstChoke();
	        Chokepoint mySecondChoke = ChokeUtils.mySecondChoke();

	        int turretCount = UnitUtils.getCompletedUnitCount(UnitType.Terran_Missile_Turret);

	        Position firstChokeMainHalf = new Position((myBase.getPosition().getX() + myFirstChoke.getX() * 2) / 3 - 60,
	                (myBase.getPosition().getY() + myFirstChoke.getY() * 2) / 3 - 60);

	        Position firstChokeExpHalf = new Position((myFirstExpansion.getPosition().getX() * 2 + myFirstChoke.getX()) / 3,
	                (myFirstExpansion.getPosition().getY() * 2 + myFirstChoke.getY()) / 3);

////			Position betweenChoke = new Position((myFirstChoke.getX() * 2 + myFirstChoke.getX()) / 3,
////					(mySecondChoke.getY() * 2 + mySecondChoke.getY()) / 3);
////			

//			Broodwar.drawTextMap(firstChokeExpHalf.getX() + 20, firstChokeExpHalf.getY() + 10, "(" + (int) (firstChokeExpHalf.getX()) + ", " + (int) (firstChokeExpHalf.getY()) + ")");
//			
//			Broodwar.drawCircleMap(firstChokeExpHalf, 150, Color.Orange, false);
//			
//			Broodwar.drawCircleMap(firstChokeExpHalf, 150 + turretCount * 15, Color.Orange, false);
//			
//			Broodwar.drawTextMap(mySecondChoke.getCenter().getX() + 20, mySecondChoke.getCenter().getY() + 10, "(" + (int) (mySecondChoke.getCenter().getX()) + ", " + (int) (mySecondChoke.getCenter().getY()) + ")");
//			
//			Broodwar.drawCircleMap(mySecondChoke.getCenter(), 150, Color.Cyan, false);
//			
//			Broodwar.drawCircleMap(mySecondChoke.getCenter(), 150 + turretCount * 15, Color.Cyan, false);
//			
//			Position betweenChoke2 = Position.None;
//			
//			if (MapUtils.getMapSpecificInformation().getMap() == GameMap.FIGHTING_SPIRITS) {
//				betweenChoke2 = new Position((firstChokeMainHalf.getX() * 4 + mySecondChoke.getX() * 7) / 11,
//				(firstChokeMainHalf.getY() * 4 + mySecondChoke.getY() * 7) / 11);
//			}else {
//				betweenChoke2 = new Position((firstChokeMainHalf.getX() * 3 + mySecondChoke.getX() * 4) / 7,
//				(firstChokeMainHalf.getY() * 4 + mySecondChoke.getY() * 7) / 11);
//			}
//			
////			Position betweenChoke2 = new Position((firstChokeMainHalf.getX() * 4 + mySecondChoke.getX() * 7) / 11,
////					(firstChokeMainHalf.getY() * 4 + mySecondChoke.getY() * 7) / 11);
//			
//			Broodwar.drawTextMap(betweenChoke2.getX() + 20, betweenChoke2.getY() + 10, "(" + (int) (betweenChoke2.getX()) + ", " + (int) (betweenChoke2.getY()) + ")");
//			
//			Broodwar.drawCircleMap(betweenChoke2, 120, Color.White, false);
//			
//			Broodwar.drawCircleMap(betweenChoke2, 120 + turretCount * 15, Color.White, false);
//			
////			radius1 + turretCount * 15

	    }
	    private void addDrawStrategyListOrigin() throws IllegalArgumentException, IllegalAccessException{
			// TODO Auto-generated method stub
	    	if(!drawStrategyListOrigin[uxOption].isEmpty()){
	    		UxDrawConfig uxDrawConfig;
	    		for (UxDrawConfig drwaConfig : drawStrategyListOrigin[uxOption]) {
	    			drwaConfig.setClazz(drwaConfig.getKey());
	    			Class<?> c = drwaConfig.getClazz();
	    			if(drwaConfig.getValue() != null){
	    				Field fld = null;
	    				try{
	    					fld = c.getDeclaredField(drwaConfig.getValue());
	    					if (!fld.isAccessible()) fld.setAccessible(true);
	    					fld.get(drwaConfig.getValue());


	    				} catch (Exception e) {
	    					continue;
	    				}
		        		uxDrawConfig = UxDrawConfig.newInstanceObjectType(fld.getName(),fld.get(c).toString(),drwaConfig.getColor());
		        		uxDrawConfig.setPos(drwaConfig.getPos());
		        		if(drwaConfig.getPos() == UxDrawConfig.posMap.get("L")){
		        			drawStrategyLeftList.add(uxDrawConfig);
		        		}else if(drwaConfig.getPos() == UxDrawConfig.posMap.get("M")){
		        			drawStrategyMidList.add(uxDrawConfig);
		        		}else if(drwaConfig.getPos() == UxDrawConfig.posMap.get("R")){
		        			drawStrategyRightList.add(uxDrawConfig);
		        		}else{
		        			drawStrategyLeftList.add(uxDrawConfig);
		        		}
	    			}
	        		
				}
	    	}
		}
	    // 600 * 300
	    private void drawStrategyList() {
			// TODO Auto-generated method stub
	    	int nextLy = 20, nextMy = 20, nextRy = 20;
//	    	nextLy = nextMy = nextRy = 20;
	    	
	    	for(UxDrawConfig uxDraw : drawStrategyLeftList){
	    		int lineCnt = 1;
	    	    int fromIndex = -1;
	    	    while ((fromIndex = uxDraw.getClassFieldName().indexOf("\n", fromIndex + 1)) >= 0) {
	    	      lineCnt++;
	    	    }
	   			Broodwar.drawTextScreen(uxDraw.getPos(), nextLy, uxDraw.getColor() + uxDraw.getClassFieldName());
//	   			y += (lineCnt*12);
	   			nextLy += (lineCnt*12);
	    	}
	    	
	    	for(UxDrawConfig uxDraw : drawStrategyMidList){
	    		int lineCnt = 1;
	    	    int fromIndex = -1;
	    	    while ((fromIndex = uxDraw.getClassFieldName().indexOf("\n", fromIndex + 1)) >= 0) {
	    	      lineCnt++;
	    	    }
	   			Broodwar.drawTextScreen(uxDraw.getPos(), nextMy, uxDraw.getColor() + uxDraw.getClassFieldName());
//	   			y += (lineCnt*12);
	   			nextMy += (lineCnt*12);
	    	}
	    	
	    	for(UxDrawConfig uxDraw : drawStrategyRightList){
	    		int lineCnt = 1;
	    	    int fromIndex = -1;
	    	    while ((fromIndex = uxDraw.getClassFieldName().indexOf("\n", fromIndex + 1)) >= 0) {
	    	      lineCnt++;
	    	    }
	   			Broodwar.drawTextScreen(uxDraw.getPos(), nextRy, uxDraw.getColor() + uxDraw.getClassFieldName());
//	   			y += (lineCnt*12);
	   			nextRy += (lineCnt*12);
	    	}
	    	
	    	if(TimeUtils.getFrame() % 7 == 0){
	    		Broodwar.drawTextScreen(L, (nextLy + 24),  " ");
	    		Broodwar.drawTextScreen(M, (nextMy + 24),  " ");
	    		Broodwar.drawTextScreen(R, (nextRy + 24),  " ");
	    	}else{
	    		Broodwar.drawTextScreen(L, (nextLy + 24),  " : ");
	    		Broodwar.drawTextScreen(M, (nextMy + 24),  " : ");
	    		Broodwar.drawTextScreen(R, (nextRy + 24),  " : ");
//	    		Broodwar.drawTextScreen(20, nextY+12, "");
	    	}
		}
	    
	    /*
	     * 사용자 별 화면 */
	    
	    public void getSMDisplay(){
			if (uxOption == 0) {
				clearList();
				drawDebugginUxMenu();
				drawUxInfo();
	        } else if (uxOption == 1) {
            	UXManager.Instance().update();
            	clearList();
                // 미네랄PATH
            } else if (uxOption == 2) {
            	 clearList();
            	 drawGameInformationOnScreen(5, 5);
                 drawBWTAResultOnMap();
                 drawBuildOrderQueueOnScreen(500, 50);
                 drawBuildStatusOnScreen(370, 50);
                 drawReservedBuildingTilesOnMap();
                 drawTilesToAvoidOnMap();
                 drawWorkerMiningStatusOnMap();
                 drawUnitTargetOnMap();
                 drawnextPoints();
                 //drawTurretMap();
                 drawManagerTimeSpent();
                 drawConstructionQueueOnScreenAndMap();

                 // draw tile position of mouse cursor
                 int mouseX = Broodwar.getMousePosition().getX() + Broodwar.getScreenPosition().getX();
                 int mouseY = Broodwar.getMousePosition().getY() + Broodwar.getScreenPosition().getY();
                 Broodwar.drawTextMap(mouseX + 20, mouseY,
                         "(" + (int) (mouseX / 32) + ", " + (int) (mouseY / 32) + ")");
                 Broodwar.drawTextMap(mouseX + 20, mouseY + 10, "(" + (int) (mouseX) + ", " + (int) (mouseY) + ")");

            } else if (uxOption == 3) {
            	clearList();
            	drawStrategy();
                // drawCCtoScvCount();
            } else if (uxOption == 4) {
            	clearList();
            	drawEnemyBuildTimer();
            } else if (uxOption == 5) {
            	 clearList();
            	 drawSquadInfoOnMap();
                 drawManagerTimeSpent();
                 drawDecision();
                 drawEnemyAirDefenseRange();
                 drawAirForceInformation();
                 drawVulturePolicy();
            } else if (uxOption == 6) {
            	clearList();
                drawEnemyBaseToBaseTime();
            } else if (uxOption == 7) {
            	clearList();
            	drawBigWatch();
                drawManagerTimeSpent();
            } else if (uxOption == 8) {
            	clearList();
            	drawTurretMap();
                drawTilesToAvoidOnMap();
                drawReservedBuildingTilesOnMap();
            } else if (uxOption == 9) {
            	//drawExpectedResource();
            	//drawExpectedResource2();
            	
            }
            
            drawMineralIdOnMap();
            drawUnitIdOnMap();
            drawPositionInformation();
            drawTimer();
            drawPathData();
            drawSquadUnitTagMap();
            
            addDrawStrategyListOrigin();	            
            drawStrategyList();

            clearDecisionListForUx();
	    }
		public void getJwDisplay() throws Exception {
		}


	}