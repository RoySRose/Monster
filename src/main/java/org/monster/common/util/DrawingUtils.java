package org.monster.common.util;

import bwapi.Color;
import bwapi.Position;
import org.monster.common.constant.CommonCode;

public class DrawingUtils {

    public static void drawCircleMap(Position lastPosition, int i, Color color, boolean b) {
        DrawDebugger.Instance().drawCircleMap(lastPosition, i, color, b);
    }

    public static String framesToTimeString(int frames) {
        if (frames == CommonCode.UNKNOWN) {
            return "unknown";
        }

        int minutes = TimeUtils.framesToMinutes(frames);
        int seconds = TimeUtils.framesToSeconds(frames - minutes * TimeUtils.MINUTE);
        return minutes + "min " + seconds + "sec";
    }

    public static Position positionAdjusted(Position position, int x, int y) {
        if (!PositionUtils.isValidPosition(position)) {
            return Position.None;
        }
        return new Position(position.getX() + x, position.getY() + y).makeValid();
    }
}