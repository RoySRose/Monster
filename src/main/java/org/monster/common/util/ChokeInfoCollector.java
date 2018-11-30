package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;
import org.monster.common.constant.CommonCode;

import java.util.HashMap;
import java.util.Map;

public class ChokeInfoCollector implements InfoCollector {

    private static ChokeInfoCollector instance = new ChokeInfoCollector();

    public static ChokeInfoCollector Instance() {
        return instance;
    }

    Game Broodwar;

    protected Map<Player, Chokepoint> firstChokePoint = new HashMap();
    protected Map<Player, Chokepoint> secondChokePoint = new HashMap();

    private Player selfPlayer;
    private Player enemyPlayer;

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
        selfPlayer = Broodwar.self();
        enemyPlayer = Broodwar.enemy();
    }

    @Override
    public void update() {

    }

    protected void updateClosestChokePoints(Player player, BaseLocation sourceBase) {

        firstChokePoint.put(player, BWTA.getNearestChokepoint(sourceBase.getTilePosition()));

        Chokepoint secondChoke = findClosestChokePoint(sourceBase, firstChokePoint.get(player));
        secondChokePoint.put(player, secondChoke);
    }

    protected Chokepoint findClosestChokePoint(BaseLocation sourceBase, Chokepoint skipChokepoint){

        double tempDistance;
        double closestDistance = CommonCode.DOUBLE_MAX;
        Chokepoint closestChokepoint = null;

        for (Chokepoint chokepoint : BWTA.getChokepoints()) {
            if (chokepoint.getCenter().equals(skipChokepoint.getCenter()))
                continue;

            tempDistance = PositionUtils.getGroundDistance(sourceBase.getPosition(),
                    chokepoint.getPoint()) * 1.1;
            tempDistance += PositionUtils.getGroundDistance(CommonCode.Center, chokepoint.getPoint());

            if (tempDistance < closestDistance && tempDistance > 0) {
                closestDistance = tempDistance;
                closestChokepoint = chokepoint;
            }
        }
        return closestChokepoint;
    }
}
