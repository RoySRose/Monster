package org.monster.micro.squad;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.UnitUtils;
import org.monster.strategy.manage.VultureTravelManager;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.control.groundforce.VultureControl;
import org.monster.micro.targeting.TargetFilter;

import java.util.List;

public class GuerillaSquad extends Squad {
    private Position targetPosition;
    private VultureControl vultureControl = new VultureControl();

    public GuerillaSquad(Position position) {
        super(MicroConfig.SquadInfo.GUERILLA_, position);
        this.targetPosition = position;
        setUnitType(UnitType.Terran_Vulture);
    }

    public Position getTargetPosition() {
        return targetPosition;
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
        euiList = MicroUtils.filterTargetInfos(euiList, TargetFilter.AIR_UNIT | TargetFilter.LARVA_LURKER_EGG);
        vultureControl.setTargetPosition(targetPosition);
        vultureControl.controlIfUnitExist(unitList, euiList);
    }

    @Override
    public void findEnemies() {
        euiList.clear();
        UnitUtils.addEnemyUnitInfosInRadiusForGround(euiList, targetPosition, UnitType.Terran_Vulture.sightRange() + MicroConfig.COMMON_ADD_RADIUS);
        for (Unit unit : unitList) {
            if (!VultureTravelManager.Instance().guerillaIgnoreModeEnabled(getSquadName())) {
                UnitUtils.addEnemyUnitInfosInRadiusForGround(euiList, unit.getPosition(), unit.getType().sightRange() + MicroConfig.COMMON_ADD_RADIUS);
            }
        }
    }
}