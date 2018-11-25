package prebot.build.initialProvider.BlockingEntrance;

public enum Location {
    START(0),
    One(1),
    Three(2),
    Five(3),
    Six(4),
    Seven(5),
    Nine(6),
    Eleven(7),
    Twelve(8);

    private final int value;

    Location(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
