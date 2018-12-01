package org.monster.common;

import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.util.TimeUtils;

public class UnitInfo {

    private Unit unit;
    private int unitID;
    private UnitType type;
    private int lastHealth;
    private int lastShields;
    private Player player;
    private Position lastPosition;
    private boolean isFlying;
    private boolean completed;
    private int updateFrame;
    private int remainingBuildTime;

    public UnitInfo() {
        unitID = 0;
        type = UnitType.None;
        lastHealth = 0;
        player = null;
        unit = null;
        lastPosition = Position.None;
        completed = false;
        updateFrame = 0;
    }

    public UnitInfo(Unit unit) {
        this.unit = unit;
        this.setUpdateFrame(TimeUtils.getFrame());
        this.setPlayer(unit.getPlayer());
        this.setLastPosition(unit.getPosition());
        this.setLastHealth(unit.getHitPoints());
        this.setLastShields(unit.getShields());
        this.setUnitID(unit.getID());
        this.setUnitType(unit.getType());
        this.setCompleted(unit.isCompleted());
        this.setRemainingBuildTime(unit.getRemainingBuildTime());
    }

    public UnitType getType() {
        return type;
    }

    private void setUnitType(UnitType type) {
        this.type = type;
    }

    public int completeFrame() {
        return remainingBuildTime + updateFrame;
    }

    public boolean isCompleted() {
        return completed || (remainingBuildTime < TimeUtils.getFrame() - updateFrame);
    }

    private void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Position getLastPosition() {
        return lastPosition;
    }

    private void setLastPosition(Position lastPosition) {
        this.lastPosition = lastPosition;
    }

    public int getUnitID() {
        return unitID;
    }

    private void setUnitID(int unitID) {
        this.unitID = unitID;
    }

    public int getLastHealth() {
        return lastHealth;
    }

    private void setLastHealth(int lastHealth) {
        this.lastHealth = lastHealth;
    }

    public int getLastShields() {
        return lastShields;
    }

    private void setLastShields(int lastShields) {
        this.lastShields = lastShields;
    }

    public Player getPlayer() {
        return player;
    }

    private void setPlayer(Player player) {
        this.player = player;
    }

    public Unit getUnit() {
        return unit;
    }

    public boolean isFlying() {
        return isFlying;
    }

    private void setFlying(boolean isflying) {
        this.isFlying = isFlying;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        this.setUpdateFrame(TimeUtils.getFrame());
        this.setPlayer(unit.getPlayer());
        this.setLastPosition(unit.getPosition());
        this.setLastHealth(unit.getHitPoints());
        this.setLastShields(unit.getShields());
        this.setUnitID(unit.getID());
        this.setUnitType(unit.getType());
        this.setCompleted(unit.isCompleted());
        this.setRemainingBuildTime(unit.getRemainingBuildTime());
        this.setFlying(unit.isFlying());
    }

    public int getRemainingBuildTime() {
        return remainingBuildTime;
    }

    public void setRemainingBuildTime(int remainingBuildTime) {
        this.remainingBuildTime = remainingBuildTime;
    }

    public int getUpdateFrame() {
        return updateFrame;
    }

    public void setUpdateFrame(int updateFrame) {
        this.updateFrame = updateFrame;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnitInfo)) return false;

        UnitInfo that = (UnitInfo) o;

        if (this.getUnitID() != that.getUnitID()) return false;

        return true;
    }

    @Override
    public String toString() {
        return "UnitInfo [unitID=" + unitID + ", type=" + type + ", lastHealth=" + lastHealth + "." + lastShields + ", unit=" + unit + ", lastPosition=" + lastPosition + ", completed=" + completed + ", updateFrame=" + updateFrame + "]";
    }
}