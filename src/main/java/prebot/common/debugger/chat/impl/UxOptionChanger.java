package prebot.common.debugger.chat.impl;

import prebot.common.debugger.chat.ChatExecuter;
import prebot.main.PreBotUXManager;

public class UxOptionChanger extends ChatExecuter {
    public UxOptionChanger(char type) {
        super(type);
    }

    @Override
    public void execute(String option) {
        int optionInt = stringToInteger(option);
        PreBotUXManager.Instance().setUxOption(optionInt);
    }
};