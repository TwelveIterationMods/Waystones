package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.client.gui.widget.RemoveWaystoneButton;
import net.blay09.mods.waystones.client.gui.widget.SortWaystoneButton;
import net.blay09.mods.waystones.client.gui.widget.WaystoneEntryButton;
import net.blay09.mods.waystones.core.IWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageRemoveWaystone;
import net.blay09.mods.waystones.network.message.MessageSortWaystone;
import net.blay09.mods.waystones.network.message.MessageTeleportToWaystone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class WaystoneListScreen extends Screen {

    private final WarpMode warpMode;
    private final Hand hand;
    private final IWaystone fromWaystone;
    private IWaystone[] entries;
    private Button btnPrevPage;
    private Button btnNextPage;
    private int pageOffset;
    private int headerY;
    private boolean isLocationHeaderHovered;

    public WaystoneListScreen(IWaystone[] entries, WarpMode warpMode, Hand hand, @Nullable IWaystone fromWaystone) {
        super(new TranslationTextComponent("gui.waystones:warpStone.selectDestination"));
        this.entries = entries;
        this.warpMode = warpMode;
        this.hand = hand;
        this.fromWaystone = fromWaystone;
    }

    @Override
    public void init() {
        btnPrevPage = new Button(width / 2 - 100, height / 2 + 40, 95, 20, I18n.format("gui.waystones:warpStone.previousPage"), button -> {
            pageOffset--;
            updateList();
        });
        addButton(btnPrevPage);

        btnNextPage = new Button(width / 2 + 5, height / 2 + 40, 95, 20, I18n.format("gui.waystones:warpStone.nextPage"), button -> {
            pageOffset++;
            updateList();
        });
        addButton(btnNextPage);

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

        btnPrevPage.active = pageOffset > 0;
        btnNextPage.active = pageOffset < (entries.length - 1) / buttonsPerPage;

        buttons.removeIf(button -> button instanceof WaystoneEntryButton || button instanceof SortWaystoneButton || button instanceof RemoveWaystoneButton);

        int y = headerHeight;
        for (int i = 0; i < buttonsPerPage; i++) {
            int entryIndex = pageOffset * buttonsPerPage + i;
            if (entryIndex >= 0 && entryIndex < entries.length) {
                WaystoneEntryButton btnWaystone = new WaystoneEntryButton(width / 2 - 100, headerY + y, entries[entryIndex], warpMode, button -> {
                    NetworkHandler.channel.sendToServer(new MessageTeleportToWaystone(((WaystoneEntryButton) button).getWaystone(), warpMode, hand, fromWaystone));
                    getMinecraft().displayGuiScreen(null);
                });
                if (entries[entryIndex].equals(fromWaystone)) {
                    btnWaystone.active = false;
                }
                addButton(btnWaystone);

                SortWaystoneButton sortUp = new SortWaystoneButton(width / 2 + 108, headerY + y + 2, btnWaystone, -1, this::sortButtonHandler);
                if (entryIndex == 0) {
                    sortUp.visible = false;
                }
                addButton(sortUp);

                SortWaystoneButton sortDown = new SortWaystoneButton(width / 2 + 108, headerY + y + 11, btnWaystone, 1, this::sortButtonHandler);
                if (entryIndex == entries.length - 1) {
                    sortDown.visible = false;
                }
                addButton(sortDown);

                RemoveWaystoneButton remove = new RemoveWaystoneButton(width / 2 + 122, headerY + y + 4, btnWaystone, button -> {
                    IWaystone waystoneEntry = ((RemoveWaystoneButton) button).getWaystone();
                    int index = ArrayUtils.indexOf(entries, waystoneEntry);
                    entries = ArrayUtils.remove(entries, index);
                    NetworkHandler.channel.sendToServer(new MessageRemoveWaystone(index));
                    updateList();
                });
                addButton(remove);

                y += 22;
            }
        }

        btnPrevPage.y = headerY + headerHeight + buttonsPerPage * 22 + (entries.length > 0 ? 10 : 0);
        btnNextPage.y = headerY + headerHeight + buttonsPerPage * 22 + (entries.length > 0 ? 10 : 0);
    }

    private void sortButtonHandler(Button button) {
        IWaystone waystoneEntry = ((SortWaystoneButton) button).getWaystone();
        int index = ArrayUtils.indexOf(entries, waystoneEntry);
        int sortDir = ((SortWaystoneButton) button).getSortDir();
        int otherIndex = index + sortDir;
        if (index == -1 || otherIndex < 0 || otherIndex >= entries.length) {
            return;
        }

        IWaystone swap = entries[index];
        entries[index] = entries[otherIndex];
        entries[otherIndex] = swap;
        NetworkHandler.channel.sendToServer(new MessageSortWaystone(index, otherIndex));
        updateList();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (isLocationHeaderHovered && fromWaystone != null) {
            Waystones.proxy.openWaystoneSettings(Minecraft.getInstance().player, fromWaystone, true);

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

        GL11.glColor4f(1f, 1f, 1f, 1f);
        drawCenteredString(fontRenderer, getTitle().getFormattedText(), width / 2, headerY + (fromWaystone != null ? 20 : 0), 0xFFFFFF);
        if (fromWaystone != null) {
            drawLocationHeader(fromWaystone.getName(), mouseX, mouseY, width / 2, headerY);
        }

        if (entries.length == 0) {
            drawCenteredString(fontRenderer, TextFormatting.RED + I18n.format("waystones:scrollNotBound"), width / 2, height / 2 - 20, 0xFFFFFF);
        }
    }

    public void drawLocationHeader(String locationName, int mouseX, int mouseY, int x, int y) {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

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
            GlStateManager.translated(x + fullWidth / 2f + 4, y, 0f);
            float scale = 0.5f;
            GlStateManager.scalef(scale, scale, scale);
            Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(Items.WRITABLE_BOOK), 0, 0);
            GlStateManager.popMatrix();
        }
    }

}
