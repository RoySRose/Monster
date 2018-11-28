package org.monster.main;

import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import org.monster.build.base.BuildManager;
import org.monster.build.base.ConstructionManager;
import org.monster.build.base.ConstructionPlaceFinder;
import org.monster.build.initialProvider.InitialBuildProvider;
import org.monster.build.provider.BuildQueueProvider;
import org.monster.common.MapGrid;
import org.monster.common.debugger.BigWatch;
import org.monster.common.debugger.chat.ChatBot;
import org.monster.common.util.CollectorManager;
import org.monster.common.util.UnitCache;
import org.monster.micro.CombatManager;
import org.monster.strategy.StrategyManager;
import org.monster.worker.WorkerManager;

public class GameCommander {

    private static GameCommander instance = new GameCommander();
    private UnitBalancer unitBalancer = new UnitBalancer(); // for debugging
    private Game Broodwar;

    public static GameCommander Instance() {
        return instance;
    }

    public void onStart(Game Broodwar) {

        this.Broodwar = Broodwar;

        System.out.println("onStart() started");
        TilePosition startLocation = Broodwar.self().getStartLocation();
        if (startLocation == TilePosition.None || startLocation == TilePosition.Unknown) {
            return;
        }

        CollectorManager.Instance().onStart(Broodwar);
        //BlockingEntrance.Instance().onStart(Broodwar);
        ConstructionPlaceFinder.Instance().setTilesToAvoidSupply();
        ConstructionPlaceFinder.Instance().setTilesToAvoidBaseLocation();
        InitialBuildProvider.Instance().onStart();
        StrategyManager.Instance().onStart();
        //AttackDecisionMaker.Instance().onStart();
        CombatManager.Instance().onStart();
        System.out.println("onStart() finished");
    }

    public void onEnd(boolean isWinner) {
        StrategyManager.Instance().onEnd(isWinner);
    }

    /// player가 게임을 진행할 수 없으면 true를 반환
    private boolean isDisabled(Player player) {
        return player == null || player.isDefeated() || player.leftGame();
    }

    public void onFrame() {

        if (Broodwar.isPaused() || isDisabled(Broodwar.self()) || isDisabled(Broodwar.enemy())) {
            return;
        }

        try {
            BigWatch.start("... GAME COMMANDER ...");

            //InformationManager.Instance().updateTimeCheck();
            CollectorManager.Instance().updateTimeCheck();
            MapGrid.Instance().updateTimeCheck();
            StrategyManager.Instance().updateTimeCheck();

            // progressive & complete => initial end
            InitialBuildProvider.Instance().updateInitialBuild();
            BuildQueueProvider.Instance().updateTimeCheck();
            BuildManager.Instance().updateTimeCheck();

            ConstructionManager.Instance().updateTimeCheck();

            //TODO 일단은 disable unitBalancer
            //unitBalancer.update();

            WorkerManager.Instance().updateTimeCheck();
            CombatManager.Instance().updateTimeCheck();
//            AttackDecisionMaker.Instance().updateTimeCheck();

            BigWatch.record("... GAME COMMANDER ...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUnitCreate(Unit unit) {
        UnitCache.getCurrentCache().onUnitCreate(unit);

    }

    public void onUnitDestroy(Unit unit) {
        UnitCache.getCurrentCache().onUnitDestroy(unit);
        WorkerManager.Instance().onUnitDestroy(unit);
//        AttackDecisionMaker.Instance().removeDestroyedDepot(unit);
    }

    public void onUnitMorph(Unit unit) {
        UnitCache.getCurrentCache().onUnitMorph(unit);

        WorkerManager.Instance().onUnitMorph(unit);
    }

    public void onUnitRenegade(Unit unit) {
        //Monster.Broodwar.sendText("A %s [%p] has renegaded. It is now owned by %s", unit.getType().c_str(), unit, unit.getPlayer().getName().c_str());

        UnitCache.getCurrentCache().onUnitRenegade(unit);
    }

    public void onUnitComplete(Unit unit) {
        UnitCache.getCurrentCache().onUnitComplete(unit);

        WorkerManager.Instance().onUnitComplete(unit);
    }

    public void onUnitDiscover(Unit unit) {
        //UnitCache.getCurrentCache().onUnitDiscover(unit);
    }

    public void onUnitEvade(Unit unit) {
    }

    public void onUnitShow(Unit unit) {
        UnitCache.getCurrentCache().onUnitShow(unit);
    }

    public void onUnitHide(Unit unit) {
        UnitCache.getCurrentCache().onUnitHide(unit);
    }

    public void onNukeDetect(Position target) {
    }

    public void onPlayerLeft(Player player) {
    }

    public void onSaveGame(String gameName) {
    }

    public void onSendText(String text) {
        ChatBot.operateChatBot(text);
    }

    public void onReceiveText(Player player, String text) {
    }
}