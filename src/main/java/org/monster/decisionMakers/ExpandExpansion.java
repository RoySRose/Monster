package org.monster.decisionMakers;


import org.monster.board.StrategyBoard;

public class ExpandExpansion implements DecisionMaker {

    boolean expandExpansion;

    @Override
    public boolean calculateDecision() {
        //TODO 해당 프레임에 계산할지 여부
        return false;
    }

    @Override
    public void decisionLogic() {

    }

    @Override
    public void pushToStrategyBoard() {
        StrategyBoard.expandExpansion = expandExpansion;
    }
}
