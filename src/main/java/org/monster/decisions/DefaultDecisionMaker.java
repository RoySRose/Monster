package org.monster.decisions;


import org.monster.board.Decision;
import org.monster.board.StrategyBoard;

public abstract class DefaultDecisionMaker implements DecisionMaker {

    Boolean execute;
    Decision decision;

    public DefaultDecisionMaker(Decision decision) {
        this.decision = decision;
    }

    public abstract boolean calculateDecision();

    public abstract void decisionLogic();

    public void pushToStrategyBoard() {
        StrategyBoard.decisions.put(decision, execute);
    }
}
