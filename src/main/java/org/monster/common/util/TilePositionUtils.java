package org.monster.common.util;

import bwapi.TilePosition;

public class TilePositionUtils {

    public static boolean isValidTilePosition(TilePosition tilePosition) {
        return tilePosition != TilePosition.None && tilePosition != TilePosition.Unknown && tilePosition != TilePosition.Invalid && tilePosition.isValid();
    }

    public static boolean equals(TilePosition tilePosition1, TilePosition tilePosition2) {
        if (tilePosition1 == null || tilePosition2 == null) {
            return false;
        }
        return tilePosition1.equals(tilePosition2);
    }
}
