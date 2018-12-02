package org.monster.decisions.strategy;

import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;
import org.monster.board.StrategyBoard;
import org.monster.bootstrap.GameManager;
import org.monster.common.LagObserver;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.decisions.strategy.manage.ActionManager;
import org.monster.decisions.strategy.manage.AirForceManager;
import org.monster.decisions.strategy.manage.DefenseTowerTimer;
import org.monster.decisions.strategy.manage.InitialAction;
import org.monster.decisions.strategy.manage.PositionFinder;
import org.monster.decisions.strategy.manage.SpiderMineManger;
import org.monster.decisions.strategy.manage.StrategyAnalyseManager;
import org.monster.decisions.strategy.manage.TankPositionManager;
import org.monster.micro.constant.MicroConfig;

import java.util.List;

/// 상황을 판단하여, 정찰, 빌드, 공격, 방어 등을 수행하도록 총괄 지휘를 하는 class <br>
/// InformationManager 에 있는 정보들로부터 상황을 판단하고, <br>
/// BuildManager 의 buildQueue에 빌드 (건물 건설 / 유닛 훈련 / 테크 리서치 / 업그레이드) 명령을 입력합니다.<br>
/// 정찰, 빌드, 공격, 방어 등을 수행하는 코드가 들어가는 class
public class StrategyManager extends GameManager {

    private static StrategyManager instance = new StrategyManager();

    public static StrategyManager Instance() {
        return instance;
    }

    public void onStart() {
    }

    public void onEnd(boolean isWinner) {
    }

    public void update() {
        if (TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER1, 0), LagObserver.managerRotationSize())) {
            // 전략 파악
            InitialAction.Instance().update();
            StrategyAnalyseManager.Instance().update();
            ActionManager.Instance().update();
            DefenseTowerTimer.Instance().update();

            SpiderMineManger.Instance().update();
            //VultureTravelManager.Instance().update();
            TankPositionManager.Instance().update();
        }

        if (TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER1, 1), LagObserver.managerRotationSize())) {
            AirForceManager.Instance().update();
            PositionFinder.Instance().update();

            expansionOkay();
            changeMainSquadMode();
        }
    }

    /// 테스트용 임시 공격 타이밍
    private void changeMainSquadMode() {
        //TODO 일단 무조건 공격
//        if (AttackDecisionMaker.Instance().decision == Decision.NO_MERCY_ATTACK) {
        StrategyBoard.mainSquadMode = MicroConfig.MainSquadMode.NO_MERCY;

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

    private void expansionOkay() {
        boolean expansionOkay = false;
        BaseLocation myFirstExpansion = BaseUtils.myFirstExpansion();
        List<Unit> commandCenterList = UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Command_Center);
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