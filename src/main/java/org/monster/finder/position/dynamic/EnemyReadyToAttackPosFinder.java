package org.monster.finder.position.dynamic;

import bwapi.Position;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.BaseUtils;
import org.monster.finder.DefaultPositionFinder;

//TODO prebot 읜 전진 배치 등의 사용된 로직으로 추정됨. 저그는 클래스명 부터 바꿔서 쌈싸먹는 위치 찾아야함. 참고용
public class EnemyReadyToAttackPosFinder extends DefaultPositionFinder {

    private static boolean isCalculate;
    @Override
    public boolean isCalcLocation() {
        return isCalculate;
    }

    @Override
    public Position findLocation() {
        Position enemyExpansionPosition = BaseUtils.enemyFirstExpansion().getPosition();
        Position centerTilePosition = CommonCode.CENTER_POS;

        int enemyX = enemyExpansionPosition.getX() + centerTilePosition.getX();
        int enemyY = enemyExpansionPosition.getY() + centerTilePosition.getY();

        return new Position(enemyX / 2, enemyY / 2);
    }

    @Override
    public final void afterCalc(){
        isCalculate = false;
    }
    public static void setCalculate(boolean calculate) {
        isCalculate = calculate;
    }
}