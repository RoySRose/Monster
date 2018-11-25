package prebot.main;

import bwapi.Player;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import prebot.build.initialProvider.BlockingEntrance.BlockingEntrance;
import prebot.build.initialProvider.InitialBuildProvider;
import prebot.build.base.BuildManager;
import prebot.build.base.ConstructionManager;
import prebot.build.base.ConstructionPlaceFinder;
import prebot.build.provider.BuildQueueProvider;
import prebot.common.MapGrid;
import prebot.common.debugger.BigWatch;
import prebot.common.debugger.chat.ChatBot;
import prebot.common.util.PlayerUtils;
import prebot.macro.AttackDecisionMaker;
import prebot.micro.CombatManager;
import prebot.micro.WorkerManager;
import prebot.strategy.InformationManager;
import prebot.strategy.StrategyManager;

public class GameCommander {

    private static GameCommander instance = new GameCommander();
    private UnitBalancer unitBalancer = new UnitBalancer(); // for debugging

    public static GameCommander Instance() {
        return instance;
    }

    public void onStart() {
        System.out.println("onStart() started");
        TilePosition startLocation = Monster.Broodwar.self().getStartLocation();
        if (startLocation == TilePosition.None || startLocation == TilePosition.Unknown) {
            return;
        }

        BlockingEntrance.Instance().onStart();
        ConstructionPlaceFinder.Instance().setTilesToAvoidSupply();
        ConstructionPlaceFinder.Instance().setTilesToAvoidBaseLocation();
        InitialBuildProvider.Instance().onStart();
        StrategyManager.Instance().onStart();
        AttackDecisionMaker.Instance().onStart();
        CombatManager.Instance().onStart();
        System.out.println("onStart() finished");
    }

    public void onEnd(boolean isWinner) {
        StrategyManager.Instance().onEnd(isWinner);
    }

    public void onFrame() {

        if (!playableCondition()) {
            return;
        }

        try {
            BigWatch.start("... GAME COMMANDER ...");

            InformationManager.Instance().updateTimeCheck();
            MapGrid.Instance().updateTimeCheck();
            StrategyManager.Instance().updateTimeCheck();

            // progressive & complete => initial end
            InitialBuildProvider.Instance().updateInitialBuild();
            BuildQueueProvider.Instance().updateTimeCheck();
            BuildManager.Instance().updateTimeCheck();

            ConstructionManager.Instance().updateTimeCheck();

            unitBalancer.update();

            WorkerManager.Instance().updateTimeCheck();
            CombatManager.Instance().updateTimeCheck();
            AttackDecisionMaker.Instance().updateTimeCheck();

            BigWatch.record("... GAME COMMANDER ...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUnitCreate(Unit unit) {
        InformationManager.Instance().onUnitCreate(unit);

    }

    public void onUnitDestroy(Unit unit) {
        WorkerManager.Instance().onUnitDestroy(unit);
        InformationManager.Instance().onUnitDestroy(unit);
    }

    public void onUnitMorph(Unit unit) {
        InformationManager.Instance().onUnitMorph(unit);

        WorkerManager.Instance().onUnitMorph(unit);
    }

    public void onUnitRenegade(Unit unit) {
        //Monster.Broodwar.sendText("A %s [%p] has renegaded. It is now owned by %s", unit.getType().c_str(), unit, unit.getPlayer().getName().c_str());

        InformationManager.Instance().onUnitRenegade(unit);
    }

    public void onUnitComplete(Unit unit) {
        InformationManager.Instance().onUnitComplete(unit);

        WorkerManager.Instance().onUnitComplete(unit);
    }

    public void onUnitDiscover(Unit unit) {
    }

    public void onUnitEvade(Unit unit) {
    }

    public void onUnitShow(Unit unit) {
        InformationManager.Instance().onUnitShow(unit);

    }

    public void onUnitHide(Unit unit) {
        InformationManager.Instance().onUnitHide(unit);
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

    private boolean playableCondition() {
        return !Monster.Broodwar.isPaused() && !PlayerUtils.isDisabled(Monster.Broodwar.self()) && !PlayerUtils.isDisabled(Monster.Broodwar.enemy());
    }

}