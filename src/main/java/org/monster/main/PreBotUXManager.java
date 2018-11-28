package org.monster.main;

import bwapi.Bullet;
import bwapi.BulletType;
import bwapi.Color;
import bwapi.Force;
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
import org.monster.board.StrategyBoard;
import org.monster.build.base.BuildManager;
import org.monster.build.base.BuildOrderItem;
import org.monster.build.base.ConstructionManager;
import org.monster.build.base.ConstructionPlaceFinder;
import org.monster.build.base.ConstructionTask;
import org.monster.build.initialProvider.InitialBuildProvider;
import org.monster.build.provider.BuildQueueProvider;
import org.monster.common.LagObserver;
import org.monster.common.MapGrid;
import org.monster.common.MetaType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.constant.CommonConfig;
import org.monster.common.debugger.BigWatch;
import org.monster.common.debugger.UxColor;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.InfoUtils;
import org.monster.common.util.InformationManager;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.PositionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.decisionMakers.constant.StrategyCode;
import org.monster.decisionMakers.decisionTypes.EnemyStrategy;
import org.monster.macro.AttackDecisionMaker;
import org.monster.macro.EnemyCommandInfo;
import org.monster.macro.util.MutableFloat;
import org.monster.micro.CombatManager;
import org.monster.micro.MicroDecision;
import org.monster.micro.Minerals;
import org.monster.micro.squad.Squad;
import org.monster.micro.squad.WatcherSquad;
import org.monster.strategy.StrategyManager;
import org.monster.strategy.manage.AirForceManager;
import org.monster.strategy.manage.AirForceTeam;
import org.monster.strategy.manage.ClueManager;
import org.monster.strategy.manage.EnemyBuildTimer;
import org.monster.strategy.manage.StrategyAnalyseManager;
import org.monster.strategy.manage.VultureTravelManager;
import org.monster.worker.WorkerData;
import org.monster.worker.WorkerManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

/// 봇 프로그램 개발의 편의성 향상을 위해 게임 화면에 추가 정보들을 표시하는 class<br>
/// 여러 Manager 들로부터 정보를 조회하여 Screen 혹은 Map 에 정보를 표시합니다
public class PreBotUXManager {

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

    private UnitType factorySelected = UnitType.None;

    private Map<Integer, MicroDecision> decisionListForUx = new HashMap<>();

    /// static singleton 객체를 리턴합니다
    public static PreBotUXManager Instance() {
        return instance;
    }

    public void setUxOption(int uxOption) {
        this.uxOption = uxOption;
    }

    /// 경기가 시작될 때 일회적으로 추가 정보를 출력합니다
    public void onStart() {
    }

    /// 경기 진행 중 매 프레임마다 추가 정보를 출력하고 사용자 입력을 처리합니다
    public void update() {
        if (uxOption == 0) {
            UXManager.Instance().update();
//			drawDebugginUxMenu();
        } else {

            if (uxOption == 1) {
                drawGameInformationOnScreen(5, 5);
                drawBWTAResultOnMap();
                drawBuildOrderQueueOnScreen(500, 50);
                drawBuildStatusOnScreen(370, 50);
                drawConstructionQueueOnScreenAndMap(200, 150);
                drawReservedBuildingTilesOnMap();
                drawTilesToAvoidOnMap();
                drawWorkerMiningStatusOnMap();
                drawUnitTargetOnMap();
                drawnextPoints();
                drawTurretMap();
                drawManagerTimeSpent(490, 200);

                // draw tile position of mouse cursor
                int mouseX = Monster.Broodwar.getMousePosition().getX() + Monster.Broodwar.getScreenPosition().getX();
                int mouseY = Monster.Broodwar.getMousePosition().getY() + Monster.Broodwar.getScreenPosition().getY();
                Monster.Broodwar.drawTextMap(mouseX + 20, mouseY,
                        "(" + (int) (mouseX / CommonConfig.UxConfig.TILE_SIZE) + ", " + (int) (mouseY / CommonConfig.UxConfig.TILE_SIZE) + ")");
                Monster.Broodwar.drawTextMap(mouseX + 20, mouseY + 10, "(" + (int) (mouseX) + ", " + (int) (mouseY) + ")");
                // 미네랄PATH
            } else if (uxOption == 2) {
                drawStrategy();

            } else if (uxOption == 3) {
                drawEnemyBuildTimer();
                // drawCCtoScvCount();
            } else if (uxOption == 4) {
                drawSquadInfoOnMap(20, 30);
                drawManagerTimeSpent(490, 200);
                drawDecision();
                drawEnemyAirDefenseRange();
                drawAirForceInformation();
                drawVulturePolicy();

            } else if (uxOption == 5) {
                drawEnemyBaseToBaseTime();

            } else if (uxOption == 6) {
                drawBigWatch();
                drawManagerTimeSpent(490, 200);
            } else if (uxOption == 7) {
                drawTurretMap();
                drawTilesToAvoidOnMap();
                drawReservedBuildingTilesOnMap();
            } else if (uxOption == 8) {
                drawExpectedResource();
                //drawExpectedResource2();
            }

            drawMineralIdOnMap();
            drawUnitIdOnMap();
            drawPositionInformation();
            drawTimer();
            drawPathData();
            drawSquadUnitTagMap();
        }

        clearDecisionListForUx();

//		if (TimeUtils.executeRotation(1, 11)) {
////			System.out.println("execute debugBuildLocationSet");
//			ConstructionPlaceFinder.Instance().debugBuildLocationSet();
//		}
//		
//		if (TimeUtils.executeRotation(1, 233)) {
////			System.out.println("execute debugBuildLocationPrint");
//			ConstructionPlaceFinder.Instance().debugBuildLocationPrint();
//		}
    }


    private void drawExpectedResource2() {
        int m = 190;
        //Monster.Broodwar.drawTextScreen(190, 10, "EnemyPredictedUnitLIst: " + AttackDecisionMaker.Instance().predictedTotalEnemyAttackUnit.size());
        System.out.println("EnemyPredictedUnitLIst: " + AttackDecisionMaker.Instance().predictedTotalEnemyAttackUnit.size());
        int l = 10;
        for (Entry<UnitType, MutableFloat> enenmy : AttackDecisionMaker.Instance().predictedTotalEnemyAttackUnit.entrySet()) {
            float cnt = enenmy.getValue().get();
            UnitType unitType = enenmy.getKey();
            System.out.println(unitType.toString().substring(7, 14) + " : " + cnt);
            //Monster.Broodwar.drawTextScreen(m, l+=10, unitType.toString().substring(7, 14)+" : "+cnt);
        }
    }

    private void drawExpectedResource() {
        Map<UnitInfo, EnemyCommandInfo> enemyCommandInfoMap = AttackDecisionMaker.Instance().enemyResourceDepotInfoMap;

        int y = 0;
        Monster.Broodwar.drawTextScreen(10, y += 10, "this mymineral  : " + Monster.Broodwar.self().gatheredMinerals());
        Monster.Broodwar.drawTextScreen(10, y += 10, "total enemy cnt : " + enemyCommandInfoMap.size());
        Monster.Broodwar.drawTextScreen(10, y += 10, "mygas           : " + Monster.Broodwar.self().gatheredGas());
        Monster.Broodwar.drawTextScreen(10, y += 10, "mywrkcnt(real)  : " + Monster.Broodwar.self().completedUnitCount(UnitType.Terran_SCV));
        Monster.Broodwar.drawTextScreen(10, y += 10, "frame ==== " + TimeUtils.getFrame());

        y = drawCalculation(10, y);


        y += 10;
        if (enemyCommandInfoMap.size() == 0) {
            Monster.Broodwar.drawTextScreen(10, y += 10, "No enemy base info");
        } else {

            int k = 1;
            int x = 10;
            int temp = y;
            for (Map.Entry<UnitInfo, EnemyCommandInfo> entry : enemyCommandInfoMap.entrySet()) {
                y = temp;
                UnitInfo unitInfo = entry.getKey();
                EnemyCommandInfo enemyCommandInfo = entry.getValue();

                if (AttackDecisionMaker.Instance().skipResourceDepot.size() == 0) {
                    Monster.Broodwar.drawTextScreen(10, y += 10, "skipped base : no skipped base");
                } else {
                    for (UnitInfo skip : AttackDecisionMaker.Instance().skipResourceDepot) {
                        Monster.Broodwar.drawTextScreen(10, y += 10, "skipped base : " + skip.getLastPosition());
                    }
                }

                Monster.Broodwar.drawTextScreen(x, y += 10, k++ + " base" + unitInfo.getLastPosition() + ", " + enemyCommandInfo.mineralCalculator.getMineralCount());
                Monster.Broodwar.drawTextScreen(x, y += 10, "isMainBase      : " + enemyCommandInfo.isMainBase);
                Monster.Broodwar.drawTextScreen(x, y += 10, "has gas         : " + enemyCommandInfo.gasCalculator.hasGasBuilding());
                Monster.Broodwar.drawTextScreen(x, y += 10, "mineral(real)   : " + ((int) enemyCommandInfo.mineralCalculator.getFullCheckMineral() + (int) 50));
                Monster.Broodwar.drawTextScreen(x, y += 10, "mineral(r+p):   : " + ((int) enemyCommandInfo.uxmineral + (int) 50));
                Monster.Broodwar.drawTextScreen(x, y += 10, "gas(real)       : " + enemyCommandInfo.gasCalculator.getRealGas());
                Monster.Broodwar.drawTextScreen(x, y += 10, "gas(r+p)        : " + enemyCommandInfo.gasCalculator.getGas());
                Monster.Broodwar.drawTextScreen(x, y += 10, "fullWorkerFrame : " + enemyCommandInfo.fullWorkerFrame);
                Monster.Broodwar.drawTextScreen(x, y += 10, "halfWorkerFrame : " + enemyCommandInfo.halfWorkerFrame);
                Monster.Broodwar.drawTextScreen(x, y += 10, "wrkcnt(real)    : " + enemyCommandInfo.workerCounter.realWorkerCount);
                Monster.Broodwar.drawTextScreen(x, y += 10, "wrkcnt(r+p)     : " + ((int) enemyCommandInfo.workerCounter.getWorkerCount(enemyCommandInfo.lastFullCheckFrame)));
                Monster.Broodwar.drawTextScreen(x, y += 10, "fwrkcnt         : " + ((int) enemyCommandInfo.lastFullCheckWorkerCount));
                Monster.Broodwar.drawTextScreen(x, y += 10, "last full check : " + enemyCommandInfo.lastFullCheckFrame);
                x += 140;
            }

        }

    }

