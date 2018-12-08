package org.monster.common;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.debugger.BigWatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnitBalancer {

    public static Map<Integer, Integer> mapper = new HashMap<>();
    public static int groupSize;
    private List<UnitType> listType = new ArrayList<>();

    public UnitBalancer() {
        groupSize = 0;


        listType.add(UnitType.Terran_Siege_Tank_Tank_Mode);
        listType.add(UnitType.Terran_Goliath);
        listType.add(UnitType.Terran_Valkyrie);
        listType.add(UnitType.Terran_Vulture);
        listType.add(UnitType.Terran_SCV);
        listType.add(UnitType.Terran_Science_Vessel);
        listType.add(UnitType.Terran_Wraith);
        listType.add(UnitType.Terran_Marine);

        listType.add(UnitType.Terran_Science_Facility);
        listType.add(UnitType.Terran_Engineering_Bay);
        listType.add(UnitType.Terran_Barracks);
        listType.add(UnitType.Terran_Command_Center);
    }

    public static boolean skipControl(Unit unit) {
        if (mapper.get(unit.getID()) == null) {
            System.out.println("성욱한테 말하기 오류는 안날꺼 unit.getID " + unit.getID() + ", " + unit.getType() + ", " + unit.isCompleted());
            return false;
        }
        return (TimeUtils.getFrame() % groupSize) != mapper.get(unit.getID());
    }

    public void update() {
        BigWatch.start("balancer");
        groupSize = LagObserver.groupsize();
        resetMap();
        BigWatch.record("balancer");
    }

    private void resetMap() {
        mapper.clear();
        int count = 0;
        for (UnitType unitType : listType) {

            List<Unit> units = UnitUtils.getCompletedUnitList(unitType);
            for (Unit unit : units) {
                mapper.put(unit.getID(), count);
                count++;
                if (count >= groupSize) {
                    count = 0;
                }
            }
            if (unitType == UnitType.Terran_Siege_Tank_Tank_Mode) {
                List<Unit> units2 = UnitUtils.getCompletedUnitList(UnitType.Terran_Siege_Tank_Siege_Mode);
                for (Unit unit : units2) {
                    mapper.put(unit.getID(), count);
                    count++;
                    if (count >= groupSize) {
                        count = 0;
                    }
                }
            }
        }
    }


}
