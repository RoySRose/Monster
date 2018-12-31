package org.monster.oldmicro;

import bwapi.Position;

public class PositionReserveInfo {
    public int unitId;
    public Position position;
    public int reservedFrame;

    public PositionReserveInfo(int unitId, Position position, int reservedFrame) {
        this.unitId = unitId;
        this.position = position;
        this.reservedFrame = reservedFrame;
    }

    @Override
    public String toString() {
        return "PositionReserveInfo [unitId=" + unitId + ", position=" + position + ", reservedFrame=" + reservedFrame + "]";
    }
}
