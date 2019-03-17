package org.monster.strategy.manage;

import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import org.monster.board.StrategyBoard;
import org.monster.common.UnitInfo;
import org.monster.common.constant.CommonCode;
import org.monster.common.constant.EnemyUnitVisibleStatus;
import org.monster.common.constant.UnitFindStatus;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.MapUtils;
import org.monster.common.util.MicroUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.PositionUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitTypeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.micro.compute.WraithFightPredictor;
import org.monster.micro.constant.MicroConfig;
import org.monster.worker.WorkerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Deprecated
public class AirForceManager {

    public static final int AIR_FORCE_TEAM_MERGE_DISTANCE = 80;
    public static final int AIR_FORCE_SAFE_DISTANCE = 100; // 안전 실제 터렛 사정거리보다 추가로 확보하는 거리
    public static final int AIR_FORCE_SAFE_DISTANCE2 = 150; // 안전 실제 터렛 사정거리보다 추가로 확보하는 거리
    public static final int AIR_FORCE_TARGET_MIDDLE_POSITION_SIZE = 1;
    public static int airForceTargetPositionSize = 0; // 최소값=3 (enemyMainBase, enemyExpansionBase, middlePosition1)
    private static AirForceManager instance = new AirForceManager();
    private boolean initialized = false;
    private int strikeLevelStartFrame = CommonCode.NONE;
    private boolean goliathDiscovered = false;
    private int strikeLevel = AirForceManager.StrikeLevel.CRITICAL_SPOT;
    private boolean disabledAutoAdjustment = false;
    private BaseLocation firstBase = null; // 시작 공격 베이스
    private BaseLocation secondBase = null; // 다음 공격 베이스
    private Position retreatPosition = null; // 후퇴지점1
    private List<Position> targetPositions = new ArrayList<>(); // 타깃포지션
    private Map<Integer, AirForceTeam> airForceTeamMap = new HashMap<>(); // key : wraith ID
    private int achievementEffectiveFrame = 0; // 일정기간 안에서의 성취 (공격성 레벨 조절)
    private int accumulatedAchievement = 0; // 누적된 총 성취 (레이쓰 숫자 조절. 조절되면 값 리셋)
    private boolean airForceDefenseMode = false;
    private int waitingEndFrame = 0;
    private int offensePositionResetFrame = 0;
//	private Position retreatPositionLeft = null; // 후퇴지점2
//	private Position retreatPositionRight = null; // 후퇴지점3

    /// static singleton 객체를 리턴합니다
    public static AirForceManager Instance() {
        return instance;
    }

    public boolean airForceManagerInitialized() {
        return initialized;
    }

    public int getStrikeLevel() {
        return strikeLevel;
    }

    public void setStrikeLevel(int strikeLevel) {
        this.strikeLevel = strikeLevel;
    }

    public boolean isDisabledAutoAdjustment() {
        return disabledAutoAdjustment;
    }

    public void setDisabledAutoAdjustment(boolean disabledAutoAdjustment) {
        this.disabledAutoAdjustment = disabledAutoAdjustment;
    }

    public Position getRetreatPosition() {
        return retreatPosition;
    }

    public List<Position> getTargetPositions() {
        return targetPositions;
    }

    public Map<Integer, AirForceTeam> getAirForceTeamMap() {
        return airForceTeamMap;
    }

    public int getAchievementEffectiveFrame() {
        return achievementEffectiveFrame;
    }

    public int getAccumulatedAchievement() {
        return accumulatedAchievement;
    }

    public boolean isAirForceDefenseMode() {
        // 적 공격 수비 또는 적 베이스를 발견못한 경우 임시 defenseMode이다.
        // 이 때의 targetPosition.size는 1이다.
        if (targetPositions.size() <= 1) {
            return true;
        }
        return airForceDefenseMode;
    }

    public void setAirForceDefenseMode(boolean airForceDefenseMode) {
        this.airForceDefenseMode = airForceDefenseMode;
    }

    public void update() {
        if (!UnitUtils.myCompleteUnitDiscovered(UnitType.Terran_Wraith)) {
            return;
        }
        if (!initialized) {
//			System.out.println("AirForceManager first update started");
        }

        defenseModeChange();
        setTargetPosition();
        changeAirForceTeamTargetPosition();
        adjustStrikeLevel();
        adjustWraithCount();

        if (!initialized) {
//			System.out.println("AirForceManager first update finished");
            initialized = true;
        }
    }

