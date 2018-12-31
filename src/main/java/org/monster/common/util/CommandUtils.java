package org.monster.common.util;

import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitCommand;
import bwapi.UnitCommandType;

public class CommandUtils {

    public static void attackUnit(Unit unit, Unit target) {
        if (validCommand(unit, target, UnitCommandType.Attack_Unit, true, false)) {
            unit.attack(target);
        }
    }

    public static void attackMove(Unit unit, Position targetPosition) {
        if (validCommand(unit, targetPosition, UnitCommandType.Attack_Move, true, false)) {
            unit.attack(targetPosition);
        }
    }

    public static void move(Unit unit, Position targetPosition) {
        if (validCommand(unit, targetPosition, UnitCommandType.Move, true, true)) {
            unit.move(targetPosition);
        }
    }

    public static void land(Unit unit, TilePosition targetPosition) {
        if (validCommand(unit, targetPosition, UnitCommandType.Land, true, false)) {
            unit.land(targetPosition);
        }
    }

    public static void lift(Unit unit) {
        if (unit.canLift() && validCommand(unit, UnitCommandType.Lift)) {
            unit.lift();
        }
    }

    public static void rightClick(Unit unit, Unit target) {
        if (validCommand(unit, target, UnitCommandType.Right_Click_Unit, false, true)) {
            unit.rightClick(target);
        }
    }

    public static void rightClick(Unit unit, Position position) {
        if (validCommand(unit, position, UnitCommandType.Right_Click_Position, false, true)) {
            unit.rightClick(position);
        }
    }

    public static void fastestRightClick(Unit unit, Unit target) {
        if (!UnitUtils.isValidUnit(unit)) {
            return;
        }
        if (!UnitUtils.isValidUnit(target)) {
            return;
        }
        if (unit.getLastCommandFrame() >= TimeUtils.getFrame()) {
            return;
        }
        UnitCommand currentCommand = unit.getLastCommand();
        if (currentCommand.getUnitCommandType() == UnitCommandType.Right_Click_Unit) {
            if (currentCommand.getTarget().getID() == target.getID()) {
                return;
            }
        }
        unit.rightClick(target);
    }

    public static void fastestRightClick(Unit unit, Position position) {
        if (!UnitUtils.isValidUnit(unit)) {
            return;
        }
        if (!PositionUtils.isValidPosition(position)) {
            return;
        }
        if (unit.getLastCommandFrame() >= TimeUtils.getFrame()) {
            return;
        }
        UnitCommand currentCommand = unit.getLastCommand();
        if (currentCommand.getUnitCommandType() == UnitCommandType.Right_Click_Position) {
            if (currentCommand.getTargetPosition().equals(position)) {
                return;
            }
        }
        unit.rightClick(position);
    }

    public static void repair(Unit unit, Unit target) {
        if (validCommand(unit, target, UnitCommandType.Repair, true, false)) {
            unit.repair(target);
        }
    }

    public static void useTechPosition(Unit unit, TechType tech, Position position) {
        if (validCommand(unit, position, UnitCommandType.Use_Tech_Position, true, false)) {
            unit.useTech(tech, position);
        }
    }

    public static void useTechTarget(Unit unit, TechType tech, Unit target) {
        if (validCommand(unit, target, UnitCommandType.Use_Tech_Unit, true, false)) {
            unit.useTech(tech, target);
        }
    }

    public static void load(Unit bunkerOrDropShip, Unit target) {
        if (validCommand(bunkerOrDropShip, target, UnitCommandType.Load, true, false)) {
            bunkerOrDropShip.load(target);
        }
    }

    public static void unload(Unit bunkerOrDropShip, Unit target) {
        if (validCommand(bunkerOrDropShip, target, UnitCommandType.Unload, true, false)) {
            bunkerOrDropShip.unload(target);
        }
    }

