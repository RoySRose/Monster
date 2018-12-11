package org.monster.finder;

import org.monster.bootstrap.GameManager;
import org.monster.finder.baselocation.NextExpansionFinder;
import org.monster.finder.chokepoint.BestChokePointToDefenceFinder;
import org.monster.finder.position.dynamic.EnemyReadyToAttackPosFinder;
import org.monster.finder.position.dynamic.MyReadyToAttackPosFinder;

import java.util.ArrayList;
import java.util.List;

public class LocationManager extends GameManager {

    private static LocationManager instance = new LocationManager();
    public static LocationManager Instance() {
        return instance;
    }

    List<LocationFinder> locationFinders = new ArrayList();

    public void onStart() {

        locationFinders.add(new EnemyReadyToAttackPosFinder());
        locationFinders.add(new MyReadyToAttackPosFinder());

        locationFinders.add(new NextExpansionFinder());
        locationFinders.add(new BestChokePointToDefenceFinder());

    }

    @Override
    public void update() {

        for (LocationFinder locationFinder : locationFinders) {
            if (locationFinder.isProceedCalc()) {
                locationFinder.process();
            }
        }

        //TODO debugger 의 결정 된 것은 여기서 모두 엎어치면 될듯.
    }

}
