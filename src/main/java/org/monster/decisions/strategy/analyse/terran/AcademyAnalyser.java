package org.monster.decisions.strategy.analyse.terran;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;

import java.util.List;

public class AcademyAnalyser extends UnitAnalyser {

    public AcademyAnalyser() {
        super(UnitType.Terran_Academy);
    }

    @Override
    public void analyse() {
        fastAcademy();
    }

    private void fastAcademy() {
        if (ClueManager.Instance().containsClueType(Clue.ClueType.FAST_ACADEMY)) {
            return;
        }

        List<UnitInfo> found = found();
        if (found.isEmpty()) {
            return;
        }

        int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
        int academyFrame = EnemyStrategy.TERRAN_BIONIC.buildTimeMap.frame(UnitType.Terran_Academy, 30);

        if (academyFrame < buildFrame) {
            ClueManager.Instance().addClueInfo(Clue.ClueInfo.ACADEMY_FAST);
        } else {
            ClueManager.Instance().addClueInfo(Clue.ClueInfo.ACADEMY_FOUND);
        }
    }
}
