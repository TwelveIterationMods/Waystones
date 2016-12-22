package net.blay09.mods.waystones.client;

import com.google.common.collect.Lists;
import net.blay09.mods.waystones.CommonProxy;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.client.gui.GuiButtonInventoryWarp;
import net.blay09.mods.waystones.client.gui.GuiConfirmInventoryButtonReturn;
import net.blay09.mods.waystones.client.gui.GuiEditWaystone;
import net.blay09.mods.waystones.client.gui.GuiWaystoneList;
import net.blay09.mods.waystones.client.render.RenderWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

public class ClientProxy extends CommonProxy {

	private GuiButtonInventoryWarp buttonWarp;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(this);

		ClientRegistry.bindTileEntitySpecialRenderer(TileWaystone.class, new RenderWaystone());
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(Waystones.blockWaystone), 0, TileWaystone.class);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Waystones.blockWaystone), 0, new ModelResourceLocation("waystones:waystone", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Waystones.itemWarpStone, 0, new ModelResourceLocation(Waystones.itemWarpStone.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Waystones.itemReturnScroll, 0, new ModelResourceLocation(Waystones.itemReturnScroll.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Waystones.itemWarpScroll, 0, new ModelResourceLocation(Waystones.itemWarpScroll.getRegistryName(), "inventory"));
	}

	@SubscribeEvent
	public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if (Waystones.getConfig().teleportButton && event.getGui() instanceof GuiInventory) {
			buttonWarp = new GuiButtonInventoryWarp((GuiContainer) event.getGui());
			event.getButtonList().add(buttonWarp);
		}
	}

	@SubscribeEvent
	public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		if (event.getButton() instanceof GuiButtonInventoryWarp) {
			EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
			if (PlayerWaystoneHelper.canFreeWarp(entityPlayer)) {
				if(Waystones.getConfig().teleportButtonReturnOnly) {
					if(PlayerWaystoneHelper.getLastWaystone(entityPlayer) != null){
						event.getGui().mc.displayGuiScreen(new GuiConfirmInventoryButtonReturn());
					}
				} else {
					Waystones.proxy.openWaystoneSelection(WarpMode.INVENTORY_BUTTON, EnumHand.MAIN_HAND, null);
				}
			} else {
				event.getGui().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 0.5f));
				event.setCanceled(true);
			}
		}
	}

	private static final List<String> tmpTooltip = Lists.newArrayList();

	@SubscribeEvent
	public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
		if (event.getGui() instanceof GuiInventory && buttonWarp != null && buttonWarp.isHovered()) {
			tmpTooltip.clear();
			long timeSince = System.currentTimeMillis() - PlayerWaystoneHelper.getLastFreeWarp(FMLClientHandler.instance().getClientPlayerEntity());
			int secondsLeft = (int) ((Waystones.getConfig().teleportButtonCooldown * 1000 - timeSince) / 1000);
			if (Waystones.getConfig().teleportButtonReturnOnly) {
				tmpTooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:returnToWaystone"));
				WaystoneEntry lastEntry = PlayerWaystoneHelper.getLastWaystone(FMLClientHandler.instance().getClientPlayerEntity());
				if (lastEntry != null) {
					tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", TextFormatting.DARK_AQUA + lastEntry.getName()));
				} else {
					tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", I18n.format("tooltip.waystones:none")));
				}
				if (secondsLeft > 0) {
					tmpTooltip.add("");
					tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:cooldownLeft", secondsLeft));
				}
			} else {
				tmpTooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:openWaystoneMenu"));
				if (secondsLeft > 0) {
					tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:cooldownLeft", secondsLeft));
				}
			}
			event.getGui().drawHoveringText(tmpTooltip, event.getMouseX(), event.getMouseY());
		}
	}

	@SubscribeEvent
	public void onFOV(FOVUpdateEvent event) {
		if(!event.getEntity().getActiveItemStack().isEmpty() && event.getEntity().getActiveItemStack().getItem() == Waystones.itemReturnScroll) {
			event.setNewfov(event.getEntity().getItemInUseCount() / 64f * 2f + 0.5f);
		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if(id == 1) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if(tileEntity instanceof TileWaystone) {
				return new GuiEditWaystone((TileWaystone) tileEntity);
			}
		}
		return null;
	}

	@Override
	public void openWaystoneSelection(WarpMode mode, EnumHand hand, @Nullable WaystoneEntry fromWaystone) {
		WaystoneEntry[] waystones = PlayerWaystoneData.fromPlayer(FMLClientHandler.instance().getClientPlayerEntity()).getWaystones();
 		Minecraft.getMinecraft().displayGuiScreen(new GuiWaystoneList(waystones, mode, hand, fromWaystone));
	}

	@Override
	public void playSound(SoundEvent sound, BlockPos pos, float pitch) {
		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(sound, SoundCategory.AMBIENT, WaystoneConfig.soundVolume, pitch, pos));
	}

}
