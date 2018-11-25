package prebot.build.initialProvider.BlockingEntrance;

import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import prebot.main.Monster;
import prebot.strategy.InformationManager;
import prebot.strategy.MapSpecificInformation;

import java.util.HashMap;

//
public class BlockingEntrance {

    public static boolean entranceBlock = true;
    private static BlockingEntrance instance = new BlockingEntrance();
    private static Location loc_t = null;
    private final int SMALL = 42;
    private final int BIG = 84;
    ;
    private final int CENTER = 64;

    //필요한것
    //스타팅 위치
    //첫 서플 위치
    //배럭 위치
    //첫팩 위치
    //투서플 위치
    //테란&플토는 입막, 저그는 심시티
    public int maxSupplyCntX = 3;
    public int maxSupplyCntY = 3;
    //	xinc 는 x축의 증감여부(true 면 증가)
    public boolean xinc;
    //	yinc 는 y축의 증감여부(true 면 증가)
    public boolean yinc;
    public TilePosition starting = TilePosition.None;
    public TilePosition first_supple = TilePosition.None;
    public TilePosition second_supple = TilePosition.None;
    public TilePosition barrack = TilePosition.None;
    public TilePosition factory = TilePosition.None;
    public TilePosition bunker1 = TilePosition.None;
    public TilePosition bunker2 = TilePosition.None;
    public TilePosition entrance_turret1 = TilePosition.None;

    //public TilePosition entrance_turret = TilePosition.None;
    public TilePosition entrance_turret2 = TilePosition.None;

//    private static int first_suppleX_array[] = null;// new int [];//{29, 52, 96, 102, 93, 55, 12, 23};
//    private static int first_suppleY_array[] = null;//new int []{19, 23, 21,   61, 95, 94, 97, 54};
//
//    private static int second_suppleX_array[] = null; //new int []{29, 52, 96, 102, 93, 55, 12, 23};
//    private static int second_suppleY_array[] = null; //new int []{19, 23, 21,   61, 95, 94, 97, 54};
//
//    private static int barrackX_array[] = null; //new int []{26, 54, 98, 104, 90, 52, 14, 20};
//    private static int barrackY_array[] = null; //new int []{21, 25, 23,   63, 97, 96, 99, 56};
//
//    private static int factoryX_array[] = null; //new int []{26, 54, 98, 104, 90, 52, 14, 20};
//    private static int factoryY_array[] = null; //new int []{21, 25, 23,   63, 97, 96, 99, 56};
//
//    private static int bunkerX_array[] = null; //new int []{26, 54, 98, 104, 90, 52, 14, 20};
//    private static int bunkerY_array[] = null; //new int []{21, 25, 23,   63, 97, 96, 99, 56};
//
//    private static int fix_supplyX[] = null; //new int []{26, 54, 98, 104, 90, 52, 14, 20};
//    private static int fix_supplyY[] = null; //new int []{21, 25, 23,   63, 97, 96, 99, 56};

//    private int starting_int = 0;
    public TilePosition supply_area = TilePosition.None;
    public TilePosition starport1 = TilePosition.None;
    public TilePosition starport2 = TilePosition.None;
    public Location loc = Location.START;
    private Map mapName = null;
    private HashMap<Integer, TilePosition> postitionStorage = new HashMap<>();
    
    /*private static int fix_supplyX[] = null; //new int []{26, 54, 98, 104, 90, 52, 14, 20};
	private static int fix_supplyY[] = null; //new int []{21, 25, 23,   63, 97, 96, 99, 56};*/

    public static BlockingEntrance Instance() {
        return instance;
    }

    public void onStart() {
        setInitialPosition();
        setBlockingEntrance();
        SetBlockingTilePosition();
    }

