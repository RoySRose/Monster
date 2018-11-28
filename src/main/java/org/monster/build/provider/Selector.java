package org.monster.build.provider;


public interface Selector<T> {
    T getSelected();

    void select();
}
