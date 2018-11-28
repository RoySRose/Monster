package org.monster.build.provider;

public interface BuildableItem {


    boolean buildCondition();

    void process();
}
