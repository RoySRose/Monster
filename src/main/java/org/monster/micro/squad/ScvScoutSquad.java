package org.monster.micro.squad;

import bwapi.Unit;
import bwapi.UnitType;
import bwta.Chokepoint;
import org.monster.common.util.InfoUtils;
import org.monster.common.util.UnitUtils;
import org.monster.board.StrategyBoard;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.control.ScvScoutControl;

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
            Chokepoint firstChoke = InfoUtils.myFirstChoke();
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