package org.monster.micro;

public class KitingOption {

    public FleeOption fOption;
    public CoolTimeAttack cooltimeAlwaysAttack;

    public KitingOption(FleeOption fOption, CoolTimeAttack cooltimeAlwaysAttack) {
        this.fOption = fOption;
        this.cooltimeAlwaysAttack = cooltimeAlwaysAttack;
    }

    public static KitingOption defaultOption() {
        return new KitingOption(FleeOption.defaultOption(), KitingOption.CoolTimeAttack.COOLTIME_ALWAYS);
    }

    public enum CoolTimeAttack {
        KEEP_SAFE_DISTANCE(false), COOLTIME_ALWAYS(true), COOLTIME_ALWAYS_IN_RANGE(true);


        public boolean coolTimeAlwaysAttack;

        private CoolTimeAttack(boolean coolTimeAlwaysAttack) {
            this.coolTimeAlwaysAttack = coolTimeAlwaysAttack;
        }
    }
}
