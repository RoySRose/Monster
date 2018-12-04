package org.monster.common.util;

import bwapi.Color;
import bwapi.Position;
import org.monster.common.constant.CommonConfig;

import java.io.*;

public class DrawingUtils {

    public static void drawCircleMap(Position lastPosition, int i, Color color, boolean b) {
        DrawDebugger.Instance().drawCircleMap(lastPosition, i, color, b);
    }
}