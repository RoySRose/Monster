package org.monster.micro;

import bwapi.Unit;
import org.monster.common.UnitInfo;

public class MicroDecision {

    public MicroDecisionType type;
    public Unit myUnit;
    public UnitInfo eui;

    private MicroDecision(MicroDecisionType type, Unit myUnit) {
        this.type = type;
        this.myUnit = myUnit;
    }

    private MicroDecision(MicroDecisionType type, Unit myUnit, UnitInfo eui) {
        this.type = type;
        this.myUnit = myUnit;
        this.eui = eui;
    }

    public static MicroDecision kitingUnit(Unit myUnit, UnitInfo eui) {
        MicroDecision decision = new MicroDecision(MicroDecision.MicroDecisionType.KITING_UNIT, myUnit, eui);
//		UXManager.Instance().addDecisionListForUx(myUnit, decision);
        return decision;
    }

    public static MicroDecision attackUnit(Unit myUnit, UnitInfo eui) {
        MicroDecision decision = new MicroDecision(MicroDecision.MicroDecisionType.ATTACK_UNIT, myUnit, eui);
//		UXManager.Instance().addDecisionListForUx(myUnit, decision);
        return decision;
    }

    public static MicroDecision fleeFromUnit(Unit myUnit, UnitInfo eui) {
        MicroDecision decision = new MicroDecision(MicroDecision.MicroDecisionType.FLEE_FROM_UNIT, myUnit, eui);
//		UXManager.Instance().addDecisionListForUx(myUnit, decision);
        return decision;
    }

    public static MicroDecision attackPosition(Unit myUnit) {
        MicroDecision decision = new MicroDecision(MicroDecision.MicroDecisionType.ATTACK_POSITION, myUnit);
//		UXManager.Instance().addDecisionListForUx(myUnit, decision);
        return decision;
    }

    public static MicroDecision fleeFromPosition(Unit myUnit) {
        MicroDecision decision = new MicroDecision(MicroDecision.MicroDecisionType.FLEE_FROM_POSITION, myUnit);
//		UXManager.Instance().addDecisionListForUx(myUnit, decision);
        return decision;
    }

    public static MicroDecision hold(Unit myUnit) {
        MicroDecision decision = new MicroDecision(MicroDecision.MicroDecisionType.HOLD, myUnit);
//		UXManager.Instance().addDecisionListForUx(myUnit, decision);
        return decision;
    }

    public static MicroDecision stop(Unit myUnit) {
        MicroDecision decision = new MicroDecision(MicroDecision.MicroDecisionType.STOP, myUnit);
//		UXManager.Instance().addDecisionListForUx(myUnit, decision);
        return decision;
    }

    public static MicroDecision change(Unit myUnit) {
        MicroDecision decision = new MicroDecision(MicroDecision.MicroDecisionType.CHANGE_MODE, myUnit);
//		UXManager.Instance().addDecisionListForUx(myUnit, decision);
        return decision;
    }

    public static MicroDecision move(Unit myUnit) {
        MicroDecision decision = new MicroDecision(MicroDecision.MicroDecisionType.RIGHT_CLICK, myUnit);
//		UXManager.Instance().addDecisionListForUx(myUnit, decision);
        return decision;
    }

    public static MicroDecision unite(Unit myUnit) {
        MicroDecision decision = new MicroDecision(MicroDecision.MicroDecisionType.UNITE, myUnit);
//		UXManager.Instance().addDecisionListForUx(myUnit, decision);
        return decision;
    }

    public static MicroDecision doNothing(Unit myUnit) {
        MicroDecision decision = new MicroDecision(MicroDecision.MicroDecisionType.DO_NOTHING, myUnit);
//		UXManager.Instance().addDecisionListForUx(myUnit, decision);
        return decision;
    }

    @Override
    public String toString() {
        if (eui == null) {
            return type.SHORTNAME;
        } else {
            return type.SHORTNAME + " -> " + eui.getType();
        }
    }

    public enum MicroDecisionType {
        KITING_UNIT("KU"),
        ATTACK_UNIT("AU"),
        ATTACK_POSITION("AP"),
        FLEE_FROM_UNIT("FU"),
        FLEE_FROM_POSITION("FP"),
        HOLD("H"),
        STOP("S"),
        CHANGE_MODE("C"),
        RIGHT_CLICK("R"),
        UNITE("U"),
        DO_NOTHING("N");

        public String SHORTNAME;

        private MicroDecisionType(String shortName) {
            this.SHORTNAME = shortName;
        }
    }
}