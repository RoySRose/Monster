package org.monster.decisions.strategy.manage;

import bwapi.Race;
import bwapi.TechType;
import bwapi.UnitType;
import org.monster.board.StrategyBoard;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.decisions.constant.EnemyStrategyOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnemyBuildTimer {

    private static EnemyBuildTimer instance = new EnemyBuildTimer();
    // lair, hydraden, spire, core, adun, templar arch, robotics, robo support, factory, starport
    public Map<UnitType, Integer> buildTimeExpectMap = new HashMap<>();
    public Map<UnitType, Integer> buildTimeMinimumMap = new HashMap<>();
    public Set<UnitType> buildTimeCertain = new HashSet<>();
    public int darkTemplarInMyBaseFrame = CommonCode.UNKNOWN;
    public int reaverInMyBaseFrame = CommonCode.UNKNOWN;
    public int mutaliskInMyBaseFrame = CommonCode.UNKNOWN;
    public int lurkerInMyBaseFrame = CommonCode.UNKNOWN;
    public int cloakingWraithFrame = CommonCode.UNKNOWN;
    private Map<UnitType, List<UnitType>> nextBuildingMap = new HashMap<>();

    public EnemyBuildTimer() {
        nextBuildingMap.put(UnitType.Protoss_Cybernetics_Core, Arrays.asList(UnitType.Protoss_Citadel_of_Adun, UnitType.Protoss_Robotics_Facility));//, UnitType.Protoss_Stargate));
        nextBuildingMap.put(UnitType.Protoss_Citadel_of_Adun, Arrays.asList(UnitType.Protoss_Templar_Archives));
        nextBuildingMap.put(UnitType.Protoss_Robotics_Facility, Arrays.asList(UnitType.Protoss_Robotics_Support_Bay, UnitType.Protoss_Observatory));
//		nextBuildingMap.put(UnitType.Protoss_Stargate, Arrays.asList(UnitType.Protoss_Fleet_Beacon));

        nextBuildingMap.put(UnitType.Zerg_Spawning_Pool, Arrays.asList(UnitType.Zerg_Lair, UnitType.Zerg_Hydralisk_Den));
        nextBuildingMap.put(UnitType.Zerg_Lair, Arrays.asList(UnitType.Zerg_Spire));
    }

    public static EnemyBuildTimer Instance() {
        return instance;
    }

    public int getBuildStartFrameExpect(UnitType buildingType) {
        Integer expectFrame = buildTimeExpectMap.get(buildingType);
        return expectFrame == null ? CommonCode.UNKNOWN : expectFrame;
    }

    private void setImportantTime() {
        Race enemyRace = PlayerUtils.enemyRace();

        if (enemyRace == Race.Protoss) {
            int templarArchFrame = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Protoss_Templar_Archives);
            if (templarArchFrame != CommonCode.UNKNOWN) {
                darkTemplarInMyBaseFrame = templarArchFrame + UnitType.Protoss_Templar_Archives.buildTime() + UnitType.Protoss_Dark_Templar.buildTime() + TimeUtils.baseToBaseFrame(UnitType.Protoss_Dark_Templar);
            }

            int roboSupportFrame = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Protoss_Robotics_Support_Bay);
            if (roboSupportFrame != CommonCode.UNKNOWN) {
                reaverInMyBaseFrame = roboSupportFrame + UnitType.Protoss_Robotics_Support_Bay.buildTime() + UnitType.Protoss_Reaver.buildTime() + TimeUtils.baseToBaseFrame(UnitType.Protoss_Shuttle);
            }

            boolean darktemplarTime = darkTemplarInMyBaseFrame != CommonCode.UNKNOWN && (reaverInMyBaseFrame == CommonCode.UNKNOWN || darkTemplarInMyBaseFrame < reaverInMyBaseFrame);
            boolean reaverTime = reaverInMyBaseFrame != CommonCode.UNKNOWN && (darkTemplarInMyBaseFrame == CommonCode.UNKNOWN || reaverInMyBaseFrame < darkTemplarInMyBaseFrame);

            if (darktemplarTime) {
                StrategyBoard.turretNeedFrame = darkTemplarInMyBaseFrame - 8 * TimeUtils.SECOND;

            } else if (reaverTime) {
                StrategyBoard.turretNeedFrame = reaverInMyBaseFrame - 8 * TimeUtils.SECOND;
            }

            if (darkTemplarInMyBaseFrame != CommonCode.UNKNOWN) {
                StrategyBoard.academyFrame = darkTemplarInMyBaseFrame - UnitType.Terran_Academy.buildTime() - UnitType.Terran_Comsat_Station.buildTime() - 20 * TimeUtils.SECOND;
            }

        } else if (enemyRace == Race.Zerg) {
            int spireFrame = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Zerg_Spire);
            if (spireFrame != CommonCode.UNKNOWN) {
                mutaliskInMyBaseFrame = spireFrame + UnitType.Zerg_Spire.buildTime() + UnitType.Zerg_Mutalisk.buildTime() + TimeUtils.baseToBaseFrame(UnitType.Zerg_Mutalisk);
            }
            int hydradenFrame = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Zerg_Hydralisk_Den);
            int lairFrame = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Zerg_Lair);
            if (hydradenFrame != CommonCode.UNKNOWN && lairFrame != CommonCode.UNKNOWN) {
                lurkerInMyBaseFrame = Math.max(hydradenFrame, lairFrame) + TechType.Lurker_Aspect.researchTime() + UnitType.Zerg_Lurker.buildTime() + TimeUtils.baseToBaseFrame(UnitType.Zerg_Lurker);
            }

