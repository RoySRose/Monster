package org.monster.common.util;

import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.Region;
import org.monster.common.UnitInfo;
import org.monster.common.util.internal.MapSpecificInformation;
import org.monster.micro.CombatManager;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.squad.Squad;

import java.util.List;
import java.util.Set;

public class InfoUtils {

    public static Position myReadyToPosition() {

        //TODO disable
        return null;
        //return InformationManager.Instance().getReadyToAttackPosition(Monster.Broodwar.self());
    }

    public static Position enemyReadyToPosition() {
        //TODO disable
        return null;
        //return InformationManager.Instance().getReadyToAttackPosition(Monster.Broodwar.enemy());
    }

    public static Unit myBaseGas() {
        if (BaseUtils.myMainBase() != null) {
            List<Unit> geysers = BaseUtils.myMainBase().getGeysers();
            if (geysers != null && !geysers.isEmpty()) {
                return geysers.get(0);
            }
        }
        return null;
    }

    public static Unit enemyBaseGas() {
        if (BaseUtils.enemyMainBase() != null) {
            List<Unit> geysers = BaseUtils.enemyMainBase().getGeysers();
            if (geysers != null && !geysers.isEmpty()) {
                return geysers.get(0);
            }
        }
        return null;
    }


    public static MapSpecificInformation mapInformation() {
        return StaticMapUtils.getMapSpecificInformation();
    }

    public static List<UnitInfo> euiListInMyRegion(Region myRegion) {
        //TODO disable
        return null;

//        List<UnitInfo> euiListInMyRegion = InformationManager.Instance().getEuiListInMyRegion(myRegion);
//        if (euiListInMyRegion == null) {
//            euiListInMyRegion = new ArrayList<>();
//        }
//        return euiListInMyRegion;
    }

    public static Set<UnitInfo> euiListInBase() {
        //TODO disable
        return null;

//        return InformationManager.Instance().getEuisInBaseRegion();
    }

    public static Set<UnitInfo> euiListInExpansion() {
        //TODO disable
        return null;
//        return InformationManager.Instance().getEuisInExpansionRegion();
    }

    public static Set<UnitInfo> euiListInThirdRegion() {
        //TODO disable
        return null;
//        return InformationManager.Instance().getEuisInThirdRegion();
    }

    public static Region myThirdRegion() {
        //TODO disable
        return null;
//        return InformationManager.Instance().getThirdRegion(Monster.Broodwar.self());
    }

    public static Region enemyThirdRegion() {
        //TODO disable
        return null;
//        return InformationManager.Instance().getThirdRegion(Monster.Broodwar.enemy());
    }


    public static int squadUnitSize(MicroConfig.SquadInfo squadInfo) {
        Squad squad = CombatManager.Instance().squadData.getSquad(squadInfo.squadName);
        if (squad != null && squad.unitList != null) {
            return squad.unitList.size();
        } else {
            return 0;
        }
    }

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

    public UnitType getObserverUnitType(Race race) {
//		if (race == Race.Protoss) {
//			return UnitType.Protoss_Observer;
//		} else if (race == Race.Terran) {
        return UnitType.Terran_Science_Vessel;
//		} else if (race == Race.Zerg) {
//			return UnitType.Zerg_Overlord;
//		} else {
//			return UnitType.None;
//		}
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

    //TODO 나의 첫 가스위치 찾는 로직. 일단 모아둠
//    public Unit myfirstGeyser;
//    myfirstGas = null;
//    public void updateFirstGasInformation() {
//        if (selfPlayer != null && getMainBaseLocation(selfPlayer) != null
//                && getMainBaseLocation(selfPlayer).getGeysers().size() > 0) {
//            myfirstGas = getMainBaseLocation(selfPlayer).getGeysers().get(0);
//        }
//    }


    //TODO 해당 baselocation에 빌딩 여부 판단

//    public boolean hasBuildingAroundBaseLocation(BaseLocation baseLocation, Player player) {
//        return hasBuildingAroundBaseLocation(baseLocation, player, 10);
//    }
//
//    public boolean hasBuildingAroundBaseLocation(BaseLocation baseLocation, Player player, int radius) {
//        return hasBuildingAroundBaseLocation(baseLocation, player, radius, null);
//    }
//
//    public boolean hasBuildingAroundBaseLocation(BaseLocation baseLocation, Player player, int radius,
//                                                 UnitType unitType) {
//
//        // invalid regions aren't considered the same, but they will both be null
//        if (baseLocation == null) {
//            return false;
//        }
//        // 반지름 10 (TilePosition 단위) 이면 거의 화면 가득이다
//        if (radius > 10) {
//            radius = 10;
//        }
//
//        if (unitData.get(player) != null) {
//            Iterator<Integer> it = unitData.get(player).getUnitAndUnitInfoMap().keySet().iterator();
//
//            while (it.hasNext()) {
//                final UnitInfo ui = unitData.get(player).getUnitAndUnitInfoMap().get(it.next());
//                if (unitType != null && ui.getType() != unitType) {
//                    continue;
//                }
//                if (ui.getType().isBuilding()) {
//
//                    // 띄워졌있는 배럭, 엔베는 차지한 영역으로 안쓴다. 왜냐면 우리는 이것들을 시야확보용으로 쓸 것이기 때문이다.
//                    if (player == Monster.Broodwar.self() && UnitUtils.isCompleteValidUnit(ui.getUnit())
//                            && ui.getUnit().isLifted() && (ui.getType() == UnitType.Terran_Barracks
//                            || ui.getType() == UnitType.Terran_Engineering_Bay)) {
//                        continue;
//                    }
//
//                    TilePosition buildingPosition = ui.getLastPosition().toTilePosition();
//
//                    if (BWTA.getRegion(buildingPosition) != BWTA.getRegion(baseLocation.getTilePosition())) { // basicbot
//                        // 1.2
//                        continue;
//                    }
//
////					System.out.print("buildingPositionX : " + buildingPosition.getX());
////					System.out.println(", buildingPositionY : " + buildingPosition.getY());
////					System.out.print("baseLocationX : " + baseLocation.getTilePosition().getX());
////					System.out.println(", baseLocationY : " + baseLocation.getTilePosition().getY());
//
//                    if (buildingPosition.getX() >= baseLocation.getTilePosition().getX() - radius
//                            && buildingPosition.getX() <= baseLocation.getTilePosition().getX() + radius
//                            && buildingPosition.getY() >= baseLocation.getTilePosition().getY() - radius
//                            && buildingPosition.getY() <= baseLocation.getTilePosition().getY() + radius) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
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
