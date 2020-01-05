package net.blay09.mods.waystones.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;

public class WaystoneEntryButton extends Button {

    private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");

    private final IWaystone waystone;
    private final int xpLevelCost;

    public WaystoneEntryButton(int x, int y, IWaystone waystone, WarpMode mode, IPressable pressable) {
        super(x, y, 150, 20, (waystone.isGlobal() ? TextFormatting.YELLOW : "") + waystone.getName(), pressable);
        this.waystone = waystone;
        PlayerEntity player = Minecraft.getInstance().player;
        boolean enableXPCost = false;
        switch (mode) {
            case WARP_STONE:
                enableXPCost = WaystoneConfig.COMMON.warpStoneXpCost.get();
                break;
            case INVENTORY_BUTTON:
                enableXPCost = WaystoneConfig.COMMON.inventoryButtonXpCost.get();
                break;
            case WAYSTONE_TO_WAYSTONE:
                enableXPCost = WaystoneConfig.COMMON.waystoneXpCost.get();
                break;
        }

        if (!WaystoneConfig.SERVER.globalWaystonesCostXp.get() && waystone.isGlobal()) {
            enableXPCost = false;
        }

        this.xpLevelCost = (enableXPCost && WaystoneConfig.SERVER.blocksPerXPLevel.get() > 0) ? MathHelper.clamp((int) Math.sqrt(player.getDistanceSq(new Vec3d(waystone.getPos()))) / WaystoneConfig.SERVER.blocksPerXPLevel.get(), 0, WaystoneConfig.SERVER.maximumXpCost.get()) : 0;

        if (waystone.getDimensionType() != Minecraft.getInstance().world.getDimension().getType()) {
            // TODO if (!WaystoneManagerLegacy.isDimensionWarpAllowed(waystone)) {
                //active = false;
            //}
        }

        if (player.experienceLevel < xpLevelCost && !player.abilities.isCreativeMode) {
            active = false;
        }
    }

    public IWaystone getWaystone() {
        return waystone;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        super.renderButton(mouseX, mouseY, partialTicks);
        GlStateManager.color4f(1f, 1f, 1f, 1f);

        Minecraft minecraft = Minecraft.getInstance();
        if (xpLevelCost > 0) {
            boolean canAfford = minecraft.player.experienceLevel >= xpLevelCost || minecraft.player.abilities.isCreativeMode;
            minecraft.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
            blit(x + 2, y + 2, (Math.min(xpLevelCost, 3) - 1) * 16, 223 + (!canAfford ? 16 : 0), 16, 16);

            if (xpLevelCost > 3) {
                minecraft.fontRenderer.drawString("+", x + 17, y + 6, 0xC8FF8F);
            }

            if (isHovered && mouseX <= x + 16) {
                GuiUtils.drawHoveringText(Lists.newArrayList((canAfford ? TextFormatting.GREEN : TextFormatting.RED) + I18n.format("tooltip.waystones:levelRequirement", xpLevelCost)), mouseX, mouseY + minecraft.fontRenderer.FONT_HEIGHT, minecraft.mainWindow.getWidth(), minecraft.mainWindow.getHeight(), 200, minecraft.fontRenderer);
            }
            GlStateManager.disableLighting();
        }
    }

}
