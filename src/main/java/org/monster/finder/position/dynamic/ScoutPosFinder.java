package org.monster.finder.position.dynamic;

import bwapi.Position;
import org.monster.finder.DefaultPositionFinder;

//TODO 최대한의 정보 수집을 위한
public class ScoutPosFinder extends DefaultPositionFinder {

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