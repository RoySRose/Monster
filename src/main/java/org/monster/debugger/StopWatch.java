package org.monster.debugger;

/**
 * 테스트를 위해 소요되는 시간을 체크
 */
@Deprecated
public class StopWatch {
    private static final long DEFAULT_WARN_DURATION = 35;

    private String tag;
    private boolean isMicroTimeTest; // 15.625ms == 15625000nanoseconds

    private long timeStarted;

    public StopWatch(boolean isMicroTimeTest) {
        this.isMicroTimeTest = isMicroTimeTest;
    }

    public StopWatch() {
        this.isMicroTimeTest = false;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void start() {
        timeStarted = systemTime();
    }

    public long record() {
        long timeCurrent = systemTime();
        long timeSpent = timeCurrent - this.timeStarted;

        this.timeStarted = timeCurrent;
        this.warning(timeSpent);
        return timeSpent;
    }

    private long systemTime() {
        long timeCurrent;
        if (isMicroTimeTest) {
            timeCurrent = System.nanoTime() / 1000;
        } else {
            timeCurrent = System.currentTimeMillis();
        }
        return timeCurrent;
    }

    private void warning(long timeSpent) {
        long warnDuration = DEFAULT_WARN_DURATION;
        if (isMicroTimeTest) {
            warnDuration = DEFAULT_WARN_DURATION * 1000;
        }
        if (timeSpent < warnDuration) {
            return;
        }

//		for (StackTraceElement ste : new Throwable().getStackTrace()) {
//			if (ste.getClassName().endsWith(this.getClass().getSimpleName())) {
//				continue;
//			}
//			String[] classTrace = ste.getClassName().split("\\.");
//			String className = classTrace[classTrace.length - 1];
//			
//			String warnTag = tag == null ? "" : tag + " @ ";
//			String warnDetail = className + " (L" + ste.getLineNumber() + ", timeSpent: " + timeSpent + ")";
//			Prebot.Broodwar.printf("WARNING : " + warnTag + warnDetail);
//			break;
//		}
    }

}
