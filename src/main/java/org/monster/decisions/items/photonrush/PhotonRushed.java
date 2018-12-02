package org.monster.decisions.items.photonrush;

import org.monster.board.Decision;
import org.monster.decisions.DecisionMaker;
import org.monster.decisions.DefaultDecisionMaker;

//TODO gas 러쉬를 당했는지 판단하기 위한 클래스
public class PhotonRushed extends DefaultDecisionMaker implements DecisionMaker {


    public PhotonRushed() {
        super(Decision.PhotonRushed);
    }

    @Override
    public boolean calculateDecision() {
        return false;
    }

    @Override
    public void decisionLogic() {

    }


//    private boolean photonRushed;
//    photonRushed = false;
//    private Unit gasRushEnemyRefi;
//    private boolean gasRushed;
//    private boolean checkGasRush;
//
//    gasRushEnemyRefi = null;
//    gasRushed = false;
//    checkGasRush = true;
//
//    // 10000프레임 이전까지만 포톤러쉬 확인.
//        if (TimeUtils.getFrame() < 10000) {
//            // 1. 본진에 적 포톤캐논이 있는지 본다.
//            List<UnitInfo> enemyUnitsInRegion = InfoTypeUtils.euiListInMyRegion(BaseUtils.myMainBase().getRegion());
//            if (enemyUnitsInRegion.size() >= 1) {
//                for (int enemy = 0; enemy < enemyUnitsInRegion.size(); enemy++) {
//                    if (enemyUnitsInRegion.get(enemy).getType() == getAdvancedRushBuildingType(enemyRace)) {
//                        photonRushed = true;
//                    }
//                }
//            }
//        }
}


//    // 해당 종족의 UnitType 중 Advanced Depense 기능을 하는 UnitType을 리턴합니다
//    public UnitType getAdvancedRushBuildingType(Race race) {
//        if (race == Race.Protoss) {
//            return UnitType.Protoss_Photon_Cannon;
//        } else if (race == Race.Terran) {
//            return UnitType.Terran_Bunker;
//        } else if (race == Race.Zerg) {
//            return UnitType.Zerg_Sunken_Colony;
//        } else {
//            return UnitType.None;
//        }
//    }
//

