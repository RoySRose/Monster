package org.monster.decisions;


public abstract class DefaultDecisionMaker implements DecisionMaker {


    public abstract boolean calculateDecision();

    public abstract void decisionLogic();

    public abstract void pushToStrategyBoard();
}
