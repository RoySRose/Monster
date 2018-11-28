package org.monster.decisions.constant;

import bwapi.UnitType;
import bwapi.UpgradeType;

public class TimeMapForProtoss {

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_1GATE_CORE() {
        return new EnemyStrategyOptions.BuildTimeMap().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DETECT_IMPORTANT)
                .put(UnitType.Protoss_Gateway, 1, 20)
                .put(UnitType.Protoss_Assimilator, 1, 30)
                .put(UnitType.Protoss_Cybernetics_Core, 2, 00);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_2GATE() {
        return new EnemyStrategyOptions.BuildTimeMap().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.TWOGATE)
                .put(UnitType.Protoss_Gateway, 1, 20)
                .put(UnitType.Protoss_Gateway, 1, 45)
                .put(UnitType.Protoss_Assimilator, 3, 00)
                .put(UnitType.Protoss_Cybernetics_Core, 3, 20);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_DOUBLE() {
        return new EnemyStrategyOptions.BuildTimeMap().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DOUBLE)
                .put(UnitType.Protoss_Nexus, 1, 55)
                .put(UnitType.Protoss_Gateway, 2, 5)
                .put(UnitType.Protoss_Assimilator, 2, 20)
                .put(UnitType.Protoss_Gateway, 2, 45)
                .put(UnitType.Protoss_Cybernetics_Core, 2, 55);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_FORGE_DEFENSE() {
        return new EnemyStrategyOptions.BuildTimeMap()
                .put(UnitType.Protoss_Forge, 1, 40)
                .put(UnitType.Protoss_Photon_Cannon, 2, 15)
                .put(UnitType.Protoss_Cybernetics_Core, 3, 20);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_FORGE_CANNON_RUSH() {
        return new EnemyStrategyOptions.BuildTimeMap().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DOUBLE)
                .put(UnitType.Protoss_Forge, 1, 40)
                .put(UnitType.Protoss_Photon_Cannon, 2, 15)
                .put(UnitType.Protoss_Cybernetics_Core, 4, 20);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_FORGE_DOUBLE() {
        return new EnemyStrategyOptions.BuildTimeMap().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DOUBLE, EnemyStrategyOptions.BuildTimeMap.Feature.QUICK_ATTACK)
                .put(UnitType.Protoss_Forge, 1, 40)
                .put(UnitType.Protoss_Photon_Cannon, 2, 15)
                .put(UnitType.Protoss_Nexus, 2, 30)
                .put(UnitType.Protoss_Cybernetics_Core, 3, 50);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_GATE_DOUBLE() {
        return new EnemyStrategyOptions.BuildTimeMap().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DOUBLE)
                .put(UnitType.Protoss_Forge, 1, 40)
                .put(UnitType.Protoss_Gateway, 2, 15)
                .put(UnitType.Protoss_Nexus, 2, 30)
                .put(UnitType.Protoss_Cybernetics_Core, 3, 50);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_FAST_DARK() {
        return PROTOSS_1GATE_CORE().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DEFENSE_FRONT, EnemyStrategyOptions.BuildTimeMap.Feature.DETECT_IMPORTANT)
                .put(UnitType.Protoss_Citadel_of_Adun, 2, 40)
                .put(UnitType.Protoss_Templar_Archives, 3, 20);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_DARK_DROP() {
        return PROTOSS_1GATE_CORE().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DEFENSE_DROP, EnemyStrategyOptions.BuildTimeMap.Feature.DETECT_IMPORTANT)
                .put(UnitType.Protoss_Citadel_of_Adun, 2, 40)
                .put(UnitType.Protoss_Templar_Archives, 3, 20);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_FAST_DRAGOON() {
        return PROTOSS_1GATE_CORE().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DETECT_IMPORTANT)
                .put(UpgradeType.Singularity_Charge, 2, 50)
                .put(UnitType.Protoss_Citadel_of_Adun, 3, 00)
                .put(UnitType.Protoss_Templar_Archives, 3, 40);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_HARDCORE_ZEALOT() {
        return PROTOSS_2GATE()
                .put(UnitType.Protoss_Citadel_of_Adun, 4, 20)
                .put(UnitType.Protoss_Templar_Archives, 5, 00);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_TWOGATE_TECH() {
        return PROTOSS_2GATE()
                .put(UnitType.Protoss_Citadel_of_Adun, 4, 20)
                .put(UnitType.Protoss_Templar_Archives, 5, 00);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_POWER_DRAGOON() { // non-precise
        return PROTOSS_1GATE_CORE().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DETECT_IMPORTANT)
                .put(UpgradeType.Singularity_Charge, 2, 50)
                .put(UnitType.Protoss_Gateway, 3, 30)
                .put(UnitType.Protoss_Citadel_of_Adun, 3, 50)
                .put(UnitType.Protoss_Templar_Archives, 4, 30);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_ROBOTICS_REAVER() { // non-precise
        return PROTOSS_1GATE_CORE().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.DEFENSE_DROP)
                .put(UpgradeType.Singularity_Charge, 2, 50)
                .put(UnitType.Protoss_Robotics_Facility, 3, 40)
                .put(UnitType.Protoss_Gateway, 4, 0)
                .put(UnitType.Protoss_Robotics_Support_Bay, 4, 40)
                .put(UnitType.Protoss_Citadel_of_Adun, 4, 45)
                .put(UnitType.Protoss_Templar_Archives, 5, 25);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_ROBOTICS_OB_DRAGOON() { // non-precise
        return PROTOSS_1GATE_CORE()
                .put(UpgradeType.Singularity_Charge, 2, 50)
                .put(UnitType.Protoss_Robotics_Facility, 3, 40)
                .put(UnitType.Protoss_Gateway, 4, 0)
                .put(UnitType.Protoss_Observatory, 4, 40)
                .put(UnitType.Protoss_Citadel_of_Adun, 4, 45)
                .put(UnitType.Protoss_Templar_Archives, 5, 25);
    }

//	public static EnemyStrategyOptions.BuildTimeMap PROTOSS_1GATE_CORE_DOUBLE() {
//		return PROTOSS_1GATE_CORE()
//		.put(UpgradeType.Singularity_Charge, 2, 50)
//		.put(UnitType.Protoss_Nexus, 4, 10)
//		.put(UnitType.Protoss_Stargate, 5, 40)
//		.put(UnitType.Protoss_Fleet_Beacon, 6, 30);
//	}

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_STARGATE() { // non-precise
        return PROTOSS_1GATE_CORE().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.QUICK_ATTACK)
                .put(UpgradeType.Singularity_Charge, 2, 50)
                .put(UnitType.Protoss_Nexus, 4, 10)
                .put(UnitType.Protoss_Stargate, 5, 40)
                .put(UnitType.Protoss_Fleet_Beacon, 6, 30)
                .put(UnitType.Protoss_Citadel_of_Adun, 9, 00)
                .put(UnitType.Protoss_Templar_Archives, 9, 40);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_DOUBLE_GROUND() { // non-precise
        return PROTOSS_DOUBLE()
                .put(UnitType.Protoss_Citadel_of_Adun, 3, 50)
                .put(UnitType.Protoss_Templar_Archives, 4, 30);
    }

    public static EnemyStrategyOptions.BuildTimeMap PROTOSS_DOUBLE_CARRIER() { // non-precise
        return PROTOSS_DOUBLE().setFeature(EnemyStrategyOptions.BuildTimeMap.Feature.QUICK_ATTACK)
                .put(UnitType.Protoss_Citadel_of_Adun, 3, 50)
                .put(UnitType.Protoss_Stargate, 5, 00)
                .put(UnitType.Protoss_Fleet_Beacon, 5, 50)
                .put(UnitType.Protoss_Citadel_of_Adun, 9, 00)
                .put(UnitType.Protoss_Templar_Archives, 9, 40);
    }

}
