package org.monster.finder;

import org.monster.finder.baselocation.NextExpansionFinder;
import org.monster.finder.chokepoint.BestChokePointToDefenceFinder;
import org.monster.bootstrap.GameManager;

import java.util.ArrayList;
import java.util.List;

public class LocationManager extends GameManager {

    private static LocationManager instance = new LocationManager();
    List<LocationFinder> locationFinders = new ArrayList();

    public static LocationManager Instance() {
        return instance;
    }

    public void onStart() {

        //ORDER SENSITIVE!!!!!!
        locationFinders.add(new NextExpansionFinder());
        locationFinders.add(new BestChokePointToDefenceFinder());

    }

    @Override
    public void update() {

        /**
         *  이렇게 매 프레임 계산되어야 하는것이 맞는지 따져볼 필요가 있다.
         */
        for (LocationFinder locationFinder : locationFinders) {
            if (locationFinder.calculateLocation()) {
                locationFinder.decisionLogic();
                locationFinder.pushToStrategyBoard();
            }
        }
    }

}
