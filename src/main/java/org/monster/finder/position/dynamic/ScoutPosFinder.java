package org.monster.finder.position.dynamic;

import org.monster.board.Decision;
import org.monster.board.Location;
import org.monster.board.StrategyBoard;
import org.monster.finder.DefaultPositionFinder;
import org.monster.finder.LocationFinder;

//TODO 최대한의 정보 수집을 위한
public class ScoutPosFinder extends DefaultPositionFinder implements LocationFinder {

    public ScoutPosFinder() {
        super(Location.NeedToAdd);
    }

    @Override
    public boolean calculateLocation() {

        return StrategyBoard.decisions.get(Decision.NeedToAdd);
    }

    @Override
    public void decisionLogic() {

    }
}