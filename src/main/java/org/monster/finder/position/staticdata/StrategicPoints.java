package org.monster.finder.position.staticdata;

import org.monster.board.Decision;
import org.monster.board.Location;
import org.monster.board.StrategyBoard;
import org.monster.finder.DefaultPositionFinder;
import org.monster.finder.LocationFinder;

//TODO 지도상 주요 거점 찾기
public class StrategicPoints extends DefaultPositionFinder implements LocationFinder {

    public StrategicPoints() {
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