package org.monster.build.provider;


import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import org.monster.build.base.BuildManager;
import org.monster.common.MetaType;
import org.monster.common.constant.CommonCode;
import org.monster.common.util.UnitUtils;
import org.monster.main.Monster;
import org.monster.decisions.strategy.manage.AttackExpansionManager;

//EXAMPLE
@Deprecated
public class UpgradeSelector implements Selector<MetaType> {

    MetaType metaType;
    //BuildCondition buildCondition;

    public final MetaType getSelected() {
        return metaType;
    }

    public final void select() {
        // 업그레이드
        executeUpgrade();

        //buildCondition = new BuildCondition();
        //metaType = new MetaType(UpgradeType.Terran_Infantry_Armor);


    }


    //EnemyStrategyOptions.UpgradeOrder.VM_TS_VS;


    private void executeUpgrade() {

        Unit armory = null;
        for (Unit unit : UnitUtils.getUnitList(CommonCode.UnitFindStatus.COMPLETE, UnitType.Terran_Armory)) {
            if (unit.getType() == UnitType.Terran_Armory) {
                armory = unit;
            }
        }
        if (armory == null) {
            return;
        }

        boolean standard = false;
        int unitPoint = AttackExpansionManager.Instance().unitPoint;

        int myFactoryUnitSupplyCount = UnitUtils.myFactoryUnitSupplyCount();
        if (myFactoryUnitSupplyCount > 42 || (unitPoint > 10 && myFactoryUnitSupplyCount > 30)) {
            standard = true;
        }
        if (unitPoint < -10 && myFactoryUnitSupplyCount < 60) {
            standard = false;
        }

        //공공방공방방

        if (Monster.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Weapons) < 2) {
            // Fac Unit 18 마리 이상 되면 1단계 업그레이드 시도
            if (Monster.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Weapons) == 0 && standard && armory.canUpgrade(UpgradeType.Terran_Vehicle_Weapons)
                    && Monster.Broodwar.self().minerals() > 100 && Monster.Broodwar.self().gas() > 100) {
                if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Weapons) == 0) {
                    //BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Weapons, false);
                    metaType = new MetaType(UpgradeType.Terran_Vehicle_Weapons);
                }
            }
            // Fac Unit 30 마리 이상, 사이언스 퍼실리티가 있고, 일정 이상의 자원 2단계
            //		else if (Prebot.Broodwar.self().minerals() > 250 && Prebot.Broodwar.self().gas() > 225 && Prebot.Broodwar.self().completedUnitCount(UnitType.Terran_Science_Facility) > 0) {
            else if (Monster.Broodwar.self().minerals() > 400 && Monster.Broodwar.self().gas() > 300 && Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Science_Facility) > 0) {
                if ((Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Command_Center) >= 2 && myFactoryUnitSupplyCount > 140)
                        || (Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Command_Center) >= 3 && myFactoryUnitSupplyCount > 80)) {

                    if (Monster.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Weapons) == 1 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Weapons)) {
                        if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Weapons) == 0) {
                            //BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Weapons, false);
                            metaType = new MetaType(UpgradeType.Terran_Vehicle_Weapons);
                        }
                    }
                }
            }
        } else {

            if (Monster.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Plating) == 0 && standard && armory.canUpgrade(UpgradeType.Terran_Vehicle_Plating)
                    && Monster.Broodwar.self().minerals() > 100 && Monster.Broodwar.self().gas() > 100) {
                if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Plating) == 0) {
                    //BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Plating, false);
                    metaType = new MetaType(UpgradeType.Terran_Vehicle_Plating);
                }
            }
            // Fac Unit 30 마리 이상, 사이언스 퍼실리티가 있고, 일정 이상의 자원 2단계
            //		else if (Prebot.Broodwar.self().minerals() > 250 && Prebot.Broodwar.self().gas() > 225 && Prebot.Broodwar.self().completedUnitCount(UnitType.Terran_Science_Facility) > 0) {
            else if (Monster.Broodwar.self().minerals() > 400 && Monster.Broodwar.self().gas() > 300 && Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Science_Facility) > 0) {
                if ((Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Command_Center) >= 2 && myFactoryUnitSupplyCount > 140)
                        || (Monster.Broodwar.self().completedUnitCount(UnitType.Terran_Command_Center) >= 3 && myFactoryUnitSupplyCount > 80)) {

                    if (Monster.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Weapons) == 2 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Weapons)) {// 3단계
                        if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Weapons) == 0) {
                            //BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Weapons, false);
                            metaType = new MetaType(UpgradeType.Terran_Vehicle_Weapons);
                        }
                    } else if (Monster.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Plating) == 1 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Plating)) {
                        if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Plating) == 0) {
                            //BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Plating, false);
                            metaType = new MetaType(UpgradeType.Terran_Vehicle_Plating);
                        }
                    } else if (Monster.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Plating) == 2 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Plating)) {
                        if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Plating) == 0) {
                            //BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Plating, false);
                            metaType = new MetaType(UpgradeType.Terran_Vehicle_Plating);
                        }
                    }
                }
            }
        }
    }


