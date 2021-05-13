package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IAttunementItem;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.container.WarpPlateContainer;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class AttunedShardItem extends Item implements IAttunementItem {

    public static final String name = "attuned_shard";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    private static final Style GALACTIC_STYLE = Style.EMPTY.setFontId(new ResourceLocation("minecraft", "alt"));

    public AttunedShardItem() {
        super(new Properties().group(Waystones.itemGroup).maxStackSize(1));
    }

    @Override
    public boolean hasEffect(ItemStack itemStack) {
        IWaystone waystoneAttunedTo = getWaystoneAttunedTo(itemStack);
        return waystoneAttunedTo != null && waystoneAttunedTo.isValid();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        IWaystone attunedWarpPlate = getWaystoneAttunedTo(stack);
        if (attunedWarpPlate == null || !attunedWarpPlate.isValid()) {
            TranslationTextComponent textComponent = new TranslationTextComponent("tooltip.waystones.attuned_shard.attunement_lost");
            textComponent.mergeStyle(TextFormatting.GRAY);
            tooltip.add(textComponent);
            return;
        }

        String name = StringUtils.substringBeforeLast(attunedWarpPlate.getName(), " ");
        StringTextComponent galacticName = new StringTextComponent(name);
        galacticName.mergeStyle(getColorForName(name));
        galacticName.mergeStyle(GALACTIC_STYLE);
        tooltip.add(galacticName);

        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null && player.openContainer instanceof WarpPlateContainer) {
            IWaystone currentWarpPlate = ((WarpPlateContainer) player.openContainer).getWaystone();
            if (attunedWarpPlate.getWaystoneUid().equals(currentWarpPlate.getWaystoneUid())) {
                tooltip.add(new TranslationTextComponent("tooltip.waystones.attuned_shard.move_to_other_warp_plate"));
            } else {
                tooltip.add(new TranslationTextComponent("tooltip.waystones.attuned_shard.plug_into_warp_plate"));
            }
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.waystones.attuned_shard.plug_into_warp_plate"));
        }
    }

    public static TextFormatting getColorForName(String name) {
        int colorIndex = Math.abs(name.hashCode()) % 15;
        TextFormatting textFormatting = TextFormatting.fromColorIndex(colorIndex);
        if(textFormatting == TextFormatting.GRAY) {
            return TextFormatting.LIGHT_PURPLE;
        } else if(textFormatting == TextFormatting.DARK_GRAY) {
            return TextFormatting.DARK_PURPLE;
        } else if(textFormatting == TextFormatting.BLACK) {
            return TextFormatting.GOLD;
        }
        return textFormatting != null ? textFormatting : TextFormatting.GRAY;
    }

    @Nullable
    @Override
    public IWaystone getWaystoneAttunedTo(ItemStack itemStack) {
        CompoundNBT compound = itemStack.getTag();
        if (compound != null && compound.contains("AttunedToWaystone", Constants.NBT.TAG_INT_ARRAY)) {
            return new WaystoneProxy(NBTUtil.readUniqueId(Objects.requireNonNull(compound.get("AttunedToWaystone"))));
        }

        return null;
    }

    public static void setWaystoneAttunedTo(ItemStack itemStack, @Nullable IWaystone waystone) {
        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundNBT();
            itemStack.setTag(tagCompound);
        }

        if (waystone != null) {
            tagCompound.put("AttunedToWaystone", NBTUtil.func_240626_a_(waystone.getWaystoneUid()));
        } else {
            tagCompound.remove("AttunedToWaystone");
        }
    }
}
