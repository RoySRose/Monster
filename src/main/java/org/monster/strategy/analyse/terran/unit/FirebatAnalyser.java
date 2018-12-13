package org.monster.strategy.analyse.terran.unit;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.util.TimeUtils;
import org.monster.strategy.constant.EnemyStrategy;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;

import java.util.List;

public class FirebatAnalyser extends UnitAnalyser {

    public FirebatAnalyser() {
        super(UnitType.Terran_Firebat);
    }

    @Override
    public void analyse() {
        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int medicFrame = EnemyStrategy.TERRAN_BIONIC.buildTimeMap.frame(UnitType.Terran_Academy) + UnitType.Terran_Firebat.buildTime() + 20 * TimeUtils.SECOND;
            if (found.get(0).getUpdateFrame() < medicFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_FIREBAT);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FIREBAT_FOUND);
            }
        }
    }
}
