package org.monster.finder;

import bwta.BaseLocation;
import org.monster.board.Location;
import org.monster.board.StrategyBoard;

public abstract class DefaultBaseLocationFinder implements LocationFinder {

    BaseLocation baseLocation;
    Location location;

    public DefaultBaseLocationFinder(Location location) {
        this.location = location;
    }

    @Override
    public abstract boolean calculateLocation();

    @Override
    public abstract void decisionLogic();

    @Override
    public void pushToStrategyBoard() {
        StrategyBoard.locations.put(location, baseLocation);
    }
}
