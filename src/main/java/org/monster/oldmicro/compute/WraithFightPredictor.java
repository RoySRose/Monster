package org.monster.oldmicro.compute;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.UnitUtils;
import org.monster.strategy.constant.StrategyCode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WraithFightPredictor {

    private static final int POWER_WRAITH = 100;
    private static final int POWER_CLOAKING = 300;
    private static final Map<UnitType, Integer> WRAITH_TARGET = new HashMap<>();

    static {
        WRAITH_TARGET.put(UnitType.Zerg_Mutalisk, 80);
        WRAITH_TARGET.put(UnitType.Zerg_Hydralisk, 150);
        WRAITH_TARGET.put(UnitType.Zerg_Scourge, 100);
        WRAITH_TARGET.put(UnitType.Zerg_Devourer, 300);
        WRAITH_TARGET.put(UnitType.Zerg_Spore_Colony, 1200);

        WRAITH_TARGET.put(UnitType.Terran_Marine, 80);
        WRAITH_TARGET.put(UnitType.Terran_Medic, 150);
        WRAITH_TARGET.put(UnitType.Terran_Ghost, 150);
        WRAITH_TARGET.put(UnitType.Terran_Goliath, 400);
        WRAITH_TARGET.put(UnitType.Terran_Wraith, 100);
        WRAITH_TARGET.put(UnitType.Terran_Valkyrie, 300);
        WRAITH_TARGET.put(UnitType.Terran_Battlecruiser, 500);
        WRAITH_TARGET.put(UnitType.Terran_Bunker, 1000);
        WRAITH_TARGET.put(UnitType.Terran_Missile_Turret, 800);
    }

    public static StrategyCode.SmallFightPredict airForcePredictByUnitInfo(Collection<Unit> wraithList, Collection<UnitInfo> euiList, boolean cloakingBonus, boolean mainSquadBonus) {
        int wraithPower = powerOfAirForce(wraithList, cloakingBonus);
        if (mainSquadBonus) {
            wraithPower += 1000;
//			System.out.println("Bonus! 1000++");
        }
        int enemyPower = powerOfEnemies(euiList);
//		System.out.println(wraithPower + "(" + wraithList.size() + ")" + " / " + enemyPower + "(" + euiList.size() + ")");

        if (wraithPower > enemyPower) {
            return StrategyCode.SmallFightPredict.ATTACK;
        } else {
            return StrategyCode.SmallFightPredict.BACK;
        }
    }

    // 레이쓰 한기 최대 점수 : 100점
    public static int powerOfAirForce(Collection<Unit> wraithList, boolean cloakingBonus) {
        int totalPower = 0;
        for (Unit wraith : wraithList) {
            double hitPointRate = (double) wraith.getHitPoints() / UnitType.Terran_Wraith.maxHitPoints();
            totalPower += POWER_WRAITH * hitPointRate;

            if (cloakingBonus) {
                totalPower += POWER_CLOAKING;
            }
        }
        return totalPower;
    }

    public static int powerOfEnemies(Collection<UnitInfo> euiList) {
        int enemyPower = 0;
        for (UnitInfo eui : euiList) {
            enemyPower += powerOfUnit(eui);
        }
        return enemyPower;
    }

    private static int powerOfUnit(UnitInfo eui) {
        Integer enemyPower = WRAITH_TARGET.get(eui.getType());
        if (enemyPower == null) {
            return 0;
        }
        Unit unitInSight = UnitUtils.enemyUnitInSight(eui);
        if (unitInSight != null) {
            double hitPointRate;
            if (unitInSight.isCloaked()) {
                if (MicroUtils.totalComsatCount() <= 0) {
                    hitPointRate = 5.0;
                } else {
                    hitPointRate = 1.0;
                }
            } else {
                hitPointRate = (double) unitInSight.getHitPoints() / eui.getType().maxHitPoints();
            }
            int power = (int) (enemyPower * hitPointRate);
//			System.out.println(power);
            return power;
        } else {
            return enemyPower;
        }
    }

}
