package prebot.main;

import bwapi.Color;

public class Config {

    public static final String BotName = "Monster";
    public static final String BotAuthors = "Monster";
    public static final Color ColorLineTarget = Color.White;
    public static final Color ColorLineMineral = Color.Cyan;
    public static final Color ColorUnitNearEnemy = Color.Red;

    public static final Color ColorUnitNotNearEnemy = Color.Green;
    public static String LogFilename = BotName + "_LastGameLog.dat";
    public static String ReadDirectory = "c:\\starcraft\\bwapi-data\\read\\";
    public static String WriteDirectory = "c:\\starcraft\\bwapi-data\\write\\";

    public static int SetLocalSpeed = 20;
    public static int SetFrameSkip = 0;
    public static boolean EnableUserInput = true;
    public static boolean EnableCompleteMapInformation = false;
    public static int MAP_GRID_SIZE = 32;
    public static int TILE_SIZE = 32;
    public static int WorkersPerRefinery = 3;
    public static int BuildingSpacing = 2;
    public static int BuildingResourceDepotSpacing = 0;
    public static int BuildingPylonEarlyStageSpacing = 4;
    public static int BuildingPylonSpacing = 2;
    public static int BuildingSupplyDepotSpacing = 0;
    public static int BuildingDefenseTowerSpacing = 0;

    public static boolean DrawGameInfo = true;
    public static boolean DrawResourceInfo = false;
    public static boolean DrawBWTAInfo = true;
    public static boolean DrawMapGrid = false;
    public static boolean DrawUnitHealthBars = true;
    public static boolean DrawEnemyUnitInfo = true;
    public static boolean DrawUnitTargetInfo = true;
    public static boolean DrawProductionInfo = true;
    public static boolean DrawBuildingInfo = true;
    public static boolean DrawReservedBuildingTiles = false;
    public static boolean DrawScoutInfo = true;
    public static boolean DrawWorkerInfo = true;
    public static boolean DrawMouseCursorInfo = true;
}