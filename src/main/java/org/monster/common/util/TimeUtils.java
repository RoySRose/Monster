package org.monster.common.util;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.constant.CommonCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtils {

    public static final int FRAME = 1;
    public static final int SECOND = 24;
    public static final int MINUTE = 24 * 60;
    private static final Logger logger = LoggerFactory.getLogger(TimeUtils.class);

    public static int getFrame() {
        return TimeInfoCollector.Instance().getframe();
    }

    /// 이전 프레임이면 true
    public static boolean beforeTime(int minutes, int seconds) {
        return before(timeToFrames(minutes, seconds));
    }

    public static boolean before(int frame) {
        return TimeInfoCollector.Instance().getframe() < frame;
    }

    /// 지나간 프레임이면 true
    public static boolean afterTime(int minutes, int seconds) {
        return after(timeToFrames(minutes, seconds));
    }

    public static boolean after(int frame) {
        return TimeInfoCollector.Instance().getframe() > frame;
    }

    /// 경과시간을 second로 리턴
    public static int elapsedSeconds() {
        return TimeInfoCollector.Instance().getframe() / SECOND;
    }

    /// 경과시간을 minute으로 리턴
    public static int elapsedMiniutes() {
        return TimeInfoCollector.Instance().getframe() / MINUTE;
    }

    /// 경과시간을 frame으로 리턴
    public static int getFrame(int startFrame) {
        return TimeInfoCollector.Instance().getframe() - startFrame;
    }

    /// 경과시간을 second으로 리턴
    public static int elapsedSeconds(int startFrame) {
        return (TimeInfoCollector.Instance().getframe() - startFrame) / SECOND;
    }

    /// 경과시간을 minute으로 리턴
    public static int elapsedMiniutes(int startFrame) {
        return (TimeInfoCollector.Instance().getframe() - startFrame) / MINUTE;
    }

    /// frame을 second로 단위변경하여 리턴
    public static int framesToSeconds(int frame) {
        return frame / SECOND;
    }

    /// frame을 second로 단위변경하여 리턴
    public static int framesToMinutes(int frame) {
        return frame / MINUTE;
    }

    /// 시작된 빌드시간
    public static int buildStartFrames(Unit building) {

        if (!building.getType().isBuilding()) {
            logger.error("UnitType of parameter should be building");
        }
        if (building.isCompleted()) {
            return CommonCode.UNKNOWN;
        }
        if (building.getType() == UnitType.Zerg_Lair
                || building.getType() == UnitType.Zerg_Hive
                || building.getType() == UnitType.Zerg_Sunken_Colony
                || building.getType() == UnitType.Zerg_Spore_Colony
                || building.getType() == UnitType.Zerg_Greater_Spire) {
            return CommonCode.UNKNOWN;
        }

        double completeRate = (double) building.getHitPoints() / building.getType().maxHitPoints();
        return getFrame() - (int) (building.getType().buildTime() * completeRate);
    }

    public static int timeToFrames(int minutes, int seconds) {
        return minutes * MINUTE + seconds * SECOND;
    }

    /// 실행할 frame되어야 하는 frame이면 true
    public static boolean executeRotation(int group, int rotationSize) {
        return (TimeInfoCollector.Instance().getframe() % rotationSize) == group;
    }

    /// unit이 실행할 rotation이면 true
    public static boolean isExecuteFrame(Unit unit, int rotationSize) {
        //TODO UnitBalancer비활성화로 주석처리
        //return !UnitBalancer.skipControl(unit);
        int unitGroup = unit.getID() % rotationSize;
        return executeRotation(unitGroup, rotationSize);
    }

    public static int baseToBaseFrame(UnitType unitType) {
        return TimeInfoCollector.Instance().baseToBaseFrame(unitType);
    }

}
