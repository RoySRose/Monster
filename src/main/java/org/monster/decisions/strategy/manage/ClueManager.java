package org.monster.decisions.strategy.manage;

import bwapi.UnitType;
import org.monster.decisions.strategy.analyse.Clue;

import java.util.HashSet;
import java.util.Set;

public class ClueManager {

    private static ClueManager instance = new ClueManager();
    private Set<Clue.ClueInfo> clueInfoSet = new HashSet<>();

    public static ClueManager Instance() {
        return instance;
    }

    public Set<Clue.ClueInfo> getClueInfoList() {
        return clueInfoSet;
    }

    public void addClueInfo(Clue.ClueInfo info) {
        if (containsClueInfo(info)) {
            return;
        }

        Clue.ClueInfo removeInfo = null;
        for (Clue.ClueInfo clueInfo : clueInfoSet) {
            if (clueInfo.type == info.type) {
                removeInfo = clueInfo;
                break;
            }
        }
        if (removeInfo != null) {
            clueInfoSet.remove(removeInfo);
        }
        clueInfoSet.add(info);
    }

    public boolean containsClueType(Clue.ClueType type) {
        for (Clue.ClueInfo clueInfo : clueInfoSet) {
            if (clueInfo.type == type) {
                return true;
            }
        }
        return false;
    }

    public boolean containsClueInfo(Clue.ClueInfo info) {
        return clueInfoSet.contains(info);
    }

    public int baseToBaseFrame(UnitType unitType) {
        return 0;
    }
}
