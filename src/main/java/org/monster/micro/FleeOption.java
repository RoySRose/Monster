package org.monster.micro;

import bwapi.Position;
import org.monster.micro.constant.MicroConfig;

public class FleeOption {

    public Position goalPosition;
    public boolean united;
    public int[] angles;

    public FleeOption(Position goalPosition, boolean united, int[] angles) {
        this.goalPosition = goalPosition;
        this.united = united;
        this.angles = angles;
    }

    public static FleeOption defaultOption() {
        return new FleeOption(Position.None, true, MicroConfig.Angles.WIDE);
    }
}
