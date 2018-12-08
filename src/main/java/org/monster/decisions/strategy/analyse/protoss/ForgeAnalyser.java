package org.monster.decisions.strategy.analyse.protoss;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.RegionType;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;

import java.util.List;

public class ForgeAnalyser extends UnitAnalyser {

    public ForgeAnalyser() {
        super(UnitType.Protoss_Forge);
    }

    @Override
    public void analyse() {
        fastForge();
    }

    private void fastForge() {
        List<UnitInfo> found = found(RegionType.ENEMY_FIRST_EXPANSION);
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            int forgeDoubleForgeFrame = EnemyStrategy.PROTOSS_FORGE_DOUBLE.buildTimeMap.frame(UnitType.Protoss_Forge, 15);
            if (buildFrame < forgeDoubleForgeFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FORGE_FAST_IN_EXPANSION);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FORGE_FOUND);
            }

        } else {
            found = found();
            if (!found.isEmpty()) {
                int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
                int forgeDoubleForgeFrame = EnemyStrategy.PROTOSS_FORGE_DOUBLE.buildTimeMap.frame(UnitType.Protoss_Forge, 15);
                if (buildFrame < forgeDoubleForgeFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FORGE_FAST_IN_BASE);
                } else {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.FORGE_FOUND);
                }
            }
        }
    }

}
