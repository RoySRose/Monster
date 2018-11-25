package prebot.build.provider;

public interface BuildableItem {


    boolean buildCondition();

    void process();
}
