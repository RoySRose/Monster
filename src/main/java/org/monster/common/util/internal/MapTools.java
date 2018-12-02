package org.monster.common.util.internal;

import bwapi.Position;
import bwapi.TilePosition;
import org.monster.common.DistanceMap;
import org.monster.bootstrap.Monster;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/// provides useful tools for analyzing the starcraft map<br>
/// calculates connectivity and distances using flood fills
public class MapTools {

    private static MapTools instance = new MapTools();
    /// a cache of already computed distance maps
    private Map<Position, DistanceMap> allMaps = new HashMap<Position, DistanceMap>();
    /// the map stored at TilePosition resolution, values are 0/1 for walkable or not walkable
    private boolean[] map;
    /// map that stores whether a unit is on this position
    private boolean[] units;
    /// the fringe vector which is used as a sort of 'open list'
    private int[] fringe;
    private int rows;
    private int cols;

    // constructor for MapTools
    public MapTools() {
        rows = Monster.Broodwar.mapHeight();
        cols = Monster.Broodwar.mapWidth();
        map = new boolean[rows * cols];
        units = new boolean[rows * cols];
        fringe = new int[rows * cols];

        setBWAPIMapData();
    }

    /// static singleton 객체를 리턴합니다
    public static MapTools Instance() {
        return instance;
    }

    /// return the index of the 1D array from (row,col)
    private int getIndex(int row, int col) // inline
    {
        return row * cols + col;
    }

    private final boolean unexplored(DistanceMap dmap, final int index) {
        return (index != -1) && dmap.getDistItem(index) == -1 && map[index];
    }

    /// resets the distance and fringe vectors, call before each search
    private void reset() {
        // C+ : std::fill(_fringe.begin(),_fringe.end(),0);
        for (int i = 0; i < fringe.length; i++) {
            fringe[i] = 0;
        }
    }

    /// reads in the map data from bwapi and stores it in our map format
    private void setBWAPIMapData() {
        try {
            // for each row and column
            for (int r = 0; r < rows; ++r) {
                for (int c = 0; c < cols; ++c) {
                    boolean clear = true;

                    // check each walk tile within this TilePosition
                    for (int i = 0; i < 4; ++i) {
                        for (int j = 0; j < 4; ++j) {
                            if (!Monster.Broodwar.isWalkable(c * 4 + i, r * 4 + j)) {
                                clear = false;
                                break;
                            }

                            if (clear) {
                                break;
                            }
                        }
                    }
                    // set the map as binary clear or not
                    map[getIndex(r, c)] = clear;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetFringe() {
        // C+ : std::fill(_fringe.begin(),_fringe.end(),0);
        for (int i = 0; i < fringe.length; i++) {
            fringe[i] = 0;
        }
    }

    /// from 에서 to 까지 지상유닛이 이동할 경우의 거리 (walk distance)
    public int getGroundDistance(Position origin, Position destination) {
        // if we have too many maps, reset our stored maps in case we run out of memory
        if (allMaps.size() > 20) {
            allMaps.clear();
            // Monster.game.printf("Cleared stored distance map cache");
        }

        // if we haven't yet computed the distance map to the destination
        if (!allMaps.containsKey(destination)) {
            // if we have computed the opposite direction, we can use that too
            if (allMaps.containsKey(origin)) {
                return allMaps.get(origin).getDistItem(destination);
            }

            // add the map and compute it
            allMaps.put(destination, new DistanceMap());
            computeDistance(allMaps.get(destination), destination);
        }

        // get the distance from the map
        return allMaps.get(destination).getDistItem(origin);
    }

    /// computes walk distance from Position P to all other points on the map
    private void computeDistance(DistanceMap dmap, final Position p) {
        search(dmap, p.getY() / 32, p.getX() / 32);
    }

    // does the dynamic programming search
    private void search(DistanceMap dmap, final int sR, final int sC) {
        // reset the internal variables
        resetFringe();

        // set the starting position for this search
        dmap.setStartPosition(sR, sC);

        // set the distance of the start cell to zero
        dmap.setDistance(getIndex(sR, sC), 0);

        // set the fringe variables accordingly
        int fringeSize = 1;
        int fringeIndex = 0;
        fringe[0] = getIndex(sR, sC);
        dmap.addSorted(getTilePosition(fringe[0]));

        // temporary variables used in search loop
        int currentIndex, nextIndex;
        int newDist;

        // the size of the map
        int size = rows * cols;

        // while we still have things left to expand
        while (fringeIndex < fringeSize) {
            // grab the current index to expand from the fringe
            currentIndex = fringe[fringeIndex++];
            newDist = dmap.getDistItem(currentIndex) + 1;

            // search up
            nextIndex = (currentIndex > cols) ? (currentIndex - cols) : -1;
            if (unexplored(dmap, nextIndex)) {
                // set the distance based on distance to current cell
                dmap.setDistance(nextIndex, newDist);
                dmap.setMoveTo(nextIndex, 'D');
                dmap.addSorted(getTilePosition(nextIndex));

                // put it in the fringe
                fringe[fringeSize++] = nextIndex;
            }

            // search down
            nextIndex = (currentIndex + cols < size) ? (currentIndex + cols) : -1;
            if (unexplored(dmap, nextIndex)) {
                // set the distance based on distance to current cell
                dmap.setDistance(nextIndex, newDist);
                dmap.setMoveTo(nextIndex, 'U');
                dmap.addSorted(getTilePosition(nextIndex));

                // put it in the fringe
                fringe[fringeSize++] = nextIndex;
            }

            // search left
            nextIndex = (currentIndex % cols > 0) ? (currentIndex - 1) : -1;
            if (unexplored(dmap, nextIndex)) {
                // set the distance based on distance to current cell
                dmap.setDistance(nextIndex, newDist);
                dmap.setMoveTo(nextIndex, 'R');
                dmap.addSorted(getTilePosition(nextIndex));

                // put it in the fringe
                fringe[fringeSize++] = nextIndex;
            }

            // search right
            nextIndex = (currentIndex % cols < cols - 1) ? (currentIndex + 1) : -1;
            if (unexplored(dmap, nextIndex)) {
                // set the distance based on distance to current cell
                dmap.setDistance(nextIndex, newDist);
                dmap.setMoveTo(nextIndex, 'L');
                dmap.addSorted(getTilePosition(nextIndex));

                // put it in the fringe
                fringe[fringeSize++] = nextIndex;
            }
        }
    }

    /// Position 에서 가까운 순서대로 타일의 목록을 반환한다
    public final Vector<TilePosition> getClosestTilesTo(Position pos) {
        // make sure the distance map is calculated with pos as a destination
        int a = getGroundDistance(Monster.Broodwar.self().getStartLocation().toPosition(), pos);

        return allMaps.get(pos).getSortedTiles();
    }

    private TilePosition getTilePosition(int index) {
        return new TilePosition(index % cols, index / cols);
    }
}