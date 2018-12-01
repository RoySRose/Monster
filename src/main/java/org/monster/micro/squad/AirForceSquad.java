package org.monster.micro.squad;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.LagObserver;
import org.monster.common.UnitInfo;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.control.airforce.AirForceControl;
import org.monster.micro.targeting.TargetFilter;
import org.monster.board.StrategyBoard;
import org.monster.decisions.strategy.manage.AirForceManager;
import org.monster.decisions.strategy.manage.AirForceTeam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AirForceSquad extends Squad {

    private AirForceControl airForceControl = new AirForceControl();

    public AirForceSquad() {
        super(MicroConfig.SquadInfo.AIR_FORCE);
        setUnitType(UnitType.Terran_Wraith);
    }

    @Override
    public boolean want(Unit unit) {
        return true;
    }

    @Override
    public List<Unit> recruit(List<Unit> assignableUnitList) {
        return assignableUnitList;
    }

    @Override
    public void execute() {
        if (!AirForceManager.Instance().airForceManagerInitialized()) {
            return;
        }

        AirForceManager.Instance().updateAirForceTeam(unitList);

        List<Unit> leaderAirunits = new ArrayList<>();
        for (Unit airunit : unitList) {
            if (AirForceManager.Instance().isLeader(airunit.getID())) {
                leaderAirunits.add(airunit);
            }
        }
        int airForceGroupSize = Math.max(Math.max(unitList.size(), 5), LagObserver.groupsize());

        // 리더유닛이 먼저 실행되면 member 유닛들은 그 후 같은 명령을 실행한다.
        for (Unit leaderAirunit : leaderAirunits) {
            if (!TimeUtils.executeUnitRotation(leaderAirunit, airForceGroupSize)) {
                continue;
            }

            AirForceTeam airForceTeam = AirForceManager.Instance().airForTeamOfUnit(leaderAirunit.getID());
            Set<UnitInfo> euis = findEnemiesForTeam(airForceTeam.memberList);
            airForceControl.controlIfUnitExist(airForceTeam.memberList, euis);
        }
    }

    public Set<UnitInfo> findEnemiesForTeam(Collection<Unit> unitList) {
        Set<UnitInfo> euis = new HashSet<>();

        if (AirForceManager.Instance().isAirForceDefenseMode()) {
            Set<UnitInfo> mainSquadEnemies = getMainSquadEnemies();
            if (mainSquadEnemies != null) {
                euis = mainSquadEnemies;
            }

            if (StrategyBoard.mainSquadMode.isAttackMode) {
                for (Unit unit : unitList) {
                    if (AirForceManager.Instance().isLeader(unit.getID())) {
                        UnitUtils.addEnemyUnitInfosInRadius(TargetFilter.LARVA_LURKER_EGG, euis, unit.getPosition(), unit.getType().sightRange() + MicroConfig.COMMON_ADD_RADIUS, false, true);
                    }
                }
            }

        } else {
            for (Unit unit : unitList) {
                if (AirForceManager.Instance().isLeader(unit.getID())) {
                    UnitUtils.addEnemyUnitInfosInRadius(TargetFilter.LARVA_LURKER_EGG, euis, unit.getPosition(), unit.getType().sightRange() + MicroConfig.COMMON_ADD_RADIUS, false, true);
                }
            }
        }
        return euis;
    }

    @Override
    public void findEnemies() {
        // nothing
    }
}
