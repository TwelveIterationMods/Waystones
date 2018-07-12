package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWarpStone extends Item implements IResetUseOnDamage {

    public static final String name = "warp_stone";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    public static long lastTimerUpdate;

    public ItemWarpStone() {
        setRegistryName(name);
        setUnlocalizedName(registryName.toString());
        setCreativeTab(Waystones.creativeTab);
        setMaxStackSize(1);
        setMaxDamage(100);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return WaystoneConfig.general.warpStoneUseTime;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        if (Waystones.proxy.isVivecraftInstalled()) {
            return EnumAction.NONE;
        }

        return EnumAction.BOW;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entityLiving) {
        if (world.isRemote) {
            Waystones.proxy.openWaystoneSelection(WarpMode.WARP_STONE, entityLiving.getActiveHand(), null);
        }
        return itemStack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (player.capabilities.isCreativeMode) {
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
            return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
        } else {
            player.sendStatusMessage(new TextComponentTranslation("waystones:stoneNotCharged"), true);
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean showDurabilityBar(ItemStack itemStack) {
        return getDurabilityForDisplay(itemStack) > 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getDurabilityForDisplay(ItemStack stack) {
        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        if (player == null) {
            return 0.0;
        }

        long timeSince = System.currentTimeMillis() - PlayerWaystoneHelper.getLastWarpStoneUse(player);
        float percentage = (float) timeSince / (float) (WaystoneConfig.general.warpStoneCooldown * 1000);
        return 1.0 - (double) (Math.max(0, Math.min(1, percentage)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack itemStack) {
        return PlayerWaystoneHelper.canUseWarpStone(FMLClientHandler.instance().getClientPlayerEntity());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }
        long timeLeft = PlayerWaystoneHelper.getLastWarpStoneUse(player);
        long timeSince = System.currentTimeMillis() - lastTimerUpdate;
        int secondsLeft = (int) ((timeLeft - timeSince) / 1000);
        if (secondsLeft > 0) {
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:cooldownLeft", secondsLeft));
        }
    }

}
