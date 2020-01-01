package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class WarpStoneItem extends Item implements IResetUseOnDamage {

    public static final String name = "warp_stone";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    public static long lastTimerUpdate;

    public WarpStoneItem() {
        super(new Item.Properties().group(Waystones.itemGroup).maxStackSize(1).maxDamage(100));
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return WaystoneConfig.SERVER.warpStoneUseTime.get();
    }

    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        if (Waystones.proxy.isVivecraftInstalled()) {
            return UseAction.NONE;
        }

        return UseAction.BOW;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, LivingEntity entityLiving) {
        if (world.isRemote && entityLiving instanceof PlayerEntity) {
            Waystones.proxy.openWaystoneSelection((PlayerEntity) entityLiving, WarpMode.WARP_STONE, entityLiving.getActiveHand(), null);
        }

        return itemStack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (player.abilities.isCreativeMode) {
            PlayerWaystoneHelper.setLastWarpStoneUse(player, 0);
        }
        if (PlayerWaystoneHelper.canUseWarpStone(player)) {
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
            player.sendStatusMessage(new TranslationTextComponent("waystones:stoneNotCharged"), true);
            return new ActionResult<>(ActionResultType.FAIL, itemStack);
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack itemStack) {
        return getDurabilityForDisplay(itemStack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return 0.0;
        }

        long timeLeft = PlayerWaystoneHelper.getLastWarpStoneUse(player);
        long timeSince = (System.currentTimeMillis() - lastTimerUpdate);
        float percentage = (float) timeSince / timeLeft;
        return 1.0 - (double) (Math.max(0, Math.min(1, percentage)));
    }

    @Override
    public boolean hasEffect(ItemStack itemStack) {
        return PlayerWaystoneHelper.canUseWarpStone(Minecraft.getInstance().player);
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        long timeLeft = PlayerWaystoneHelper.getLastWarpStoneUse(player);
        long timeSince = System.currentTimeMillis() - lastTimerUpdate;
        int secondsLeft = (int) ((timeLeft - timeSince) / 1000);
        if (secondsLeft > 0) {
            TranslationTextComponent secondsLeftText = new TranslationTextComponent("tooltip.waystones:cooldownLeft", secondsLeft);
            secondsLeftText.getStyle().setColor(TextFormatting.GRAY);
            tooltip.add(secondsLeftText);
        }
    }

}
