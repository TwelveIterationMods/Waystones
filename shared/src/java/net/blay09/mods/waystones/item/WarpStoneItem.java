package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.item.BalmItem;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.IResetUseOnDamage;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarpStoneItem extends BalmItem implements IResetUseOnDamage {

    private static final BalmMenuProvider containerProvider = new BalmMenuProvider() {
        @Override
        public Component getDisplayName() {
            return new TranslatableComponent("container.waystones.waystone_selection");
        }

        @Override
        public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return WaystoneSelectionMenu.createWaystoneSelection(i, playerEntity, WarpMode.WARP_STONE, null);
        }

        @Override
        public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
            buf.writeByte(WarpMode.WARP_STONE.ordinal());
        }
    };

    public WarpStoneItem(Properties properties) {
        super(properties.durability(100));
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return WaystonesConfig.getActive().warpStoneUseTime();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        if (Compat.isVivecraftInstalled) {
            return UseAnim.NONE;
        }

        return UseAnim.BOW;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof ServerPlayer) {
            Balm.getNetworking().openGui(((ServerPlayer) entity), containerProvider);
        }

        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Reset cooldown when using in creative mode
        if (player.getAbilities().instabuild) {
            PlayerWaystoneManager.setWarpStoneCooldownUntil(player, 0);
        }

        if (PlayerWaystoneManager.canUseWarpStone(player, itemStack)) {
            if (!player.isUsingItem() && !world.isClientSide) {
                world.playSound(null, player, SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.1f, 2f);
            }
            if (Compat.isVivecraftInstalled) {
                finishUsingItem(itemStack, world, player);
            } else {
                player.startUsingItem(hand);
            }
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
        } else {
            TranslatableComponent chatComponent = new TranslatableComponent("chat.waystones.warpstone_not_charged");
            chatComponent.withStyle(ChatFormatting.RED);
            player.displayClientMessage(chatComponent, true);
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
        }
    }

    @Override
    public boolean balmShowDurabilityBar(ItemStack itemStack) {
        return balmGetDurabilityForDisplay(itemStack) > 0;
    }

    @Override
    public double balmGetDurabilityForDisplay(ItemStack stack) {
        Player player = BalmClient.getClientPlayer();
        if (player == null) {
            return 0.0;
        }

        long timeLeft = PlayerWaystoneManager.getWarpStoneCooldownLeft(player);
        int maxCooldown = WaystonesConfig.getActive().warpStoneCooldown() * 20;
        if (maxCooldown == 0) {
            return 0f;
        }

        return Mth.clamp(timeLeft / (float) maxCooldown, 0f, 1f);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return PlayerWaystoneManager.canUseWarpStone(BalmClient.getClientPlayer(), itemStack) || super.isFoil(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        Player player = BalmClient.getClientPlayer();
        if (player == null) {
            return;
        }

        long timeLeft = PlayerWaystoneManager.getWarpStoneCooldownLeft(player);
        int secondsLeft = (int) (timeLeft / 20);
        if (secondsLeft > 0) {
            TranslatableComponent secondsLeftText = new TranslatableComponent("tooltip.waystones.cooldown_left", secondsLeft);
            secondsLeftText.withStyle(ChatFormatting.GOLD);
            list.add(secondsLeftText);
        }
    }

}
