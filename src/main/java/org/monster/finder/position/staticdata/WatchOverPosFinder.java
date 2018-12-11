package org.monster.finder.position.staticdata;

import bwapi.Position;
import org.monster.finder.DefaultPositionFinder;

//TODO 정찰에 적합한 오버로드 위치 찾기
public class WatchOverPosFinder extends DefaultPositionFinder {

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
