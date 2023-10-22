package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IAttunementItem;
import net.blay09.mods.waystones.api.IFOVOnUse;
import net.blay09.mods.waystones.api.IResetUseOnDamage;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.menu.WarpPlateContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CrumblingAttunedShardItem extends Item implements IAttunementItem, IFOVOnUse, IResetUseOnDamage {

    public CrumblingAttunedShardItem(Properties properties) {
        super(properties.stacksTo(4));
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        IWaystone waystoneAttunedTo = getWaystoneAttunedTo(null, itemStack);
        return waystoneAttunedTo != null && waystoneAttunedTo.isValid();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        IWaystone attunedWarpPlate = getWaystoneAttunedTo(null, stack);
        if (attunedWarpPlate == null || !attunedWarpPlate.isValid()) {
            var textComponent = Component.translatable("tooltip.waystones.attuned_shard.attunement_lost");
            textComponent.withStyle(ChatFormatting.GRAY);
            list.add(textComponent);
            return;
        }

        list.add(WarpPlateBlock.getGalacticName(attunedWarpPlate));

        Player player = Balm.getProxy().getClientPlayer();
        if (player != null && player.containerMenu instanceof WarpPlateContainer) {
            IWaystone currentWarpPlate = ((WarpPlateContainer) player.containerMenu).getWaystone();
            if (attunedWarpPlate.getWaystoneUid().equals(currentWarpPlate.getWaystoneUid())) {
                list.add(Component.translatable("tooltip.waystones.attuned_shard.move_to_other_warp_plate"));
            } else {
                list.add(Component.translatable("tooltip.waystones.attuned_shard.plug_into_warp_plate_or_use"));
            }
        } else {
            list.add(Component.translatable("tooltip.waystones.attuned_shard.plug_into_warp_plate_or_use"));
        }

        list.add(Component.translatable("tooltip.waystones.attuned_shard.attunement_crumbling"));
    }

    @Nullable
    @Override
    public IWaystone getWaystoneAttunedTo(MinecraftServer server, ItemStack itemStack) {
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
    public int getUseDuration(ItemStack itemStack) {
        return WaystonesConfig.getActive().cooldowns.scrollUseTime;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof ServerPlayer) {
            Player player = (Player) entity;
            IWaystone boundTo = getWaystoneAttunedTo(player.getServer(), stack);
            if (boundTo != null) {
                double distance = entity.distanceToSqr(boundTo.getPos().getX(), boundTo.getPos().getY(), boundTo.getPos().getZ());
                if (distance <= 3.0) {
                    return stack;
                }

                PlayerWaystoneManager.tryTeleportToWaystone(player, boundTo, WarpMode.WARP_PLATE_CONSUMES, null);
            }
        }

        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        IWaystone boundTo = getWaystoneAttunedTo(world.getServer(), itemStack);
        if (boundTo != null) {
            if (!player.isUsingItem() && world.isClientSide) {
                world.playSound(null, player, SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.1f, 2f);
            }

            if (getUseDuration(itemStack) <= 0 || Compat.isVivecraftInstalled) {
                finishUsingItem(itemStack, world, player);
            } else {
                player.startUsingItem(hand);
            }

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
        } else {
            var chatComponent = Component.translatable("tooltip.waystones.attuned_shard.attunement_lost");
            chatComponent.withStyle(ChatFormatting.RED);
            player.displayClientMessage(chatComponent, true);
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
        }

    }
}
