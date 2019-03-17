package org.monster.debugger.chat.impl;

import org.monster.debugger.chat.ChatExecuter;
import org.monster.strategy.manage.AirForceManager;

public class StrikeLevelAdjuster extends ChatExecuter {
    public StrikeLevelAdjuster(char type) {
        super(type);
    }

    @Override
    public void execute(String option) {
        int optionInt = stringToInteger(option);
        AirForceManager.Instance().setStrikeLevel(optionInt);
        AirForceManager.Instance().setDisabledAutoAdjustment(true);
    }
};