package org.monster.build.constant;

/// 봇 프로그램 설정
public class BuildConfig {
    // 유닛생산을 하지 않을 정도의 생산기지 hitpoint
    public static final int UNIT_PRODUCE_HITPOINT = 80;

    // 최대로 생산할 수 있는 일꾼수
    public static final int MAX_WORKER_COUNT = 65;

    /// MapGrid 에서 한 개 GridCell 의 size
    public static final int MAP_GRID_SIZE = 256;

    /// StarCraft 및 BWAPI 에서 1 Tile = 32 * 32 Point (Pixel) 입니다<br>
    /// Position 은 Point (Pixel) 단위이고, TilePosition 은 Tile 단위입니다
    public static final int TILE_SIZE = 32;
    /// 건물과 건물간 띄울 최소한의 간격 - Protoss_Pylon 건물의 경우 - 게임 초기에
    public static final int BUILDING_PYLON_EARLY_STAGE_SPACING = 4;
    /// 건물과 건물간 띄울 최소한의 간격 - Protoss_Pylon 건물의 경우 - 게임 초기 이후에
    public static final int BUILDING_PYLON_SPACING = 2;
    /// 건물과 건물간 띄울 최소한의 간격 - Terran_Supply_Depot 건물의 경우
    public static final int BUILDING_SUPPLY_DEPOT_SPACING = 1;
    /// 건물과 건물간 띄울 최소한의 간격 - 방어 건물의 경우 (포톤캐논. 성큰콜로니. 스포어콜로니. 터렛. 벙커)
    public static final int BUILDING_DEFENSE_TOWER_SPACING = 1;
    /// 건물과 건물간 띄울 최소한의 간격 - 일반적인 건물의 경우
    public static int buildingSpacing = 1;
    /// 건물과 건물간 띄울 최소한의 간격 - ResourceDepot 건물의 경우 (Nexus, Hatchery, Command CENTER_POS)
    public static int buildingResourceDepotSpacing = 1;
	
	
	/*public static int BuildingSpacing = 0;
	/// 건물과 건물간 띄울 최소한의 간격 - ResourceDepot 건물의 경우 (Nexus, Hatchery, Command CENTER_POS)
	public static int BuildingResourceDepotSpacing = 0;
	/// 건물과 건물간 띄울 최소한의 간격 - Protoss_Pylon 건물의 경우 - 게임 초기에
	public static int BuildingPylonEarlyStageSpacing = 4;
	/// 건물과 건물간 띄울 최소한의 간격 - Protoss_Pylon 건물의 경우 - 게임 초기 이후에
	public static int BuildingPylonSpacing = 2;
	/// 건물과 건물간 띄울 최소한의 간격 - Terran_Supply_Depot 건물의 경우
	public static int BuildingSupplyDepotSpacing = 0;
	/// 건물과 건물간 띄울 최소한의 간격 - 방어 건물의 경우 (포톤캐논. 성큰콜로니. 스포어콜로니. 터렛. 벙커)
	public static int BuildingDefenseTowerSpacing = 0;*/
}