//			System.out.println("spireFrame : " + TimeUtils.framesToTimeString(spireFrame));
//			System.out.println("hydradenFrame : " + TimeUtils.framesToTimeString(hydradenFrame));
//			System.out.println("lairFrame : " + TimeUtils.framesToTimeString(lairFrame));

            boolean muteTime = mutaliskInMyBaseFrame != CommonCode.UNKNOWN && (lurkerInMyBaseFrame == CommonCode.UNKNOWN || mutaliskInMyBaseFrame < lurkerInMyBaseFrame);
            boolean lurkTime = lurkerInMyBaseFrame != CommonCode.UNKNOWN && (mutaliskInMyBaseFrame == CommonCode.UNKNOWN || lurkerInMyBaseFrame < mutaliskInMyBaseFrame);

            if (muteTime) {
                StrategyBoard.turretNeedFrame = mutaliskInMyBaseFrame - 8 * TimeUtils.SECOND;
            } else if (lurkTime) {
                StrategyBoard.turretNeedFrame = lurkerInMyBaseFrame - 8 * TimeUtils.SECOND;
            }
            if (StrategyBoard.buildTimeMap.featureEnabled(EnemyStrategyOptions.BuildTimeMap.Feature.NO_LAIR)) {
                StrategyBoard.turretNeedFrame += 3 * TimeUtils.MINUTE;
            }

            if (lurkerInMyBaseFrame != CommonCode.UNKNOWN) {
                StrategyBoard.academyFrame = lurkerInMyBaseFrame - UnitType.Terran_Academy.buildTime() - UnitType.Terran_Comsat_Station.buildTime() - 10 * TimeUtils.SECOND;

                if (StrategyBoard.buildTimeMap.featureEnabled(EnemyStrategyOptions.BuildTimeMap.Feature.NO_LAIR)) {
                    StrategyBoard.academyFrame += 3 * TimeUtils.MINUTE;
                } else if (StrategyBoard.currentStrategy == EnemyStrategy.ZERG_FAST_MUTAL) {
                    StrategyBoard.academyFrame += 4 * TimeUtils.MINUTE;
                } else if (StrategyBoard.currentStrategy == EnemyStrategy.ZERG_VERY_FAST_MUTAL) {
                    StrategyBoard.academyFrame += 5 * TimeUtils.MINUTE;
                }
            }

        } else {
            int starportFrame = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Terran_Starport);
            if (starportFrame != CommonCode.UNKNOWN) {
                if (StrategyBoard.currentStrategy == EnemyStrategy.TERRAN_2STAR) {
                    cloakingWraithFrame = starportFrame + UnitType.Terran_Starport.buildTime() + UnitType.Terran_Control_Tower.buildTime() + TechType.Cloaking_Field.researchTime();
                    StrategyBoard.academyFrame = cloakingWraithFrame - UnitType.Terran_Academy.buildTime() - UnitType.Terran_Comsat_Station.buildTime();
                    StrategyBoard.turretNeedFrame = cloakingWraithFrame;
                }
            }
            // TODO prebot TODO -> 테란전 터렛
        }

        StrategyBoard.turretBuildStartFrame = StrategyBoard.turretNeedFrame - UnitType.Terran_Missile_Turret.buildTime() - 8 * TimeUtils.SECOND;
        StrategyBoard.engineeringBayBuildStartFrame = StrategyBoard.turretBuildStartFrame - UnitType.Terran_Engineering_Bay.buildTime() - 8 * TimeUtils.SECOND;

    }

    // [[건물 예측시간 기준]]
    //
    // [확정 케이스 - 발견]
    // 1. 빌드 중인 건물을 본 경우 - 정확한 시간
    // - 빌드 중인 건물의 에너지를 기반으로 정확한 빌드시작시간 예측 가능(레어, 하이브 제외)
    // 2. 완성된 건물을 본 경우 - 부정확한 시간
    // - 전략별 시간, 발견시간에 막 완성 중 작은값.
    // - 전략별 시간이 논리적인 최소시간보다 작을 경우 논리적 최소시간을 사용한다.
    // - 후자가 빠르다면 전략예측이 빗나간 케이스일 확률이 높음
    //
    // [미확정 케이스 - 미발견]
    // 1. 건물을 발견하지 못함 - 부정확한 시간
    // - 전략별 시간
    //
    // * 논리적 최소시간
    // 1. 연계건물
    // ex) 아둔은 코어빌드시작프레임 + 코어빌드타임 이전에 지어질 수 없다.
    //     템플러 아카이브는 아둔빌드시작프레임 + 아둔빌드타임 이전에 지어질 수 없다.
    // 2. 정찰이 모두 끝난 상태(본진, 가스, 앞마당)의 프레임에 이전에 건물이 있을 수 없다.
    // - 단, 다크의 경우 숨김건물이 있을 수 있으므로 예외로직 필요
    public void update() {
        Race enemyRace = PlayerUtils.enemyRace();
        if (enemyRace == Race.Protoss) {
            updateCoreExpectTime();
            updateAdunExpectTime();
            updateTemplarArchExpectTime();
            updateRoboticsExpectTime();
            updateRoboticsSupportExpectTime();
            updateObservatoryExpectTime();

        } else if (enemyRace == Race.Zerg) {
            updateSpawningPoolExpectTime();
            updateLairExpectTime();
            updateHydradenExpectTime();
            updateSpireExpectTime();

        } else if (enemyRace == Race.Terran) {
            updateFactoryExpectTime();
            updateStarportExpectTime();
            // updateEngineeringExpectTime();
            // updateTurretExpectTime();
            // updateArmoryExpectTime();
        }

        setImportantTime();
    }

    private void updateFactoryExpectTime() {
        updateByBuilding(UnitType.Terran_Factory);
        updateByFlagUnit(UnitType.Terran_Starport, UnitType.Terran_Vulture);
    }

    private void updateStarportExpectTime() {
        updateByBuilding(UnitType.Terran_Starport);
        updateByFlagUnit(UnitType.Terran_Starport, UnitType.Terran_Wraith);
    }

    private void updateCoreExpectTime() {
        updateByBuilding(UnitType.Protoss_Cybernetics_Core);
        updateByFlagUnit(UnitType.Protoss_Cybernetics_Core, UnitType.Protoss_Dragoon);
    }

    private void updateAdunExpectTime() {
        updateByBuilding(UnitType.Protoss_Citadel_of_Adun);
        updateByFlagUnit(UnitType.Protoss_Citadel_of_Adun, UnitType.Protoss_Dark_Templar);
        updateByFlagUnit(UnitType.Protoss_Citadel_of_Adun, UnitType.Protoss_High_Templar);
    }

    private void updateTemplarArchExpectTime() {
        updateByBuilding(UnitType.Protoss_Templar_Archives);
        updateByFlagUnit(UnitType.Protoss_Templar_Archives, UnitType.Protoss_Dark_Templar);
        updateByFlagUnit(UnitType.Protoss_Templar_Archives, UnitType.Protoss_High_Templar);
    }

    private void updateRoboticsExpectTime() {
        updateByBuilding(UnitType.Protoss_Robotics_Facility);
        updateByFlagUnit(UnitType.Protoss_Robotics_Facility, UnitType.Protoss_Shuttle);
        updateByFlagUnit(UnitType.Protoss_Robotics_Facility, UnitType.Protoss_Reaver);
        updateByFlagUnit(UnitType.Protoss_Robotics_Facility, UnitType.Protoss_Observer);
    }

    private void updateRoboticsSupportExpectTime() {
        updateByBuilding(UnitType.Protoss_Robotics_Support_Bay);
        updateByFlagUnit(UnitType.Protoss_Robotics_Support_Bay, UnitType.Protoss_Reaver);
    }

    private void updateObservatoryExpectTime() {
        updateByBuilding(UnitType.Protoss_Observatory);
        updateByFlagUnit(UnitType.Protoss_Observatory, UnitType.Protoss_Observer);
    }

    private void updateSpawningPoolExpectTime() {
        updateByBuilding(UnitType.Zerg_Spawning_Pool);
        updateByFlagUnit(UnitType.Zerg_Spawning_Pool, UnitType.Zerg_Zergling);
    }

    private void updateLairExpectTime() {
        updateByBuilding(UnitType.Zerg_Lair);
        updateByFlagUnit(UnitType.Zerg_Lair, UnitType.Zerg_Lurker);
        updateByFlagUnit(UnitType.Zerg_Lair, UnitType.Zerg_Mutalisk);
    }

    private void updateHydradenExpectTime() {
        int lairInProgressExpect = getBuildStartFrameExpect(UnitType.Zerg_Lair) + (int) (UnitType.Zerg_Lair.buildTime() * 0.4);
        updateByBuilding(lairInProgressExpect, UnitType.Zerg_Hydralisk_Den);
        updateByFlagUnit(UnitType.Zerg_Hydralisk_Den, UnitType.Zerg_Hydralisk);
    }

    private void updateSpireExpectTime() {
        int lairCompleteExpect = getBuildStartFrameExpect(UnitType.Zerg_Lair) + UnitType.Zerg_Lair.buildTime();
        updateByBuilding(lairCompleteExpect, UnitType.Zerg_Spire);
        updateByFlagUnit(UnitType.Zerg_Spire, UnitType.Zerg_Mutalisk);
    }

    /// 건물에 따른 시간 예측 (또는 확정)
    private void updateByBuilding(UnitType buildingType) {
        int buildFrameByStrategy = StrategyBoard.currentStrategy.buildTimeMap.frame(buildingType);
        if (buildFrameByStrategy == CommonCode.UNKNOWN) {
            buildFrameByStrategy = StrategyBoard.startStrategy.buildTimeMap.frame(buildingType);
        }
        updateByBuilding(buildFrameByStrategy, buildingType);
    }

    /// 건물에 따른 시간 확정. 연계건물(nextBuilding)에 대한 최소시간 설정
    private void updateByBuilding(int buildFrameByStrategy, UnitType buildingType) {
        if (isCertainBuildTime(buildingType)) {
            return;
        }

        List<UnitInfo> buildingInfos = UnitUtils.getEnemyUnitInfoList(CommonCode.EnemyUnitVisibleStatus.VISIBLE, buildingType);
        if (!buildingInfos.isEmpty()) {
            if (buildingInfos.get(0).isCompleted()) {
                // 완성된 건물 발견. 막 완성되었다고 가정한 시간과 전략별 시간 중 빠른 값 선택
                int expectBuildFrame = buildingInfos.get(0).getUpdateFrame() - buildingType.buildTime();
                if (buildFrameByStrategy != CommonCode.UNKNOWN && buildFrameByStrategy < expectBuildFrame) {
                    expectBuildFrame = buildFrameByStrategy;
                }
                updateBuildTimeExpect(buildingType, expectBuildFrame);
                addCertainBuildTime(buildingType, expectBuildFrame);

            } else {
                // 미완성 건물 발견. 에너지로 빌드시간 추측 가능
                boolean ignoreMinimum = true;
                int expectBuildFrame = TimeUtils.buildStartFrames(buildingInfos.get(0).getUnit());
                if (expectBuildFrame == CommonCode.UNKNOWN) {
                    ignoreMinimum = false;
                    expectBuildFrame = buildFrameByStrategy;
                }
                if (expectBuildFrame == CommonCode.UNKNOWN) {
                    System.out.println("################ ERROR - updateDefaultExpectTime" + buildFrameByStrategy + " / " + buildingType);
                }
                updateBuildTimeExpect(buildingType, expectBuildFrame, ignoreMinimum);
                addCertainBuildTime(buildingType, expectBuildFrame);
            }

        } else {
            // 미발견. 전략별 시간 선택. 전략별 시간 이후 적 본진, 앞마당, 가스가 모두 정찰 되었다면 최후 정찰시간을 이후로 최소값을 증가시킨다.
            if (buildFrameByStrategy != CommonCode.UNKNOWN) {

                int gasLastCheckFrame = StrategyAnalyseManager.Instance().lastCheckFrame(StrategyAnalyseManager.LastCheckLocation.GAS);
                if (!avoidRashExpectation(buildingType)) {
                    int baseLastCheckFrame = StrategyAnalyseManager.Instance().lastCheckFrame(StrategyAnalyseManager.LastCheckLocation.BASE);
                    int expansionLastCheckFrame = StrategyAnalyseManager.Instance().lastCheckFrame(StrategyAnalyseManager.LastCheckLocation.FIRST_EXPANSION);

                    if (baseLastCheckFrame > buildFrameByStrategy && gasLastCheckFrame > buildFrameByStrategy && expansionLastCheckFrame > buildFrameByStrategy) {
                        int minimumFrame = baseLastCheckFrame < gasLastCheckFrame ? baseLastCheckFrame : gasLastCheckFrame;
                        minimumFrame = minimumFrame < expansionLastCheckFrame ? minimumFrame : expansionLastCheckFrame;
                        updateBuildTimeMinimum(buildingType, minimumFrame);
                    }
                }

                // 가스가 없으면 모든 가스가 필요한 건물의 최소 빌드타임을 대충 올려놓는다.
                if (buildingType.gasPrice() > 0 && UnitUtils.enemyBaseGas() != null) {
                    if (UnitUtils.getEnemyUnitCount(UnitType.Protoss_Assimilator, UnitType.Zerg_Extractor, UnitType.Terran_Refinery) == 0) {
                        updateBuildTimeMinimum(buildingType, gasLastCheckFrame + UnitType.Terran_Refinery.buildTime() + 10 * TimeUtils.SECOND);
                    }
                }
                updateBuildTimeExpect(buildingType, buildFrameByStrategy);
            }
        }
    }

    /// 특정 주요건물에 대해 섣부른 판단을 하지 않는다.(적베이스, 앞마당, 가스를 모두 정찰해도 최소시간을 업데이트하지 않는다.)
    /// - 적 전략이 패스트 다크라고 판단되면 숨김아둔, 템플러아카이브가 있을 수 있다.
    /// - 적 전략이 뮤탈전략이라고 판단되면 앞마당 크립 구섞탱이에 스파이어가 건설될 수 있다.
    private boolean avoidRashExpectation(UnitType buildingType) {
        if (StrategyBoard.currentStrategy == EnemyStrategy.PROTOSS_FAST_DARK) {
            return buildingType == UnitType.Protoss_Citadel_of_Adun || buildingType == UnitType.Protoss_Templar_Archives;

        } else if (StrategyBoard.currentStrategy == EnemyStrategy.ZERG_FAST_MUTAL) {
            return buildingType == UnitType.Zerg_Spire;

        } else if (StrategyBoard.currentStrategy == EnemyStrategy.ZERG_VERY_FAST_MUTAL) {
            return buildingType == UnitType.Zerg_Spire;
        }
        return false;
    }

    /// 유닛에 따른 시간 확정
    private void updateByFlagUnit(UnitType buildingType, UnitType flagUnitType) {
        if (isCertainBuildTime(buildingType)) {
            return;
        }
        if (UnitUtils.getEnemyUnitInfoList(CommonCode.EnemyUnitVisibleStatus.VISIBLE, flagUnitType).isEmpty()) {
            return;
        }

        int expectBuildTime = TimeUtils.getFrame() - flagUnitType.buildTime() - buildingType.buildTime();

        if (buildingType == UnitType.Protoss_Citadel_of_Adun) {
            if (flagUnitType == UnitType.Protoss_Dark_Templar || flagUnitType == UnitType.Protoss_High_Templar) {
                expectBuildTime -= UnitType.Protoss_Templar_Archives.buildTime();
            }
        } else if (buildingType == UnitType.Protoss_Robotics_Facility) {
            if (flagUnitType == UnitType.Protoss_Reaver) {
                expectBuildTime -= UnitType.Protoss_Robotics_Support_Bay.buildTime();
            } else if (flagUnitType == UnitType.Protoss_Observer) {
                expectBuildTime -= UnitType.Protoss_Observatory.buildTime();
            }
        } else if (buildingType == UnitType.Zerg_Lair) {
            if (flagUnitType == UnitType.Zerg_Lurker) {
                expectBuildTime -= TechType.Lurker_Aspect.researchTime();
            } else if (flagUnitType == UnitType.Zerg_Mutalisk) {
                expectBuildTime -= UnitType.Zerg_Spire.buildTime();
            }
        }

        // flagUnit으로 미루어 building이 전략시간보다 빠른 시간에 지어진 것으로 판단되면 바꾸어 확정한다.
        int buildFrameByStrategy = StrategyBoard.currentStrategy.buildTimeMap.frame(buildingType);
        if (buildFrameByStrategy != CommonCode.UNKNOWN && buildFrameByStrategy < expectBuildTime) {
            expectBuildTime = buildFrameByStrategy;
        } else {
            buildFrameByStrategy = StrategyBoard.startStrategy.buildTimeMap.frame(buildingType);
            if (buildFrameByStrategy != CommonCode.UNKNOWN && buildFrameByStrategy < expectBuildTime) {
                expectBuildTime = buildFrameByStrategy;
            }
        }
        updateBuildTimeExpect(buildingType, expectBuildTime);
        addCertainBuildTime(buildingType, expectBuildTime);
    }

    /// 빌드시간 확정되었는지 여부
    private boolean isCertainBuildTime(UnitType buildingType) {
        return buildTimeCertain.contains(buildingType);
    }

    /// 빌드 예상시간 업데이트
    private void updateBuildTimeExpect(UnitType buildingType, int frame) {
        updateBuildTimeExpect(buildingType, frame, false);
    }

    private void updateBuildTimeExpect(UnitType buildingType, int frame, boolean ignoreMinimum) {
        if (frame == CommonCode.UNKNOWN) {
            return;
        }
        Integer buildTimeMinimum = null;
        if (!ignoreMinimum) {
            buildTimeMinimum = buildTimeMinimumMap.get(buildingType);
        }
        if (buildTimeMinimum != null && frame < buildTimeMinimum) {
            buildTimeExpectMap.put(buildingType, buildTimeMinimum);
        } else {
            buildTimeExpectMap.put(buildingType, frame);
        }
    }

    /// 빌드 최소시간 업데이트 (이 시간보다 빠르게 지을 수는 없다.)
    private void updateBuildTimeMinimum(UnitType buildingType, int frame) {
        if (frame == CommonCode.UNKNOWN) {
            return;
        }
        Integer buildTimeMinimum = buildTimeMinimumMap.get(buildingType);
        if (buildTimeMinimum == null || frame > buildTimeMinimum) {
            buildTimeMinimumMap.put(buildingType, frame);
        }
    }

    /// 빌드시간 확정
    private void addCertainBuildTime(UnitType buildingType, int expectBuildFrame) {
        buildTimeCertain.add(buildingType);

        List<UnitType> nextBuildingTypes = nextBuildingMap.get(buildingType);
        if (nextBuildingTypes != null) {
            for (UnitType nextBuildingType : nextBuildingTypes) {
                updateBuildTimeMinimum(nextBuildingType, expectBuildFrame + buildingType.buildTime());
            }
        }
    }
}
