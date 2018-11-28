package org.monster.common;

@Deprecated
public class LagTest {

    private static final long DEFAULT_WARN_DURATION = 50;
    private static final String ROOT_PACKAGE = "pre.";

    private String name;
    private long startTime;
    private boolean microTime; // 15.625ms == 15625000nanoseconds
    private long warnDuration;

    private LagTest(String testName, boolean microTime) {
        this.warnDuration = DEFAULT_WARN_DURATION;
        this.name = testName;
        this.microTime = microTime;
        this.startTime = microTime ? System.nanoTime() / 1000 : System.currentTimeMillis();
    }

    public static LagTest startTest() {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String className = ste.getClassName();
        String methodName = ste.getMethodName();

        String testName = className.replace(ROOT_PACKAGE, "") + "." + methodName;
        return new LagTest(testName, false);
    }

    public static LagTest startTest(boolean microTime) {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String className = ste.getClassName();
        String methodName = ste.getMethodName();

        String testName = className.replace(ROOT_PACKAGE, "") + "." + methodName;
        return new LagTest(testName, microTime);
    }

    public void setDuration(long warnDuration) {
        this.warnDuration = warnDuration;
    }

    public boolean estimate() {
        long currentTime = microTime ? System.nanoTime() / 1000 : System.currentTimeMillis();

        String section = "line" + new Throwable().getStackTrace()[1].getLineNumber();
        long estimatedTime = currentTime - startTime;
        startTime = currentTime;

        if (estimatedTime > warnDuration) {
            System.out.println("### " + name + "." + section + " : " + estimatedTime + (microTime ? " microsec" : " millisec"));
            return true;
        } else {
            return false;
        }
    }

    public boolean estimate(String tag) {
        long currentTime = microTime ? System.nanoTime() / 1000 : System.currentTimeMillis();

        String section = "line" + new Throwable().getStackTrace()[1].getLineNumber() + " *" + tag;
        long estimatedTime = currentTime - startTime;
        startTime = currentTime;

        if (estimatedTime > warnDuration) {
            System.out.println("### " + name + "." + section + " : " + estimatedTime + (microTime ? " microsec" : " millisec"));
            return true;
        } else {
            return false;
        }
    }

}
