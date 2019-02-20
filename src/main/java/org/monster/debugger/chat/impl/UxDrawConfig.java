package org.monster.debugger.chat.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.monster.board.StrategyBoard;
import org.monster.debugger.UxColor;
import org.monster.worker.WorkerManager;

public class UxDrawConfig {

	static Map<String, Class<?>> classMap = new HashMap<>();
	static {
		classMap.put("STRATEGYBOARD", StrategyBoard.class);
		classMap.put("WORKERMANAGER", WorkerManager.class);
	}
	
	public static Map<String, Integer> posMap = new HashMap<>();
	static {
		posMap.put("L", 20);
		posMap.put("M", 250);
		posMap.put("R", 500);

	}

	private char color;
	private Class<?> clazz;
	private Field field;
	
	private int pos = posMap.get("L");
	
	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getPos() {
		return pos;
	}

	private String key;
	private Object value;

	
	public char getColor() {
		return color;
	}

	public void setColor(char color) {
		this.color = color;
	}

	private UxDrawConfig(Class<?> clazz, Field field, char color) {
		this.clazz = clazz;
		this.field = field;
		this.color = color;

	}

	private UxDrawConfig(String key, Object value, char color) {
		this.key = key;
		this.value = value;
		this.color = color;

	}

	public UxDrawConfig(String pos, String key, Object value, char color) {
		this.pos = posMap.get(pos.toUpperCase());
		this.key = key;
		this.value = value;
		this.color = color;
		// TODO Auto-generated constructor stub
	}

	public static UxDrawConfig newInstanceClassType(Class<?> clazz, Field field, char color) {
		return new UxDrawConfig(clazz, field, color);
	}
	
	public static UxDrawConfig newInstanceClassType(String pos,String key, Object value, char color) {
		return new UxDrawConfig(pos, key, value, color);
	}

	public static UxDrawConfig newInstanceObjectType(String pos, String key, Object value) {
		return newInstanceObjectType(pos, key, value, UxColor.CHAR_YELLOW);
	}
	
	public static UxDrawConfig newInstanceObjectType(String key, Object value, char color) {
		return new UxDrawConfig(key, value, color);
	}
	
	public static UxDrawConfig newInstanceObjectType(String pos,String key, Object value, char color) {
		return new UxDrawConfig(pos, key, value, color);
	}

	public String getClassFieldName() {
		if (clazz != null) {
			return this.clazz.getSimpleName() + "." + this.field.getName();
		} else if(this.key.equals("")){
			return this.value.toString();
		}else{
			return this.key + " : " + this.value.toString();
		}
	}

	public String getFieldName() {
		if (this.field == null) {
			return "";
		} else {
			return field.toString();
		}
	}
	
	public String getValue() {
		if (this.value == null) {
			return "";
		} else {
			return value.toString();
		}
	}


	public Class<?> getClazz() {
		return this.clazz;
	}
	
	public String getKey() {
		return this.key;
	}

	public void setClazz(String className) {
		this.clazz = classMap.get(className.toUpperCase());
	}

	/// 일꾼 유닛에게 지정하는 임무의 종류

};