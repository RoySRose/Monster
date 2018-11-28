package org.monster.micro.targeting;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.UnitUtils;
import org.monster.micro.constant.MicroConfig;

import java.util.List;

//TODO NEED CHECK
public class DefaultTargetCalculator extends TargetScoreCalculator {

    @Override
    public int calculate(Unit unit, UnitInfo eui) {
        boolean enemyIsComplete = eui.isCompleted();
        Position enemyPosition = eui.getLastPosition();
        UnitType enemyUnitType = eui.getType();

        Unit enemyUnit = UnitUtils.unitInSight(eui);
        if (enemyUnit != null) {
            enemyIsComplete = enemyUnit.isCompleted();
            enemyPosition = enemyUnit.getPosition();
            enemyUnitType = enemyUnit.getType();
        }

        int distanceScore = 0; // 거리 점수 : 최고 100점(사정거리안). 멀수록 감점
        int hitPointScore = 0; // HP 점수 : 최고 50점. 체력이 많을수록 감점
        int specialScore = 0; // 특별 점수 : 탱크앞에 붙어있는 밀리유닛 +100점

        if (enemyUnit != null) {
            distanceScore = getDistanceScore(unit, enemyUnit.getPosition(), true);
            hitPointScore = getHitPointScore(enemyUnit.getHitPoints());
            specialScore = getSpecialScore(enemyUnit);

        } else {
            distanceScore = getDistanceScore(unit, eui.getLastPosition(), false);
            hitPointScore = getHitPointScore(eui.getLastHealth());
            specialScore = -100;
        }

        // 우선순위 점수 : 유닛 우선순위 맵
        int priorityScore = TargetPriority.getPriority(unit.getType(), enemyUnitType);
        return priorityScore + distanceScore + hitPointScore + specialScore;
    }

    private int getDistanceScore(Unit unit, Position enemyPosition, boolean inSight) {
        int distanceScore = inSight ? 100 : 0;
        int substract = unit.getDistance(enemyPosition) / 5;
        return distanceScore - substract;
    }

    private int getHitPointScore(int hitPoint) {
        int hitPointScore = 50;
        int substract = hitPoint / 10;
        return hitPointScore - substract;
    }

    private int getSpecialScore(Unit enemyUnit) {
        int specialScore = 0;
        if (enemyUnit.getType().groundWeapon().maxRange() <= MicroConfig.Tank.SIEGE_MODE_MIN_RANGE) {
            List<Unit> nearSiegeMode = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, enemyUnit.getPosition(), MicroConfig.Tank.SIEGE_MODE_MIN_RANGE, UnitType.Terran_Siege_Tank_Siege_Mode);
            if (!nearSiegeMode.isEmpty()) {
                return 100;
            }
        }
        return specialScore;
    }

}
