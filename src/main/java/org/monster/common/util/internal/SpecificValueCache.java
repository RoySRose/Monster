package org.monster.common.util.internal;


import org.monster.common.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class SpecificValueCache {

    private static Map<ValueType, Object> data = new HashMap<>();

    ;
    private static Map<ValueType, Integer> dataSavedFrame = new HashMap<>();

    public static <T> T get(ValueType valueType, Class<T> type) {
        Object value = data.get(valueType);
        Integer frame = dataSavedFrame.get(valueType);
        if (frame == null || frame < TimeUtils.getFrame()) {
            return null;
        }
        return type.cast(value);
    }

    public static void put(ValueType valueType, Object value) {
        data.put(valueType, value);
        dataSavedFrame.put(valueType, TimeUtils.getFrame());
    }

    public enum ValueType {
        ACTIVATED_COMMAND_COUNT,
        AVAILABLE_SCANNING_COUNT,
        FACTORY_SUPPLY_COUNT,

        ENEMY_GROUND_UNIT_POWER,
        ENEMY_AIR_UNIT_POWER,
        ENEMY_FIRST_EXPANSION_OCCUPIED,
    }

}
