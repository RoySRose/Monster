package org.monster.strategy.constant;

import org.monster.common.MetaType;

import java.util.Collections;
import java.util.List;

public enum EnemyStrategy {

    // PHASE1 : 시작 ~ 코어완료 OR 일정시간 경과
    PROTOSS_INIT(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForProtoss.PROTOSS_1GATE_CORE()),

    PROTOSS_1GATE_CORE(PROTOSS_INIT),

    PROTOSS_2GATE(5, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForProtoss.PROTOSS_2GATE()),

    PROTOSS_2GATE_CENTER(PROTOSS_2GATE),

    PROTOSS_DOUBLE(1, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.ONE_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.ONE_FACTORY
            , TimeMapForProtoss.PROTOSS_DOUBLE()),

    PROTOSS_FORGE_DEFENSE(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.ONE_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.ONE_FACTORY
            , TimeMapForProtoss.PROTOSS_FORGE_DEFENSE()), //

    PROTOSS_FORGE_CANNON_RUSH(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.ONE_FACTORY
            , TimeMapForProtoss.PROTOSS_FORGE_CANNON_RUSH()),

    PROTOSS_FORGE_DOUBLE(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.ONE_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForProtoss.PROTOSS_FORGE_DOUBLE()),

    PROTOSS_GATE_DOUBLE(1, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS),
            EnemyStrategyOptions.MarineCount.ONE_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.ONE_FACTORY
            , TimeMapForProtoss.PROTOSS_GATE_DOUBLE()),

    // PHASE2 : PHASE1 종료 ~ PHASE2 에 대한 위험이 종료되는 시점
    PROTOSS_FAST_DRAGOON(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.ONE_FACTORY
            , TimeMapForProtoss.PROTOSS_FAST_DRAGOON()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.NO_ENEMY, EnemyStrategyOptions.Mission.MissionType.VULTURE, EnemyStrategyOptions.Mission.MissionType.TANK)),

    PROTOSS_FAST_DARK(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.ONE_FACTORY
            , TimeMapForProtoss.PROTOSS_FAST_DARK()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.NO_ENEMY, EnemyStrategyOptions.Mission.MissionType.COMSAT_OK, EnemyStrategyOptions.Mission.MissionType.TURRET_OK, EnemyStrategyOptions.Mission.MissionType.VULTURE, EnemyStrategyOptions.Mission.MissionType.TANK)),

    PROTOSS_DARK_DROP(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.ONE_FACTORY
            , TimeMapForProtoss.PROTOSS_DARK_DROP()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.NO_ENEMY, EnemyStrategyOptions.Mission.MissionType.NO_AIR_ENEMY, EnemyStrategyOptions.Mission.MissionType.VULTURE, EnemyStrategyOptions.Mission.MissionType.TANK)),

    PROTOSS_ROBOTICS_REAVER(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.ONE_FACTORY
            , TimeMapForProtoss.PROTOSS_ROBOTICS_REAVER()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.NO_ENEMY, EnemyStrategyOptions.Mission.MissionType.NO_AIR_ENEMY, EnemyStrategyOptions.Mission.MissionType.VULTURE, EnemyStrategyOptions.Mission.MissionType.TANK)),

    PROTOSS_ROBOTICS_OB_DRAGOON(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.ONE_FACTORY
            , TimeMapForProtoss.PROTOSS_ROBOTICS_OB_DRAGOON()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.NO_ENEMY, EnemyStrategyOptions.Mission.MissionType.TANK)),

    PROTOSS_HARDCORE_ZEALOT(4, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.TS)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForProtoss.PROTOSS_HARDCORE_ZEALOT()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.NO_ENEMY, EnemyStrategyOptions.Mission.MissionType.VULTURE)),

    PROTOSS_TWOGATE_TECH(3, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.TS)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForProtoss.PROTOSS_TWOGATE_TECH()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.NO_ENEMY, EnemyStrategyOptions.Mission.MissionType.VULTURE)),

    PROTOSS_STARGATE(2, 3, 1, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForProtoss.PROTOSS_STARGATE()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.RETREAT)),

    PROTOSS_DOUBLE_GROUND(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.ONE_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.ONE_FACTORY
            , TimeMapForProtoss.PROTOSS_DOUBLE_GROUND()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION)),

    PROTOSS_DOUBLE_CARRIER(2, 3, 1, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)
            , EnemyStrategyOptions.MarineCount.ONE_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForProtoss.PROTOSS_DOUBLE_CARRIER()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.RETREAT)),


    // PHASE3 - PHASE2 종료 ~
    PROTOSS_GROUND(1, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)),
    PROTOSS_PROTOSS_AIR1(5, 5, 1, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.GR)),
    PROTOSS_PROTOSS_AIR2(1, 5, 5, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.GR)),
    PROTOSS_PROTOSS_AIR3(1, 1, 8, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.GR)),

    // [저그전 기본전략 : 8배럭벙커링후 2팩토리 메카닉]
    // : 8배럭 -> 2scv정찰 -> 마린6기 -> 1팩 -> 벌처 -> 애드온 -> 마인업 -> 2팩 -> 멀티

    // PHASE1 : 시작 ~ 레어발견 OR 일정시간 경과
    ZERG_INIT(2, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS) // INIT DEFAULT, camp=S_CHOKE,
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForZerg.ZERG_9DRONE()),

    ZERG_5DRONE(5, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.GR, EnemyStrategyOptions.UpgradeOrder.FacUp.TS) // camp=BASE, 벙커
            , EnemyStrategyOptions.MarineCount.EIGHT_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForZerg.ZERG_5DRONE()),

    ZERG_9DRONE(3, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.TS) // camp=F_CHOKE, 벙커
            , EnemyStrategyOptions.MarineCount.EIGHT_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForZerg.ZERG_9DRONE()),

    ZERG_9DRONE_GAS(3, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS) // 마린=MORE, camp=F_CHOKE, 벙커
            , EnemyStrategyOptions.MarineCount.EIGHT_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForZerg.ZERG_9DRONE_GAS()),

    ZERG_9DRONE_GAS_DOUBLE(2, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS) // 마린=MORE, camp=F_CHOKE, 벙커
            , EnemyStrategyOptions.MarineCount.EIGHT_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForZerg.ZERG_9DRONE_GAS_DOUBLE()),

    ZERG_OVERPOOL(2, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.TS) // camp=F_CHOKE
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForZerg.ZERG_OVERPOOL()),

    ZERG_OVERPOOL_GAS(2, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS) // camp=F_CHOKE
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForZerg.ZERG_OVERPOOL_GAS()),

    ZERG_OVERPOOL_GAS_DOUBLE(2, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS) // camp=F_CHOKE
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForZerg.ZERG_OVERPOOL_GAS_DOUBLE()),

    ZERG_2HAT_GAS(2, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS) // camp=S_CHOKE, 벙커(공격)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForZerg.ZERG_2HAT_GAS()),

    ZERG_TWIN_HAT(ZERG_2HAT_GAS),

    ZERG_HYDRA_ALL_IN(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM)
            , EnemyStrategyOptions.MarineCount.EIGHT_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForZerg.ZERG_NO_LAIR_HYDRA()),

    ZERG_LURKER_ALL_IN(ZERG_HYDRA_ALL_IN),

    ZERG_3HAT(2, 1, 1, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS) // camp=S_CHOKE, 벙커(공격)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForZerg.ZERG_3HAT()),

    // PHASE2 : PHASE1 종료 ~ PHASE2 에 대한 위험이 종료되는 시점 (camp가 F_EXPANSION으로 이동, 적 병력/다크, 아군 병력/터렛/컴셋 고려)
    // PHASE2 : 시작 ~ 레어발견 OR 일정시간 경과
    ZERG_VERY_FAST_MUTAL(1, 0, 2, EnemyStrategyOptions.FactoryRatio.Weight.GOLIATH, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.GR, EnemyStrategyOptions.UpgradeOrder.FacUp.VM)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.ONE_STARPORT
            , TimeMapForZerg.ZERG_FAST_MUTAL()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.TURRET_OK, EnemyStrategyOptions.Mission.MissionType.ARMORY)),

    ZERG_FAST_MUTAL(2, 0, 1, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.GR) // camp=F_CHOKE
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForZerg.ZERG_FAST_MUTAL()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.TURRET_OK, EnemyStrategyOptions.Mission.MissionType.ARMORY)),

    ZERG_FAST_LURKER(3, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS) // camp=F_CHOKE
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForZerg.ZERG_FAST_LURKER()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.COMSAT_OK, EnemyStrategyOptions.Mission.MissionType.TURRET_OK, EnemyStrategyOptions.Mission.MissionType.TANK)),

    ZERG_FAST_1HAT_LURKER(3, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS)
            , EnemyStrategyOptions.MarineCount.EIGHT_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForZerg.ZERG_FAST_LURKER()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.COMSAT_OK, EnemyStrategyOptions.Mission.MissionType.TURRET_OK, EnemyStrategyOptions.Mission.MissionType.TANK)),

    ZERG_HYDRA_WAVE(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM) // camp=F_CHOKE
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForZerg.ZERG_NO_LAIR_HYDRA()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.TANK)),

    ZERG_NO_LAIR_LING(3, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.VS) // camp=F_CHOKE
            , EnemyStrategyOptions.MarineCount.EIGHT_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForZerg.ZERG_NO_LAIR_LING()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.VULTURE)),

    ZERG_NO_LAIR_HYDRA(1, 2, 0, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM) // camp=F_CHOKE
            , EnemyStrategyOptions.MarineCount.EIGHT_MARINE, EnemyStrategyOptions.AddOnOption.IMMEDIATELY, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForZerg.ZERG_NO_LAIR_HYDRA()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.TANK)),

    ZERG_LAIR_MIXED(2, 1, 2, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS) // camp=F_CHOKE
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForZerg.ZERG_LAIR_MIXED()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.TANK, EnemyStrategyOptions.Mission.MissionType.TURRET_OK)),
    // + 위험종료 : BASE근처에 적이 없음. 포지션별 터렛 완성. 골리앗 일정량 이상 보유.

    // PHASE3 : PHASE2 종료 ~
    ZERG_GROUND3(1, 5, 2, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.GR)),
    ZERG_GROUND2(1, 5, 3, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.GR)),
    ZERG_GROUND1(1, 4, 3, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.GR)),
    ZERG_MIXED(1, 3, 5, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.GR, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)),
    ZERG_AIR1(1, 2, 6, EnemyStrategyOptions.FactoryRatio.Weight.GOLIATH, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.GR, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)),
    ZERG_AIR2(1, 1, 10, EnemyStrategyOptions.FactoryRatio.Weight.GOLIATH, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.GR, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)),

    // [테란전 기본전략 : 2스타포트 클로킹 레이쓰]
    // : 1팩 -> 마린2기 -> 1벌처 (SCV죽이고) -> 2스타 -> 1스타애드온 클로킹개발 -> 레이스 몇개에서 출발할지는 TBD -> 팩토리애드온 -> 커맨드 -> 탱크
    // * 레이쓰는 빌드중인 건물 SCV를 최우선으로 공격한다. 특히 아머리, 엔베, 터렛, 아케데미

    // PHASE1 : 시작 ~ 팩토리 또는 아카데미 발견 OR 일정시간 경과
    TERRAN_INIT(1, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS)
            , EnemyStrategyOptions.MarineCount.TWO_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForTerran.TERRAN_MECHANIC()),

    TERRAN_MECHANIC(TERRAN_INIT),

    TERRAN_2BARRACKS(1, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForTerran.TERRAN_2BARRACKS()),

    TERRAN_BBS(1, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForTerran.TERRAN_BBS()),

    TERRAN_1BARRACKS_DOUBLE(1, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS)
            , EnemyStrategyOptions.MarineCount.TWO_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForTerran.TERRAN_1BARRACKS_DOUBLE()),

    TERRAN_NO_BARRACKS_DOUBLE(1, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.TS)
            , EnemyStrategyOptions.MarineCount.TWO_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForTerran.NO_BARRACKS_DOUBLE()),

    // PHASE2 : PHASE1 종료 ~ ?
    TERRAN_1FAC_DOUBLE(1, 0, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get()
            , EnemyStrategyOptions.MarineCount.TWO_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForTerran.TERRAN_1FAC_DOUBLE()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION)),

    TERRAN_1FAC_DOUBLE_1STAR(1, 0, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get()
            , EnemyStrategyOptions.MarineCount.TWO_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForTerran.TERRAN_1FAC_DOUBLE_1STAR()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION)),

    TERRAN_1FAC_DOUBLE_ARMORY(1, 0, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get()
            , EnemyStrategyOptions.MarineCount.TWO_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForTerran.TERRAN_1FAC_DOUBLE_ARMORY()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION)),

    TERRAN_2FAC(1, 0, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get()
            , EnemyStrategyOptions.MarineCount.TWO_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForTerran.TERRAN_2FAC()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION)),

    TERRAN_1FAC_1STAR(1, 0, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get()
            , EnemyStrategyOptions.MarineCount.TWO_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForTerran.TERRAN_1FAC_1STAR()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION)),

    TERRAN_2STAR(1, 0, 1, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get()
            , EnemyStrategyOptions.MarineCount.TWO_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_STARPORT
            , TimeMapForTerran.TERRAN_2STAR()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.ARMORY)),

    TERRAN_BIONIC(2, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.TS)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForTerran.TERRAN_BIONIC()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.VULTURE, EnemyStrategyOptions.Mission.MissionType.TANK)),

    TERRAN_2BARRACKS_1FAC(1, 1, 0, EnemyStrategyOptions.FactoryRatio.Weight.VULTURE, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VM)
            , EnemyStrategyOptions.MarineCount.FOUR_MARINE, EnemyStrategyOptions.AddOnOption.VULTURE_FIRST, EnemyStrategyOptions.ExpansionOption.TWO_FACTORY
            , TimeMapForTerran.TERRAN_2BARRACKS_1FAC()
            , EnemyStrategyOptions.Mission.missions(EnemyStrategyOptions.Mission.MissionType.EXPANSION, EnemyStrategyOptions.Mission.MissionType.VULTURE, EnemyStrategyOptions.Mission.MissionType.TANK)),

    TERRAN_DOUBLE_BIONIC(TERRAN_BIONIC), //

    TERRAN_DOUBLE_MECHANIC(TERRAN_1FAC_DOUBLE), //

    // PHASE3 : PHASE2 종료 ~
    TERRAN_MECHANIC_VULTURE_TANK(1, 8, 1, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)),
    TERRAN_MECHANIC_GOLIATH_TANK(1, 4, 1, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS, EnemyStrategyOptions.UpgradeOrder.FacUp.GR)),
    TERRAN_MECHANIC_GOL_GOL_TANK(1, 4, 4, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.GR, EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)),
