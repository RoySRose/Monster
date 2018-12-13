package org.monster.strategy.analyse;

public class Clue {

    public static enum ClueType {
        // PROTOSS
        PROTOSS_GAS, FAST_GATE, FAST_NEXSUS, FAST_FORGE, FAST_CANNON, FAST_CORE,
        FAST_DRAGOON_RANGE, FAST_ADUN, FAST_TEMPLAR_ARCH, FAST_ROBO, FAST_ROBO_SUPPORT, FAST_OB, STARGATE,
        FLEET_BEACON,

        FAST_ZEALOT, FAST_DRAGOON, FAST_DARK, FAST_SHUTTLE, FAST_OBSERVER, FAST_REAVER,

        // ZERG
        ZERG_GAS, POOL, POOL_ASSUME, DOUBLE_HATCH,
        FAST_LAIR, LAIR_STATUS, SPIRE, HYDRADEN,

        FAST_ZERGLING,
        FAST_HYDRA, FAST_LURKER, FAST_MUTAL,

        // TERRAN
        TERRAN_GAS, FAST_BARRACK, FAST_COMMAND, FAST_ACADEMY,
        FAST_FACTORY, FAST_STARPORT, FAST_ARMORY,

        FAST_MARINE, MEDIC, FIREBAT,
        FAST_VULTURE, FAST_TANK, FAST_GOLIATH, FAST_WRAITH,

        DUMMY,
    }

    public static enum ClueInfo {
        // PROTOSS
        ASSIMILATOR_FAST(Clue.ClueType.PROTOSS_GAS),
        ASSIMILATOR_LATE(Clue.ClueType.PROTOSS_GAS),
        NO_ASSIMILATOR(Clue.ClueType.PROTOSS_GAS),

        FORGE_FAST_IN_EXPANSION(Clue.ClueType.FAST_FORGE),
        FORGE_FAST_IN_BASE(Clue.ClueType.FAST_FORGE),
        FORGE_FOUND(Clue.ClueType.FAST_FORGE),

        CANNON_FAST_IN_EXPANSION(Clue.ClueType.FAST_CANNON),
        CANNON_FAST_IN_BASE(Clue.ClueType.FAST_CANNON),
        CANNON_FAST_SOMEWHERE(Clue.ClueType.FAST_CANNON),

        GATE_NOT_FOUND(Clue.ClueType.FAST_GATE),
        GATE_FAST_ONE(Clue.ClueType.FAST_GATE),
        GATE_FAST_TWO(Clue.ClueType.FAST_GATE),
        GATE_ONE(Clue.ClueType.FAST_GATE),
        GATE_TWO(Clue.ClueType.FAST_GATE),

        CORE_FAST(Clue.ClueType.FAST_CORE),
        CORE_FOUND(Clue.ClueType.FAST_CORE),

        DRAGOON_RANGE_FAST(Clue.ClueType.FAST_DRAGOON_RANGE),

        ADUN_FAST(Clue.ClueType.FAST_ADUN),
        ADUN_FOUND(Clue.ClueType.FAST_ADUN),
        TEMPLAR_ARCH_FAST(Clue.ClueType.FAST_TEMPLAR_ARCH),
        TEMPLAR_ARCH_FOUND(Clue.ClueType.FAST_TEMPLAR_ARCH),

        ROBO_FAST(Clue.ClueType.FAST_ROBO),
        ROBO_FOUND(Clue.ClueType.FAST_ROBO),
        ROBO_SUPPORT_FAST(Clue.ClueType.FAST_ROBO_SUPPORT),
        ROBO_SUPPORT_FOUND(Clue.ClueType.FAST_ROBO_SUPPORT),
        OBSERVERTORY_FAST(Clue.ClueType.FAST_OB),
        OBSERVERTORY_FOUND(Clue.ClueType.FAST_OB),

        STARGATE_ONEGATE_FAST(Clue.ClueType.STARGATE),
        STARGATE_DOUBLE_FAST(Clue.ClueType.STARGATE),
        STARGATE_FOUND(Clue.ClueType.STARGATE),

        FAST_FLEET_BEACON(Clue.ClueType.FLEET_BEACON),
        FLEET_BEACON_FOUND(Clue.ClueType.FLEET_BEACON),

