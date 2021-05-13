package net.blay09.mods.waystones.block;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.tileentity.SharestoneTileEntity;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class SharestoneBlock extends WaystoneBlockBase {

    private static final VoxelShape LOWER_SHAPE = VoxelShapes.or(
            makeCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            makeCuboidShape(1.0, 3.0, 1.0, 15.0, 7.0, 15.0),
            makeCuboidShape(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
            makeCuboidShape(3.0, 9.0, 3.0, 13.0, 16.0, 13.0)
    ).simplify();

    private static final VoxelShape UPPER_SHAPE = VoxelShapes.or(
            makeCuboidShape(3.0, 0.0, 3.0, 13.0, 7.0, 13.0),
            makeCuboidShape(2.0, 7.0, 2.0, 14.0, 9.0, 14.0),
            makeCuboidShape(1.0, 9.0, 1.0, 15.0, 13.0, 15.0),
            makeCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0)
    ).simplify();

    @Nullable
    private final DyeColor color;

    public SharestoneBlock(@Nullable DyeColor color) {
        this.color = color;
        this.setDefaultState(this.stateContainer.getBaseState().with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SharestoneTileEntity();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return state.get(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPE : LOWER_SHAPE;
    }

    @Override
    protected void handleActivation(World world, BlockPos pos, PlayerEntity player, WaystoneTileEntityBase tileEntity, IWaystone waystone) {
        if (!world.isRemote) {
            NetworkHooks.openGui(((ServerPlayerEntity) player), tileEntity.getWaystoneSelectionContainerProvider(), it -> WaystoneSelectionContainer.writeSharestoneContainer(it, pos));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        TranslationTextComponent component = new TranslationTextComponent(color != null ? "tooltip.waystones." + color.getTranslationKey() + "_sharestone" : "tooltip.waystones.sharestone");
        component.mergeStyle(TextFormatting.GRAY);
        tooltip.add(component);

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HALF);
    }

    @Nullable
    public DyeColor getColor() {
        return color;
    }

}
