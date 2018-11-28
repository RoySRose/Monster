package org.monster.strategy.analyse.terran.unit;

import bwapi.UnitType;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.common.UnitInfo;
import org.monster.common.util.TimeUtils;
import org.monster.strategy.analyse.Clue;
import org.monster.strategy.manage.ClueManager;

import java.util.List;

public class MedicAnalyser extends UnitAnalyser {

    public MedicAnalyser() {
        super(UnitType.Terran_Medic);
    }

    @Override
    public void analyse() {
        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int medicFrame = EnemyStrategy.TERRAN_BIONIC.buildTimeMap.frame(UnitType.Terran_Academy) + UnitType.Terran_Medic.buildTime() + 20 * TimeUtils.SECOND;
            if (found.get(0).getUpdateFrame() < medicFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_MEDIC);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.MEDIC_FOUND);
            }
        }
    }
}
