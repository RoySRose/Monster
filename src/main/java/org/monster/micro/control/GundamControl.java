package org.monster.micro.control;

import bwapi.Unit;
import org.monster.common.UnitInfo;
import org.monster.common.util.CommandUtils;
import org.monster.common.util.UnitUtils;
import org.monster.worker.WorkerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GundamControl extends Control {
    @Override
    public void control(Collection<Unit> unitList, Collection<UnitInfo> euiList) {

        List<UnitInfo> enemyWorkers = new ArrayList<>();
        //List<UnitInfo> enemyBuildings = new ArrayList<>();
        List<UnitInfo> enemyUnit = new ArrayList<>();
        for (UnitInfo eui : euiList) {
            if (eui.getType().isWorker()) {
                enemyWorkers.add(eui);
            } /*else if (eui.getType().isBuilding()) {
				enemyBuildings.add(eui);
			} */ else {
                enemyUnit.add(eui);
            }
        }

        Map<Integer, UnitInfo> scvTargetMap = new HashMap<>();
        Set<Integer> assignedEnemeyIds = new HashSet<>();

        for (UnitInfo euiWorker : enemyWorkers) {
            Unit combatScv = UnitUtils.getClosestUnitToPositionNotInSet(unitList, euiWorker.getLastPosition(), assignedEnemeyIds);
            if (combatScv == null) {
                continue;
            } else if (scvTargetMap.get(combatScv.getID()) != null) {
                continue;
            }
            //assignedEnemeyIds.add(euiWorker.getUnitID());
            assignedEnemeyIds.add(combatScv.getID());
            scvTargetMap.put(combatScv.getID(), euiWorker);
            CommandUtils.attackUnit(combatScv, euiWorker.getUnit());
        }

        for (Unit worker : unitList) {
            if (skipControl(worker)) {
                continue;
            }
            UnitInfo eui = scvTargetMap.get(worker.getID());

            if (eui != null) {
                CommandUtils.attackUnit(worker, eui.getUnit());
            } else {
                UnitInfo enemyUnitInfo = getClosestEnemyUnitFromWorker(enemyUnit, worker);
                //Unit unitInSight = UnitUtils.unitInSight(closeBuildingInfo);
                if (enemyUnitInfo != null) {
                    WorkerManager.Instance().setCombatWorker(worker);
                    CommandUtils.attackUnit(worker, enemyUnitInfo.getUnit());
                }
            }
        }

    }

    /// 해당 일꾼 유닛으로부터 가장 가까운 적군 유닛을 리턴합니다
    private UnitInfo getClosestEnemyUnitFromWorker(List<UnitInfo> euiBuildingList, Unit worker) {
        if (worker == null)
            return null;

        UnitInfo closestEui = null;
        double closestDist = 10000;
        for (UnitInfo euiBuilding : euiBuildingList) {
            double dist = worker.getDistance(euiBuilding.getLastPosition());

            if (dist < 700 && (closestEui == null || (dist < closestDist))) {
                closestEui = euiBuilding;
                closestDist = dist;
            }
        }

        return closestEui;
    }
}
