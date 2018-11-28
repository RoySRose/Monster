package org.monster.strategy.manage;

import bwapi.Race;
import bwapi.TilePosition;
import org.monster.board.StrategyBoard;
import org.monster.common.constant.CommonCode;
import org.monster.common.debugger.UxColor;
import org.monster.common.debugger.chat.impl.StrategyChanger;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.InfoUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.decisionMakers.constant.EnemyStrategyOptions;
import org.monster.decisionMakers.decisionTypes.EnemyStrategy;
import org.monster.main.Monster;
import org.monster.strategy.analyse.ProtossStrategist;
import org.monster.strategy.analyse.Strategist;
import org.monster.strategy.analyse.TerranStrategist;
import org.monster.strategy.analyse.UnitAnalyser;
import org.monster.strategy.analyse.ZergStrategist;
import org.monster.strategy.analyse.protoss.AdunAnalyser;
import org.monster.strategy.analyse.protoss.AssimilatorAnalyser;
import org.monster.strategy.analyse.protoss.CannonAnalyser;
import org.monster.strategy.analyse.protoss.CoreAnalyser;
import org.monster.strategy.analyse.protoss.FleetBeaconAnalyser;
import org.monster.strategy.analyse.protoss.ForgeAnalyser;
import org.monster.strategy.analyse.protoss.GateAnalyser;
import org.monster.strategy.analyse.protoss.NexsusAnalyser;
import org.monster.strategy.analyse.protoss.ObservatoryAnalyser;
import org.monster.strategy.analyse.protoss.RoboticsAnalyser;
import org.monster.strategy.analyse.protoss.RoboticsSupportAnalyser;
import org.monster.strategy.analyse.protoss.StargateAnalyser;
import org.monster.strategy.analyse.protoss.TemplarArchAnalyser;
import org.monster.strategy.analyse.protoss.unit.DarkTemplarAnalyser;
import org.monster.strategy.analyse.protoss.unit.DragoonAnalyser;
import org.monster.strategy.analyse.protoss.unit.ObserverAnalyser;
import org.monster.strategy.analyse.protoss.unit.ReaverAnalyser;
import org.monster.strategy.analyse.protoss.unit.ShuttleAnalyser;
import org.monster.strategy.analyse.protoss.unit.ZealotAnalyser;
import org.monster.strategy.analyse.terran.AcademyAnalyser;
import org.monster.strategy.analyse.terran.BarracksAnalyser;
import org.monster.strategy.analyse.terran.CommandCenterAnalyser;
import org.monster.strategy.analyse.terran.FactoryAnalyser;
import org.monster.strategy.analyse.terran.RefineryAnalyser;
import org.monster.strategy.analyse.terran.StarportAnalyser;
import org.monster.strategy.analyse.terran.unit.DropshipAnalyser;
import org.monster.strategy.analyse.terran.unit.FirebatAnalyser;
import org.monster.strategy.analyse.terran.unit.GoliathAnalyser;
import org.monster.strategy.analyse.terran.unit.MarineAnalyser;
import org.monster.strategy.analyse.terran.unit.MedicAnalyser;
import org.monster.strategy.analyse.terran.unit.TankAnalyser;
import org.monster.strategy.analyse.terran.unit.VultureAnalyser;
import org.monster.strategy.analyse.terran.unit.WraithAnalyser;
import org.monster.strategy.analyse.zerg.ExtractorAnalyser;
import org.monster.strategy.analyse.zerg.HatcheryAnalyser;
import org.monster.strategy.analyse.zerg.HydraDenAnalyser;
import org.monster.strategy.analyse.zerg.LairAnalyser;
import org.monster.strategy.analyse.zerg.OverloadAnalyser;
import org.monster.strategy.analyse.zerg.SpawningPoolAnalyser;
import org.monster.strategy.analyse.zerg.SpireAnalyser;
import org.monster.strategy.analyse.zerg.unit.HydraliskAnalyser;
import org.monster.strategy.analyse.zerg.unit.LurkerAnalyser;
import org.monster.strategy.analyse.zerg.unit.MutaliskAnalyser;
import org.monster.strategy.analyse.zerg.unit.ZerglingAnalyser;

