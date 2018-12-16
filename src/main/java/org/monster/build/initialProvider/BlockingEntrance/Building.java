package org.monster.build.initialProvider.BlockingEntrance;

@Deprecated
public enum Building {
    START(0),
    FIRST_SUPPLY(1),
    SECOND_SUPPLY(2),
    BARRACK(3),
    FACTORY(4),
    FACTORY2(5),
    BUNKER1(6),
    BUNKER2(7),
    ENTRANCE_TURRET1(8),
    ENTRANCE_TURRET2(9),
    SUPPLY_AREA(10),
    STARPORT1(11),
    STARPORT2(12),
    BARRACK_LAND(13);

    private final int value;

    Building(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