    private int drawCalculation(int x, int y) {
        y += 10;
        Monster.Broodwar.drawTextScreen(x, y += 10, UxColor.CHAR_RED + "Decision : " + AttackDecisionMaker.Instance().decision + ", phase3: " + StrategyAnalyseManager.Instance().getPhase() + ", strategy" + StrategyBoard.currentStrategy.name());
        Monster.Broodwar.drawTextScreen(x, y += 10, "MineralToPredict: " + AttackDecisionMaker.Instance().UXMineralToPredict);
        Monster.Broodwar.drawTextScreen(x, y += 10, "GasToPredict    : " + AttackDecisionMaker.Instance().UXGasToPredict);
        Monster.Broodwar.drawTextScreen(x, y += 10, "MineralMinus:   : " + AttackDecisionMaker.Instance().UXMinusMineralToPredict);
        Monster.Broodwar.drawTextScreen(x, y += 10, "GasMinus        : " + AttackDecisionMaker.Instance().UXMinusGasToPredict);

        Monster.Broodwar.drawTextScreen(x, y += 10, "my point        : " + AttackDecisionMaker.Instance().tempMypoint);
        Monster.Broodwar.drawTextScreen(x, y += 10, "enemy point     : " + AttackDecisionMaker.Instance().tempEnemypoint);
        return y;
    }

