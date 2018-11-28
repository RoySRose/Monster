package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;
import org.junit.Before;
import org.junit.Test;
import org.monster.common.UnitInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UnitCacheTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    Game BroodWar = mock(Game.class);
    Player enemyPlayer = mock(Player.class);

    UnitCache unitCache = UnitCache.getCurrentCache();

    UnitType AllUnits = mock(UnitType.class);
    UnitType Zerg_Hydralisk = mock(UnitType.class);
    UnitType Zerg_Lurker = mock(UnitType.class);
    UnitType Zerg_Zergling = mock(UnitType.class);
    UnitType Zerg_Overload = mock(UnitType.class);

    Unit unitNotOnMap1;
    Unit unitNotOnMap2;
    Unit unit1;
    Unit unit2;
    Unit unit3;
    Unit unit4;
    Unit dupUnit2;
    Unit unit6;

    List<Unit> unitList;


    @Before
    public void createUnits(){


//        Constructor<Unit> constructor = Unit.class.getDeclaredConstructor();
//        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

//        constructor.setAccessible(true);

        when(Zerg_Hydralisk.toString()).thenReturn("Zerg_Hydralisk");
        when(Zerg_Lurker.toString()).thenReturn("Zerg_Lurker");
        when(Zerg_Zergling.toString()).thenReturn("Zerg_Zergling");
        when(Zerg_Overload.toString()).thenReturn("Zerg_Overload");
        //when(Zerg_Zergling.toString()).thenReturn("Zerg_Lurker");

        //create unit
        unit1 = mock(Unit.class);
        when(unit1.getID()).thenReturn(1);
        when(unit1.getType()).thenReturn(Zerg_Hydralisk);
        //create unit
        unit2 = mock(Unit.class);
        when(unit2.getID()).thenReturn(2);
        when(unit2.getType()).thenReturn(Zerg_Hydralisk);
        //create unit
        dupUnit2 = mock(Unit.class);
        when(dupUnit2.getID()).thenReturn(2);
        when(dupUnit2.getType()).thenReturn(Zerg_Lurker);
        //create unit
        unit3 = mock(Unit.class);
        when(unit3.getID()).thenReturn(3);
        when(unit3.getType()).thenReturn(Zerg_Zergling);
        //create unit
        unitNotOnMap1 = mock(Unit.class);
        when(unitNotOnMap1.getID()).thenReturn(4);
        when(unitNotOnMap1.getType()).thenReturn(Zerg_Overload);
        //create unit
        unitNotOnMap2 = mock(Unit.class);
        when(unitNotOnMap2.getID()).thenReturn(5);
        when(unitNotOnMap2.getType()).thenReturn(Zerg_Overload);

        unitList = new ArrayList<>();
        unitList.add(unit1);
        unitList.add(unit2);
        unitList.add(unit3);

        when(enemyPlayer.getUnits()).thenReturn(unitList);
        when(enemyPlayer.allUnitCount()).thenReturn(unitList.size());
        when(BroodWar.enemy()).thenReturn(enemyPlayer);
        unitCache.onStart(BroodWar);

    }

    @Test
    public void unitCacheTest() {

        //add unit
        unitCache.updateEnemyUnitInfo(unit1);
        List<UnitInfo> enemyUnitInfoList= unitCache.enemyAllUnits(UnitType.AllUnits);
        for(UnitInfo unitInfo : enemyUnitInfoList){
            logger.debug("before:{},{}", unitInfo.getUnitID(), unitInfo.getType());
        }
        assertEquals(enemyUnitInfoList.size(), 1);

        //add unit
        unitCache.updateEnemyUnitInfo(unit2);
        enemyUnitInfoList= unitCache.enemyAllUnits(UnitType.AllUnits);

        for(UnitInfo unitInfo : enemyUnitInfoList){
            logger.debug("after:{},{}", unitInfo.getUnitID(), unitInfo.getType());
        }
        assertEquals(enemyUnitInfoList.size(), 2);

        //add morphed unit
        unitCache.updateEnemyUnitInfo(unit3);
        enemyUnitInfoList= unitCache.enemyAllUnits(UnitType.AllUnits);

        for(UnitInfo unitInfo : enemyUnitInfoList){
            logger.debug("last:{},{}", unitInfo.getUnitID(), unitInfo.getType());
        }
        assertEquals(enemyUnitInfoList.size(), 2);
    }


    @Test
    public void unitCacheEnemyMapTest() {

        alwaysTrue();
        //add unit
        unitCache.updateEnemyUnitInfo(unit1);
        unitCache.updateEnemyUnitInfo(unit2);
        unitCache.updateEnemyUnitInfo(unit3);
        assertEquals(3, unitCache.enemyAllCount(UnitType.AllUnits));

        //add unit
        unitCache.updateEnemyUnitInfo(unitNotOnMap1);
        unitCache.updateEnemyUnitInfo(unitNotOnMap2);

        assertEquals(5, unitCache.enemyAllCount(UnitType.AllUnits));
        assertEquals(2, unitCache.enemyAllCount(Zerg_Overload));

        assertEquals(2, unitCache.enemyAllUnits(Zerg_Hydralisk).size());
        assertEquals(5, unitCache.enemyAllUnits(UnitType.AllUnits).size());

        unitCache.destroyedUnitInfo(unitNotOnMap2);
        assertEquals(1, unitCache.enemyAllCount(Zerg_Overload));

        for(UnitInfo unitInfo : unitCache.enemyAllUnits(UnitType.AllUnits)){
            logger.debug("before morph:{},{}", unitInfo.getUnitID(), unitInfo.getType());
        }

        unitCache.updateEnemyUnitInfo(dupUnit2);
        alwaysTrue();

        for(UnitInfo unitInfo : unitCache.enemyAllUnits(UnitType.AllUnits)){
            logger.debug("after morph:{},{}", unitInfo.getUnitID(), unitInfo.getType());
        }

        assertEquals(4, unitCache.enemyAllUnits(UnitType.AllUnits).size());

        //false data
        unitCache.destroyedUnitInfo(unitNotOnMap2);

        //morph cancel and destroyed. then remove
        unitCache.destroyedUnitInfo(unit2);
        assertEquals(1, unitCache.enemyAllCount(Zerg_Overload));

        assertEquals(1, unitCache.enemyAllUnits(Zerg_Hydralisk).size());
        assertEquals(3, unitCache.enemyAllUnits(UnitType.AllUnits).size());



        alwaysTrue();
    }

    private void alwaysTrue() {
        assertEquals(3, unitCache.enemyVisibleCount(UnitType.AllUnits));
        assertEquals(0, unitCache.enemyVisibleCount(Zerg_Overload));
        assertEquals(1, unitCache.enemyVisibleUnits(Zerg_Zergling).size());
        assertEquals(2, unitCache.enemyVisibleUnits(Zerg_Hydralisk).size());
    }
}