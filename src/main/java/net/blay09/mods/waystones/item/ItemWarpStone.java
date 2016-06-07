package net.blay09.mods.waystones.item;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemWarpStone extends Item {

	public ItemWarpStone() {
		setUnlocalizedName(Waystones.MOD_ID + ":warpStone");
		setTextureName(Waystones.MOD_ID + ":warpStone");
		setCreativeTab(CreativeTabs.tabTools);
		setMaxStackSize(1);
		setMaxDamage(100);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemStack) {
		return EnumAction.bow;
	}

	@Override
	public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player) {
		if(world.isRemote) {
			Waystones.proxy.openWaystoneSelection(false);
		}
		return itemStack;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if(player.capabilities.isCreativeMode) {
			PlayerWaystoneData.setLastWarpStoneUse(player, 0);
		}
		if (PlayerWaystoneData.canUseWarpStone(player)) {
			if(PlayerWaystoneData.getLastWaystone(player) != null || !WaystoneManager.getServerWaystones().isEmpty()) {
				if(!player.isUsingItem() && world.isRemote) {
					Waystones.proxy.playSound("portal.trigger", 2f);
				}
				player.setItemInUse(itemStack, getMaxItemUseDuration(itemStack));
			} else {
				ChatComponentTranslation chatComponent = new ChatComponentTranslation("waystones:scrollNotBound");
				chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
				Waystones.proxy.printChatMessage(3, chatComponent);
			}
		} else {
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("waystones:stoneNotCharged");
			chatComponent.getChatStyle().setColor(EnumChatFormatting.RED);
			Waystones.proxy.printChatMessage(3, chatComponent);
		}
		return itemStack;
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean showDurabilityBar(ItemStack itemStack) {
		return getDisplayDamage(itemStack) > 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getDisplayDamage(ItemStack itemStack) {
		long timeSince = System.currentTimeMillis() - PlayerWaystoneData.getLastWarpStoneUse(FMLClientHandler.instance().getClientPlayerEntity());
		float percentage = (float) timeSince / (float) (Waystones.getConfig().warpStoneCooldown * 1000);
		return 100 - (int) (Math.max(0, Math.min(1, percentage)) * 100);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean debug) {
		long timeSince = System.currentTimeMillis() - PlayerWaystoneData.getLastWarpStoneUse(FMLClientHandler.instance().getClientPlayerEntity());
		int secondsLeft = (int) ((Waystones.getConfig().warpStoneCooldown * 1000 - timeSince) / 1000);
		if(secondsLeft > 0) {
			list.add(EnumChatFormatting.GRAY + I18n.format("tooltip.waystones:cooldownLeft", secondsLeft));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack itemStack, int pass) {
		return PlayerWaystoneData.canUseWarpStone(FMLClientHandler.instance().getClientPlayerEntity());
	}
}
