package org.monster.common;

import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import org.monster.bootstrap.GameManager;
import org.monster.build.constant.BuildConfig;
import org.monster.common.constant.PlayerRange;
import org.monster.common.util.BaseUtils;
import org.monster.common.util.MapUtils;
import org.monster.common.util.PlayerUtils;
import org.monster.common.util.TimeUtils;
import org.monster.common.util.UnitUtils;
import org.monster.strategy.constant.StrategyConfig;

import java.util.ArrayList;
import java.util.List;

/// 지도를 바둑판처럼 Cell 들로 나누고, 매 frame 마다 각 Cell 의 timeLastVisited 시간정보, timeLastOpponentSeen 시간정보, ourUnits 와 oppUnits 목록을 업데이트 합니다
public class MapGrid extends GameManager {

    private static MapGrid instance = new MapGrid(MapUtils.mapHeight() * 32, MapUtils.mapHeight() * 32, BuildConfig.MAP_GRID_SIZE);
    public GridCell[] gridCells;
    private GridCell lastScanGridCell = null;

    ;
    private int cellSize;
    private int mapWidth;
    private int mapHeight;
    private int cols;
    private int rows;
    private int lastUpdated;

    public MapGrid(int mapWidth, int mapHeight, int cellSize) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.cellSize = cellSize;
        this.cols = (mapWidth + cellSize - 1) / cellSize;
        this.rows = (mapHeight + cellSize - 1) / cellSize;
        this.gridCells = new GridCell[this.rows * this.cols];
        for (int i = 0; i < this.gridCells.length; ++i) {
            gridCells[i] = new GridCell();
        }
        this.lastUpdated = 0;
        calculateCellCenters();
    }

    /// static singleton 객체를 리턴합니다
    public static MapGrid Instance() {
        return instance;
    }

    public boolean scanAbnormalTime() {
        if (lastScanGridCell == null) {
            return false;
        }

        int timeLastScan = lastScanGridCell.timeLastScan;
        return TimeUtils.getFrame(timeLastScan) < LagObserver.managerRotationSize() + 10;
    }

    public Position getLeastExplored() {
        int minSeen = 1000000;
        double minSeenDist = 0;
        int leastRow = 0;
        int leastCol = 0;

        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                // get the center of this cell
                Position cellCenter = getCellCenter(r, c);

                // don't worry about places that aren't connected to our start locatin
                if (!BWTA.isConnected(cellCenter.toTilePosition(), BaseUtils.myMainBase().getTilePosition())) {
                    continue;
                }

                Position home = BaseUtils.myMainBase().getPosition();
                double dist = home.getDistance(getCellByIndex(r, c).center);
                int lastVisited = getCellByIndex(r, c).timeLastVisited;
                if (lastVisited < minSeen || ((lastVisited == minSeen) && (dist > minSeenDist))) {
                    leastRow = r;
                    leastCol = c;
                    minSeen = getCellByIndex(r, c).timeLastVisited;
                    minSeenDist = dist;
                }
            }
        }

        return getCellCenter(leastRow, leastCol);
    }

    private void calculateCellCenters() {
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                GridCell cell = getCellByIndex(r, c);

                int centerX = (c * cellSize) + (cellSize / 2);
                int centerY = (r * cellSize) + (cellSize / 2);

                // if the x position goes past the end of the map
                if (centerX > mapWidth) {
                    // when did the last cell start
                    int lastCellStart = c * cellSize;

                    // how wide did we go
                    int tooWide = mapWidth - lastCellStart;

                    // go half the distance between the last start and how wide we were
                    centerX = lastCellStart + (tooWide / 2);
                } else if (centerX == mapWidth) {
                    centerX -= 50;
                }

                if (centerY > mapHeight) {
                    // when did the last cell start
                    int lastCellStart = r * cellSize;

                    // how wide did we go
                    int tooHigh = mapHeight - lastCellStart;

                    // go half the distance between the last start and how wide we were
                    centerY = lastCellStart + (tooHigh / 2);
                } else if (centerY == mapHeight) {
                    centerY -= 50;
                }

                cell.center = new Position(centerX, centerY);
                //assert(cell.center.isValid());
            }
        }
    }

    private Position getCellCenter(int row, int col) {
        return getCellByIndex(row, col).center;
    }

    private GridCell getCellByIndex(int r, int c) {
        return gridCells[r * cols + c];
    }

    // clear the vectors in the grid
    private void clearGrid() {
        for (int i = 0; i < gridCells.length; ++i) {
            gridCells[i].oppUnits = new ArrayList<Unit>();
            gridCells[i].ourUnits = new ArrayList<Unit>();
        }
    }

    /// 각 Cell 의 timeLastVisited 시간정보, timeLastOpponentSeen 시간정보, ourUnits 와 oppUnits 목록 등을 업데이트 합니다
    public void update() {
        if (TimeUtils.executeRotation(LagObserver.managerExecuteRotation(LagObserver.MANAGER2, 0), LagObserver.managerRotationSize())) {
            // clear the grid
            clearGrid();

            //PlayerUtils.printf("MapGrid info: WH(%d, %d)  CS(%d)  RC(%d, %d)  C(%d)", mapWidth, mapHeight, cellSize, rows, cols, cells.size());

            // add our units to the appropriate cell
            for (Unit unit : UnitUtils.getUnitList()) {
                getCell(unit).ourUnits.add(unit);
                getCell(unit).timeLastVisited = TimeUtils.getFrame();
            }

            // add enemy units to the appropriate cell
            for (Unit unit : UnitUtils.getEnemyUnitList()) {
                if (unit.getHitPoints() > 0) {
                    getCell(unit).oppUnits.add(unit);
                    getCell(unit).timeLastOpponentSeen = TimeUtils.getFrame();
                }
            }
        }
    }

    private GridCell getCell(Unit unit) {
        return getCell(unit.getPosition());
    }

    public GridCell getCell(Position pos) {
        return getCellByIndex(pos.getY() / cellSize, pos.getX() / cellSize);
    }

    // UnitUtils의 메서드로 대체한다.
    @Deprecated
    public List<Unit> getUnitsNearForAir(Position position, int radius, boolean ourUnits, boolean oppUnits) {
        List<Unit> units = new ArrayList<>();
        List<Unit> unitsInRadius = UnitUtils.getUnitsInRadius(PlayerRange.ALL, position, radius);
        for (Unit u : unitsInRadius) {
            if (oppUnits && u.getPlayer() == PlayerUtils.enemyPlayer()) {

                if (PlayerUtils.enemyRace() == Race.Protoss) {
                    if (u.getType() == UnitType.Protoss_Dragoon
                            || u.getType() == UnitType.Protoss_Archon
                            || u.getType() == UnitType.Protoss_Carrier
                            || u.getType() == UnitType.Protoss_Corsair
                            || u.getType() == UnitType.Protoss_Scout
                            ) {
                        if (!units.contains(u)) {
                            units.add(u);
                        }
                    }
                } else if (PlayerUtils.enemyRace() == Race.Terran) {
                    if (u.getType() == UnitType.Terran_Missile_Turret
                            || u.getType() == UnitType.Terran_Goliath
                            || u.getType() == UnitType.Terran_Marine
                            || u.getType() == UnitType.Terran_Wraith
                            ) {
                        if (!units.contains(u)) {
                            units.add(u);
                        }
                    }
                } else {
                    if (u.getType() == UnitType.Zerg_Hydralisk
                            || u.getType() == UnitType.Zerg_Mutalisk
                            || u.getType() == UnitType.Zerg_Scourge
                            || u.getType() == UnitType.Zerg_Devourer
                            ) {
                        if (!units.contains(u)) {
                            units.add(u);
                        }
                    }
                }
            }
        }
        return units;
    }

    // UnitUtils.getUnitsInRadius 메서드로 대체한다.
    @Deprecated
    public List<Unit> getUnitsNear(Position position, int radius, boolean ourUnits, boolean oppUnits, UnitType unitType) {
        List<Unit> units = new ArrayList<>();
        List<Unit> unitsInRadius = UnitUtils.getUnitsInRadius(PlayerRange.ALL, position, radius);
        for (Unit u : unitsInRadius) {
            if (ourUnits && u.getPlayer() == PlayerUtils.myPlayer()) {
                if ((unitType == null || u.getType() == unitType) && !units.contains(u)) {
                    units.add(u);
                }

            } else if (oppUnits && u.getPlayer() == PlayerUtils.enemyPlayer()) {
                if ((unitType == null || u.getType() == unitType) && !units.contains(u)) {
                    units.add(u);
                }
            }
        }
        return units;

//		List<Unit> units = new ArrayList<>();
//		final int x0 = Math.max((position.getX() - radius) / cellSize, 0);
//		final int x1 = Math.min((position.getX() + radius) / cellSize, cols-1);
//		final int y0 = Math.max((position.getY() - radius) / cellSize, 0);
//		final int y1 = Math.min((position.getY() + radius) / cellSize, rows-1);
//		final int radiusSq = radius * radius;
//		for (int y = y0; y <= y1; ++y)
//		{
//			for (int x = x0; x <= x1; ++x)
//			{
//				int row = y;
//				int col = x;
//
//				GridCell cell = getCellByIndex(row, col);
//				if (ourUnits)
//				{
//					for (Unit unit : cell.ourUnits)
//					{
//						Position d = new Position(unit.getPosition().getX() - position.getX(), unit.getPosition().getY() - position.getY());
//						if (d.getX() * d.getX() + d.getY() * d.getY() <= radiusSq)
//						{
//							if ((unitType == null || unit.getType() == unitType) && !units.contains(unit))
//							{
//								units.add(unit);
//							}
//						}
//					}
//				}
//				if (oppUnits)
//				{
//					for (Unit unit : cell.oppUnits)
//					{
//						if (unit.getType() != UnitType.Unknown && unit.isVisible())
//						{
//							Position d = new Position(unit.getPosition().getX() - position.getX(), unit.getPosition().getY() - position.getY());
//							if (d.getX() * d.getX() + d.getY() * d.getY() <= radiusSq)
//							{
//								if ((unitType == null || unit.getType() == unitType) && !units.contains(unit))
//								{
//									units.add(unit);
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		return units;
    }

    // The bot scanned the given position. Record it so we don't scan the same position
    // again before it wears off.
    public void scanAtPosition(Position pos) {
        GridCell cell = getCell(pos);
        cell.timeLastScan = TimeUtils.getFrame();

        this.lastScanGridCell = cell;
    }

    public boolean scanIsActiveAt(Position pos) {
        GridCell cell = getCell(pos);
        return cell.timeLastScan + StrategyConfig.SCAN_DURATION > TimeUtils.getFrame();
    }

    public int getCellLastVisitDuration(Position pos) {

        int tpx = pos.getX() / cellSize;
        int tpy = pos.getY() / cellSize;
        int latesttime = 0;


        //@@@@@@ 해당 로직이 과연 맞을까?
        for (int i = -5; i <= 5; i++) {
            for (int j = -5; j <= 5; j++) {
                if (i * i + j * j > 26) {
                    continue;
                }
                if ((tpy + i) * cols + (tpx + j) < 0) {
                    continue;
                }
                if (gridCells[(tpy + i) * cols + (tpx + j)].timeLastVisited > latesttime) {
                    latesttime = gridCells[(tpy + i) * cols + (tpx + j)].timeLastVisited;
                }
            }
        }

        if (latesttime == 0) {
            return 100000000;
        }
//		System.out.println("timeLastVisited returning: " + (TimeUtils.getFrame() - latesttime));
        return TimeUtils.getFrame() - latesttime;
    }


    //	public GridCell getCell(Position pos)
//	{
//		return getCellByIndex(pos.getY() / cellSize, pos.getX() / cellSize);
//	}
//	public GridCell getCellByIndex(int r, int c)
//	{
//		return gridCells[r * cols + c];	
//	}	

    public int getCellSize() {
        return cellSize;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    /// 지도를 바둑판처럼 Cell 들로 나누기 위해서 정의한 하나의 Cell
    public class GridCell {
        private int timeLastVisited;        ///< 가장 마지막에 방문했던 시각이 언제인지 -> Scout 에 활용

        private int timeLastOpponentSeen;    ///< 가장 마지막에 적을 발견했던 시각이 언제인지 -> 적 의도 파악, 적 부대 파악, 전략 수립에 활용
        private int timeLastScan;
        private List<Unit> ourUnits = new ArrayList<Unit>();
        private List<Unit> oppUnits = new ArrayList<Unit>();
        private Position center;

        public GridCell() {
            timeLastScan = -StrategyConfig.SCAN_DURATION;
            timeLastVisited = 0;
            timeLastOpponentSeen = 0;
        }

        public Position getCenter() {
            return center;
        }

        public int getTimeLastVisited() {
            return timeLastVisited;
        }

        public int getTimeLastScan() {
            return timeLastScan;
        }

    }
}