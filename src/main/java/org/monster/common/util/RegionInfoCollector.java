package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwta.BWTA;
import bwta.Region;
import org.monster.common.UnitInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegionInfoCollector implements InfoCollector {

    private static RegionInfoCollector instance = new RegionInfoCollector();
    protected Map<Player, Region> thirdRegion = new HashMap();
    protected Map<Player, Set<Region>> occupiedRegions = new HashMap();
    Game Broodwar;
    private Player selfPlayer;
    private Player enemyPlayer;
    private BaseInfoCollector baseInfoCollector;
    private ChokeInfoCollector chokeInfoCollector;

    public static RegionInfoCollector Instance() {
        return instance;
    }

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
        selfPlayer = Broodwar.self();
        enemyPlayer = Broodwar.enemy();

        baseInfoCollector = BaseInfoCollector.Instance();
        chokeInfoCollector = ChokeInfoCollector.Instance();
    }

    protected void calculateThirdRegion(Player player) {
        double radian = MicroUtils.targetDirectionRadian(
                baseInfoCollector.firstExpansionLocation.get(player).getPosition(),
                chokeInfoCollector.secondChokePoint.get(player).getCenter());
        Region myThirdRegion = BWTA.getRegion(
                MicroUtils.getMovePosition(chokeInfoCollector.secondChokePoint.get(player).getCenter(), radian, 100));
        thirdRegion.put(player, myThirdRegion);
    }


    @Override
    public void update() {
        initializeOccupiedRegion();
        updateRegionsOccupiedRegion();
    }

    private void initializeOccupiedRegion() {
        if (occupiedRegions.get(selfPlayer) != null) {
            occupiedRegions.get(selfPlayer).clear();
        }
        if (occupiedRegions.get(enemyPlayer) != null) {
            occupiedRegions.get(enemyPlayer).clear();
        }
    }

    private void updateRegionsOccupiedRegion() {
        for (UnitInfo ui : UnitUtils.getEnemyUnitInfoList()) {

            if (ui.getType().isBuilding()) {
                updateOccupiedRegions(BWTA.getRegion(ui.getLastPosition()),
                        Broodwar.enemy());
            }
        }

        for (Unit ui : UnitUtils.getUnitList()) {
            if (ui.getType().isBuilding()) {
                updateOccupiedRegions(BWTA.getRegion(ui.getPosition()),
                        Broodwar.self());
            }
        }
    }

    public void updateOccupiedRegions(Region region, Player player) {
        // if the region is valid (flying buildings may be in null regions)
        if (region != null) {
            if (occupiedRegions.get(player) == null) {
                occupiedRegions.put(player, new HashSet<>());
            }
            occupiedRegions.get(player).add(region);
        }
    }

}
