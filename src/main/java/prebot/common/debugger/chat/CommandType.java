package prebot.common.debugger.chat;

public enum CommandType {
    // default chatOrderables
    SPEED("s"), DISPLAY("d"),

    // mute executer
    MUTE("m");

    public String TYPE;

    CommandType(String type) {
        this.TYPE = type;
    }
}