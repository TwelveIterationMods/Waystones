package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.client.ClientWaystones;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMemoryStone extends Item {

	private static final String NBT_MEMORY_STONE_TARGET = "MemoryStoneTarget";

	public static final String name = "memory_stone";
	public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

	public ItemMemoryStone() {
		setCreativeTab(Waystones.creativeTab);
		setRegistryName(name);
		setUnlocalizedName(registryName.toString());
	}

	private WaystoneEntry getBoundWaystone(ItemStack stack) {
		WaystoneEntry boundEntry = null;

		if(stack.hasTagCompound()) {
			if(stack.getTagCompound().hasKey(NBT_MEMORY_STONE_TARGET)) {
				boundEntry = WaystoneEntry.read(stack.getTagCompound().getCompoundTag(NBT_MEMORY_STONE_TARGET));
			}
		}

		return boundEntry;
	}

	public static boolean imprintWaystone(EntityPlayer player, ItemStack stack, WaystoneEntry entry) {
		if(entry.isGlobal()) {
			player.sendStatusMessage(new TextComponentTranslation("waystones:waystoneIsGlobal"), true);
			return false;
		}
		if(!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		if(stack.getTagCompound().hasKey(NBT_MEMORY_STONE_TARGET)) {
			player.sendStatusMessage(new TextComponentTranslation("waystones:filledMemoryStone"), true);
			return false;
		}
		stack.getTagCompound().setTag(NBT_MEMORY_STONE_TARGET, entry.writeToNBT());

		Waystones.proxy.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, new BlockPos(player.posX, player.posY, player.posZ), 2f);
		Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.posX, player.posY, player.posZ), 3f);
		player.sendStatusMessage(new TextComponentString(I18n.format("waystones:filledMemoryStoneWith", TextFormatting.DARK_AQUA + entry.getName())), true);

		stack.setStackDisplayName(I18n.format("item.waystones:memory_stone.name.bound", entry.getName()));

		return true;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemStack) {
		return EnumAction.BOW;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entity) {

		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			WaystoneEntry waystone = getBoundWaystone(itemStack);

			if(waystone != null) {
				WaystoneEntry knownWaystone = world.isRemote ? ClientWaystones.getKnownWaystone(waystone.getName()) : null;

				if(knownWaystone == null) {
					if(!world.isRemote) {
						WaystoneManager.removePlayerWaystone(player, waystone);
						WaystoneManager.addPlayerWaystone(player, waystone);
						WaystoneManager.sendPlayerWaystones(player);
					}
					else {
						BlockPos pos = player.getPosition();
						Waystones.proxy.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, pos, 1f);
						for (int i = 0; i < 32; i++) {
							world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, pos.getY() + 3, pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, 0, -5, 0);
							world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, pos.getY() + 4, pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 2, 0, -5, 0);
						}
						player.sendStatusMessage(new TextComponentString(I18n.format("waystones:knowNewWaystone", TextFormatting.DARK_AQUA + waystone.getName())), true);
					}
				}
				else {
					player.sendStatusMessage(new TextComponentTranslation("waystones:waystoneIsKnown"), true);
				}
			}
		}

		return itemStack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack itemStack = player.getHeldItem(hand);
		WaystoneEntry boundEntry = getBoundWaystone(itemStack);

		if(boundEntry != null) {
			if(!player.isHandActive() && world.isRemote) {
				Waystones.proxy.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, new BlockPos(player.posX, player.posY, player.posZ), 3f);
			}
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
		} else {
			player.sendStatusMessage(new TextComponentTranslation("waystones:emptyMemoryStone"), true);
			return new ActionResult<>(EnumActionResult.FAIL, itemStack);
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player == null) {
			return;
		}
		WaystoneEntry boundEntry = getBoundWaystone(stack);

		if(boundEntry != null) {
			tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:memoryOf", boundEntry.getName()));
		} else {
			tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:noMemory"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack itemStack) {
		return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(NBT_MEMORY_STONE_TARGET);
	}

}
