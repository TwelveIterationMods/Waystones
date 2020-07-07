package net.blay09.mods.waystones.config;

import net.blay09.mods.waystones.Waystones;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WaystoneConfig {

    public static final ForgeConfigSpec commonSpec;
    public static final WaystoneCommonConfig COMMON;

    static {
        final Pair<WaystoneCommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(WaystoneCommonConfig::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static final ForgeConfigSpec serverSpec;
    public static final WaystoneServerConfig SERVER;

    static {
        final Pair<WaystoneServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(WaystoneServerConfig::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static final ForgeConfigSpec clientSpec;
    public static final WaystoneClientConfig CLIENT;

    static {
        final Pair<WaystoneClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(WaystoneClientConfig::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static InventoryButtonMode getInventoryButtonMode() {
        return new InventoryButtonMode(SERVER.inventoryButton.get());
    }
}