    private void defenseModeChange() {
        List<Unit> wraithList = UnitUtils.getCompletedUnitList(UnitType.Terran_Wraith);
        List<UnitInfo> airEuiList = new ArrayList<>();
        if (PlayerUtils.enemyRace() == Race.Zerg) {
            airEuiList = UnitUtils.getEnemyUnitInfoList(EnemyUnitVisibleStatus.ALL, UnitType.Zerg_Mutalisk, UnitType.Zerg_Scourge, UnitType.Zerg_Devourer);
        } else if (PlayerUtils.enemyRace() == Race.Terran) {
            airEuiList = UnitUtils.getEnemyUnitInfoList(EnemyUnitVisibleStatus.ALL, UnitType.Terran_Wraith, UnitType.Terran_Valkyrie);
        } else if (PlayerUtils.enemyRace() == Race.Protoss) {
            airEuiList = UnitUtils.getEnemyUnitInfoList(EnemyUnitVisibleStatus.ALL, UnitType.Protoss_Scout, UnitType.Protoss_Corsair);
        }

        int powerOfAirForce = WraithFightPredictor.powerOfAirForce(wraithList, false);
        int powerOfEnemies = WraithFightPredictor.powerOfEnemies(airEuiList);

        if (airForceDefenseMode) { // 방어에서 공격으로 바꿀땐 충분한 힘을 모으고 나가라
            powerOfEnemies += 200;
        }
        // System.out.println("airforce defense mode = " + powerOfAirForce + " / " + powerOfEnemies);
        if (powerOfAirForce > powerOfEnemies) { // airBattlePredict
            if (TimeUtils.before(waitingEndFrame)) { // 역레이스 준비시간
                int myTankCount = UnitUtils.getUnitCount(UnitFindStatus.COMPLETE, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode);
                int myWraithCount = UnitUtils.getUnitCount(UnitFindStatus.COMPLETE, UnitType.Terran_Wraith);

                if (myTankCount < 8) { // 탱크가 줄어들었다면 즉시 출발
                    waitingEndFrame = TimeUtils.getFrame();
                    airForceDefenseMode = false;
                } else if (myWraithCount >= 8) { // 레이쓰가 충분히 모였다면 즉시 출발
                    waitingEndFrame = TimeUtils.getFrame();
                    airForceDefenseMode = false;
                } else {
                    airForceDefenseMode = true;
                }

            } else {
                airForceDefenseMode = false;
            }
        } else {
            airForceDefenseMode = true;
        }
    }

    public void setAirForceWaiting() {
        if (TimeUtils.before(waitingEndFrame)) {
            return;
        }

        StrategyBoard.wraithCount = 8;
        waitingEndFrame = TimeUtils.getFrame() + UnitType.Terran_Wraith.buildTime() * 5; // 투스타 기준 8마리 채우는데에 1마리 여유시간
    }

    private void setTargetPosition() {
        boolean defenseMode = airForceDefenseMode;
        if (!defenseMode) {
            if (firstBase == null || secondBase == null || targetPositions.isEmpty()) {
                BaseLocation enemyBase = BaseUtils.enemyMainBase();
                BaseLocation enemyFirstExpansion = BaseUtils.enemyFirstExpansion();
                if (enemyBase == null || enemyFirstExpansion == null) {
                    defenseMode = true;
                }
            }

            if (StrategyBoard.nearAirEnemyPosition != Position.Unknown) {
                defenseMode = true;
            }
        }

        if (defenseMode) {
            setDefensePositions();
            return;
        }

        // base가 변경된 경우
        boolean enemyBaseFirstCase = BaseUtils.enemyMainBase().equals(firstBase) && BaseUtils.enemyFirstExpansion().equals(secondBase);
        boolean enemyExpansionFirstCase = BaseUtils.enemyFirstExpansion().equals(firstBase) && BaseUtils.enemyMainBase().equals(secondBase);
        if ((!enemyBaseFirstCase && !enemyExpansionFirstCase)
                || TimeUtils.getFrame(offensePositionResetFrame) > 30 * TimeUtils.SECOND) {
            setOffensePositions();
//			this.setRetreatPosition();

            offensePositionResetFrame = TimeUtils.getFrame();
        }
    }

