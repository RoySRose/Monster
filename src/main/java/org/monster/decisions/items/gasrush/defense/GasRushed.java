package org.monster.decisions.items.gasrush.defense;

import org.monster.board.Decision;
import org.monster.decisions.DecisionMaker;
import org.monster.decisions.DefaultDecisionMaker;

//TODO gas 러쉬를 당했는지 판단하기 위한 클래스
public class GasRushed extends DefaultDecisionMaker implements DecisionMaker {

    public GasRushed() {
        super(Decision.GasRushed);
    }

    @Override
    public boolean calculateDecision() {
        return true;
    }

    @Override
    public void decisionLogic() {

    }

//    private Unit gasRushEnemyRefi;
//    private boolean gasRushed;
//    private boolean checkGasRush;
//
//    gasRushEnemyRefi = null;
//    gasRushed = false;
//    checkGasRush = true;
//
//    if (checkGasRush == true) {
//
//        for (Unit unit : Monster.Broodwar.self().getUnits()) {
//            if (unit.getType() == UnitType.Terran_Refinery && unit.isCompleted() && myfirstGas != null) {
//                if (myfirstGas.getPosition().equals(unit.getPosition())) {
////						//FileUtils.appendTextToFile("log.txt", "\n Information checkGasRush :: we have Refinery :: not danger gas rush");
//                    checkGasRush = false;// 가스 러쉬 위험 끝
//                }
//            }
//        }
//        for (Unit unit : Monster.Broodwar.enemy().getUnits()) {
//            if (unit.getType() == InfoTypeUtils.getRefineryBuildingType(enemyRace) && myfirstGas != null) {
//                if (myfirstGas.getPosition().equals(unit.getPosition())) {
////						//FileUtils.appendTextToFile("log.txt", "\n  checkGasRush :: gasRsuhed is true :: "+ Prebot.Broodwar.getFrameCount() + " :: " + unit.getType());
//                    gasRushed = true;// 가스 러쉬 당함
//                    gasRushEnemyRefi = unit;
////						//FileUtils.appendTextToFile("log.txt", "\n  checkGasRush :: gasRushEnemyRefi :: "+ Prebot.Broodwar.getFrameCount() + " :: "+ gasRushEnemyRefi.getType());
//                    if (BuildManager.Instance().buildQueue.getItemCount(UnitType.Terran_Refinery) > 0) {
//
//                        BuildOrderQueue tempbuildQueue = BuildManager.Instance().getBuildQueue();
//                        BuildOrderItem currentItem = null;
//
//                        if (!tempbuildQueue.isEmpty()) {
//                            currentItem = tempbuildQueue.getHighestPriorityItem();
//                            while (true) {
//                                if (currentItem.metaType.isUnit() == true && currentItem.metaType.isRefinery()) {
//                                    tempbuildQueue.removeCurrentItem();
//                                    break;
//                                } else if (tempbuildQueue.canGetNextItem() == true) {
//                                    tempbuildQueue.PointToNextItem();
//                                    currentItem = tempbuildQueue.getItem();
//                                } else {
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (gasRushed == true && gasRushEnemyRefi != null) {
//            if (gasRushEnemyRefi == null || gasRushEnemyRefi.getHitPoints() <= 0
//                    || gasRushEnemyRefi.isTargetable() == false) {
////					//FileUtils.appendTextToFile("log.txt", "\n Information checkGasRush :: gas rush end"+ Prebot.Broodwar.getFrameCount());
//                gasRushed = false;// 가스 러쉬 위험 끝
////					System.out.println("gas rush finished");
////					if(BuildManager.Instance().buildQueue.getItemCount(UnitType.Terran_Refinery) < 1){
////						BuildManager.Instance().buildQueue.queueAsHighestPriority(UnitType.Terran_Refinery,BuildOrderItem.SeedPositionStrategy.MainBaseLocation, true);
////					}
//            }
//        }
//    }
}
