package org.monster.decisions.items.expansion;

import org.monster.board.Decision;
import org.monster.decisions.DecisionMaker;
import org.monster.decisions.DefaultDecisionMaker;

public class ExpandExpansion extends DefaultDecisionMaker implements DecisionMaker {

    public ExpandExpansion() {
        super(Decision.ExpandMyExpansion);
    }

    @Override
    public boolean calculateDecision() {
        return true;
    }

    @Override
    public void decisionLogic() {

    }

}