    private void setDefensePositions() {
        firstBase = secondBase = null;
        targetPositions.clear();

        setDefensivePosition();
        this.retreatPosition = StrategyBoard.campPosition;
        AirForceManager.airForceTargetPositionSize = targetPositions.size();
//		this.setRetreatPosition();
    }

    private void setOffensePositions() {
        firstBase = secondBase = null;
        targetPositions.clear();

        setOffensivePosition();
        setRetreatPositionForUseMapSetting();
        AirForceManager.airForceTargetPositionSize = targetPositions.size();
    }

    private void setDefensivePosition() {
        if (StrategyBoard.nearAirEnemyPosition != Position.Unknown) {
            targetPositions.add(StrategyBoard.nearAirEnemyPosition);
        }
//		if (StrategyBoard.enemyGroundSquadPosition != Position.Unknown) {
//			targetPositions.add(StrategyBoard.enemyGroundSquadPosition);
//		}
        targetPositions.add(StrategyBoard.mainSquadCenter);
//		targetPositions.add(StrategyBoard.mainPosition);
    }

    private void setOffensivePosition() {
        /// 첫번째 공격 base, 마지막 공격 base를 지정하고, 중간 포지션을 설정한다.
        double airDistanceToBase = BaseUtils.myMainBase().getAirDistance(BaseUtils.enemyMainBase());
        double airDistanceToExpansion = BaseUtils.myMainBase().getAirDistance(BaseUtils.enemyFirstExpansion());

        boolean expansionFirst = false;
        if (airDistanceToBase < airDistanceToExpansion) {
            expansionFirst = false; // 본진먼저
            firstBase = BaseUtils.enemyMainBase();
            secondBase = BaseUtils.enemyFirstExpansion();
        } else {
            expansionFirst = true; // 앞마당먼저
            firstBase = BaseUtils.enemyFirstExpansion();
            secondBase = BaseUtils.enemyMainBase();
        }

        int vectorX = secondBase.getPosition().getX() - firstBase.getPosition().getX();
        int vectorY = secondBase.getPosition().getY() - firstBase.getPosition().getY();

        double vectorXSegment = vectorX / (AIR_FORCE_TARGET_MIDDLE_POSITION_SIZE + 1);
        double vectorYSegment = vectorY / (AIR_FORCE_TARGET_MIDDLE_POSITION_SIZE + 1);

        if (strikeLevel < AirForceManager.StrikeLevel.CRITICAL_SPOT && expansionFirst) {
            addAirForceTargetPositions(getMineralPositions());
        }

        addAirForceTargetPositions(firstBase.getPosition());
        for (int index = 0; index < AIR_FORCE_TARGET_MIDDLE_POSITION_SIZE; index++) {
            int resultX = firstBase.getPosition().getX() + (int) (vectorXSegment * (index + 1));
            int resultY = firstBase.getPosition().getY() + (int) (vectorYSegment * (index + 1));

            addAirForceTargetPositions(new Position(resultX, resultY).makeValid());
        }
        addAirForceTargetPositions(secondBase.getPosition());

        if (strikeLevel < AirForceManager.StrikeLevel.CRITICAL_SPOT && !expansionFirst) {
            addAirForceTargetPositions(getMineralPositions());
        }

        if (PlayerUtils.enemyRace() == Race.Terran) {
            addAirForceTargetPositions(baseSidePosition());
        }

        List<BaseLocation> occupiedBases = BaseUtils.enemyOccupiedBases();
        for (BaseLocation base : occupiedBases) {
            if (base.equals(BaseUtils.enemyMainBase())
                    || base.equals(BaseUtils.enemyFirstExpansion())) {
                continue;
            }

            addAirForceTargetPositions(base.getPosition());

            Position positionUp = new Position(base.getPosition().getX(), base.getPosition().getY() - 300);
            Position positionDown = new Position(base.getPosition().getX(), base.getPosition().getY() + 300);
            Position positionLeft = new Position(base.getPosition().getX() - 300, base.getPosition().getY());
            Position positionRight = new Position(base.getPosition().getX() + 300, base.getPosition().getY());

            addAirForceTargetPositions(positionUp, positionDown, positionLeft, positionRight);
        }

        if (TimeUtils.afterTime(8, 0) && PlayerUtils.enemyRace() == Race.Terran) {
            Position position = BaseUtils.myMainBase().getPosition();
            UnitInfo closeTankInfo = null;
            double closestDistance = CommonCode.DOUBLE_MAX;

            List<UnitInfo> tankInfoList = UnitUtils.getEnemyUnitInfoList(EnemyUnitVisibleStatus.VISIBLE, UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_Siege_Tank_Tank_Mode);
            for (UnitInfo eui : tankInfoList) {
                double distance = eui.getLastPosition().getDistance(position);

                if (distance < closestDistance) {
                    closeTankInfo = eui;
                    closestDistance = distance;
                }
            }

            if (closeTankInfo != null) {
                addAirForceTargetPositions(closeTankInfo.getLastPosition());
            }
        }
    }

