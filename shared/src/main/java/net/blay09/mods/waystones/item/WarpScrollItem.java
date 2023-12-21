package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.IAttunementItem;
import net.blay09.mods.waystones.api.IResetUseOnDamage;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.menu.ModMenus;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class WarpScrollItem extends ScrollItemBase implements IResetUseOnDamage, IAttunementItem {

    public WarpScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return WaystonesConfig.getActive().cooldowns.scrollUseTime;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof ServerPlayer player) {
            final var boundTo = getWaystoneAttunedTo(player.getServer(), player, itemStack);
            if (boundTo != null) {
                WaystoneTeleportManager.tryTeleportToWaystone(player, boundTo, WarpMode.WARP_SCROLL, null);
            } else {
                final var waystones = PlayerWaystoneManager.getTargetsForItem(player, itemStack);
                PlayerWaystoneManager.ensureSortingIndex(player, waystones);
                Balm.getNetworking().openGui(((ServerPlayer) entity), new BalmMenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("container.waystones.waystone_selection");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
                        return new WaystoneSelectionMenu(ModMenus.warpScrollSelection.get(), WarpMode.WARP_SCROLL, null, windowId, waystones);
                    }

                    @Override
                    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                        Waystone.writeList(buf, waystones);
                    }
                });
            }
        }
        return itemStack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!player.isUsingItem() && !world.isClientSide) {
            world.playSound(null, player, SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.1f, 2f);
        }
        if (getUseDuration(itemStack) <= 0 || Compat.isVivecraftInstalled) {
            finishUsingItem(itemStack, world, player);
        } else {
            player.startUsingItem(hand);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
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
}
