package net.blay09.mods.waystones;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.client.ForgeWaystonesClient;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Waystones.MOD_ID)
public class ForgeWaystones {
    public ForgeWaystones() {
        Waystones.initialize();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeWaystonesClient::setupClient);

        Balm.initialize(Waystones.MOD_ID);
    }

}
