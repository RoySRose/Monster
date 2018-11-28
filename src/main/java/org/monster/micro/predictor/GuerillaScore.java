package org.monster.micro.predictor;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.micro.constant.MicroConfig;

import java.util.Collection;
import java.util.List;

public class GuerillaScore {
    public static int guerillaScore(List<Unit> enemyUnitList) {
        int score = 0;
        for (Unit unit : enemyUnitList) {
            if (unit.getType().isResourceDepot()) {
                score += 100;
            } else if (unit.getType().isWorker()) {
                score += 100;
            } else if (unit.getType().isBuilding() && !unit.getType().canAttack()) {
                score += 20;
            } else if (unit.getType().groundWeapon().maxRange() < MicroConfig.Tank.SIEGE_MODE_MIN_RANGE) {
                score += 10;
            } else if (!unit.getType().isFlyer()) {
                score += 5;
            } else {
                score -= 100;
            }
        }
        return score;
    }

    public static int guerillaScoreByUnitInfo(Collection<UnitInfo> euiList) {
        int score = 0;
        for (UnitInfo eui : euiList) {
            if (eui.getType().isResourceDepot()) { // 자원채취한다 100점
                score += 100;
            } else if (eui.getType() == UnitType.Terran_Supply_Depot || eui.getType() == UnitType.Protoss_Pylon) {
                score += 100;
            } else if (eui.getType().isWorker()) { // 일꾼있다 100점
                score += 100;
            } else if (eui.getType().isBuilding() && !eui.getType().canAttack()) { // 공격할 수 없는 건물
                score += 20;
            } else if (eui.getType().groundWeapon().maxRange() < MicroConfig.Tank.SIEGE_MODE_MIN_RANGE) { // melee 유닛 10점
                score += 10;
            } else if (!eui.getType().isFlyer()) { // 걷는애 5점
                score += 5;
            }
        }
        if (score < 50) { // 채소50점이상
            return 0;
        } else {
            return score;
        }
    }
}
