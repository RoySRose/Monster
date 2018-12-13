package org.monster.strategy.manage;

import bwapi.Position;
import bwapi.TechType;
import bwapi.Unit;
import org.monster.board.StrategyBoard;
import org.monster.common.UnitInfo;
import org.monster.common.util.UnitTypeUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.common.util.UpgradeUtils;
import org.monster.micro.constant.MicroConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AirForceTeam {

    private static final int MAX_TARGET_TRY_COUNT = 2; // 타깃포지션별 재공격 시도 횟수
    private static final int RETREAT_TIME = 2 * TimeUtils.SECOND; // 후퇴시간
    private static final int RETREAT_TIME_REPAIR = 4 * TimeUtils.SECOND; // 후퇴시간
    private static final int WRAITH_EFFECTIVE_FRAME_SIZE = 25 * TimeUtils.SECOND;

    public Unit leaderUnit;
    public Set<Unit> memberList = new HashSet<>();
    public int currentTargetIndex = 0;
    public int retreatFrame = 0;
    public int targetTryCount = 0;
    public boolean directionReservse;
    public Position leaderOrderPosition;
    public UnitInfo fleeEui;
    public int[] driveAngle;
    public boolean cloakingMode;
    public Unit repairCenter;

    // for achievement
    public int damagedEffectiveFrame = 0;
    public int killedEffectiveFrame = 0;

    public Map<Integer, Integer> preKillCounts = new HashMap<>();
    public Map<Integer, Integer> preHitPoints = new HashMap<>();
    public int[] damagedMemory = new int[WRAITH_EFFECTIVE_FRAME_SIZE];
    public int[] killedMemory = new int[WRAITH_EFFECTIVE_FRAME_SIZE];

    public AirForceTeam(Unit leaderUnit) {
        this.leaderUnit = leaderUnit; // leader wraith unit ID
        this.currentTargetIndex = 0; // 현재 타겟 index
        this.retreatFrame = 0;
        this.targetTryCount = 0; // 공격 재시도 count
        this.directionReservse = false; // 타깃포지션 변경 역방향 여부
        this.leaderOrderPosition = null;
        this.fleeEui = null;
        this.driveAngle = MicroConfig.Angles.AIR_FORCE_DRIVE_LEFT;
        this.cloakingMode = false;
        this.repairCenter = null;
    }

    public int achievement() {
        int reducedHitPoints = 0;
        int killCounts = 0;
        for (Unit wraith : memberList) {
            Integer preHitPoint = preHitPoints.get(wraith.getID());
            Integer preKillCount = preKillCounts.get(wraith.getID());
            if (preHitPoint == null) {
                continue;
            }
            if (wraith.getHitPoints() < preHitPoint) {
                int reduced = preHitPoint - wraith.getHitPoints();
                reducedHitPoints += reduced;
            }
            if (wraith.getKillCount() > preKillCount) {
                int killed = wraith.getKillCount() - preKillCount;
                killCounts += killed;
            }
        }

        int index = TimeUtils.getFrame() % WRAITH_EFFECTIVE_FRAME_SIZE;
        damagedEffectiveFrame = damagedEffectiveFrame + reducedHitPoints - damagedMemory[index];
        killedEffectiveFrame = killedEffectiveFrame + killCounts - killedMemory[index];

        damagedMemory[index] = reducedHitPoints;
        killedMemory[index] = killCounts;

        preHitPoints.clear();
        preKillCounts.clear();
        for (Unit wraith : memberList) {
            preHitPoints.put(wraith.getID(), wraith.getHitPoints());
            preKillCounts.put(wraith.getID(), wraith.getKillCount());
        }

        return killCounts * 100 - reducedHitPoints;
    }

    public Position getTargetPosition() {
        List<Position> targetPositions = AirForceManager.Instance().getTargetPositions();
        if (targetPositions.isEmpty()) {
            System.out.println("ERROR - airforceTeam targetposition is empty");
            return StrategyBoard.mainSquadCenter;
        }

        // AirForceManager의 targetPosition이 변경된 경우
        if (currentTargetIndex >= AirForceManager.airForceTargetPositionSize) {
            currentTargetIndex = 0;
        }
        Position targetPosition = targetPositions.get(currentTargetIndex);
        if (targetPosition == null) {
            System.out.println("ERROR - index=" + currentTargetIndex + " / " + targetPositions);
            return StrategyBoard.mainSquadCenter;
        }
        return targetPosition;
    }

    public void switchDriveAngle() {
        if (driveAngle == MicroConfig.Angles.AIR_FORCE_DRIVE_LEFT) {
            driveAngle = MicroConfig.Angles.AIR_FORCE_DRIVE_RIGHT;
        } else {
            driveAngle = MicroConfig.Angles.AIR_FORCE_DRIVE_LEFT;
        }
    }

    public boolean retreating() {
        if (AirForceManager.Instance().isAirForceDefenseMode()) {
            return false;
        }
        int retreatTime = repairCenter != null ? RETREAT_TIME_REPAIR : RETREAT_TIME;
        return TimeUtils.getFrame(retreatFrame) < retreatTime;
    }

    public void retreat(UnitInfo eui) {
        if (retreating()) {
            return;
        }
        this.fleeEui = eui;
        this.retreatFrame = TimeUtils.getFrame();
        this.targetTryCount++;
        if (this.targetTryCount == MAX_TARGET_TRY_COUNT) {
            changeTargetIndex();
        }
    }

    public boolean isInAirForceWeaponRange(Unit enemyUnit) {
        for (Unit member : memberList) {
            if (!member.isInWeaponRange(enemyUnit)) {
                return false;
            }
        }
        return true;
    }

    public void changeTargetIndex() {
        if (AirForceManager.Instance().isAirForceDefenseMode()) {
            return;
        }

        targetTryCount = 0;
        int foundCount = AirForceManager.airForceTargetPositionSize;
        while (foundCount > 0) {
            currentTargetIndex = directionReservse ? currentTargetIndex - 1 : currentTargetIndex + 1;

            if (currentTargetIndex < 0) {
                currentTargetIndex = 1;
                directionReservse = false;

            } else if (currentTargetIndex >= AirForceManager.airForceTargetPositionSize) {
                currentTargetIndex = AirForceManager.airForceTargetPositionSize - 2;
                directionReservse = true;
            }

            Position nextPosition = AirForceManager.Instance().getTargetPositions().get(currentTargetIndex);
            Set<UnitInfo> enemyDefTowerList = UnitUtils.getCompleteEnemyInfosInRadiusForAir(nextPosition, 20, UnitTypeUtils.enemyAirDefenseUnitType());
            foundCount = !enemyDefTowerList.isEmpty() ? foundCount - 1 : 0;
        }
    }

    public void cloak() {
        this.cloakingMode = true;
    }

    public void decloak() {
        this.cloakingMode = false;
    }

    public boolean cloakable() {
        if (!UpgradeUtils.selfISResearched(TechType.Cloaking_Field)) {
            return false;
        }
        if (cloakingMode) {
            return false;
        }

        for (Unit airunit : memberList) {
            if (airunit.getEnergy() < TechType.Cloaking_Field.energyCost() + 20) {
                return false;
            }
        }
        return true;
    }

    public boolean uncloakable() {
        return cloakingMode;
    }

    @Override
    public String toString() {
        return "Team" + leaderUnit.getID() + "(" + (cloakingMode ? "C" : "U") + ") size=" + memberList.size() + ", achieve=" + killedEffectiveFrame + "/" + damagedEffectiveFrame + (repairCenter != null ? " * repair" : "");
    }

}