        NEXSUS_FASTEST_DOUBLE(Clue.ClueType.FAST_NEXSUS),
        NEXSUS_FAST_DOUBLE(Clue.ClueType.FAST_NEXSUS),
        NEXSUS_NOT_DOUBLE(Clue.ClueType.FAST_NEXSUS),

        FAST_ONE_ZEALOT(Clue.ClueType.FAST_ZEALOT),
        FAST_THREE_ZEALOT(Clue.ClueType.FAST_ZEALOT),
        FAST_DRAGOON(Clue.ClueType.FAST_DRAGOON),
        FAST_DARK(Clue.ClueType.FAST_DARK),
        FAST_SHUTTLE(Clue.ClueType.FAST_SHUTTLE),
        SHUTTLE_FOUND(Clue.ClueType.FAST_SHUTTLE),
        FAST_OBSERVER(Clue.ClueType.FAST_OBSERVER),
        FAST_REAVER(Clue.ClueType.FAST_REAVER),

        // ZERG
        EXTRACTOR_9DRONE(Clue.ClueType.ZERG_GAS),
        EXTRACTOR_OVERPOOL(Clue.ClueType.ZERG_GAS),
        EXTRACTOR_2HAT(Clue.ClueType.ZERG_GAS),
        EXTRACTOR_LATE(Clue.ClueType.ZERG_GAS),
        NO_EXTRACTOR(Clue.ClueType.ZERG_GAS),

        POOL_9DRONE_UNDER(Clue.ClueType.POOL_ASSUME),
        POOL_OVERPOOL_UNDER(Clue.ClueType.POOL_ASSUME),

        POOL_5DRONE(Clue.ClueType.POOL),
        POOL_9DRONE(Clue.ClueType.POOL),
        POOL_OVERPOOL(Clue.ClueType.POOL),
        POOL_2HAT(Clue.ClueType.POOL),
        LATE_POOL(Clue.ClueType.POOL),

        DOUBLE_HATCH_3HAT(Clue.ClueType.DOUBLE_HATCH),
        DOUBLE_HATCH_12HAT(Clue.ClueType.DOUBLE_HATCH),
        DOUBLE_HATCH_OVERPOOL(Clue.ClueType.DOUBLE_HATCH),
        DOUBLE_HATCH_9DRONE(Clue.ClueType.DOUBLE_HATCH),
        DOUBLE_HATCH_LATE(Clue.ClueType.DOUBLE_HATCH),
        TWIN_HATCH(Clue.ClueType.DOUBLE_HATCH),

        LAIR_1HAT_FAST(Clue.ClueType.FAST_LAIR),
        LAIR_2HAT_FAST(Clue.ClueType.FAST_LAIR),
        LAIR_3HAT_FAST(Clue.ClueType.FAST_LAIR),

        NO_LAIR(Clue.ClueType.LAIR_STATUS),
        LAIR_INCOMPLETE(Clue.ClueType.LAIR_STATUS),
        LAIR_COMPLETE(Clue.ClueType.LAIR_STATUS),

        HYDRADEN_BEFORE_LAIR_START(Clue.ClueType.HYDRADEN),
        HYDRADEN_BEFORE_LAIR_COMPLETE(Clue.ClueType.HYDRADEN),
        HYDRADEN(Clue.ClueType.HYDRADEN),

        FAST_SPIRE(Clue.ClueType.SPIRE),
        SPIRE(Clue.ClueType.SPIRE),

        FAST_OVERTEN_ZERGLING(Clue.ClueType.FAST_ZERGLING),
        FAST_EIGHT_ZERGLING(Clue.ClueType.FAST_ZERGLING),
        FAST_SIX_ZERGLING(Clue.ClueType.FAST_ZERGLING),

        FAST_HYDRA(Clue.ClueType.FAST_HYDRA),
        FIVE_MANY_HYDRA(Clue.ClueType.FAST_HYDRA),
        THREE_MANY_HYDRA(Clue.ClueType.FAST_HYDRA),
        FAST_LURKER(Clue.ClueType.FAST_LURKER),
        FAST_MUTAL(Clue.ClueType.FAST_MUTAL),

