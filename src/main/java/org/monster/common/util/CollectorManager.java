package org.monster.common.util;

import bwapi.Game;
import org.monster.main.GameManager;

import java.util.ArrayList;
import java.util.List;

public class CollectorManager extends GameManager {

    private static CollectorManager instance = new CollectorManager();

    public static CollectorManager Instance() {
        return instance;
    }

    List<InfoCollector> infoCollectors = new ArrayList();

    public void onStart(Game Broodwar) {

        //ORDER SENSITIVE!!!!!!
        infoCollectors.add(PlayerInfoCollector.Instance());
        infoCollectors.add(StaticMapInfoCollector.Instance());
        infoCollectors.add(UnitCache.getCurrentCache());
        infoCollectors.add(BaseInfoCollector.Instance());
        infoCollectors.add(TimeInfoCollector.Instance());
        infoCollectors.add(MapInfoCollector.Instance());
        infoCollectors.add(UpgradeInfoCollector.Instance());


//        infoCollectors.add(MapInfoCollector.Instance());
//        infoCollectors.add(MapInfoCollector.Instance());

        for(InfoCollector infoCollector : infoCollectors){
            infoCollector.onStart(Broodwar);
        }
    }

    @Override
    public void update() {
        for(InfoCollector infoCollector : infoCollectors){
            infoCollector.update();
        }
    }

}