    private void setInitialPosition() {


        //starting position..... needed?
        for (Unit unit : Monster.Broodwar.self().getUnits()) {
            if (unit.getType() == UnitType.Terran_Command_Center) {

                //System.out.println("unit.getTilePosition().getX() ==>> " + unit.getTilePosition().getX() + "  //  unit.getTilePosition().getY() ==>> " +unit.getTilePosition().getY());
                starting = new TilePosition(unit.getTilePosition().getX(), unit.getTilePosition().getY());
            }
        }

        //TODO MAP, 지도의 ABCD 이름에 맞춰 바꾸면 될듯
//        mapName = Map.CIRCUITBREAKER;
        if (InformationManager.Instance().getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.FIGHTING_SPIRITS) {
            mapName = Map.FIGHTING_SPIRITS;
        } else if (InformationManager.Instance().getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.CIRCUITBREAKER) {
            mapName = Map.CIRCUITBREAKER;
        } else {
            mapName = Map.UNKNOWN;
        }
        //System.out.println("this map ==>> " + map.toString());

        if (starting.getX() < SMALL
                && starting.getY() < SMALL) {
            loc = Location.Eleven;
            xinc = true;
            yinc = true;
        }
        if (SMALL < starting.getX() && starting.getX() < BIG
                && starting.getY() < SMALL) {
            loc = Location.Twelve;
        }
        if (BIG < starting.getX()
                && starting.getY() < SMALL) {
            loc = Location.One;
            xinc = false;
            yinc = true;
        }
        if (starting.getX() < SMALL
                && SMALL < starting.getY() && starting.getY() < BIG) {
            loc = Location.Nine;
        }
        //center
        if (SMALL < starting.getX() && starting.getX() < BIG
                && SMALL < starting.getY() && starting.getY() < BIG) {
            loc = Location.Twelve;
        }
        if (BIG < starting.getX()
                && SMALL < starting.getY() && starting.getY() < BIG) {
            loc = Location.Three;
        }
        if (starting.getX() < SMALL
                && starting.getY() > BIG) {
            loc = Location.Seven;
            xinc = true;
            yinc = false;
        }
        if (SMALL < starting.getX() && starting.getX() < BIG
                && starting.getY() > SMALL) {
            loc = Location.Six;
        }
        if (BIG < starting.getX()
                && starting.getY() > BIG) {
            loc = Location.Five;
            xinc = false;
            yinc = false;
        }

        if (InformationManager.Instance().getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.FIGHTING_SPIRITS) {
            xinc = true;
            yinc = true;
        }


    }

    private final int combine(Map map, Location location, Building building) {
        return map.getValue() * 100 + location.getValue() * 10 + building.getValue();
    }

    public void SetBlockingTilePosition() {

//		서킷브레이커만 4X4
        if (InformationManager.Instance().getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.CIRCUITBREAKER) {
            maxSupplyCntX = 7;
            maxSupplyCntY = 2;
        } else if (InformationManager.Instance().getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.FIGHTING_SPIRITS) {
            maxSupplyCntX = 2;
            maxSupplyCntY = 7;
        }

        first_supple = postitionStorage.get(combine(mapName, loc, Building.FIRST_SUPPLY));
        second_supple = postitionStorage.get(combine(mapName, loc, Building.SECOND_SUPPLY));
        barrack = postitionStorage.get(combine(mapName, loc, Building.BARRACK));
        factory = postitionStorage.get(combine(mapName, loc, Building.FACTORY));
        bunker1 = postitionStorage.get(combine(mapName, loc, Building.BUNKER1));
        bunker2 = postitionStorage.get(combine(mapName, loc, Building.BUNKER2));
        entrance_turret1 = postitionStorage.get(combine(mapName, loc, Building.ENTRANCE_TURRET1));
        entrance_turret2 = postitionStorage.get(combine(mapName, loc, Building.ENTRANCE_TURRET2));
        starport1 = postitionStorage.get(combine(mapName, loc, Building.STARPORT1));
        starport2 = postitionStorage.get(combine(mapName, loc, Building.STARPORT2));

        supply_area = postitionStorage.get(combine(mapName, loc, Building.SUPPLY_AREA));

        loc_t = loc;

//        System.out.println("this map & location ==>>>>  " + mapName + " : " + loc);
//        System.out.println("xinc ==> " + xinc + " // yinc ==> " + yinc);

    }

