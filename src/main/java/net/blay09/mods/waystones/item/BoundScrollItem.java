package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneProxy;
import net.blay09.mods.waystones.tileentity.WaystoneTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BoundScrollItem extends Item implements IResetUseOnDamage {

    public static final String name = "bound_scroll";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    public BoundScrollItem() {
        super(new Item.Properties().group(Waystones.itemGroup));
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return WaystoneConfig.SERVER.warpScrollUseTime.get();
    }

    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        if (Waystones.proxy.isVivecraftInstalled()) {
            return UseAction.NONE;
        }

        return UseAction.BOW;
    }

    private void setBoundTo(ItemStack itemStack, @Nullable IWaystone entry) {
        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundNBT();
            itemStack.setTag(tagCompound);
        }

        if (entry != null) {
            tagCompound.put("WaystonesBoundTo", NBTUtil.writeUniqueId(entry.getWaystoneUid()));
        } else {
            tagCompound.remove("WaystonesBoundTo");
        }
    }

    @Nullable
    protected IWaystone getBoundTo(PlayerEntity player, ItemStack itemStack) {
        CompoundNBT tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            return new WaystoneProxy(NBTUtil.readUniqueId(tagCompound.getCompound("WaystonesBoundTo")));
        }

        return null;
    }

    protected WarpMode getWarpMode() {
        return WarpMode.BOUND_SCROLL;
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResultType.PASS;
        }

        ItemStack heldItem = player.getHeldItem(context.getHand());
        World world = context.getWorld();
        TileEntity tileEntity = world.getTileEntity(context.getPos());
        if (tileEntity instanceof WaystoneTileEntity) {
            IWaystone waystone = ((WaystoneTileEntity) tileEntity).getWaystone();
            if (!PlayerWaystoneManager.isWaystoneActivated(player, waystone)) {
                PlayerWaystoneManager.activateWaystone(player, waystone);
            }

            if (!world.isRemote) {
                ItemStack boundItem = heldItem.getCount() == 1 ? heldItem : heldItem.split(1);
                setBoundTo(boundItem, waystone);
                if (boundItem != heldItem) {
                    if (!player.addItemStackToInventory(boundItem)) {
                        player.dropItem(boundItem, false);
                    }
                }

                player.sendStatusMessage(new TranslationTextComponent("chat.waystones.scroll_bound", waystone.getName()), true);
            }

            Waystones.proxy.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, context.getPos(), 2f);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, LivingEntity entity) {
        if (!world.isRemote && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            IWaystone boundTo = getBoundTo(player, itemStack);
            if (boundTo != null) {
                double distance = entity.getDistanceSq(boundTo.getPos().getX(), boundTo.getPos().getY(), boundTo.getPos().getZ());
                if (distance <= 3.0) {
                    return itemStack;
                }

                if (PlayerWaystoneManager.tryTeleportToWaystone(player, boundTo, getWarpMode(), itemStack, null)) {
                    if (!player.abilities.isCreativeMode) {
                        itemStack.shrink(1);
                    }
                }
            }
        }

        return itemStack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        IWaystone boundTo = getBoundTo(player, itemStack);
        if (boundTo != null) {
            if (!player.isHandActive() && world.isRemote) {
                Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.posX, player.posY, player.posZ), 2f);
            }

            if (Waystones.proxy.isVivecraftInstalled()) {
                onItemUseFinish(itemStack, world, player);
            } else {
                player.setActiveHand(hand);
            }

            return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
        } else {
            player.sendStatusMessage(new TranslationTextComponent("waystones:scrollNotBound"), true);
            return new ActionResult<>(ActionResultType.FAIL, itemStack);
        }

    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        IWaystone boundTo = getBoundTo(player, itemStack);
        ITextComponent targetText = boundTo != null ? new StringTextComponent(boundTo.getName()) : new TranslationTextComponent("tooltip.waystones:none");
        if (boundTo != null) {
            targetText.getStyle().setColor(TextFormatting.AQUA);
        }

        TranslationTextComponent boundToText = new TranslationTextComponent("tooltip.waystones:boundTo", targetText);
        boundToText.getStyle().setColor(TextFormatting.GRAY);
        tooltip.add(boundToText);
    }

}
