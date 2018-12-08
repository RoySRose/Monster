package org.monster.common.util;

import bwapi.Game;
import org.monster.bootstrap.GameManager;

import java.util.ArrayList;
import java.util.List;

public class InfoCollectorManager extends GameManager {

    private static InfoCollectorManager instance = new InfoCollectorManager();
    List<InfoCollector> infoCollectors = new ArrayList();

    public static InfoCollectorManager Instance() {
        return instance;
    }

    public void onStart(Game Broodwar) {

        /**
         * ORDER SENSITIVE!!!!!!
         */
        infoCollectors.add(PlayerInfoCollector.Instance());
        infoCollectors.add(MapInfoCollector.Instance());
        infoCollectors.add(UnitCache.getCurrentCache());
        infoCollectors.add(BaseInfoCollector.Instance());
        infoCollectors.add(ChokeInfoCollector.Instance());
        infoCollectors.add(RegionInfoCollector.Instance());
        infoCollectors.add(PositionInfoCollector.Instance());
        //infoCollectors.add(TilePositionInfoCollector.Instance());
        infoCollectors.add(UnitInRegionInfoCollector.Instance());
        infoCollectors.add(TimeInfoCollector.Instance());
        infoCollectors.add(UpgradeInfoCollector.Instance());
        infoCollectors.add(ScoutInfoCollector.Instance());

        infoCollectors.add(DrawDebugger.Instance());

        for (InfoCollector infoCollector : infoCollectors) {
            infoCollector.onStart(Broodwar);
        }
    }

    @Override
    public void update() {
        for (InfoCollector infoCollector : infoCollectors) {
            infoCollector.update();
        }
    }

}
