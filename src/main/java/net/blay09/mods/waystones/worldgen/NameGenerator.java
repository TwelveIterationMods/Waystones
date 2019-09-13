package net.blay09.mods.waystones.worldgen;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.Map;
import java.util.Random;
import java.util.Set;

public class NameGenerator extends WorldSavedData {

    private static final String DATA_NAME = Waystones.MOD_ID + "_NameGenerator";
    private static final String TAG_LIST_NAME = "UsedNames";

    public NameGenerator() {
        super(DATA_NAME);
    }

    public NameGenerator(String name) {
        super(name);
    }

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

    private String randomName(Random rand) {
        return random1[rand.nextInt(random1.length)] + random2[rand.nextInt(random2.length)] + random3[rand.nextInt(random3.length)];
    }
    // ^^^^^^

    private Map<String, String> BIOME_NAMES;

    private final Set<String> usedNames = Sets.newHashSet();

    public void init() {
        BIOME_NAMES = Maps.newHashMap();

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

    private void addBiomeName(Biome biome, String name) {
        BIOME_NAMES.put(biome.biomeName, name);
    }

    public String getName(Biome biome, Random rand) {
        if (BIOME_NAMES == null) {
            init();
        }

        String name = null;
        for (String customName : WaystoneConfig.COMMON.customNames.get()) {
            if (!usedNames.contains(customName)) {
                name = customName;
                break;
            }
        }

        if (name == null) {
            String biomeSuffix = BIOME_NAMES.get(biome.biomeName);
            name = randomName(rand) + (biomeSuffix != null ? " " + biomeSuffix : "");
            String tryName = name;
            int i = 1;
            while (usedNames.contains(tryName)) {
                tryName = name + " " + RomanNumber.toRoman(i);
                i++;
            }

            name = tryName;
        }

        usedNames.add(name);
        markDirty();
        return name;
    }

    @Override
    public void read(CompoundNBT compound) {
        ListNBT tagList = compound.getList(TAG_LIST_NAME, Constants.NBT.TAG_STRING);
        for (int i = 0; i < tagList.size(); i++) {
            usedNames.add(tagList.get(i).getString());
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT tagList = new ListNBT();
        for (String entry : usedNames) {
            tagList.add(new StringNBT(entry));
        }
        compound.put(TAG_LIST_NAME, tagList);
        return compound;
    }

    public static NameGenerator get(World world) {
        MapStorage storage = world.getMapStorage();
        if (storage != null) {
            NameGenerator instance = (NameGenerator) storage.getOrLoadData(NameGenerator.class, DATA_NAME);
            if (instance == null) {
                instance = new NameGenerator();
                storage.setData(DATA_NAME, instance);
            }
            return instance;
        }

        return new NameGenerator();
    }

}
