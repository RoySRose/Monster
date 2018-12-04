package org.monster.debugger.chat;

import bwapi.Game;

public abstract class ChatExecuter {
    private char type;

    public ChatExecuter(char ch) {
        this.type = Character.toLowerCase(ch);
    }

    public boolean isExecuteCharacter(char ch) {
        return this.type == Character.toLowerCase(ch);
    }

    public abstract void execute(String option);

    /**
     * string을 int로 바꾼다. 숫자가 아닌 경우 default 0
     */
    protected int stringToInteger(String stringValue) {
        try {
            return Integer.parseInt(stringValue);
        } catch (NumberFormatException nfe) {
            System.out.println("stringValue is not number.");
            return 0;
        }
    }
}
