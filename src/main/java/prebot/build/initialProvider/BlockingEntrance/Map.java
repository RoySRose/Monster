package prebot.build.initialProvider.BlockingEntrance;

public enum Map {
    UNKNOWN(0),
    OVERWATCH(1),
    CIRCUITBREAKER(2),
    FIGHTING_SPIRITS(3);

    private final int value;

    Map(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
