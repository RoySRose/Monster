package org.monster.finder;

import bwta.Chokepoint;
import org.monster.board.Location;
import org.monster.board.StrategyBoard;

public abstract class DefaultChokePointFinder implements LocationFinder {

    Chokepoint chokepoint;
    Location location;

    public DefaultChokePointFinder(Location location) {
        this.location = location;
    }

    @Override
    public abstract boolean calculateLocation();

    @Override
    public abstract void decisionLogic();

    @Override
    public void pushToStrategyBoard() {
        StrategyBoard.locations.put(location, chokepoint);
    }
}
