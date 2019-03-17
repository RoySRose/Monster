package org.monster.debugger;

public class ScreenUx {

	private static ScreenUx instance = new ScreenUx();
	private static int uxOption;

	public static void showDisplay() {
		uxOption = PreBotUXManager.Instance().uxOption;
		PreBotUXManager.Instance().clearList();
		if (uxOption == 0) {
			PreBotUXManager.Instance().drawDebugginUxMenu(); // drawing ux 메뉴 설명
			PreBotUXManager.Instance().drawUxInfo(); // debuging 메뉴 설명 (화면 , 속도
														// 등)
		} else if (uxOption == 1) {
			UXManager.Instance().update(); // 프리봇 전 기본 디버깅 메뉴
			// 미네랄PATH
		} else if (uxOption == 2) {
			PreBotUXManager.Instance().drawGameInformationOnScreen();
			PreBotUXManager.Instance().drawBWTAResultOnMap();
			PreBotUXManager.Instance().drawBuildOrderQueueOnScreen();
			PreBotUXManager.Instance().drawBuildStatusOnScreen();
			PreBotUXManager.Instance().drawReservedBuildingTilesOnMap();
			PreBotUXManager.Instance().drawTilesToAvoidOnMap();
			PreBotUXManager.Instance().drawWorkerMiningStatusOnMap();
			PreBotUXManager.Instance().drawUnitTargetOnMap();
			// drawTurretMap();
			PreBotUXManager.Instance().drawManagerTimeSpent();
			PreBotUXManager.Instance().drawConstructionQueueOnScreenAndMap(); // ConstructionQueue

			// draw tile position of mouse cursor
			int mouseX = PreBotUXManager.Instance().Broodwar.getMousePosition().getX()
					+ PreBotUXManager.Instance().Broodwar.getScreenPosition().getX();
			int mouseY = PreBotUXManager.Instance().Broodwar.getMousePosition().getY()
					+ PreBotUXManager.Instance().Broodwar.getScreenPosition().getY();
			PreBotUXManager.Instance().Broodwar.drawTextMap(mouseX + 20, mouseY,
					"(" + (int) (mouseX / 32) + ", " + (int) (mouseY / 32) + ")");
			PreBotUXManager.Instance().Broodwar.drawTextMap(mouseX + 20, mouseY + 10,
					"(" + (int) (mouseX) + ", " + (int) (mouseY) + ")");

		} else if (uxOption == 3) {
			PreBotUXManager.Instance().drawStrategy();
		} else if (uxOption == 4) {
			PreBotUXManager.Instance().drawEnemyBuildTimer();
		} /*
			 * else if (uxOption == 5) { clearList(); drawSquadInfoOnMap();
			 * drawManagerTimeSpent(); drawDecision();
			 * drawEnemyAirDefenseRange(); drawAirForceInformation(); } else if
			 * (uxOption == 6) { clearList(); drawEnemyBaseToBaseTime(); } else
			 * if (uxOption == 7) { clearList(); drawBigWatch();
			 * drawManagerTimeSpent(); } else if (uxOption == 8) { clearList();
			 * drawTilesToAvoidOnMap(); drawReservedBuildingTilesOnMap(); } else
			 * if (uxOption == 9) { //drawExpectedResource();
			 * //drawExpectedResource2();
			 * 
			 * }
			 */

		PreBotUXManager.Instance().drawMineralIdOnMap();
		PreBotUXManager.Instance().drawUnitIdOnMap();
		PreBotUXManager.Instance().drawPositionInformation();
		PreBotUXManager.Instance().drawTimer();
		PreBotUXManager.Instance().drawPathData();
		PreBotUXManager.Instance().drawSquadUnitTagMap();

		/*
		 * try { PreBotUXManager.Instance().addDrawStrategyListOrigin(); } catch
		 * (IllegalArgumentException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IllegalAccessException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (ClassNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		PreBotUXManager.Instance().drawStrategyList();
		PreBotUXManager.Instance().clearDecisionListForUx();
	}
}
