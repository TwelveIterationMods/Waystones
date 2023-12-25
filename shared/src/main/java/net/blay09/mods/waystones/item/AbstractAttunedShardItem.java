package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.trait.IAttunementItem;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.core.InvalidWaystone;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.menu.WarpPlateMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractAttunedShardItem extends Item implements IAttunementItem {

    public AbstractAttunedShardItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        final var waystoneAttunedTo = getWaystoneAttunedTo(null, null, itemStack);
        return waystoneAttunedTo.map(Waystone::isValid).orElse(false);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        final var attunedWaystone = getWaystoneAttunedTo(null, null, stack).orElse(InvalidWaystone.INSTANCE);
        if (!attunedWaystone.isValid()) {
            var textComponent = Component.translatable("tooltip.waystones.attuned_shard.attunement_lost");
            textComponent.withStyle(ChatFormatting.GRAY);
            list.add(textComponent);
            return;
        }

        if (attunedWaystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE)) {
            list.add(WarpPlateBlock.getGalacticName(attunedWaystone));
        } else {
            list.add(attunedWaystone.getName().copy().withStyle(ChatFormatting.LIGHT_PURPLE));
        }

        Player player = Balm.getProxy().getClientPlayer();
        if (player != null && player.containerMenu instanceof WarpPlateMenu) {
            Waystone currentWarpPlate = ((WarpPlateMenu) player.containerMenu).getWaystone();
            if (attunedWaystone.getWaystoneUid().equals(currentWarpPlate.getWaystoneUid())) {
                list.add(Component.translatable("tooltip.waystones.attuned_shard.move_to_other_warp_plate"));
            } else {
                list.add(Component.translatable("tooltip.waystones.attuned_shard.plug_into_warp_plate"));
            }
        } else {
            list.add(Component.translatable("tooltip.waystones.attuned_shard.plug_into_warp_plate"));
        }
    }

    @Override
    public Optional<Waystone> getWaystoneAttunedTo(MinecraftServer server, Player player, ItemStack itemStack) {
        CompoundTag compound = itemStack.getTag();
        if (compound != null && compound.contains("AttunedToWaystone", Tag.TAG_INT_ARRAY)) {
            return Optional.of(new WaystoneProxy(server, NbtUtils.loadUUID(Objects.requireNonNull(compound.get("AttunedToWaystone")))));
        }

        return Optional.empty();
    }

    @Override
    public void setWaystoneAttunedTo(ItemStack itemStack, @Nullable Waystone waystone) {
        CompoundTag tagCompound = itemStack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundTag();
            itemStack.setTag(tagCompound);
        }

        if (waystone != null) {
            tagCompound.put("AttunedToWaystone", NbtUtils.createUUID(waystone.getWaystoneUid()));
        } else {
            tagCompound.remove("AttunedToWaystone");
        }
    }
}
