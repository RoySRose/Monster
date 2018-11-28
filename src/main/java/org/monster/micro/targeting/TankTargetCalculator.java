package org.monster.micro.targeting;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.util.InformationManager;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.UnitUtils;
import org.monster.micro.constant.MicroConfig;

import java.util.List;

@Deprecated
public class TankTargetCalculator extends TargetScoreCalculator {

    @Override
    public int calculate(Unit mechanicUnit, UnitInfo eui) {
        Unit enemy = UnitUtils.unitInSight(eui);
        if (enemy == null) {
            return 0;
        }

        UnitType enemyUnitType = enemy.getType();

        // 우선순위 점수 : 유닛 우선순위 맵
        int priorityScore = TargetPriority.getPriority(mechanicUnit.getType(), enemyUnitType);
        int splashScore = 0; // 스플래시 점수 : 시즈모드 탱크만 해당
        int distanceScore = 0; // 거리 점수 : 최고점수 100점. 멀수록 점수 높음.
        int hitPointScore = 0; // HP 점수 : 최고점수 50점. HP가 많을 수록 점수 낮음.
        int specialScore = 0; // 특별 점수 : 탱크앞에 붙어있는 밀리유닛 +100점

        List<Unit> unitsInSplash = enemy.getUnitsInRadius(MicroConfig.Tank.SIEGE_MODE_OUTER_SPLASH_RAD);
        for (Unit unitInSplash : unitsInSplash) {
            int splashUnitDistance = enemy.getDistance(unitInSplash.getPosition());
            int priorityInSpash = TargetPriority.getPriority(mechanicUnit, unitInSplash);
            if (splashUnitDistance <= MicroConfig.Tank.SIEGE_MODE_INNER_SPLASH_RAD) {
                priorityInSpash = (int) (priorityInSpash * 0.8);
            } else if (splashUnitDistance <= MicroConfig.Tank.SIEGE_MODE_MEDIAN_SPLASH_RAD) {
                priorityInSpash = (int) (priorityInSpash * 0.4);
            } else if (splashUnitDistance <= MicroConfig.Tank.SIEGE_MODE_OUTER_SPLASH_RAD) {
                priorityInSpash = (int) (priorityInSpash * 0.2);
            }

            // 아군일 경우 우선순위를 뺀다. priority값이 마이너스(-)가 나올 수도 있다. 이때는 타겟으로 지정하지 않는다.
            if (unitInSplash.getPlayer() == InformationManager.Instance().enemyPlayer) {
                splashScore += priorityInSpash;
            } else if (unitInSplash.getPlayer() == InformationManager.Instance().selfPlayer) {
                splashScore -= priorityInSpash;
            }
        }
        if (priorityScore + splashScore < 0) { // splash로 인해 아군피해가 더 심한 경우 skip
            return 0;
        }

        int distanceToTarget = mechanicUnit.getDistance(enemy.getPosition());
        distanceScore = 100 - distanceToTarget / 5;

        // 시즈모드 : 한방에 죽는다면 HP 높을 수록 우선순위가 높다.
        if (MicroUtils.killedByNShot(mechanicUnit, enemy, 1)) {
            hitPointScore = 50 + enemy.getHitPoints() / 10;
        } else {
            hitPointScore = 50 - enemy.getHitPoints() / 10;
        }

        return priorityScore + splashScore + distanceScore + hitPointScore + specialScore;
    }

}
