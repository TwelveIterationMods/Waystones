package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.*;
import net.blay09.mods.waystones.client.ClientWaystones;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.blay09.mods.waystones.util.WaystoneActivatedEvent;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.Random;

public class WaystoneBlock extends Block {

    public static final String name = "waystone";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty BASE = BooleanProperty.create("base");

    // TODO private static final Stat WAYSTONE_ACTIVATED = (new Stat("stat.waystones:waystonesActivated", new TranslationTextComponent("stat.waystones:waystonesActivated"))).registerStat();

    public WaystoneBlock() {
        super(Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(5f, 2000f));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(BASE);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new WaystoneTileEntity(!state.get(BASE));
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        if (WaystoneConfig.SERVER.creativeModeOnly.get() && !player.abilities.isCreativeMode) {
            return -1f;
        }

        WaystoneTileEntity tileWaystone = getTileWaystone(world, pos);
        if (tileWaystone != null && !player.abilities.isCreativeMode) {
            if (tileWaystone.wasGenerated() && WaystoneConfig.COMMON.disallowBreakingGenerated.get()) {
                return -1f;
            }

            if (tileWaystone.isGlobal() && !WaystoneConfig.SERVER.allowEveryoneGlobal.get()) {
                return -1f;
            }
        }

        return super.getPlayerRelativeBlockHardness(state, player, world, pos);
    }

