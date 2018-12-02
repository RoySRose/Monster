package org.monster.decisions.items.lossonattack;

import org.monster.board.Decision;
import org.monster.decisions.DecisionMaker;
import org.monster.decisions.DefaultDecisionMaker;

//TODO 전장에 나가있으면 손해 볼수 있는지.. 유닛 타입별로? 예를 들어 상대 벌쳐가 나오면 저글링은 lossOnAttack = true;
public class LossOnAttack extends DefaultDecisionMaker implements DecisionMaker {

    public LossOnAttack() {
        super(Decision.LossOnAttack);
    }

    @Override
    public boolean calculateDecision() {
        return true;
    }

    @Override
    public void decisionLogic() {

    }
}
