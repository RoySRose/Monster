package org.monster.strategy.action;

public abstract class Action {
    private Action nextAction = null;

    public Action getNextAction() {
        return nextAction;
    }

    public void setNextAction(Action nextAction) {
        this.nextAction = nextAction;
    }

    public abstract boolean exitCondition();

    public abstract void action();
}