    public static void siege(Unit tank) {
        if (tank.canSiege() && validCommand(tank, UnitCommandType.Siege)) {
//			Integer changeTime = siegeModeChangeMap.get(tank.getID());
//			if (changeTime == null || TimeUtils.getFrame(changeTime) > 4 * TimeUtils.SECOND) {
//				siegeModeChangeMap.put(tank.getID(), TimeUtils.getFrame());
            tank.siege();
//			}
        }
    }

    public static void unsiege(Unit tank) {
        if (tank.canUnsiege() && validCommand(tank, UnitCommandType.Unsiege)) {
//			Integer changeTime = siegeModeChangeMap.get(tank.getID());
//			if (changeTime == null || TimeUtils.getFrame(changeTime) > 4 * TimeUtils.SECOND) {
//				siegeModeChangeMap.put(tank.getID(), TimeUtils.getFrame());
            tank.unsiege();
//			}
        }
    }

    public static void holdPosition(Unit unit) {
        if (validCommand(unit, UnitCommandType.Hold_Position)) {
            unit.holdPosition();
        }
    }

    private static boolean validCommand(Unit unit, UnitCommandType commandType) {
        if (!UnitUtils.isValidUnit(unit)) {
            return false;
        }
        if (!TimeUtils.after(unit.getLastCommandFrame())) {
            return false;
        }
        UnitCommand currentCommand = unit.getLastCommand();
        if (currentCommand.getUnitCommandType() == commandType) {
            return false;
        }
        return true;
    }

    private static boolean validCommand(Unit unit, Unit target, UnitCommandType commandType, boolean notIssueOnAttackFrame, boolean checkCommandByPosition) {
        if (!UnitUtils.isValidUnit(unit)) {
            return false;
        }
        if (!UnitUtils.isValidUnit(target)) {
            return false;
        }
        if (!TimeUtils.after(unit.getLastCommandFrame())) {
            return false;
        }
        if (notIssueOnAttackFrame && unit.isAttackFrame()) {
            return false;
        }
        UnitCommand currentCommand = unit.getLastCommand();
        if (currentCommand.getUnitCommandType() == commandType) {
            if (checkCommandByPosition) {
                if (currentCommand.getTargetPosition().equals(target.getPosition())) {
                    return false;
                }
            } else if (currentCommand.getTarget().getID() == target.getID()) {
                return false;
            }
        }
        return true;
    }

    private static boolean validCommand(Unit unit, Position position, UnitCommandType commandType, boolean notIssueOnAttackFrame, boolean issueIfNotMoving) {
        if (!UnitUtils.isValidUnit(unit)) {
            return false;
        }
        if (!PositionUtils.isValidPosition(position)) {
            return false;
        }
        if (unit.getLastCommandFrame() >= TimeUtils.getFrame()) {
            return false;
        }
        if (notIssueOnAttackFrame && unit.isAttackFrame()) {
            return false;
        }

        if (issueIfNotMoving && !unit.isMoving()) {
            return true;
        }
        UnitCommand currentCommand = unit.getLastCommand();
        if (currentCommand.getUnitCommandType() == commandType) {
            if (currentCommand.getTargetPosition().equals(position)) {
                return false;
            }
        }

        return true;
    }

    private static boolean validCommand(Unit unit, TilePosition tilePosition, UnitCommandType commandType, boolean notIssueOnAttackFrame, boolean issueIfNotMoving) {


        if (!UnitUtils.isValidUnit(unit)) {
            return false;
        }
        if (!TilePositionUtils.isValidTilePosition(tilePosition)) {
            return false;
        }
        if (unit.getLastCommandFrame() >= TimeUtils.getFrame()) {
            return false;
        }
        if (notIssueOnAttackFrame && unit.isAttackFrame()) {
            return false;
        }

        if (issueIfNotMoving && !unit.isMoving()) {
            return true;
        }
        UnitCommand lastCommand = unit.getLastCommand();
        if (lastCommand.getUnitCommandType() == commandType) {
            if (unit.getLastCommandFrame() + 1 >= TimeUtils.getFrame()) {
                return false;
            }
        }

        return true;
    }
}