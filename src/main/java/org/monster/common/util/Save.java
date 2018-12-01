//package org.monster.common.util;
//
//import bwapi.Player;
//import bwapi.Position;
//import bwapi.Unit;
//import bwapi.UnitType;
//import bwapi.WeaponType;
//import org.monster.common.UnitInfo;
//import org.monster.micro.constant.MicroConfig;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class Save {
//
//
//    /// 해당 Player (아군 or 적군) 의 position 주위의 유닛 목록을 unitInfo 에 저장합니다
//    public List<UnitInfo> getNearbyForce(Position p, Player player, int radius) {
//        List<UnitInfo> unitInfo = new ArrayList<>();
//        getNearbyForce(unitInfo, p, player, radius, false);
//        return unitInfo;
//    }
//
//    public List<UnitInfo> getNearbyForce(Position p, Player player, int radius, boolean allUnits) {
//        List<UnitInfo> unitInfo = new ArrayList<>();
//        getNearbyForce(unitInfo, p, player, radius, allUnits);
//        return unitInfo;
//    }
//
//    public void getNearbyForce(List<UnitInfo> unitInfo, Position p, Player player, int radius) {
//        getNearbyForce(unitInfo, p, player, radius, false);
//    }
//
//    public void getNearbyForce(List<UnitInfo> unitInfo, Position p, Player player, int radius, boolean allUnits) {
//        Iterator<Integer> it = getUnitData(player).getUnitAndUnitInfoMap().keySet().iterator();
//
//        // for each unit we know about for that player
//        // for (final Unit kv :
//        // getUnitData(player).getUnits().keySet().iterator()){
//
//        int currFrame = TimeUtils.getFrame();
//        while (it.hasNext()) {
//            final UnitInfo ui = getUnitData(player).getUnitAndUnitInfoMap().get(it.next());
//            if (unitInfo.contains(ui)) {
//                continue;
//            }
//
//            // if it's a combat unit we care about
//            // and it's finished!
//            if (allUnits || ui.getType() == UnitType.Terran_Barracks || ui.getType() == UnitType.Terran_Engineering_Bay
//                    || (isCombatUnitType(ui.getType()) && ui.isCompleted())) {
//                if (!ui.getType().isBuilding()
//                        && (currFrame - ui.getUpdateFrame()) > MicroConfig.Common.NO_UNIT_FRAME(ui.getType())) {
//                    continue;
//                }
//
//                // determine its attack range
//                int range = 0;
//                if (ui.getType().groundWeapon() != WeaponType.None) {
//                    range = ui.getType().groundWeapon().maxRange() + 40;
//                }
//
//                // if it can attack into the radius we care about
//                if (ui.getLastPosition().getDistance(p) <= (radius + range)) {
//                    // add it to the vector
//                    // C++ : unitInfo.push_back(ui);
//                    unitInfo.add(ui);
//                }
//            } else if (ui.getType().isDetector() && ui.getLastPosition().getDistance(p) <= (radius + 250)) {
//                if (unitInfo.contains(ui)) {
//                    continue;
//                }
//                // add it to the vector
//                // C++ : unitInfo.push_back(ui);
//                unitInfo.add(ui);
//            }
//        }
//    }
//
//
//    public List<UnitInfo> getEnemyUnitsNear(Unit myunit, int radius, boolean ground, boolean air) {
//        List<UnitInfo> units = new ArrayList<>();
//
//        Iterator<Integer> it = null;
//        it = unitData.get(enemyPlayer).getUnitAndUnitInfoMap().keySet().iterator();
//
//        while (it.hasNext()) {
//            final UnitInfo ui = unitData.get(enemyPlayer).getUnitAndUnitInfoMap().get(it.next());
//            if (ui != null) {
//                if (myunit.getDistance(ui.getLastPosition()) > radius) {
//                    continue;
//                }
//                if (ui.getType().isBuilding()) {
//                    if (ground) {
//                        if (ui.getType().groundWeapon() != WeaponType.None) {
//                            units.add(ui);
//                        }
//                    }
//                    if (air) {
//                        if (ui.getType().airWeapon() != WeaponType.None) {
//                            units.add(ui);
//                        }
//                    }
//                }
//            }
//        }
//
//        return units;
//    }
//
//    public List<UnitInfo> getEnemyBuildingUnitsNear(Unit myunit, int radius, boolean canAttack, boolean ground,
//                                                    boolean air) {
//        List<UnitInfo> units = new ArrayList<>();
//
//        Iterator<Integer> it = null;
//        it = unitData.get(enemyPlayer).getUnitAndUnitInfoMap().keySet().iterator();
//
//        while (it.hasNext()) {
//            final UnitInfo ui = unitData.get(enemyPlayer).getUnitAndUnitInfoMap().get(it.next());
//            if (ui != null) {
//
//                if (ui.getLastPosition() == Position.None) {
//                    continue;
//                }
//                if (myunit.getDistance(ui.getLastPosition()) > radius) {
//                    continue;
//                }
//                if (ui.getType().isBuilding()) {
//                    if (canAttack != true) {
//                        units.add(ui);
//                    } else {
//                        if (ground) {
//                            if (ui.getType().groundWeapon() != WeaponType.None) {
//                                units.add(ui);
//                            }
//                        }
//                        if (air) {
//                            if (ui.getType().airWeapon() != WeaponType.None) {
//                                units.add(ui);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return units;
//    }
//
//    public List<UnitInfo> getEnemyBuildingUnitsNear(Position myunit, int radius, boolean canAttack, boolean ground,
//                                                    boolean air) {
//        List<UnitInfo> units = new ArrayList<>();
//
//        Iterator<Integer> it = null;
//        it = unitData.get(enemyPlayer).getUnitAndUnitInfoMap().keySet().iterator();
//
//        while (it.hasNext()) {
//            final UnitInfo ui = unitData.get(enemyPlayer).getUnitAndUnitInfoMap().get(it.next());
//            if (ui != null) {
//
//                if (ui.getLastPosition() == Position.None) {
//                    continue;
//                }
//                if (myunit.getDistance(ui.getLastPosition()) > radius) {
//                    continue;
//                }
//                if (ui.getType().isBuilding()) {
//                    if (canAttack != true) {
//                        units.add(ui);
//                    } else {
//                        if (ground) {
//                            if (ui.getType().groundWeapon() != WeaponType.None) {
//                                units.add(ui);
//                            }
//                        }
//                        if (air) {
//                            if (ui.getType().airWeapon() != WeaponType.None) {
//                                units.add(ui);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return units;
//    }
//
//private void updateBlockingEnterance() {
//        // 터렛지어지면 더이상 체크 안함
//        if (UnitUtils.myUnitDiscovered(UnitType.Terran_Missile_Turret)
//                && !UnitUtils.enemyUnitDiscovered(UnitType.Protoss_Dark_Templar)
//                || TimeUtils.getFrame() > 12000) {
//            return;
//        }
//        // 저그는 입막 안하므로 체크 안함
//        if (Monster.Broodwar.self().getRace() == Race.Zerg) {
//            blockingEnterance = false;
//            return;
//        }
//        // update our units info
//        boolean firstBarrack = false;
//        boolean firstSupple = false;
//        boolean secondSupple = false;
//
//        TilePosition firstBarracks = BlockingEntrance.Instance().barrack;
//        TilePosition firstSupplePos = BlockingEntrance.Instance().first_supple;
//        TilePosition secondSupplePos = BlockingEntrance.Instance().second_supple;
//
//        for (Unit supple : UnitUtils.getUnitList(CommonCode.UnitFindStatus.ALL, UnitType.Terran_Supply_Depot)) {
//            if (supple.getTilePosition().equals(firstSupplePos)) {
//                firstSupple = true;
//                if (safePosition == null) {
//                    earlyDefenseSafePosition(UnitType.Terran_Marine, supple);
//                    earlyDefenseHoldePosition(UnitType.Terran_Marine, supple);
//                }
//            } else if (supple.getTilePosition().equals(secondSupplePos)) {
//                secondSupple = true;
//            }
//        }
//
//        for (Unit barrack : UnitUtils.getUnitList(CommonCode.UnitFindStatus.ALL, UnitType.Terran_Barracks)) {
//            if (barrack.getTilePosition().equals(firstBarracks) && !barrack.isLifted()) {
//                firstBarrack = true;
//                break;
//            }
//        }
//
//        if (firstBarrack && firstSupple && secondSupple) {
//            blockingEnterance = true;
//        } else {
//            blockingEnterance = false;
//        }
//    }


