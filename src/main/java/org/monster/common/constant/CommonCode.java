package org.monster.common.constant;

import bwapi.Position;

public class CommonCode {

    public static final int INDEX_NOT_FOUND = -1;
    public static final int NONE = -1;
    public static final int UNKNOWN = -1;

    public static final int INT_MAX = 100000000;
    public static final double DOUBLE_MAX = 100000000.0;

    public static final Position Center = new Position(2048, 2048);

    public static enum UnitFindRange {
        COMPLETE, INCOMPLETE, ALL, CONSTRUCTION_QUEUE, ALL_AND_CONSTRUCTION_QUEUE
    }

    public static enum PlayerRange {
        SELF, ENEMY, NEUTRAL, ALL
    }

    public static enum EnemyUnitFindRange {
        VISIBLE, ALL
    }

    public static enum RegionType {
        MY_BASE, MY_FIRST_EXPANSION, MY_THIRD_REGION, ENEMY_BASE, ENEMY_FIRST_EXPANSION, ENEMY_THIRD_REGION, ETC, UNKNOWN
    }
}
