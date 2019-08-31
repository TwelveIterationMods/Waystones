package net.blay09.mods.waystones.client.gui;

import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageRemoveWaystone;
import net.blay09.mods.waystones.network.message.MessageSortWaystone;
import net.blay09.mods.waystones.network.message.MessageTeleportToWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.io.IOException;

public class GuiWaystoneList extends GuiScreen {

    private final WarpMode warpMode;
    private final EnumHand hand;
    private final WaystoneEntry fromWaystone;
    private WaystoneEntry[] entries;
    private GuiButton btnPrevPage;
    private GuiButton btnNextPage;
    private int pageOffset;
    private int headerY;
    private boolean isLocationHeaderHovered;

    public GuiWaystoneList(WaystoneEntry[] entries, WarpMode warpMode, EnumHand hand, @Nullable WaystoneEntry fromWaystone) {
        this.entries = entries;
        this.warpMode = warpMode;
        this.hand = hand;
        this.fromWaystone = fromWaystone;
    }

    @Override
    public void initGui() {
        btnPrevPage = new GuiButton(0, width / 2 - 100, height / 2 + 40, 95, 20, I18n.format("gui.waystones:warpStone.previousPage"));
        buttonList.add(btnPrevPage);

        btnNextPage = new GuiButton(1, width / 2 + 5, height / 2 + 40, 95, 20, I18n.format("gui.waystones:warpStone.nextPage"));
        buttonList.add(btnNextPage);

        updateList();
    }

    public void updateList() {
        final int maxContentHeight = (int) (height * 0.8f);
        final int headerHeight = 40;
        final int footerHeight = 25;
        final int entryHeight = 25;
        final int maxButtonsPerPage = (maxContentHeight - headerHeight - footerHeight) / entryHeight;

        final int buttonsPerPage = Math.max(4, Math.min(maxButtonsPerPage, entries.length));
        final int contentHeight = headerHeight + buttonsPerPage * entryHeight + footerHeight;
        headerY = height / 2 - contentHeight / 2;

        btnPrevPage.enabled = pageOffset > 0;
        btnNextPage.enabled = pageOffset < (entries.length - 1) / buttonsPerPage;

        buttonList.removeIf(button -> button instanceof GuiButtonWaystoneEntry || button instanceof GuiButtonSortWaystone || button instanceof GuiButtonRemoveWaystone);

        int id = 2;
        int y = headerHeight;
        for (int i = 0; i < buttonsPerPage; i++) {
            int entryIndex = pageOffset * buttonsPerPage + i;
            if (entryIndex >= 0 && entryIndex < entries.length) {
                GuiButtonWaystoneEntry btnWaystone = new GuiButtonWaystoneEntry(id, width / 2 - 100, headerY + y, entries[entryIndex], warpMode);
                if (entries[entryIndex].equals(fromWaystone)) {
                    btnWaystone.enabled = false;
                }
                buttonList.add(btnWaystone);
                id++;

                GuiButtonSortWaystone sortUp = new GuiButtonSortWaystone(id, width / 2 + 108, headerY + y + 2, btnWaystone, -1);
                if (entryIndex == 0) {
                    sortUp.visible = false;
                }
                buttonList.add(sortUp);
                id++;

                GuiButtonSortWaystone sortDown = new GuiButtonSortWaystone(id, width / 2 + 108, headerY + y + 11, btnWaystone, 1);
                if (entryIndex == entries.length - 1) {
                    sortDown.visible = false;
                }
                buttonList.add(sortDown);
                id++;

                GuiButtonRemoveWaystone remove = new GuiButtonRemoveWaystone(id, width / 2 + 122, headerY + y + 4, btnWaystone);
                buttonList.add(remove);
                id++;

                y += 22;
            }
        }

        btnPrevPage.y = headerY + headerHeight + buttonsPerPage * 22 + (entries.length > 0 ? 10 : 0);
        btnNextPage.y = headerY + headerHeight + buttonsPerPage * 22 + (entries.length > 0 ? 10 : 0);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == btnNextPage) {
            pageOffset++;
            updateList();
        } else if (button == btnPrevPage) {
            pageOffset--;
            updateList();
        } else if (button instanceof GuiButtonWaystoneEntry) {
            NetworkHandler.channel.sendToServer(new MessageTeleportToWaystone(((GuiButtonWaystoneEntry) button).getWaystone(), warpMode, hand, fromWaystone));
            mc.displayGuiScreen(null);
        } else if (button instanceof GuiButtonSortWaystone) {
            WaystoneEntry waystoneEntry = ((GuiButtonSortWaystone) button).getWaystone();
            int index = ArrayUtils.indexOf(entries, waystoneEntry);
            int sortDir = ((GuiButtonSortWaystone) button).getSortDir();
            int otherIndex = index + sortDir;
            if (index == -1 || otherIndex < 0 || otherIndex >= entries.length) {
                return;
            }

            WaystoneEntry swap = entries[index];
            entries[index] = entries[otherIndex];
            entries[otherIndex] = swap;
            NetworkHandler.channel.sendToServer(new MessageSortWaystone(index, otherIndex));
            updateList();
        } else if (button instanceof GuiButtonRemoveWaystone) {
            WaystoneEntry waystoneEntry = ((GuiButtonRemoveWaystone) button).getWaystone();
            int index = ArrayUtils.indexOf(entries, waystoneEntry);
            entries = ArrayUtils.remove(entries, index);
            NetworkHandler.channel.sendToServer(new MessageRemoveWaystone(index));
            updateList();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isLocationHeaderHovered && fromWaystone != null) {
            Waystones.proxy.openWaystoneSettings(mc.player, fromWaystone, true);

            mouseHandled = true;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        drawCenteredString(fontRenderer, I18n.format("gui.waystones:warpStone.selectDestination"), width / 2, headerY + (fromWaystone != null ? 20 : 0), 0xFFFFFF);
        if (fromWaystone != null) {
            drawLocationHeader(fromWaystone.getName(), mouseX, mouseY, width / 2, headerY);
        }

        if (entries.length == 0) {
            drawCenteredString(fontRenderer, TextFormatting.RED + I18n.format("waystones:scrollNotBound"), width / 2, height / 2 - 20, 0xFFFFFF);
        }
    }

    public void drawLocationHeader(String locationName, int mouseX, int mouseY, int x, int y) {
        String locationPrefix = TextFormatting.YELLOW + I18n.format("gui.waystones:current_location") + " ";
        int locationPrefixWidth = fontRenderer.getStringWidth(locationPrefix);

        int locationWidth = fontRenderer.getStringWidth(locationName);

        int fullWidth = locationPrefixWidth + locationWidth;

        int startX = x - fullWidth / 2 + locationPrefixWidth;
        if (mouseX >= startX && mouseX < startX + locationWidth + 16
                && mouseY >= y && mouseY < y + fontRenderer.FONT_HEIGHT) {
            isLocationHeaderHovered = true;
        } else {
            isLocationHeaderHovered = false;
        }

        String fullText = locationPrefix + TextFormatting.WHITE;
        if (isLocationHeaderHovered) {
            fullText += TextFormatting.UNDERLINE;
        }
        fullText += locationName;

        drawString(fontRenderer, TextFormatting.UNDERLINE + fullText, x - fullWidth / 2, y, 0xFFFFFF);

        if (isLocationHeaderHovered) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + fullWidth / 2 + 4, y, 0f);
            float scale = 0.5f;
            GlStateManager.scale(scale, scale, scale);
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.WRITABLE_BOOK), 0, 0);
            GlStateManager.popMatrix();
        }
    }

}
