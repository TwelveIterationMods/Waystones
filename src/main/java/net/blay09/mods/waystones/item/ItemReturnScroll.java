package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.util.WaystoneEntry;
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

public class ItemReturnScroll extends Item {

	public ItemReturnScroll() {
		setCreativeTab(CreativeTabs.tabTools);
		setUnlocalizedName(Waystones.MOD_ID + ":returnScroll");
		setTextureName(Waystones.MOD_ID + ":returnScroll");
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
		if(!world.isRemote) {
			WaystoneEntry lastEntry = PlayerWaystoneData.getLastWaystone(player);
			if(lastEntry != null) {
				if(WaystoneManager.teleportToWaystone(player, lastEntry)) {
					if(!player.capabilities.isCreativeMode) {
						itemStack.stackSize--;
					}
				}
			}
		}
		return itemStack;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if(PlayerWaystoneData.getLastWaystone(player) != null) {
			if(!player.isUsingItem() && world.isRemote) {
				Waystones.proxy.playSound("portal.trigger", 2f);
			}
			player.setItemInUse(itemStack, getMaxItemUseDuration(itemStack));
		} else {
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("waystones:scrollNotBound");
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
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean debug) {
		WaystoneEntry lastEntry = PlayerWaystoneData.getLastWaystone(player);
		if(lastEntry != null) {
			list.add(EnumChatFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", EnumChatFormatting.DARK_AQUA + lastEntry.getName()));
		} else {
			list.add(EnumChatFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", I18n.format("tooltip.waystones:none")));
		}
	}
}
