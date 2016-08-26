package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.util.WaystoneEntry;
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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemReturnScroll extends Item {

	public ItemReturnScroll() {
		setCreativeTab(CreativeTabs.TOOLS);
		setRegistryName(Waystones.MOD_ID, "warpScroll");
		setUnlocalizedName(getRegistryName().toString());
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
	public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entity) {
		if(!world.isRemote && entity instanceof EntityPlayer) {
			WaystoneEntry lastEntry = PlayerWaystoneData.getLastWaystone((EntityPlayer) entity);
			if(lastEntry != null) {
				if(WaystoneManager.teleportToWaystone((EntityPlayer) entity, lastEntry)) {
					if(!((EntityPlayer) entity).capabilities.isCreativeMode) {
						itemStack.stackSize--;
					}
				}
			}
		}
		return itemStack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if(PlayerWaystoneData.getLastWaystone(player) != null) {
			if(!player.isHandActive() && world.isRemote) {
				Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, 2f);
			}
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
		} else {
			TextComponentTranslation chatComponent = new TextComponentTranslation("waystones:scrollNotBound");
			chatComponent.getStyle().setColor(TextFormatting.RED);
			Waystones.proxy.printChatMessage(3, chatComponent);
			return new ActionResult<>(EnumActionResult.FAIL, itemStack);
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean debug) {
		WaystoneEntry lastEntry = PlayerWaystoneData.getLastWaystone(player);
		if(lastEntry != null) {
			list.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", TextFormatting.DARK_AQUA + lastEntry.getName()));
		} else {
			list.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", I18n.format("tooltip.waystones:none")));
		}
	}
}
