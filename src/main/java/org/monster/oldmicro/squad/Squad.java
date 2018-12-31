package org.monster.oldmicro.squad;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import org.monster.common.LagObserver;
import org.monster.common.UnitInfo;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.debugger.BigWatch;
import org.monster.oldmicro.CombatManager;
import org.monster.oldmicro.constant.MicroConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Squad {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Set<Unit> unitList = new HashSet<>();
    public Set<UnitInfo> euiList = new HashSet<>();
    protected UnitType[] unitTypes;
    private int squadExecutedFrame;
    private String squadName;

    public Squad(MicroConfig.SquadInfo squadInfo) {
        this.squadName = squadInfo.squadName;
    }

    // private List<Unit> unitOldBies = new ArrayList<>();
    // private List<Unit> unitNewBies = new ArrayList<>();

    public Squad(MicroConfig.SquadInfo squadInfo, Position targetPosition) {
        this.squadName = squadInfo.squadName + "P" + targetPosition.toString();
    }

    public Squad(MicroConfig.SquadInfo squadInfo, Unit unit) {
        this.squadName = squadInfo.squadName + "U" + unit.getID();
    }

    public boolean squadExecuted() {
        return squadExecutedFrame == TimeUtils.getFrame();
    }

    public void setUnitType(UnitType... unitTypes) {
        this.unitTypes = unitTypes;
    }

    public UnitType[] getUnitTypes() {
        return unitTypes;
    }

    public String getSquadName() {
        return squadName;
    }

    public boolean hasUnit(Unit unit) {
        return unitList.contains(unit);
    }

    public void update() {
    }

    public abstract boolean want(Unit unit);

    /// 스쿼드 업데이트
    public abstract List<Unit> recruit(List<Unit> assignableUnitList);

    /// squad 실행
    public void findEnemiesAndExecuteSquad() {
        if (squadExecutedFrame == TimeUtils.getFrame()) {
//			logger.debug("ALREADY EXECUTED SQUAD - " + squadName);
            return;
        }
        BigWatch.start("findEnemies - " + squadName);
        findEnemies();
        BigWatch.record("findEnemies - " + squadName);

        if (!squadExecuted()) {
            BigWatch.start("squadExecution - " + squadName);
        }
        execute();
        if (!squadExecuted()) {
            BigWatch.record("squadExecution - " + squadName);
        }

        squadExecutedFrame = TimeUtils.getFrame();
    }

    public abstract void execute();

    /// 유효하지 않은 유닛(죽은 유닛 등)을 리턴
    public List<Unit> invalidUnitList() {
        List<Unit> invalidUnitList = new ArrayList<>();
        for (Unit unit : unitList) {
            if (!UnitUtils.isCompleteValidUnit(unit) || !want(unit)) {
                invalidUnitList.add(unit);
            }
        }
        return invalidUnitList;
    }

    /// 적 탐색
    protected void findEnemies() {


        euiList.clear();

        if (LagObserver.groupsize() > 10) {

            for (Unit unit : unitList) {
                if (!TimeUtils.isExecuteFrame(unit, LagObserver.groupsize())) {
                    continue;
                }
                UnitUtils.addEnemyUnitInfosInRadiusForGround(euiList, unit.getPosition(), unit.getType().sightRange());
            }
        } else {

//            for (Unit unit : unitList) {
//                logger.debug("555");
//                UnitUtils.addEnemyUnitInfosInRadiusForGround(euiList, unit.getPosition(), unit.getType().sightRange());
//            }
        }

    }

    protected Set<UnitInfo> getMainSquadEnemies() {
        MainAttackSquad mainSquad = (MainAttackSquad) CombatManager.Instance().squadData.getSquad(MicroConfig.SquadInfo.MAIN_ATTACK.squadName);
        if (mainSquad.squadExecuted()) {
            return mainSquad.euiList;
        } else {
            logger.debug("#### SOMETHING'S WRONG!!! MAIN SQUAD'S EUILIST MUST NOT BE EMPTY ####");
            return null;
        }
    }

}
