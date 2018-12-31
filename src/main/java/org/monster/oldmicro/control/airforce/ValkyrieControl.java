package org.monster.oldmicro.control.airforce;

import bwapi.Position;
import bwapi.Unit;
import org.monster.board.StrategyBoard;
import org.monster.common.UnitInfo;
import org.monster.common.util.CommandUtils;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.PositionUtils;
import org.monster.oldmicro.FleeOption;
import org.monster.oldmicro.KitingOption;
import org.monster.oldmicro.MicroDecision;
import org.monster.oldmicro.MicroDecisionMaker;
import org.monster.oldmicro.constant.MicroConfig;
import org.monster.oldmicro.control.Control;
import org.monster.oldmicro.targeting.DefaultTargetCalculator;

import java.util.Collection;

@Deprecated
public class ValkyrieControl extends Control {

    @Override
    public void control(Collection<Unit> unitList, Collection<UnitInfo> euiList) {

        MicroDecisionMaker decisionMaker = new MicroDecisionMaker(new DefaultTargetCalculator());

        FleeOption fOption = new FleeOption(StrategyBoard.mainSquadCenter, true, MicroConfig.Angles.NARROW);
        KitingOption kOption = new KitingOption(fOption, KitingOption.CoolTimeAttack.COOLTIME_ALWAYS);

        for (Unit unit : unitList) {
            if (skipControl(unit)) {
                continue;
            }
            if (MicroUtils.isBeingHealed(unit)) {
                CommandUtils.holdPosition(unit);
                continue;
            }
            if (!StrategyBoard.mainSquadMode.isAttackMode) {
                if (dangerousOutOfMyRegion(unit)) {
                    CommandUtils.move(unit, StrategyBoard.mainPosition);
                    continue;
                }
            } else {
                if (StrategyBoard.mainSquadCenter.getDistance(unit) > StrategyBoard.mainSquadCoverRadius) {
                    CommandUtils.move(unit, StrategyBoard.mainSquadCenter);
                    continue;
                }
            }

            MicroDecision decision = decisionMaker.makeDecision(unit, euiList);
            if (decision.type == MicroDecision.MicroDecisionType.FLEE_FROM_UNIT) {
                MicroUtils.flee(unit, decision.eui.getLastPosition(), fOption);

            } else if (decision.type == MicroDecision.MicroDecisionType.KITING_UNIT) {
                MicroUtils.kiting(unit, decision.eui, kOption);

            } else if (decision.type == MicroDecision.MicroDecisionType.ATTACK_POSITION) {
                if (MicroUtils.arrivedToPosition(unit, StrategyBoard.mainSquadCenter)) {
                    if (MicroUtils.timeToRandomMove(unit)) {
                        Position randomPosition = PositionUtils.randomPosition(unit.getPosition(), MicroConfig.RANDOM_MOVE_DISTANCE);
                        CommandUtils.attackMove(unit, randomPosition);
                    }
                } else {
                    CommandUtils.attackMove(unit, StrategyBoard.mainSquadCenter);
                }
            }
        }
    }
}