///* 입막시 마린 안전 방어 지역 (다른 유닛 필요시 사용) */
//public void earlyDefenseSafePosition(UnitType unitType, Unit supple) {
//        Position firstCheokePoint = InformationManager.Instance()
//        .getFirstChokePoint(PlayerUtils.myPlayer()).getPoint();
//
//        int reverseX = supple.getPosition().getX() - firstCheokePoint.getX(); // 타겟과 반대로 가는 x양
//        int reverseY = supple.getPosition().getY() - firstCheokePoint.getY(); // 타겟과 반대로 가는 y양
//final double fleeRadian = Math.atan2(reverseY, reverseX); // 회피 각도
//
//        double fleeRadianAdjust = fleeRadian; // 회피 각(radian)
//        int moveCalcSize = (int) (unitType.topSpeed() * 15);
//        Position fleeVector = new Position((int) (moveCalcSize * Math.cos(fleeRadianAdjust)),
//        (int) (moveCalcSize * Math.sin(fleeRadianAdjust))); // 이동벡터
//        safePosition = new Position(supple.getPosition().getX() + fleeVector.getX(),
//        supple.getPosition().getY() + fleeVector.getY()); // 회피지점
//
//        }
//
///* 입막시 마린 홀드컨트롤 지역 (다른 유닛 필요시 사용) */
//public void earlyDefenseHoldePosition(UnitType unitType, Unit supple) {
//        Position firstCheokePoint = InformationManager.Instance()
//        .getFirstChokePoint(PlayerUtils.myPlayer()).getCenter();
//
//        int reverseX = firstCheokePoint.getX() - supple.getPosition().getX(); // 타겟과 반대로 가는 x양
//        int reverseY = firstCheokePoint.getY() - supple.getPosition().getY(); // 타겟과 반대로 가는 y양
//final double fleeRadian = Math.atan2(reverseY, reverseX); // 회피 각도
//
//        double fleeRadianAdjust = fleeRadian; // 회피 각(radian)
//        int moveCalcSize = (int) (unitType.topSpeed() * 40);
//        if (StaticMapUtils.getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.CIRCUITBREAKER) {
//        moveCalcSize = (int) (unitType.topSpeed() * 40);
//        } else if (StaticMapUtils.getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.FIGHTING_SPIRITS) {
//        moveCalcSize = (int) (unitType.topSpeed() * 30);
//        }
//        Position fleeVector = new Position((int) (moveCalcSize * Math.cos(fleeRadianAdjust)),
//        (int) (moveCalcSize * Math.sin(fleeRadianAdjust))); // 이동벡터
//        holdConPosition = new Position(supple.getPosition().getX() + fleeVector.getX(),
//        supple.getPosition().getY() + fleeVector.getY()); // 회피지점
//
//        }
//}.

