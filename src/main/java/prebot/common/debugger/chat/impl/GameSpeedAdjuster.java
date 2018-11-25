package prebot.common.debugger.chat.impl;

import prebot.common.debugger.chat.ChatExecuter;
import prebot.main.Monster;

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