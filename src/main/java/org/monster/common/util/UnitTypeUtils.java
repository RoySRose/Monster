package org.monster.common.util;

import bwapi.Race;
import bwapi.UnitType;

public class UnitTypeUtils {

    public static UnitType getBasicResourceDepotBuildingType(Race race) {
        if (race == Race.Protoss) {
            return UnitType.Protoss_Nexus;
        } else if (race == Race.Terran) {
            return UnitType.Terran_Command_Center;
        } else if (race == Race.Zerg) {
            return UnitType.Zerg_Hatchery;
        } else {
            return UnitType.None;
        }
    }

    public static UnitType getBasicProduceBuildingType(Race race) {
        if (race == Race.Protoss) {
            return UnitType.Protoss_Gateway;
        } else if (race == Race.Terran) {
            return UnitType.Terran_Barracks;
        } else if (race == Race.Zerg) {
            return UnitType.Zerg_Spawning_Pool;
        } else {
            return UnitType.None;
        }
    }

    public static UnitType getBasicDefenseBuildingType(Race race) {
        if (race == Race.Protoss) {
            return UnitType.Protoss_Photon_Cannon;
        } else if (race == Race.Terran) {
            return UnitType.Terran_Bunker;
        } else if (race == Race.Zerg) {
            return UnitType.Zerg_Sunken_Colony;
        } else {
            return UnitType.None;
        }
    }

    public static UnitType getBasicSupplyBuildingType(Race race) {
        if (race == Race.Protoss) {
            return UnitType.Protoss_Pylon;
        } else if (race == Race.Terran) {
            return UnitType.Terran_Supply_Depot;
        } else if (race == Race.Zerg) {
            return UnitType.Zerg_Overlord;
        } else {
            return UnitType.None;
        }
    }

    public static UnitType getRefineryBuildingType(Race race) {
        if (race == Race.Protoss) {
            return UnitType.Protoss_Assimilator;
        } else if (race == Race.Terran) {
            return UnitType.Terran_Refinery;
        } else if (race == Race.Zerg) {
            return UnitType.Zerg_Extractor;
        } else {
            return UnitType.None;
        }
    }

    public static UnitType getWorkerType(Race race) {
        if (race == Race.Protoss) {
            return UnitType.Protoss_Probe;
        } else if (race == Race.Terran) {
            return UnitType.Terran_SCV;
        } else if (race == Race.Zerg) {
            return UnitType.Zerg_Drone;
        } else {
            return UnitType.None;
        }
    }

    public static UnitType[] enemyAirDefenseUnitType() {
        if (PlayerUtils.enemyRace() == Race.Protoss) {
            return new UnitType[]{UnitType.Protoss_Photon_Cannon};
        } else if (PlayerUtils.enemyRace() == Race.Zerg) {
            return new UnitType[]{UnitType.Zerg_Spore_Colony};
        } else if (PlayerUtils.enemyRace() == Race.Terran) {
            return new UnitType[]{UnitType.Terran_Missile_Turret, UnitType.Terran_Bunker};
        } else {
            return new UnitType[]{};
        }
    }

    public static UnitType[] wraithKillerUnitType() {
        if (PlayerUtils.enemyRace() == Race.Protoss) {
            return new UnitType[]{UnitType.Protoss_Dragoon, UnitType.Protoss_Archon};
        } else if (PlayerUtils.enemyRace() == Race.Zerg) {
            return new UnitType[]{UnitType.Zerg_Hydralisk, UnitType.Zerg_Scourge};
        } else if (PlayerUtils.enemyRace() == Race.Terran) {
            return new UnitType[]{UnitType.Terran_Goliath};
        } else {
            return new UnitType[]{};
        }
    }

    public UnitType getObserverUnitType(Race race) {
		if (race == Race.Protoss) {
			return UnitType.Protoss_Observer;
		} else if (race == Race.Terran) {
        return UnitType.Terran_Science_Vessel;
		} else if (race == Race.Zerg) {
			return UnitType.Zerg_Overlord;
		} else {
			return UnitType.None;
		}
    }

    //TODO 나의 첫 가스위치 찾는 로직. 일단 모아둠
//    public Unit myfirstGeyser;
//    myfirstGas = null;
//    public void updateFirstGasInformation() {
//        if (selfPlayer != null && getMainBaseLocation(selfPlayer) != null
//                && getMainBaseLocation(selfPlayer).getGeysers().size() > 0) {
//            myfirstGas = getMainBaseLocation(selfPlayer).getGeysers().get(0);
//        }
//    }


//    //TODO map tile 여유분 찾기 로직 참고용
//    private void checkTileForSupply() {
//
//        int MainBaseSpaceForSup =0;
//        Polygon temp= getMainBaseLocation(selfPlayer).getRegion().getPolygon();
//        for(int y=0; y<128 ; y++){
//            for(int x=0; x<128 ; x++){
//                Position test2 = new Position(x*32+16,y*32+16);
//                if(temp.isInside(test2)){
//                    MainBaseSpaceForSup++;
//                }
//            }
//        }
//        MainBaseSuppleLimit =  (int)((MainBaseSpaceForSup - 106)/30)+5;
//    }


//TODO 모든 멀티 확인여부 참고용
//	public void setEveryMultiInfo() {
//
//		if (mainBaseLocation.get(selfPlayer) != null && mainBaseLocation.get(enemyPlayer) != null) {
//			BaseLocation sourceBaseLocation = mainBaseLocation.get(selfPlayer);
//			for (BaseLocation targetBaseLocation : BWTA.getBaseLocations())
//			{
//				if (!BWTA.isConnected(targetBaseLocation.getTilePosition(), sourceBaseLocation.getTilePosition())) continue;
//				if (targetBaseLocation.getTilePosition().equals(mainBaseLocation.get(enemyPlayer).getTilePosition())) continue;
//				if (targetBaseLocation.getTilePosition().equals(mainBaseLocation.get(selfPlayer).getTilePosition())) continue;
//				//적군 베이스도 아닐때
//				if (hasBuildingAroundBaseLocation(targetBaseLocation,enemyPlayer,10) == true) continue;
//
////				occupiedBaseLocations.
//
////				System.out.print("targetBaseLocationX : " + targetBaseLocation.getTilePosition().getX());
////				System.out.println(", targetBaseLocationY : " + targetBaseLocation.getTilePosition().getY());
////
////				System.out.println("getduration: "+ MapGrid.Instance().getCellLastVisitDuration(targetBaseLocation.getPosition()));
//				if (MapGrid.Instance().getCellLastVisitDuration(targetBaseLocation.getPosition()) > 8000)
//				{
//					ReceivingEveryMultiInfo = false;
////					System.out.println("ReceivingEveryMultiInfo1: " + ReceivingEveryMultiInfo);
//					return;
//				}
//			}
//			ReceivingEveryMultiInfo = true;
////			System.out.println("ReceivingEveryMultiInfo2: " + ReceivingEveryMultiInfo);
//		}else{
//			ReceivingEveryMultiInfo = false;
////			System.out.println("ReceivingEveryMultiInfo3: " + ReceivingEveryMultiInfo);
//		}
//	}
}
