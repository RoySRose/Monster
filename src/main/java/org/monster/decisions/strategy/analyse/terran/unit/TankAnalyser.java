package org.monster.decisions.strategy.analyse.terran.unit;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;

import java.util.List;

public class TankAnalyser extends UnitAnalyser {

    public TankAnalyser() {
        super(UnitType.Terran_Siege_Tank_Tank_Mode);
    }

    @Override
    public void analyse() {
        // 시즈탱크는 부득이하게 UnitUtils, TimeUtils를 써야겠다.
        List<UnitInfo> enemyTanks = UnitUtils.getEnemyUnitInfoList(CommonCode.EnemyUnitVisibleStatus.ALL,
                UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode);

        if (!enemyTanks.isEmpty()) {
            if (enemyTanks.size() >= 3) {
                int tankInMyRegionFrame = tankInMyRegionFrame(3) + 15 * TimeUtils.SECOND;
                if (TimeUtils.before((tankInMyRegionFrame))) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_THREE_TANK);
                }
            }
            if (enemyTanks.size() >= 2) {
                int tankInMyRegionFrame = tankInMyRegionFrame(2);
                if (TimeUtils.before((tankInMyRegionFrame))) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_TWO_TANK);
                }
            } else if (enemyTanks.size() >= 1) {
                int tankInMyRegionFrame = tankInMyRegionFrame(1);
                if (TimeUtils.before((tankInMyRegionFrame))) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_ONE_TANK);
                }
            }

            if (!ClueManager.Instance().containsClueType(Clue.ClueType.FAST_TANK)) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.TANK_FOUND);
            }
        }
    }

    private int tankInMyRegionFrame(int enemyCount) {
        int factoryCompleteFrame = EnemyStrategy.TERRAN_2FAC.buildTimeMap.frame(UnitType.Terran_Factory, 10) + UnitType.Terran_Factory.buildTime();
        int nTankCompleteFrame = factoryCompleteFrame + UnitType.Terran_Siege_Tank_Tank_Mode.buildTime() * (enemyCount / 2 + 1);
        return nTankCompleteFrame + baseToBaseFrame(UnitType.Terran_Siege_Tank_Tank_Mode);
    }
}