    private List<Position> getMineralPositions() {
        Position enemyBasePosition = BaseUtils.enemyMainBase().getPosition();
        Position enemyFirstExpansionPosition = BaseUtils.enemyFirstExpansion().getPosition();

        int[] angles;
        if (!PositionFinder.Instance().enemyBaseDestroyed(BaseUtils.enemyFirstExpansion())) {
            angles = MicroConfig.Angles.AIRFORCE_MINERAL_TARGET_ANGLE_1;
        } else {
            angles = MicroConfig.Angles.AIRFORCE_MINERAL_TARGET_ANGLE_2;
        }

        List<Position> positions = new ArrayList<>();
        double radian = MicroUtils.targetDirectionRadian(enemyBasePosition, enemyFirstExpansionPosition);
        for (int angle : angles) {
            double rotateAngle = MicroUtils.rotate(radian, angle);
            Position mineralPosition = MicroUtils.getMovePosition(enemyFirstExpansionPosition, rotateAngle, 300).makeValid();
            if (PositionUtils.isValidPosition(mineralPosition)) {
                positions.add(mineralPosition);
            }
        }
        return positions;
    }

    private Position baseSidePosition() {
        Position enemyBasePosition = BaseUtils.enemyMainBase().getPosition();
        Position enemyFirstExpansionPosition = BaseUtils.enemyFirstExpansion().getPosition();

        double radian = MicroUtils.targetDirectionRadian(enemyBasePosition, enemyFirstExpansionPosition);
        double radian90plus = MicroUtils.rotate(radian, +90);
        double radian90minus = MicroUtils.rotate(radian, -90);

        Position position90plus = MicroUtils.getMovePosition(enemyBasePosition, radian90plus, 400);
        Position position90minus = MicroUtils.getMovePosition(enemyBasePosition, radian90minus, 400);

        if (!PositionUtils.isValidPosition(position90plus)) {
            return position90minus.makeValid();
        }
        if (!PositionUtils.isValidPosition(position90minus)) {
            return position90plus.makeValid();
        }

        double distance90plus = position90plus.getDistance(enemyFirstExpansionPosition);
        double distance90minus = position90minus.getDistance(enemyFirstExpansionPosition);

        if (distance90plus < distance90minus) {
            return position90plus;
        } else {
            return position90minus;
        }
    }

    private void setRetreatPosition() {
        Position enemyExpansionPosition = BaseUtils.enemyFirstExpansion().getPosition();

        Position retreatPosition = null;
        double minimumDistance = CommonCode.DOUBLE_MAX;
        for (BaseLocation baseLocation : BWTA.getStartLocations()) {
            if (baseLocation.getPosition().equals(BaseUtils.enemyMainBase().getPosition())) {
                continue;
            }

            double distance = baseLocation.getDistance(enemyExpansionPosition);
            if (distance < minimumDistance) {
                minimumDistance = distance;
                retreatPosition = baseLocation.getPosition();
            }
        }
        this.retreatPosition = retreatPosition;
//		BaseLocation enemyMainBase = BaseUtils.enemyMainBase();
//		int x = enemyMainBase.getX() / 500;
//		int y = enemyMainBase.getY() / 500;
//
//		int width = Prebot.Broodwar.mapWidth() * 32 - 1;
//		int height = Prebot.Broodwar.mapHeight() * 32 - 1;
//
//		if (x == 0 && y == 0) {
//			retreatPositionLeft = new Position(0, height);
//			retreatPositionRight = new Position(width, 0);
//		} else if (x > 0 && y == 0) {
//			retreatPositionLeft = new Position(0, 0);
//			retreatPositionRight = new Position(width, height);
//		} else if (x > 0 && y > 0) {
//			retreatPositionLeft = new Position(width, 0);
//			retreatPositionRight = new Position(0, height);
//		} else if (x == 0 && y > 0) {
//			retreatPositionLeft = new Position(width, height);
//			retreatPositionRight = new Position(0, 0);
//		}
    }

