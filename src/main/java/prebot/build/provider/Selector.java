package prebot.build.provider;


public interface Selector<T> {
    T getSelected();

    void select();
}
