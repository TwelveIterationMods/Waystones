package net.blay09.mods.waystones.compat;

import mcjty.theoneprobe.api.*;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntityBase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.InterModComms;

import javax.annotation.Nullable;
import java.util.function.Function;

public class TheOneProbeAddon {

    public static void register() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopInitializer::new);
    }

    public static class TopInitializer implements Function<ITheOneProbe, Void> {
        @Nullable
        @Override
        public Void apply(@Nullable ITheOneProbe top) {
            if (top != null) {
                top.registerProvider(new ProbeInfoProvider());
            }
            return null;
        }
    }

    public static class ProbeInfoProvider implements IProbeInfoProvider {

        @Override
        public String getID() {
            return Waystones.MOD_ID;
        }

        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo info, PlayerEntity playerEntity, World world, BlockState state, IProbeHitData data) {
            TileEntity tileEntity = world.getTileEntity(data.getPos());
            if (tileEntity instanceof WaystoneTileEntityBase) {
                String name = ((WaystoneTileEntityBase) tileEntity).getWaystone().getName();
                info.text(new StringTextComponent(name));
            }
        }

    }

}