        // TERRAN
        REFINERY_FAST(Clue.ClueType.TERRAN_GAS),
        REFINERY_LATE(Clue.ClueType.TERRAN_GAS),
        NO_REFINERY(Clue.ClueType.TERRAN_GAS),

        BARRACK_NOT_FOUND(Clue.ClueType.FAST_BARRACK),
        BARRACK_FAST_ONE(Clue.ClueType.FAST_BARRACK),
        BARRACK_FAST_TWO(Clue.ClueType.FAST_BARRACK),
        BARRACK_FASTEST_TWO(Clue.ClueType.FAST_BARRACK),
        BARRACK_ONE(Clue.ClueType.FAST_BARRACK),
        BARRACK_TWO(Clue.ClueType.FAST_BARRACK),

        ACADEMY_FAST(Clue.ClueType.FAST_ACADEMY),
        ACADEMY_FOUND(Clue.ClueType.FAST_ACADEMY),

        ARMORY_FAST(Clue.ClueType.FAST_ARMORY),
        ARMORY_FOUND(Clue.ClueType.FAST_ARMORY),

        FACTORY_NOT_FOUND(Clue.ClueType.FAST_FACTORY),
        FACTORY_FAST_ONE(Clue.ClueType.FAST_FACTORY),
        FACTORY_FAST_TWO(Clue.ClueType.FAST_FACTORY),
        FACTORY_ONE(Clue.ClueType.FAST_FACTORY),
        FACTORY_TWO(Clue.ClueType.FAST_FACTORY),

        STARPORT_FAST_ONE(Clue.ClueType.FAST_STARPORT),
        STARPORT_FAST_TWO(Clue.ClueType.FAST_STARPORT),
        STARPORT_ONE(Clue.ClueType.FAST_STARPORT),
        STARPORT_TWO(Clue.ClueType.FAST_STARPORT),

        COMMAND_FASTEST_DOUBLE(Clue.ClueType.FAST_COMMAND),
        COMMAND_FAST_DOUBLE(Clue.ClueType.FAST_COMMAND),
        COMMAND_FAC_DOUBLE(Clue.ClueType.FAST_COMMAND),

        FAST_SIX_MARINE(Clue.ClueType.FAST_MARINE),
        FAST_FOUR_MARINE(Clue.ClueType.FAST_MARINE),
        FAST_TWO_MARINE(Clue.ClueType.FAST_MARINE),

        FAST_SIX_VULTURE(Clue.ClueType.FAST_VULTURE),
        FAST_FOUR_VULTURE(Clue.ClueType.FAST_VULTURE),
        FAST_TWO_VULTURE(Clue.ClueType.FAST_VULTURE),

        FAST_MEDIC(Clue.ClueType.MEDIC),
        MEDIC_FOUND(Clue.ClueType.MEDIC),
        FAST_FIREBAT(Clue.ClueType.FIREBAT),
        FIREBAT_FOUND(Clue.ClueType.FIREBAT),

        FAST_ONE_TANK(Clue.ClueType.FAST_TANK),
        FAST_TWO_TANK(Clue.ClueType.FAST_TANK),
        FAST_THREE_TANK(Clue.ClueType.FAST_TANK),
        TANK_FOUND(Clue.ClueType.FAST_TANK),

        FAST_ONE_GOLIATH(Clue.ClueType.FAST_GOLIATH),
        FAST_TWO_GOLIATH(Clue.ClueType.FAST_GOLIATH),
        FAST_THREE_GOLIATH(Clue.ClueType.FAST_GOLIATH),
        GOLIATH_FOUND(Clue.ClueType.FAST_GOLIATH),

        FAST_ONE_WRAITH(Clue.ClueType.FAST_WRAITH),
        FAST_TWO_WRAITH(Clue.ClueType.FAST_WRAITH),
        FAST_THREE_WRAITH(Clue.ClueType.FAST_WRAITH),
        WRAITH_FOUND(Clue.ClueType.FAST_WRAITH),

        DUMMY(Clue.ClueType.DUMMY);

        public Clue.ClueType type;

        private ClueInfo(Clue.ClueType type) {
            this.type = type;
        }
    }
}
