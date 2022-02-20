package net.blay09.mods.waystones;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.waystones.client.WaystonesClient;
import net.blay09.mods.waystones.compat.Compat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(Waystones.MOD_ID)
public class ForgeWaystones {
    public ForgeWaystones() {
        Balm.initialize(Waystones.MOD_ID, Waystones::initialize);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initialize(Waystones.MOD_ID, WaystonesClient::initialize));

        Balm.initializeIfLoaded(Compat.THEONEPROBE, "net.blay09.mods.waystones.compat.TheOneProbeAddon");
    }
}
