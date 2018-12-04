package org.monster.micro.control;

import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.*;
import org.monster.common.util.internal.IConditions;
import org.monster.bootstrap.Monster;
import org.monster.micro.FleeOption;
import org.monster.micro.constant.MicroConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ScvScoutControl extends Control {

    private Map<Integer, BaseLocation> scoutBaseMap = new HashMap<>();
    private Map<Integer, Integer> scoutVertexIndexMap = new HashMap<>();
    private boolean scoutFirstExpansionFlag = false;

    @Override
    public void control(Collection<Unit> unitList, Collection<UnitInfo> euiList) {
        for (Unit unit : unitList) {
            if (skipControl(unit)) {
                continue;
            }
            moveScoutUnit(unit, euiList);
        }
    }

    /// 정찰 유닛을 이동시킵니다
    // 상대방 MainBaseLocation 위치를 모르는 상황이면, StartLocation 들에 대해 아군의 MainBaseLocation에서 가까운 것부터 순서대로 정찰
    // 상대방 MainBaseLocation 위치를 아는 상황이면, 해당 BaseLocation 이 있는 Region의 가장자리를 따라 계속 이동함 (정찰 유닛이 죽을때까지)
    private void moveScoutUnit(Unit scoutScv, Collection<UnitInfo> euiList) {
        BaseLocation enemyBaseLocation = BaseUtils.enemyMainBase();

        //BaseLocation enemyFirstExpansionLocation = BaseUtils.enemyFirstExpansion();


        if (enemyBaseLocation == null) {
            BaseLocation scoutBaseLocation;
            if (PlayerUtils.enemyRace() == Race.Terran) {
                scoutBaseLocation = notExloredFarthestBaseLocation(scoutScv);
            } else {
                scoutBaseLocation = notExloredBaseLocationNearScoutScv(scoutScv);
            }
            scoutBaseMap.put(scoutScv.getID(), scoutBaseLocation);
            for (UnitInfo eui : euiList) {
                if (isCloseDangerousTarget(scoutScv, eui)) {
                    FleeOption fOption = new FleeOption(scoutBaseLocation.getPoint(), false, MicroConfig.Angles.WIDE);
                    MicroUtils.fleeScout(scoutScv, eui.getLastPosition(), fOption);
                    return;
                } else {
                    CommandUtils.move(scoutScv, scoutBaseLocation.getPosition());
                    return;
                }
            }

            CommandUtils.move(scoutScv, scoutBaseLocation.getPosition());
        } else {
            if (!StaticMapUtils.isExplored(enemyBaseLocation.getTilePosition())) {
                CommandUtils.move(scoutScv, enemyBaseLocation.getPosition());
            } else {
                Position currentScoutTargetPosition = getScoutFleePositionFromEnemyRegionVertices(scoutScv);
                if (TimeUtils.getFrame() % 2000 == 0) {
                    scoutFirstExpansionFlag = true;
                }
                if (scoutFirstExpansionFlag) {
                    if (canMoveFirstExpansion(scoutScv, enemyBaseLocation)) {
                        BaseLocation enemyFisrtExpansionPosition = getClosestFirstExpansionBase(enemyBaseLocation);
                        if (!PlayerUtils.isVisible(enemyFisrtExpansionPosition.getTilePosition())) {
                            CommandUtils.move(scoutScv, enemyFisrtExpansionPosition.getPosition());
                        } else {
                            CommandUtils.move(scoutScv, currentScoutTargetPosition);
                            scoutFirstExpansionFlag = false;
                        }
                    } else {
                        CommandUtils.move(scoutScv, currentScoutTargetPosition);
                    }
                } else {
                    CommandUtils.move(scoutScv, currentScoutTargetPosition);
                }
                // WorkerManager.Instance().setIdleWorker(scoutScv);
            }
        }
		/*if (enemyBaseLocation == null) {
			BaseLocation scoutBaseLocation; 
			if (StrategyBoard.enemyBaseExpected != null) {
				scoutBaseLocation = StrategyBoard.enemyBaseExpected;
			} else {
				scoutBaseLocation = notExloredBaseLocationNearScoutScv(scoutScv);
				scoutBaseMap.put(scoutScv.getID(), scoutBaseLocation);
			}
			if(scoutBaseLocation != null){
				BaseLocation enemyFisrtExpansionPosition = getClosestFirstExpansionBase(scoutBaseLocation);
				if (!Prebot.Broodwar.isExplored(enemyFisrtExpansionPosition.getTilePosition())) {
					CommandUtils.move(scoutScv, enemyFisrtExpansionPosition.getPosition());
				}else{
					CommandUtils.move(scoutScv, scoutBaseLocation.getPosition());
				}
			}else{
				CommandUtils.move(scoutScv, scoutBaseLocation.getPosition());
			}
			
		} else {
			BaseLocation enemyFisrtExpansionPosition = getClosestFirstExpansionBase(enemyBaseLocation);
			if (!Prebot.Broodwar.isExplored(enemyFisrtExpansionPosition.getTilePosition())) {
				CommandUtils.move(scoutScv, enemyFisrtExpansionPosition.getPosition());
			}else if (!Prebot.Broodwar.isExplored(enemyBaseLocation.getTilePosition())) {
				CommandUtils.move(scoutScv, enemyBaseLocation.getPosition());
			} else {
				Position currentScoutTargetPosition = getScoutFleePositionFromEnemyRegionVertices(scoutScv);
				CommandUtils.move(scoutScv, currentScoutTargetPosition);
				// WorkerManager.Instance().setIdleWorker(scoutScv);
			}
		}*/
    }

    private boolean canMoveFirstExpansion(Unit scoutScv, BaseLocation enemyBaseLocation) {
        Chokepoint nearestChoke = ChokePointUtils.enemyFirstChoke();
        if (nearestChoke.getCenter().getDistance(scoutScv) < 300 && !scoutScv.isUnderAttack()) {
            return true;
        }

        return false;
    }

    /**
     * 정찰되지 않은 SCV근처에 있는 base (정찰 scv가 2기 이상인 경우 겹치지 않도록 한다.)
     */
    private BaseLocation notExloredBaseLocationNearScoutScv(Unit scoutScv) {
        final List<BaseLocation> otherScvScoutBaseList = new ArrayList<>();
        for (Integer unitId : scoutBaseMap.keySet()) {
            if (unitId == scoutScv.getID()) {
                continue;
            }
            BaseLocation base = scoutBaseMap.get(unitId);
            if (base != null) {
                otherScvScoutBaseList.add(base);
            }
        }

        BaseLocation nearestBase = BaseLocationUtils.getClosestBaseToPosition(BWTA.getStartLocations(), scoutScv.getPosition());
        BaseLocation notExploredBase = BaseLocationUtils.getGroundClosestBaseToPosition(BWTA.getStartLocations(), nearestBase, new IConditions.BaseCondition() {
            @Override
            public boolean correspond(BaseLocation base) {
                return !StaticMapUtils.isExplored(base.getTilePosition()) && !otherScvScoutBaseList.contains(base);
            }
        });

        if (notExploredBase == null) {
            notExploredBase = BaseLocationUtils.getGroundClosestBaseToPosition(BWTA.getStartLocations(), nearestBase, new IConditions.BaseCondition() {
                @Override
                public boolean correspond(BaseLocation base) {
                    return !StaticMapUtils.isExplored(base.getTilePosition());
                }
            });
        }
        return notExploredBase;
    }

    /**
     * 본진에서 먼 곳부터 정찰 (대각선 정찰)
     */
    private BaseLocation notExloredFarthestBaseLocation(Unit scv) {
        BaseLocation notExploredFarthestBase = BaseLocationUtils.getGroundFarthestBaseToPosition(BWTA.getStartLocations(), BaseUtils.myMainBase(), new IConditions.BaseCondition() {
            @Override
            public boolean correspond(BaseLocation base) {
                return !StaticMapUtils.isExplored(base.getTilePosition());
            }
        });
        return notExploredFarthestBase;
    }

    private Position getScoutFleePositionFromEnemyRegionVertices(Unit scoutWorker) {
        BaseLocation enemyBase = BaseUtils.enemyMainBase();

        // calculate enemy region vertices if we haven't yet
        Vector<Position> regionVertices = ScoutUtils.getRegionVertices(enemyBase);
        if (regionVertices == null || regionVertices.isEmpty()) {
            //TODO 이거 다시 계산하는게 맞는건지 확인 필요. 사실상 처음에 전체 계산을 하는데?;
            ScoutUtils.calculateEnemyRegionVertices(enemyBase);
            regionVertices = ScoutUtils.getRegionVertices(enemyBase);
        }

        if (regionVertices.isEmpty()) {
            return BaseUtils.myMainBase().getPosition();
        }

        // if this is the first flee, we will not have a previous perimeter index
        Integer vertexIndex = scoutVertexIndexMap.get(scoutWorker.getID());
        if (vertexIndex == null) {
            // so return the closest position in the polygon
            vertexIndex = getClosestVertexIndex(scoutWorker.getPosition(), regionVertices);

            if (vertexIndex == CommonCode.INDEX_NOT_FOUND) {
                return BaseUtils.myMainBase().getPosition();
            } else {
                scoutVertexIndexMap.put(scoutWorker.getID(), vertexIndex);
                return regionVertices.get(vertexIndex);
            }
        }
        // if we are still fleeing from the previous frame, get the next location if we are close enough
        else {
            if (regionVertices.size() - 1 < vertexIndex) {
                // scout scv가 오래 살아남았을 때 regionVertices가 변경되어 ArrayIndexOutOfBoundsException 발생 방지
                System.out.println("reset vertexIndex to 0. regionVertices.size()=" + regionVertices.size() + ", vertexIndex=" + vertexIndex);
                vertexIndex = 0;

            } else {
                double distanceFromCurrentVertex = regionVertices.get(vertexIndex).getDistance(scoutWorker.getPosition());

                // keep going to the next vertex in the perimeter until we get to one we're far enough from to issue another move command
                int limit = 0;
                while (distanceFromCurrentVertex < 128 && limit < regionVertices.size()) {
                    limit++;
                    vertexIndex = (vertexIndex + 1) % regionVertices.size();
                    distanceFromCurrentVertex = regionVertices.get(vertexIndex).getDistance(scoutWorker.getPosition());
                }
            }
            scoutVertexIndexMap.put(scoutWorker.getID(), vertexIndex);
            return regionVertices.get(vertexIndex);
        }
    }

    public int getClosestVertexIndex(Position position, Vector<Position> regionVertices) {
        int closestIndex = CommonCode.INDEX_NOT_FOUND;
        double closestDistance = CommonCode.DOUBLE_MAX;

        for (int i = 0; i < regionVertices.size(); i++) {
            double dist = position.getDistance(regionVertices.get(i));
            if (dist < closestDistance) {
                closestDistance = dist;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    public BaseLocation getClosestFirstExpansionBase(BaseLocation scoutBaseLocation) {
        double tempDistance;
        double closestDistance = 1000000000;
        BaseLocation expansionBase = scoutBaseLocation;
        for (BaseLocation targetBaseLocation : BWTA.getBaseLocations()) {
            if (targetBaseLocation.getTilePosition().equals(scoutBaseLocation.getTilePosition())) continue;

            tempDistance = PositionUtils.getGroundDistance(scoutBaseLocation.getPosition(), targetBaseLocation.getPosition());
            if (tempDistance < closestDistance && tempDistance > 0) {
                closestDistance = tempDistance;
                expansionBase = targetBaseLocation;
            }
        }
        return expansionBase;

    }

    private boolean isCloseDangerousTarget(Unit myUnit, UnitInfo eui) {
        boolean enemyIsComplete = eui.isCompleted();
        Position enemyPosition = eui.getLastPosition();
        UnitType enemyUnitType = eui.getType();

        Unit enemyUnit = UnitUtils.enemyUnitInSight(eui);
        if (UnitUtils.isValidUnit(enemyUnit)) {
            enemyIsComplete = enemyUnit.isCompleted();
            enemyPosition = enemyUnit.getPosition();
            enemyUnitType = enemyUnit.getType();
        }

        // 접근하면 안되는 거리인지 있는지 판단
        int distanceToNearEnemy = myUnit.getDistance(enemyPosition);
        int enemyWeaponRange = 0;

        if (enemyUnitType == UnitType.Terran_Bunker) {
            enemyWeaponRange = PlayerUtils.enemyPlayer().weaponMaxRange(UnitType.Terran_Marine.groundWeapon()) + 96;
        } else {
            enemyWeaponRange = PlayerUtils.enemyPlayer().weaponMaxRange(enemyUnitType.groundWeapon());
        }
        return distanceToNearEnemy <= enemyWeaponRange + 64;
    }
}
