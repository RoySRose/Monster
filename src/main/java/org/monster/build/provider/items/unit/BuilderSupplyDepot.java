package org.monster.build.provider.items.unit;

import bwapi.Unit;
import bwapi.UnitType;
import org.monster.build.base.BuildManager;
import org.monster.build.base.BuildOrderItem;
import org.monster.build.base.BuildOrderQueue;
import org.monster.build.base.ConstructionManager;
import org.monster.build.provider.DefaultBuildableItem;
import org.monster.common.MetaType;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.main.Monster;

import java.util.List;

//EXAMPLE
@Deprecated
public class BuilderSupplyDepot extends DefaultBuildableItem {

    public BuilderSupplyDepot(MetaType metaType) {
        super(metaType);
    }

    public final boolean buildCondition() {

        BuildOrderQueue tempbuildQueue = BuildManager.Instance().getBuildQueue();
        BuildOrderItem checkItem = null;


//    	frame으로 처리되어 있는 이유. initial이 끝난 후로 하면 되지 않나?
//    	일단 기존 조건대로 처리. 셀렉터는 이니셜 빌드 이후에 도므로 아래 조건의 필요 유무 판단
//    	if (!(Prebot.Broodwar.getFrameCount() % 29 == 0 && Prebot.Broodwar.getFrameCount() > 4500)) {
//    		return false;
//    	}

        if (Monster.Broodwar.self().supplyTotal() >= 400) {
            return false;
        }


        if (!tempbuildQueue.isEmpty()) {
            checkItem = tempbuildQueue.getHighestPriorityItem();
            while (true) {
                if (checkItem.blocking == true) {
                    break;
                }
                // if(checkItem.metaType.isUnit() && checkItem.metaType.getUnitType().isAddon()){
                // return;
                // }
                if (checkItem.metaType.isUnit() && checkItem.metaType.getUnitType() == UnitType.Terran_Missile_Turret) {
                    return false;
                }
                if (checkItem.metaType.isUnit() && checkItem.metaType.getUnitType() == UnitType.Terran_Supply_Depot) {
                    return false;
                }
                if (tempbuildQueue.canSkipCurrentItem() == true) {
                    tempbuildQueue.skipCurrentItem();
                } else {
                    break;
                }
                checkItem = tempbuildQueue.getItem();
            }
            if (checkItem.metaType.isUnit() && checkItem.metaType.getUnitType() == UnitType.Terran_Supply_Depot) {
                return false;
            }
        }


        // 서플라이가 다 꽉찼을때 새 서플라이를 지으면 지연이 많이 일어나므로, supplyMargin (게임에서의 서플라이 마진 값의 2배)만큼 부족해지면 새 서플라이를 짓도록 한다
        // 이렇게 값을 정해놓으면, 게임 초반부에는 서플라이를 너무 일찍 짓고, 게임 후반부에는 서플라이를 너무 늦게 짓게 된다
        int supplyMargin = 4;
        boolean barrackflag = false;
        boolean factoryflag = false;
        boolean starportflag = false;
//        int barrackMargin = 4;
//        int facMargin = 2;
//        int satrportMargin = 2;


        if (Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Barracks) > 0) {
            barrackflag = true;
        }

        if (Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Factory) > 0) {
            factoryflag = true;
        }

        if (Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Starport) > 0) {
            starportflag = true;
        }
        
        /*if(factoryflag==false){
            for (Unit unit : Prebot.Broodwar.self().getUnits()) {
                if (unit.getType() == UnitType.Terran_Factory  && unit.isCompleted()) {
                    factoryflag = true;
                }
                if (unit.getType() == UnitType.Terran_Barracks && unit.isCompleted()) {
                    barrackflag = true;
                }
            }
        }*/

        int Faccnt = 0;
        int Starportcnt = 0;
        int CCcnt = Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Command_Center);
        int facFullOperating = 0;
        int starportOperating = 0;

//        Factory 와 Starport 에서 유닛이 생산되는중인지 체크.
//        기본적으로 유닛생산 건물수 만큼의 여유분이 있어야 하고, 현재 생산되고 있는 유닛만큼 여유분이 더 있어야 한다.

        List<Unit> factory = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Factory);
        for (Unit unit : factory) {

            Faccnt++;
            if (unit.isTraining() == true) {
                facFullOperating++;
            }

        }

        List<Unit> starport = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Starport);
        for (Unit unit : starport) {

            Starportcnt++;
            if (unit.isTraining() == true) {
                starportOperating++;
            }

        }

        if (CCcnt == 1) {
            supplyMargin = 4;
            if (barrackflag == true) {
                supplyMargin++;
            }
            if (factoryflag == true) {
                supplyMargin = supplyMargin + 2 + (4 * Faccnt) + (facFullOperating * 2);
            }
            if (starportflag == true) {
                if (factoryflag == false) {
                    supplyMargin = supplyMargin + 2 + (4 * Starportcnt) + (starportOperating * 2);
                } else {
                    supplyMargin = supplyMargin + (4 * Starportcnt) + (starportOperating * 2);
                }
            }
        } else { //if((TimeUtils.getFrame()>=6000 && TimeUtils.getFrame()<10000) || (Faccnt > 3 && CCcnt == 2)){
            supplyMargin = 11 + 4 * Faccnt + facFullOperating * 2;
        }

        // currentSupplyShortage 를 계산한다
        int currentSupplyShortage = Monster.Broodwar.self().supplyUsed() + supplyMargin + 1 - Monster.Broodwar.self().supplyTotal();

        if (currentSupplyShortage > 0) {
            // 생산/건설 중인 Supply를 센다
            int onBuildingSupplyCount = 0;
            // 저그 종족이 아닌 경우, 건설중인 Protoss_Pylon, Terran_Supply_Depot 를 센다. Nexus, Command Center 등 건물은 세지 않는다
            onBuildingSupplyCount += ConstructionManager.Instance().getConstructionQueueItemCount(
                    UnitType.Terran_Supply_Depot, null)
                    * UnitType.Terran_Supply_Depot.supplyProvided();

            if (currentSupplyShortage > onBuildingSupplyCount) {
                setHighPriority(true);
                setSeedPositionStrategy(BuildOrderItem.SeedPositionStrategy.NextSupplePoint);
                //this.setSeedPositionStrategy(BuildOrderItem.SeedPositionStrategy.NextSupplePoint);
                //System.out.println("return supply true");
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean checkInitialBuild() {
        return TimeUtils.afterTime(3, 0);
    }
}
