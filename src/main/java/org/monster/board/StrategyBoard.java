package org.monster.board;

import bwapi.Position;
import bwta.BaseLocation;
import bwta.Chokepoint;
import org.monster.common.MetaType;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.TimeUtils;
import org.monster.strategy.constant.EnemyStrategy;
import org.monster.strategy.constant.EnemyStrategyOptions;
import org.monster.strategy.constant.StrategyCode;
import org.monster.strategy.manage.PositionFinder;
import org.monster.strategy.manage.SpiderMineManger;
import org.monster.micro.constant.MicroConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrategyBoard {

    private static StrategyBoard instance = new StrategyBoard();

    public static StrategyBoard Instance() {
        return instance;
    }

    public static EnemyStrategy startStrategy = EnemyStrategy.UNKNOWN;
    public static EnemyStrategy currentStrategy = EnemyStrategy.UNKNOWN;
    public static List<EnemyStrategy> strategyHistory = new ArrayList<>();

    public static EnemyStrategyOptions.FactoryRatio factoryRatio = EnemyStrategyOptions.FactoryRatio.ratio(0, 0, 0, 1);
    public static List<MetaType> upgrade = new ArrayList<>();
    public static int marineCount = 0;
    public static EnemyStrategyOptions.AddOnOption addOnOption = null;
    public static EnemyStrategyOptions.ExpansionOption expansionOption = null;
    public static EnemyStrategyOptions.BuildTimeMap buildTimeMap = new EnemyStrategyOptions.BuildTimeMap();

    public static int wraithCount = 0;
    public static int valkyrieCount = 0;

    // 터렛이 필요한 frame (turretBuildStartFrame 이전에는 터렛을 짓지 않는다.)
    public static int turretNeedFrame = TimeUtils.timeToFrames(13, 0);
    public static int turretBuildStartFrame = TimeUtils.timeToFrames(13, 0);
    public static int engineeringBayBuildStartFrame = TimeUtils.timeToFrames(13, 0);
    public static int academyFrame = TimeUtils.timeToFrames(13, 0);

    public static boolean EXOK = false; // 앞마당 커맨드가 제자리에 안착했는지 여부

    // [적군 위치에 따른 상태 파악]
    // 빌드매니저: COMMING, IN_MY_REGION인 경우 벌처, 팩토리가 없는 경우 마린을 우선적으로 추가 생산한다.
    // (마린 6기 이상이면 추가생산 없음, 벌처 2기 이상이면 추가생산 없음 - 적용 특이사항 예시) (initial이 끝나기 전)
    // 컴뱃매니저: ?
    public static StrategyCode.EnemyUnitStatus enemyUnitStatus = StrategyCode.EnemyUnitStatus.SLEEPING;
    public static Position totalEnemyCneterPosition = Position.Unknown;
    public static Position nearGroundEnemyPosition = Position.Unknown;
    public static Position nearAirEnemyPosition = Position.Unknown;
    public static Position dropEnemyPosition = Position.Unknown;

    // [아군 메인부대의 전투개시 여부]
    // 컴뱃매니저: 시즈모드 변경시, watcher전투 판단시
    public static boolean initiated = false;

    // [메인스쿼드 모드]
    // 모드에 따라 mainSquadPosition이 변경된다.
    // 모드에 따라 게릴라벌처 투입 비율이 조정된다.
    // 수비모드, 공격모드에 따라 적 선택 방식이 달라진다.(수비모드인 경우 건물 위주, 공격모드인 경우 유닛 위주)
    // 공격모드 종류에 따라 적 진영으로 빠르게 전진할지, 부하가 걸리는 컨트롤을 사용할지 여부 등을 결정한다.
    public static MicroConfig.MainSquadMode mainSquadMode = MicroConfig.MainSquadMode.NORMAL;
    public static int attackStartedFrame = CommonCode.NONE;
    public static int retreatFrame = CommonCode.NONE;

    // [포지션 정보]
    public static PositionFinder.CampType campType = null; // 수비 포지션 타입
    public static Position campPosition = null; // 수비 포지션
    public static Position campPositionSiege = null; // 수비 포지션
    public static Position mainPosition = null; // 메인스쿼드 목표 포지션
    public static Position watcherPosition = null; // watcher 포지션
    public static Position mainSquadCenter = null; // 메인스쿼드 유닛들의 센터 포지션
    public static Position mainSquadLeaderPosition = null; // 메인스쿼드 유닛들의 리더 포지션
    public static int mainSquadCoverRadius = 0; // 메인스쿼드가 차지한 지역
    public static boolean mainSquadCrossBridge = false;

    public static int findRatFinishFrame = CommonCode.NONE;

    public static int checkerMaxNumber = 0; // 정찰벌처 최대수
    public static int spiderMineNumberPerPosition = 1; //
    public static int spiderMineNumberPerGoodPosition = 1;
    public static SpiderMineManger.MinePositionLevel watcherMinePositionLevel = SpiderMineManger.MinePositionLevel.NOT_MY_OCCUPIED;

    // [전략에 따른 가스조절]
    public static boolean gasAdjustment = false;
    public static int gasAdjustmentWorkerCount = 0;

    // [정찰SCV 할당]
    public static boolean assignScoutScv = false;

    /**
     * Strategy for Monster
     */
    public static Map<Decision, Boolean> decisions = new HashMap<>();
    public static Map<String, Position> positions = new HashMap<>();
    public static Map<String, BaseLocation> baseLocations = new HashMap<>();
    public static Map<String, Chokepoint> chokePoints = new HashMap<>();

    public void init() {
        //TODO 더 좋은 방법이 있을것 같은데..
        for(Decision decision : Decision.values()) {
            decisions.put(decision, false);
        }
    }
}