    private void setRetreatPositionForUseMapSetting() {
        int width = MapUtils.mapWidth() * 32 - 1;
        int height = MapUtils.mapHeight() * 32 - 1;

        List<Position> positions = new ArrayList<>();
        positions.add(new Position(0, 0));
        positions.add(new Position(0, height));
        positions.add(new Position(width, 0));
        positions.add(new Position(width, height));

        Position enemyExpansionPosition = BaseUtils.enemyFirstExpansion().getPosition();

        Position retreatPosition = null;
        double minimumDistance = CommonCode.DOUBLE_MAX;
        for (Position position : positions) {
            if (position.getDistance(BaseUtils.enemyMainBase().getPosition()) < 1000) {
                continue;
            }

            double distance = position.getDistance(enemyExpansionPosition);
            if (distance < minimumDistance) {
                minimumDistance = distance;
                retreatPosition = position;
            }
        }
        this.retreatPosition = retreatPosition;
    }

    private void changeAirForceTeamTargetPosition() {
        for (AirForceTeam airForceTeam : airForceTeamMap.values()) {
            Position targetPosition = airForceTeam.getTargetPosition();
            if (targetPosition == null) {
                continue;
            }

            Set<UnitInfo> enemyDefTowerList = UnitUtils.getCompleteEnemyInfosInRadiusForAir(targetPosition, 20, UnitTypeUtils.enemyAirDefenseUnitType());
            if (enemyDefTowerList.isEmpty()) {
                continue;
            }
            airForceTeam.changeTargetIndex();
        }
    }

    private void adjustStrikeLevel() {
        if (isAirForceDefenseMode()) {
            strikeLevel = AirForceManager.StrikeLevel.DEFENSE_MODE;
            return;
        }

        // TODO 디버깅용 코드 추후 삭제 필요
        if (disabledAutoAdjustment) {
            return;
        }

        boolean levelDown = false;
        boolean levelUp = false;
        if (strikeLevelStartFrame == CommonCode.NONE
                || AirForceManager.StrikeLevel.SORE_SPOT >= strikeLevel && achievementEffectiveFrame > 0) {
            strikeLevelStartFrame = TimeUtils.getFrame();
        }

        int airunitCount = UnitUtils.getUnitCount(UnitFindStatus.COMPLETE, UnitType.Terran_Wraith);
        if (strikeLevel == AirForceManager.StrikeLevel.CRITICAL_SPOT) {
            if (PlayerUtils.enemyRace() == Race.Terran) {
                if (TimeUtils.getFrame(strikeLevelStartFrame) > 50 * TimeUtils.SECOND) { // 레이쓰가 활동한지 일정시간 지남
                    levelDown = true;
                } else if (UnitUtils.enemyCompleteUnitDiscovered(UnitType.Terran_Wraith, UnitType.Terran_Goliath, UnitType.Terran_Armory, UnitType.Terran_Medic)) { // 골리앗 발견, 완성된 아모리 발견
                    levelDown = true;
                }
            } else if (PlayerUtils.enemyRace() == Race.Zerg) {
                if (TimeUtils.getFrame(strikeLevelStartFrame) > 100 * TimeUtils.SECOND) { // 레이쓰가 활동한지 일정시간 지남
                    levelDown = true;
                } else if (UnitUtils.enemyCompleteUnitDiscovered(UnitType.Zerg_Hydralisk)) { // 히드라 발견
                    levelDown = true;
                }
            } else if (PlayerUtils.enemyRace() == Race.Protoss) {
                levelDown = true;
            }

        } else if (strikeLevel == AirForceManager.StrikeLevel.SORE_SPOT) {
            // TODO 레이쓰가 일정 수 파괴되었을 때로 할지 고민
            int levelDownSeconds = Math.max(10 - airunitCount, 1);
            if (TimeUtils.getFrame(strikeLevelStartFrame) > levelDownSeconds * TimeUtils.SECOND) {
                levelDown = true;
            } else if (achievementEffectiveFrame <= -50) {
                levelDown = true;
            }

        } else if (strikeLevel == AirForceManager.StrikeLevel.POSSIBLE_SPOT) {
            if (achievementEffectiveFrame <= -100) { // defense 모드로 변경
                levelDown = true;
            } else if (achievementEffectiveFrame >= 150) {
                levelUp = true;
            }
        } else if (strikeLevel == AirForceManager.StrikeLevel.DEFENSE_MODE) {
            levelUp = true;
        }

        if (levelDown) {
            strikeLevel--;
            strikeLevelStartFrame = TimeUtils.getFrame();
            setOffensePositions();

        } else if (levelUp) {
            strikeLevel++;
            strikeLevelStartFrame = TimeUtils.getFrame();
            setOffensePositions();
        }
    }

