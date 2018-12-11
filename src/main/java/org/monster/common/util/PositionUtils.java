package org.monster.common.util;

import bwapi.Position;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import org.monster.common.constant.RegionType;
import org.monster.common.util.internal.MapTools;
import org.monster.finder.position.dynamic.EnemyReadyToAttackPosFinder;
import org.monster.finder.position.dynamic.MyReadyToAttackPosFinder;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PositionUtils {

    /// 유효한 position이면 true
    public static boolean isValidPosition(Position position) {
        return position != Position.None && position != Position.Invalid && position != Position.Unknown && position.isValid();
    }

    public static boolean isAllValidPosition(Position position, Position... positions) {
        if (!isValidPosition(position)) {
            return false;
        }
        for (Position p : positions) {
            if (!isValidPosition(p)) {
                return false;
            }
        }
        return true;
    }

    /// 유효한 지상 position이면 true
    public static boolean isValidGroundPosition(Position position) {
        if (!isValidPosition(position)) {
            return false;
        }
        return BWTA.getRegion(position) != null && MapUtils.isWalkable(position.getX() / 8, position.getY() / 8);
    }

    /// unit이 이동하기에 유효한 position이면 true
    public static boolean isValidPositionToMove(Position position, Unit unit) {
        return unit.isFlying() ? isValidPosition(position)
                : (isValidGroundPosition(position) && isValidGroundPath(unit.getPosition(), position));
    }

    /// 두 position 사이를 지상으로 이동할 수 있으면 true
    public static boolean isValidGroundPath(Position from, Position to) {
        return MapUtils.hasPath(from, to) && isConnected(from, to);
    }

    /// 두 position이 같은 region이거나, 가까운 choke에서 서로 연결된 region이면 true
    public static boolean isConnected(Position from, Position to) {

        Region regionSrc = BWTA.getRegion(from);

        for (Region regionNear : regionSrc.getReachableRegions()) {
            if (RegionUtils.equals(regionSrc, regionNear)) {
                return true;
            }
        }

        return false;
    }

    /// 중앙
    public static Position centerOf(Position position1, Position position2) {
        return new Position((position1.getX() + position2.getX()) / 2, (position1.getY() + position2.getY()) / 2);
    }

    /// 두 position 사이의 거리를 리턴
    public static int getGroundDistance(Position origin, Position destination) {
        return MapTools.Instance().getGroundDistance(origin, destination);
    }

    public static RegionType positionToRegionType(Position position) {
        Region positionRegion = BWTA.getRegion(position);
        Region myBaseRegion = BWTA.getRegion(BaseUtils.myMainBase().getPosition());
        if (positionRegion == myBaseRegion) {
            return RegionType.MY_BASE;
        }
        Region myExpansionRegion = BWTA.getRegion(BaseUtils.myFirstExpansion().getPosition());
        if (positionRegion == myExpansionRegion) {
            return RegionType.MY_FIRST_EXPANSION;
        }
        Region myThirdRegion = RegionUtils.myThirdRegion();
        if (positionRegion == myThirdRegion) {
            return RegionType.MY_THIRD_REGION;
        }
        if (BaseUtils.enemyMainBase() != null) {
            Region enemyBaseRegion = BWTA.getRegion(BaseUtils.enemyMainBase().getPosition());
            if (positionRegion == enemyBaseRegion) {
                return RegionType.ENEMY_BASE;
            }
            Region enemyExpansionRegion = BWTA.getRegion(BaseUtils.enemyFirstExpansion().getPosition());
            if (positionRegion == enemyExpansionRegion) {
                return RegionType.ENEMY_FIRST_EXPANSION;
            }
            Region enemyThirdRegion = RegionUtils.enemyThirdRegion();
            if (positionRegion == enemyThirdRegion) {
                return RegionType.ENEMY_THIRD_REGION;
            }
            return RegionType.ETC;
        }
        return RegionType.UNKNOWN;
    }

    public static Region regionTypeToRegion(RegionType regionType) {
        if (regionType == RegionType.MY_BASE) {
            return BWTA.getRegion(BaseUtils.myMainBase().getPosition());
        }
        if (regionType == RegionType.MY_FIRST_EXPANSION) {
            return BWTA.getRegion(BaseUtils.myFirstExpansion().getPosition());
        }
        if (regionType == RegionType.MY_THIRD_REGION) {
            return RegionUtils.myThirdRegion();
        }

        if (BaseUtils.enemyMainBase() != null) {
            if (regionType == RegionType.ENEMY_BASE) {
                return BWTA.getRegion(BaseUtils.enemyMainBase().getPosition());
            }
            if (regionType == RegionType.ENEMY_FIRST_EXPANSION) {
                return BWTA.getRegion(BaseUtils.myFirstExpansion().getPosition());
            }
            if (regionType == RegionType.ENEMY_THIRD_REGION) {
                return RegionUtils.enemyThirdRegion();
            }
        }
        return null;
    }

    public static boolean isStartingPosition(Position position) {
        for (BaseLocation startingBase : BWTA.getStartLocations()) {
            if (startingBase.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * positionList 중 position에 가장 가까운 position 리턴
     */

    public static Position getClosestPositionToPosition(List<Position> candidatePositionList, Position position, double limitDistance) {

        if (candidatePositionList.size() == 0) {
            return null;
        }
        if (!PositionUtils.isValidPosition(position)) {
            return candidatePositionList.get(0);
        }

        Position closestPosition = null;
        double closestDist = limitDistance;

        for (Position p : candidatePositionList) {
            double dist = p.getDistance(position);
            if (closestPosition == null && dist < closestDist) {
                closestPosition = p;
                closestDist = dist;
            }
        }
        return closestPosition;
    }

    public static Position randomPosition(Position sourcePosition, int dist) {
        int x = sourcePosition.getX() + ThreadLocalRandom.current().nextInt(dist) - dist / 2;
        int y = sourcePosition.getY() + ThreadLocalRandom.current().nextInt(dist) - dist / 2;
        return new Position(x, y).makeValid();
    }

    public static boolean equals(Position position1, Position position2) {
        if (position1 == null || position2 == null) {
            return false;
        }
        return position1.equals(position2);
    }

    @Deprecated
    public static Position myReadyToPosition() {
        return MyReadyToAttackPosFinder.get();
    }

    @Deprecated
    public static Position enemyReadyToPosition() {
        return EnemyReadyToAttackPosFinder.get();
    }
}