//	TERRAN_MECHANIC_WRAITH_TANK(1, 8, 1, EnemyStrategyOptions.FactoryRatio.Weight.TANK, EnemyStrategyOptions.UpgradeOrder.get(EnemyStrategyOptions.UpgradeOrder.FacUp.VM, EnemyStrategyOptions.UpgradeOrder.FacUp.TS, EnemyStrategyOptions.UpgradeOrder.FacUp.VS)),
//	TERRAN_MECHANIC_BATTLE_TANK(0, 4, 1, EnemyStrategyOptions.UpgradeOrder.TS_VM_VS_GR),

    UNKNOWN(ZERG_INIT);

    public EnemyStrategyOptions.FactoryRatio factoryRatio;
    public List<MetaType> upgrade;
    public int marineCount;
    public EnemyStrategyOptions.AddOnOption addOnOption;
    public EnemyStrategyOptions.ExpansionOption expansionOption;
    public EnemyStrategyOptions.BuildTimeMap buildTimeMap;
    public List<EnemyStrategyOptions.Mission.MissionType> missionTypeList;

    private EnemyStrategy(EnemyStrategy strategy) {
        this.factoryRatio = strategy.factoryRatio;
        this.upgrade = strategy.upgrade;
        this.marineCount = strategy.marineCount;
        this.addOnOption = strategy.addOnOption;
        this.expansionOption = strategy.expansionOption;
        this.buildTimeMap = strategy.buildTimeMap;
        this.missionTypeList = strategy.missionTypeList;
    }

    private EnemyStrategy(int vulture, int tank, int goliath, int weight, List<MetaType> upgrade, int marineCount, EnemyStrategyOptions.AddOnOption addOnOption, EnemyStrategyOptions.ExpansionOption expansionOption, EnemyStrategyOptions.BuildTimeMap defaultTimeMap) {
        this(vulture, tank, goliath, weight, upgrade, marineCount, addOnOption, expansionOption, defaultTimeMap, Collections.emptyList());
    }

    private EnemyStrategy(int vulture, int tank, int goliath, int weight, List<MetaType> upgrade, int marineCount, EnemyStrategyOptions.AddOnOption addOnOption, EnemyStrategyOptions.ExpansionOption expansionOption, EnemyStrategyOptions.BuildTimeMap defaultTimeMap, List<EnemyStrategyOptions.Mission.MissionType> missionTypeList) {
        this.factoryRatio = EnemyStrategyOptions.FactoryRatio.ratio(vulture, tank, goliath, weight);
        this.upgrade = upgrade;
        this.marineCount = marineCount;
        this.addOnOption = addOnOption;
        this.expansionOption = expansionOption;
        this.buildTimeMap = defaultTimeMap;
        this.missionTypeList = missionTypeList;
    }

    private EnemyStrategy(int vulture, int tank, int goliath, int weight, List<MetaType> upgrade) {
        this.factoryRatio = EnemyStrategyOptions.FactoryRatio.ratio(vulture, tank, goliath, weight);
        this.upgrade = upgrade;
        this.marineCount = 0;
        this.addOnOption = null;
        this.expansionOption = null;
        this.buildTimeMap = new EnemyStrategyOptions.BuildTimeMap();
        this.missionTypeList = Collections.emptyList();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder().append("[").append(name()).append("]").append("\n").append(factoryRatio.toString()).append("\n");
        for (MetaType metaType : upgrade) {
            if (metaType.isUnit()) {
                sb.append(metaType.getUnitType()).append(" / ");
            } else if (metaType.isTech()) {
                sb.append(metaType.getTechType()).append(" / ");
            } else if (metaType.isUpgrade()) {
                sb.append(metaType.getUpgradeType()).append(" / ");
            }
        }
        sb.append("\n").append("MARINE COUNT=").append(marineCount).append("\n").append(addOnOption).append("\n").append(expansionOption).append("");
        sb.append("\n\n").append(buildTimeMap);
        return sb.toString();
    }
}
