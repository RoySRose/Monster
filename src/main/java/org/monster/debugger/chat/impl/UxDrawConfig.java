package org.monster.debugger.chat.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	private Method method;
	public Method getMethod() {
		return method;
	}


	private Field field;
	//private Field field;
	
	public Field getField() {
		return field;
	}


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

	public UxDrawConfig(String pos, String key, Class<?> clazz, Method method, char color) {
		this.pos = posMap.get(pos.toUpperCase());
		this.key = key;
		this.clazz = clazz;
		this.method = method;
		this.color = color;
		// TODO Auto-generated constructor stub
	}
	
	public UxDrawConfig(String pos, String key,  Class<?> clazz, Field field, char color) {
		this.pos = posMap.get(pos.toUpperCase());
		this.key = key;
		this.clazz = clazz;
		this.field = field;
		this.color = color;
		// TODO Auto-generated constructor stub
	}

	public UxDrawConfig(String pos, String key, Object value, char color) {
		this.pos = posMap.get(pos.toUpperCase());
		this.key = key;
		this.value = value;
		this.color = color;
		// TODO Auto-generated constructor stub
	}
	
	public UxDrawConfig(String pos, String key, int value, char color) {
		this.pos = posMap.get(pos.toUpperCase());
		this.key = key;
		this.value = value;
		this.color = color;
		// TODO Auto-generated constructor stub
	}

	public static UxDrawConfig newInstanceFiledType(String pos, String key, Class<?> clazz, String field, char color) {
		Class<?> c = clazz.getClass();
		Field fld = null;
		try{
			fld = c.getDeclaredField(field);
			if (!fld.isAccessible()) {
				fld.setAccessible(true);
			}
		} catch (Exception e) {
//			System.out.println(e.getMessage());
		}
		System.out.println(fld.getName().toString());
		return new UxDrawConfig(pos, key, c, fld, color);
	}
	
	public static UxDrawConfig newInstanceMethodType(String pos, String key, Class<?> clazz, String method, char color) {
		Method invokeMethod = null;
		try {
			invokeMethod = clazz.getDeclaredMethod(method);
			if (!invokeMethod.isAccessible()) {
				invokeMethod.setAccessible(true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new UxDrawConfig(pos, key, clazz, invokeMethod, color);
	}

	/*public static UxDrawConfig newInstanceStringType(String pos, String key, String value, char color) {
		
		return new UxDrawConfig(pos, key, value, color);
	}*/
	
	public static UxDrawConfig newInstanceStringType(String pos, String key, Object value, char color) {
		
		return new UxDrawConfig(pos, key, value, color);
	}
	
/*	public static UxDrawConfig newInstanceObjectType(String key, Object value, char color) {
		return new UxDrawConfig(key, value, color);
	}
	
	public static UxDrawConfig newInstanceObjectType(String pos,String key, Object value, char color) {
		return new UxDrawConfig(pos, key, value, color);
	}*/


	public String getClassFieldName() {
		if (clazz != null) {
			if(method != null){
				try {
					return this.key + " : " + this.method.invoke(clazz).toString();
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(field != null){
				return this.key + " : " +this.field.getName().toString();
			}
		}else if(this.key.equals("")){
			return this.value.toString();
		}else{
			return this.key + " : " + this.value.toString();
		}
		return key;
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


};