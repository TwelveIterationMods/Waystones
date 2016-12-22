package net.blay09.mods.waystones.worldgen;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.Random;
import java.util.Set;

public class NameGenerator {

	// Stolen from MrPork:
	private static final String[] random1 = new String[]{
			"Kr", "Ca", "Ra",
			"Rei", "Mar", "Luk", "Cro", "Cru", "Ray", "Bre", "Zed", "Mor", "Jag", "Mer", "Jar", "Mad", "Cry", "Zur",
			"Mjol", "Zork", "Creo", "Azak", "Azur", "Mrok", "Drak",
	};

	private static final String[] random2 = new String[]{
			"ir", "mi",
			"air", "sor", "mee", "clo", "red", "cra", "ark", "arc", "mur", "zer",
			"miri", "lori", "cres", "zoir", "urak",
			"marac",
			"slamar", "salmar",
	};

	private static final String[] random3 = new String[]{
			"d",
			"ed", "es", "er",
			"ark", "arc", "der", "med", "ure", "zur", "mur",
			"tron", "cred",
	};

	private static String randomName(Random rand) {
		return random1[rand.nextInt(random1.length)] + random2[rand.nextInt(random2.length)] + random3[rand.nextInt(random3.length)];
	}
	// ^^^^^^

	private static Map<String, String> SPECIAL_NAMES;
	private static Map<String, String> BIOME_NAMES;

	private static final Set<String> usedNames = Sets.newHashSet();

