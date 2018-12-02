package org.monster.finder.position.dynamic;

import org.monster.board.Decision;
import org.monster.board.Location;
import org.monster.board.StrategyBoard;
import org.monster.finder.DefaultPositionFinder;
import org.monster.finder.LocationFinder;

//TODO prebot 읜 전진 배치 등의 사용된 로직으로 추정됨. 저그는 클래스명 부터 바꿔서 쌈싸먹는 위치 찾아야함. 참고용
public class ReadyToAttackPosFinder extends DefaultPositionFinder implements LocationFinder {

    public ReadyToAttackPosFinder() {
        super(Location.NeedToAdd);
    }

    @Override
    public boolean calculateLocation() {

        return StrategyBoard.decisions.get(Decision.NeedToAdd);
    }

    @Override
    public void decisionLogic() {

    }
}

//    public void updateReadyToAttackPosition() {
//        try {
//            Position myExpansionPosition = firstExpansionLocation.get(selfPlayer).getPosition();
//            Position enemyExpansionPosition = firstExpansionLocation.get(enemyPlayer).getPosition();
//            Position centerTilePosition = CommonCode.CENTER_POS;
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