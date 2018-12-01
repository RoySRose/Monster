package org.monster.debugger;

import org.monster.build.base.BuildManager;
import org.monster.build.base.ConstructionManager;
import org.monster.build.provider.BuildQueueProvider;
import org.monster.common.MapGrid;
import org.monster.common.util.TimeUtils;
import org.monster.main.GameManager;
import org.monster.micro.CombatManager;
import org.monster.decisions.strategy.StrategyManager;
import org.monster.worker.WorkerManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BigWatch {

    private static final int TIME_TO_LIVE_SECONDS = 10;
    private static Map<String, Integer> totalDebugMap = new HashMap<>();
    private static Map<String, Long> startTimeMap = new HashMap<>();
    private static Map<String, Long> resultTimeMap = new HashMap<>();
    private static Map<String, Long> recordMap = new HashMap<>();
    private static Map<String, Integer> recordFrameMap = new HashMap<>();

    static {
        totalDebugMap.put("InformationManager", 0);
        totalDebugMap.put("StrategyManager", 0);
        totalDebugMap.put("MapGrid", 0);
        totalDebugMap.put("BuildManager", 0);
        totalDebugMap.put("BuildQueueProvider", 0);
        totalDebugMap.put("ConstructionManager", 0);
        totalDebugMap.put("WorkerManager", 0);
        totalDebugMap.put("CombatManager", 0);
        totalDebugMap.put("AttackDecisionMaker", 0);
    }

    public static Map<String, Long> getResultTimeMap() {
        return resultTimeMap;
    }

    public static Map<String, Long> getRecordTimeMap() {
        return recordMap;
    }

    public static void clear() {
        for (String tag : resultTimeMap.keySet()) {
            Long recordTime = recordMap.get(tag);
            Long time = resultTimeMap.get(tag);
            if (recordTime == null || time > recordTime) {
                recordMap.put(tag, time);
                recordFrameMap.put(tag, TimeUtils.elapsedFrames());
            }
        }

        for (String tag : recordFrameMap.keySet()) {
            Integer recordFrame = recordFrameMap.get(tag);
            if (TimeUtils.elapsedFrames(recordFrame) > TIME_TO_LIVE_SECONDS * TimeUtils.SECOND) {
                recordMap.remove(tag);
            }
        }

        Long timeSpent = resultTimeMap.get("... GAME COMMANDER ...");
        if (timeSpent >= 85) {
            List<GameManager> gameManagers = Arrays.asList(
                    //InformationManager.Instance(),
                    StrategyManager.Instance(),
                    MapGrid.Instance(),
                    BuildManager.Instance(),
                    BuildQueueProvider.Instance(),
                    ConstructionManager.Instance(),
                    WorkerManager.Instance(),
                    CombatManager.Instance())
//                    AttackDecisionMaker.Instance())
                    ;

            System.out.println("################# over 55ms ########################");
            System.out.println("[managers]");
            for (GameManager gameManager : gameManagers) {
                String simpleName = gameManager.getClass().getSimpleName();
                long recorded = gameManager.getRecorded();
                if (recorded > 20) {
                    Integer integer = totalDebugMap.get(simpleName);
                    if (integer != null) {
                        integer++;
                    }
                    totalDebugMap.put(simpleName, integer);
                }
                System.out.println(simpleName + "=" + recorded);
            }
            System.out.println();
            System.out.println("[bigwatch]");
            System.out.println(resultTimeMap);
            System.out.println();
            System.out.println("[result]");
            System.out.println(totalDebugMap);
            System.out.println("####################################################");
        }

        startTimeMap.clear();
        resultTimeMap.clear();
    }

    public static void start(String tag) {
        Long time = startTimeMap.get(tag);
        if (time != null) {
            System.out.println("##### ALREADY EXIST " + tag + "#####");
            return;
        }
        startTimeMap.put(tag, System.currentTimeMillis());
    }


    public static void record(String tag) {
        Long time = startTimeMap.get(tag);
        if (time == null) {
            System.out.println("##### NO TIME - " + tag + " #####");
            return;
        }
        resultTimeMap.put(tag, System.currentTimeMillis() - time);
    }
}
