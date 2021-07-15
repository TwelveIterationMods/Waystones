package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.waystones.api.IFOVOnUse;
import net.blay09.mods.waystones.api.IResetUseOnDamage;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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

public class BoundScrollItem extends Item implements IResetUseOnDamage, IFOVOnUse {

    public BoundScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return WaystonesConfig.getActive().scrollUseTime();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        if (Compat.isVivecraftInstalled) {
            return UseAnim.NONE;
        }

        return UseAnim.BOW;
    }

    private void setBoundTo(ItemStack itemStack, @Nullable IWaystone entry) {
        CompoundTag tagCompound = itemStack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundTag();
            itemStack.setTag(tagCompound);
        }

        if (entry != null) {
            tagCompound.put("WaystonesBoundTo", NbtUtils.createUUID(entry.getWaystoneUid()));
        } else {
            tagCompound.remove("WaystonesBoundTo");
        }
    }

    @Nullable
    protected IWaystone getBoundTo(Player player, ItemStack itemStack) {
        CompoundTag tagCompound = itemStack.getTag();
        if (tagCompound != null && tagCompound.contains("WaystonesBoundTo", Tag.TAG_INT_ARRAY)) {
            return new WaystoneProxy(player.getServer(), NbtUtils.loadUUID(Objects.requireNonNull(tagCompound.get("WaystonesBoundTo"))));
        }

        return null;
    }

    protected WarpMode getWarpMode() {
        return WarpMode.BOUND_SCROLL;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(context.getHand());
        Level world = context.getLevel();
        BlockEntity tileEntity = world.getBlockEntity(context.getClickedPos());
        if (tileEntity instanceof WaystoneBlockEntity) {
            IWaystone waystone = ((WaystoneBlockEntity) tileEntity).getWaystone();
            if (!PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
                PlayerWaystoneManager.activateWaystone(player, waystone);
            }

            if (!world.isClientSide) {
                ItemStack boundItem = heldItem.getCount() == 1 ? heldItem : heldItem.split(1);
                setBoundTo(boundItem, waystone);
                if (boundItem != heldItem) {
                    if (!player.addItem(boundItem)) {
                        player.drop(boundItem, false);
                    }
                }

                TranslatableComponent chatComponent = new TranslatableComponent("chat.waystones.scroll_bound", waystone.getName());
                chatComponent.withStyle(ChatFormatting.YELLOW);
                player.displayClientMessage(chatComponent, true);

                world.playSound(null, context.getClickedPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1f, 2f);
            }

            return !world.isClientSide ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof ServerPlayer) {
            Player player = (Player) entity;
            IWaystone boundTo = getBoundTo(player, stack);
            if (boundTo != null) {
                double distance = entity.distanceToSqr(boundTo.getPos().getX(), boundTo.getPos().getY(), boundTo.getPos().getZ());
                if (distance <= 3.0) {
                    return stack;
                }

                PlayerWaystoneManager.tryTeleportToWaystone(((ServerPlayer) player), boundTo, getWarpMode(), null);
            }
        }

        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        IWaystone boundTo = getBoundTo(player, itemStack);
        if (boundTo != null) {
            if (!player.isUsingItem() && world.isClientSide) {
                world.playSound(null, player, SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 1f, 2f);
            }

            if (Compat.isVivecraftInstalled) {
                finishUsingItem(itemStack, world, player);
            } else {
                player.startUsingItem(hand);
            }

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
        } else {
            TranslatableComponent chatComponent = new TranslatableComponent("chat.waystones.scroll_not_yet_bound");
            chatComponent.withStyle(ChatFormatting.RED);
            player.displayClientMessage(chatComponent, true);
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
        }

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        Player player = BalmClient.getClientPlayer();
        if (player == null) {
            return;
        }

        IWaystone boundTo = getBoundTo(player, stack);
        MutableComponent targetText = boundTo != null ? new TextComponent(boundTo.getName()) : new TranslatableComponent("tooltip.waystones.bound_to_none");
        if (boundTo != null) {
            targetText.withStyle(ChatFormatting.AQUA);
        }

        TranslatableComponent boundToText = new TranslatableComponent("tooltip.waystones.bound_to", targetText);
        boundToText.withStyle(ChatFormatting.GRAY);
        list.add(boundToText);
    }

}