    private void drawDecision() {
        for (Integer unitId : decisionListForUx.keySet()) {
            Unit unit = Monster.Broodwar.getUnit(unitId);
            MicroDecision decision = decisionListForUx.get(unitId);
            Monster.Broodwar.drawTextMap(unit.getPosition(), UxColor.CHAR_YELLOW + decision.toString());
            if (decision.eui != null) {
                Monster.Broodwar.drawLineMap(unit.getPosition(), decision.eui.getLastPosition(), Color.Yellow);
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
        Monster.Broodwar.drawTextScreen(170, 353, battleColor + StrategyBoard.mainSquadMode.toString() + ": " + TimeUtils.framesToTimeString(TimeUtils.elapsedFrames()) + "(" + TimeUtils.elapsedFrames() + ")");

        char apmColor = UxColor.CHAR_WHITE;
        int apm = Monster.Broodwar.getAPM();
        if (apm > 3000) {
            apmColor = UxColor.CHAR_RED;
        } else if (apm > 2000) {
            apmColor = UxColor.CHAR_YELLOW;
        } else if (apm > 1000) {
            apmColor = UxColor.CHAR_GREEN;
        } else {
            apmColor = UxColor.CHAR_WHITE;
        }
        Monster.Broodwar.drawTextScreen(395, 353, apmColor + "APM : " + Monster.Broodwar.getAPM());
    }

    private void drawEnemyBuildTimer() {

        Map<UnitType, Integer> buildTimeExpectMap = EnemyBuildTimer.Instance().buildTimeExpectMap;
        Map<UnitType, Integer> buildTimeMinimumMap = EnemyBuildTimer.Instance().buildTimeMinimumMap;
        Set<UnitType> buildTimeCertain = EnemyBuildTimer.Instance().buildTimeCertain;

        int y = 20;
        Monster.Broodwar.drawTextScreen(20, y += 15, "engine Build Frame : " + TimeUtils.framesToTimeString(StrategyBoard.engineeringBayBuildStartFrame));
        Monster.Broodwar.drawTextScreen(20, y += 15, "turret Build Frame : " + TimeUtils.framesToTimeString(StrategyBoard.turretBuildStartFrame));
        Monster.Broodwar.drawTextScreen(20, y += 15, "turret Need  Frame : " + TimeUtils.framesToTimeString(StrategyBoard.turretNeedFrame));
        y += 15;

        Monster.Broodwar.drawTextScreen(20, y += 15, "academy Build Frame : " + TimeUtils.framesToTimeString(StrategyBoard.academyFrame));
        Monster.Broodwar.drawTextScreen(20, y += 15, "comsat Build Frame : " + TimeUtils.framesToTimeString(StrategyBoard.academyFrame + UnitType.Terran_Academy.buildTime()));
        y += 15;

        Monster.Broodwar.drawTextScreen(20, y += 15, "darkTemplarInMyBaseFrame : " + TimeUtils.framesToTimeString(EnemyBuildTimer.Instance().darkTemplarInMyBaseFrame));
        Monster.Broodwar.drawTextScreen(20, y += 15, "reaverInMyBaseFrame : " + TimeUtils.framesToTimeString(EnemyBuildTimer.Instance().reaverInMyBaseFrame));
        Monster.Broodwar.drawTextScreen(20, y += 15, "mutaliskInMyBaseFrame : " + TimeUtils.framesToTimeString(EnemyBuildTimer.Instance().mutaliskInMyBaseFrame));
        Monster.Broodwar.drawTextScreen(20, y += 15, "lurkerInMyBaseFrame : " + TimeUtils.framesToTimeString(EnemyBuildTimer.Instance().lurkerInMyBaseFrame));
        y += 15;

        for (UnitType unitType : buildTimeExpectMap.keySet()) {
            Integer buildTimeExpect = buildTimeExpectMap.get(unitType);
            if (buildTimeExpect != null && buildTimeExpect != CommonCode.UNKNOWN) {
                String expect = TimeUtils.framesToTimeString(buildTimeExpect);
                String minimum = "";
                Integer buildMinimum = buildTimeMinimumMap.get(unitType);
                if (buildMinimum != null && buildMinimum != CommonCode.UNKNOWN) {
                    minimum = TimeUtils.framesToTimeString(buildMinimum);
                }

                Monster.Broodwar.drawTextScreen(20, y += 15, unitType + " : " + expect + " - min: " + minimum + " (" + buildTimeCertain.contains(unitType) + ")");
            }
        }
    }

    private void drawDebugginUxMenu() {
        Monster.Broodwar.drawTextScreen(20, 20, "1. Default Information");
        Monster.Broodwar.drawTextScreen(20, 35, "2. Strategy Information");
        Monster.Broodwar.drawTextScreen(20, 50, "3. Position Finder Test");
        Monster.Broodwar.drawTextScreen(20, 65, "4. Air Micro Test");
        Monster.Broodwar.drawTextScreen(20, 80, "5. Unit Bast To Base");
    }

    // 게임 개요 정보를 Screen 에 표시합니다
    public void drawGameInformationOnScreen(int x, int y) {
//		Monster.Broodwar.drawTextScreen(x, y, white + "Players : ");
//		Monster.Broodwar.drawTextScreen(x + 50, y, Monster.Broodwar.self().getTextColor() + Monster.Broodwar.self().getName() + "(" + InformationManager.Instance().selfRace + ") " + white + " vs.  " +
//				InformationManager.Instance().enemyPlayer.getTextColor() + InformationManager.Instance().enemyPlayer.getName() + "(" + PlayerUtils.enemyRace() + ")");
//		y += 12;
//
//		Monster.Broodwar.drawTextScreen(x, y, white + "Map : ");
//		Monster.Broodwar.drawTextScreen(x + 50, y, white + Monster.Broodwar.mapFileName() + " (" + Monster.Broodwar.mapWidth() + " x " +  Monster.Broodwar.mapHeight() + " size)");
//		Monster.Broodwar.setTextSize();
//		y += 12;

//		Monster.Broodwar.drawTextScreen(x, y, white + "Time : ");
//		Monster.Broodwar.drawTextScreen(x + 50, y, "" + white + TimeUtils.getFrame());
//		Monster.Broodwar.drawTextScreen(x + 90, y, "" + white + (int)(TimeUtils.getFrame() / (23.8 * 60)) + ":" + (int)((int)(TimeUtils.getFrame() / 23.8) % 60));
//		y += 11;


        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + "Current Strategy : ");
        Monster.Broodwar.drawTextScreen(x + 100, y, "" + UxColor.CHAR_WHITE + StrategyBoard.currentStrategy.name());
        y += 11;

        String history = "";
        for (int i = StrategyBoard.strategyHistory.size() - 1; i >= 0; i--) {
            if (i == StrategyBoard.strategyHistory.size() - 3) {
                history = "... " + history;
                break;
            } else {
                history = StrategyBoard.strategyHistory.get(i).name() + " -> " + history;
            }
        }
        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + "Strategy History : ");
        Monster.Broodwar.drawTextScreen(x + 100, y, "" + UxColor.CHAR_WHITE + history);
        y += 11;

        int vultureCount = UnitUtils.getUnitCount(CommonCode.UnitFindRange.ALL, UnitType.Terran_Vulture);
        int tankCount = UnitUtils.getUnitCount(CommonCode.UnitFindRange.ALL, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode);
        int goliathCount = UnitUtils.getUnitCount(CommonCode.UnitFindRange.ALL, UnitType.Terran_Goliath);

//        UnitType selected = BuildQueueProvider.Instance().getFactoryUnitSelector().getSelected();
//        if (selected != UnitType.None) {
//            factorySelected = selected;
//        }


        Monster.Broodwar.drawTextScreen(x + 100, y + 5, UxColor.CHAR_TEAL + "" + vultureCount + "      " + tankCount + "        " + goliathCount);
        Monster.Broodwar.drawTextScreen(x, y, "" + UxColor.CHAR_WHITE + StrategyBoard.factoryRatio + ", selected=" + factorySelected);
        y += 11;

        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + "Wraith Count : ");
        Monster.Broodwar.drawTextScreen(x + 75, y, "" + UxColor.CHAR_WHITE + StrategyBoard.wraithCount + " / " + UnitUtils.getUnitCount(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Wraith));
        y += 11;

        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + "Valkyrie Count : ");
        Monster.Broodwar.drawTextScreen(x + 75, y, "" + UxColor.CHAR_WHITE + StrategyBoard.valkyrieCount + " / " + UnitUtils.getUnitCount(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Valkyrie));
        y += 11;

        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_RED + "MYKillScore : ");
        Monster.Broodwar.drawTextScreen(x + 70, y, "" + UxColor.CHAR_RED + Monster.Broodwar.self().getKillScore());
        y += 11;
        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_RED + "MYRazingScore : ");
        Monster.Broodwar.drawTextScreen(x + 85, y, "" + UxColor.CHAR_RED + Monster.Broodwar.self().getRazingScore());
        y += 11;
        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_PURPLE + "EnemyKillScore : ");
        Monster.Broodwar.drawTextScreen(x + 85, y, "" + UxColor.CHAR_PURPLE + Monster.Broodwar.enemy().getKillScore());
        y += 11;
        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_PURPLE + "EnemyRazingScore : ");
        Monster.Broodwar.drawTextScreen(x + 100, y, "" + UxColor.CHAR_PURPLE + Monster.Broodwar.enemy().getRazingScore());
        y += 11;
        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_TEAL + "Reserved Resource : " + ConstructionManager.Instance().getReservedMinerals() + " / " + ConstructionManager.Instance().getReservedGas());
        y += 11;
    }

    /// APM (Action Per Minute) 숫자를 Screen 에 표시합니다
    public void drawAPM(int x, int y) {
        int bwapiAPM = Monster.Broodwar.getAPM();
        Monster.Broodwar.drawTextScreen(x, y, "APM : " + bwapiAPM);
    }

    /// Players 정보를 Screen 에 표시합니다
    public void drawPlayers() {
        for (Player p : Monster.Broodwar.getPlayers()) {
            Monster.Broodwar.sendText("Player [" + p.getID() + "]: " + p.getName() + " is in force: " + p.getForce().getName());
        }
    }

    /// Player 들의 팀 (Force) 들의 정보를 Screen 에 표시합니다
    public void drawForces() {
        for (Force f : Monster.Broodwar.getForces()) {
            Monster.Broodwar.sendText("Force " + f.getName() + " has the following players:");
            for (Player p : f.getPlayers()) {
                Monster.Broodwar.sendText("  - Player [" + p.getID() + "]: " + p.getName());
            }
        }
    }

    /// Unit 의 HitPoint 등 추가 정보를 Map 에 표시합니다
    public void drawUnitExtendedInformationOnMap() {
        int verticalOffset = -10;

        for(UnitInfo ui : UnitUtils.getEnemyUnitInfoList()){

            UnitType type = ui.getType();
            int hitPoints = ui.getLastHealth();
            int shields = ui.getLastShields();

            Position pos = ui.getLastPosition();

            int left = pos.getX() - type.dimensionLeft();
            int right = pos.getX() + type.dimensionRight();
            int top = pos.getY() - type.dimensionUp();
            int bottom = pos.getY() + type.dimensionDown();

            // 적 유닛이면 주위에 박스 표시
            if (!Monster.Broodwar.isVisible(ui.getLastPosition().toTilePosition())) {
                Monster.Broodwar.drawBoxMap(new Position(left, top), new Position(right, bottom), Color.Grey, false);
                Monster.Broodwar.drawTextMap(new Position(left + 3, top + 4), ui.getType().toString());
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

                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Grey, true);
                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(ratioRight, hpBottom), hpColor, true);
                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Black, false);

                int ticWidth = 3;

                for (int i = left; i < right - 1; i += ticWidth) {
                    Monster.Broodwar.drawLineMap(new Position(i, hpTop), new Position(i, hpBottom), Color.Black);
                }
            }

            // 유닛의 Shield 남아있는 비율 표시
            if (!type.isResourceContainer() && type.maxShields() > 0) {
                double shieldRatio = (double) shields / (double) type.maxShields();

                int ratioRight = left + (int) ((right - left) * shieldRatio);
                int hpTop = top - 3 + verticalOffset;
                int hpBottom = top + 1 + verticalOffset;

                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Grey, true);
                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(ratioRight, hpBottom), Color.Blue, true);
                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Black, false);

                int ticWidth = 3;

                for (int i = left; i < right - 1; i += ticWidth) {
                    Monster.Broodwar.drawLineMap(new Position(i, hpTop), new Position(i, hpBottom), Color.Black);
                }
            }
        }

        // draw neutral units and our units
        for (Unit unit : Monster.Broodwar.getAllUnits()) {
            if (unit.getPlayer() == InformationManager.Instance().enemyPlayer) {
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

                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Grey, true);
                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(ratioRight, hpBottom), hpColor, true);
                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), hpColor.Black, false);

                int ticWidth = 3;

                for (int i = left; i < right - 1; i += ticWidth) {
                    Monster.Broodwar.drawLineMap(new Position(i, hpTop), new Position(i, hpBottom), Color.Black);
                }
            }

            // 유닛의 Shield 남아있는 비율 표시
            if (!unit.getType().isResourceContainer() && unit.getType().maxShields() > 0) {
                double shieldRatio = (double) unit.getShields() / (double) unit.getType().maxShields();

                int ratioRight = left + (int) ((right - left) * shieldRatio);
                int hpTop = top - 3 + verticalOffset;
                int hpBottom = top + 1 + verticalOffset;

                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Grey, true);
                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(ratioRight, hpBottom), Color.Blue, true);
                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Black, false);

                int ticWidth = 3;

                for (int i = left; i < right - 1; i += ticWidth) {
                    Monster.Broodwar.drawLineMap(new Position(i, hpTop), new Position(i, hpBottom), Color.Black);
                }
            }

            // Mineral / Gas 가 얼마나 남아있는가
            if (unit.getType().isResourceContainer() && unit.getInitialResources() > 0) {
                double mineralRatio = (double) unit.getResources() / (double) unit.getInitialResources();

                int ratioRight = left + (int) ((right - left) * mineralRatio);
                int hpTop = top + verticalOffset;
                int hpBottom = top + 4 + verticalOffset;

                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Grey, true);
                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(ratioRight, hpBottom), Color.Cyan, true);
                Monster.Broodwar.drawBoxMap(new Position(left, hpTop), new Position(right, hpBottom), Color.Black, false);

                int ticWidth = 3;

                for (int i = left; i < right - 1; i += ticWidth) {
                    Monster.Broodwar.drawLineMap(new Position(i, hpTop), new Position(i, hpBottom), Color.Black);
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
        //Monster.Broodwar.drawTextScreen(x, currentY,  white + " allUnitCount: " + Monster.Broodwar.self().allUnitCount(UnitType.AllUnits));
        //currentY += 10;

        // 아군 건설/훈련 완료한 유닛 숫자 합계
        //Monster.Broodwar.drawTextScreen(x, currentY,  white + " completedUnitCount: " + Monster.Broodwar.self().completedUnitCount(UnitType.AllUnits));
        //currentY += 10;

        // 아군 건설/훈련중인 유닛 숫자 합계
        //Monster.Broodwar.drawTextScreen(x, currentY,  white + " incompleteUnitCount: " + Monster.Broodwar.self().incompleteUnitCount(UnitType.AllUnits));
        //currentY += 10;

        // 아군 유닛 파괴/사망 숫자 누적값
        //Monster.Broodwar.drawTextScreen(x, currentY,  white + " deadUnitCount: " + Monster.Broodwar.self().deadUnitCount(UnitType.AllUnits));
        //currentY += 10;

        // 상대방 유닛을 파괴/사망 시킨 숫자 누적값
        //Monster.Broodwar.drawTextScreen(x, currentY,  white + " killedUnitCount: " + Monster.Broodwar.self().killedUnitCount(UnitType.AllUnits));
        //currentY += 10;

        //Monster.Broodwar.drawTextScreen(x, currentY,  white + " UnitScore: " + Monster.Broodwar.self().getUnitScore());
        //currentY += 10;
        //Monster.Broodwar.drawTextScreen(x, currentY,  white + " RazingScore: " + Monster.Broodwar.self().getRazingScore());
        //currentY += 10;
        //Monster.Broodwar.drawTextScreen(x, currentY,  white + " BuildingScore: " + Monster.Broodwar.self().getBuildingScore());
        //currentY += 10;
        //Monster.Broodwar.drawTextScreen(x, currentY,  white + " KillScore: " + Monster.Broodwar.self().getKillScore());
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
			Monster.Broodwar.drawBoxMap(p.getX() * 32, p.getY() * 32, p.getX() * 32 + 4 * 32, p.getY() * 32 + 3 * 32, Color.Blue);

			//draw a circle at each mineral patch
			// C++ : for (BWAPI.Unitset.iterator j = (*i).getStaticMinerals().begin(); j != (*i).getStaticMinerals().end(); j++)
			for(Unit unit : baseLocation.getStaticMinerals())
			{
				Position q = unit.getInitialPosition();
				Monster.Broodwar.drawCircleMap(q.getX(), q.getY(), 30, Color.Cyan);
			}

			//draw the outlines of vespene geysers
			// C++ : for (BWAPI.Unitset.iterator j = (*i).getGeysers().begin(); j != (*i).getGeysers().end(); j++)
			for(Unit unit :baseLocation.getGeysers() )
			{
				TilePosition q = unit.getInitialTilePosition();
				Monster.Broodwar.drawBoxMap(q.getX() * 32, q.getY() * 32, q.getX() * 32 + 4 * 32, q.getY() * 32 + 2 * 32, Color.Orange);
			}

			//if this is an island expansion, draw a yellow circle around the base location
			if (baseLocation.isIsland())
			{
				Monster.Broodwar.drawCircleMap(c, 80, Color.Yellow);
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
				Monster.Broodwar.drawLineMap(point1, point2, Color.Green);
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
				Monster.Broodwar.drawLineMap(point1, point2, Color.Red);
			}
		}*/
        int blueCount = 0;
        int cyanCount = 0;
        int orangeCount = 0;
