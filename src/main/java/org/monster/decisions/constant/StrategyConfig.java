package org.monster.decisions.constant;

public class StrategyConfig {

    public static final int APPROX_DISTANCE_1280 = 1280;
    public static final int APPROX_DISTANCE_640 = 640;
    public static final int APPROX_DISTANCE_320 = 320;

    public static final int SCAN_DURATION = 240; // approximate time that a comsat scan provides vision
    public static final int IGNORE_ENEMY_UNITINFO_SECONDS = 12;
    public static final int IGNORE_ENEMY_SIEGE_TANK_SECONDS = 300;

    // 기본 전략
    public static enum EnemyStrategy {
        ZERGBASIC(0, 2, 10, 1),
        ZERGBASIC_HYDRAWAVE(1, 5, 2, 2),
        //		ZERGBASIC_GIFTSET(0, 2, 10, 1),
        ZERGBASIC_HYDRAMUTAL(1, 5, 6, 3),
        ZERGBASIC_LINGHYDRA(1, 4, 3, 1),
        ZERGBASIC_LINGLURKER(2, 4, 4, 1),
        ZERGBASIC_LINGMUTAL(1, 2, 10, 1),
        ZERGBASIC_LINGULTRA(5, 6, 2, 1),
        ZERGBASIC_MUTAL(1, 1, 9, 3),
        ZERGBASIC_MUTALMANY(0, 1, 12, 3),
        ZERGBASIC_ULTRA(5, 6, 1, 1),

        PROTOSSBASIC(5, 5, 0, 2),
        PROTOSSBASIC_CARRIER(1, 2, 8, 3),
        PROTOSSBASIC_DOUBLEPHOTO(3, 3, 0, 1),

        TERRANBASIC(0, 2, 0, 1),
        TERRANBASIC_BIONIC(10, 5, 0, 1),
        TERRANBASIC_MECHANIC(5, 8, 1, 2),
        TERRANBASIC_MECHANICWITHWRAITH(0, 7, 1, 3),
        TERRANBASIC_MECHANICAFTER(0, 9, 1, 3),
        TERRANBASIC_BATTLECRUISER(5, 1, 1, 2),
        ATTACKISLAND(0, 0, 1, 3);;

        public int vultureRatio;
        public int tankRatio;
        public int goliathRatio;
        public int weight;

        private EnemyStrategy(int vultureRatio, int tankRatio, int goliathRatio, int weight) {
            this.vultureRatio = vultureRatio;
            this.tankRatio = tankRatio;
            this.goliathRatio = goliathRatio;
            this.weight = weight;
        }
    }

    //예외 전략. 예외가 아닐때는 무조건 INIT 으로
    public static enum EnemyStrategyException {
        ZERGEXCEPTION_FASTLURKER(1, 4, 1, 2),
        //		ZERGEXCEPTION_GUARDIAN(0, 2, 10, 1),
//		ZERGEXCEPTION_NONGBONG(0, 2, 10, 1),
        ZERGEXCEPTION_ONLYLING(7, 3, 2, 1),
        ZERGEXCEPTION_PREPARELURKER(0, 0, 0, 1),
        ZERGEXCEPTION_REVERSERUSH(1, 3, 3, 2),
        ZERGEXCEPTION_HIGHTECH(0, 0, 0, 1),
        //		PROTOSSEXCEPTION_CARRIERMANY(9, 3, 0, 1),
        PROTOSSEXCEPTION_DARK(9, 3, 0, 1),
        //		PROTOSSEXCEPTION_REAVER(8, 3, 1, 1),
        PROTOSSEXCEPTION_SCOUT(7, 3, 2, 1),
        PROTOSSEXCEPTION_SHUTTLE(7, 3, 2, 1),
        //		PROTOSSEXCEPTION_SHUTTLEMIX(10, 2, 0, 1),
        PROTOSSEXCEPTION_READYTOZEALOT(6, 1, 0, 1),
        PROTOSSEXCEPTION_ZEALOTPUSH(6, 1, 0, 1),
        PROTOSSEXCEPTION_READYTODRAGOON(1, 3, 0, 2),
        PROTOSSEXCEPTION_DRAGOONPUSH(1, 4, 0, 2),
        PROTOSSEXCEPTION_PHOTONRUSH(0, 0, 0, 0),
        PROTOSSEXCEPTION_DOUBLENEXUS(0, 0, 0, 0),
        PROTOSSEXCEPTION_ARBITER(0, 0, 0, 0),
        //		TERRANEXCEPTION_CHEESERUSH(0, 0, 0, 0),
//		TERRANEXCEPTION_NUCLEAR(0, 0, 0, 0),
        TERRANEXCEPTION_WRAITHCLOAK(0, 0, 0, 0),
        INIT(0, 0, 0, 0);

        public int vultureRatio;
        public int tankRatio;
        public int goliathRatio;
        public int weight;

        private EnemyStrategyException(int vultureRatio, int tankRatio, int goliathRatio, int weight) {
            this.vultureRatio = vultureRatio;
            this.tankRatio = tankRatio;
            this.goliathRatio = goliathRatio;
            this.weight = weight;
        }
    }
}
