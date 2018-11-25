package prebot.build.initialProvider;

import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import prebot.build.initialProvider.BlockingEntrance.BlockingEntrance;
import prebot.build.initialProvider.buildSets.AdaptNewStrategy;
import prebot.build.initialProvider.buildSets.VsProtoss;
import prebot.build.initialProvider.buildSets.VsTerran;
import prebot.build.initialProvider.buildSets.VsZerg;
import prebot.build.base.BuildManager;
import prebot.build.base.BuildOrderItem;
import prebot.build.base.BuildOrderQueue;
import prebot.build.base.ConstructionManager;
import prebot.build.base.ConstructionTask;
import prebot.common.constant.CommonCode;
import prebot.common.util.UnitUtils;
import prebot.strategy.InformationManager;
import prebot.strategy.StrategyIdea;
import prebot.strategy.constant.EnemyStrategy;
import prebot.strategy.constant.EnemyStrategyOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class InitialBuildProvider {

    private static InitialBuildProvider instance = new InitialBuildProvider();
    public EnemyStrategyOptions.ExpansionOption nowStrategy, bfStrategy;
    public EnemyStrategy dbgNowStg, dbgBfStg;
    public TilePosition firstSupplyPos = TilePosition.None;
    public TilePosition barrackPos = TilePosition.None;
    public TilePosition secondSupplyPos = TilePosition.None;
    public TilePosition factoryPos = TilePosition.None;
    public TilePosition bunkerPos1 = TilePosition.None;
    public TilePosition bunkerPos2 = TilePosition.None;
    public TilePosition starport1 = TilePosition.None;
    public TilePosition starport2 = TilePosition.None;
    private InitialBuildProvider.AdaptStrategyStatus adaptStrategyStatus = InitialBuildProvider.AdaptStrategyStatus.BEFORE;

    public static InitialBuildProvider Instance() {
        return instance;
    }

    public InitialBuildProvider.AdaptStrategyStatus getAdaptStrategyStatus() {
        return adaptStrategyStatus;
    }

    public void onStart() {
        System.out.println("InitialBuildProvider onStart start");

        nowStrategy = null;
        bfStrategy = null;
        dbgNowStg = null;
        dbgBfStg = null;


        firstSupplyPos = BlockingEntrance.Instance().first_supple;
        barrackPos = BlockingEntrance.Instance().barrack;
        secondSupplyPos = BlockingEntrance.Instance().second_supple;
        factoryPos = BlockingEntrance.Instance().factory;
        bunkerPos1 = BlockingEntrance.Instance().bunker1;
        bunkerPos2 = BlockingEntrance.Instance().bunker2;
        starport1 = BlockingEntrance.Instance().starport1;
        starport2 = BlockingEntrance.Instance().starport2;

        if (InformationManager.Instance().enemyRace == Race.Terran) {
            new VsTerran(firstSupplyPos, barrackPos, secondSupplyPos, factoryPos, starport1, starport2);
        } else if (InformationManager.Instance().enemyRace == Race.Protoss) {
            new VsProtoss(firstSupplyPos, barrackPos, secondSupplyPos, factoryPos, starport1, starport2);
        } else {
            new VsZerg(firstSupplyPos, barrackPos, secondSupplyPos, factoryPos, bunkerPos1, starport1, starport2);
        }

        System.out.println("InitialBuildProvider onStart end");
    }

    public void updateInitialBuild() {
        if (InitialBuildProvider.Instance().getAdaptStrategyStatus() == InitialBuildProvider.AdaptStrategyStatus.COMPLETE) {
            return;
        }

        if (adaptStrategyStatus == InitialBuildProvider.AdaptStrategyStatus.BEFORE) {
            if (BuildManager.Instance().buildQueue.isEmpty()) {
                nowStrategy = StrategyIdea.expansionOption;
                if (nowStrategy == EnemyStrategyOptions.ExpansionOption.TWO_FACTORY || nowStrategy == EnemyStrategyOptions.ExpansionOption.TWO_STARPORT || nowStrategy == EnemyStrategyOptions.ExpansionOption.ONE_STARPORT) {
                    new AdaptNewStrategy().adapt(nowStrategy, factoryPos, starport1, starport2);
                }
                adaptStrategyStatus = InitialBuildProvider.AdaptStrategyStatus.PROGRESSING;
            }


        } else if (adaptStrategyStatus == InitialBuildProvider.AdaptStrategyStatus.PROGRESSING) {
            if (nowStrategy != StrategyIdea.expansionOption) {
                nowStrategy = StrategyIdea.expansionOption;

                // 폭파하기
                cancelConstructionAndRemoveFromBuildQueue();
                new AdaptNewStrategy().adapt(nowStrategy, factoryPos, starport1, starport2);
                adaptStrategyStatus = InitialBuildProvider.AdaptStrategyStatus.COMPLETE; // 2번은 취소하지 않도록

            } else {
                if (nowStrategy == EnemyStrategyOptions.ExpansionOption.TWO_FACTORY) {
                    List<Unit> factoryList = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Factory);
                    if (factoryList.size() == 2) {
                        adaptStrategyStatus = InitialBuildProvider.AdaptStrategyStatus.COMPLETE;
                    }
                } else if (nowStrategy == EnemyStrategyOptions.ExpansionOption.TWO_STARPORT) {
                    List<Unit> starportList = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Starport);
                    if (starportList.size() == 2) {
                        adaptStrategyStatus = InitialBuildProvider.AdaptStrategyStatus.COMPLETE;
                    }
                } else if (nowStrategy == EnemyStrategyOptions.ExpansionOption.ONE_STARPORT) {
                    List<Unit> starportList = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Starport);
                    if (starportList.size() == 1) {
                        adaptStrategyStatus = InitialBuildProvider.AdaptStrategyStatus.COMPLETE;
                    }

                } else if (nowStrategy == EnemyStrategyOptions.ExpansionOption.ONE_FACTORY) {
                    List<Unit> starportList = UnitUtils.getUnitList(CommonCode.UnitFindRange.ALL, UnitType.Terran_Command_Center);
                    if (starportList.size() == 2) {
                        adaptStrategyStatus = InitialBuildProvider.AdaptStrategyStatus.COMPLETE;
                    }
                }
            }
        }


        //TODO 여기에 조건별 다 때려 넣으소서.......... 몇개 안되는거 굳이 나눌 필요가 있나싶다.


    }

    /// 빌드큐, 컨스트럭션 큐, 건설 중 건물을 취소한다.
    private void cancelConstructionAndRemoveFromBuildQueue() {

        int removedFromBuildQueue = 0;
        int removedFromConstructionQueue = 0;
        List<Unit> cancelBuildings = new ArrayList<>();

        // 1팩토리, 1스타포트, 2스타포트 : 2번째 팩토리를 취소한다.
        if (nowStrategy == EnemyStrategyOptions.ExpansionOption.TWO_STARPORT || nowStrategy == EnemyStrategyOptions.ExpansionOption.ONE_FACTORY || nowStrategy == EnemyStrategyOptions.ExpansionOption.ONE_STARPORT) {
            Unit completeFirstFactory = null; // 첫번째 팩토리 (완성)
            List<Unit> completeFactories = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Factory);
            if (completeFactories.size() >= 1) {
                completeFirstFactory = completeFactories.get(0);
            }

            List<Unit> incompleteFactories = UnitUtils.getUnitList(CommonCode.UnitFindRange.INCOMPLETE, UnitType.Terran_Factory);
            Unit incompleteFirstFactory = null; // 첫번째 팩토리 (미완성 - 완성된 팩토리가 없는 경우)
            if (completeFirstFactory == null) {
                int minimumRemainingBuildTime = CommonCode.INT_MAX;
                for (Unit incompleteFactory : incompleteFactories) {
                    if (incompleteFactory.getRemainingBuildTime() < minimumRemainingBuildTime) {
                        incompleteFirstFactory = incompleteFactory;
                        minimumRemainingBuildTime = incompleteFactory.getRemainingBuildTime();
                    }
                }
            }

            // 완성되지 않은 팩토리를 취소한다. (첫번째 팩토리면 취소하지 않는다.)
            for (Unit incompleteFactory : incompleteFactories) {
                if (incompleteFirstFactory == null // completeFirstFactory가 있는 경우
                        || incompleteFirstFactory.getID() != incompleteFactory.getID()) {
                    cancelBuildings.add(incompleteFactory);
                }
            }

            if (cancelBuildings.isEmpty()) {
                // 컨스트럭션 큐에서 지운다.
                boolean notFirstOne = completeFirstFactory == null; // 큐에서 첫번째 있는 팩토리를 폭파시키지 않는다.
                removedFromConstructionQueue = deleteFromConstructionQueue(UnitType.Terran_Factory, notFirstOne);

                // 지워지지 않았다면 빌드큐에서 지운다.(건설 시작전)
                if (removedFromConstructionQueue == 0) {
                    notFirstOne = completeFirstFactory == null && incompleteFirstFactory == null;
                    removedFromBuildQueue = deleteFromBuildQueue(UnitType.Terran_Factory, notFirstOne);
                }
            }
        }

        // 2팩, 1스타포트 : 2번째 스타포트를 취소한다.
        if (nowStrategy == EnemyStrategyOptions.ExpansionOption.TWO_FACTORY || nowStrategy == EnemyStrategyOptions.ExpansionOption.ONE_STARPORT) {
            Unit completeFirstStarport = null; // 첫번째 스타포트 (완성)
            List<Unit> completeBuildings = UnitUtils.getUnitList(CommonCode.UnitFindRange.COMPLETE, UnitType.Terran_Starport);
            if (completeBuildings.size() >= 1) {
                completeFirstStarport = completeBuildings.get(0);
            }

            List<Unit> incompleteStarports = UnitUtils.getUnitList(CommonCode.UnitFindRange.INCOMPLETE, UnitType.Terran_Starport);
            Unit incompleteFirstStarport = null; // 두번째 스타포트 (미완성 - 완성된 스타포트가 없는 경우)
            if (completeFirstStarport == null) {
                int minimumRemainingBuildTime = CommonCode.INT_MAX;
                for (Unit incompleteStarport : incompleteStarports) {
                    if (incompleteStarport.getRemainingBuildTime() < minimumRemainingBuildTime) {
                        incompleteFirstStarport = incompleteStarport;
                        minimumRemainingBuildTime = incompleteStarport.getRemainingBuildTime();
                    }
                }
            }

            // 완성되지 않은 스타포트를 취소한다. (첫번째 스타포트면 취소하지 않는다.)
            for (Unit incompleteStarport : incompleteStarports) {
                if (incompleteFirstStarport == null // completeFirstFactory가 있는 경우
                        || incompleteFirstStarport.getID() != incompleteStarport.getID()) {
                    cancelBuildings.add(incompleteStarport);
                }
            }

            if (cancelBuildings.isEmpty()) {
                // 컨스트럭션 큐에서 지운다.
                boolean notFirstOne = completeFirstStarport == null; // 큐에서 첫번째 있는 스타포트 폭파시키지 않는다.
                removedFromConstructionQueue = deleteFromConstructionQueue(UnitType.Terran_Starport, notFirstOne);

                // 지워지지 않았다면 빌드큐에서 지운다.(건설 시작전)
                if (removedFromConstructionQueue == 0) {
                    notFirstOne = completeFirstStarport == null && incompleteFirstStarport == null;
                    removedFromBuildQueue = deleteFromBuildQueue(UnitType.Terran_Starport, notFirstOne);
                }
            }
        }

        for (Unit cancelBuilding : cancelBuildings) {
            ConstructionManager.Instance().addCancelBuildingId(cancelBuilding.getID());
        }

        if (removedFromBuildQueue > 0 || removedFromConstructionQueue > 0 || cancelBuildings.size() > 0) {
            System.out.println("########################################");
            System.out.println("removed from build queue : " + removedFromBuildQueue);
            System.out.println("removed from construction queue : " + removedFromConstructionQueue);
            System.out.println("canceled incomplete building = " + cancelBuildings.size());
            System.out.println("########################################");
        }
    }

    public int deleteFromBuildQueue(UnitType unitType, boolean notFirstOne) {
        BuildOrderQueue tempbuildQueue = BuildManager.Instance().getBuildQueue();
        if (tempbuildQueue.isEmpty()) {
            return 0;
        }

        return tempbuildQueue.removeUnitTypeItems(unitType, notFirstOne);
    }

    public int deleteFromConstructionQueue(UnitType deleteType, boolean notFirstOne) {
        boolean haveToSkipFirstOne = notFirstOne;

        Vector<ConstructionTask> removeFromQueue = new Vector<>();
        Vector<ConstructionTask> constructionQueue = ConstructionManager.Instance().getConstructionQueue();
        for (ConstructionTask constructionTask : constructionQueue) {
            if (constructionTask.getType() == deleteType) {
                if (haveToSkipFirstOne) {
                    haveToSkipFirstOne = false;
                } else {
                    removeFromQueue.add(constructionTask);
                }
            }
        }

        int count = 0;
        for (ConstructionTask constructionTask : removeFromQueue) {
//    		ConstructionManager.Instance().cancelConstructionTask(constructionTask.getType(), constructionTask.getDesiredPosition());
            ConstructionManager.Instance().cancelConstructionTask(constructionTask);
            count++;
        }
        return count;
    }

    public void deleteFromQueueCnt(UnitType unitType, int chkCnt) {
        BuildOrderItem checkItem = null;
        BuildOrderQueue tempbuildQueue = BuildManager.Instance().getBuildQueue();
//        BuildOrderQueue tempbuildQueue = BuildManager.Instance().buildQueue;


        int cnt = 0;

        if (unitType == UnitType.Terran_SCV) {
            chkCnt = chkCnt - 4;
        }

        if (!tempbuildQueue.isEmpty()) {
//            checkItem= tempbuildQueue.getHighestPriorityItem();
            while (true) {
                System.out.println("unitType :: " + unitType + " while true");
                if (tempbuildQueue.canGetNextItem() == true) {
                    tempbuildQueue.canGetNextItem();
                } else {
                    break;
                }
                checkItem = tempbuildQueue.getHighestPriorityItem();
//                tempbuildQueue.PointToNextItem();
//                checkItem = tempbuildQueue.getItem();

                if (checkItem.metaType.isUnit() && checkItem.metaType.getUnitType() == unitType) {

                    tempbuildQueue.removeCurrentItem();
                    cnt++;
//                    debugingFromQueue(" deleteFromQueueCnt "+ cnt + " :: " + unitType.toString());
//                    if(cnt >= chkCnt) break;
                } else {
                    cnt = cnt + deleteFromQueueNext(tempbuildQueue, unitType);
                }
                if (cnt >= chkCnt) return;
            }
        }
//        return cnt;
    }

    public int deleteFromQueueNext(BuildOrderQueue tempQueue, UnitType unit) {
        if (tempQueue.canGetNextItem() == true) {
            tempQueue.PointToNextItem();
            if (tempQueue.getItem().metaType.getUnitType() == unit) {
                tempQueue.removeCurrentItem();
                return 1;
            } else {
                deleteFromQueueNext(tempQueue, unit);
            }
            return 0;
        }
        return 0;
    }

    public enum AdaptStrategyStatus {
        BEFORE, PROGRESSING, COMPLETE
    }


}









