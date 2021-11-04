package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.api.IAttunementItem;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.tileentity.WarpPlateTileEntity;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class WarpPlateBlock extends WaystoneBlockBase {

    private static final Style GALACTIC_STYLE = Style.EMPTY.setFontId(new ResourceLocation("minecraft", "alt"));

    private static final VoxelShape SHAPE = VoxelShapes.or(
            makeCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
            makeCuboidShape(3.0, 1.0, 3.0, 13.0, 2.0, 13.0)
    ).simplify();

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public WarpPlateBlock() {
        this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, false).with(ACTIVE, false));
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof WarpPlateTileEntity && !player.abilities.isCreativeMode) {
            boolean isSilkTouch = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.SILK_TOUCH, player) > 0;
            WarpPlateTileEntity warpPlate = (WarpPlateTileEntity) tileEntity;
            if (warpPlate.isCompletedFirstAttunement()) {
                ItemStackHandler itemStackHandler = warpPlate.getItemStackHandler();
                for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                    ItemStack itemStack = itemStackHandler.getStackInSlot(i);

                    // If not silk touching, don't bother dropping shards attuned to this waystone, since the waystone is gonna die anyways
                    if (!isSilkTouch && itemStack.getItem() == ModItems.attunedShard) {
                        IWaystone waystoneAttunedTo = ((IAttunementItem) ModItems.attunedShard).getWaystoneAttunedTo(itemStack);
                        if (waystoneAttunedTo != null && waystoneAttunedTo.getWaystoneUid().equals(warpPlate.getWaystone().getWaystoneUid())) {
                            continue;
                        }
                    }

                    spawnAsEntity(world, pos, itemStack);
                }
            }
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    protected boolean canSilkTouch() {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(ACTIVE);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity.getPosX() >= pos.getX() && entity.getPosX() < pos.getX() + 1
                && entity.getPosY() >= pos.getY() && entity.getPosY() < pos.getY() + 1
                && entity.getPosZ() >= pos.getZ() && entity.getPosZ() < pos.getZ() + 1
                && !world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof WarpPlateTileEntity) {
                ((WarpPlateTileEntity) tileEntity).onEntityCollision(entity);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        if (state.get(ACTIVE)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof WarpPlateTileEntity) {
                IWaystone targetWaystone = ((WarpPlateTileEntity) tileEntity).getTargetWaystone();
                if (targetWaystone != null && targetWaystone.isValid()) {
                    for (int i = 0; i < 50; i++) {
                        world.addParticle(ParticleTypes.CRIMSON_SPORE, pos.getX() + Math.random(), pos.getY() + Math.random() * 2, pos.getZ() + Math.random(), 0f, 0f, 0f);
                        world.addParticle(ParticleTypes.PORTAL, pos.getX() + Math.random(), pos.getY() + Math.random() * 2, pos.getZ() + Math.random(), 0f, 0f, 0f);
                    }
                } else {
                    for (int i = 0; i < 10; i++) {
                        world.addParticle(ParticleTypes.SMOKE, pos.getX() + Math.random(), pos.getY(), pos.getZ() + Math.random(), 0f, 0.01f, 0f);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new WarpPlateTileEntity();
    }

    @Override
    protected void handleActivation(World world, BlockPos pos, PlayerEntity player, WaystoneTileEntityBase tileEntity, IWaystone waystone) {
        if (!world.isRemote) {
            NetworkHooks.openGui(((ServerPlayerEntity) player), tileEntity.getWaystoneSelectionContainerProvider(), pos);
        }
    }

    @Override
    protected void addWaystoneNameToTooltip(List<ITextComponent> tooltip, WaystoneProxy waystone) {
        String name = StringUtils.substringBeforeLast(waystone.getName(), " ");
        StringTextComponent galacticName = new StringTextComponent(name);
        galacticName.mergeStyle(getColorForName(name));
        galacticName.mergeStyle(GALACTIC_STYLE);
        tooltip.add(galacticName);
    }

    public static TextFormatting getColorForName(String name) {
        int colorIndex = Math.abs(name.hashCode()) % 15;
        TextFormatting textFormatting = TextFormatting.fromColorIndex(colorIndex);
        if (textFormatting == TextFormatting.GRAY) {
            return TextFormatting.LIGHT_PURPLE;
        } else if (textFormatting == TextFormatting.DARK_GRAY) {
            return TextFormatting.DARK_PURPLE;
        } else if (textFormatting == TextFormatting.BLACK) {
            return TextFormatting.GOLD;
        }
        return textFormatting != null ? textFormatting : TextFormatting.GRAY;
    }

    public static ITextComponent getGalacticName(IWaystone waystone) {
        String name = StringUtils.substringBeforeLast(waystone.getName(), " ");
        StringTextComponent galacticName = new StringTextComponent(name);
        galacticName.mergeStyle(WarpPlateBlock.getColorForName(name));
        galacticName.mergeStyle(GALACTIC_STYLE);
        return galacticName;
    }
}
