package org.monster.finder.position.staticdata;

import bwapi.Position;
import org.monster.finder.DefaultPositionFinder;

//TODO 지도상 주요 거점 찾기
public class StrategicPoints extends DefaultPositionFinder {

    @Override
    public boolean isCalcLocation() {
        //return StrategyBoard.decisions.get(Decision.NeedToAdd);
        return true;
    }

    @Override
    public Position findLocation() {
        return null;
    }
}