	public static void init() {
		SPECIAL_NAMES = Maps.newHashMap();
		BIOME_NAMES = Maps.newHashMap();
		addSpecialName(Biomes.BEACH, "Dusty Beach");
		addSpecialName(Biomes.FOREST, "Spectacular Forest");
		addSpecialName(Biomes.EXTREME_HILLS, "Smart Hills");
		addSpecialName(Biomes.DESERT, "Fiery Sands");
		addSpecialName(Biomes.OCEAN, "Dubzian Ocean");
		addSpecialName(Biomes.JUNGLE, "Senroht Jungles");
		addSpecialName(Biomes.ICE_MOUNTAINS, "Azra Mountain");
		addSpecialName(Biomes.MESA, "Black Mesa");
		addSpecialName(Biomes.TAIGA, "Aisaka Taiga");
		addSpecialName(Biomes.SWAMPLAND, "Darkon Swamps");
		addSpecialName(Biomes.REDWOOD_TAIGA, "Foxglove Woods");
		addSpecialName(Biomes.DESERT_HILLS, "Medabi Desert");
		addSpecialName(Biomes.STONE_BEACH, "Suton Beach");
		addSpecialName(Biomes.SAVANNA, "Mystic's Sogen");
		addSpecialName(Biomes.BIRCH_FOREST_HILLS, "Hizuru Bachi");
		addSpecialName(Biomes.ICE_PLAINS, "The Zero");
		addSpecialName(Biomes.ROOFED_FOREST, "Moriyane");
		addSpecialName(Biomes.MUTATED_EXTREME_HILLS_WITH_TREES, "The Movie");
		addSpecialName(Biomes.RIVER, "Kawa River");
		addSpecialName(Biomes.MUSHROOM_ISLAND, "Hemara Island");
		addSpecialName(Biomes.PLAINS, "Orilis Plains");
		addSpecialName(Biomes.REDWOOD_TAIGA_HILLS, "Caeril");
		addSpecialName(Biomes.DEFAULT, "Maldor");
		addSpecialName(Biomes.SKY, "The Nocturne");
		addSpecialName(Biomes.VOID, "The Nihilus");

		addBiomeName(Biomes.COLD_TAIGA, "Taiga");
		addBiomeName(Biomes.REDWOOD_TAIGA, "Taiga");
		addBiomeName(Biomes.TAIGA, "Taiga");

		addBiomeName(Biomes.PLAINS, "Plains");

		addBiomeName(Biomes.MUSHROOM_ISLAND, "Island");
		addBiomeName(Biomes.MUSHROOM_ISLAND_SHORE, "Island");

		addBiomeName(Biomes.RIVER, "River");
		addBiomeName(Biomes.FROZEN_RIVER, "River");

		addBiomeName(Biomes.BEACH, "Beach");
		addBiomeName(Biomes.COLD_BEACH, "Beach");
		addBiomeName(Biomes.STONE_BEACH, "Beach");

		addBiomeName(Biomes.BIRCH_FOREST, "Forest");
		addBiomeName(Biomes.BIRCH_FOREST_HILLS, "Forest");
		addBiomeName(Biomes.FOREST_HILLS, "Forest");
		addBiomeName(Biomes.FOREST, "Forest");
		addBiomeName(Biomes.ROOFED_FOREST, "Forest");
		addBiomeName(Biomes.MUTATED_FOREST, "Forest");
		addBiomeName(Biomes.MUTATED_BIRCH_FOREST, "Forest");
		addBiomeName(Biomes.MUTATED_BIRCH_FOREST_HILLS, "Forest");
		addBiomeName(Biomes.MUTATED_ROOFED_FOREST, "Forest");

		addBiomeName(Biomes.DEEP_OCEAN, "Ocean");
		addBiomeName(Biomes.OCEAN, "Ocean");
		addBiomeName(Biomes.FROZEN_OCEAN, "Ocean");

		addBiomeName(Biomes.DESERT, "Desert");
		addBiomeName(Biomes.DESERT_HILLS, "Desert");
		addBiomeName(Biomes.MUTATED_DESERT, "Desert");

		addBiomeName(Biomes.COLD_TAIGA_HILLS, "Hills");
		addBiomeName(Biomes.EXTREME_HILLS, "Hills");
		addBiomeName(Biomes.EXTREME_HILLS_EDGE, "Hills");
		addBiomeName(Biomes.EXTREME_HILLS_WITH_TREES, "Hills");
		addBiomeName(Biomes.MUTATED_EXTREME_HILLS, "Hills");
		addBiomeName(Biomes.MUTATED_EXTREME_HILLS_WITH_TREES, "Hills");
		addBiomeName(Biomes.REDWOOD_TAIGA_HILLS, "Hills");
		addBiomeName(Biomes.TAIGA_HILLS, "Hills");
		addBiomeName(Biomes.MUTATED_REDWOOD_TAIGA_HILLS, "Hills");

		addBiomeName(Biomes.SWAMPLAND, "Swamps");
		addBiomeName(Biomes.MUTATED_SWAMPLAND, "Swamps");

		addBiomeName(Biomes.SAVANNA, "Savanna");
		addBiomeName(Biomes.SAVANNA_PLATEAU, "Plateau");
		addBiomeName(Biomes.MUTATED_SAVANNA, "Savanna");
		addBiomeName(Biomes.MUTATED_SAVANNA_ROCK, "Savanna");

		addBiomeName(Biomes.ICE_PLAINS, "Icelands");
		addBiomeName(Biomes.ICE_MOUNTAINS, "Icelands");
		addBiomeName(Biomes.MUTATED_ICE_FLATS, "Icelands");

		addBiomeName(Biomes.JUNGLE, "Jungle");
		addBiomeName(Biomes.JUNGLE_EDGE, "Jungle");
		addBiomeName(Biomes.JUNGLE_HILLS, "Jungle");
		addBiomeName(Biomes.MUTATED_JUNGLE, "Jungle");
		addBiomeName(Biomes.MUTATED_JUNGLE_EDGE, "Jungle");

		addBiomeName(Biomes.MESA_ROCK, "Mesa");
		addBiomeName(Biomes.MESA, "Mesa");
		addBiomeName(Biomes.MESA_CLEAR_ROCK, "Mesa");
		addBiomeName(Biomes.MUTATED_MESA, "Mesa");
		addBiomeName(Biomes.MUTATED_MESA_CLEAR_ROCK, "Mesa");
		addBiomeName(Biomes.MUTATED_MESA_ROCK, "Mesa");

		addBiomeName(Biomes.VOID, "Void");
		addBiomeName(Biomes.SKY, "Skies");
	}

	private static void addBiomeName(Biome biome, String name) {
		BIOME_NAMES.put(biome.getBiomeName(), name);
	}

	private static void addSpecialName(Biome biome, String name) {
		SPECIAL_NAMES.put(biome.getBiomeName(), name);
	}

	public static String getName(Biome biome, Random rand) {
		if(SPECIAL_NAMES == null) {
			init();
		}
		String name = SPECIAL_NAMES.get(biome.getBiomeName());
		if (name == null || usedNames.contains(name)) {
			String biomeSuffix = BIOME_NAMES.get(biome.getBiomeName());
			name = randomName(rand) + (biomeSuffix != null ? " " + biomeSuffix : "");
			String tryName = name;
			int i = 1;
			while(usedNames.contains(tryName)) {
				tryName = name + " " + RomanNumber.toRoman(i);
				i++;
			}
			name = tryName;
		}
		usedNames.add(name);
		return name;
	}

}
