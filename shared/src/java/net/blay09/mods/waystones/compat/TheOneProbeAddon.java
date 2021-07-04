//package net.blay09.mods.waystones.compat;
//
//import mcjty.theoneprobe.api.*;
//import net.blay09.mods.waystones.Waystones;
//import net.blay09.mods.waystones.api.IWaystone;
//import net.blay09.mods.waystones.block.entity.WaystoneBlockEntityBase;
//import net.blay09.mods.waystones.core.PlayerWaystoneManager;
//import net.blay09.mods.waystones.core.WaystoneTypes;
//import net.minecraft.network.chat.TextComponent;
//import net.minecraft.network.chat.TranslatableComponent;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//
//public class TheOneProbeAddon {
//
//    public static void register() {
//        InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopInitializer::new);
//    }
//
//    public static class TopInitializer implements Function<ITheOneProbe, Void> {
//        @Nullable
//        @Override
//        public Void apply(@Nullable ITheOneProbe top) {
//            if (top != null) {
//                top.registerProvider(new ProbeInfoProvider());
//            }
//            return null;
//        }
//    }
//
//    public static class ProbeInfoProvider implements IProbeInfoProvider {
//
//        @Override
//        public String getID() {
//            return WaystonesMod.ID;
//        }
//
//        @Override
//        public void addProbeInfo(ProbeMode mode, IProbeInfo info, PlayerEntity playerEntity, Level world, BlockState state, IProbeHitData data) {
//            BlockEntity tileEntity = world.getBlockEntity(data.getPos());
//            //noinspection StatementWithEmptyBody
//            if (tileEntity instanceof WarpPlateBlockEntity) {
//                /* TOP does not use the correct galactic font, so don't display for warp plates.
//                IWaystone waystone = ((WarpPlateTileEntity) tileEntity).getWaystone();
//                ITextComponent galacticName = WarpPlateBlock.getGalacticName(waystone);
//                info.text(galacticName); */
//            } else if (tileEntity instanceof WaystoneBlockEntityBase) {
//                IWaystone waystone = ((WaystoneBlockEntityBase) tileEntity).getWaystone();
//                boolean isActivated = !waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE) || PlayerWaystoneManager.isWaystoneActivated(playerEntity, waystone);
//                if (isActivated && waystone.hasName() && waystone.isValid()) {
//                    info.text(new TextComponent(waystone.getName()));
//                } else {
//                    info.text(new TranslatableComponent("tooltip.waystones.undiscovered"));
//                }
//            }
//        }
//
//    }
//
//}
