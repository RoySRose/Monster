package org.monster.finder.baselocation;


import org.junit.Assert;
import org.junit.Test;

public class NextExpansionFinderTest {

    @Test
    public void test(){
        NextExpansionFinder nextExpansionFinder = new NextExpansionFinder();

        Assert.assertEquals("NextExpansionFinder", nextExpansionFinder.keyString);
    }

}