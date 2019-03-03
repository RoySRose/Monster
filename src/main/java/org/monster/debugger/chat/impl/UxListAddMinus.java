package org.monster.debugger.chat.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.monster.board.StrategyBoard;
import org.monster.debugger.PreBotUXManager;
import org.monster.debugger.UxColor;
import org.monster.debugger.chat.ChatExecuter;

public class UxListAddMinus extends ChatExecuter {
	public UxListAddMinus(char type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(String option) {
		// TODO Auto-generated method stub
		if('a' == getType()){
			String pos	 = option.split(" ")[1];
			String key = option.split(" ")[2];
			String value = option.split(" ")[3];
			
			UxDrawConfig ux = UxDrawConfig.newInstanceFiledType(pos,key,UxDrawConfig.classMap.get(key.toUpperCase()),value,UxColor.CHAR_GREEN);
			PreBotUXManager.Instance().drawStrategyListOrigin[PreBotUXManager.Instance().getUxOption()].add(ux);
		}else if('m' == getType()){
			for (UxDrawConfig drawList : PreBotUXManager.Instance().drawStrategyListOrigin[PreBotUXManager.Instance().getUxOption()]) {
				if(drawList.getFieldName().equals(option.split(" ")[1])
						|| drawList.getValue().equals(option.split(" ")[1])){
					PreBotUXManager.Instance().drawStrategyListOrigin[PreBotUXManager.Instance().getUxOption()].remove(drawList);
				}
			}
		}
		
	}

}
