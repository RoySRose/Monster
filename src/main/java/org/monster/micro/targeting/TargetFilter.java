package org.monster.micro.targeting;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.UnitUtils;

//TODO NEED RESET
public class TargetFilter {

    public static final int NO_FILTER = 0;
    public static final int GROUND_UNIT = 1;
    public static final int AIR_UNIT = 2;
    public static final int LARVA_LURKER_EGG = 4;
    public static final int INCOMPLETE = 8;
    public static final int UNFIGHTABLE = 16;
    public static final int BUILDING = 32;
    public static final int SPIDER_MINE = 64;
    public static final int WORKER = 128;
    public static final int INVISIBLE = 256;

    public static boolean excludeByFilter(UnitInfo eui, int targetFilter) {
        Unit target = UnitUtils.unitInSight(eui);

        if (target != null) {
            if (!UnitUtils.isValidUnit(target)) {
                return true;
            }
            if (target.getType() == UnitType.Zerg_Egg) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.GROUND_UNIT) && !target.isFlying()) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.AIR_UNIT) && target.isFlying()) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.LARVA_LURKER_EGG) && target.getType().armor() >= 10) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.INCOMPLETE) && (!target.isCompleted() && target.getHitPoints() <= target.getType().maxHitPoints() * 0.8)) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.UNFIGHTABLE) && !MicroUtils.combatEnemyType(target.getType())) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.BUILDING) && target.getType().isBuilding()) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.SPIDER_MINE) && target.getType() == UnitType.Terran_Vulture_Spider_Mine) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.WORKER) && target.getType().isWorker()) {
                return true;
            }
            return !target.isVisible() || target.isStasised();

        } else {
            if (eui.getType() == UnitType.Zerg_Egg) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.GROUND_UNIT) && !eui.getType().isFlyer()) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.AIR_UNIT) && eui.getType().isFlyer()) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.LARVA_LURKER_EGG) && eui.getType().armor() >= 10) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.INCOMPLETE) && (!eui.isCompleted() && eui.getLastHealth() <= eui.getType().maxHitPoints() * 0.8)) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.UNFIGHTABLE) && !MicroUtils.combatEnemyType(eui.getType())) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.BUILDING) && eui.getType().isBuilding()) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.SPIDER_MINE) && eui.getType() == UnitType.Terran_Vulture_Spider_Mine) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.WORKER) && eui.getType().isWorker()) {
                return true;
            }
            if (TargetFilter.exclude(targetFilter, TargetFilter.INVISIBLE)) {
                return true;
            }
            return false;
        }
    }

    private static boolean exclude(int filter, int type) {
        return (filter & type) == type;
    }

}
