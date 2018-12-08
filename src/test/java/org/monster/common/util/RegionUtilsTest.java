package org.monster.common.util;

import bwapi.Position;
import bwta.Region;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RegionUtilsTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void equalsTest() throws Exception {

        Region region1 = mock(Region.class);
        Region region2 = mock(Region.class);

        when(region1.getCenter()).thenReturn(new Position(1,1));
        when(region2.getCenter()).thenReturn(new Position(1,1));

        Assert.assertFalse(region1 == region2);
        Assert.assertTrue(RegionUtils.equals(region1, region2));
    }
}