package prebot.common.debugger.chat.impl;

import prebot.common.debugger.chat.ChatExecuter;
import prebot.strategy.manage.AirForceManager;

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