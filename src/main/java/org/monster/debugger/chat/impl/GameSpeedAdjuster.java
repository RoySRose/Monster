package org.monster.debugger.chat.impl;

import org.monster.debugger.chat.ChatExecuter;
import org.monster.bootstrap.Monster;

public class GameSpeedAdjuster extends ChatExecuter {
    public GameSpeedAdjuster(char type) {
        super(type);
    }

    @Override
    public void execute(String option) {
        int optionInt = stringToInteger(option);
        Monster.Broodwar.setLocalSpeed(optionInt);
    }
};