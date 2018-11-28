package org.monster.decisions;

public interface DecisionMaker {

    boolean calculateDecision();

    void decisionLogic();

    void pushToStrategyBoard();
}