    // 레이쓰 출산전략 : 유지숫자가 커지면 증가율을 낮추고, 감소율을 높힌다.
    private void adjustWraithCount() {
        if (!goliathDiscovered && UnitUtils.enemyCompleteUnitDiscovered(UnitType.Terran_Goliath)) {
            goliathDiscovered = true;
            if (StrategyBoard.wraithCount > 4) {
                StrategyBoard.wraithCount = 4;
            }
        }

        boolean suppress = false;
        if (PlayerUtils.enemyRace() == Race.Terran) {
            if (StrategyBoard.mainSquadMode.isAttackMode || UnitUtils.enemyUnitDiscovered(UnitType.Terran_Battlecruiser, UnitType.Terran_Physics_Lab)) {
                suppress = false;
            } else {
                suppress = true;
            }
        }

        int downAchievement;
        int upAchievement;
        if (StrategyBoard.wraithCount > 0 && StrategyBoard.wraithCount < 6) {
            downAchievement = -90;
            if (PlayerUtils.enemyRace() == Race.Zerg) {
                upAchievement = +150;
            } else {
                upAchievement = +120;
            }
        } else if (StrategyBoard.wraithCount >= 6 && StrategyBoard.wraithCount < 12) {
            downAchievement = -70;
            if (PlayerUtils.enemyRace() == Race.Zerg) {
                upAchievement = +300;
            } else {
                upAchievement = +180;
            }
        } else if (StrategyBoard.wraithCount >= 12 && StrategyBoard.wraithCount < 14) { //24) {
            downAchievement = -50;
            if (PlayerUtils.enemyRace() == Race.Zerg) {
                upAchievement = +600;
            } else {
                upAchievement = +200;
            }
        } else {
            return;
        }

        if (suppress) {
            upAchievement *= 5;
        }

        int wraithCount = UnitUtils.getUnitCount(UnitFindStatus.ALL, UnitType.Terran_Wraith);

        // 실제 레이쓰 수와 유지 수가 너무 큰 차이가 나지 않도록 한다.
        int maxWraitCount = Math.min(wraithCount + 2, 8);
        int minWraitCount = Math.max(wraithCount - 2, 0);

        if (accumulatedAchievement <= downAchievement) {
            if (StrategyBoard.wraithCount > minWraitCount) {
                StrategyBoard.wraithCount--;
            }
            accumulatedAchievement = 0;

        } else if (accumulatedAchievement >= upAchievement) {
            if (StrategyBoard.wraithCount < maxWraitCount) {
                StrategyBoard.wraithCount++;
            }
            accumulatedAchievement = 0;
        }


//		if (PlayerUtils.enemyRace() == Race.Terran) {
//			StrategyBoard.wraithCount = 4;
//
//		} else if (PlayerUtils.enemyRace() == Race.Zerg) {
//			StrategyBoard.wraithCount = 4;
//
//		} else if (PlayerUtils.enemyRace() == Race.Protoss) {
//			StrategyBoard.wraithCount = 0;
//		}
    }

