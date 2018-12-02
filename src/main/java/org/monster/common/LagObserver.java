package org.monster.common;


import org.monster.common.util.TimeUtils;
import org.monster.bootstrap.Monster;

public class LagObserver {

    public static final int MANAGER0 = 0; //info
    public static final int MANAGER1 = 1; //strategy
    public static final int MANAGER2 = 2; //mapgrid
    public static final int MANAGER3 = 3; //build provider
    public static final int MANAGER4 = 4; //build
    public static final int MANAGER5 = 5; //construction

    //	public static void main(String[] ar) {
//		
//		for (int frame = 0; frame < 32; frame++) {
//			boolean e0 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER0, 0), testRotationSize());
//			boolean e0_2 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER0, 1), testRotationSize());
//			boolean e1 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER1, 0), testRotationSize());
//			boolean e1_2 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER1, 1), testRotationSize());
//			boolean e2 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER2, 0), testRotationSize());
//			boolean e2_2 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER2, 1), testRotationSize());
//			boolean e3 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER3, 0), testRotationSize());
//			boolean e3_2 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER3, 1), testRotationSize());
//			boolean e4 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER4, 0), testRotationSize());
//			boolean e4_2 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER4, 1), testRotationSize());
//			boolean e5 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER5, 0), testRotationSize());
//			boolean e5_2 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER5, 1), testRotationSize());
//			boolean e6 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER6, 0), testRotationSize());
//			boolean e6_2 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER6, 1), testRotationSize());
//			boolean e7 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER7, 0), testRotationSize());
//			boolean e7_2 = testRotation(frame, testManagerExecuteRotation(LagObserver.MANAGER7, 1), testRotationSize());
//			System.out.println(e0 + ", " + e1 + ", " + e2 + ", " + e3 + ", " + e4 + ", " + e5 + ", " + e6 + ", " + e7);
//			System.out.println(e0_2 + ", " + e1_2 + ", " + e2_2 + ", " + e3_2 + ", " + e4_2 + ", " + e5_2 + ", " + e6_2 + ", " + e7_2);
//			System.out.println();
//		}
//	}
//	
//
//	private static int testManagerExecuteRotation(int manager, int index) {
//		return (MANAGER_ROTATION_SIZE * index + manager) % testRotationSize();
//	}
//	
//	private static boolean testRotation(int frame, int group, int rotationSize) {
//		return (frame % rotationSize) == group;
////		return true;
//	}
//	
//	private static int testRotationSize() {
//		return 16;
//	}
    public static final int MANAGER6 = 6; //worker
    public static final int MANAGER7 = 7; //combat
    public static final int MANAGER8 = 8; //attack decision
    public static final long MILLISEC_MAX_COAST = 30;
    private static final int MANAGER_ROTATION_SIZE = 9;
    private static final boolean ADJUST_ON = true;
    private static final int OBSERVER_CAPACITY = 15 * 24; // 15초간 delay가 없어서 groupsize를 낮춘다.
    private static final String LAG_RELIEVE_ADJUSTMENT = "Lag Relieve Adjustment: LELEL - %d ... (%s)";
    private static int groupsize = 3; // 1 ... 24
    private static int managerLagLevel = 1;
    private static int groupMaxSize = 48; // max : 2초 딜레이
    private static int groupMinSize = 3;
    //	private static final long MILLISEC_MIN_COAST = 30;
    private static long[] observedTime = new long[OBSERVER_CAPACITY];
    private long startTime;

    public LagObserver() {
    }

    public static int groupsize() {
        return groupsize;
    }

    public static int getManagerLagLevel() {
        return managerLagLevel;
    }

    public static int managerRotationSize() { // 7, 14, 21, 28
        return MANAGER_ROTATION_SIZE * managerLagLevel;
    }

    public static int managerExecuteRotation(int manager, int index) {
        return (MANAGER_ROTATION_SIZE * index + manager) % managerRotationSize();
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void observe() {
        observedTime[TimeUtils.getFrame() % OBSERVER_CAPACITY] = System.currentTimeMillis() - this.startTime;
        this.startTime = System.currentTimeMillis();
        this.adjustment();
    }

    public void adjustment() {
        if (ADJUST_ON) {
            long cost = observedTime[TimeUtils.getFrame() % OBSERVER_CAPACITY];

            if (cost > MILLISEC_MAX_COAST) {
                if (groupsize < groupMaxSize) {
                    groupsize++;
                }
            } else {
                if (groupsize > groupMinSize) {
                    boolean exceedTimeExist = false;
                    for (long t : observedTime) {
                        if (t >= MILLISEC_MAX_COAST) {
                            exceedTimeExist = true;
                            break;
                        }
                    }
                    if (!exceedTimeExist) {
                        groupsize--;
                    }
                }
            }
            managerLagLevel = groupsize / MANAGER_ROTATION_SIZE + 1; // manager size = 9

            if (Monster.Broodwar.self().supplyUsed() > 300) {
                groupMinSize = MANAGER_ROTATION_SIZE;
                if (groupsize < groupMinSize) {
                    groupsize = MANAGER_ROTATION_SIZE;
                }
            }
        }
    }

}
