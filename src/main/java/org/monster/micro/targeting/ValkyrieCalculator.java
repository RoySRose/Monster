package org.monster.micro.targeting;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.util.UnitUtils;

@Deprecated
public class ValkyrieCalculator extends TargetScoreCalculator {

    @Override
    public int calculate(Unit valkyrie, UnitInfo eui) {
        Unit enemy = UnitUtils.enemyUnitInSight(eui);
        if (enemy == null || !enemy.isDetected()) {
            return 0;
        }

        int distanceScore = 100; // 거리 점수
        int distanceToTarget = valkyrie.getDistance(enemy);
        if (distanceToTarget <= UnitType.Terran_Valkyrie.airWeapon().maxRange()) {
            distanceScore += distanceToTarget;
        } else {
            distanceScore += UnitType.Terran_Valkyrie.airWeapon().maxRange() - distanceToTarget;
        }

        int hitPointScore = 100; // HP 점수
        if (hitPointScore > 0) {
            hitPointScore -= enemy.getHitPoints();
        }

        return distanceScore + hitPointScore;
    }

}
