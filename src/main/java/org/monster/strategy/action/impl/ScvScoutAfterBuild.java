package org.monster.strategy.action.impl;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.main.Monster;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.InformationManager;
import org.monster.common.util.UnitUtils;
import org.monster.board.StrategyBoard;
import org.monster.strategy.action.Action;

/**
 * ScoutManager 기본소스에서 가져와서 수정함 unitType의 빌드완료시간이 remainingSeconds만큼 남았으면 정찰시작 (remainingSeconds이 0이면 완료시 정찰)
 */
public class ScvScoutAfterBuild extends Action {

    private final UnitType buildingType;
    private final int remainingFrames;

    private int marginFrames = 5;
    private boolean assigned = false;

    public ScvScoutAfterBuild(UnitType buildingType, int remainingFrames) {
        this.buildingType = buildingType;
        this.remainingFrames = remainingFrames;
    }

    @Override
    public boolean exitCondition() {
        if (assigned) {
            if (marginFrames > 0) {
                marginFrames--;
                return false;
            } else {
                if (InformationManager.Instance().getMainBaseLocation(Monster.Broodwar.enemy()) == null) {
                    StrategyBoard.assignScoutScv = true;
                }
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public void action() {
        if (assigned) {
            return;
        }
        for (Unit building : UnitUtils.getUnitList(CommonCode.UnitFindRange.ALL, buildingType)) {
            if (building.getType() == buildingType && building.getRemainingBuildTime() <= remainingFrames) {
                assigned = true;
                break;
            }
        }
    }
}