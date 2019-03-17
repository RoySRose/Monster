package org.monster.worker;

import bwapi.Position;
import bwapi.Unit;

public class Minerals {
    public int unitId = 0;
    public Unit mineralUnit = null;

    public Unit mineralTrick = null;//mineral which allows for mineral trick to speed up SCV
    public Position posTrick = null;//Position to allow path finding trick
    public String facing = ""; //0 = scv enters left, 1 = scv enters up
    public Boolean possibleTrick = false;
}