    public void setBlockingEntrance() {


//    	기존 프리봇1 에서 입구 터렛 위치 추가


//    	맵 : Over_wath
        if (InformationManager.Instance().getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.OVERWATCH) {
    		
    		
			/*int[] fix_supplyXX = { 0, 115, 94, 0, 21 };
			fix_supplyX = fix_supplyXX;
			int[] fix_supplyYY = { 0, 28, 121, 95, 0 };
			fix_supplyY = fix_supplyYY;*/

            postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.SUPPLY_AREA), new TilePosition(115, 25));
            postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.SUPPLY_AREA), new TilePosition(94, 121));
            postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.SUPPLY_AREA), new TilePosition(0, 95));
            postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.SUPPLY_AREA), new TilePosition(21, 0));

            if (InformationManager.Instance().enemyRace == Race.Protoss
                    || InformationManager.Instance().enemyRace == Race.Terran) {
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.FIRST_SUPPLY), new TilePosition(108, 14));
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.BARRACK), new TilePosition(104, 15));
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.SECOND_SUPPLY), new TilePosition(110, 12));
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.FACTORY), new TilePosition(115, 21));
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.BUNKER1), new TilePosition(108, 16));
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.ENTRANCE_TURRET1), new TilePosition(111, 14));
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.ENTRANCE_TURRET2), TilePosition.None);


                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.FIRST_SUPPLY), new TilePosition(113, 107));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.BARRACK), new TilePosition(109, 107));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.SECOND_SUPPLY), new TilePosition(107, 105));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.FACTORY), new TilePosition(107, 115));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.BUNKER1), new TilePosition(113, 109));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.ENTRANCE_TURRET1), new TilePosition(116, 108));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.ENTRANCE_TURRET2), TilePosition.None);


                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.FIRST_SUPPLY), new TilePosition(20, 110));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.BARRACK), new TilePosition(16, 112));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.SECOND_SUPPLY), new TilePosition(23, 110));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.FACTORY), new TilePosition(7, 105));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.BUNKER1), new TilePosition(20, 108));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.ENTRANCE_TURRET1), new TilePosition(18, 110));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.ENTRANCE_TURRET2), TilePosition.None);


                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.FIRST_SUPPLY), new TilePosition(16, 20));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.BARRACK), new TilePosition(12, 18));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.SECOND_SUPPLY), new TilePosition(18, 22));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.FACTORY), new TilePosition(17, 10));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.BUNKER1), new TilePosition(16, 18));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.ENTRANCE_TURRET1), new TilePosition(19, 20));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.ENTRANCE_TURRET2), TilePosition.None);
            } else {

                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.FIRST_SUPPLY), new TilePosition(114, 13));
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.BARRACK), new TilePosition(116, 15));
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.SECOND_SUPPLY), new TilePosition(120, 44));
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.FACTORY), new TilePosition(120, 16));
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.BUNKER1), new TilePosition(117, 13));
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.ENTRANCE_TURRET1), TilePosition.None);
                postitionStorage.put(combine(Map.OVERWATCH, Location.One, Building.ENTRANCE_TURRET2), TilePosition.None);


                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.FIRST_SUPPLY), new TilePosition(121, 113));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.BARRACK), new TilePosition(113, 112));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.SECOND_SUPPLY), new TilePosition(113, 115));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.FACTORY), new TilePosition(117, 110));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.BUNKER1), new TilePosition(116, 115));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.ENTRANCE_TURRET1), TilePosition.None);
                postitionStorage.put(combine(Map.OVERWATCH, Location.Five, Building.ENTRANCE_TURRET2), TilePosition.None);

                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.FIRST_SUPPLY), new TilePosition(11, 113));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.BARRACK), new TilePosition(11, 115));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.SECOND_SUPPLY), new TilePosition(4, 113));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.FACTORY), new TilePosition(7, 110));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.BUNKER1), new TilePosition(6, 115));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.ENTRANCE_TURRET1), TilePosition.None);
                postitionStorage.put(combine(Map.OVERWATCH, Location.Seven, Building.ENTRANCE_TURRET2), TilePosition.None);

                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.FIRST_SUPPLY), new TilePosition(0, 15));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.BARRACK), new TilePosition(10, 14));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.SECOND_SUPPLY), new TilePosition(7, 15));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.FACTORY), new TilePosition(3, 16));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.BUNKER1), new TilePosition(7, 13));
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.ENTRANCE_TURRET1), TilePosition.None);
                postitionStorage.put(combine(Map.OVERWATCH, Location.Eleven, Building.ENTRANCE_TURRET2), TilePosition.None);

            }


        } else if (InformationManager.Instance().getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.CIRCUITBREAKER) {

            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.SUPPLY_AREA), new TilePosition(115, 0));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.SUPPLY_AREA), new TilePosition(115, 125));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.SUPPLY_AREA), new TilePosition(10, 125));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.SUPPLY_AREA), new TilePosition(10, 0));

            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.STARPORT1), new TilePosition(118, 0));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.STARPORT2), new TilePosition(121, 3));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.STARPORT1), new TilePosition(106, 107));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.STARPORT2), new TilePosition(104, 110));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.STARPORT1), new TilePosition(13, 119));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.STARPORT2), new TilePosition(20, 119));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.STARPORT1), new TilePosition(0, 0));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.STARPORT2), new TilePosition(0, 4));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.ENTRANCE_TURRET1), new TilePosition(116, 23));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.ENTRANCE_TURRET2), TilePosition.None);
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.ENTRANCE_TURRET1), new TilePosition(116, 103));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.ENTRANCE_TURRET2), TilePosition.None);
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.ENTRANCE_TURRET1), new TilePosition(10, 103));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.ENTRANCE_TURRET2), TilePosition.None);
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.ENTRANCE_TURRET1), new TilePosition(11, 23));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.ENTRANCE_TURRET2), TilePosition.None);


