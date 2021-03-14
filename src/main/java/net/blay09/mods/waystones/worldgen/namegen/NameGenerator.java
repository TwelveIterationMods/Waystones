package net.blay09.mods.waystones.worldgen.namegen;

import com.google.common.collect.Sets;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class NameGenerator extends WorldSavedData {

    private static final String DATA_NAME = Waystones.MOD_ID + "_NameGenerator";
    private static final String USED_NAMES = "UsedNames";
    private static final NameGenerator clientStorageCopy = new NameGenerator();

    private final MrPorkNameGenerator generator = new MrPorkNameGenerator();
    private final Set<String> usedNames = Sets.newHashSet();

    private NameGenerator() {
        super(DATA_NAME);
    }

    public synchronized String getName(IWaystone waystone, Random rand) {
        String name = null;
        List<? extends String> customNames = WaystonesConfig.COMMON.customWaystoneNames.get();
        Collections.shuffle(customNames, rand);
        for (String customName : customNames) {
            if (!usedNames.contains(customName)) {
                name = customName;
                break;
            }
        }

        if (name == null) {
            name = generator.randomName(rand);
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
        ListNBT tagList = compound.getList(USED_NAMES, Constants.NBT.TAG_STRING);
        for (INBT tag : tagList) {
            usedNames.add(tag.getString());
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT tagList = new ListNBT();
        for (String entry : usedNames) {
            tagList.add(StringNBT.valueOf(entry));
        }
        compound.put(USED_NAMES, tagList);
        return compound;
    }

    public static NameGenerator get() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            ServerWorld overworld = server.getWorld(World.OVERWORLD);
            DimensionSavedDataManager storage = overworld.getSavedData();
            return storage.getOrCreate(NameGenerator::new, DATA_NAME);
        }

        return clientStorageCopy;
    }

}
