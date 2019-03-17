package org.monster.finder;

import bwta.BaseLocation;
import org.monster.board.StrategyBoard;

public abstract class DefaultBaseLocationFinder implements LocationFinder {

    private static BaseLocation baseLocation;
    public static String keyString;

    public DefaultBaseLocationFinder() {
        this.keyString = this.getClass().getSimpleName();
    }

    @Override
    public final void process(){
        baseLocation = findLocation();
        pushToStrategyBoard();
    }

    private final void pushToStrategyBoard() {
        StrategyBoard.baseLocations.put(keyString, baseLocation);
    }

    @Override
    public final boolean isProceedCalc(){
        return isCalcLocation();
    }

    public abstract boolean isCalcLocation();

    public abstract BaseLocation findLocation();

    public static final BaseLocation get(){
        return StrategyBoard.baseLocations.get(keyString);
    }
}
