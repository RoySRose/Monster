package org.monster.finder.position.dynamic;

import org.monster.board.Decision;
import org.monster.board.Location;
import org.monster.board.StrategyBoard;
import org.monster.finder.DefaultPositionFinder;
import org.monster.finder.LocationFinder;

//TODO 뒤에 보급로 찾기
public class ThroatCuttingPosFinder extends DefaultPositionFinder implements LocationFinder {

    public ThroatCuttingPosFinder() {
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