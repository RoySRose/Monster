package org.monster.oldmicro;

import bwapi.Unit;
import bwapi.UnitType;

import org.monster.oldmicro.squad.Squad;
import org.monster.worker.WorkerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SquadData {

    private Map<String, Squad> squadMap = new HashMap<>(); // 이름별 squad 저장

    public Map<String, Squad> getSquadMap() {
        return squadMap;
    }

    public Squad getSquad(String squadName) {
        return squadMap.get(squadName);
    }

    public List<Squad> getSquadList(String squadName) {
        List<Squad> squadList = new ArrayList<>();
        for (Squad squad : squadMap.values()) {
            if (squad.getSquadName().startsWith(squadName)) {
                squadList.add(squad);
            }
        }
        return squadList;
    }

    public void addSquad(Squad squad) {
        squadMap.put(squad.getSquadName(), squad);
    }

    public Squad removeSquad(String squadName) {
        Squad squad = squadMap.get(squadName);
        for (Unit unit : squad.unitList) {
            if (unit.getType() == UnitType.Terran_SCV) {
                WorkerManager.Instance().setIdleWorker(unit); // SCV를 WorkerManager에서 관리
            }
        }
        squad.unitList.clear();
        return squadMap.remove(squadName);
    }


    public Squad getSquad(Unit unit) {
        for (String sqaudName : squadMap.keySet()) {
            Squad squad = squadMap.get(sqaudName);
            if (squad.hasUnit(unit)) {
                return squad;
            }
        }
        return null;
    }

    public void assign(Unit unit, Squad squad) {
        Squad previousSquad = getSquad(unit);
        if (previousSquad != null) {
            previousSquad.unitList.remove(unit);
        }
        squad.unitList.add(unit);

        if (unit.getType() == UnitType.Terran_SCV) {
            WorkerManager.Instance().setCombatWorker(unit); // SCV를 CombatManager에서 관리
        }
    }

    public void exclude(Unit unit) {
        Squad previousSquad = getSquad(unit);
        if (previousSquad != null) {
            previousSquad.unitList.remove(unit);
        }
        if (unit.getType() == UnitType.Terran_SCV) {
            WorkerManager.Instance().setIdleWorker(unit); // SCV를 WorkerManager에서 관리
        }
    }

}