//		int purpleCount = 0;

        if (hasSavedBWTAInfo == false) {
            for (BaseLocation baseLocation : BWTA.getBaseLocations()) {
                blueCount++;
//				purpleCount++;
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

//			purple = new int[purpleCount][4];
//			int purpleIndex = 0;

            for (BaseLocation baseLocation : BWTA.getBaseLocations()) {
                TilePosition p = baseLocation.getTilePosition();
                Position c = baseLocation.getPosition();

                blue[blueIndex][0] = p.getX() * 32;
                blue[blueIndex][1] = p.getY() * 32;
                blue[blueIndex][2] = p.getX() * 32 + 4 * 32;
                blue[blueIndex][3] = p.getY() * 32 + 3 * 32;
                blueIndex++;

//				purple[purpleIndex][0] = (p.getX()+4) * 32;
//				purple[purpleIndex][1] = (p.getY()+1) * 32;
//				purple[purpleIndex][2] = (p.getX()+4) * 32 + 2 * 32;
//				purple[purpleIndex][3] = (p.getY()+1) * 32 + 2 * 32;
//				purpleIndex++;

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

//			System.out.println(blueCount + " " + cyanCount + " " + orangeCount + " " + yellowCount + " " + greenCount + " " + redCount);
        }

        if (hasSavedBWTAInfo) {
            for (int i1 = 0; i1 < blue.length; i1++) {
                Monster.Broodwar.drawBoxMap(blue[i1][0], blue[i1][1], blue[i1][2], blue[i1][3], Color.Blue);
            }
//			for(int i1=0 ; i1<purple.length ; i1++)
//			{
//				Prebot.Broodwar.drawBoxMap(purple[i1][0], purple[i1][1], purple[i1][2], purple[i1][3], Color.Purple);
//			}
            for (int i2 = 0; i2 < cyan.length; i2++) {
                Monster.Broodwar.drawCircleMap(cyan[i2][0], cyan[i2][1], 30, Color.Cyan);
            }
            for (int i3 = 0; i3 < orange.length; i3++) {
                Monster.Broodwar.drawBoxMap(orange[i3][0], orange[i3][1], orange[i3][2], orange[i3][3], Color.Orange);
            }
            for (int i4 = 0; i4 < yellow.size(); i4++) {
                Monster.Broodwar.drawCircleMap(yellow.get(i4), 80, Color.Yellow);
            }
            for (int i5 = 0; i5 < green1.size(); i5++) {
                Monster.Broodwar.drawLineMap(green1.get(i5), green2.get(i5), Color.Green);
            }
            for (int i6 = 0; i6 < red1.size(); i6++) {
                Monster.Broodwar.drawLineMap(red1.get(i6), red2.get(i6), Color.Red);
            }

            // OccupiedBaseLocation 을 원으로 표시
            for (BaseLocation baseLocation : InformationManager.Instance().getOccupiedBaseLocations(InformationManager.Instance().selfPlayer)) {
                Monster.Broodwar.drawCircleMap(baseLocation.getPosition(), 10 * CommonConfig.UxConfig.TILE_SIZE, Color.Blue);
            }
            for (BaseLocation baseLocation : InformationManager.Instance().getOccupiedBaseLocations(InformationManager.Instance().enemyPlayer)) {
                Monster.Broodwar.drawCircleMap(baseLocation.getPosition(), 10 * CommonConfig.UxConfig.TILE_SIZE, Color.Red);
            }

            // ChokePoint, BaseLocation 을 텍스트로 표시
            if (InformationManager.Instance().getFirstChokePoint(Monster.Broodwar.self()) != null) {
                Monster.Broodwar.drawTextMap(InformationManager.Instance().getMainBaseLocation(Monster.Broodwar.self()).getPosition(), "My MainBaseLocation");
            }
            if (InformationManager.Instance().getFirstChokePoint(Monster.Broodwar.self()) != null) {
                Monster.Broodwar.drawTextMap(InformationManager.Instance().getFirstChokePoint(Monster.Broodwar.self()).getCenter(), "My First ChokePoint");
            }
            if (InformationManager.Instance().getSecondChokePoint(Monster.Broodwar.self()) != null) {
                Monster.Broodwar.drawTextMap(InformationManager.Instance().getSecondChokePoint(Monster.Broodwar.self()).getCenter(), "My Second ChokePoint");
            }
            if (InformationManager.Instance().getFirstExpansionLocation(Monster.Broodwar.self()) != null) {
                Monster.Broodwar.drawTextMap(InformationManager.Instance().getFirstExpansionLocation(Monster.Broodwar.self()).getPosition(), "My First ExpansionLocation");
            }

            if (InformationManager.Instance().getFirstChokePoint(InformationManager.Instance().enemyPlayer) != null) {
                Monster.Broodwar.drawTextMap(InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().enemyPlayer).getPosition(), "Enemy MainBaseLocation");
            }
            if (InformationManager.Instance().getFirstChokePoint(InformationManager.Instance().enemyPlayer) != null) {
                Monster.Broodwar.drawTextMap(InformationManager.Instance().getFirstChokePoint(InformationManager.Instance().enemyPlayer).getCenter(), "Enemy First ChokePoint");
            }
            if (InformationManager.Instance().getSecondChokePoint(InformationManager.Instance().enemyPlayer) != null) {
                Monster.Broodwar.drawTextMap(InformationManager.Instance().getSecondChokePoint(InformationManager.Instance().enemyPlayer).getCenter(), "Enemy Second ChokePoint");
            }
            if (InformationManager.Instance().getFirstExpansionLocation(InformationManager.Instance().enemyPlayer) != null) {
                Monster.Broodwar.drawTextMap(InformationManager.Instance().getFirstExpansionLocation(InformationManager.Instance().enemyPlayer).getPosition(), "Enemy First ExpansionLocation");
            }

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
            Monster.Broodwar.drawLineMap(i * cellSize, 0, i * cellSize, mapHeight, Color.Blue);
        }

        for (int j = 0; j < rows; j++) {
            Monster.Broodwar.drawLineMap(0, j * cellSize, mapWidth, j * cellSize, Color.Blue);
        }

        for (int r = 0; r < rows; r += 2) {
            for (int c = 0; c < cols; c += 2) {
                Monster.Broodwar.drawTextMap(c * 32, r * 32, c + "," + r);
            }
        }
    }

    /// BuildOrderQueue 를 Screen 에 표시합니다
    public void drawBuildOrderQueueOnScreen(int x, int y) {
        char initialFinishedColor;
        InitialBuildProvider.AdaptStrategyStatus adaptStrategyStatus = InitialBuildProvider.Instance().getAdaptStrategyStatus();
        if (adaptStrategyStatus == InitialBuildProvider.AdaptStrategyStatus.COMPLETE) {
            initialFinishedColor = UxColor.CHAR_WHITE;
        } else if (adaptStrategyStatus == InitialBuildProvider.AdaptStrategyStatus.PROGRESSING) {
            initialFinishedColor = UxColor.CHAR_YELLOW;
        } else {
            initialFinishedColor = UxColor.CHAR_GREEN;
        }

        Monster.Broodwar.drawTextScreen(x, y, initialFinishedColor + " <Build Order>");

		/*
		std.deque< BuildOrderItem >* queue = BuildManager.Instance().buildQueue.getQueue();
		size_t reps = queue.size() < 24 ? queue.size() : 24;
		for (size_t i(0); i<reps; i++) {
			const MetaType & type = (*queue)[queue.size() - 1 - i].metaType;
			Monster.game.drawTextScreen(x, y + 10 + (i * 10), " %s", type.getName().c_str());
		}
		*/

        Deque<BuildOrderItem> buildQueue = BuildManager.Instance().buildQueue.getQueue();
        int itemCount = 0;

        // C++ : for (std.deque<BuildOrderItem>.reverse_iterator itr = buildQueue.rbegin(); itr != buildQueue.rend(); itr++) {
        // C++ : 			BuildOrderItem & currentItem = *itr;
        // C++ : 			Monster.game.drawTextScreen(x, y + 10 + (itemCount * 10), " %s", currentItem.metaType.getName().c_str());
        // C++ : 			itemCount++;
        // C++ : 			if (itemCount >= 24) break;
        // C++ : 		}

        Object[] tempQueue = buildQueue.toArray();

        for (int i = 0; i < tempQueue.length; i++) {
            BuildOrderItem currentItem = (BuildOrderItem) tempQueue[i];
            Monster.Broodwar.drawTextScreen(x, y + 10 + (itemCount * 10), currentItem.blocking + " " + UxColor.CHAR_WHITE + currentItem.metaType.getName());
            itemCount++;
            if (itemCount >= 24) break;
        }
    }

    /// Build 진행 상태를 Screen 에 표시합니다
    public void drawBuildStatusOnScreen(int x, int y) {
        // 건설 / 훈련 중인 유닛 진행상황 표시
        Vector<Unit> unitsUnderConstruction = new Vector<Unit>();
        for (Unit unit : Monster.Broodwar.self().getUnits()) {
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

        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + " <Build Status>");

        int reps = unitsUnderConstruction.size() < 10 ? unitsUnderConstruction.size() : 10;

        for (Unit unit : unitsUnderConstruction) {
            y += 10;
            UnitType t = unit.getType();
            if (t == UnitType.Zerg_Egg) {
                t = unit.getBuildType();
            }

            Monster.Broodwar.drawTextScreen(x, y, "" + UxColor.CHAR_WHITE + t + " (" + unit.getRemainingBuildTime() + ")");
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

                        Monster.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Yellow, false);
                    }
                }
            }
        }
    }

    /// Construction 을 하지 못하는 Tile 들을 Map 에 표시합니다
    public void drawTilesToAvoidOnMap() {
//		Set<TilePosition> tilesToAvoid = ConstructionPlaceFinder.Instance().getTilesToAvoid();
//		for (TilePosition t : tilesToAvoid)
//		{
//			int x1 = t.getX() * 32 + 8;
//			int y1 = t.getY() * 32 + 8;
//			int x2 = (t.getX() + 1) * 32 - 8;
//			int y2 = (t.getY() + 1) * 32 - 8;
//
//			Monster.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Orange, false);
//		}
        int y = 0;
        int x = 0;


//		for(y = 0; y < 128 ; y++) {
//			for(x = 0; x < 128 ; x++) {
//				if(ConstructionPlaceFinder.Instance().getTilesToAvoid(x, y)) {
//					int x1 = x * 32 + 8;
//					int y1 = y * 32 + 8;
//					int x2 = (x + 1) * 32 - 8;
//					int y2 = (y + 1) * 32 - 8;
//	
//					Monster.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Orange, false);
//				}
////				
//			}
//		}


        for (y = 0; y < 128; y++) {
            for (x = 0; x < 128; x++) {
                if (ConstructionPlaceFinder.Instance().getTilesToAvoid(x, y)) {
                    int x1 = x * 32 + 8;
                    int y1 = y * 32 + 8;
                    int x2 = (x + 1) * 32 - 8;
                    int y2 = (y + 1) * 32 - 8;

                    Monster.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Orange, false);
                }
                if (ConstructionPlaceFinder.Instance().getTilesToAvoidAbsolute(x, y)) {
                    int x1 = x * 32 + 8;
                    int y1 = y * 32 + 8;
                    int x2 = (x + 1) * 32 - 8;
                    int y2 = (y + 1) * 32 - 8;

                    Monster.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Purple, false);
                }
                if (ConstructionPlaceFinder.Instance().getTilesToAvoidSupply(x, y)) {
                    int x1 = x * 32 + 8;
                    int y1 = y * 32 + 8;
                    int x2 = (x + 1) * 32 - 8;
                    int y2 = (y + 1) * 32 - 8;

                    Monster.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Red, false);
                }
