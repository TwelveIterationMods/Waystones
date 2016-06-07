package net.blay09.mods.waystones.client;

import com.google.common.collect.Lists;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
import net.blay09.mods.waystones.client.render.WaystoneBlockRenderer;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

	private GuiButtonWarp buttonWarp;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(this);

		ClientRegistry.bindTileEntitySpecialRenderer(TileWaystone.class, new RenderWaystone());
		RenderingRegistry.registerBlockHandler(WaystoneBlockRenderer.RENDER_ID, new WaystoneBlockRenderer());
	}

	@SubscribeEvent
	@SuppressWarnings("unchecked")
	public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if (Waystones.getConfig().teleportButton && event.gui instanceof GuiInventory) {
			buttonWarp = new GuiButtonWarp((GuiContainer) event.gui);
			event.buttonList.add(buttonWarp);
		}
	}

	@SubscribeEvent
	public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		if (event.button instanceof GuiButtonWarp) {
			EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
			if (PlayerWaystoneData.canFreeWarp(entityPlayer) && PlayerWaystoneData.getLastWaystone(entityPlayer) != null) {
				if(Waystones.getConfig().teleportButtonReturnOnly) {
					event.gui.mc.displayGuiScreen(new GuiConfirmReturn());
				} else {
					Waystones.proxy.openWaystoneSelection(true);
				}
			} else {
				event.gui.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("random.click"), 0.5f));
				event.setCanceled(true);
			}
		}
	}

	private static final List<String> tmpTooltip = Lists.newArrayList();

	@SubscribeEvent
	public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
		if (event.gui instanceof GuiInventory && buttonWarp != null && buttonWarp.isHovered()) {
			tmpTooltip.clear();
			long timeSince = System.currentTimeMillis() - PlayerWaystoneData.getLastFreeWarp(FMLClientHandler.instance().getClientPlayerEntity());
			int secondsLeft = (int) ((Waystones.getConfig().warpStoneCooldown * 1000 - timeSince) / 1000);
			if (Waystones.getConfig().teleportButtonReturnOnly) {
				tmpTooltip.add(EnumChatFormatting.YELLOW + I18n.format("tooltip.waystones:returnToWaystone"));
				WaystoneEntry lastEntry = PlayerWaystoneData.getLastWaystone(FMLClientHandler.instance().getClientPlayerEntity());
				if (lastEntry != null) {
					tmpTooltip.add(EnumChatFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", EnumChatFormatting.DARK_AQUA + lastEntry.getName()));
				} else {
					tmpTooltip.add(EnumChatFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", I18n.format("tooltip.waystones:none")));
				}
				if (secondsLeft > 0) {
					tmpTooltip.add("");
					tmpTooltip.add(EnumChatFormatting.GRAY + I18n.format("tooltip.waystones:cooldownLeft", secondsLeft));
				}
			} else {
				tmpTooltip.add(EnumChatFormatting.YELLOW + I18n.format("tooltip.waystones:openWaystoneMenu"));
				if (secondsLeft > 0) {
					tmpTooltip.add(EnumChatFormatting.GRAY + I18n.format("tooltip.waystones:cooldownLeft", secondsLeft));
				}
			}
			event.gui.func_146283_a(tmpTooltip, event.mouseX, event.mouseY);
		}
	}

	@SubscribeEvent
	public void onFOV(FOVUpdateEvent event) {
		if(event.entity.getItemInUse() != null && event.entity.getItemInUse().getItem() == Waystones.itemReturnScroll) {
			event.newfov = event.entity.getItemInUseDuration() / 64f * 2f + 0.5f;
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
	public void printChatMessage(int id, IChatComponent chatComponent) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(chatComponent, id);
	}

	@Override
	public void playSound(String soundName, float pitch) {
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(soundName), pitch));
	}
}