//    20180810.hkk. original source
//    if (Prebot.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Weapons) == 0 && standard && armory.canUpgrade(UpgradeType.Terran_Vehicle_Weapons)
//			&& Prebot.Broodwar.self().minerals() > 100 && Prebot.Broodwar.self().gas() > 100) {
//		if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Weapons) == 0) {
//			//BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Weapons, false);
//			metaType =  new MetaType(UpgradeType.Terran_Vehicle_Weapons);
//		}
//	} else if (Prebot.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Plating) == 0 && standard && armory.canUpgrade(UpgradeType.Terran_Vehicle_Plating)
//			&& Prebot.Broodwar.self().minerals() > 100 && Prebot.Broodwar.self().gas() > 100) {
//		if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Plating) == 0) {
//			//BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Plating, false);
//			metaType =  new MetaType(UpgradeType.Terran_Vehicle_Plating);
//		}
//	}
//	// Fac Unit 30 마리 이상, 사이언스 퍼실리티가 있고, 일정 이상의 자원 2단계
////		else if (Prebot.Broodwar.self().minerals() > 250 && Prebot.Broodwar.self().gas() > 225 && Prebot.Broodwar.self().completedUnitCount(UnitType.Terran_Science_Facility) > 0) {
//	else if (Prebot.Broodwar.self().minerals() > 400 && Prebot.Broodwar.self().gas() > 300 && Prebot.Broodwar.self().completedUnitCount(UnitType.Terran_Science_Facility) > 0) {
//		if ((Prebot.Broodwar.self().completedUnitCount(UnitType.Terran_Command_Center) >= 2 && myFactoryUnitSupplyCount > 140)
//				|| (Prebot.Broodwar.self().completedUnitCount(UnitType.Terran_Command_Center) >= 3 && myFactoryUnitSupplyCount > 80)) {
//
//			if (Prebot.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Weapons) == 1 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Weapons)) {
//				if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Weapons) == 0) {
//					//BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Weapons, false);
//					metaType =  new MetaType(UpgradeType.Terran_Vehicle_Weapons);
//				}
//			} else if (Prebot.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Plating) == 1 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Plating)) {
//				if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Plating) == 0) {
//					//BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Plating, false);
//					metaType =  new MetaType(UpgradeType.Terran_Vehicle_Plating);
//				}
//			} else if (Prebot.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Weapons) == 2 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Weapons)) {// 3단계
//				if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Weapons) == 0) {
//					//BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Weapons, false);
//					metaType =  new MetaType(UpgradeType.Terran_Vehicle_Weapons);
//				}
//			} else if (Prebot.Broodwar.self().getUpgradeLevel(UpgradeType.Terran_Vehicle_Plating) == 2 && armory.canUpgrade(UpgradeType.Terran_Vehicle_Plating)) {
//				if (BuildManager.Instance().buildQueue.getItemCount(UpgradeType.Terran_Vehicle_Plating) == 0) {
//					//BuildManager.Instance().buildQueue.queueAsLowestPriority(UpgradeType.Terran_Vehicle_Plating, false);
//					metaType =  new MetaType(UpgradeType.Terran_Vehicle_Plating);
//				}
//			}
//		}
//	}

}