//    	맵 : CIRCUITBREAKER
            if (InformationManager.Instance().enemyRace == Race.Protoss
                    || InformationManager.Instance().enemyRace == Race.Terran) {
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.FIRST_SUPPLY), new TilePosition(122, 24));
                //postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.FIRST_SUPPLY)   , new TilePosition(125, 24));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.BARRACK), new TilePosition(118, 23));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.SECOND_SUPPLY), new TilePosition(125, 25));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.FACTORY), new TilePosition(113, 15));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.BUNKER1), new TilePosition(122, 22));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.BUNKER2), new TilePosition(107, 34));


                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.FIRST_SUPPLY), new TilePosition(125, 100));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.BARRACK), new TilePosition(118, 102));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.SECOND_SUPPLY), new TilePosition(122, 101));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.FACTORY), new TilePosition(111, 118));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.BUNKER1), new TilePosition(122, 103));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.BUNKER2), new TilePosition(106, 93));


                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.FIRST_SUPPLY), new TilePosition(7, 102));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.BARRACK), new TilePosition(0, 101));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.SECOND_SUPPLY), new TilePosition(4, 102));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.FACTORY), new TilePosition(11, 116));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.BUNKER1), new TilePosition(4, 103));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.BUNKER2), new TilePosition(18, 93));


                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.FIRST_SUPPLY), new TilePosition(8, 23));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.BARRACK), new TilePosition(1, 24));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.SECOND_SUPPLY), new TilePosition(5, 23));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.FACTORY), new TilePosition(12, 13));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.BUNKER1), new TilePosition(5, 21));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.BUNKER2), new TilePosition(17, 34));


            } else {

                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.FIRST_SUPPLY), new TilePosition(119, 16));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.BARRACK), new TilePosition(115, 14));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.SECOND_SUPPLY), new TilePosition(114, 9));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.FACTORY), new TilePosition(113, 11));

                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.BUNKER1), new TilePosition(119, 14));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.BUNKER2), new TilePosition(122, 25));
//    			postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.ENTRANCE_TURRET1), TilePosition.None);
//    			postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.ENTRANCE_TURRET2), TilePosition.None);


                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.FIRST_SUPPLY), new TilePosition(112, 115));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.BARRACK), new TilePosition(113, 112));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.SECOND_SUPPLY), new TilePosition(122, 113));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.FACTORY), new TilePosition(111, 118));

                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.BUNKER1), new TilePosition(115, 115));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.BUNKER2), new TilePosition(120, 101));
//    			postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.ENTRANCE_TURRET1), TilePosition.None);
//    			postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.ENTRANCE_TURRET2), TilePosition.None);


                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.FIRST_SUPPLY), new TilePosition(7, 111));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.BARRACK), new TilePosition(3, 111));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.SECOND_SUPPLY), new TilePosition(1, 115));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.FACTORY), new TilePosition(11, 116));

                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.BUNKER1), new TilePosition(5, 115));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.BUNKER2), new TilePosition(4, 101));
