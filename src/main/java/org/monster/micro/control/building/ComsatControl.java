package org.monster.micro.control.building;

import bwapi.Unit;
import org.monster.common.UnitInfo;
import org.monster.micro.control.Control;

import java.util.Collection;


@Deprecated
public class ComsatControl extends Control {
    @Override
    public void control(Collection<Unit> unitList, Collection<UnitInfo> euiList) {

    }

//    private int scanUsedFrame = 0;
//    private int scanEnemySquadFrame = 10 * TimeUtils.MINUTE;
//
//    @Override
//    public void control(Collection<Unit> unitList, Collection<UnitInfo> euiList) {
//        if (TimeUtils.getFrame(scanUsedFrame) < 4 * TimeUtils.SECOND) {
//            return;
//        }
//        if (!TimeUtils.executeRotation(0, 23)) {
//            return;
//        }
//
//        Position scanPosition;
//        boolean scanAtReadyTo;
//        if (PlayerUtils.enemyRace() == Race.Terran && PositionFinder.Instance().scanAtReadyToPosition()) {
//            scanPosition = UnitTypeUtils.myReadyToPosition();
//            scanAtReadyTo = true;
//        } else { // 상대 클록 유닛
//            scanPosition = scanPositionForInvisibleEnemy(euiList);
//            scanAtReadyTo = false;
//        }
//
//        if (PositionUtils.isValidPosition(scanPosition)) {
//            if (!MapGrid.Instance().scanIsActiveAt(scanPosition)) {
//                Unit comsatMaxEnergy = null;
//                int maxEnergy = 50;
//                for (Unit comsat : unitList) {
//                    if (comsat.getEnergy() >= maxEnergy && comsat.canUseTech(TechType.Scanner_Sweep, scanPosition)) {
//                        maxEnergy = comsat.getEnergy();
//                        comsatMaxEnergy = comsat;
//                    }
//                }
//                if (comsatMaxEnergy != null) {
//                    MapGrid.Instance().scanAtPosition(scanPosition);
//                    comsatMaxEnergy.useTech(TechType.Scanner_Sweep, scanPosition);
//                    scanUsedFrame = TimeUtils.getFrame();
//
//                    if (scanAtReadyTo) {
//                        System.out.println("scan for ready to position. position=" + scanPosition + ", time=" + TimeUtils.framesToTimeString(scanUsedFrame));
//                    } else {
//                        System.out.println("scan for invisible. position=" + scanPosition + ", time=" + TimeUtils.framesToTimeString(scanUsedFrame));
//                    }
//
//                    return;
//                }
//            }
//        }
//
//        Unit comsatToUse = null;
//        int usableEnergy = 150;
//        int comsatCnt = UnitUtils.getUnitCount(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Comsat_Station);
//        if (comsatCnt > 1) {
//            usableEnergy = 135;
//        }
//        if (comsatCnt > 2) {
//
//            if (comsatCnt < 6) {
//                usableEnergy -= 20 * (UnitUtils.getUnitCount(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Comsat_Station) - 2);
//            } else {
//                usableEnergy -= 20 * 3;
//            }
//        }
//
//
//        if (UnitUtils.invisibleEnemyDiscovered() || StrategyBoard.buildTimeMap.featureEnabled(EnemyStrategyOptions.BuildTimeMap.Feature.DETECT_IMPORTANT)) {
//            usableEnergy += 50;
//
//            if (UnitUtils.getUnitCount(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Science_Vessel) > 0) {
//                usableEnergy -= 25;
//            }
//
//        }
//
//        if (usableEnergy > 195) {
//            usableEnergy = 195;
//        }
//
//        for (Unit comsatStation : unitList) {
//            if (TimeUtils.getFrame(comsatStation.getLastCommandFrame()) < 5 * TimeUtils.SECOND) {
//                continue;
//            }
//
//            if (comsatStation.getEnergy() >= usableEnergy) {
//                comsatToUse = comsatStation;
//                usableEnergy = comsatStation.getEnergy();
//            }
//        }
//
//        if (comsatToUse != null) {
//            Position scanPositionForObservation = getScanPositionForObservation();
//
//            if (PositionUtils.isValidPosition(scanPositionForObservation)) {
//                MapGrid.Instance().scanAtPosition(scanPositionForObservation);
//                comsatToUse.useTech(TechType.Scanner_Sweep, scanPositionForObservation);
//                scanUsedFrame = TimeUtils.getFrame();
//            }
//        }
//
//    }
//
//    /// 클로킹 유닛용 스캔 포지션
//    private Position scanPositionForInvisibleEnemy(Collection<UnitInfo> euiList) {
//        for (UnitInfo eui : euiList) {
//            Unit enemyUnit = eui.getUnit();
//            if (!UnitUtils.isValidUnit(enemyUnit) && eui.getType() != UnitType.Terran_Vulture_Spider_Mine) {
//                continue;
//            }
//            if (!enemyUnit.isVisible() && eui.getType() != UnitType.Terran_Vulture_Spider_Mine) {
//                continue;
//            }
//            if (enemyUnit.isDetected() && enemyUnit.getOrder() != Order.Burrowing) {
//                continue;
//            }
//            // 주위에 베슬이 있는지 확인하고 베슬이 여기로 오는 로직인지도 확인한 후에 오게 되면 패스 아니면 스캔으로 넘어간다
//            List<Unit> nearVessel = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, eui.getLastPosition(), UnitType.Terran_Science_Vessel.sightRange() * 2, UnitType.Terran_Science_Vessel);
//            if (nearVessel != null) {
//                Unit neareasetVessel = UnitUtils.getClosestUnitToPositionNotStunned(nearVessel, eui.getLastPosition());
//                if (neareasetVessel != null) {
//                    List<Unit> nearAllies = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, neareasetVessel.getPosition(), UnitType.Terran_Science_Vessel.sightRange());
//                    if (nearAllies != null && nearAllies.size() > 2) {
//                        continue;// 베슬이 올것으로 예상됨
//                    }
//                }
//            }
//
//            List<Unit> myAttackUnits = UnitUtils.getCompletedUnitList(UnitType.Terran_Vulture,
//                    UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_Goliath);
//
//            Race enemyRace = PlayerUtils.enemyRace();
//            int myAttackUnitInWeaponRangeCount = 0;
//            for (Unit myAttackUnit : myAttackUnits) {
//                WeaponType weaponType = MicroUtils.getWeapon(myAttackUnit.getType(), eui.getType());
//                if (weaponType == WeaponType.None) {
//                    continue;
//                }
//
//                int weaponMaxRange = Monster.Broodwar.self().weaponMaxRange(weaponType);
//                if (myAttackUnit.getType() == UnitType.Terran_Bunker) {
//                    weaponMaxRange = Monster.Broodwar.self().weaponMaxRange(UnitType.Terran_Marine.groundWeapon()) + 20;// + AirForceManager.AIR_FORCE_SAFE_DISTANCE;
//                }
//                int weaponRangeMargin = 15; // 쉽게 스캔을 사용해 공격할 수 있도록 두는 여유값(조절필요)
//                if (!enemyUnit.isMoving()) {
//                    weaponRangeMargin += 10;
//                }
//                int enemyUnitDistance = myAttackUnit.getDistance(eui.getLastPosition());
//
////				System.out.println("1: " + enemyUnitDistance);
////				System.out.println("2: " + (weaponMaxRange + weaponRangeMargin));
//                if (enemyUnitDistance < weaponMaxRange + weaponRangeMargin) {
//                    myAttackUnitInWeaponRangeCount++;
//
//                    if (enemyRace == Race.Protoss) {
//                        if (myAttackUnitInWeaponRangeCount >= 3) {
//                            return eui.getLastPosition();
//                        }
//                    } else if (enemyRace == Race.Terran) {
//                        if (myAttackUnitInWeaponRangeCount >= 2) {
//                            return eui.getLastPosition();
//                        }
//                    } else if (enemyRace == Race.Zerg) {
//                        if (myAttackUnitInWeaponRangeCount >= 5
//                                || myAttackUnit.getType() == UnitType.Terran_Siege_Tank_Tank_Mode
//                                || myAttackUnit.getType() == UnitType.Terran_Siege_Tank_Siege_Mode
//                                || (myAttackUnit.getType() == UnitType.Terran_Bunker && UnitUtils.getUnitCount(UnitFindStatus.COMPLETE, UnitType.Terran_Marine) > 0)) {
//                            return eui.getLastPosition();
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    /// 정찰용 스캔 포지션
//    private Position getScanPositionForObservation() {
//        // find place
//        List<TilePosition> scanTilePositionCandidate = new ArrayList<TilePosition>();
//        if (BaseUtils.enemyMainBase() != null) {
//            scanTilePositionCandidate.add(BaseUtils.enemyMainBase().getTilePosition());
//            if (PlayerUtils.enemyRace() == Race.Protoss || PlayerUtils.enemyRace() == Race.Terran) {
//                scanTilePositionCandidate.add(UnitTypeUtils.enemyFirstChoke().getCenter().toTilePosition());
//            }
//        }
//        if (BaseUtils.enemyFirstExpansion() != null) {
//            scanTilePositionCandidate.add(BaseUtils.enemyFirstExpansion().getTilePosition());
//            if (PlayerUtils.enemyRace() == Race.Protoss || PlayerUtils.enemyRace() == Race.Terran) {
//                scanTilePositionCandidate.add(ChokePointUtils.enemySecondChoke().getCenter().toTilePosition());
//            }
//        }
//
//        if (TimeUtils.afterTime(14, 0)) {
//            if (BaseUtils.getIslandBaseLocations() != null) {
//                for (BaseLocation islands : BaseUtils.getIslandBaseLocations()) {
//
//                    Position scanPosotion = islands.getPosition();
//                    MapGrid.GridCell cell = MapGrid.Instance().getCell(scanPosotion);
//                    if (cell == null) {
//                        continue;
//                    }
//                    if (TimeUtils.getFrame(cell.getTimeLastScan()) > 12000) {
//                        scanTilePositionCandidate.add(islands.getTilePosition());
//                    }
//                }
//            }
//        }
//
//
//        Position oldestCheckPosition = CheckForResourceInfoNeeded();
//
//        if (oldestCheckPosition != Position.None) {
//            System.out.println("scan resource. position=" + oldestCheckPosition + ", time=" + TimeUtils.framesToTimeString(scanUsedFrame));
//
//            return oldestCheckPosition;
//        }
//        int oldestLastCheckTime = CommonCode.INT_MAX;
//        for (TilePosition scanTilePosition : scanTilePositionCandidate) {
//            if (PlayerUtils.isVisible(scanTilePosition)) {
//                continue;
//            }
//            Position scanPosotion = scanTilePosition.toPosition();
//            MapGrid.GridCell cell = MapGrid.Instance().getCell(scanPosotion);
//
//
//            int lastScanTime = TimeUtils.getFrame(cell.getTimeLastScan());
//            int lastVisitTime = TimeUtils.getFrame(cell.getTimeLastVisited());
//            int lastCheckTime = Math.min(lastScanTime, lastVisitTime);
//
//            if (lastCheckTime < oldestLastCheckTime) {
//                oldestCheckPosition = scanPosotion;
//                oldestLastCheckTime = lastCheckTime;
//            }
//        }
//
//        if (StrategyBoard.totalEnemyCneterPosition != null
//                && StrategyBoard.mainSquadLeaderPosition != null
//                && scanEnemySquadFrame < oldestLastCheckTime) {
//
//            double radian = MicroUtils.targetDirectionRadian(StrategyBoard.mainSquadLeaderPosition, StrategyBoard.totalEnemyCneterPosition);
//            Position squadFrontPosition = MicroUtils.getMovePosition(StrategyBoard.mainSquadLeaderPosition, radian, 700).makeValid();
//
////			if (!Prebot.Broodwar.isVisibleenemyCommandInfo(squadFrontPosition.toTilePosition())) {}
//            scanEnemySquadFrame = TimeUtils.getFrame();
//            return squadFrontPosition;
//        }
//
//        return oldestCheckPosition;
//    }
//
//    private Position CheckForResourceInfoNeeded() {
//
//        Map<UnitInfo, EnemyCommandInfo> enemyResourceDepotInfoMap = AttackDecisionMaker.Instance().enemyResourceDepotInfoMap;
//        Position scanPosition = Position.None;
//
//        if (enemyResourceDepotInfoMap.size() == 0) {
//            return Position.None;
//        }
//
//
//        int earlist = CommonCode.INT_MAX;
//
//        for (Map.Entry<UnitInfo, EnemyCommandInfo> enemyResourceDepot : enemyResourceDepotInfoMap.entrySet()) {
//
//            EnemyCommandInfo enemyCommandInfo = enemyResourceDepot.getValue();
//
//            if (enemyCommandInfo.mineralCalculator.getMineralCount() * 1500 <= enemyCommandInfo.mineralCalculator.getFullCheckMineral()) {
//                continue;
//            }
//
//            int lastFullCheckFrame = enemyResourceDepot.getValue().getLastFullCheckFrame();
//            if (TimeUtils.getFrame(lastFullCheckFrame) > 2500) {
//                if (lastFullCheckFrame < earlist) {
//                    scanPosition = enemyResourceDepot.getKey().getLastPosition();
//                    earlist = lastFullCheckFrame;
//                }
//            }
//
//        }
//
//        return scanPosition;
//    }

}