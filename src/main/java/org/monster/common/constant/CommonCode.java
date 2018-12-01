package org.monster.common.constant;

import bwapi.Position;
import bwapi.TilePosition;

public class CommonCode {

    public static final int INDEX_NOT_FOUND = -1;
    public static final int NONE = -1;
    public static final int UNKNOWN = -1;

    public static final int INT_MAX = Integer.MAX_VALUE;
    public static final double DOUBLE_MAX = Double.MAX_VALUE;

    public static final Position CENTER_POS = new Position(2048, 2048);
    public static final TilePosition CENTER_TILE_POS = new TilePosition(2048, 2048);

    public static enum UnitFindStatus {
        COMPLETE, INCOMPLETE, ALL, CONSTRUCTION_QUEUE, ALL_AND_CONSTRUCTION_QUEUE
    }

    public static enum PlayerRange {
        SELF, ENEMY, NEUTRAL, ALL
    }

    public static enum EnemyUnitVisibleStatus {
        VISIBLE, ALL
    }

    public static enum RegionType {
        MY_BASE, MY_FIRST_EXPANSION, MY_THIRD_REGION, ENEMY_BASE, ENEMY_FIRST_EXPANSION, ENEMY_THIRD_REGION, ETC, UNKNOWN
    }
}
