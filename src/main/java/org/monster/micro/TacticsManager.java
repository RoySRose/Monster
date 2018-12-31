package org.monster.micro;

/**
 * TacticsManager를 구현시
 * 1. Manager는 싱글턴으로 만들어야 한다.
 * 2. Manager를 필요로 하는 Control 또는 SquadGenerator는 TacticsManagerMapping.java에 등록되어 있어야 한다.
 * 
 * @author jw
 *
 */
public abstract class TacticsManager {
	
	protected abstract void update();
}
