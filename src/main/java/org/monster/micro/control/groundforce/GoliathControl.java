package org.monster.micro.control.factory;

import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import org.monster.common.UnitInfo;
import org.monster.common.util.CommandUtils;
import org.monster.common.util.InfoUtils;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.PositionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.micro.FleeOption;
import org.monster.micro.KitingOption;
import org.monster.micro.MicroDecision;
import org.monster.micro.MicroDecisionMakerPrebot1;
import org.monster.micro.constant.MicroConfig;
import org.monster.micro.control.Control;
import org.monster.board.StrategyBoard;

import java.util.Collection;

@Deprecated
public class GoliathControl extends Control {

    private int saveUnitLevel;

    public void setSaveUnitLevel(int saveUnitLevel) {
        this.saveUnitLevel = saveUnitLevel;
    }

    @Override
    public void control(Collection<Unit> unitList, Collection<UnitInfo> euiList) {
        if (TimeUtils.before(StrategyBoard.findRatFinishFrame)) {
            findRat(unitList);
            return;
        }

//		DecisionMaker decisionMaker = new DecisionMaker(new DefaultTargetCalculator());
        FleeOption fOption = new FleeOption(StrategyBoard.mainSquadCenter, true, MicroConfig.Angles.NARROW);
        KitingOption kOption = new KitingOption(fOption, KitingOption.CoolTimeAttack.COOLTIME_ALWAYS_IN_RANGE);

        int coverRadius = StrategyBoard.mainSquadCoverRadius * 3 / 5;
        if (PlayerUtils.enemyRace() == Race.Zerg) {
            coverRadius = StrategyBoard.mainSquadCoverRadius;
        }

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
                if (InfoUtils.euiListInBase().isEmpty() && InfoUtils.euiListInExpansion().isEmpty()) {
                    if (unit.getDistance(StrategyBoard.mainSquadCenter) > coverRadius) {
                        CommandUtils.move(unit, StrategyBoard.mainSquadCenter);
                        continue;
                    }
                }
            }

            MicroDecision decision = MicroDecisionMakerPrebot1.makeDecisionPrebot1(unit, euiList, null, saveUnitLevel);
            if (decision.type == MicroDecision.MicroDecisionType.FLEE_FROM_UNIT) {
                MicroUtils.flee(unit, decision.eui.getLastPosition(), fOption);

            } else if (decision.type == MicroDecision.MicroDecisionType.KITING_UNIT) {
                if (MicroUtils.isRemovableEnemySpiderMine(unit, decision.eui)) {
                    MicroUtils.holdControlToRemoveMine(unit, decision.eui.getLastPosition(), fOption);
                } else {
                    MicroUtils.kiting(unit, decision.eui, kOption);
                }

            } else if (decision.type == MicroDecision.MicroDecisionType.ATTACK_POSITION) {
                if (MicroUtils.arrivedToPosition(unit, StrategyBoard.mainPosition)) {
                    if (MicroUtils.timeToRandomMove(unit)) {
                        Position randomPosition = PositionUtils.randomPosition(unit.getPosition(), MicroConfig.RANDOM_MOVE_DISTANCE);
                        CommandUtils.attackMove(unit, randomPosition);
                    }
                } else {
                    if (unit.getGroundWeaponCooldown() > 15) { // UnitType.Terran_Goliath.groundWeapon().damageCooldown() = 22
                        unit.move(StrategyBoard.mainPosition);
                    } else {
                        CommandUtils.attackMove(unit, StrategyBoard.mainPosition);
                    }
                }
            }
        }
    }
}
