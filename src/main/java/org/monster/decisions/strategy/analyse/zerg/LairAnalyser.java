package org.monster.decisions.strategy.analyse.zerg;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.common.UnitInfo;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;
import org.monster.decisions.strategy.manage.EnemyBuildTimer;
import org.monster.decisions.strategy.manage.StrategyAnalyseManager;

import java.util.List;

public class LairAnalyser extends UnitAnalyser {

    public LairAnalyser() {
        super(UnitType.Zerg_Lair);
    }

    @Override
    public void analyse() {
        fastLair();
        lairStatus();
    }

    private void fastLair() {
        if (ClueManager.Instance().containsClueType(Clue.ClueType.FAST_LAIR)) {
            return;
        }

        int oneHatFrame = EnemyStrategy.ZERG_OVERPOOL_GAS.buildTimeMap.frame(UnitType.Zerg_Lair, 30);
        int twoHatFrame = EnemyStrategy.ZERG_2HAT_GAS.buildTimeMap.frame(UnitType.Zerg_Lair, 30);
        int threeHatFrame = EnemyStrategy.ZERG_3HAT.buildTimeMap.frame(UnitType.Zerg_Lair, 30);

        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));

            if (buildFrame < oneHatFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.LAIR_1HAT_FAST);
            } else if (buildFrame < twoHatFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.LAIR_2HAT_FAST);
            } else if (buildFrame < threeHatFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.LAIR_3HAT_FAST);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.LAIR_3HAT_FAST);
            }
        }
    }

    private void lairStatus() {
        if (ClueManager.Instance().containsClueInfo(Clue.ClueInfo.LAIR_COMPLETE)) {
            return;
        }

        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            UnitInfo lairInfo = found.get(0);
            Unit lairInSight = UnitUtils.unitInSight(lairInfo);
            if (lairInSight != null && lairInSight.isCompleted()) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.LAIR_COMPLETE);
            } else {
                int updateFrame = found.get(0).getUpdateFrame();
                if (TimeUtils.after(updateFrame + UnitType.Zerg_Lair.buildTime())) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.LAIR_COMPLETE);
                } else {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.LAIR_INCOMPLETE);
                }
            }

        } else {
            int lairExpect = EnemyBuildTimer.Instance().getBuildStartFrameExpect(UnitType.Zerg_Lair) + 5 * TimeUtils.SECOND;
            int baseLastBaseCheckFrame = StrategyAnalyseManager.Instance().lastCheckFrame(StrategyAnalyseManager.LastCheckLocation.BASE);
            int baseLastExpansionCheckFrame = StrategyAnalyseManager.Instance().lastCheckFrame(StrategyAnalyseManager.LastCheckLocation.FIRST_EXPANSION);

            if (baseLastBaseCheckFrame > lairExpect && baseLastExpansionCheckFrame > lairExpect) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.NO_LAIR);
            }
        }
    }

}
