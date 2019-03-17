package org.monster.finder;

import bwta.Chokepoint;
import org.monster.board.StrategyBoard;

public abstract class DefaultChokePointFinder implements LocationFinder {

    private static Chokepoint chokepoint;
    private static String keyString;

    public DefaultChokePointFinder() {
        this.keyString = this.getClass().getName();
    }

    @Override
    public final void process(){
        chokepoint = findLocation();
        pushToStrategyBoard();
    }

    private final void pushToStrategyBoard() {
        StrategyBoard.chokePoints.put(keyString, chokepoint);
    }

    @Override
    public final boolean isProceedCalc(){
        return isCalcLocation();
    }

    public abstract boolean isCalcLocation();

    public abstract Chokepoint findLocation();

    public final Chokepoint get(){
        return StrategyBoard.chokePoints.get(keyString);
    }
}
