package org.monster.finder.chokepoint;

import bwta.Chokepoint;
import org.monster.finder.DefaultChokePointFinder;

//TODO 일단 옮겨놓긴 했는데 다음 초크 포인트 찾는거는 유틸성으로 보임. util 쪽으로 옮겨야할지도. 참고용
public class BestChokePointToDefenceFinder extends DefaultChokePointFinder {

    @Override
    public boolean isCalcLocation() {
        //return StrategyBoard.decisions.get(Decision.NeedToAdd);
        return true;
    }

    @Override
    public Chokepoint findLocation() {

        return null;
    }
}

//
//
//    public Chokepoint getNextChokepoint(Chokepoint currChoke, Player toPlayer) {
//        Chokepoint enemyFirstChoke = firstChokePoint.get(toPlayer);
//
//        int chokeToEnemyChoke = PositionUtils.getGroundDistance(currChoke.getCenter(), enemyFirstChoke.getCenter()); // 현재chokepoint
//        // ~
//        // 목적지chokepoint
//
//        Chokepoint nextChoke = null;
//        int closestChokeToNextChoke = 999999;
//        for (Chokepoint choke : BWTA.getChokepoints()) {
//            if (choke.equals(currChoke)) {
//                continue;
//            }
//            int chokeToNextChoke = PositionUtils.getGroundDistance(currChoke.getCenter(), choke.getCenter()); // 현재chokepoint
//            // ~
//            // 다음chokepoint
//            int nextChokeToEnemyChoke = PositionUtils.getGroundDistance(choke.getCenter(), enemyFirstChoke.getCenter()); // 다음chokepoint
//            // ~
//            // 목적지chokepoint
//            if (chokeToNextChoke + nextChokeToEnemyChoke < chokeToEnemyChoke + 10 // 최단거리 오차범위 10 * 32
//                    && chokeToNextChoke > 10 // 너무 가깝지 않아야 한다.
//                    && chokeToNextChoke < closestChokeToNextChoke) { // 가장 가까운 초크포인트를 선정
//                nextChoke = choke;
//                closestChokeToNextChoke = chokeToNextChoke;
//            }
//        }
//        return nextChoke;
//    }