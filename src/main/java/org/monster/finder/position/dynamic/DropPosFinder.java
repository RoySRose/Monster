package org.monster.finder.position.dynamic;

import org.monster.board.Decision;
import org.monster.board.Location;
import org.monster.board.StrategyBoard;
import org.monster.finder.DefaultPositionFinder;
import org.monster.finder.LocationFinder;

//TODO 드롭위치 찾기
//TODO
public class DropPosFinder extends DefaultPositionFinder implements LocationFinder {

    public DropPosFinder() {
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