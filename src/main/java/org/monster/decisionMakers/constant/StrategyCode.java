package org.monster.decisionMakers.constant;

public class StrategyCode {

    public static enum KnowlegeStatus {
        WAITING, VERIFING, PROVED_TRUE, PROVED_FALSE, WRITE_FILE, ENDED
    }

    public static enum KnowledgeActionEvaluation {
        DONT_KNOW, GOOD_ACTION, BAD_ACTION
    }

    public static enum SmallFightPredict {
        BACK, ATTACK, OVERWHELM
    }

    public static enum EnemyUnitStatus {
        SLEEPING, COMMING, IN_MY_REGION
    }
}
