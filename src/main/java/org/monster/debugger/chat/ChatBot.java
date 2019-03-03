package org.monster.debugger.chat;

import bwapi.Game;

import org.monster.debugger.chat.impl.GameSpeedAdjuster;
import org.monster.debugger.chat.impl.StrategyChanger;
import org.monster.debugger.chat.impl.StrikeLevelAdjuster;
import org.monster.debugger.chat.impl.UxListAddMinus;
import org.monster.debugger.chat.impl.UxOptionChanger;

import java.util.ArrayList;
import java.util.List;

public class ChatBot {

    private static List<ChatExecuter> chatExecuters = new ArrayList<>();
    private static Game Broodwar;
    /*****************************************************************************
     * 설명
     *
     * 1) 게임스피드 조절			: s + 숫자			ex) s0=매우빠름, s24=bot경기스피드, s42=fastest
     * 2) UX 타입 변경				: d + 숫자			ex) d1=?, d0=prebot1 display
     * 3) 전략 변경				: $ + 전략명			ex) $TERRAN INIT
     * 4) 레이쓰 공격레벨 변경		: w + 숫자			ex) w1
     * 5) 디버깅 변수 추가 			: a + class + 변수 	ex) a L strategyBoard startStrategy
     * 5) 디버깅 변수 제거 			: m + 변수이름  		ex) m startStrategy
     * 6) 디버깅 초기화
     *
     *****************************************************************************/
    static {
        chatExecuters.add(new GameSpeedAdjuster('s', Broodwar));
        chatExecuters.add(new UxOptionChanger('d'));
        chatExecuters.add(new StrategyChanger('$'));
        chatExecuters.add(new StrikeLevelAdjuster('w'));
        chatExecuters.add(new UxListAddMinus('a'));
        chatExecuters.add(new UxListAddMinus('m'));
    }

    public static void addChatExecuter(ChatExecuter chatExecuter) {
        chatExecuters.add(chatExecuter);
    }

    /**
     * 채팅 명령을 인식하여 수행한다.<br/>
     *
     * @param command
     */
    public static void operateChatBot(String command) {
        if (command == null || command.length() < 2) {
            return;
        }

        char type = command.charAt(0);
        String option = command.substring(1);
        
        for (ChatExecuter executer : chatExecuters) {
            if (executer.isExecuteCharacter(type)) {
        		executer.execute(option);
                break;
            }
        }
    }

    /**
     * @param text
     * @return
     */
    public static void reply(String text) {
    }

}
