package org.monster.debugger.chat.impl;

import org.monster.debugger.PreBotUXManager;
import org.monster.debugger.chat.ChatExecuter;

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