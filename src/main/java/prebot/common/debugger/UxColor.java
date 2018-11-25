package prebot.common.debugger;

import bwapi.Color;
import prebot.micro.squad.CheckerSquad;
import prebot.micro.squad.EarlyDefenseSquad;
import prebot.micro.squad.GuerillaSquad;
import prebot.micro.squad.MainAttackSquad;
import prebot.micro.squad.MultiDefenseSquad;
import prebot.micro.squad.ScvScoutSquad;
import prebot.micro.squad.SpecialSquad;
import prebot.micro.squad.Squad;
import prebot.micro.squad.WatcherSquad;

import java.util.HashMap;
import java.util.Map;

public class UxColor {

    public static final char CHAR_RED = (char) 0x08;
    public static final char CHAR_BLUE = (char) 0x0E;
    public static final char CHAR_TEAL = (char) 0x0F;
    public static final char CHAR_PURPLE = (char) 0x10;
    public static final char CHAR_ORANGE = (char) 0x11;
    public static final char CHAR_BROWN = (char) 0x15;
    public static final char CHAR_WHITE = (char) 0x16;
    public static final char CHAR_YELLOW = (char) 0x17;
    public static final char CHAR_GREEN = (char) 0x18;
    public static final char CHAR_CYAN = (char) 0x19;
    public static final char CHAR_BLACK = (char) 0x14;
    public static final char CHAR_GREY = (char) 0x05;

    public static final Map<Class<? extends Squad>, Color> SQUAD_COLOR = new HashMap<>();
    public static final Map<Color, Character> COLOR_TO_CHARACTER = new HashMap<>();

    static {
        SQUAD_COLOR.put(EarlyDefenseSquad.class, Color.Yellow);
        SQUAD_COLOR.put(MainAttackSquad.class, Color.Red);
        SQUAD_COLOR.put(WatcherSquad.class, Color.Blue);
        SQUAD_COLOR.put(CheckerSquad.class, Color.Orange);
        SQUAD_COLOR.put(ScvScoutSquad.class, Color.Black);
        SQUAD_COLOR.put(SpecialSquad.class, Color.Purple);
        SQUAD_COLOR.put(GuerillaSquad.class, Color.Cyan);
        SQUAD_COLOR.put(MultiDefenseSquad.class, Color.Teal);

        COLOR_TO_CHARACTER.put(Color.Red, CHAR_RED);
        COLOR_TO_CHARACTER.put(Color.Blue, CHAR_BLUE);
        COLOR_TO_CHARACTER.put(Color.Teal, (char) CHAR_TEAL);
        COLOR_TO_CHARACTER.put(Color.Purple, (char) CHAR_PURPLE);
        COLOR_TO_CHARACTER.put(Color.Orange, (char) CHAR_ORANGE);
        COLOR_TO_CHARACTER.put(Color.Brown, (char) CHAR_BROWN);
        COLOR_TO_CHARACTER.put(Color.White, (char) CHAR_WHITE);
        COLOR_TO_CHARACTER.put(Color.Yellow, (char) CHAR_YELLOW);
        COLOR_TO_CHARACTER.put(Color.Green, (char) CHAR_GREEN);
        COLOR_TO_CHARACTER.put(Color.Cyan, (char) CHAR_CYAN);
        COLOR_TO_CHARACTER.put(Color.Black, (char) CHAR_BLACK);
        COLOR_TO_CHARACTER.put(Color.Grey, (char) CHAR_GREY);
    }
}
