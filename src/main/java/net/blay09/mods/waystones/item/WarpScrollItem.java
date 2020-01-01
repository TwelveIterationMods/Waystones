package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WarpScrollItem extends Item implements IResetUseOnDamage {

    public static final String name = "warp_scroll";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    public WarpScrollItem() {
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


    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, LivingEntity entityLiving) {
        Waystones.proxy.openWaystoneSelection((PlayerEntity) entityLiving, WarpMode.WARP_SCROLL, entityLiving.getActiveHand(), null);
        return itemStack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (!player.isHandActive() && world.isRemote) {
            Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.posX, player.posY, player.posZ), 2f);
        }
        if (Waystones.proxy.isVivecraftInstalled()) {
            onItemUseFinish(itemStack, world, player);
        } else {
            player.setActiveHand(hand);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
    }

    @Override
    public boolean hasEffect(ItemStack itemStack) {
        return true;
    }


}
