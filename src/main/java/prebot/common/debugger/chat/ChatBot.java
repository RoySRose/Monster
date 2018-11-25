package prebot.common.debugger.chat;

import prebot.common.debugger.chat.impl.GameSpeedAdjuster;
import prebot.common.debugger.chat.impl.StrategyChanger;
import prebot.common.debugger.chat.impl.StrikeLevelAdjuster;
import prebot.common.debugger.chat.impl.UxOptionChanger;

import java.util.ArrayList;
import java.util.List;

public class ChatBot {

    private static List<ChatExecuter> chatExecuters = new ArrayList<>();

    /*****************************************************************************
     * 설명
     *
     * 1) 게임스피드 조절			: s + 숫자	ex) s0=매우빠름, s24=bot경기스피드, s42=fastest
     * 2) UX 타입 변경			: d + 숫자	ex) d1=?, d0=prebot1 display
     * 3) 전략 변경				: $ + 전략명	ex) $TERRAN INIT
     * 4) 레이쓰 공격레벨 변경		: w + 숫자	ex) w1
     *
     *****************************************************************************/
    static {
        chatExecuters.add(new GameSpeedAdjuster('s'));
        chatExecuters.add(new UxOptionChanger('d'));
        chatExecuters.add(new StrategyChanger('$'));
        chatExecuters.add(new StrikeLevelAdjuster('w'));
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
