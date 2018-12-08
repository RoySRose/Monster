package org.monster.decisions.strategy.analyse;

import bwapi.UnitType;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.constant.EnemyUnitVisibleStatus;
import org.monster.common.constant.RegionType;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.PositionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UnitAnalyser {

    private List<UnitInfo> foundAll = new ArrayList<>();
    private List<UnitInfo> foundFoundInEnemyBase = new ArrayList<>();
    private List<UnitInfo> foundInEnemyExpansion = new ArrayList<>();
    private List<UnitInfo> foundInMyBase = new ArrayList<>();
    private List<UnitInfo> foundInMyExpansion = new ArrayList<>();
    private List<UnitInfo> foundEtc = new ArrayList<>();
    private List<UnitInfo> foundUnknown = new ArrayList<>();

    private Map<Integer, Integer> buildStartFrameMap = new HashMap<>();

    private UnitType unitType;

    public UnitAnalyser(UnitType unitType) {
        this.unitType = unitType;
    }

    public abstract void analyse();

    public void upateFoundInfo() {
        if (!foundUnknown.isEmpty() && BaseUtils.enemyMainBase() != null) {
            for (UnitInfo found : foundUnknown) {
                RegionType regionType = PositionUtils.positionToRegionType(found.getLastPosition());
                if (regionType == RegionType.ENEMY_BASE) {
                    foundFoundInEnemyBase.add(found);
                } else if (regionType == RegionType.ENEMY_FIRST_EXPANSION) {
                    foundInEnemyExpansion.add(found);
                } else if (regionType == RegionType.MY_BASE) {
                    foundInMyBase.add(found);
                } else if (regionType == RegionType.MY_FIRST_EXPANSION) {
                    foundInMyExpansion.add(found);
                } else if (regionType == RegionType.ETC) {
                    foundEtc.add(found);
                }
            }
            foundUnknown.clear();
        }


        List<UnitInfo> unitInfos = UnitUtils.getEnemyUnitInfoList(EnemyUnitVisibleStatus.VISIBLE, unitType);
        if (unitInfos.isEmpty()) {
            return;
        }

        for (UnitInfo unitInfo : unitInfos) {
            boolean containUnit = false;
            for (UnitInfo found : foundAll) {
                if (unitInfo.getUnitID() == found.getUnitID()) {
                    containUnit = true;
                    break;
                }
            }
            if (!containUnit) {
                RegionType regionType = PositionUtils.positionToRegionType(unitInfo.getLastPosition());
                if (regionType == RegionType.ENEMY_BASE) {
                    foundFoundInEnemyBase.add(unitInfo);
                } else if (regionType == RegionType.ENEMY_FIRST_EXPANSION) {
                    foundInEnemyExpansion.add(unitInfo);
                } else if (regionType == RegionType.MY_BASE) {
                    foundInMyBase.add(unitInfo);
                } else if (regionType == RegionType.MY_FIRST_EXPANSION) {
                    foundInMyExpansion.add(unitInfo);
                } else if (regionType == RegionType.ETC) {
                    foundEtc.add(unitInfo);
                } else {
                    foundUnknown.add(unitInfo);
                }
                foundAll.add(unitInfo);

                // 빌드시작한 시잔 (초). 이미 완성되어 알 수 없다면 UNKNOWN
                buildStartFrameMap.put(unitInfo.getUnitID(), TimeUtils.buildStartFrames(unitInfo.getUnit()));
            }
        }
    }

    protected List<UnitInfo> found(RegionType... regionTypes) {
        if (regionTypes == null || regionTypes.length == 0) {
            return foundAll;
        }
        List<UnitInfo> foundList = new ArrayList<>();
        for (RegionType regionType : regionTypes) {
            if (regionType == RegionType.ENEMY_BASE) {
                foundList.addAll(foundFoundInEnemyBase);
            } else if (regionType == RegionType.ENEMY_FIRST_EXPANSION) {
                foundList.addAll(foundInEnemyExpansion);
            } else if (regionType == RegionType.MY_BASE) {
                foundList.addAll(foundInMyBase);
            } else if (regionType == RegionType.MY_FIRST_EXPANSION) {
                foundList.addAll(foundInMyExpansion);
            } else if (regionType == RegionType.ETC) {
                foundList.addAll(foundEtc);
            } else {
                foundList.addAll(foundUnknown);
            }
        }
        return foundList;
    }

    protected int buildStartFrame(UnitInfo eui) {
        return buildStartFrameMap.get(eui.getUnitID());
    }

    protected int buildStartFrameDefaultJustBefore(UnitInfo eui) {
        int buildStartFrame = buildStartFrame(eui);
        if (buildStartFrame == CommonCode.UNKNOWN) {
            return eui.getUpdateFrame() - eui.getType().buildTime();
        } else {
            return buildStartFrame;
        }
    }

    protected int baseToBaseFrame(UnitType unitType) {
        return TimeUtils.baseToBaseFrame(unitType);
    }

    protected int lastUnitFoundFrame(List<UnitInfo> euiList, int lastCount) {
        if (euiList.size() < lastCount) {
            lastCount = euiList.size();
        }
        int maxFrame = 0;
        for (int i = 0; i < lastCount; i++) {
            UnitInfo unitInfo = euiList.get(i);
            if (unitInfo.getUpdateFrame() > maxFrame) {
                maxFrame = unitInfo.getUpdateFrame();
            }
        }
        return maxFrame;
    }

}
