package org.monster.oldmicro;

import bwapi.Position;

public class PositionSiegeInfo {
    public int unitId;
    public Position position;
    public int postionCnt;

    public PositionSiegeInfo(int unitId, Position position, int postionCnt) {
        this.unitId = unitId;
        this.position = position;
        this.postionCnt = postionCnt;
    }

    @Override
    public String toString() {
        return "PositionReserveInfo [unitId=" + unitId + ", position=" + position + ", postionCnt=" + postionCnt + "]";
    }
}
