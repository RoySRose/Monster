package org.monster.common.util;

import bwapi.Game;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

import java.util.HashMap;
import java.util.Map;

public class TimeInfoCollector implements InfoCollector {

    private static TimeInfoCollector instance = new TimeInfoCollector();
    protected static TimeInfoCollector Instance() {
        return instance;
    }

    private Game Broodwar;

    private static Map<UnitType, Integer> baseToBaseUnit = new HashMap<>();

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
    }

    @Override
    public void update() {

    }

    public int getframe() {
        return Broodwar.getFrameCount();
    }

    // 대략적인 enemyFirstExpansion <-> myFirstExpansion 사이에 unitType이 이동하는데 걸리는 시간 리턴 (단위 frame)
    protected int baseToBaseFrame(UnitType unitType) {

        Integer baseToBaseFrame = baseToBaseUnit.get(unitType);

        if (baseToBaseFrame == null) {
            BaseLocation selfFirstExpansion = BaseUtils.myFirstExpansion();

            BaseLocation enemyFirstExpansion;
            if (BaseUtils.enemyFirstExpansion() != null) {
                enemyFirstExpansion = BaseUtils.enemyFirstExpansion();
            } else {
                // TODO 가로방향 base의 first expansion으로 계산 <--- need fix. 가장 가까운 베이스가 첫 멀티지역이 아닐수도 있음
                enemyFirstExpansion = BWTA.getNearestBaseLocation(selfFirstExpansion.getPosition());
            }

            if (unitType.isFlyer()) {
                baseToBaseFrame = (int) (selfFirstExpansion.getGroundDistance(enemyFirstExpansion)
                        / (unitType.topSpeed()));
            } else {
                baseToBaseFrame = (int) (selfFirstExpansion.getAirDistance(enemyFirstExpansion)
                        / (unitType.topSpeed()));
            }
            baseToBaseUnit.put(unitType, baseToBaseFrame);
        }
        return baseToBaseFrame;
    }

    protected void clearBaseToBaseFrame() {
        baseToBaseUnit.clear();
    }
}
