package org.monster.finder.position.staticdata;

import org.monster.board.Decision;
import org.monster.board.Location;
import org.monster.board.StrategyBoard;
import org.monster.finder.DefaultPositionFinder;
import org.monster.finder.LocationFinder;

//TODO 정찰에 적합한 오버로드 위치 찾기
public class WatchOverPosFinder extends DefaultPositionFinder implements LocationFinder {

    public WatchOverPosFinder() {
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