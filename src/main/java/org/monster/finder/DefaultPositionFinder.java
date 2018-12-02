package org.monster.finder;

import bwapi.Position;
import org.monster.board.Location;
import org.monster.board.StrategyBoard;

public abstract class DefaultPositionFinder implements LocationFinder {

    Position position;
    Location location;

    public DefaultPositionFinder(Location location) {
        this.location = location;
    }

    @Override
    public abstract boolean calculateLocation();

    @Override
    public abstract void decisionLogic();

    @Override
    public void pushToStrategyBoard() {
        StrategyBoard.locations.put(location, position);
    }
}
