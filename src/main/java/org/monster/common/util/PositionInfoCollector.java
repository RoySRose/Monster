package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import org.monster.common.constant.CommonCode;

import java.util.HashMap;
import java.util.Map;


/**
 * positionFinder로 넘어가야 되는 내용 같음
 */
@Deprecated
public class PositionInfoCollector implements InfoCollector {

    private static PositionInfoCollector instance = new PositionInfoCollector();
    protected Map<Player, Position> readyToAttackPosition = new HashMap();

    Game Broodwar;
    private Player selfPlayer;
    private Player enemyPlayer;
    private BaseInfoCollector baseInfoCollector;

    public static PositionInfoCollector Instance() {
        return instance;
    }

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
        selfPlayer = Broodwar.self();
        enemyPlayer = Broodwar.enemy();

        baseInfoCollector = BaseInfoCollector.Instance();
    }

    @Override
    public void update() {
        updateReadyToAttackPosition();
    }

    public void updateReadyToAttackPosition() {
        try {
            Position myExpansionPosition = baseInfoCollector.firstExpansionLocation.get(selfPlayer).getPosition();
            Position enemyExpansionPosition = baseInfoCollector.firstExpansionLocation.get(enemyPlayer).getPosition();
            Position centerTilePosition = CommonCode.CENTER_POS;

            int myX = myExpansionPosition.getX() + centerTilePosition.getX();
            int myY = myExpansionPosition.getY() + centerTilePosition.getY();

            int enemyX = enemyExpansionPosition.getX() + centerTilePosition.getX();
            int enemyY = enemyExpansionPosition.getY() + centerTilePosition.getY();

            Position myReadyToPosition = new Position(myX / 2, myY / 2);
            Position enemyReadyToPosition = new Position(enemyX / 2, enemyY / 2);

            readyToAttackPosition.put(selfPlayer, myReadyToPosition);
            readyToAttackPosition.put(enemyPlayer, enemyReadyToPosition);

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
