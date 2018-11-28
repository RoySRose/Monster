package org.monster.finder.position.dynamic;

import bwapi.Position;
import org.monster.finder.position.PositionFinder;

import java.util.List;

//TODO prebot 읜 전진 배치 등의 사용된 로직으로 추정됨. 저그는 클래스명 부터 바꿔서 쌈싸먹는 위치 찾아야함. 참고용
public class ReadyToAttackPosFinder implements PositionFinder {

    @Override
    public List<Position> getPosition() {
        return null;
    }
}

//    public void updateReadyToAttackPosition() {
//        try {
//            Position myExpansionPosition = firstExpansionLocation.get(selfPlayer).getPosition();
//            Position enemyExpansionPosition = firstExpansionLocation.get(enemyPlayer).getPosition();
//            Position centerTilePosition = TilePositionUtils.getCenterTilePosition().toPosition();
//
//            int myX = myExpansionPosition.getX() + centerTilePosition.getX();
//            int myY = myExpansionPosition.getY() + centerTilePosition.getY();
//
//            int enemyX = enemyExpansionPosition.getX() + centerTilePosition.getX();
//            int enemyY = enemyExpansionPosition.getY() + centerTilePosition.getY();
//
//            Position myReadyToPosition = new Position(myX / 2, myY / 2);
//            Position enemyReadyToPosition = new Position(enemyX / 2, enemyY / 2);
//
//            readyToAttackPosition.put(selfPlayer, myReadyToPosition);
//            readyToAttackPosition.put(enemyPlayer, enemyReadyToPosition);
//
////			Chokepoint secChokeSelf = secondChokePoint.get(selfPlayer);
////			Chokepoint secChokeEnemy = secondChokePoint.get(enemyPlayer);
////			Position selfReadyToPos = getNextChokepoint(secChokeSelf, enemyPlayer).getCenter();
////			Position enemyReadyToPos = getNextChokepoint(secChokeEnemy, selfPlayer).getCenter();
////
////			readyToAttackPosition.put(selfPlayer, selfReadyToPos);
////			readyToAttackPosition.put(enemyPlayer, enemyReadyToPos);
//
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
//    }