package org.monster.common.util;

import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import org.monster.common.util.internal.MapTools;
import org.monster.main.Monster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ScoutInfoCollector implements InfoCollector{

    private static ScoutInfoCollector instance = new ScoutInfoCollector();
    protected static ScoutInfoCollector Instance() {
        return instance;
    }
    Game Broodwar;

    /*Info*/
    private static Map<Position, Vector<Position>> baseRegionVerticesMap = new HashMap<>();

    @Override
    public void onStart(Game Broodwar) {
        this.Broodwar = Broodwar;
        updateBaseRegionVerticesMap();
    }

    @Override
    public void update() {

    }

    private void updateBaseRegionVerticesMap() {
		for (BaseLocation base : BWTA.getStartLocations()) {
			calculateEnemyRegionVertices(base);
		}
	}

    protected Vector<Position> getRegionVertices(BaseLocation base) {
        return baseRegionVerticesMap.get(base.getPosition());
    }

    // Enemy MainBaseLocation 이 있는 Region 의 가장자리를 enemyBaseRegionVertices 에 저장한다
    // Region 내 모든 건물을 Eliminate 시키기 위한 지도 탐색 로직 작성시 참고할 수 있다
    protected void calculateEnemyRegionVertices(BaseLocation base) {
        if (base == null) {
            return;
        }
        Region enemyRegion = base.getRegion();
        if (enemyRegion == null) {
            return;
        }

        Vector<Position> regionVertices = new Vector<>();

        final Position basePosition = Monster.Broodwar.self().getStartLocation().toPosition();
        final Vector<TilePosition> closestTobase = MapTools.Instance().getClosestTilesTo(basePosition);
        Set<Position> unsortedVertices = new HashSet<Position>();

        // check each tile position
        for (final TilePosition tp : closestTobase) {
            if (BWTA.getRegion(tp) != enemyRegion) {
                continue;
            }

            // a tile is 'surrounded' if
            // 1) in all 4 directions there's a tile position in the current region
            // 2) in all 4 directions there's a buildable tile
            boolean surrounded = true;
            if (BWTA.getRegion(new TilePosition(tp.getX() + 1, tp.getY())) != enemyRegion
                    || !Monster.Broodwar.isBuildable(new TilePosition(tp.getX() + 1, tp.getY()))
                    || BWTA.getRegion(new TilePosition(tp.getX(), tp.getY() + 1)) != enemyRegion
                    || !Monster.Broodwar.isBuildable(new TilePosition(tp.getX(), tp.getY() + 1))
                    || BWTA.getRegion(new TilePosition(tp.getX() - 1, tp.getY())) != enemyRegion
                    || !Monster.Broodwar.isBuildable(new TilePosition(tp.getX() - 1, tp.getY()))
                    || BWTA.getRegion(new TilePosition(tp.getX(), tp.getY() - 1)) != enemyRegion
                    || !Monster.Broodwar.isBuildable(new TilePosition(tp.getX(), tp.getY() - 1))) {
                surrounded = false;
            }

            // push the tiles that aren't surrounded
            // Region의 가장자리 타일들만 추가한다
            if (!surrounded && Monster.Broodwar.isBuildable(tp)) {
                unsortedVertices.add(new Position(tp.toPosition().getX() + 16, tp.toPosition().getY() + 16));
            }
        }

        Vector<Position> sortedVertices = new Vector<Position>();
        Position current = unsortedVertices.iterator().next();
        regionVertices.add(current);
        unsortedVertices.remove(current);

        // while we still have unsorted vertices left, find the closest one remaining to
        // current
        while (!unsortedVertices.isEmpty()) {
            double bestDist = 1000000;
            Position bestPos = null;

            for (final Position pos : unsortedVertices) {
                double dist = pos.getDistance(current);

                if (dist < bestDist) {
                    bestDist = dist;
                    bestPos = pos;
                }
            }

            current = bestPos;
            sortedVertices.add(bestPos);
            unsortedVertices.remove(bestPos);
        }

        // let's close loops on a threshold, eliminating death grooves
        int distanceThreshold = 100;

        while (true) {
            // find the largest index difference whose distance is less than the threshold
            int maxFarthest = 0;
            int maxFarthestStart = 0;
            int maxFarthestEnd = 0;

            // for each starting vertex
            for (int i = 0; i < (int) sortedVertices.size(); ++i) {
                int farthest = 0;
                int farthestIndex = 0;

                // only test half way around because we'll find the other one on the way back
                for (int j = 1; j < sortedVertices.size() / 2; ++j) {
                    int jindex = (i + j) % sortedVertices.size();

                    if (sortedVertices.get(i).getDistance(sortedVertices.get(jindex)) < distanceThreshold) {
                        farthest = j;
                        farthestIndex = jindex;
                    }
                }

                if (farthest > maxFarthest) {
                    maxFarthest = farthest;
                    maxFarthestStart = i;
                    maxFarthestEnd = farthestIndex;
                }
            }

            // stop when we have no long chains within the threshold
            if (maxFarthest < 4) {
                break;
            }

            double dist = sortedVertices.get(maxFarthestStart).getDistance(sortedVertices.get(maxFarthestEnd));

            Vector<Position> temp = new Vector<Position>();

            for (int s = maxFarthestEnd; s != maxFarthestStart; s = (s + 1) % sortedVertices.size()) {

                temp.add(sortedVertices.get(s));
            }

            sortedVertices = temp;
        }

        regionVertices = sortedVertices;
        baseRegionVerticesMap.put(base.getPosition(), regionVertices);
    }
}
