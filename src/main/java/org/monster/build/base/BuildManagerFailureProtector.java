package org.monster.build.base;

import org.monster.common.MetaType;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.TimeUtils;
import org.monster.bootstrap.Monster;

import java.util.HashMap;
import java.util.Map;

public class BuildManagerFailureProtector {

    private static final int INITIAL_TIME = 10 * TimeUtils.SECOND;

    private Map<MetaType, BuildManagerFailureProtector.FailInfo> failCountMap = new HashMap<>();

    public boolean isSuspended(MetaType metaType) {
        FailInfo failInfo = failCountMap.get(metaType);
        if (failInfo == null || failInfo.suspendStartFrame == CommonCode.NONE) {
            return false;
        }

        if (TimeUtils.elapsedFrames(failInfo.suspendStartFrame) > failInfo.suspendDurationFrame) {
            failInfo.suspendStartFrame = CommonCode.NONE; // 정지 시작시간을 초기화 한다.
            failInfo.suspendDurationFrame *= 2; // 정지 시간을 2배로 증가시킨다.

            String message = "*** " + metaType.getName() + " unlocked ***";
            PlayerUtils.printf(message);
            System.out.println(message);
            return false;
        }

        return true;
    }

    public void update(MetaType metaType) {
        BuildManagerFailureProtector.FailInfo failInfo = failCountMap.get(metaType);
        if (failInfo == null) {
            failInfo = new BuildManagerFailureProtector.FailInfo(metaType);
        }
        failInfo.failCount = failInfo.failCount + 1;
        failCountMap.put(metaType, failInfo);

        if (failInfo.failCount > 10) { // retry count = 10
            failInfo.failCount = 0;
            failInfo.suspendStartFrame = TimeUtils.elapsedFrames();

            String message = "*** " + metaType.getName() + " locked - " + TimeUtils.framesToTimeString(failInfo.suspendDurationFrame) + " ***";
            PlayerUtils.printf(message);
            System.out.println(message);
        }
    }

    private class FailInfo {
        public int failCount;
        public int suspendStartFrame;
        public int suspendDurationFrame;

        public FailInfo(MetaType metaType) {
            super();
            this.failCount = 0;
            this.suspendStartFrame = CommonCode.NONE;
            this.suspendDurationFrame = INITIAL_TIME;
        }
    }

}
