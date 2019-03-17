package org.monster.micro.squad;

import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import org.monster.board.StrategyBoard;
import org.monster.common.UnitInfo;
import org.monster.common.constant.PlayerRange;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.CommandUtils;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.UnitUtils;
import org.monster.micro.FleeOption;
import org.monster.micro.KitingOption;
import org.monster.micro.MicroDecision;
import org.monster.micro.MicroDecisionMaker;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.targeting.DefaultTargetCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoutDefenseSquad extends Squad {

    private static final int REACT_RADIUS = 50;

    public ScoutDefenseSquad() {
        super(MicroConfig.SquadInfo.IDLE);
        setUnitType(UnitType.Terran_SCV);
    }

    @Override
    public boolean want(Unit unit) {
        return unit.getHitPoints() > 16;
    }

    @Override
    public List<Unit> recruit(List<Unit> assignableUnitList) {
        BaseLocation myBase = BaseUtils.myMainBase();
        Region myRegion = BWTA.getRegion(myBase.getPosition());
        List<UnitInfo> euiList = UnitUtils.euiListInMyRegion(myRegion);

        List<Unit> enemyUnitList = new ArrayList<>(); // 메인베이스 지역 내의 시야에 있는 적 리스트
        for (UnitInfo eui : euiList) {
            Unit enemyUnit = UnitUtils.enemyUnitInSight(eui);
            if (enemyUnit != null) {
                enemyUnitList.add(enemyUnit);
            }
        }
        if (unitList.size() >= enemyUnitList.size()) {
            return Collections.emptyList();
        }

        // 메인베이스와 가장 가까운 적 유닛이, 아군유닛의 REACT_RADIUS 내로 들어왔으면 유닛 할당
        Unit closeEnemyUnit = UnitUtils.getClosestUnitToPosition(enemyUnitList, myBase.getPosition());
        List<Unit> myUnitList = UnitUtils.getUnitsInRadius(PlayerRange.SELF, closeEnemyUnit.getPosition(), REACT_RADIUS);
        if (myUnitList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Unit> recruitList = new ArrayList<>();
        List<Unit> candidateUnitList = new ArrayList<>(assignableUnitList);
        while (recruitList.size() + unitList.size() < enemyUnitList.size()) {
            Unit defenseScv = UnitUtils.getClosestMineralWorkerToPosition(candidateUnitList, closeEnemyUnit.getPosition());
            if (defenseScv == null) {
                break;
            }
            recruitList.add(defenseScv);
            candidateUnitList.remove(defenseScv);
        }
        return recruitList;
    }

    @Override
    public void execute() {
        MicroDecisionMaker decisionMaker = new MicroDecisionMaker(new DefaultTargetCalculator());
        FleeOption fOption = new FleeOption(StrategyBoard.campPosition, false, MicroConfig.Angles.WIDE);
        KitingOption kOption = new KitingOption(fOption, KitingOption.CoolTimeAttack.KEEP_SAFE_DISTANCE);

        for (Unit unit : unitList) {
            MicroDecision decision = decisionMaker.makeDecision(unit, euiList);
            if (decision.type == MicroDecision.MicroDecisionType.FLEE_FROM_UNIT) {
                MicroUtils.flee(unit, decision.eui.getLastPosition(), fOption);

            } else if (decision.type == MicroDecision.MicroDecisionType.KITING_UNIT) {
                MicroUtils.kiting(unit, decision.eui, kOption);

            } else {
                CommandUtils.attackMove(unit, StrategyBoard.mainPosition);
            }
        }
    }

}
