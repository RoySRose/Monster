package org.monster.common.util;

import org.monster.common.util.internal.MapSpecificInformation;

public class MapUtils {

    public static MapSpecificInformation getMapSpecificInformation() {
        return StaticMapInfoCollector.Instance().mapSpecificInformation;
    }

}
