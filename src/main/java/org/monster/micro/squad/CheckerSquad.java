package org.monster.micro.squad;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.board.StrategyBoard;
import org.monster.common.UnitInfo;
import org.monster.common.util.UnitUtils;
import org.monster.strategy.manage.VultureTravelManager;
import org.monster.micro.CombatManager;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.control.groundforce.VultureControl;
import org.monster.micro.targeting.TargetFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CheckerSquad extends Squad {
    private VultureControl vultureControl = new VultureControl();

    public CheckerSquad() {
        super(MicroConfig.SquadInfo.CHECKER);
        setUnitType(UnitType.Terran_Vulture);
    }

    @Override
    public boolean want(Unit unit) {
        Squad squad = CombatManager.Instance().squadData.getSquad(unit);
        if (squad instanceof GuerillaSquad) {
            return false;
        }
        return !VultureTravelManager.Instance().checkerRetired(unit.getID());
    }

    /// checker squad는 매 frame 1회 용감한 checker부대원을 모집한다.
    /// 스파이더마인을 많이 보유한 벌처의 우선순위가 높다.
    @Override
    public List<Unit> recruit(List<Unit> assignableUnitList) {
        int openingCount = StrategyBoard.checkerMaxNumber - unitList.size();
        if (openingCount <= 0) {
            return Collections.emptyList();
        }

        // 보유마인 개수별로 벌처 분류
        Map<Integer, List<Unit>> vultureIdByMineCount = new HashMap<>();
        for (Unit vulture : assignableUnitList) {
            List<Unit> vultureList = vultureIdByMineCount.get(new Integer(vulture.getSpiderMineCount()));
            if (vultureList == null) {
                vultureList = new ArrayList<Unit>();
            }
            vultureList.add(vulture);
            vultureIdByMineCount.put(new Integer(vulture.getSpiderMineCount()), vultureList);
        }

        // 마인이 많은 순서대로 벌처 할당
        List<Unit> recruitList = new ArrayList<>();
        for (int mineCount = 3; mineCount >= 0; mineCount--) {
            List<Unit> vultureList = vultureIdByMineCount.getOrDefault(new Integer(mineCount), new ArrayList<Unit>());
            for (Unit vulture : vultureList) {
                recruitList.add(vulture);
                openingCount--;
                if (openingCount == 0) {
                    return recruitList;
                }
            }
        }
        return recruitList;
    }

    @Override
    public void execute() {
        for (Unit unit : unitList) {
            Set<UnitInfo> euiList = UnitUtils.getEnemyUnitInfosInRadius(TargetFilter.UNFIGHTABLE | TargetFilter.AIR_UNIT | TargetFilter.LARVA_LURKER_EGG | TargetFilter.INVISIBLE
                    , unit.getPosition(), unit.getType().sightRange(), true, false);

            vultureControl.controlIfUnitExist(new HashSet<>(Arrays.asList(unit)), euiList);
        }
    }

    @Override
    public void findEnemies() {
        // do nothing
    }
}