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
}
