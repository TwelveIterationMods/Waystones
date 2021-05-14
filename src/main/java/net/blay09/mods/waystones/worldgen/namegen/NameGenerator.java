package net.blay09.mods.waystones.worldgen.namegen;

import com.google.common.collect.Sets;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.api.IWaystone;
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

import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class NameGenerator extends WorldSavedData {

    private static final String DATA_NAME = Waystones.MOD_ID + "_NameGenerator";
    private static final String USED_NAMES = "UsedNames";
    private static final NameGenerator clientStorageCopy = new NameGenerator();

    private final Set<String> usedNames = Sets.newHashSet();

    private NameGenerator() {
        super(DATA_NAME);
    }

    private INameGenerator getNameGenerator(NameGenerationMode nameGenerationMode) {
        switch (nameGenerationMode) {
            case MIXED:
                return new MixedNameGenerator(new MrPorkNameGenerator(), new CustomNameGenerator(false, usedNames));
            case RANDOM_ONLY:
                return new MrPorkNameGenerator();
            case PRESET_ONLY:
                return new CustomNameGenerator(true, usedNames);
            case PRESET_FIRST:
            default:
                return new SequencedNameGenerator(new CustomNameGenerator(false, usedNames), new MrPorkNameGenerator());
        }
    }

    public synchronized String getName(IWaystone waystone, Random rand, NameGenerationMode nameGenerationMode) {
        INameGenerator nameGenerator = getNameGenerator(nameGenerationMode);
        String originalName = nameGenerator.randomName(rand);
        if (originalName == null) {
            // This should never happen, but just in case generate a fallback if something did go wrong
            originalName = Objects.requireNonNull(new MrPorkNameGenerator().randomName(rand));
        }
        String name = resolveDuplicate(originalName);

        GenerateWaystoneNameEvent event = new GenerateWaystoneNameEvent(waystone, name);
        MinecraftForge.EVENT_BUS.post(event);
        name = event.getName();

        usedNames.add(name);
        markDirty();
        return name;
    }

    private String resolveDuplicate(String name) {
        String tryName = name;
        int i = 1;
        while (usedNames.contains(tryName)) {
            tryName = name + " " + RomanNumber.toRoman(i);
            i++;
        }
        return tryName;
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
