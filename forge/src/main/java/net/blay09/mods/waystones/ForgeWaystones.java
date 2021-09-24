package net.blay09.mods.waystones;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.client.WaystonesClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(Waystones.MOD_ID)
public class ForgeWaystones {
    public ForgeWaystones() {
        Waystones.initialize();

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> WaystonesClient::initialize);

        Balm.initialize(Waystones.MOD_ID);
    }

}
