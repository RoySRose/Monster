package org.monster.common.debugger.chat.impl;

import org.monster.main.Monster;
import org.monster.common.debugger.chat.ChatExecuter;

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