    /// update air force team
    public void updateAirForceTeam(Collection<Unit> airunits) {

        // remove airunit of invalid team
        List<Integer> invalidUnitIds = new ArrayList<>();

        for (Integer airunitId : airForceTeamMap.keySet()) {
            Unit airunit = UnitUtils.enemyUnitInSight(airunitId);
            if (!UnitUtils.isCompleteValidUnit(airunit)) {
                invalidUnitIds.add(airunitId);
            } else if (airForceTeamMap.get(airunitId).leaderUnit == null) {
                invalidUnitIds.add(airunitId);
            }
        }
        for (Integer invalidUnitId : invalidUnitIds) {
            airForceTeamMap.remove(invalidUnitId);
        }

        // new team
        for (Unit airunit : airunits) {
            AirForceTeam teamOfAirunit = airForceTeamMap.get(airunit.getID());
            if (teamOfAirunit == null) {
                airForceTeamMap.put(airunit.getID(), new AirForceTeam(airunit));
            }
        }

        // 리더의 위치를 비교하여 합칠 그룹인지 체크한다.
        // - 클로킹모드 상태가 다른 그룹은 합치지 않는다.
        // - 수리 상태의 그룹은 합치지 않는다.
        List<AirForceTeam> airForceTeamList = new ArrayList<>(new HashSet<>(airForceTeamMap.values()));
        Map<Integer, Integer> airForceTeamMergeMap = new HashMap<>(); // key:merge될 그룹 leaderID, value:merge할 그룹 leaderID

        for (int i = 0; i < airForceTeamList.size(); i++) {
            AirForceTeam airForceTeam = airForceTeamList.get(i);
            if (airForceTeam.repairCenter != null) {
                continue;
            }

            boolean cloakingMode = airForceTeam.cloakingMode;
            for (int j = i + 1; j < airForceTeamList.size(); j++) {
                AirForceTeam compareForceUnit = airForceTeamList.get(j);
                if (compareForceUnit.repairCenter != null) {
                    continue;
                }
                if (cloakingMode != compareForceUnit.cloakingMode) { // 클로킹상태가 다른 레이쓰부대는 합쳐질 수 없다.
                    continue;
                }

                Unit airForceLeader = airForceTeam.leaderUnit;
                Unit compareForceLeader = compareForceUnit.leaderUnit;
                if (airForceLeader.getID() == compareForceLeader.getID()) {
                    System.out.println("no sense. the same id = " + airForceLeader.getID());
                    continue;
                }
                if (airForceLeader.getDistance(compareForceLeader) <= AIR_FORCE_TEAM_MERGE_DISTANCE) {
                    airForceTeamMergeMap.put(compareForceLeader.getID(), airForceLeader.getID());
                }
            }
        }

        // 합쳐지는 에어포스팀의 airForceTeamMap을 재설정한다.
        for (Unit airunit : airunits) {
            Integer fromForceUnitLeaderId = airForceTeamMap.get(airunit.getID()).leaderUnit.getID();
            Integer toForceUnitLeaderId = airForceTeamMergeMap.get(fromForceUnitLeaderId);
            if (toForceUnitLeaderId != null) {
                airForceTeamMap.put(airunit.getID(), airForceTeamMap.get(toForceUnitLeaderId));
            }
        }

        // team 멤버 세팅
        Set<AirForceTeam> airForceTeamSet = new HashSet<>(airForceTeamMap.values());
        for (AirForceTeam airForceTeam : airForceTeamSet) {
            airForceTeam.memberList.clear();
        }

        List<Integer> needRepairAirunitList = new ArrayList<>(); // 치료가 필요한 유닛
        Map<Integer, Unit> airunitRepairCenterMap = new HashMap<>(); // 치료받을 커맨드센터
        List<Integer> uncloakedAirunitList = new ArrayList<>(); // 언클락 유닛

        for (Integer airunitId : airForceTeamMap.keySet()) {
            Unit airunit = UnitUtils.enemyUnitInSight(airunitId);
            if (airunit.getHitPoints() <= 50) { // repair hit points
                AirForceTeam repairTeam = airForceTeamMap.get(airunitId);
                if (repairTeam == null || repairTeam.repairCenter == null) {
                    Unit repairCenter = UnitUtils.getClosestActivatedCommandCenter(airunit.getPosition());
                    if (repairCenter != null) {
                        needRepairAirunitList.add(airunitId);
                        airunitRepairCenterMap.put(airunitId, repairCenter);
                        continue;
                    }
                }
            }

            AirForceTeam airForceTeam = airForceTeamMap.get(airunit.getID());
            if (airForceTeam.cloakingMode && (airunit.getType() != UnitType.Terran_Wraith || airunit.getEnergy() < 15)) {
                uncloakedAirunitList.add(airunitId);
                continue;
            }

            airForceTeam.memberList.add(airunit);
        }

        // create separated team for no energy airunit
        for (Integer airunitId : uncloakedAirunitList) {
            Unit airunit = UnitUtils.enemyUnitInSight(airunitId);
            AirForceTeam uncloackedForceTeam = new AirForceTeam(airunit);
            uncloackedForceTeam.memberList.add(airunit);

            airForceTeamMap.put(airunitId, uncloackedForceTeam);
        }

        // create repair airforce team
        for (Integer airunitId : needRepairAirunitList) {
            Unit airunit = UnitUtils.enemyUnitInSight(airunitId);
            AirForceTeam needRepairTeam = new AirForceTeam(airunit);
            needRepairTeam.memberList.add(airunit);
            needRepairTeam.repairCenter = airunitRepairCenterMap.get(airunit.getID());

            airForceTeamMap.put(airunitId, needRepairTeam);
        }

        // etc (changing leader, finishing repair, achievement)
        Set<AirForceTeam> reorganizedSet = new HashSet<>(airForceTeamMap.values());
        achievementEffectiveFrame = 0;
        for (AirForceTeam airForceTeam : reorganizedSet) {
            // leader 교체
            Unit newLeader = UnitUtils.getClosestUnitToPosition(airForceTeam.memberList, airForceTeam.getTargetPosition());
            airForceTeam.leaderUnit = newLeader;

            // repair 완료처리
            if (airForceTeam.repairCenter != null) {
                if (!UnitUtils.isValidUnit(airForceTeam.repairCenter) || WorkerManager.Instance().getWorkerData().getNumAssignedWorkers(airForceTeam.repairCenter) < 3) {
                    Unit repairCenter = UnitUtils.getClosestActivatedCommandCenter(airForceTeam.leaderUnit.getPosition());
                    if (repairCenter != null) {
                        airForceTeam.repairCenter = repairCenter;
                    }
                }

                boolean repairComplete = true;
                for (Unit airunit : airForceTeam.memberList) {
                    if (airunit.getHitPoints() < airunit.getType().maxHitPoints() * 0.95) { // repair complete hit points
                        repairComplete = false;
                        break;
                    }
                }
                if (repairComplete) {
                    airForceTeam.repairCenter = null;
                }
            }

            // achievement
            int achievement = airForceTeam.achievement();
            accumulatedAchievement += achievement;
            achievementEffectiveFrame = achievementEffectiveFrame + airForceTeam.killedEffectiveFrame * 100 - airForceTeam.damagedEffectiveFrame;
        }
    }

