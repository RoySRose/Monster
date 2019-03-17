package org.monster.finder.position.dynamic;

import bwapi.Position;
import org.monster.finder.DefaultPositionFinder;

//TODO 드롭위치 찾기
public class DropPosFinder extends DefaultPositionFinder {

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