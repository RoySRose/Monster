package org.monster.common.util;

import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Race;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BWTA.class)
public class GameBase {

    public Game Broodwar = mock(Game.class);
    public Player enemyPlayer = mock(Player.class);
    public Player selfPlayer = mock(Player.class);

    public Race selfRace = mock(Race.class);
    public Race enemyRace = mock(Race.class);

    public Region myRegion = mock(Region.class);

    List<BaseLocation> baseLocationList = new ArrayList<>();

    BaseLocation base0 = mock(BaseLocation.class);
    BaseLocation base1 = mock(BaseLocation.class);
    BaseLocation base2 = mock(BaseLocation.class);
    BaseLocation base3 = mock(BaseLocation.class);


    @Before
    public void setting(){
        when(Broodwar.self()).thenReturn(selfPlayer);
        when(Broodwar.enemy()).thenReturn(enemyPlayer);
        when(selfPlayer.getRace()).thenReturn(selfRace);
        when(enemyPlayer.getRace()).thenReturn(enemyRace);
        when(Broodwar.mapFileName()).thenReturn("CIRCUIT");

        baseLocationList.add(base0);
        baseLocationList.add(base1);
        baseLocationList.add(base2);
        baseLocationList.add(base3);

        PowerMockito.mockStatic(BWTA.class);
        when(BWTA.getStartLocations()).thenReturn(baseLocationList);
        when(BWTA.getStartLocation(selfPlayer)).thenReturn(base0);
        when(BWTA.getRegion(new Position(1,1))).thenReturn(myRegion);
    }

    @Test
    public void dummy(){

    }

}