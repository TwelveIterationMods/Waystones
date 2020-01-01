package net.blay09.mods.waystones.worldgen;

import com.google.common.collect.Sets;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

import java.util.Random;
import java.util.Set;

public class NameGenerator extends WorldSavedData {

    private static final String DATA_NAME = Waystones.MOD_ID + "_NameGenerator";
    private static final String TAG_LIST_NAME = "UsedNames";

    private static final NameGenerator clientStorageCopy = new NameGenerator();

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

    private final Set<String> usedNames = Sets.newHashSet();

    public synchronized String getName(IWaystone waystone, Random rand) {
        String name = null;
        for (String customName : WaystoneConfig.COMMON.customNames.get()) {
            if (!usedNames.contains(customName)) {
                name = customName;
                break;
            }
        }

        if (name == null) {
            name = randomName(rand);
            String tryName = name;
            int i = 1;
            while (usedNames.contains(tryName)) {
                tryName = name + " " + RomanNumber.toRoman(i);
                i++;
            }

            name = tryName;
        }

        GenerateWaystoneNameEvent event = new GenerateWaystoneNameEvent(waystone, name);
        MinecraftForge.EVENT_BUS.post(event);
        name = event.getName();

        usedNames.add(name);
        markDirty();
        return name;
    }

    @Override
    public void read(CompoundNBT compound) {
        ListNBT tagList = compound.getList(TAG_LIST_NAME, Constants.NBT.TAG_STRING);
        for (INBT tag : tagList) {
            usedNames.add(tag.getString());
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
        MinecraftServer server = world.getServer();
        if (server != null) {
            ServerWorld overworld = server.getWorld(DimensionType.OVERWORLD);
            DimensionSavedDataManager storage = overworld.getSavedData();
            return storage.getOrCreate(NameGenerator::new, DATA_NAME);
        }

        return clientStorageCopy;
    }

}
