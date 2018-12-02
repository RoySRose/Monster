package org.monster.micro.squad;

import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.Region;
import org.monster.board.StrategyBoard;
import org.monster.common.UnitInfo;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.UnitUtils;
import org.monster.micro.SquadData;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.control.GundamControl;
import org.monster.micro.control.MarineControl;
import org.monster.worker.WorkerData;
import org.monster.worker.WorkerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EarlyDefenseSquad extends Squad {

    private static final int REACT_RADIUS = 50;
    private GundamControl gundamControl = new GundamControl();
    private MarineControl marineControl = new MarineControl();

    public EarlyDefenseSquad() {
        super(MicroConfig.SquadInfo.EARLY_DEFENSE);
        setUnitType(UnitType.Terran_Marine, UnitType.Terran_SCV);
    }

    @Override
    public boolean want(Unit unit) {
        if (unit.getType() == UnitType.Terran_SCV) {
            if (euiList.isEmpty()) {
                return false;
            } else {
                return unit.getHitPoints() > 16;
            }
        }
        return true;
    }

    @Override
    public List<Unit> recruit(List<Unit> assignableUnitList) {
        List<Unit> marineList = new ArrayList<>();
        List<Unit> scvList = new ArrayList<>();

        for (Unit unit : assignableUnitList) {
            if (unit.getType() == UnitType.Terran_Marine && unit.isCompleted()) {
                marineList.add(unit);
            } else if (unit.getType() == UnitType.Terran_SCV
                    && WorkerManager.Instance().getWorkerData().getWorkerJob(unit) != WorkerData.WorkerJob.Scout) {
                scvList.add(unit);
            }
        }
        if (!euiList.isEmpty()) {
            return defenseForScvAndMarine(marineList, scvList, euiList);
        } else {
            return marineList;
        }
    }

    private List<Unit> defenseForScvAndMarine(List<Unit> marineList, List<Unit> scvList, Collection<UnitInfo> euiList) {
        /**
         * 포톤캐넌, 파일런, 벙커 등 전략 대응. 반응거리(REACT_RADIUS) 더 길게 처리. 건물 당 SCV 몇기를 동원할지 등 처리
         */

        List<Unit> enemyInSightList = new ArrayList<>();
        for (UnitInfo eui : euiList) {
            Unit enemy = UnitUtils.unitInSight(eui);
            if (enemy != null) {
                enemyInSightList.add(enemy);
            }
        }
        // 메인베이스와 가장 가까운 적 유닛이, 아군유닛의 REACT_RADIUS 내로 들어왔으면 유닛 할당
        Unit closeEnemyUnit = UnitUtils.getClosestUnitToPosition(enemyInSightList, BaseUtils.myMainBase().getPosition());
        if (closeEnemyUnit == null) {
            return marineList;
        }

        double scvCnt = 0;
        for (Unit unit : unitList) {
            if (unit.getType() == UnitType.Terran_SCV) {
                scvCnt++;
            }
        }

        // 얼마나 SCV 동원이 필요한지 체크
        double scvCountForDefense = scvCountForDefense(enemyInSightList, marineList);
        WorkerData workerData = WorkerManager.Instance().getWorkerData();
        scvCountForDefense = (scvCountForDefense > workerData.getNumWorkers() - 3) ? workerData.getNumWorkers() - 3 : scvCountForDefense;
        /*유닛이 줄었을때 필요일꾼 만큼만 스쿼드 유지 나머지는 idle*/
        while (marineList.size() + scvCnt > scvCountForDefense) {
            SquadData squadData = new SquadData();
            Unit defenseScv = UnitUtils.getFarthestCombatWorkerToPosition(unitList, closeEnemyUnit.getPosition());
            if (defenseScv == null) {
                break;
            }

            squadData.exclude(defenseScv);
            unitList.remove(defenseScv);
            scvCnt--;
        }

        //List<Unit> myUnitList = UnitUtils.getUnitsInRadius(CommonCode.PlayerRange.SELF, closeEnemyUnit.getPosition(), REACT_RADIUS);
        //if (myUnitList.isEmpty() && !marineList.isEmpty()) {
		/*if(!marineList.isEmpty()){
			return marineList;
		}*/


        List<Unit> recruitScvList = new ArrayList<>();
        List<Unit> recruitMarineList = marineList;
        while (unitList.size() + recruitScvList.size() < scvCountForDefense) {
            if (marineList.size() < scvCountForDefense) {
                Unit defenseScv = UnitUtils.getClosestMineralWorkerToPosition(scvList, closeEnemyUnit.getPosition());
                if (defenseScv == null) {
                    break;
                }
                recruitMarineList.add(defenseScv);
                scvList.remove(defenseScv);
            }
            recruitScvList = recruitMarineList;
        }
        return recruitScvList.isEmpty() ? recruitMarineList : recruitScvList;


    }

    private double scvCountForDefense(List<Unit> enemyInSightList, List<Unit> marineList) {
        Region campRegion = BWTA.getRegion(StrategyBoard.campPosition);
        Unit bunker = marineControl.getCompleteBunker(campRegion);

        double scvCountForDefense = 0.0;
        for (Unit enemy : enemyInSightList) {
            if (UnitUtils.isValidUnit(enemy)) {
                if (!enemy.getType().isWorker()
                        && !enemy.getType().isBuilding()
                        && BaseUtils.myMainBase().getPosition().getDistance(enemy) > 300) {
                    continue;
                }

                if (enemy.getType().isWorker()) {
                    Region unitRegion = BWTA.getRegion(enemy.getPosition());
                    Region baseRegion = BWTA.getRegion(BaseUtils.myMainBase().getPosition());
                    if (unitRegion != baseRegion) {
                        continue;
                    }

                }

                if (bunker != null && bunker.getLoadedUnits().size() > 0 || marineList.size() >= 2) {
                    if (enemy.getType() == UnitType.Protoss_Probe || enemy.getType() == UnitType.Zerg_Drone) {
                        scvCountForDefense += 1.0;
                    } else if (enemy.getType() == UnitType.Terran_SCV) {
                        scvCountForDefense += 1.0;
                    } else if (enemy.getType() == UnitType.Protoss_Zealot) {
                        scvCountForDefense += 2;
                    } else if (enemy.getType() == UnitType.Zerg_Zergling) {
                        scvCountForDefense += 1;
                    } else if (enemy.getType() == UnitType.Terran_Marine) {
                        scvCountForDefense += 1;
                    } else if (enemy.getType().isBuilding()) {
                        scvCountForDefense += 3;
                    }
                } else {
                    if (enemy.getType() == UnitType.Protoss_Probe || enemy.getType() == UnitType.Zerg_Drone) {
                        scvCountForDefense += 1.0;
                    } else if (enemy.getType() == UnitType.Terran_SCV) {
                        scvCountForDefense += 1.0;
                    } else if (enemy.getType() == UnitType.Protoss_Zealot) {
                        scvCountForDefense += 4;
                    } else if (enemy.getType() == UnitType.Zerg_Zergling) {
                        scvCountForDefense += 2;
                    } else if (enemy.getType() == UnitType.Terran_Marine) {
                        scvCountForDefense += 2;
                    } else if (enemy.getType().isBuilding()) {
                        scvCountForDefense += 3;
                    }
                }
            }
        }
        return scvCountForDefense;
    }

    @Override
    public void findEnemies() {
        euiList.clear();

        List<UnitInfo> enemyUnitsInRegion = UnitUtils.euiListInMyRegion(BaseUtils.myMainBase().getRegion());
        if (enemyUnitsInRegion.size() >= 1) {
            for (UnitInfo enemy : enemyUnitsInRegion) {
                euiList.add(enemy);
            }
        }
        for (Unit unit : unitList) {
            UnitUtils.addEnemyUnitInfosInRadiusForEarlyDefense(euiList, unit.getPosition(), unit.getType().sightRange() + MicroConfig.COMMON_ADD_RADIUS);
        }
    }

    @Override
    public void execute() {
        Map<UnitType, List<Unit>> unitListMap = UnitUtils.makeUnitListMap(unitList);
        List<Unit> scvList = unitListMap.getOrDefault(UnitType.Terran_SCV, new ArrayList<Unit>());
        List<Unit> marineList = unitListMap.getOrDefault(UnitType.Terran_Marine, new ArrayList<Unit>());

        marineControl.controlIfUnitExist(marineList, euiList);
        gundamControl.controlIfUnitExist(scvList, euiList);
    }

}
