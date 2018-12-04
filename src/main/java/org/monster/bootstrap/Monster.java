package org.monster.bootstrap;

/*
+----------------------------------------------------------------------+
| UAlbertaBot                                                          |
+----------------------------------------------------------------------+
| University of Alberta - AIIDE StarCraft Competition                  |
+----------------------------------------------------------------------+
|                                                                      |
+----------------------------------------------------------------------+
| Author: David Churchill <dave.churchill@gmail.com>                   |
+----------------------------------------------------------------------+
*/

import bwapi.DefaultBWListener;
import bwapi.Flag.Enum;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import bwta.BWTA;
import org.monster.debugger.BigWatch;
import org.monster.debugger.PreBotUXManager;
import org.slf4j.MDC;

public class Monster extends DefaultBWListener {

    private static Game Broodwar;
    private Mirror mirror = new Mirror();
    private GameCommander gameCommander;


    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    @Override
    public void onStart() {

        Broodwar = mirror.getGame();

        gameCommander = new GameCommander();

        if (Broodwar.isReplay()) {
            return;
        }

        if (Config.EnableCompleteMapInformation) {
            Broodwar.enableFlag(Enum.CompleteMapInformation.getValue());
        }

        if (Config.EnableUserInput) {
            Broodwar.enableFlag(Enum.UserInput.getValue());
        }

        Broodwar.setCommandOptimizationLevel(1);

        Broodwar.setLocalSpeed(Config.SetLocalSpeed);
        Broodwar.setFrameSkip(Config.SetFrameSkip);

        System.out.println("Map analyzing started");
        BWTA.readMap();
        BWTA.analyze();
        BWTA.buildChokeNodes();
        System.out.println("Map analyzing finished");

        try {
            gameCommander.onStart(Broodwar);
        } catch (Exception e) {

            Broodwar.sendText("[Error Stack Trace]");
            System.out.println("[Error Stack Trace]");
            for (StackTraceElement ste : e.getStackTrace()) {
                Broodwar.sendText(ste.toString());
                System.out.println(ste.toString());
            }
            Broodwar.sendText("not properly started");
        }
    }

    @Override
    public void onEnd(boolean isWinner) {
        if (isWinner) {
            System.out.println("I won the game");
        } else {
            System.out.println("I lost the game");
        }

        gameCommander.onEnd(isWinner);

        System.out.println("Match ended");
        System.exit(0);
    }

    @Override
    public void onFrame() {
        MDC.put("FRAME", Integer.toString(Broodwar.getFrameCount()));

        if (Broodwar.isReplay()) {
            return;
        }
        if (!Broodwar.isPaused()) {
            try {
                gameCommander.onFrame();
            } catch (Exception e) {

                Broodwar.sendText("[Error Stack Trace]");
                System.out.println("[Error Stack Trace]");
                for (StackTraceElement ste : e.getStackTrace()) {
                    Broodwar.sendText(ste.toString());
                    System.out.println(ste.toString());
                }
                Broodwar.sendText("GG");

            }
        }

        PreBotUXManager.Instance().update();
        BigWatch.clear();
    }

    @Override
    public void onUnitCreate(Unit unit) {
        if (!Broodwar.isReplay()) {

            gameCommander.onUnitCreate(unit);
        }
    }

    @Override
    public void onUnitDestroy(Unit unit) {
        if (!Broodwar.isReplay()) {

            gameCommander.onUnitDestroy(unit);
        }
    }

    @Override
    public void onUnitMorph(Unit unit) {
        if (!Broodwar.isReplay()) {

            gameCommander.onUnitMorph(unit);
        }
    }

    @Override
    public void onUnitComplete(Unit unit) {
        if (!Broodwar.isReplay()) {

            gameCommander.onUnitComplete(unit);
        }
    }

    @Override
    public void onUnitRenegade(Unit unit) {
        if (!Broodwar.isReplay()) {

            gameCommander.onUnitRenegade(unit);
        }
    }

    @Override
    public void onUnitDiscover(Unit unit) {
        if (!Broodwar.isReplay()) {

            gameCommander.onUnitDiscover(unit);
        }
    }

    @Override
    public void onUnitEvade(Unit unit) {
        if (!Broodwar.isReplay()) {

            gameCommander.onUnitEvade(unit);
        }
    }

    @Override
    public void onUnitShow(Unit unit) {
        if (!Broodwar.isReplay()) {

            gameCommander.onUnitShow(unit);
        }
    }

    @Override
    public void onUnitHide(Unit unit) {
        if (!Broodwar.isReplay()) {

            gameCommander.onUnitHide(unit);
        }
    }

    @Override
    public void onNukeDetect(Position target) {
        if (!Broodwar.isReplay()) {

            gameCommander.onNukeDetect(target);
        }
    }

    @Override
    public void onPlayerLeft(Player player) {
        if (!Broodwar.isReplay()) {
            gameCommander.onPlayerLeft(player);
        }
    }

    @Override
    public void onSaveGame(String gameName) {
        if (!Broodwar.isReplay()) {
            gameCommander.onSaveGame(gameName);
        }
    }

    @Override
    public void onSendText(String text) {

        gameCommander.onSendText(text);

        Broodwar.sendText(text);
    }

    @Override
    public void onReceiveText(Player player, String text) {

        Broodwar.printf(player.getName() + " said \"" + text + "\"");

        gameCommander.onReceiveText(player, text);
    }

}