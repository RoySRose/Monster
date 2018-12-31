package org.monster.micro.temporaryutil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bwapi.UnitType;

public class TemporaryUtils {

	public static List<UnitType> getAllZergUnitTypes() {
		return Stream.of(UnitType.Zerg_Zergling, UnitType.Zerg_Hydralisk, UnitType.Zerg_Overlord,
				UnitType.Zerg_Lurker, UnitType.Zerg_Mutalisk, UnitType.Zerg_Scourge,
				UnitType.Zerg_Queen, UnitType.Zerg_Guardian, UnitType.Zerg_Devourer,
				UnitType.Zerg_Ultralisk, UnitType.Zerg_Defiler, UnitType.Zerg_Broodling, UnitType.Zerg_Infested_Terran)
				.collect(Collectors.toList());
		// UnitType.Zerg_Drone, UnitType.Zerg_Larva, UnitType.Zerg_Egg, UnitType.Zerg_Lurker_Egg
	}

	public static List<UnitType> getAllZergBuildingTypes() {
		return Stream.of(UnitType.Zerg_Hatchery, UnitType.Zerg_Lair, UnitType.Zerg_Hive,
				UnitType.Zerg_Evolution_Chamber, UnitType.Zerg_Creep_Colony, UnitType.Zerg_Spore_Colony, UnitType.Zerg_Sunken_Colony,
				UnitType.Zerg_Extractor, UnitType.Zerg_Spawning_Pool, UnitType.Zerg_Hydralisk_Den,
				UnitType.Zerg_Spire, UnitType.Zerg_Greater_Spire, UnitType.Zerg_Queens_Nest, UnitType.Zerg_Infested_Command_Center,
				UnitType.Zerg_Ultralisk_Cavern, UnitType.Zerg_Defiler_Mound, UnitType.Zerg_Nydus_Canal).collect(Collectors.toList());
	}
}
