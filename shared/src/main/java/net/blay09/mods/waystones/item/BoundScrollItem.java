package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.block.entity.WarpPlateBlockEntity;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.core.WaystoneTeleportManager;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BoundScrollItem extends ScrollItemBase implements IResetUseOnDamage, IFOVOnUse, IAttunementItem {

    public BoundScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return WaystonesConfig.getActive().cooldowns.scrollUseTime;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof ServerPlayer) {
            Player player = (Player) entity;
            IWaystone boundTo = getWaystoneAttunedTo(player.getServer(), player, stack);
            if (boundTo != null) {
                WaystoneTeleportManager.tryTeleportToWaystone(player, boundTo, WarpMode.WARP_SCROLL, null);
            }
        }

        return stack;
    }

    @Nullable
    @Override
    public IWaystone getWaystoneAttunedTo(MinecraftServer server, Player player, ItemStack itemStack) {
        CompoundTag compound = itemStack.getTag();
        if (compound != null && compound.contains("AttunedToWaystone", Tag.TAG_INT_ARRAY)) {
            return new WaystoneProxy(server, NbtUtils.loadUUID(Objects.requireNonNull(compound.get("AttunedToWaystone"))));
        }

        return null;
    }

    @Override
    public void setWaystoneAttunedTo(ItemStack itemStack, @Nullable IWaystone waystone) {
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

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        final var player = Balm.getProxy().getClientPlayer();
        if (player == null) {
            return;
        }

        final var boundTo = getWaystoneAttunedTo(player.getServer(), player, itemStack);
        if (boundTo != null) {
            final var boundToValueComponent = Component.literal(boundTo.getName()).withStyle(ChatFormatting.AQUA);
            list.add(Component.translatable("tooltip.waystones.bound_to", boundToValueComponent).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }
}
