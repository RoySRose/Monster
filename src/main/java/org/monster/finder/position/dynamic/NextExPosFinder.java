package org.monster.finder.position.dynamic;

import bwapi.Position;
import org.monster.finder.position.PositionFinder;

import java.util.List;

//TODO 다음 멀티지역 찾기
public class NextExPosFinder implements PositionFinder {

    @Override
    public List<Position> getPosition() {
        return null;
    }
}

//    public BaseLocation getNextExpansionLocation() {
//
//        BaseLocation resultBase = null;
//
//        if (mainBaseLocations.get(enemyPlayer) != null) {
//            int numberOfCC = Monster.Broodwar.self().allUnitCount(UnitType.Terran_Command_Center);
////			//FileUtils.appendTextToFile("log.txt", "\n getNextExpansionLocation CommandCenter cnt :: " + numberOfCC + " :: " + Prebot.Broodwar.self().allUnitCount(UnitType.Terran_Command_Center));
////			int numberOfCC = Prebot.Broodwar.self().allUnitCount(UnitType.Terran_Command_Center) + Prebot.Broodwar.self().deadUnitCount(UnitType.Terran_Command_Center);
////			//FileUtils.appendTextToFile("log.txt", "\n getNextExpansionLocation CommandCenter cnt :: " + numberOfCC + " :: " + Prebot.Broodwar.self().allUnitCount(UnitType.Terran_Command_Center) +" + "+ Prebot.Broodwar.self().deadUnitCount(UnitType.Terran_Command_Center));
////			if (numberOfCC == 2) {
////				resultBase = getCloseButFarFromEnemyLocation(BWTA.getBaseLocations(), false, true, false, false);
//////				resultBase = getCloseButFarFromEnemyLocation(BWTA.getBaseLocations(), false, true, true);
////			}
//
//            if (numberOfCC >= 2) {
//                resultBase = getCloseButFarFromEnemyLocation(BWTA.getBaseLocations(), false, true, false, false);
////				resultBase = getCloseButFarFromEnemyLocation(BWTA.getBaseLocations(), false, true, true);
//            }
////			else if (numberOfCC == 3) {
////				resultBase = secondStartPosition;
////				if (!InfoUtils.getOccupiedRegions(PlayerUtils.myPlayer()).contains(resultBase)
////					&& InfoUtils.getOccupiedRegions(PlayerUtils.enemyPlayer()).contains(resultBase))
////					{
////						resultBase = getCloseButFarFromEnemyLocation(BWTA.getBaseLocations(), false, true, false, true);
////					}else {
////						for(Unit unit : UnitUtils.getUnitList(UnitType.Terran_Command_Center)) {
////							if(TilePositionUtils.equals(resultBase.getTilePosition(), unit.getTilePosition())) {
////								resultBase = getCloseButFarFromEnemyLocation(BWTA.getBaseLocations(), false, true, true, false);
////								break;
////							}
////						}
////
////					}
////
//////				resultBase = getCloseButFarFromEnemyLocation(BWTA.getBaseLocations(), false, true, true);
////			}
////			else{
////				resultBase = getCloseButFarFromEnemyLocation(BWTA.getBaseLocations(), false, true, true, false);
////			}
//
////			if (resultBase == null) {
////				resultBase = getCloseButFarFromEnemyLocation(BWTA.getBaseLocations(), false, true, false, false);
////			}
//
////			//FileUtils.appendTextToFile("log.txt", "\n getNextExpansionLocation numberOfCC :: " + numberOfCC + " :: " + resultBase.getTilePosition());
//        }
//
//
//        getExpansionLocation = resultBase;
//        return resultBase;
//    }