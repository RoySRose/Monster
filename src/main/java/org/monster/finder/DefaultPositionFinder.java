package org.monster.finder;

import bwapi.Position;
import org.monster.board.StrategyBoard;

public abstract class DefaultPositionFinder implements LocationFinder {

    private static Position position;
    private static String keyString;

    public DefaultPositionFinder() {
        this.keyString = this.getClass().getName();
    }

    @Override
    public final void process(){
        position = findLocation();
        pushToStrategyBoard();
        afterCalc();
    }

    private final void pushToStrategyBoard() {
        StrategyBoard.positions.put(keyString, position);
    }

    @Override
    public final boolean isProceedCalc(){
        return isCalcLocation();
    }

    public void afterCalc(){};

    public abstract boolean isCalcLocation();

    public abstract Position findLocation();

    public static final Position get(){
        return StrategyBoard.positions.get(keyString);
    }
}
