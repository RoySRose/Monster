package org.monster.strategy;

import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;
import org.monster.board.StrategyBoard;
import org.monster.bootstrap.GameManager;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.UnitUtils;
import org.monster.micro.constant.MicroConfig;
import org.monster.strategy.manage.EnemyStrategyAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StrategyManager extends GameManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static StrategyManager instance = new StrategyManager();
    public static StrategyManager Instance() {
        return instance;
    }

    public void onStart() {
    }

    public void onEnd(boolean isWinner) {
    }

    public void update() {

        EnemyStrategyAnalyzer.Instance().update();
        //TODO
        //My Strategy??
        changeMainSquadMode();

//        if (TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER1, 0), LagObserver.managerRotationSize())) {
//            DefenseTowerTimer.Instance().update();
//            SpiderMineManger.Instance().update();
//            TankPositionManager.Instance().update();
//        }
//        if (TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER1, 1), LagObserver.managerRotationSize())) {
//            AirForceManager.Instance().update();
//            PositionFinder.Instance().update();
//
//            expansionOkay();
//
//        }
    }

    /// 테스트용 임시 공격 타이밍
    private void changeMainSquadMode() {
        //TODO 일단 무조건 공격
//        if (AttackDecisionMaker.Instance().decision == Decision.NO_MERCY_ATTACK) {
        StrategyBoard.mainSquadMode = MicroConfig.MainSquadMode.ATTCK;

//        } else if (AttackDecisionMaker.Instance().decision == Decision.FULL_ATTACK) {
//            if (StrategyBoard.buildTimeMap.featureEnabled(EnemyStrategyOptions.BuildTimeMap.Feature.QUICK_ATTACK)) {
//                StrategyBoard.mainSquadMode = MicroConfig.MainSquadMode.SPEED_ATTCK;
//            } else {
//                StrategyBoard.mainSquadMode = MicroConfig.MainSquadMode.ATTCK;
//            }
//
//        } else {
//            StrategyBoard.mainSquadMode = MicroConfig.MainSquadMode.NORMAL;
//        }
    }

    @Deprecated
    private void expansionOkay() {
        boolean expansionOkay = false;
        BaseLocation myFirstExpansion = BaseUtils.myFirstExpansion();
        List<Unit> commandCenterList = UnitUtils.getCompletedUnitList(UnitType.Terran_Command_Center);
        for (Unit commandCenter : commandCenterList) {
            if (commandCenter.isLifted()) {
                continue;
            }

            if (commandCenter.getTilePosition().getX() == myFirstExpansion.getTilePosition().getX()
                    && commandCenter.getTilePosition().getY() == myFirstExpansion.getTilePosition().getY()) {
                expansionOkay = true;
                break;
            }
        }
        StrategyBoard.EXOK = expansionOkay;
    }

}