import java.util.ArrayList;
import java.util.List;

public class StrategyAnalyseManager {

    private static StrategyAnalyseManager instance = new StrategyAnalyseManager();
    public int lastCheckFrameBase = 0;
    public int lastCheckFrameGas = 0;
    public int lastCheckFrameFirstExpansion = 0;
    private List<UnitAnalyser> analysers = new ArrayList<>();
    private Strategist strategist = null;

    public static StrategyAnalyseManager Instance() {
        return instance;
    }

    public int getPhase() {
        if (strategist != null) {
            return strategist.getPhase();
        } else {
            return CommonCode.UNKNOWN;
        }
    }

    public void update() {
        if (strategist != null && strategist.getPhase() < 3) {
            updateVisitFrame();
            EnemyBuildTimer.Instance().update();

            for (UnitAnalyser analyser : analysers) {
                analyser.upateFoundInfo();
                analyser.analyse();
            }
        }

        EnemyStrategy strategyToApply;
        if (strategist != null) {
            strategyToApply = strategist.strategyToApply();
        } else {
            strategyToApply = EnemyStrategy.ZERG_INIT;
        }

        if (!StrategyChanger.stopStrategiestForDebugging) {
            if (strategyToApply != EnemyStrategy.UNKNOWN && StrategyBoard.currentStrategy != strategyToApply) {
                Monster.Broodwar.printf(UxColor.CHAR_WHITE + "ENEMY STRATEY : " + strategyToApply.name());
                StrategyBoard.strategyHistory.add(StrategyBoard.currentStrategy);
                StrategyBoard.currentStrategy = strategyToApply;
                this.applyDetailValue(strategyToApply);
            }
        }

        if (PlayerUtils.enemyRace() == Race.Protoss && UnitUtils.myFactoryUnitSupplyCount() >= 3 * 3) {
            StrategyBoard.marineCount = 0;
        }
    }

    private void applyDetailValue(EnemyStrategy currentStrategy) {
        StrategyBoard.factoryRatio = currentStrategy.factoryRatio;
        StrategyBoard.upgrade = currentStrategy.upgrade;
        StrategyBoard.marineCount = currentStrategy.marineCount;

        // addOn option
        if (currentStrategy.addOnOption != null) {
            StrategyBoard.addOnOption = currentStrategy.addOnOption;
        }

        // air unit count
        if (currentStrategy.expansionOption != null) {
            StrategyBoard.expansionOption = currentStrategy.expansionOption;
            if (currentStrategy.expansionOption == EnemyStrategyOptions.ExpansionOption.TWO_STARPORT) {
                StrategyBoard.wraithCount = 4;
                StrategyBoard.valkyrieCount = 0;
            } else if (currentStrategy.expansionOption == EnemyStrategyOptions.ExpansionOption.ONE_STARPORT) {
                StrategyBoard.wraithCount = 0;
                StrategyBoard.valkyrieCount = 2;
            } else if (currentStrategy.expansionOption == EnemyStrategyOptions.ExpansionOption.ONE_FACTORY || currentStrategy.expansionOption == EnemyStrategyOptions.ExpansionOption.TWO_FACTORY) {
                StrategyBoard.wraithCount = 0;
                StrategyBoard.valkyrieCount = 0;
            }
        }
        if (currentStrategy.buildTimeMap != null) {
            StrategyBoard.buildTimeMap = currentStrategy.buildTimeMap;
        }
    }

