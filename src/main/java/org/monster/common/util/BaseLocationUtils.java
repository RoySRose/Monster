package org.monster.common.util;

import bwapi.Position;
import bwapi.TilePosition;
import bwta.BWTA;
import bwta.BaseLocation;
import org.monster.common.util.internal.IConditions;
import org.monster.common.constant.CommonCode;

import java.util.HashSet;
import java.util.List;

public class BaseLocationUtils {

    public static BaseLocation getClosestBaseToPosition(List<BaseLocation> baseList, Position position) {
        return getClosestBaseToPosition(baseList, position, new IConditions.BaseCondition() {
            @Override
            public boolean correspond(BaseLocation base) {
                return true;
            }
        });
    }

    /**
     * baseList 중 position에 가장 가까운 base 리턴
     */
    public static BaseLocation getClosestBaseToPosition(List<BaseLocation> baseList, Position position, IConditions.BaseCondition baseCondition) {
        if (baseList.size() == 0) {
            return null;
        }
        if (!PositionUtils.isValidPosition(position)) {
            return baseList.get(0);
        }

        BaseLocation closestBase = null;
        double closestDist = CommonCode.DOUBLE_MAX;

        for (BaseLocation base : baseList) {
            if (!baseCondition.correspond(base)) {
                continue;
            }
            double dist = base.getPosition().getDistance(position);
            if (closestBase == null || dist < closestDist) {
                closestBase = base;
                closestDist = dist;
            }
        }
        return closestBase;
    }

    /**
     * baseList 중 position에 가장 가까운 base 리턴
     */
    public static BaseLocation getGroundClosestBaseToPosition(List<BaseLocation> baseList, BaseLocation fromBase, IConditions.BaseCondition baseCondition) {
        if (baseList.size() == 0) {
            return null;
        }

        BaseLocation closestBase = null;
        double closestDist = CommonCode.DOUBLE_MAX;

        for (BaseLocation base : baseList) {
            if (!baseCondition.correspond(base)) {
                continue;
            }
            if (base.equals(fromBase)) {
                closestBase = base;
                break;
            }
            double dist = (double) (base.getGroundDistance(fromBase) + 0.5);
            if (closestBase == null || dist < closestDist) {
                closestBase = base;
                closestDist = dist;
            }
        }
        return closestBase;
    }

    /**
     * baseList 중 position에 가장 가까운 base 리턴
     */
    public static BaseLocation getGroundFarthestBaseToPosition(List<BaseLocation> baseList, BaseLocation fromBase, IConditions.BaseCondition baseCondition) {
        if (baseList.size() == 0) {
            return null;
        }

        BaseLocation farthestBase = null;
        double farthestDistance = CommonCode.DOUBLE_MAX;

        for (BaseLocation base : baseList) {
            if (!baseCondition.correspond(base)) {
                continue;
            }
            if (base.equals(fromBase)) {
                farthestBase = base;
                break;
            }
            double dist = (double) (base.getGroundDistance(fromBase) + 0.5);
            if (farthestBase == null || dist > farthestDistance) {
                farthestBase = base;
                farthestDistance = dist;
            }
        }
        return farthestBase;
    }

    public static HashSet<TilePosition> getBaseLocationTileHashSet() {
        HashSet<TilePosition> baseTileSet = new HashSet<>();
        for (BaseLocation base : BWTA.getBaseLocations()) {
            baseTileSet.add(base.getTilePosition());
        }
        return baseTileSet;
    }
}
