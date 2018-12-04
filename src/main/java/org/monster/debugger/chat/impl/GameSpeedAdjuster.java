package org.monster.debugger.chat.impl;

import bwapi.Game;
import org.monster.debugger.chat.ChatExecuter;
import org.monster.bootstrap.Monster;

public class GameSpeedAdjuster extends ChatExecuter {
    protected Game Broodwar;

    public GameSpeedAdjuster(char type, Game Broodwar) {
        super(type);
        this.Broodwar = Broodwar;
    }

    @Override
    public void execute(String option) {
        int optionInt = stringToInteger(option);
        Broodwar.setLocalSpeed(optionInt);
    }
};