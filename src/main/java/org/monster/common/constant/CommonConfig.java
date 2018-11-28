package org.monster.common.constant;

public class CommonConfig {

    public static class Config { // MyBotModule에서 사용하는 수정불가능한 기본 Config

        /// 로컬에서 게임을 실행할 때 게임스피드 (코드 제출 후 서버에서 게임을 실행할 때는 서버 설정을 사용함)<br>
        /// Speedups for automated play, sets the number of milliseconds bwapi spends in each frame<br>
        /// Fastest: 42 ms/frame. 1초에 24 frame. 일반적으로 1초에 24frame을 기준 게임속도로 합니다<br>
        /// Normal: 67 ms/frame. 1초에 15 frame<br>
        /// As fast as possible : 0 ms/frame. CPU가 할수있는 가장 빠른 속도.
        public static int SetLocalSpeed = 12;

        /// 로컬에서 게임을 실행할 때 FrameSkip (코드 제출 후 서버에서 게임을 실행할 때는 서버 설정을 사용함)<br>
        /// frameskip을 늘리면 화면 표시도 업데이트 안하므로 훨씬 빠릅니다
        public static int SetFrameSkip = 0;

        /// 로컬에서 게임을 실행할 때 사용자 키보드/마우스 입력 허용 여부 (코드 제출 후 서버에서 게임을 실행할 때는 서버 설정을 사용함)
        public static boolean EnableUserInput = true;

        /// 로컬에서 게임을 실행할 때 전체 지도를 다 보이게 할 것인지 여부 (코드 제출 후 서버에서 게임을 실행할 때는 서버 설정을 사용함)
        public static boolean EnableCompleteMapInformation = false;
    }

    /// 화면 표시 여부 - 게임 정보
    public static class UxConfig {

        /// StarCraft 및 BWAPI 에서 1 Tile = 32 * 32 Point (Pixel) 입니다<br>
        /// Position 은 Point (Pixel) 단위이고, TilePosition 은 Tile 단위입니다
        public static final int TILE_SIZE = 32;

//		public static boolean drawGameInfo = true;
//
//		/// 화면 표시 여부 - 미네랄, 가스
//		public static boolean drawResourceInfo = false;
//		/// 화면 표시 여부 - 지도
//		public static boolean drawBWTAInfo = true;
//		/// 화면 표시 여부 - 바둑판
//		public static boolean drawMapGrid = false;
//
//		/// 화면 표시 여부 - 유닛 HitPoint
//		public static boolean drawUnitHealthBars = true;
//		/// 화면 표시 여부 - 유닛 통계
//		public static boolean drawEnemyUnitInfo = true;
//		/// 화면 표시 여부 - 유닛 ~ Target 간 직선
//		public static boolean drawUnitTargetInfo = true;
//
//		/// 화면 표시 여부 - 빌드 큐
//		public static boolean drawProductionInfo = true;
//
//		/// 화면 표시 여부 - 건물 Construction 상황
//		public static boolean drawBuildingInfo = false;
//		/// 화면 표시 여부 - 건물 ConstructionPlace 예약 상황
//		public static boolean drawReservedBuildingTiles = true;
//
//		/// 화면 표시 여부 - 정찰 상태
//		public static boolean drawScoutInfo = true;
//		/// 화면 표시 여부 - 일꾼 목록
//		public static boolean drawWorkerInfo = false;
//
//		/// 화면 표시 여부 - 마우스 커서
//		public static boolean drawMouseCursorInfo = true;
    }

    public static class FileConfig {

        /// 봇 이름
        public static final String BOT_NAME = "PreBot2";
        /// 봇 개발자 이름
        public static final String BOT_AUTHORS = "1";

        /// 로그 파일 이름
        public static final String LOG_FILE_NAME = BOT_NAME + "_LastGameLog.dat";
        /// 읽기 파일 경로
        public static final String READ_DIRECTORY = "bwapi-data\\read\\";
        /// 쓰기 파일 경로
        public static final String WRITE_DIRECTORY = "bwapi-data\\write\\";
    }
}