//				if(ConstructionPlaceFinder.Instance().getTilesToAvoidAddonBuilding(x, y)) {
////					System.out.println("getTilesToAvoidAddonBuilding is ture :: (" + x + " , " + y + ")");
//					int x1 = x * 32 + 8;
//					int y1 = y * 32 + 8;
//					int x2 = (x + 1) * 32 - 8;
//					int y2 = (y + 1) * 32 - 8;
//	
//					Monster.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Black, false);
//				}
//				
            }
        }
    }

    /// ConstructionQueue 를 Screen 에 표시합니다
    public void drawConstructionQueueOnScreenAndMap(int x, int y) {
        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + " <Construction Status>");

        int yspace = 0;

        Vector<ConstructionTask> constructionQueue = ConstructionManager.Instance().getConstructionQueue();

        for (final ConstructionTask b : constructionQueue) {
            String constructionState = "";

            if (b.getStatus() == ConstructionTask.ConstructionStatus.Unassigned.ordinal()) {
                Monster.Broodwar.drawTextScreen(x, y + 10 + ((yspace) * 10), "" + UxColor.CHAR_WHITE + b.getType() + " - No Worker");
            } else if (b.getStatus() == ConstructionTask.ConstructionStatus.Assigned.ordinal()) {
                if (b.getConstructionWorker() == null) {
                    Monster.Broodwar.drawTextScreen(x, y + 10 + ((yspace) * 10), b.getType() + " - Assigned Worker Null");
                } else {
                    Monster.Broodwar.drawTextScreen(x, y + 10 + ((yspace) * 10), b.getType() + " - Assigned Worker " + b.getConstructionWorker().getID() + ", Position (" + b.getFinalPosition().getX() + "," + b.getFinalPosition().getY() + ")");
                }

                int x1 = b.getFinalPosition().getX() * 32;
                int y1 = b.getFinalPosition().getY() * 32;
                int x2 = (b.getFinalPosition().getX() + b.getType().tileWidth()) * 32;
                int y2 = (b.getFinalPosition().getY() + b.getType().tileHeight()) * 32;

                Monster.Broodwar.drawLineMap(b.getConstructionWorker().getPosition().getX(), b.getConstructionWorker().getPosition().getY(), (x1 + x2) / 2, (y1 + y2) / 2, Color.Orange);
                Monster.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Red, false);
            } else if (b.getStatus() == ConstructionTask.ConstructionStatus.UnderConstruction.ordinal()) {
                Monster.Broodwar.drawTextScreen(x, y + 10 + ((yspace) * 10), "" + UxColor.CHAR_WHITE + b.getType() + " - Under Construction");
            }
            yspace++;
        }
    }

    public void drawnextPoints() {

//		Position nextEX = InformationManager.Instance().getNextExpansionLocation().getPosition();
//		Position nextBuild = InformationManager.Instance().getLastBuildingLocation().toPosition();
//		Position lastBuild2 = InformationManager.Instance().getLastBuildingLocation2().toPosition();

        BaseLocation getExpansionLocation = InformationManager.Instance().getExpansionLocation;
        BaseLocation secondStartPosition = InformationManager.Instance().getSecondStartPosition();
        TilePosition getLastBuildingLocation = InformationManager.Instance().getLastBuildingLocation;
        TilePosition getLastBuildingFinalLocation = InformationManager.Instance().getLastBuildingFinalLocation;


        if (secondStartPosition != null) {
            Monster.Broodwar.drawTextScreen(10, 120, "secondStartPosition: " + secondStartPosition.getTilePosition());
            Monster.Broodwar.drawTextMap(secondStartPosition.getPosition(), "secondStartPosition");
        } else {
            Monster.Broodwar.drawTextScreen(10, 120, "secondStartPosition: null");
        }
        if (getExpansionLocation != null) {
            Monster.Broodwar.drawTextScreen(10, 130, "getExpansionLocation: " + getExpansionLocation.getTilePosition());
            Monster.Broodwar.drawTextMap(getExpansionLocation.getPosition(), "nextEX");
        } else {
            Monster.Broodwar.drawTextScreen(10, 130, "getExpansionLocation: null");
        }
        if (getLastBuildingLocation != null) {
            Monster.Broodwar.drawTextScreen(10, 140, "getLastBuildingLocation: " + getLastBuildingLocation);
            Monster.Broodwar.drawTextMap(getLastBuildingLocation.toPosition(), "nextBuild");
        } else {
            Monster.Broodwar.drawTextScreen(10, 140, "getLastBuildingLocation: null");
        }
        if (getLastBuildingFinalLocation != null) {
            Monster.Broodwar.drawTextScreen(10, 150, "getLastBuildingFinalLocation: " + getLastBuildingFinalLocation);
            Monster.Broodwar.drawTextMap(getLastBuildingFinalLocation.toPosition(), "LastBuild");
        } else {
            Monster.Broodwar.drawTextScreen(10, 150, "getLastBuildingFinalLocation: null");
        }


        Monster.Broodwar.drawTextScreen(10, 160, "mainBaseLocationFull: " + BuildManager.Instance().mainBaseLocationFull);
        Monster.Broodwar.drawTextScreen(10, 170, "secondChokePointFull: " + BuildManager.Instance().secondChokePointFull);
        Monster.Broodwar.drawTextScreen(10, 180, "secondStartLocationFull: " + BuildManager.Instance().secondStartLocationFull);
        Monster.Broodwar.drawTextScreen(10, 190, "fisrtSupplePointFull: " + BuildManager.Instance().fisrtSupplePointFull);

        Monster.Broodwar.drawTextScreen(10, 200, "myMainbaseLocation : " + InformationManager.Instance().getMainBaseLocation(Monster.Broodwar.self()).getTilePosition());
        Monster.Broodwar.drawTextScreen(10, 210, "enemyMainbaseLocation : " + InformationManager.Instance().getMainBaseLocation(Monster.Broodwar.enemy()).getTilePosition());

    }


    public void drawMineralIdOnMap() {
        for (Unit unit : Monster.Broodwar.getStaticMinerals()) {

            Monster.Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5, "" + UxColor.CHAR_WHITE + unit.getID());
        }
    }

    /// Unit 의 Id 를 Map 에 표시합니다
    public void drawUnitIdOnMap() {
        for (Unit unit : Monster.Broodwar.self().getUnits()) {
            if (unit.getType().isBuilding()) {
                Monster.Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5, "" + UxColor.CHAR_WHITE + unit.getID());
                Monster.Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 25, "" + UxColor.CHAR_WHITE + unit.getTilePosition().getX() + " / " + unit.getTilePosition().getY());
            } else {
                Monster.Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5, "" + UxColor.CHAR_WHITE + unit.getID());
            }

        }
        for (Unit unit : Monster.Broodwar.enemy().getUnits()) {
            Monster.Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5, "" + UxColor.CHAR_WHITE + unit.getID());
        }
    }

    public void drawLeaderUnitOnMap() {

//		
//		if(leader!=null){
//			for (Unit unit : Monster.Broodwar.self().getUnits())
//			{
//				if(unit.getID() == leader.getID())
//				Monster.Broodwar.drawTextMap(unit.getPosition().getX(), unit.getPosition().getY() + 5, "" + blue + "LEADER");
//			}
//		}
    }

    /// Worker Unit 들의 상태를 Screen 에 표시합니다
    public void drawWorkerStateOnScreen(int x, int y) {
        WorkerData workerData = WorkerManager.Instance().getWorkerData();

        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + "<Workers : " + workerData.getNumMineralWorkers() + ">");

        int yspace = 0;

        for (Unit unit : workerData.getWorkers()) {
            if (unit == null) continue;

            // Mineral / Gas / Idle Worker 는 표시 안한다
            if (workerData.getJobCode(unit) == 'M' || workerData.getJobCode(unit) == 'I' || workerData.getJobCode(unit) == 'G') {
                continue;
            }

            Monster.Broodwar.drawTextScreen(x, y + 10 + ((yspace) * 10), UxColor.CHAR_WHITE + " " + unit.getID());

            if (workerData.getJobCode(unit) == 'B') {
                Monster.Broodwar.drawTextScreen(x + 30, y + 10 + ((yspace++) * 10), UxColor.CHAR_WHITE + " " + workerData.getJobCode(unit) + " " + unit.getBuildType() + " " + (unit.isConstructing() ? 'Y' : 'N') + " (" + unit.getTilePosition().getX() + ", " + unit.getTilePosition().getY() + ")");
            } else {
                Monster.Broodwar.drawTextScreen(x + 30, y + 10 + ((yspace++) * 10), UxColor.CHAR_WHITE + " " + workerData.getJobCode(unit));
            }
        }
    }

    /// ResourceDepot 별 Worker 숫자를 Map 에 표시합니다
    public void drawWorkerCountOnMap() {
        for (Unit depot : WorkerManager.Instance().getWorkerData().getDepots()) {
            if (depot == null) continue;

            int x = depot.getPosition().getX() - 64;
            int y = depot.getPosition().getY() - 32;

            Monster.Broodwar.drawBoxMap(x - 2, y - 1, x + 75, y + 14, Color.Black, true);
            Monster.Broodwar.drawTextMap(x, y, UxColor.CHAR_WHITE + " Workers: " + WorkerManager.Instance().getWorkerData().getNumAssignedWorkers(depot));
        }
    }

    /// Worker Unit 의 자원채취 현황을 Map 에 표시합니다
    public void drawWorkerMiningStatusOnMap() {
        WorkerData workerData = WorkerManager.Instance().getWorkerData();

        for (Unit worker : workerData.getWorkers()) {
            if (worker == null) continue;

            Position pos = worker.getTargetPosition();

            Monster.Broodwar.drawTextMap(worker.getPosition().getX(), worker.getPosition().getY() - 5, "" + UxColor.CHAR_WHITE + workerData.getJobCode(worker));

            Monster.Broodwar.drawLineMap(worker.getPosition().getX(), worker.getPosition().getY(), pos.getX(), pos.getY(), Color.Cyan);

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
//		int currentScoutStatus = OldScoutManager.Instance().getScoutStatus();
//		String scoutStatusString = null;
//
//		if(currentScoutStatus == OldScoutManager.ScoutStatus.MovingToAnotherBaseLocation.ordinal()){
//			scoutStatusString = "Moving To Another Base Location";
//		}else if(currentScoutStatus == OldScoutManager.ScoutStatus.MoveAroundEnemyBaseLocation.ordinal()){
//			scoutStatusString = "Move Around Enemy BaseLocation";
//		}else if(currentScoutStatus == OldScoutManager.ScoutStatus.NoScout.ordinal()){
//			scoutStatusString = "No Scout";
//		}else{
//			scoutStatusString = "No Scout";
//		}

        // get the enemy base location, if we have one
        BaseLocation enemyBaseLocation = InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().enemyPlayer);

        if (enemyBaseLocation != null) {
            Monster.Broodwar.drawTextScreen(x, y, "Enemy MainBaseLocation : (" + enemyBaseLocation.getTilePosition().getX() + ", " + enemyBaseLocation.getTilePosition().getY() + ")");
        } else {
            Monster.Broodwar.drawTextScreen(x, y, "Enemy MainBaseLocation : Unknown");
        }

//		if (currentScoutStatus == OldScoutManager.ScoutStatus.NoScout.ordinal()) {
//			Prebot.Broodwar.drawTextScreen(x, y + 10, "No Scout Unit");
//		}
//		else {
//			
//			Unit scoutUnit = OldScoutManager.Instance().getScoutUnit();
//			if (scoutUnit != null) {
//				Prebot.Broodwar.drawTextScreen(x, y + 10, "Scout Unit : " + scoutUnit.getType() + " " + scoutUnit.getID() + " (" + scoutUnit.getTilePosition().getX() + ", " + scoutUnit.getTilePosition().getY() + ")");
//	
//				Position scoutMoveTo = scoutUnit.getTargetPosition();
//	
//				if (scoutMoveTo != null && scoutMoveTo != Position.None && scoutMoveTo.isValid()) {
//	
//					double currentScoutTargetDistance;
//	
//					if (currentScoutStatus == OldScoutManager.ScoutStatus.MovingToAnotherBaseLocation.ordinal()) {
//						if (scoutUnit.getType().isFlyer()) {
//							currentScoutTargetDistance = (int)(scoutUnit.getPosition().getDistance(scoutMoveTo));
//						}
//						else {
//							currentScoutTargetDistance = PositionUtils.getGroundDistance(scoutUnit.getPosition(), scoutMoveTo);
//						}
//	
//						Prebot.Broodwar.drawTextScreen(x, y + 20, "Target = (" + scoutMoveTo.getX() / CommonConfig.UxConfig.TILE_SIZE + ", " + scoutMoveTo.getY() / CommonConfig.UxConfig.TILE_SIZE + ") Distance = " + currentScoutTargetDistance);
//					}
//					/*
//					else if (currentScoutStatus == ScoutManager.ScoutStatus.MoveAroundEnemyBaseLocation.ordinal()) {
//	
//						Vector<Position> vertices = ScoutManager.Instance().getEnemyRegionVertices();
//						for (int i = 0 ; i < vertices.size() ; ++i)
//						{
//							Monster.Broodwar.drawCircleMap(vertices.get(i), 4, Color.Green, false);
//							Monster.Broodwar.drawTextMap(vertices.get(i), "" + i);
//						}
//						Monster.Broodwar.drawCircleMap(scoutMoveTo, 5, Color.Red, true);
//					}
//					*/
//				}
//			}
//		}
    }

    /// Unit 의 Target 으로 잇는 선을 Map 에 표시합니다
    public void drawUnitTargetOnMap() {
        for (Unit unit : Monster.Broodwar.self().getUnits()) {
            if (unit != null && unit.isCompleted() && !unit.getType().isBuilding() && !unit.getType().isWorker()) {
                Unit targetUnit = unit.getTarget();
                if (targetUnit != null && targetUnit.getPlayer() != Monster.Broodwar.self()) {
                    Monster.Broodwar.drawCircleMap(unit.getPosition(), dotRadius, Color.Red, true);
                    Monster.Broodwar.drawCircleMap(targetUnit.getTargetPosition(), dotRadius, Color.Red, true);
                    Monster.Broodwar.drawLineMap(unit.getPosition(), targetUnit.getTargetPosition(), Color.Red);
                } else if (unit.isMoving()) {
                    Monster.Broodwar.drawCircleMap(unit.getPosition(), dotRadius, Color.Orange, true);
                    Monster.Broodwar.drawCircleMap(unit.getTargetPosition(), dotRadius, Color.Orange, true);
                    Monster.Broodwar.drawLineMap(unit.getPosition(), unit.getTargetPosition(), Color.Orange);
                }

            }
        }
    }

    /// Bullet 을 Map 에 표시합니다 <br>
    /// Cloaking Unit 의 Bullet 표시에 쓰입니다
    public void drawBulletsOnMap() {
        for (Bullet b : Monster.Broodwar.getBullets()) {
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
            Monster.Broodwar.drawLineMap(p, new Position(p.getX() + (int) velocityX, p.getY() + (int) velocityY), b.getPlayer() == Monster.Broodwar.self() ? Color.Green : Color.Red);
            if (b.getType() != null) {
                Monster.Broodwar.drawTextMap(p, (b.getPlayer() == Monster.Broodwar.self() ? "" + UxColor.CHAR_TEAL : "" + UxColor.CHAR_RED) + bulletTypeName);
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
                Monster.Broodwar.drawCircleMap(unit.getPosition(), 10, color);
                Monster.Broodwar.drawTextMap(unit.getPosition().getX() - 20, unit.getPosition().getY() - 30, squadName);
                if (smallFightPredict != null && smallFightPredict == StrategyCode.SmallFightPredict.BACK) {
                    Monster.Broodwar.drawTextMap(unit.getPosition().getX() - 20, unit.getPosition().getY() - 15, UxColor.CHAR_RED + smallFightPredict.toString());
                }
            }

            Map<Integer, Integer> checkerSiteMap = VultureTravelManager.Instance().getCheckerSiteMap2();
            List<BaseLocation> baseList = VultureTravelManager.Instance().getBaseLocationsCheckerOrdered();
            for (Integer checkerId : checkerSiteMap.keySet()) {
                Unit unit = Monster.Broodwar.getUnit(checkerId);
                if (UnitUtils.isValidUnit(unit)) {
                    Integer index = checkerSiteMap.get(checkerId);
                    if (index != null) {
                        Monster.Broodwar.drawTextMap(unit.getPosition().getX() - 20, unit.getPosition().getY() - 5, UxColor.CHAR_ORANGE + baseList.get(index).getPosition().toString());
                    }

                }
            }
        }
    }

    private void drawSquadInfoOnMap(int x, int y) {
        /// ConstructionQueue 를 Screen 에 표시합니다
        Monster.Broodwar.drawTextScreen(x, y, UxColor.CHAR_WHITE + "<Squad Name>");
        Monster.Broodwar.drawTextScreen(x + 110, y, UxColor.CHAR_WHITE + " <Unit Size>");

        y += 15;
        Monster.Broodwar.drawTextScreen(x, y, "" + "*" + "SCV");
        Monster.Broodwar.drawTextScreen(x + 120, y, "" + UnitUtils.getUnitCount(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_SCV));
        y += 10;
        for (Squad squad : CombatManager.Instance().squadData.getSquadMap().values()) {
            Color squadColor = UxColor.SQUAD_COLOR.get(squad.getClass());
            if (squadColor != null) {
                Monster.Broodwar.drawTextScreen(x, y, "" + UxColor.COLOR_TO_CHARACTER.get(squadColor) + squad.getSquadName());
            } else {
                Monster.Broodwar.drawTextScreen(x, y, "" + "*" + squad.getSquadName());
            }
            String unitIds = " ... ";
            for (Unit unit : squad.unitList) {
                unitIds = unitIds + unit.getID() + "/";
            }
            Monster.Broodwar.drawTextScreen(x + 120, y, "" + squad.unitList.size() + unitIds);
            y += 10;
        }
    }

    private void drawManagerTimeSpent(int x, int y) {
        List<GameManager> gameManagers = Arrays.asList(
                InformationManager.Instance(),
                StrategyManager.Instance(),
                MapGrid.Instance(),
                BuildManager.Instance(),
                BuildQueueProvider.Instance(),
                ConstructionManager.Instance(),
                WorkerManager.Instance(),
                CombatManager.Instance(),
                AttackDecisionMaker.Instance());


        int currentY = y;
        for (GameManager gameManager : gameManagers) {
            currentY += 10;
            Monster.Broodwar.drawTextScreen(x, currentY, UxColor.CHAR_PURPLE + gameManager.getClass().getSimpleName());

            char drawColor = UxColor.CHAR_WHITE;
            if (gameManager.getRecorded() > 10L) {
                drawColor = UxColor.CHAR_TEAL;
            } else if (gameManager.getRecorded() > 30L) {
                drawColor = UxColor.CHAR_RED;
            }
            Monster.Broodwar.drawTextScreen(x + 103, currentY, ": " + drawColor + gameManager.getRecorded());
        }

        Monster.Broodwar.drawTextScreen(x, currentY += 15, "* group size: " + LagObserver.groupsize());
        Monster.Broodwar.drawTextScreen(x, currentY += 10, "* manager rotation size: " + LagObserver.managerRotationSize());
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
            Monster.Broodwar.drawTextScreen(10, currentY += 10, UxColor.CHAR_WHITE + tag + " : " + resultTime + " / " + drawColor + recordTime);
        }
    }

    private void drawPathData() {
        for (Unit depot : UnitUtils.getUnitList(UnitType.Terran_Command_Center)) {
            List<Minerals> mineralsList = WorkerData.depotMineral.get(depot);
            if (mineralsList == null) {
//				System.out.println("mineralsList is null.");
//				if (depot != null) {
//					System.out.println("depot=" + depot.getID() +  "" + depot.getPosition());
//				} else {
//					System.out.println("depot is null");
//				}
                return;
            }

            for (Minerals minr : mineralsList) {
                if (minr.mineralTrick != null) {
                    Monster.Broodwar.drawCircleMap(minr.mineralUnit.getPosition().getX(), minr.mineralUnit.getPosition().getY(), 4, Color.Blue, true);
                    Monster.Broodwar.drawCircleMap(minr.mineralTrick.getPosition().getX(), minr.mineralTrick.getPosition().getY(), 4, Color.Purple, true);
                }
            }


            for (Minerals minr : WorkerData.depotMineral.get(depot)) {
                if (minr.posTrick != bwapi.Position.None) {
                    Monster.Broodwar.drawCircleMap(minr.posTrick.getX(), minr.posTrick.getY(), 4, Color.Red, true);
                    Monster.Broodwar.drawCircleMap(minr.mineralUnit.getPosition().getX(), minr.mineralUnit.getPosition().getY(), 4, Color.Yellow, true);
                }
            }

            //Broodwar->drawCircleMap(Minerals[0].posTrick.x(),Minerals[0].posTrick.y(),2,Colors::Purple,true);

            //Prebot.Broodwar.drawCircleMap(Mineral.Instance().CCtrick.getX(),Mineral.Instance().CCtrick.getY(),2,Color.Brown,true);
            //for(int i=0; i< MineralManager.Instance().minerals.size(); i++){
            //Prebot.Broodwar.drawTextMap( minr.mineral.getPosition().getX(),minr.mineral.getPosition().getY(), "(" + (int)(MineralManager.Instance().minerals.get(i).MinToCC) + (int)(MineralManager.Instance().minerals.get(i).CCToMin)  + ")");
            //}
        }

    }

    private void drawStrategy() {
        String upgradeString = "";
        for (MetaType metaType : StrategyBoard.upgrade) {
            upgradeString += metaType.getName() + " > ";
        }

        int y = 10;
        Race enemyRace = PlayerUtils.enemyRace();
        EnemyStrategy strategy = StrategyBoard.currentStrategy;
        int phase = StrategyAnalyseManager.Instance().getPhase();

        Monster.Broodwar.drawTextScreen(20, y += 12, UxColor.CHAR_YELLOW + "[" + strategy.name() + " ...(phase " + phase + ")]");
        Monster.Broodwar.drawTextScreen(20, y += 12, UxColor.CHAR_YELLOW + "FAC RATIO : " + StrategyBoard.factoryRatio + ".. (" + UnitUtils.myFactoryUnitSupplyCount() + ")");
        Monster.Broodwar.drawTextScreen(20, y += 12, UxColor.CHAR_YELLOW + "UPGRADE   : " + upgradeString);
        Monster.Broodwar.drawTextScreen(20, y += 12, UxColor.CHAR_YELLOW + "MARINE CNT : " + StrategyBoard.marineCount);
        Monster.Broodwar.drawTextScreen(20, y += 12, UxColor.CHAR_YELLOW + "ADDON     : " + StrategyBoard.addOnOption);
        Monster.Broodwar.drawTextScreen(20, y += 12, UxColor.CHAR_YELLOW + "EXPANSION : " + StrategyBoard.expansionOption);
        Monster.Broodwar.drawTextScreen(20, y += 12, UxColor.CHAR_YELLOW + "WRAITH CNT : " + StrategyBoard.wraithCount);
        Monster.Broodwar.drawTextScreen(20, y += 12, UxColor.CHAR_YELLOW + "VALKYRIE CNT : " + StrategyBoard.valkyrieCount);
        Monster.Broodwar.drawTextScreen(20, y += 12, UxColor.CHAR_YELLOW + "MISSION    : " + strategy.missionTypeList);
        Monster.Broodwar.drawTextScreen(20, y += 20, UxColor.CHAR_YELLOW + "" + strategy.buildTimeMap);

        Monster.Broodwar.drawTextScreen(20, 260, "" + UxColor.CHAR_YELLOW + ClueManager.Instance().getClueInfoList());

        y = 10;
        for (EnemyStrategy enemyStrategy : EnemyStrategy.values()) {
            if (enemyStrategy.name().startsWith(enemyRace.toString().toUpperCase())) {
                Monster.Broodwar.drawTextScreen(400, y += 10, "" + UxColor.CHAR_YELLOW + enemyStrategy.name());
            }
        }
    }

    private void drawEnemyAirDefenseRange() {
        List<UnitInfo> airDefenseEuiList = UnitUtils.getEnemyUnitInfoList(CommonCode.EnemyUnitFindRange.ALL, UnitUtils.enemyAirDefenseUnitType());
        for (UnitInfo eui : airDefenseEuiList) {
            if (eui.getType() == UnitType.Terran_Bunker) {
                Monster.Broodwar.drawCircleMap(eui.getLastPosition(), Monster.Broodwar.enemy().weaponMaxRange(UnitType.Terran_Marine.groundWeapon()) + 96, Color.White);
            } else {
                Monster.Broodwar.drawCircleMap(eui.getLastPosition(), eui.getType().airWeapon().maxRange(), Color.White);
            }
        }
        List<UnitInfo> wraithKillerEuiList = UnitUtils.getEnemyUnitInfoList(CommonCode.EnemyUnitFindRange.ALL, UnitUtils.wraithKillerUnitType());
        for (UnitInfo eui : wraithKillerEuiList) {
            Monster.Broodwar.drawCircleMap(eui.getLastPosition(), eui.getType().airWeapon().maxRange(), Color.Grey);
        }
    }

    private void drawAirForceInformation() {
        // wraith moving
        for (Unit unit : UnitUtils.getUnitList(UnitType.Terran_Wraith)) {
            if (unit.isMoving()) {
                Monster.Broodwar.drawCircleMap(unit.getPosition(), dotRadius, Color.Orange, true);
                Monster.Broodwar.drawCircleMap(unit.getTargetPosition(), dotRadius, Color.Orange, true);
                Monster.Broodwar.drawLineMap(unit.getPosition(), unit.getTargetPosition(), Color.Orange);
            }
        }

        // target position
        List<Position> targetPositions = AirForceManager.Instance().getTargetPositions();
        for (int i = 0; i < targetPositions.size(); i++) {
            Monster.Broodwar.drawTextMap(targetPositions.get(i), "position#" + i);
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
            Monster.Broodwar.drawTextMap(position.getX(), position.getY() - 10, color + "leader#" + airForceTeam.leaderUnit.getID());

            Position targetPosition = new Position(airForceTeam.getTargetPosition().getX(), airForceTeam.getTargetPosition().getY() - 10);
            Monster.Broodwar.drawTextMap(targetPosition, UxColor.CHAR_RED + "*" + airForceTeam.leaderUnit.getID());
            Monster.Broodwar.drawTextScreen(20, y += 15, "" + UxColor.CHAR_YELLOW + airForceTeam.toString());
        }
        Monster.Broodwar.drawTextScreen(20, y += 15, "Defense Mode? " + AirForceManager.Instance().isAirForceDefenseMode());
        Monster.Broodwar.drawTextScreen(20, y += 15, "strike level=" + AirForceManager.Instance().getStrikeLevel());
        Monster.Broodwar.drawTextScreen(20, y += 15, "total achievement=" + AirForceManager.Instance().getAchievementEffectiveFrame());
        Monster.Broodwar.drawTextScreen(20, y += 15, "accumulated achievement=" + AirForceManager.Instance().getAccumulatedAchievement());
        Monster.Broodwar.drawTextScreen(20, y += 15, "wraith count=" + StrategyBoard.wraithCount);
    }

    private void drawVulturePolicy() {
        int y = 10;
        Monster.Broodwar.drawTextScreen(400, y += 15, "[vulture policy]");
        Monster.Broodwar.drawTextScreen(400, y += 15, "checkerMaxNumber=" + StrategyBoard.checkerMaxNumber);
        Monster.Broodwar.drawTextScreen(400, y += 15, "spiderMineNumberPerPosition=" + StrategyBoard.spiderMineNumberPerPosition);
        Monster.Broodwar.drawTextScreen(400, y += 15, "spiderMineNumberPerGoodPosition=" + StrategyBoard.spiderMineNumberPerGoodPosition);
        Monster.Broodwar.drawTextScreen(400, y += 15, "watcherMinePositionLevel=" + StrategyBoard.watcherMinePositionLevel);
    }

    private void drawEnemyBaseToBaseTime() {
        int y = 0;
        Monster.Broodwar.drawTextScreen(10, y += 15, "campPosition : " + StrategyBoard.campPosition + " / " + StrategyBoard.campType);
        Monster.Broodwar.drawTextScreen(10, y += 15, "mainPosition : " + StrategyBoard.mainPosition);
        Monster.Broodwar.drawTextScreen(10, y += 15, "watcherPosition : " + StrategyBoard.watcherPosition);
        Monster.Broodwar.drawTextScreen(10, y += 15, "mainSquadCenter : " + StrategyBoard.mainSquadCenter);
        Monster.Broodwar.drawTextScreen(10, y += 15, "enemyGroundSquadPosition : " + StrategyBoard.nearGroundEnemyPosition + " / " + StrategyBoard.enemyUnitStatus);
        Monster.Broodwar.drawTextScreen(10, y += 15, "enemyAirSquadPosition : " + StrategyBoard.nearAirEnemyPosition);
        Monster.Broodwar.drawTextScreen(10, y += 15, "enemyDropEnemyPosition : " + StrategyBoard.dropEnemyPosition);

        y += 10;
        Position enemyBasePosition = null;
        Position enemyExpansionPosition = null;
        if (BaseUtils.enemyMainBase() != null) {
            enemyBasePosition = BaseUtils.enemyMainBase().getPosition();
            enemyExpansionPosition = BaseUtils.enemyMainBase().getPosition();

        }
        Monster.Broodwar.drawTextScreen(10, y += 15, "enemyMainBase : " + enemyBasePosition);
        Monster.Broodwar.drawTextScreen(10, y += 15, "enemyFirstExpansion : " + enemyExpansionPosition);

//        if (StrategyBoard.enemyBaseExpected != null) {
//            Monster.Broodwar.drawTextScreen(10, y += 15, "enemyMainBase (Expect) : " + StrategyBoard.enemyBaseExpected.getPosition());
//        }
//		for (Entry<UnitType, Integer> unitType : InformationManager.Instance().baseToBaseUnit.entrySet()) {
//			Prebot.Broodwar.drawTextScreen(20, y += 10, "" + UxColor.CHAR_YELLOW + unitType.getKey() + " : " + unitType.getValue());
//		}
    }

    private void drawPositionInformation() {

        if (StrategyBoard.mainSquadLeaderPosition != null) {
            Monster.Broodwar.drawTextMap(PositionUtils.positionAdjsuted(StrategyBoard.mainSquadLeaderPosition, 0, -20), UxColor.CHAR_WHITE + "V");
        }
        if (StrategyBoard.campPosition.equals(StrategyBoard.mainPosition)) {
            Monster.Broodwar.drawTextMap(StrategyBoard.campPosition, UxColor.CHAR_ORANGE + "camp & main");
        } else {
            if (StrategyBoard.campPosition != null) {
                Monster.Broodwar.drawTextMap(StrategyBoard.campPosition, UxColor.CHAR_YELLOW + "camp");
            }
            if (StrategyBoard.mainPosition != null) {
                Monster.Broodwar.drawTextMap(PositionUtils.positionAdjsuted(StrategyBoard.mainPosition, 0, -10), UxColor.CHAR_RED + "main");
            }
        }
        if (StrategyBoard.campPositionSiege != null) {
            Monster.Broodwar.drawTextMap(StrategyBoard.campPositionSiege, UxColor.CHAR_YELLOW + "camp (siege)");
        }
        if (StrategyBoard.watcherPosition != null) {
            Monster.Broodwar.drawTextMap(PositionUtils.positionAdjsuted(StrategyBoard.watcherPosition, 0, -20), UxColor.CHAR_BLUE + "watcherPos");
        }
        if (StrategyBoard.mainSquadCenter != null) {
            Monster.Broodwar.drawTextMap(StrategyBoard.mainSquadCenter, "mainSqCntr");
            Monster.Broodwar.drawCircleMap(StrategyBoard.mainSquadCenter.getX(), StrategyBoard.mainSquadCenter.getY(), StrategyBoard.mainSquadCoverRadius, Color.Cyan);
        }
        if (StrategyBoard.nearGroundEnemyPosition != null) {
            Monster.Broodwar.drawTextMap(StrategyBoard.nearGroundEnemyPosition, UxColor.CHAR_RED + "nearEnemySq(Ground)");
            Monster.Broodwar.drawCircleMap(StrategyBoard.nearGroundEnemyPosition, 150, Color.Red);
        }
        if (StrategyBoard.nearAirEnemyPosition != null) {
            Monster.Broodwar.drawTextMap(StrategyBoard.nearAirEnemyPosition, UxColor.CHAR_RED + "nearEnemySq(Air)");
            Monster.Broodwar.drawCircleMap(StrategyBoard.nearAirEnemyPosition, 150, Color.Red);
        }
        if (StrategyBoard.dropEnemyPosition != null) {
            Monster.Broodwar.drawTextMap(StrategyBoard.dropEnemyPosition, UxColor.CHAR_RED + "dropEnemySq");
            Monster.Broodwar.drawCircleMap(StrategyBoard.dropEnemyPosition, 150, Color.Red);
        }
        if (StrategyBoard.totalEnemyCneterPosition != null) {
            Monster.Broodwar.drawTextMap(StrategyBoard.totalEnemyCneterPosition, "totalEnemySq");
            Monster.Broodwar.drawCircleMap(StrategyBoard.totalEnemyCneterPosition, 250, Color.Red);
        }
        if (InfoUtils.myReadyToPosition() != null) {
            Monster.Broodwar.drawTextMap(InfoUtils.myReadyToPosition(), "myReadyTo");
        }
        if (InfoUtils.enemyReadyToPosition() != null) {
            Monster.Broodwar.drawTextMap(InfoUtils.enemyReadyToPosition(), "enemyReadyTo");
        }
//		if (VultureTravelManager.Instance().getTravelSites() != null) {
//			for (TravelSite site : VultureTravelManager.Instance().getTravelSites()) {
//				Monster.Broodwar.drawTextMap(site.baseLocation.getPosition(), "travel site\n" + site);
//			}
//		}
    }

    private void drawCCtoScvCount() {

        int y = 100;
        for (Unit depot : UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Command_Center)) {
            // update workerData with the new job
            Monster.Broodwar.drawTextScreen(500, y, "depot.getID() : " + depot.getID() + " cnt : " + WorkerData.depotWorkerCount.get(depot.getID()));
            y += 10;
        }
    }

    /// turret 건설 지점의 반경 표시
    public void drawTurretMap() {
        BaseLocation myBase = BaseUtils.myMainBase();
        BaseLocation myFirstExpansion = BaseUtils.myFirstExpansion();
        Chokepoint myFirstChoke = InfoUtils.myFirstChoke();
        Chokepoint mySecondChoke = InfoUtils.mySecondChoke();

        int turretCount = Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Missile_Turret);

        Position firstChokeMainHalf = new Position((myBase.getPosition().getX() + myFirstChoke.getX() * 2) / 3 - 60,
                (myBase.getPosition().getY() + myFirstChoke.getY() * 2) / 3 - 60);

        Position firstChokeExpHalf = new Position((myFirstExpansion.getPosition().getX() * 2 + myFirstChoke.getX()) / 3,
                (myFirstExpansion.getPosition().getY() * 2 + myFirstChoke.getY()) / 3);

