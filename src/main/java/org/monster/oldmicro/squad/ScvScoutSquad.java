package org.monster.oldmicro.squad;

import bwapi.Unit;
import bwapi.UnitType;
import bwta.Chokepoint;
import org.monster.board.StrategyBoard;
import org.monster.common.util.ChokeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.oldmicro.constant.MicroConfig;
import org.monster.oldmicro.control.ScvScoutControl;

import java.util.ArrayList;
import java.util.List;

public class ScvScoutSquad extends Squad {

    private ScvScoutControl scvScoutControl = new ScvScoutControl();

    public ScvScoutSquad() {
        super(MicroConfig.SquadInfo.SCV_SCOUT);
        setUnitType(UnitType.Terran_SCV);
    }

    @Override
    public boolean want(Unit unit) {
        return true;
    }

    @Override
    public List<Unit> recruit(List<Unit> assignableUnitList) {
        List<Unit> recruitList = new ArrayList<>();
        if (StrategyBoard.assignScoutScv) {
            Chokepoint firstChoke = ChokeUtils.myFirstChoke();
            Unit scvNearFirstChoke = UnitUtils.getClosestMineralWorkerToPosition(assignableUnitList, firstChoke.getCenter());
            if (scvNearFirstChoke != null) {
                recruitList.add(scvNearFirstChoke);
                StrategyBoard.assignScoutScv = false;
            }
        }
        return recruitList;
    }

    @Override
    public void execute() {
        scvScoutControl.controlIfUnitExist(unitList, euiList);
    }
}