//
//public TilePosition getLastBuilingFinalLocation() {
//        BaseLocation closeButFarFromEnemyLocation = getCloseButFarFromEnemyLocation(BWTA.getBaseLocations(), false);
//        if (closeButFarFromEnemyLocation != null) {
//        closeButFarFromEnemyLocation.getTilePosition();
//        return closeButFarFromEnemyLocation.getTilePosition();
//        } else {
//        System.out.println("이거모야!! 나오면 안되니까 나오면 성욱이에게!");
//        return null;
//        }
//        }
//
//public BaseLocation getCloseButFarFromEnemyLocation(List<BaseLocation> bases, boolean onlyStartLocation) {
//        return getCloseButFarFromEnemyLocation(bases, onlyStartLocation, false, false, false);
//        }
//
//private BaseLocation getCloseButFarFromEnemyLocation(List<BaseLocation> bases, boolean onlyStartLocation,
//        boolean isMulti, boolean onlyGasMulti, boolean thirdPosition) {
//        BaseLocation resultBase = null;
//        BaseLocation mainBaseLocation = mainBaseLocations.get(selfPlayer);
//        BaseLocation enemyBaseLocation = mainBaseLocations.get(enemyPlayer);
//
//        BaseLocation firstExpansion = firstExpansionLocation.get(selfPlayer);
//
//        double firstExpansionToOccupied = 0;
//        double enemyBaseToOccupied = 0;
//        double closeFromMyExpansionButFarFromEnemy = 0;
//        double distanceToSecondExpansion = mainBaseLocation.getDistance(secondStartPosition);
//
//        double closestDistance = 1000000000;
//        double closestDistanceToSecondExp = 0;
//
////		//FileUtils.appendTextToFile("log.txt", "\n getCloseButFarFromEnemyLocation start");
//
//        for (BaseLocation base : bases) {
//        if (onlyStartLocation && !base.isStartLocation())
//        continue;
//        if (base.getTilePosition().equals(mainBaseLocation.getTilePosition()))
//        continue;
//        if (base.getTilePosition().equals(enemyBaseLocation.getTilePosition()))
//        continue;
//
////			//FileUtils.appendTextToFile("log.txt", "\n getCloseButFarFromEnemyLocation chk this base ::" + base.getTilePosition());
//
//        if (isMulti) {
//
////				//FileUtils.appendTextToFile("log.txt", "\n getCloseButFarFromEnemyLocation isMulti True");
//
//        if (firstExpansionLocation.get(enemyPlayer) != null) {
//        if (base.getTilePosition().equals(firstExpansionLocation.get(enemyPlayer).getTilePosition()))
//        continue;
//        }
//
//        if (base.getTilePosition().equals(firstExpansionLocation.get(selfPlayer).getTilePosition()))
//        continue;
//
//        if (hasBuildingAroundBaseLocation(base, enemyPlayer, 6))
//        continue;
//
//        if (hasBuildingAroundBaseLocation(base, selfPlayer, 10, UnitType.Terran_Command_Center))
//        continue;
//
//        }
//
//        if (onlyGasMulti) {
//        TilePosition findGeyser = ConstructionPlaceFinder.Instance()
//        .getRefineryPositionNear(base.getTilePosition());
//        if (findGeyser != null) {
//        if (findGeyser.getDistance(base.getTilePosition()) * 32 > 300) {
//        continue;
//        }
//        }
//        }
//
//
//        firstExpansionToOccupied = firstExpansion.getGroundDistance(base); // 내 앞마당 ~ 내 점령지역 지상거리
//        enemyBaseToOccupied = enemyBaseLocation.getGroundDistance(base); // 적 베이스 ~ 내 점령지역 지상거리
//        closeFromMyExpansionButFarFromEnemy = firstExpansionToOccupied - enemyBaseToOccupied;
//
//        if (closeFromMyExpansionButFarFromEnemy < closestDistance && firstExpansionToOccupied > 0) {
////				//FileUtils.appendTextToFile("log.txt", "\n getCloseButFarFromEnemyLocation closeFromMyExpansionButFarFromEnemy < closestDistance :: " + (int)closeFromMyExpansionButFarFromEnemy +" < " + (int)closestDistance);
////				if(thirdPosition) {
//////					//FileUtils.appendTextToFile("log.txt", "\n getCloseButFarFromEnemyLocation thirdPosition is true");
////					closestDistanceToSecondExp = secondStartPosition.getGroundDistance(base);
////					if(closestDistanceToSecondExp < distanceToSecondExpansion) {
////						closestDistanceToSecondExp = distanceToSecondExpansion;
////						closestDistance = closeFromMyExpansionButFarFromEnemy;
//////						//FileUtils.appendTextToFile("log.txt", "\n getCloseButFarFromEnemyLocation thirdPosition true set resultBase :: " + base.getTilePosition());
////						resultBase = base;
////					}
////				}else {
//        closestDistance = closeFromMyExpansionButFarFromEnemy;
////					//FileUtils.appendTextToFile("log.txt", "\n getCloseButFarFromEnemyLocation set resultBase :: " + base.getTilePosition());
//        resultBase = base;
////				}
//        }
//        }
//
//        return resultBase;
//        }

/// 해당 UnitType 이 전투 유닛인지 리턴합니다
//public final boolean isCombatUnitType(UnitType type) {
//        if (type == UnitType.Zerg_Lurker /* || type == UnitType.Protoss_Dark_Templar */) {
//        // return false; 왜 false로 되어 있나?
//        return true;
//        }
//
//        // check for various types of combat units
//        if (type.canAttack() || type == UnitType.Terran_Medic || type == UnitType.Protoss_Observer
//        || type == UnitType.Protoss_Carrier || type == UnitType.Terran_Bunker
//        || type == UnitType.Protoss_High_Templar) {
//        return true;
//        }
//
//        return false;
//        }