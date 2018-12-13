package org.monster.strategy;

import bwta.BaseLocation;
import org.monster.common.util.DrawingUtils;

public class TravelSite {
    public BaseLocation baseLocation;
    public int visitFrame;
    public int visitAssignedFrame;
    public int guerillaExamFrame;
    public int index;

    public TravelSite(BaseLocation baseLocation, int visitFrame, int visitAssignedFrame, int guerillaExamFrame, int index) {
        this.baseLocation = baseLocation;
        this.visitFrame = visitFrame;
        this.visitAssignedFrame = visitAssignedFrame;
        this.guerillaExamFrame = guerillaExamFrame;
        this.index = index;
    }

    @Override
    public String toString() {
        return baseLocation.getPosition() + "\nvisitFrame=" + DrawingUtils.framesToTimeString(visitFrame)
                + "\nvisitAssignedFrame=" + DrawingUtils.framesToTimeString(visitAssignedFrame)
                + "\nguerillaExamFrame=" + DrawingUtils.framesToTimeString(guerillaExamFrame)
                + "\nindex=" + index;
    }
}