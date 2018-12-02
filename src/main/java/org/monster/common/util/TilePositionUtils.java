package org.monster.common.util;

import bwapi.TilePosition;
import org.monster.bootstrap.Monster;

public class TilePositionUtils {

    /// 건설 가능한 tilePosition이면 true 리턴
    public static boolean isBuildable(TilePosition tilePosition, boolean includeBuilding) {
        return tilePosition.isValid() && Monster.Broodwar.isBuildable(tilePosition, includeBuilding);
    }


    // 팩토리, 스타포트 등 건물의 애드온이 건설가능하면 true 리턴 (TODO 검증된 로직인가? 이전에 불필요할듯)
    public static boolean addOnBuildable(TilePosition tilePosition) {
        return TilePositionUtils.isBuildable(new TilePosition(tilePosition.getX() + 4, tilePosition.getY() + 1), true)
                && TilePositionUtils.isBuildable(new TilePosition(tilePosition.getX() + 5, tilePosition.getY() + 1), true)
                && TilePositionUtils.isBuildable(new TilePosition(tilePosition.getX() + 4, tilePosition.getY() + 2), true)
                && TilePositionUtils.isBuildable(new TilePosition(tilePosition.getX() + 5, tilePosition.getY() + 2), true);
    }

    public static boolean isValidTilePosition(TilePosition tilePosition) {
        return tilePosition != TilePosition.None && tilePosition != TilePosition.Unknown && tilePosition != TilePosition.Invalid && tilePosition.isValid();
    }

    public static boolean equals(TilePosition tilePosition1, TilePosition tilePosition2) {
        if (tilePosition1 == null || tilePosition2 == null) {
//			System.out.println("tile position is null. yahokuku. tilePosition1=" + tilePosition1 + ", tilePosition2" + tilePosition2);
            return false;
        }
        return tilePosition1.getX() == tilePosition2.getX() && tilePosition1.getY() == tilePosition2.getY();
    }
}
