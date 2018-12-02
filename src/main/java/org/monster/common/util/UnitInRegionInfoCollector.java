package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;
import bwta.BWTA;
import bwta.Region;
import org.monster.common.UnitInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UnitInRegionInfoCollector implements InfoCollector {

    private static UnitInRegionInfoCollector instance = new UnitInRegionInfoCollector();
    private Map<Region, List<UnitInfo>> euiListInMyRegion = new HashMap<>();
    private Set<UnitInfo> euisInMainBaseRegion = new HashSet<>();
    private Set<UnitInfo> euisInExpansionRegion = new HashSet<>();
    private Set<UnitInfo> euisInThirdRegion = new HashSet<>();
    Game Broodwar;
    private Player selfPlayer;
    private Player enemyPlayer;
    private RegionInfoCollector regionInfoCollector;
    private BaseInfoCollector baseInfoCollector;

    public static UnitInRegionInfoCollector Instance() {
        return instance;
    }

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
        selfPlayer = Broodwar.self();
        enemyPlayer = Broodwar.enemy();
        regionInfoCollector = RegionInfoCollector.Instance();
        baseInfoCollector = BaseInfoCollector.Instance();
    }



    @Override
    public void update() {
        initializeData();
        updateEnemiesLocation();
    }

    private void initializeData() {
        euiListInMyRegion.clear();
        euisInMainBaseRegion.clear();
        euisInExpansionRegion.clear();
        euisInThirdRegion.clear();
    }

    private void updateEnemiesLocation() {

        Set<Region> myRegionSet = regionInfoCollector.occupiedRegions.get(selfPlayer);
        for (Region region : myRegionSet) {
            euiListInMyRegion.put(region, new ArrayList<>());
        }

        Region myMainBaseRegion = BWTA.getRegion(baseInfoCollector.mainBaseLocation.get(selfPlayer).getPosition());
        Region myExpansionRegion = BWTA.getRegion(baseInfoCollector.firstExpansionLocation.get(selfPlayer).getPosition());
        Region myThirdRegion = BWTA.getRegion(regionInfoCollector.thirdRegion.get(selfPlayer).getCenter());

//        Map<Integer, UnitInfo> unitAndUnitInfoMap = unitData.get(enemyPlayer).getUnitAndUnitInfoMap();
        for (UnitInfo eui : UnitUtils.getEnemyUnitInfoList()) {
            if (UnitUtils.ignorableEnemyUnitInfo(eui)) {
                continue;
            }
            if (!PositionUtils.isValidPosition(eui.getLastPosition())) {
//				System.out.println("updateEnemiesInMyRegion. invalid eui=" + eui);
                continue;
            }

            Region region = BWTA.getRegion(eui.getLastPosition());
            if (region == null) {
                continue;
            }

            if (myRegionSet.contains(region)) {
                List<UnitInfo> euiList = euiListInMyRegion.get(region);
                euiList.add(eui);
                euiListInMyRegion.put(region, euiList);
            }

            if (region.equals(myMainBaseRegion)) {
                euisInMainBaseRegion.add(eui);
            } else if (region.equals(myExpansionRegion)) {
                euisInExpansionRegion.add(eui);
            } else if (region.equals(myThirdRegion)) {
                euisInThirdRegion.add(eui);
            }
        }
    }


    public Set<UnitInfo> getEuisInMainBaseRegion() {
        return euisInMainBaseRegion;
    }

    public Set<UnitInfo> getEuisInExpansionRegion() {
        return euisInExpansionRegion;
    }

    public Set<UnitInfo> getEuisInThirdRegion() {
        return euisInThirdRegion;
    }

    public List<UnitInfo> getEuiListInMyRegion(Region region) {
        return euiListInMyRegion.get(region);
    }
}
