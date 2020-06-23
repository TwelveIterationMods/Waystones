package net.blay09.mods.waystones.config;

import net.blay09.mods.waystones.Waystones;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = Waystones.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WaystoneConfig {

    public static final String CONFIGS_NOTE = StringUtils.repeat( "IMPORTANT! READ THIS! >>> THERE IS AN ADDITIONAL CONFIG FILE IN THE WORLD FOLDER! <<< ADDITIONAL CONFIG FILE! IN THE WORLD FOLDER! GET IT? IN THE WORLD FOLDER! CREATE A WORLD AND YOU GET ACCESS TO MORE CONFIG OPTIONS. THIS IS A CHANGE IN FORGE. XP OPTIONS? YEAH THEY'RE IN THAT CONFIG FILE IN THE WORLD FOLDER. LET'S SAY IT TOGETHER: T-H-E-R-E - I-S - A-N - A-D-D-I-T-I-O-N-A-L - C-O-N-F-I-G - F-I-L-E - I-N - T-H-E - W-O-R-L-D - F-O-L-D-E-R!\n", 10);

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
