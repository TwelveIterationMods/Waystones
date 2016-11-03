package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWarpStone extends Item {

	public ItemWarpStone() {
		setRegistryName(Waystones.MOD_ID, "warpStone");
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(CreativeTabs.TOOLS);
		setMaxStackSize(1);
		setMaxDamage(100);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemStack) {
		return EnumAction.BOW;
	}

	@Nullable
	@Override
	public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entityLiving) {
		if(world.isRemote) {
			Waystones.proxy.openWaystoneSelection(false);
		}
		return itemStack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if(player.capabilities.isCreativeMode) {
			PlayerWaystoneData.setLastWarpStoneUse(player, 0);
		}
		if (PlayerWaystoneData.canUseWarpStone(player)) {
			if(PlayerWaystoneData.getLastWaystone(player) != null || !WaystoneManager.getServerWaystones().isEmpty()) {
				if(!player.isHandActive() && world.isRemote) {
					Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.posX, player.posY, player.posZ), 2f);
				}
				player.setActiveHand(hand);
				return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
			} else {
				TextComponentTranslation chatComponent = new TextComponentTranslation("waystones:scrollNotBound");
				chatComponent.getStyle().setColor(TextFormatting.RED);
				Waystones.proxy.printChatMessage(3, chatComponent);
				return new ActionResult<>(EnumActionResult.FAIL, itemStack);
			}
		} else {
			TextComponentTranslation chatComponent = new TextComponentTranslation("waystones:stoneNotCharged");
			chatComponent.getStyle().setColor(TextFormatting.RED);
			Waystones.proxy.printChatMessage(3, chatComponent);
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
		long timeSince = System.currentTimeMillis() - PlayerWaystoneData.getLastWarpStoneUse(FMLClientHandler.instance().getClientPlayerEntity());
		float percentage = (float) timeSince / (float) (Waystones.getConfig().warpStoneCooldown * 1000);
		return 1.0 - (double) (Math.max(0, Math.min(1, percentage)));
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean debug) {
		long timeSince = System.currentTimeMillis() - PlayerWaystoneData.getLastWarpStoneUse(FMLClientHandler.instance().getClientPlayerEntity());
		int secondsLeft = (int) ((Waystones.getConfig().warpStoneCooldown * 1000 - timeSince) / 1000);
		if(secondsLeft > 0) {
			list.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:cooldownLeft", secondsLeft));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack itemStack) {
		return PlayerWaystoneData.canUseWarpStone(FMLClientHandler.instance().getClientPlayerEntity());
	}

}
