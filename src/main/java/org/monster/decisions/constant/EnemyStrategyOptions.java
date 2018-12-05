package org.monster.decisions.constant;

import bwapi.Position;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import org.monster.board.StrategyBoard;
import org.monster.common.MetaType;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.PositionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnemyStrategyOptions {

    public static enum AddOnOption {
        IMMEDIATELY, VULTURE_FIRST;
    }

    public static enum ExpansionOption {
        ONE_FACTORY, TWO_FACTORY, TWO_STARPORT, ONE_STARPORT;
    }

    public static class FactoryRatio {
        public int vulture;
        public int tank;
        public int goliath;
        public int weight;

        private FactoryRatio(int vulture, int tank, int goliath, int weight) {
            this.vulture = vulture;
            this.tank = tank;
            this.goliath = goliath;
            this.weight = weight;
        }

        public static EnemyStrategyOptions.FactoryRatio ratio(int vulture, int tank, int goliath, int weight) {
            return new EnemyStrategyOptions.FactoryRatio(vulture, tank, goliath, weight);
        }

        @Override
        public String toString() {
            return "FAC RATIO : vulture=" + vulture + ", tank=" + tank + ", goliath=" + goliath + ", weight=" + weight;
        }

        public static class Weight {
            public static final int VULTURE = 1;
            public static final int TANK = 2;
            public static final int GOLIATH = 3;
        }
    }

    public static class MarineCount {
        public static final int ONE_MARINE = 1;
        public static final int TWO_MARINE = 2;
        public static final int FOUR_MARINE = 4;
        public static final int SIX_MARINE = 6;
        public static final int EIGHT_MARINE = 8;
    }

    public static class UpgradeOrder {
        private static Map<Object, MetaType> upgradeOrderMap = new HashMap<>();

        static {
            upgradeOrderMap.put(TechType.Spider_Mines, new MetaType(TechType.Spider_Mines));
            upgradeOrderMap.put(UpgradeType.Ion_Thrusters, new MetaType(UpgradeType.Ion_Thrusters));
            upgradeOrderMap.put(TechType.Tank_Siege_Mode, new MetaType(TechType.Tank_Siege_Mode));
            upgradeOrderMap.put(UpgradeType.Charon_Boosters, new MetaType(UpgradeType.Charon_Boosters));
        }

        public static List<MetaType> get(Object... upgrades) {
            List<MetaType> upgradeOrderList = new ArrayList<>();
            for (Object upgrade : upgrades) {
                MetaType metaType = upgradeOrderMap.get(upgrade);
                if (metaType != null) {
                    upgradeOrderList.add(metaType);
                }
            }
            return upgradeOrderList;
        }

        public static class FacUp {
            public static final Object VM = TechType.Spider_Mines;
            public static final Object VS = UpgradeType.Ion_Thrusters;
            public static final Object TS = TechType.Tank_Siege_Mode;
            public static final Object GR = UpgradeType.Charon_Boosters;
        }
    }

    public static class Mission {
        public static List<EnemyStrategyOptions.Mission.MissionType> missions(EnemyStrategyOptions.Mission.MissionType... missions) {
            List<EnemyStrategyOptions.Mission.MissionType> missionList = new ArrayList<>();
            for (EnemyStrategyOptions.Mission.MissionType mission : missions) {
                missionList.add(mission);
            }
            return missionList;
        }

        public static boolean complete(EnemyStrategyOptions.Mission.MissionType mission) {

            switch (mission) {
                case EXPANSION: // 앞마당
                    boolean baseCommandCentertOk = false;
                    boolean expansionCommandCenterOk = false;
                    List<Unit> commandCenterList = UnitUtils.getUnitList(CommonCode.UnitFindStatus.ALL, UnitType.Terran_Command_Center);
                    for (Unit commandCenter : commandCenterList) {
                        if (commandCenter.isLifted()) {
                            continue;
                        }
                        CommonCode.RegionType regionType = PositionUtils.positionToRegionType(commandCenter.getPosition());
                        if (regionType == CommonCode.RegionType.MY_BASE) {
                            baseCommandCentertOk = true;
                        } else if (regionType == CommonCode.RegionType.MY_FIRST_EXPANSION) {
                            expansionCommandCenterOk = true;
                        }
                    }
                    return baseCommandCentertOk && expansionCommandCenterOk;

                case NO_ENEMY: // 지상 적 없음
                    return StrategyBoard.nearGroundEnemyPosition == Position.Unknown;

                case NO_AIR_ENEMY: // 공중 적 없음
                    return StrategyBoard.nearAirEnemyPosition == Position.Unknown;

                case COMSAT_OK: // 컴셋에너지보유
                    boolean comsatReady = false;
                    List<Unit> comsatList = UnitUtils.getCompletedUnitList(UnitType.Terran_Comsat_Station);
                    int totalComsatEnergy = 0;
                    for (Unit comsat : comsatList) {
                        totalComsatEnergy += comsat.getEnergy();
                        if (totalComsatEnergy > 99) {
                            comsatReady = true;
                            break;
                        }
                    }
                    return comsatReady;

                case TURRET_OK: // 베이스터렛OK, 앞마당 터렛OK
                    boolean baseTurretOk = false;
                    boolean expansionTurretOk = false;
                    List<Unit> turretList = UnitUtils.getCompletedUnitList(UnitType.Terran_Missile_Turret);
                    for (Unit turret : turretList) {
                        CommonCode.RegionType regionType = PositionUtils.positionToRegionType(turret.getPosition());
                        if (regionType == CommonCode.RegionType.MY_BASE) {
                            baseTurretOk = true;
                        } else if (regionType == CommonCode.RegionType.MY_FIRST_EXPANSION || regionType == CommonCode.RegionType.MY_THIRD_REGION) {
                            expansionTurretOk = true;
                        }
                    }
                    return baseTurretOk && expansionTurretOk;

                case VULTURE: // 충분한 벌처
                    return UnitUtils.getCompletedUnitList(UnitType.Terran_Vulture).size() >= 3;

                case TANK: // 충분한 탱크
                    return UnitUtils.getCompletedUnitList(UnitType.Terran_Vulture).size() >= 3;

                case GOLIATH: // 충분한 골리앗
                    return UnitUtils.getCompletedUnitList(UnitType.Terran_Vulture).size() >= 3;

                case RETREAT: // 공격모드 해제
                    return StrategyBoard.attackStartedFrame > 0 && !StrategyBoard.mainSquadMode.isAttackMode;

                case ARMORY:
                    return UnitUtils.myCompleteUnitDiscovered(UnitType.Terran_Armory);
            }

            return true;
        }

        public static enum MissionType {
            EXPANSION, RETREAT, NO_ENEMY, NO_AIR_ENEMY, COMSAT_OK, TURRET_OK, ARMORY, VULTURE, TANK, GOLIATH
        }
    }

    public static class BuildTimeMap {
        private Set<Feature> features = new HashSet<>();
        private Map<UnitType, List<Integer>> buildingTime = new HashMap<>();
        private Map<TechType, Integer> techTime = new HashMap<>();
        private Map<UpgradeType, Integer> upgradeTime = new HashMap<>();

        public EnemyStrategyOptions.BuildTimeMap setFeature(EnemyStrategyOptions.BuildTimeMap.Feature... features) {
            for (Feature feature : features) {
                this.features.add(feature);
            }
            return this;
        }

        public boolean featureEnabled(Feature feature) {
            return this.features.contains(feature);
        }

        public EnemyStrategyOptions.BuildTimeMap put(UnitType unitType, int minutes, int seconds) {
            int defaultTime = TimeUtils.timeToFrames(minutes, seconds);
            List<Integer> defaultTimeList = buildingTime.get(unitType);
            if (defaultTimeList == null) {
                defaultTimeList = new ArrayList<>();
            }
            defaultTimeList.add(defaultTime);
            buildingTime.put(unitType, defaultTimeList);
            return this;
        }

        public EnemyStrategyOptions.BuildTimeMap put(TechType techType, int minutes, int seconds) {
            int defaultTime = TimeUtils.timeToFrames(minutes, seconds);
            techTime.put(techType, defaultTime);
            return this;
        }

        public EnemyStrategyOptions.BuildTimeMap put(UpgradeType upgradeType, int minutes, int seconds) {
            int defaultTime = TimeUtils.timeToFrames(minutes, seconds);
            upgradeTime.put(upgradeType, defaultTime);
            return this;
        }

        public EnemyStrategyOptions.BuildTimeMap putAll(EnemyStrategyOptions.BuildTimeMap defaultTimeMap) {
            buildingTime.putAll(defaultTimeMap.buildingTime);
            techTime.putAll(defaultTimeMap.techTime);
            upgradeTime.putAll(defaultTimeMap.upgradeTime);
            return this;
        }

        public int frame(UnitType unitType) {
            return frameOfIndex(unitType, 0);
        }

        public int frame(UnitType unitType, int margin) {
            return frameOfIndex(unitType, 0) + margin * TimeUtils.SECOND;
        }

        public int frameOfIndex(UnitType unitType, int index) {
            List<Integer> defaultTimeList = buildingTime.get(unitType);
            if (defaultTimeList == null || defaultTimeList.size() <= index) {
                return CommonCode.UNKNOWN;
            }
            return defaultTimeList.get(index);
        }

        public int frameOfIndex(UnitType unitType, int index, int margin) {
            return frameOfIndex(unitType, index) + margin * TimeUtils.SECOND;
        }

        public int frame(TechType techType) {
            Integer defaultTime = techTime.get(techType);
            return defaultTime != null ? defaultTime : CommonCode.UNKNOWN;
        }

        public int frame(TechType techType, int margin) {
            return frame(techType) + margin * TimeUtils.SECOND;
        }

        public int frame(UpgradeType upgradeType) {
            Integer defaultTime = upgradeTime.get(upgradeType);
            return defaultTime != null ? defaultTime : CommonCode.UNKNOWN;
        }

        public int frame(UpgradeType upgradeType, int margin) {
            return frame(upgradeType) + margin * TimeUtils.SECOND;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Feature: ").append(features).append("\n");

            for (UnitType unitType : buildingTime.keySet()) {
                List<Integer> times = buildingTime.get(unitType);
                List<String> timeStrings = new ArrayList<>();
                for (int time : times) {
                    timeStrings.add(TimeUtils.framesToTimeString(time));
                }
                sb.append(unitType).append(timeStrings).append("\n");
            }
            for (TechType techType : techTime.keySet()) {
                Integer time = techTime.get(techType);
                sb.append(techType).append(TimeUtils.framesToTimeString(time)).append("\n");
            }
            for (UpgradeType upgradeType : upgradeTime.keySet()) {
                Integer time = upgradeTime.get(upgradeType);
                sb.append(upgradeType).append(TimeUtils.framesToTimeString(time)).append("\n");
            }

            return sb.toString();
        }

        public enum Feature {
            DOUBLE, QUICK_ATTACK, DEFENSE_FRONT, DEFENSE_DROP, DETECT_IMPORTANT,
            TWOGATE, MECHANIC, BIONIC, NO_LAIR, ZERG_FAST_ALL_IN
        }
    }
}
