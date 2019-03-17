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
    protected static ChokeInfoCollector Instance() {
        return instance;
    }

    private Game Broodwar;
    private Player selfPlayer;
    private Player enemyPlayer;

    protected Map<Player, Chokepoint> firstChokePoint = new HashMap();
    protected Map<Player, Chokepoint> secondChokePoint = new HashMap();

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
        this.selfPlayer = Broodwar.self();
        this.enemyPlayer = Broodwar.enemy();
    }

    //TODO 뒷마당이 존재하는 맵에서는?
    protected void updateClosestChokePoints(Player player, BaseLocation sourceBase) {

        Chokepoint firstChoke = BWTA.getNearestChokepoint(sourceBase.getTilePosition());
        firstChokePoint.put(player, firstChoke);

        Chokepoint secondChoke = findSecondChokePoint(sourceBase, firstChoke);
        secondChokePoint.put(player, secondChoke);
    }

    private Chokepoint findSecondChokePoint(BaseLocation sourceBase, Chokepoint firstChoke) {

        double tempDistance;
        double closestDistance = CommonCode.DOUBLE_MAX;
        Chokepoint closestChokePoint = null;

        for (Chokepoint chokepoint : BWTA.getChokepoints()) {
            if (chokepoint.getCenter().equals(firstChoke.getCenter()))
                continue;

            tempDistance = PositionUtils.getGroundDistance(sourceBase.getPosition(),
                    chokepoint.getPoint()) * 1.1;
            tempDistance += PositionUtils.getGroundDistance(CommonCode.CENTER_POS, chokepoint.getPoint());

            if (tempDistance > 0 && tempDistance < closestDistance) {
                closestDistance = tempDistance;
                closestChokePoint = chokepoint;
            }
        }
        return closestChokePoint;
    }

    @Override
    public void update() {
        /**
         * need?
         */
    }
}
