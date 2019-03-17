package org.monster.finder.position.dynamic;

import bwapi.Position;
import org.monster.finder.DefaultPositionFinder;

//TODO 뒤에 보급로 찾기
public class ThroatCuttingPosFinder extends DefaultPositionFinder {

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