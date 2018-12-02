package org.monster.bootstrap;

import bwapi.Game;
import org.monster.debugger.StopWatch;

public abstract class GameManager {

    private int timeToLiveFrames = 0;
    private long recorded = 0L;
    private StopWatch stopWatch = new StopWatch();

    public long getRecorded() {
        return recorded;
    }

    public void setStopWatchTag(String tag) {
        stopWatch.setTag(tag);
    }

    public void start() {
        stopWatch.start();
    }

    public void updateTimeCheck(Game Broodwar) {
        start();
        update();
        end();
    }

    public void updateTimeCheck() {
        start();
        update();
        end();
    }

    public abstract void update();

    public void end() {
        long timeSpent = stopWatch.record();
        if (timeSpent > recorded || timeToLiveFrames <= 0) {
            recorded = timeSpent;
            timeToLiveFrames = 180;
        } else {
            timeToLiveFrames--;
        }
    }
}
