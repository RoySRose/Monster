package org.monster.micro.squad;

import bwapi.Unit;
import org.monster.micro.constant.MicroConfig;

import java.util.List;

public class IdleSquad extends Squad {
    public IdleSquad() {
        super(MicroConfig.SquadInfo.IDLE);
    }

    @Override
    public boolean want(Unit unit) {
        return true;
    }

    @Override
    public List<Unit> recruit(List<Unit> assignableUnitList) {
        return assignableUnitList;
    }

    @Override
    public void execute() {
    }
}