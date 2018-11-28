package org.monster.micro;

import bwapi.DamageType;
import bwapi.Unit;
import bwapi.UnitSizeType;
import bwapi.UnitType;
import bwapi.WeaponType;

import java.util.Arrays;
import java.util.List;

/**
 * BWAPI Java 오류로 사용하지 못하는 기능 구현
 *
 */
public class MirrorBugFixed {

    private static final List<UnitType> EXPLOSIVE_UNIT_TYPES;
    private static final List<UnitType> EXPLOSIVE_TO_FLYERS_UNITTYPES;
    private static final List<UnitType> CONCUSSIVE_UNIT_TYPES;

    private static final List<UnitType> LARGE_UNIT_TYPES;
    private static final List<UnitType> MEDIUM_UNIT_TYPES;
    private static final List<UnitType> SMALL_UNIT_TYPES;

    static {
        EXPLOSIVE_UNIT_TYPES = Arrays.asList(
                UnitType.Terran_Vulture_Spider_Mine,
                UnitType.Terran_Siege_Tank_Tank_Mode,
                UnitType.Terran_Siege_Tank_Siege_Mode,
                UnitType.Terran_Valkyrie,
                UnitType.Terran_Missile_Turret,
                UnitType.Zerg_Hydralisk,
                UnitType.Zerg_Devourer,
                UnitType.Zerg_Infested_Terran,
                UnitType.Zerg_Sunken_Colony,
                UnitType.Protoss_Dragoon,
                UnitType.Protoss_Arbiter,
                UnitType.Protoss_Corsair);

        EXPLOSIVE_TO_FLYERS_UNITTYPES = Arrays.asList(
                UnitType.Terran_Wraith,
                UnitType.Terran_Goliath,
                UnitType.Protoss_Scout);

        CONCUSSIVE_UNIT_TYPES = Arrays.asList(
                UnitType.Terran_Vulture,
                UnitType.Terran_Firebat,
                UnitType.Terran_Firebat);

        LARGE_UNIT_TYPES = Arrays.asList(
                UnitType.Terran_Siege_Tank_Tank_Mode,
                UnitType.Terran_Siege_Tank_Siege_Mode,
                UnitType.Terran_Goliath,
                UnitType.Terran_Wraith,
                UnitType.Terran_Dropship,
                UnitType.Terran_Science_Vessel,
                UnitType.Terran_Battlecruiser,
                UnitType.Terran_Valkyrie,
                UnitType.Protoss_Dragoon,
                UnitType.Protoss_Archon,
                UnitType.Protoss_Reaver,
                UnitType.Protoss_Shuttle,
                UnitType.Protoss_Scout,
                UnitType.Protoss_Carrier,
                UnitType.Protoss_Arbiter,
                UnitType.Zerg_Overlord,
                UnitType.Zerg_Guardian,
                UnitType.Zerg_Devourer);

        MEDIUM_UNIT_TYPES = Arrays.asList(
                UnitType.Terran_Vulture,
                UnitType.Protoss_Corsair,
                UnitType.Zerg_Hydralisk,
                UnitType.Zerg_Defiler,
                UnitType.Zerg_Queen,
                UnitType.Zerg_Lurker);

        SMALL_UNIT_TYPES = Arrays.asList(
                UnitType.Terran_Marine,
                UnitType.Terran_Firebat,
                UnitType.Terran_Ghost,
                UnitType.Terran_Medic,
                UnitType.Terran_SCV,
                UnitType.Protoss_Zealot,
                UnitType.Protoss_High_Templar,
                UnitType.Protoss_Dark_Templar,
                UnitType.Protoss_Observer,
                UnitType.Protoss_Probe,
                UnitType.Zerg_Larva,
                UnitType.Zerg_Zergling,
                UnitType.Zerg_Infested_Terran,
                UnitType.Zerg_Broodling,
                UnitType.Zerg_Scourge,
                UnitType.Zerg_Mutalisk,
                UnitType.Zerg_Drone);
    }

    /// WeaponType.damageType() 오류보완 : attack의 공격타입을 리턴
    /// https://m.blog.naver.com/PostView.nhn?blogId=erudite061&logNo=70132593191&proxyReferer=https%3A%2F%2Fwww.google.co.kr%2F
    public static DamageType getDamageType(UnitType attackerType, Unit target) {
        WeaponType weapon = target.isFlying() ? attackerType.airWeapon() : attackerType.groundWeapon();
        if (weapon == null || weapon == WeaponType.Unknown) {
            return DamageType.None;
        }
        if (EXPLOSIVE_UNIT_TYPES.contains(attackerType)) {
            return DamageType.Explosive;
        }
        if (EXPLOSIVE_TO_FLYERS_UNITTYPES.contains(attackerType)) {
            return target.isFlying() ? DamageType.Explosive : DamageType.Normal;
        }
        if (CONCUSSIVE_UNIT_TYPES.contains(attackerType)) {
            return DamageType.Concussive;
        }
        return DamageType.Normal;

    }

    /// UnitType.size() 오류보완 : unitType의 사이즈를 리턴(대형, 중형, 소형)
    public static UnitSizeType getUnitSize(UnitType unitType) {
        if (unitType.isBuilding() || LARGE_UNIT_TYPES.contains(unitType)) {
            return UnitSizeType.Large;
        }
        if (MEDIUM_UNIT_TYPES.contains(unitType)) {
            return UnitSizeType.Medium;
        }
        if (unitType.isWorker() || SMALL_UNIT_TYPES.contains(unitType)) {
            return UnitSizeType.Small;
        }
        return UnitSizeType.Small;
    }
}