//    			postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.ENTRANCE_TURRET1), TilePosition.None);
//    			postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.ENTRANCE_TURRET2), TilePosition.None);


                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.FIRST_SUPPLY), new TilePosition(8, 16));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.BARRACK), new TilePosition(11, 16));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.SECOND_SUPPLY), new TilePosition(4, 16));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.FACTORY), new TilePosition(11, 13));

                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.BUNKER1), new TilePosition(8, 14));
                postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.BUNKER2), new TilePosition(3, 25));
//    			postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.ENTRANCE_TURRET1), TilePosition.None);
//    			postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.ENTRANCE_TURRET2), TilePosition.None);

            }

            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.One, Building.BARRACK_LAND), new TilePosition(106, 31));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Five, Building.BARRACK_LAND), new TilePosition(105, 95));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Seven, Building.BARRACK_LAND), new TilePosition(18, 95));
            postitionStorage.put(combine(Map.CIRCUITBREAKER, Location.Eleven, Building.BARRACK_LAND), new TilePosition(17, 31));
        } else if (InformationManager.Instance().getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.FIGHTING_SPIRITS) {

//    		System.out.println("맵 ==>> 투혼");


            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.SUPPLY_AREA), new TilePosition(121, 18));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.SUPPLY_AREA), new TilePosition(103, 113));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.SUPPLY_AREA), new TilePosition(1, 97));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.SUPPLY_AREA), new TilePosition(17, 0));

            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.STARPORT1), new TilePosition(110, 21));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.STARPORT2), new TilePosition(112, 24));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.STARPORT1), new TilePosition(111, 124));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.STARPORT2), new TilePosition(118, 124));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.STARPORT1), new TilePosition(8, 99));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.STARPORT2), new TilePosition(14, 102));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.STARPORT1), new TilePosition(22, 18));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.STARPORT2), new TilePosition(24, 15));


            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.ENTRANCE_TURRET1), new TilePosition(103, 7));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.ENTRANCE_TURRET2), new TilePosition(100, 17));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.ENTRANCE_TURRET1), new TilePosition(112, 101));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.ENTRANCE_TURRET2), new TilePosition(104, 97));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.ENTRANCE_TURRET1), new TilePosition(26, 122));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.ENTRANCE_TURRET2), new TilePosition(24, 110));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.ENTRANCE_TURRET1), new TilePosition(13, 26));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.ENTRANCE_TURRET2), new TilePosition(20, 29));


//        	맵 : 투혼
            if (InformationManager.Instance().enemyRace == Race.Protoss
                    || InformationManager.Instance().enemyRace == Race.Terran) {
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.FIRST_SUPPLY), new TilePosition(100, 7));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.BARRACK), new TilePosition(102, 9));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.SECOND_SUPPLY), new TilePosition(97, 5));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.FACTORY), new TilePosition(110, 9));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.BUNKER1), new TilePosition(100, 5));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.BUNKER2), new TilePosition(89, 21));


                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.FIRST_SUPPLY), new TilePosition(118, 100));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.BARRACK), new TilePosition(114, 101));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.SECOND_SUPPLY), new TilePosition(120, 98));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.FACTORY), new TilePosition(115, 109));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.BUNKER1), new TilePosition(118, 102));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.BUNKER2), new TilePosition(103, 90));


                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.FIRST_SUPPLY), new TilePosition(25, 120));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.BARRACK), new TilePosition(21, 118));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.SECOND_SUPPLY), new TilePosition(28, 121));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.FACTORY), new TilePosition(14, 115));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.BUNKER1), new TilePosition(25, 122));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.BUNKER2), new TilePosition(36, 106));


                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.FIRST_SUPPLY), new TilePosition(7, 26));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.BARRACK), new TilePosition(4, 28));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.SECOND_SUPPLY), new TilePosition(10, 26));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.FACTORY), new TilePosition(1, 14));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.BUNKER1), new TilePosition(7, 24));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.BUNKER2), new TilePosition(22, 37));


            } else {

                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.FIRST_SUPPLY), new TilePosition(110, 4));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.BARRACK), new TilePosition(113, 1));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.SECOND_SUPPLY), new TilePosition(110, 2));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.FACTORY), new TilePosition(110, 7));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.BUNKER1), new TilePosition(113, 4));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.BUNKER2), new TilePosition(101, 7));
