package org.monster.common.util;

import bwapi.Game;
import bwapi.Position;
import bwapi.Race;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;

import java.util.List;

public class EnemyBaseFinder {

    private boolean finished = false;
    BaseLocation enemyBaseExpected;
    Game BroodWar;

    public EnemyBaseFinder(Game broodWar) {
        BroodWar = broodWar;
    }

    public BaseLocation find() {
        expect();
        return enemyBaseExpected;
    }

    private void expect() {
        if (finished) {
            return;
        }

        if (BaseUtils.enemyMainBase() != null) {
            enemyBaseExpected = null;
            finished = true;
            return;
        }
        if (enemyBaseExpected != null && BroodWar.isExplored(enemyBaseExpected.getTilePosition())) {
            enemyBaseExpected = null;
        }
        if (enemyBaseExpected != null) {
            return;
        }

        BaseLocation expectedByBuilding = expectedByBuilding();
        if (expectedByBuilding != null) {
            enemyBaseExpected = expectedByBuilding;
            return;
        }

        BaseLocation expectedByScout = expectedByUnit();
        if (expectedByScout != null) {
            enemyBaseExpected = expectedByScout;
        }
    }

    private BaseLocation expectedByBuilding() {
        BaseLocation baseExpected = null;
        List<UnitInfo> enemyUnits = UnitUtils.getEnemyUnitInfoList(CommonCode.EnemyUnitFindRange.ALL);

        Position centerPosition = TilePositionUtils.getCenterTilePosition().toPosition();
        for (UnitInfo eui : enemyUnits) {
            if (!eui.getType().isBuilding()) {
                continue;
            }

            int minimumDistance = 999999;
            for (BaseLocation startLocation : BWTA.getStartLocations()) {
                if (startLocation.getTilePosition().equals(BaseUtils.myMainBase().getTilePosition())) {
                    continue;
                }
                if (BroodWar.isExplored(startLocation.getTilePosition())) {
                    continue;
                }
                if (eui.getLastPosition().getDistance(centerPosition) < 500) {
                    continue;
                }

                int dist = PositionUtils.getGroundDistance(eui.getLastPosition(), startLocation.getPosition());
                if (dist < minimumDistance) {
                    baseExpected = startLocation;
                    minimumDistance = dist;
                }
            }
        }
        return baseExpected;
    }

    private BaseLocation expectedByUnit() {
        int scoutLimitFrames;
        if (PlayerUtils.enemyRace() == Race.Protoss) {
            scoutLimitFrames = TimeUtils.timeToFrames(1, 25); // 9파일런 서치 한번에 왔을때 약 1분 20초
        } else if (PlayerUtils.enemyRace() == Race.Zerg) {
            scoutLimitFrames = TimeUtils.timeToFrames(2, 35); // 9드론 서치 한번에 왔을때 약 2분 30초
        } else {
            scoutLimitFrames = TimeUtils.timeToFrames(2, 0);
        }

        if (TimeUtils.after(scoutLimitFrames)) {
            return null;
        }

        List<UnitInfo> euiList = UnitUtils.getEnemyUnitInfoList(CommonCode.EnemyUnitFindRange.ALL
                , UnitType.Protoss_Probe, UnitType.Zerg_Drone, UnitType.Terran_SCV, UnitType.Zerg_Overlord
                , UnitType.Zerg_Zergling, UnitType.Protoss_Zealot, UnitType.Terran_Marine);

        if (euiList.isEmpty()) {
            return null;
        }

        Position scoutPosition = euiList.get(0).getLastPosition();
        CommonCode.RegionType regionType = PositionUtils.positionToRegionType(scoutPosition);


        if (regionType != CommonCode.RegionType.MY_BASE && regionType != CommonCode.RegionType.MY_FIRST_EXPANSION) {
//			Prebot.Broodwar.sendText("hi");
            return null;
        }

        Position fromPosition = BaseUtils.myMainBase().getPosition();
        BaseLocation closestBase = null;
        int minimumDistance = CommonCode.INT_MAX;
        BaseLocation myBase = BaseUtils.myMainBase();
        for (BaseLocation startLocation : BWTA.getStartLocations()) {
            if (startLocation.getTilePosition().equals(myBase.getTilePosition())) {
                continue;
            }
            if (BroodWar.isExplored(startLocation.getTilePosition())) {
                continue;
            }

            int groundDistance = PositionUtils.getGroundDistance(startLocation.getPosition(), fromPosition);
            if (groundDistance < minimumDistance) {
                closestBase = startLocation;
                minimumDistance = groundDistance;
            }
        }
        return closestBase;
    }

}
