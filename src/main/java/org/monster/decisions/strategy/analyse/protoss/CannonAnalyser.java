package org.monster.decisions.strategy.analyse.protoss;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.decisions.constant.EnemyStrategy;
import org.monster.decisions.strategy.analyse.Clue;
import org.monster.decisions.strategy.analyse.UnitAnalyser;
import org.monster.decisions.strategy.manage.ClueManager;

import java.util.List;

public class CannonAnalyser extends UnitAnalyser {

    public CannonAnalyser() {
        super(UnitType.Protoss_Photon_Cannon);
    }

    @Override
    public void analyse() {
        fastCannon();
    }

    private void fastCannon() {
        int forgeDoulbeCannonFrame = EnemyStrategy.PROTOSS_FORGE_DOUBLE.buildTimeMap.frame(UnitType.Protoss_Photon_Cannon, 15);
        List<UnitInfo> found = found(CommonCode.RegionType.ENEMY_FIRST_EXPANSION);
        if (!found.isEmpty()) {
            int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
            if (buildFrame < forgeDoulbeCannonFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.CANNON_FAST_IN_EXPANSION);
            }
        } else {
            found = found(CommonCode.RegionType.ENEMY_BASE);
            if (!found.isEmpty()) {
                int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
                if (buildFrame < forgeDoulbeCannonFrame) {
                    ClueManager.Instance().addClueInfo(Clue.ClueInfo.CANNON_FAST_IN_BASE);
                }
            } else {
                found = found();
                if (!found.isEmpty()) {
                    int buildFrame = buildStartFrameDefaultJustBefore(found.get(0));
                    if (buildFrame < forgeDoulbeCannonFrame) {
                        ClueManager.Instance().addClueInfo(Clue.ClueInfo.CANNON_FAST_SOMEWHERE);
                    }
                }
            }
        }
    }

}