    public void setUp(Race race) {
        if (race == Race.Protoss) {
            analysers.add(new AdunAnalyser());
            analysers.add(new AssimilatorAnalyser());
            analysers.add(new CannonAnalyser());
            analysers.add(new CoreAnalyser());
            analysers.add(new ForgeAnalyser());
            analysers.add(new GateAnalyser());
            analysers.add(new NexsusAnalyser());
            analysers.add(new ObservatoryAnalyser());
            analysers.add(new RoboticsAnalyser());
            analysers.add(new RoboticsSupportAnalyser());
            analysers.add(new StargateAnalyser());
            analysers.add(new TemplarArchAnalyser());
            analysers.add(new FleetBeaconAnalyser());

            analysers.add(new ZealotAnalyser());
            analysers.add(new DragoonAnalyser());
            analysers.add(new DarkTemplarAnalyser());
            analysers.add(new ShuttleAnalyser());
            analysers.add(new ReaverAnalyser());
            analysers.add(new ObserverAnalyser());

            strategist = new ProtossStrategist();

        } else if (race == Race.Zerg) {
            analysers.add(new ExtractorAnalyser());
            analysers.add(new HatcheryAnalyser());
            analysers.add(new HydraDenAnalyser());
            analysers.add(new LairAnalyser());
            analysers.add(new OverloadAnalyser());
            analysers.add(new SpawningPoolAnalyser());
            analysers.add(new SpireAnalyser());

            analysers.add(new ZerglingAnalyser());
            analysers.add(new HydraliskAnalyser());
            analysers.add(new LurkerAnalyser());
            analysers.add(new MutaliskAnalyser());

            strategist = new ZergStrategist();

        } else if (race == Race.Terran) {
            analysers.add(new AcademyAnalyser());
            analysers.add(new BarracksAnalyser());
            analysers.add(new CommandCenterAnalyser());
            analysers.add(new FactoryAnalyser());
            analysers.add(new RefineryAnalyser());
            analysers.add(new StarportAnalyser());

            analysers.add(new MarineAnalyser());
            analysers.add(new MedicAnalyser());
            analysers.add(new FirebatAnalyser());
            analysers.add(new VultureAnalyser());
            analysers.add(new TankAnalyser());
            analysers.add(new GoliathAnalyser());
            analysers.add(new WraithAnalyser());
            analysers.add(new DropshipAnalyser());

            strategist = new TerranStrategist();
        }
    }

    /// 유닛 발견 맵을 업데이트한다.
    private void updateVisitFrame() {
        if (BaseUtils.enemyMainBase() == null || InfoUtils.enemyBaseGas() == null) {
            return;
        }

        TilePosition enemyBaseTile = BaseUtils.enemyMainBase().getTilePosition();
        TilePosition enemyGasTile = InfoUtils.enemyBaseGas().getTilePosition();
        TilePosition enemyFirstExpansionTile = BaseUtils.enemyFirstExpansion().getTilePosition();

        if (Monster.Broodwar.isVisible(enemyBaseTile)) {
//			System.out.println("base explored");
            lastCheckFrameBase = TimeUtils.elapsedFrames();
        }
        if (Monster.Broodwar.isVisible(enemyGasTile)) {
//			System.out.println("gas explored");
            lastCheckFrameGas = TimeUtils.elapsedFrames();
        }
        if (Monster.Broodwar.isVisible(enemyFirstExpansionTile)) {
//			System.out.println("expansion explored");
            lastCheckFrameFirstExpansion = TimeUtils.elapsedFrames();
        }
    }

    public int lastCheckFrame(StrategyAnalyseManager.LastCheckLocation lastCheckLocation) {
        if (lastCheckLocation == StrategyAnalyseManager.LastCheckLocation.BASE) {
            return StrategyAnalyseManager.Instance().lastCheckFrameBase;
        } else if (lastCheckLocation == StrategyAnalyseManager.LastCheckLocation.FIRST_EXPANSION) {
            return StrategyAnalyseManager.Instance().lastCheckFrameFirstExpansion;
        } else if (lastCheckLocation == StrategyAnalyseManager.LastCheckLocation.GAS) {
            return StrategyAnalyseManager.Instance().lastCheckFrameGas;
        } else {
            return CommonCode.NONE;
        }
    }

    public enum LastCheckLocation {
        BASE, FIRST_EXPANSION, GAS
    }

}