    // TODO @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        if (blockBelow == this) {
            return false;
        }

        Block blockAbove = world.getBlockState(pos.up(2)).getBlock();
        return blockAbove != this;// TODO && super.canPlaceBlockAt(world, pos) && world.getBlockState(pos.up()).getBlock().isReplaceable(world, pos.up());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction facing = context.getPlacementHorizontalFacing();
        if (facing.getAxis() == Direction.Axis.Y) {
            facing = Direction.NORTH;
        }

        return getDefaultState().with(FACING, facing).with(BASE, true);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        world.setBlockState(pos.up(), this.getDefaultState().with(BASE, false));
        if (placer instanceof PlayerEntity && (!WaystoneConfig.SERVER.creativeModeOnly.get() || ((PlayerEntity) placer).abilities.isCreativeMode)) {
            WaystoneTileEntity tileWaystone = (WaystoneTileEntity) world.getTileEntity(pos);
            if (tileWaystone != null) {
                tileWaystone.setOwner((PlayerEntity) placer);
                tileWaystone.setWasGenerated(false);
                Waystones.proxy.openWaystoneSettings((PlayerEntity) placer, new WaystoneEntry(tileWaystone.getParent()), false);
            }
        }
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        WaystoneTileEntity tileWaystone = getTileWaystone(world, pos);
        if (tileWaystone != null) {
            WaystoneEntry entry = new WaystoneEntry(tileWaystone);
            if (tileWaystone.isGlobal()) {
                GlobalWaystones.get(world).removeGlobalWaystone(entry);
            }
            for (PlayerEntity player : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos).grow(64, 64, 64))) {
                WaystoneManager.removePlayerWaystone(player, entry);
                WaystoneManager.sendPlayerWaystones(player);
            }
        }

        super.onReplaced(state, world, pos, newState, isMoving);

        if (world.getBlockState(pos.up()).getBlock() == this) {
            world.removeBlock(pos.up(), false);
        } else if (world.getBlockState(pos.down()).getBlock() == this) {
            world.removeBlock(pos.down(), false);
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (player.isSneaking() && (player.abilities.isCreativeMode || !WaystoneConfig.SERVER.creativeModeOnly.get())) {
            WaystoneTileEntity tileWaystone = getTileWaystone(world, pos);
            if (tileWaystone == null) {
                return true;
            }

            if (!world.isRemote) {
                if (WaystoneConfig.SERVER.restrictRenameToOwner.get() && !tileWaystone.isOwner(player)) {
                    player.sendStatusMessage(new TranslationTextComponent("waystones:notTheOwner"), true);
                    return true;
                }

                if (tileWaystone.isGlobal() && !player.abilities.isCreativeMode && !WaystoneConfig.SERVER.allowEveryoneGlobal.get()) {
                    player.sendStatusMessage(new TranslationTextComponent("waystones:creativeRequired"), true);
                    return true;
                }
            }

            Waystones.proxy.openWaystoneSettings(player, new WaystoneEntry(tileWaystone.getParent()), false);
            return true;
        }

        WaystoneTileEntity tileWaystone = getTileWaystone(world, pos);
        if (tileWaystone == null) {
            return true;
        }

        WaystoneEntry knownWaystone = world.isRemote ? ClientWaystones.getKnownWaystone(tileWaystone.getWaystoneName()) : null;
        if (knownWaystone != null) {
            Waystones.proxy.openWaystoneSelection(player, WarpMode.WAYSTONE, Hand.MAIN_HAND, knownWaystone);
        } else {
            activateWaystone(player, world, tileWaystone);
        }

        return true;
    }

    public static void activateWaystone(PlayerEntity player, World world, WaystoneTileEntity tileWaystone) {
        BlockPos pos = tileWaystone.getPos();
        if (!world.isRemote) {
            WaystoneEntry waystone = new WaystoneEntry(tileWaystone);
            if (!WaystoneManager.checkAndUpdateWaystone(player, waystone)) {
                StringTextComponent nameComponent = new StringTextComponent(tileWaystone.getWaystoneName());
                nameComponent.getStyle().setColor(TextFormatting.WHITE);
                TranslationTextComponent chatComponent = new TranslationTextComponent("waystones:activatedWaystone", nameComponent);
                chatComponent.getStyle().setColor(TextFormatting.YELLOW);
                player.sendMessage(chatComponent);
                // TODO player.addStat(WAYSTONE_ACTIVATED);
                WaystoneManager.addPlayerWaystone(player, waystone);
                WaystoneManager.sendPlayerWaystones(player);
            }

            if (WaystoneConfig.SERVER.setSpawnPoint.get()) {
                BlockState state = world.getBlockState(pos);
                Direction blockFacing = state.get(FACING);
                player.setSpawnPoint(new BlockPos(tileWaystone.getPos().offset(blockFacing)), true);
            }

            // TODO world.updateObservingBlocksAt(pos, this);
        } else {
            MinecraftForge.EVENT_BUS.post(new WaystoneActivatedEvent(tileWaystone.getWaystoneName(), tileWaystone.getPos(), tileWaystone.getWorld().getDimension()));

            Waystones.proxy.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, pos, 1f);
            for (int i = 0; i < 32; i++) {
                world.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, pos.getY() + 3, pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, 0, -5, 0);
                world.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, pos.getY() + 4, pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, 0, -5, 0);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!WaystoneConfig.CLIENT.disableParticles.get() && random.nextFloat() < 0.75f) {
            WaystoneTileEntity tileWaystone = getTileWaystone(world, pos);
            if (tileWaystone == null) {
                return;
            }
            if (ClientWaystones.getKnownWaystone(tileWaystone.getWaystoneName()) != null) {
                world.addParticle(ParticleTypes.PORTAL, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.5, pos.getY() + 0.5, pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 1.5, 0, 0, 0);
                world.addParticle(ParticleTypes.ENCHANT, pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.5, pos.getY() + 0.5, pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 1.5, 0, 0, 0);
            }
        }
    }

    @Nullable
    private WaystoneTileEntity getTileWaystone(IBlockReader world, BlockPos pos) {
        WaystoneTileEntity tileWaystone = (WaystoneTileEntity) world.getTileEntity(pos);
        return tileWaystone != null ? tileWaystone.getParent() : null;
    }

    @Override
    public boolean hasCustomBreakingProgress(BlockState state) {
        return true;
    }
}
