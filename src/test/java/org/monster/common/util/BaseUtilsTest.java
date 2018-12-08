package org.monster.common.util;

import bwapi.Position;
import bwapi.TilePosition;
import bwta.BaseLocation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.monster.common.util.internal.IConditions;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PositionUtils.class)
public class BaseUtilsTest {

    BaseLocation baseLocation1 = mock(BaseLocation.class);
    BaseLocation baseLocation2 = mock(BaseLocation.class);
    List<BaseLocation> baseLocationlist = new ArrayList<>();

    @Before
    public void settings(){

        PowerMockito.mockStatic(PositionUtils.class);

        TilePosition tilePosition1 = new TilePosition(1,1);
        TilePosition tilePosition2 = new TilePosition(2,2);

        Position position1= new Position(1,1);
        Position position2 = new Position(2,2);

        when(baseLocation1.getTilePosition()).thenReturn(tilePosition1);
        when(baseLocation2.getTilePosition()).thenReturn(tilePosition2);

        when(baseLocation1.getPosition()).thenReturn(position1);
        when(baseLocation2.getPosition()).thenReturn(position2);

        baseLocationlist.add(baseLocation1);
        baseLocationlist.add(baseLocation2);
    }

    @Test
    public void getClosestBaseFromPositionTest() throws Exception {

        TilePosition fromTilePosition = new TilePosition(3,3);
        Position fromPosition = fromTilePosition.toPosition();

        when(PositionUtils.isValidPosition(fromPosition)).thenReturn(true);

        BaseLocation result = BaseUtils.getClosestBaseFromPosition(baseLocationlist, fromPosition);
        Assert.assertTrue(BaseUtils.equals(result, baseLocation2));
    }

    @Test
    public void getGroundClosestBaseFromPosition() throws Exception {

        BaseLocation fromBaseLocation = mock(BaseLocation.class);
        when(baseLocation1.getGroundDistance(fromBaseLocation)).thenReturn(2.0);
        when(baseLocation2.getGroundDistance(fromBaseLocation)).thenReturn(1.0);

        BaseLocation result = BaseUtils.getGroundClosestBaseFromPosition(baseLocationlist, fromBaseLocation, new IConditions.BaseCondition() {
            @Override
            public boolean correspond(BaseLocation base) {
                return true;
            }
        });

        Assert.assertTrue(BaseUtils.equals(result, baseLocation2));
    }
    @Test
    public void getGroundFarthestBaseFromPosition() throws Exception {

        BaseLocation fromBaseLocation = mock(BaseLocation.class);
        when(baseLocation1.getGroundDistance(fromBaseLocation)).thenReturn(2.0);
        when(baseLocation2.getGroundDistance(fromBaseLocation)).thenReturn(1.0);

        BaseLocation result = BaseUtils.getGroundFarthestBaseFromPosition(baseLocationlist, fromBaseLocation, new IConditions.BaseCondition() {
            @Override
            public boolean correspond(BaseLocation base) {
                return true;
            }
        });

        Assert.assertTrue(BaseUtils.equals(result, baseLocation1));
    }
}