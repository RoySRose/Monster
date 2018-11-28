package org.monster.finder.baselocation;

import bwapi.Position;
import org.monster.finder.position.PositionFinder;

import java.util.List;

//TODO
public class SecondMainBaseFinder implements PositionFinder {

    @Override
    public List<Position> getPosition() {
        return null;
    }
}

//    public void updateMySecondBaseLocation() {
//
//        boolean enemyRegion = false;
//
//        if (secondStartPosition == null) {
//
////	    	//FileUtils.appendTextToFile("log.txt", "\n updateMySecondBaseLocation :: new One");
//
//            int closestDistance = 99999999;
//            BaseLocation resultBase = null;
//
//            for (BaseLocation baseLocation : BWTA.getStartLocations()) {
//
//                if (baseLocation.getTilePosition().equals(mainBaseLocations.get(enemyPlayer).getTilePosition()))
//                    continue;
//                if (baseLocation.getTilePosition().equals(mainBaseLocations.get(selfPlayer).getTilePosition()))
//                    continue;
//
////            	20180815. hkk. 해당지역에 적 메인 건물이 있거나, 건물이 5개 초과이면 적 지역이라고 인식한다.
//                List<Unit> enemyBuilding = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.ENEMY, baseLocation.getPosition(), 350);
//                for (Unit unit : enemyBuilding) {
//                    if (unit.getType().isResourceDepot()) {
////            			//FileUtils.appendTextToFile("log.txt", "\n updateMySecondBaseLocation :: there is enemyRegion :: " + unit.getType());
//                        enemyRegion = true;
//                    }
//
//                }
//
//                if (enemyBuilding.size() > 5) {
////            		//FileUtils.appendTextToFile("log.txt", "\n updateMySecondBaseLocation :: there is enemyRegion :: building cnt:: " + enemyBuilding.size());
//                    enemyRegion = true;
//                }
//
//                if (enemyRegion) continue;
//
//                int enemyFirstToBase = PositionUtils.getGroundDistance(firstExpansionLocation.get(enemyPlayer).getPosition(), baseLocation.getPosition());
//                int selfFirstToBase = PositionUtils.getGroundDistance(firstExpansionLocation.get(selfPlayer).getPosition(), baseLocation.getPosition());
//
//                int closeFromMyExpansionButFarFromEnemy = selfFirstToBase - enemyFirstToBase;
//
//                if (closeFromMyExpansionButFarFromEnemy < closestDistance) {
//                    closestDistance = closeFromMyExpansionButFarFromEnemy;
//                    resultBase = baseLocation;
//                }
//            }
//            secondStartPosition = resultBase;
//        }
//    }