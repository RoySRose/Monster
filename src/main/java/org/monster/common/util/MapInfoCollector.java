package org.monster.common.util;

import bwapi.Game;
import org.monster.common.util.internal.MapSpecificInformation;

public class MapInfoCollector implements InfoCollector{

    private static MapInfoCollector instance = new MapInfoCollector();
    protected static MapInfoCollector Instance() {
        return instance;
    }
    Game Broodwar;

    /*Info*/
    protected MapSpecificInformation mapSpecificInformation;

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
    }

    @Override
    public void update() {

    }


}