    public boolean isLeader(int wraithID) {
        Unit leaderUnit = airForceTeamMap.get(wraithID).leaderUnit;
        if (leaderUnit == null) {
            return false;
        } else {
            return leaderUnit.getID() == wraithID;
        }
    }

    public AirForceTeam airForTeamOfUnit(int unitID) {
        return airForceTeamMap.get(unitID);
    }

    private void addAirForceTargetPositions(Position... positions) {
        for (Position position : positions) {
            if (PositionUtils.isValidPosition(position)) {
                targetPositions.add(position);
            }
        }
    }

    private void addAirForceTargetPositions(List<Position> positions) {
        for (Position position : positions) {
            if (PositionUtils.isValidPosition(position)) {
                targetPositions.add(position);
            }
        }
    }

    public static class StrikeLevel {
        public static final int CRITICAL_SPOT = 3; // 때리면 죽는 곳 공격 (터렛건설중인 SCV, 아모리 건설중인 SCV, 엔지니어링베이 건설중인 SCV)
        public static final int SORE_SPOT = 2; // 때리면 아픈 곳 공격 (커맨드센터건설중인 SCV, 팩토리 건설중인 SCV, 뭔가 건설중인 SCV, 체력이 적은 SCV, 가까운 SCV, 탱크)
        public static final int POSSIBLE_SPOT = 1; // 때릴 수 있는 곳 공격 (벌처, 건물 등 잡히는 대로)
        public static final int DEFENSE_MODE = 0;
    }
}
