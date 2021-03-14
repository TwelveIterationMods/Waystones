package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class WarpScrollItem extends Item implements IResetUseOnDamage, IFOVOnUse {

    public static final String name = "warp_scroll";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    private static final INamedContainerProvider containerProvider = new INamedContainerProvider() {
        @Override
        public ITextComponent getDisplayName() {
            return new TranslationTextComponent("container.waystones.waystone_selection");
        }

        @Override
        public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            return new WaystoneSelectionContainer(i, WarpMode.WARP_SCROLL, null);
        }
    };

    public WarpScrollItem() {
        super(new Item.Properties().group(Waystones.itemGroup));
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return WaystonesConfig.SERVER.scrollUseTime.get();
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
        if (!world.isRemote && entityLiving instanceof ServerPlayerEntity) {
            NetworkHooks.openGui(((ServerPlayerEntity) entityLiving), containerProvider, it -> {
                it.writeByte(WarpMode.WARP_SCROLL.ordinal());
            });
        }
        return itemStack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (!player.isHandActive() && world.isRemote) {
            Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ()), 2f);
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