//    			postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.ENTRANCE_TURRET1), TilePosition.None);
//    			postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.ENTRANCE_TURRET2), TilePosition.None);


                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.FIRST_SUPPLY), new TilePosition(112, 114));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.BARRACK), new TilePosition(113, 111));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.SECOND_SUPPLY), new TilePosition(123, 113));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.FACTORY), new TilePosition(110, 116));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.BUNKER1), new TilePosition(115, 114));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.BUNKER2), new TilePosition(117, 100));
//    			postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.ENTRANCE_TURRET1), TilePosition.None);
//    			postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.ENTRANCE_TURRET2), TilePosition.None);


                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.FIRST_SUPPLY), new TilePosition(10, 122));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.BARRACK), new TilePosition(13, 119));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.SECOND_SUPPLY), new TilePosition(13, 122));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.FACTORY), new TilePosition(6, 123));

                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.BUNKER1), new TilePosition(10, 120));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.BUNKER2), new TilePosition(24, 120));
//    			postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.ENTRANCE_TURRET1), TilePosition.None);
//    			postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.ENTRANCE_TURRET2), TilePosition.None);


                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.FIRST_SUPPLY), new TilePosition(6, 13));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.BARRACK), new TilePosition(1, 13));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.SECOND_SUPPLY), new TilePosition(9, 10));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.FACTORY), new TilePosition(9, 12));

                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.BUNKER1), new TilePosition(5, 11));
                postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.BUNKER2), new TilePosition(7, 27));
