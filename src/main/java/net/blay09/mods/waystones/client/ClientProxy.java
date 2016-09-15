package net.blay09.mods.waystones.client;

import com.google.common.collect.Lists;
import net.blay09.mods.waystones.CommonProxy;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.client.gui.GuiButtonWarp;
import net.blay09.mods.waystones.client.gui.GuiConfirmReturn;
import net.blay09.mods.waystones.client.gui.GuiWarpStone;
import net.blay09.mods.waystones.client.gui.GuiWaystoneName;
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
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

	private GuiButtonWarp buttonWarp;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(this);

		ClientRegistry.bindTileEntitySpecialRenderer(TileWaystone.class, new RenderWaystone());
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(Waystones.blockWaystone), 0, TileWaystone.class);
		Item itemBlockWaystone = Item.getItemFromBlock(Waystones.blockWaystone);
		if(itemBlockWaystone != null) {
			ModelLoader.setCustomModelResourceLocation(itemBlockWaystone, 0, new ModelResourceLocation("waystones:waystone", "inventory"));
		}
		ModelLoader.setCustomModelResourceLocation(Waystones.itemWarpStone, 0, new ModelResourceLocation(Waystones.itemWarpStone.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Waystones.itemReturnScroll, 0, new ModelResourceLocation(Waystones.itemReturnScroll.getRegistryName(), "inventory"));
	}

	@SubscribeEvent
	@SuppressWarnings("unchecked")
	public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if (Waystones.getConfig().teleportButton && event.getGui() instanceof GuiInventory) {
			buttonWarp = new GuiButtonWarp((GuiContainer) event.getGui());
			event.getButtonList().add(buttonWarp);
		}
	}

	@SubscribeEvent
	public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		if (event.getButton() instanceof GuiButtonWarp) {
			EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
			if (PlayerWaystoneData.canFreeWarp(entityPlayer) && PlayerWaystoneData.getLastWaystone(entityPlayer) != null) {
				if(Waystones.getConfig().teleportButtonReturnOnly) {
					event.getGui().mc.displayGuiScreen(new GuiConfirmReturn());
				} else {
					Waystones.proxy.openWaystoneSelection(true);
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
			long timeSince = System.currentTimeMillis() - PlayerWaystoneData.getLastFreeWarp(FMLClientHandler.instance().getClientPlayerEntity());
			int secondsLeft = (int) ((Waystones.getConfig().warpStoneCooldown * 1000 - timeSince) / 1000);
			if (Waystones.getConfig().teleportButtonReturnOnly) {
				tmpTooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:returnToWaystone"));
				WaystoneEntry lastEntry = PlayerWaystoneData.getLastWaystone(FMLClientHandler.instance().getClientPlayerEntity());
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
		if(event.getEntity().getActiveItemStack() != null && event.getEntity().getActiveItemStack().getItem() == Waystones.itemReturnScroll) {
			event.setNewfov(event.getEntity().getItemInUseCount() / 64f * 2f + 0.5f);
		}
	}

	@Override
	public void openWaystoneNameEdit(TileWaystone tileEntity) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiWaystoneName(tileEntity));
	}

	@Override
	public void openWaystoneSelection(boolean isFree) {
		WaystoneEntry[] playerWaystones = PlayerWaystoneData.fromPlayer(FMLClientHandler.instance().getClientPlayerEntity()).getWaystones();
		WaystoneEntry[] combinedWaystones = new WaystoneEntry[WaystoneManager.getServerWaystones().size() + playerWaystones.length];
		int i = 0;
		for(WaystoneEntry entry : WaystoneManager.getServerWaystones()) {
			combinedWaystones[i] = entry;
			i++;
		}
		System.arraycopy(playerWaystones, 0, combinedWaystones, i, playerWaystones.length);
 		Minecraft.getMinecraft().displayGuiScreen(new GuiWarpStone(combinedWaystones, isFree));
	}

	@Override
	public void printChatMessage(int id, ITextComponent chatComponent) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(chatComponent, id);
	}

	@Override
	public void playSound(SoundEvent sound, float pitch) {
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, pitch));
	}

	@Override
	public void addScheduledTask(Runnable runnable) {
		Minecraft.getMinecraft().addScheduledTask(runnable);
	}
}
