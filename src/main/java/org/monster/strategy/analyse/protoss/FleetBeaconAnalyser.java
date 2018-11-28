package org.monster.strategy.analyse.protoss;

import bwapi.UnitType;
import org.monster.decisionMakers.decisionTypes.EnemyStrategy;
import org.monster.common.UnitInfo;

import org.monster.strategy.analyse.Clue;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.manage.ClueManager;

import java.util.List;

public class FleetBeaconAnalyser extends UnitAnalyser {

    public FleetBeaconAnalyser() {
        super(UnitType.Protoss_Fleet_Beacon);
    }

    @Override
    public void analyse() {
        fastFleetBeacon();
    }

    private void fastFleetBeacon() {
        List<UnitInfo> found = found();
        if (!found.isEmpty()) {
            int buildStartFrame = buildStartFrameDefaultJustBefore(found.get(0));
            int forgeDoubleForgeFrame = EnemyStrategy.PROTOSS_DOUBLE_CARRIER.buildTimeMap.frame(UnitType.Protoss_Fleet_Beacon, 90);
            if (buildStartFrame < forgeDoubleForgeFrame) {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FAST_FLEET_BEACON);
            } else {
                ClueManager.Instance().addClueInfo(Clue.ClueInfo.FLEET_BEACON_FOUND);
            }
        }
    }

}
