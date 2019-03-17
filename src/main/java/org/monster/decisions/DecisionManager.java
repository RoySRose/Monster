package org.monster.decisions;

import org.monster.decisions.items.expansion.ExpandExpansion;
import org.monster.decisions.items.photonrush.PhotonRushed;
import org.monster.bootstrap.GameManager;

import java.util.ArrayList;
import java.util.List;

public class DecisionManager extends GameManager {

    private static DecisionManager instance = new DecisionManager();
    List<DecisionMaker> decisionMakers = new ArrayList();

    public static DecisionManager Instance() {
        return instance;
    }

    public void onStart() {

        decisionMakers.add(new ExpandExpansion());
        decisionMakers.add(new PhotonRushed());

    }

    @Override
    public void update() {

        /**
         *  이렇게 매 프레임 계산되어야 하는것이 맞는지 따져볼 필요가 있다.
         */
        for (DecisionMaker decisionMaker : decisionMakers) {
            if (decisionMaker.calculateDecision()) {
                decisionMaker.decisionLogic();
                decisionMaker.pushToStrategyBoard();
            }
        }
    }

}