////		Position betweenChoke = new Position((myFirstChoke.getX() * 2 + myFirstChoke.getX()) / 3,
////				(mySecondChoke.getY() * 2 + mySecondChoke.getY()) / 3);
////		

//		Monster.Broodwar.drawTextMap(firstChokeExpHalf.getX() + 20, firstChokeExpHalf.getY() + 10, "(" + (int) (firstChokeExpHalf.getX()) + ", " + (int) (firstChokeExpHalf.getY()) + ")");
//		
//		Monster.Broodwar.drawCircleMap(firstChokeExpHalf, 150, Color.Orange, false);
//		
//		Monster.Broodwar.drawCircleMap(firstChokeExpHalf, 150 + turretCount * 15, Color.Orange, false);
//		
//		Monster.Broodwar.drawTextMap(mySecondChoke.getCenter().getX() + 20, mySecondChoke.getCenter().getY() + 10, "(" + (int) (mySecondChoke.getCenter().getX()) + ", " + (int) (mySecondChoke.getCenter().getY()) + ")");
//		
//		Monster.Broodwar.drawCircleMap(mySecondChoke.getCenter(), 150, Color.Cyan, false);
//		
//		Monster.Broodwar.drawCircleMap(mySecondChoke.getCenter(), 150 + turretCount * 15, Color.Cyan, false);
//		
//		Position betweenChoke2 = Position.None;
//		
//		if (StaticMapUtils.getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.FIGHTING_SPIRITS) {
//			betweenChoke2 = new Position((firstChokeMainHalf.getX() * 4 + mySecondChoke.getX() * 7) / 11,
//			(firstChokeMainHalf.getY() * 4 + mySecondChoke.getY() * 7) / 11);
//		}else {
//			betweenChoke2 = new Position((firstChokeMainHalf.getX() * 3 + mySecondChoke.getX() * 4) / 7,
//			(firstChokeMainHalf.getY() * 4 + mySecondChoke.getY() * 7) / 11);
//		}
//		
////		Position betweenChoke2 = new Position((firstChokeMainHalf.getX() * 4 + mySecondChoke.getX() * 7) / 11,
////				(firstChokeMainHalf.getY() * 4 + mySecondChoke.getY() * 7) / 11);
//		
//		Monster.Broodwar.drawTextMap(betweenChoke2.getX() + 20, betweenChoke2.getY() + 10, "(" + (int) (betweenChoke2.getX()) + ", " + (int) (betweenChoke2.getY()) + ")");
//		
//		Monster.Broodwar.drawCircleMap(betweenChoke2, 120, Color.White, false);
//		
//		Monster.Broodwar.drawCircleMap(betweenChoke2, 120 + turretCount * 15, Color.White, false);
//		
////		radius1 + turretCount * 15

    }

}