//    			postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.ENTRANCE_TURRET1), TilePosition.None);
//    			postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.ENTRANCE_TURRET2), TilePosition.None);

            }

            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.One, Building.BARRACK_LAND), new TilePosition(92, 20));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Five, Building.BARRACK_LAND), new TilePosition(103, 92));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Seven, Building.BARRACK_LAND), new TilePosition(32, 105));
            postitionStorage.put(combine(Map.FIGHTING_SPIRITS, Location.Eleven, Building.BARRACK_LAND), new TilePosition(21, 34));

        } else {

//    		System.out.println("맵 ==>> UNKNOWN");


            postitionStorage.put(combine(Map.UNKNOWN, Location.One, Building.SUPPLY_AREA), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Five, Building.SUPPLY_AREA), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Seven, Building.SUPPLY_AREA), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Eleven, Building.SUPPLY_AREA), TilePosition.None);

            postitionStorage.put(combine(Map.UNKNOWN, Location.One, Building.FIRST_SUPPLY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.One, Building.BARRACK), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.One, Building.SECOND_SUPPLY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.One, Building.FACTORY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.One, Building.BUNKER1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.One, Building.BUNKER2), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.One, Building.ENTRANCE_TURRET1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.One, Building.ENTRANCE_TURRET2), TilePosition.None);


            postitionStorage.put(combine(Map.UNKNOWN, Location.Five, Building.FIRST_SUPPLY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Five, Building.BARRACK), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Five, Building.SECOND_SUPPLY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Five, Building.FACTORY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Five, Building.BUNKER1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Five, Building.BUNKER2), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Five, Building.ENTRANCE_TURRET1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Five, Building.ENTRANCE_TURRET2), TilePosition.None);

            postitionStorage.put(combine(Map.UNKNOWN, Location.Seven, Building.FIRST_SUPPLY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Seven, Building.BARRACK), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Seven, Building.SECOND_SUPPLY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Seven, Building.FACTORY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Seven, Building.BUNKER1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Seven, Building.BUNKER2), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Seven, Building.ENTRANCE_TURRET1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Seven, Building.ENTRANCE_TURRET2), TilePosition.None);

            postitionStorage.put(combine(Map.UNKNOWN, Location.Eleven, Building.FIRST_SUPPLY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Eleven, Building.BARRACK), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Eleven, Building.SECOND_SUPPLY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Eleven, Building.FACTORY), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Eleven, Building.BUNKER1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Eleven, Building.BUNKER2), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Eleven, Building.ENTRANCE_TURRET1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Eleven, Building.ENTRANCE_TURRET2), TilePosition.None);

            postitionStorage.put(combine(Map.UNKNOWN, Location.One, Building.STARPORT1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.One, Building.STARPORT2), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Five, Building.STARPORT1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Five, Building.STARPORT2), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Seven, Building.STARPORT1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Seven, Building.STARPORT2), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Eleven, Building.STARPORT1), TilePosition.None);
            postitionStorage.put(combine(Map.UNKNOWN, Location.Eleven, Building.STARPORT2), TilePosition.None);


        }

    }

    public final TilePosition getSupplyPosition(TilePosition tilepos) {

//		Location loc = Location.START;
//
//        if(tilepos.getX() < SMALL
//                && tilepos.getY() < SMALL){
//            loc = Location.Eleven;
//        }
//
//        if(BIG < tilepos.getX()
//                && tilepos.getY() < SMALL){
//            loc = Location.One;
//        }
//     
//        if(tilepos.getX() < SMALL
//                && tilepos.getY() > SMALL){
//            loc = Location.Seven;
//        }
//
//        if(BIG < tilepos.getX()
//                && tilepos.getY() > SMALL){
//            loc = Location.Five;
//        }
        Location loc = Location.START;


        if (tilepos.getX() < SMALL
                && tilepos.getY() < SMALL) {
            loc = Location.Eleven;
            xinc = true;
            yinc = true;
        }
        if (SMALL < tilepos.getX() && tilepos.getX() < BIG
                && tilepos.getY() < SMALL) {
            loc = Location.Twelve;
        }
        if (BIG < tilepos.getX()
                && tilepos.getY() < SMALL) {
            loc = Location.One;
            xinc = false;
            yinc = true;
        }
        if (tilepos.getX() < SMALL
                && SMALL < tilepos.getY() && tilepos.getY() < BIG) {
            loc = Location.Nine;
        }
        //center
        if (SMALL < tilepos.getX() && tilepos.getX() < BIG
                && SMALL < tilepos.getY() && tilepos.getY() < BIG) {
            loc = Location.Twelve;
        }
        if (BIG < tilepos.getX()
                && SMALL < tilepos.getY() && tilepos.getY() < BIG) {
            loc = Location.Three;
        }
        if (tilepos.getX() < SMALL
                && tilepos.getY() > BIG) {
            loc = Location.Seven;
            xinc = true;
            yinc = false;
        }
        if (SMALL < tilepos.getX() && tilepos.getX() < BIG
                && tilepos.getY() > SMALL) {
            loc = Location.Six;
        }
        if (BIG < tilepos.getX()
                && tilepos.getY() > BIG) {
            loc = Location.Five;
            xinc = false;
            yinc = false;
        }

        if (InformationManager.Instance().getMapSpecificInformation().getMap() == MapSpecificInformation.GameMap.FIGHTING_SPIRITS) {
            xinc = true;
            yinc = true;
        }

//        System.out.println(" next supply position ==>> " + loc.toString());
        TilePosition supply_pos = postitionStorage.get(combine(mapName, loc, Building.SUPPLY_AREA));
        return supply_pos;

    }

    public final TilePosition getSupplyPosition() {
		/*System.out.println("getSupplyPosition start");
		System.out.println("getSupplyPosition mapName :: " + mapName);
		System.out.println("getSupplyPosition loc_t :: " + loc_t);
		System.out.println("getSupplyPosition SUPPLY_AREA :: " + Building.SUPPLY_AREA);*/

        TilePosition supply_pos = postitionStorage.get(combine(mapName, loc_t, Building.SUPPLY_AREA));
        //System.out.println(" supply_pos end==>>> ( " + supply_pos.getX() + " , " + supply_pos.getY() + " ) ");
        return supply_pos;
    }

    public final TilePosition getTurretPosition(int a) {
		/*System.out.println("getSupplyPosition start");
		System.out.println("getSupplyPosition mapName :: " + mapName);
		System.out.println("getSupplyPosition loc_t :: " + loc_t);
		System.out.println("getSupplyPosition SUPPLY_AREA :: " + Building.SUPPLY_AREA);*/
        if (a == 1) {
            TilePosition turret_pos = postitionStorage.get(combine(mapName, loc_t, Building.ENTRANCE_TURRET1));
            if (turret_pos != TilePosition.None) {
//				System.out.println("turret 1 position not None :: " + turret_pos );
                return turret_pos;
            }
        } else if (a == 2) {
            TilePosition turret_pos = postitionStorage.get(combine(mapName, loc_t, Building.ENTRANCE_TURRET2));
            if (turret_pos != TilePosition.None) {
//				System.out.println("turret 2 position not None :: " + turret_pos );
                return turret_pos;
            }
        }

        return TilePosition.None;
    }

}
