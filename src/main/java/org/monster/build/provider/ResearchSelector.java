package org.monster.build.provider;


import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import org.monster.build.base.BuildManager;
import org.monster.common.MetaType;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.UnitUtils;
import org.monster.main.Monster;
import org.monster.board.StrategyBoard;

import java.util.List;

//EXAMPLE
@Deprecated
public class ResearchSelector implements Selector<MetaType> {
    public int currentResearched;
    BuildCondition buildCondition;
//    MetaType techType;
//    MetaType upgradeType;
    //BuildCondition buildCondition;
    MetaType metaType;

    public final MetaType getSelected() {
        return metaType;
    }

    public final void select() {
        //metaType = new MetaType(UpgradeType.None);
        //metaType = new MetaType(UpgradeType.None);

        //if (Prebot.Broodwar.getFrameCount() % 43 == 0) {
        metaType = new MetaType();

        executeResearchChk();
        //}


        //buildCondition = new BuildCondition();

    }

    public void executeResearchChk() {

        boolean VS = (Monster.Broodwar.self().getUpgradeLevel(UpgradeType.Ion_Thrusters) == 1 ? true : false)
                || (Monster.Broodwar.self().isUpgrading(UpgradeType.Ion_Thrusters) ? true : false);
        boolean VM = (Monster.Broodwar.self().hasResearched(TechType.Spider_Mines)) || (Monster.Broodwar.self().isResearching(TechType.Spider_Mines));
        boolean TS = (Monster.Broodwar.self().hasResearched(TechType.Tank_Siege_Mode)) || (Monster.Broodwar.self().isResearching(TechType.Tank_Siege_Mode));
        boolean GR = (Monster.Broodwar.self().getUpgradeLevel(UpgradeType.Charon_Boosters) == 1 ? true : false)
                || (Monster.Broodwar.self().isUpgrading(UpgradeType.Charon_Boosters) ? true : false);


        if (VS && VM && TS && GR)
            return; // 4개 모두 완료이면

        currentResearched = 0;
        if (VS) {
            currentResearched++;
        }
        if (VM) {
            currentResearched++;
        }
        if (TS) {
            currentResearched++;
        }
        if (GR) {
            currentResearched++;
        }

//		EnemyStrategyOptions.UpgradeOrder order = StrategyBoard.currentStrategy.upgrade;

        List<Unit> canMachineShop = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Machine_Shop);

        boolean canResearch = false;

        int canMachineShopCnt = 0;

//		현재 큐에 들어있는 개발건수
        int QueueResearch = BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Ion_Thrusters)
                + BuildManager.Instance().buildQueue.getItemCount(TechType.Spider_Mines)
                + BuildManager.Instance().buildQueue.getItemCount(TechType.Tank_Siege_Mode)
                + BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Charon_Boosters);

        for (Unit unit : canMachineShop) {
            if (unit.canUpgrade()) {
                canMachineShopCnt++;

            }
        }

//		비어있는 머신샵이 최소 큐에 들어있는 개발건 보다 많아야 선택
        if (canMachineShopCnt != 0 && QueueResearch < canMachineShopCnt) {
//			System.out.println("QueueResearch : " + QueueResearch + " / canMachineShopCnt : " + canMachineShopCnt);
            canResearch = true;
        }


//		canResearch = true;

        if (canResearch == true) {

            List<MetaType> upgradeOrder = StrategyBoard.upgrade;


            for (MetaType e : upgradeOrder) {
//				System.out.println("upgradeOrder chk==> " + e.getName());
                if (BuildManager.Instance().buildQueue.getItemCount(e) != 0) {
                    continue;
                }
                if (e.isUpgrade()) {
                    if (Monster.Broodwar.self().getUpgradeLevel(e.getUpgradeType()) == 1
                            || Monster.Broodwar.self().isUpgrading(e.getUpgradeType())
                            || BuildManager.Instance().buildQueue.getItemCount(e.getUpgradeType()) != 0
                            ) {
                        continue;
                    } else {
//						System.out.println("upgradeOrder selected ==> " + metaType.getUpgradeType());
                        metaType = e;
                        break;
                    }
                }
                if (e.isTech()) {
                    if (Monster.Broodwar.self().hasResearched(e.getTechType())
                            || Monster.Broodwar.self().isResearching(e.getTechType())
                            || BuildManager.Instance().buildQueue.getItemCount(e.getTechType()) != 0
                            ) {
                        continue;
                    } else {
                        metaType = e;
//						System.out.println("techOrder selected ==> " + metaType.getTechType());
                        break;
                    }
                }

            }

        }
    }
}
