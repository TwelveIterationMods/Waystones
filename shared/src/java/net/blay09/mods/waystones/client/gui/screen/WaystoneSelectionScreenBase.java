package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.forbic.mixin.ScreenAccessor;
import net.blay09.mods.forbic.network.ForbicNetworking;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.client.gui.widget.ITooltipProvider;
import net.blay09.mods.waystones.client.gui.widget.RemoveWaystoneButton;
import net.blay09.mods.waystones.client.gui.widget.SortWaystoneButton;
import net.blay09.mods.waystones.client.gui.widget.WaystoneButton;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneEditPermissions;
import net.blay09.mods.waystones.network.message.RemoveWaystoneMessage;
import net.blay09.mods.waystones.network.message.RequestEditWaystoneMessage;
import net.blay09.mods.waystones.network.message.SelectWaystoneMessage;
import net.blay09.mods.waystones.network.message.SortWaystoneMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class WaystoneSelectionScreenBase extends AbstractContainerScreen<WaystoneSelectionMenu> {

    private final List<IWaystone> waystones;
    private final List<ITooltipProvider> tooltipProviders = new ArrayList<>();

    private Button btnPrevPage;
    private Button btnNextPage;
    private int pageOffset;
    private int headerY;
    private boolean isLocationHeaderHovered;
    private int buttonsPerPage;

    private static final int headerHeight = 40;
    private static final int footerHeight = 25;
    private static final int entryHeight = 25;

    public WaystoneSelectionScreenBase(WaystoneSelectionMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        waystones = container.getWaystones();
        imageWidth = 270;
        imageHeight = 200;
    }

    @Override
    public void init() {
        final int maxContentHeight = (int) (height * 0.6f);
        final int maxButtonsPerPage = (maxContentHeight - headerHeight - footerHeight) / entryHeight;
        buttonsPerPage = Math.max(4, Math.min(maxButtonsPerPage, waystones.size()));
        final int contentHeight = headerHeight + buttonsPerPage * entryHeight + footerHeight;

        // Leave no space for JEI!
        imageWidth = width;
        imageHeight = contentHeight;

        super.init();

        tooltipProviders.clear();
        btnPrevPage = new Button(width / 2 - 100, height / 2 + 40, 95, 20, new TranslatableComponent("gui.waystones.waystone_selection.previous_page"), button -> {
            pageOffset = Screen.hasShiftDown() ? 0 : pageOffset - 1;
            updateList();
        });
        addRenderableWidget(btnPrevPage);

        btnNextPage = new Button(width / 2 + 5, height / 2 + 40, 95, 20, new TranslatableComponent("gui.waystones.waystone_selection.next_page"), button -> {
            pageOffset = Screen.hasShiftDown() ? (waystones.size() - 1) / buttonsPerPage : pageOffset + 1;
            updateList();
        });
        addRenderableWidget(btnNextPage);

        updateList();
    }

    @Override
    protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget) {
        if (widget instanceof ITooltipProvider) {
            tooltipProviders.add((ITooltipProvider) widget);
        }
        return super.addRenderableWidget(widget);
    }

    private void updateList() {
        headerY = 0;

        btnPrevPage.active = pageOffset > 0;
        btnNextPage.active = pageOffset < (waystones.size() - 1) / buttonsPerPage;

        tooltipProviders.clear();

        Predicate<Object> removePredicate = button -> button instanceof WaystoneButton || button instanceof SortWaystoneButton || button instanceof RemoveWaystoneButton;
        ((ScreenAccessor) this).getChildren().removeIf(removePredicate);
        ((ScreenAccessor) this).getNarratables().removeIf(removePredicate);
        ((ScreenAccessor) this).getRenderables().removeIf(removePredicate);

        int y = topPos + headerHeight + headerY;
        for (int i = 0; i < buttonsPerPage; i++) {
            int entryIndex = pageOffset * buttonsPerPage + i;
            if (entryIndex >= 0 && entryIndex < waystones.size()) {
                IWaystone waystone = waystones.get(entryIndex);

                addRenderableWidget(createWaystoneButton(y, waystone));

                if (allowSorting()) {
                    SortWaystoneButton sortUpButton = new SortWaystoneButton(width / 2 + 108, y + 2, -1, y, 20, it -> sortWaystone(entryIndex, -1));
                    if (entryIndex == 0) {
                        sortUpButton.active = false;
                    }
                    addRenderableWidget(sortUpButton);

                    SortWaystoneButton sortDownButton = new SortWaystoneButton(width / 2 + 108, y + 13, 1, y, 20, it -> sortWaystone(entryIndex, 1));
                    if (entryIndex == waystones.size() - 1) {
                        sortDownButton.active = false;
                    }
                    addRenderableWidget(sortDownButton);
                }

                if (allowDeletion()) {
                    RemoveWaystoneButton removeButton = new RemoveWaystoneButton(width / 2 + 122, y + 4, y, 20, waystone, button -> {
                        Player player = Minecraft.getInstance().player;
                        PlayerWaystoneManager.deactivateWaystone(Objects.requireNonNull(player), waystone);
                        ForbicNetworking.sendToServer(new RemoveWaystoneMessage(waystone.getWaystoneUid()));
                        updateList();
                    });
                    // Only show the remove button for non-global waystones, or if the player is in creative mode
                    if (!waystone.isGlobal() || Minecraft.getInstance().player.getAbilities().instabuild) {
                        addRenderableWidget(removeButton);
                    }
                }

                y += 22;
            }
        }

        btnPrevPage.y = topPos + headerY + headerHeight + buttonsPerPage * 22 + (waystones.size() > 0 ? 10 : 0);
        btnNextPage.y = topPos + headerY + headerHeight + buttonsPerPage * 22 + (waystones.size() > 0 ? 10 : 0);
    }

    private WaystoneButton createWaystoneButton(int y, final IWaystone waystone) {
        IWaystone waystoneFrom = menu.getWaystoneFrom();
        Player player = Minecraft.getInstance().player;
        int xpLevelCost = Math.round(PlayerWaystoneManager.getExperienceLevelCost(Objects.requireNonNull(player), waystone, menu.getWarpMode(), waystoneFrom));
        WaystoneButton btnWaystone = new WaystoneButton(width / 2 - 100, y, waystone, xpLevelCost, button -> onWaystoneSelected(waystone));
        if (waystoneFrom != null && waystone.getWaystoneUid().equals(waystoneFrom.getWaystoneUid())) {
            btnWaystone.active = false;
        }
        return btnWaystone;
    }

    protected void onWaystoneSelected(IWaystone waystone) {
        ForbicNetworking.sendToServer(new SelectWaystoneMessage(waystone.getWaystoneUid()));
    }

    private void sortWaystone(int index, int sortDir) {
        if (index < 0 || index >= waystones.size()) {
            return;
        }

        int otherIndex;
        if (Screen.hasShiftDown()) {
            otherIndex = sortDir == -1 ? -1 : waystones.size();
        } else {
            otherIndex = index + sortDir;
            if (otherIndex < 0 || otherIndex >= waystones.size()) {
                return;
            }
        }

        PlayerWaystoneManager.swapWaystoneSorting(Minecraft.getInstance().player, index, otherIndex);
        ForbicNetworking.sendToServer(new SortWaystoneMessage(index, otherIndex));
        updateList();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (isLocationHeaderHovered && menu.getWaystoneFrom() != null) {
            ForbicNetworking.sendToServer(new RequestEditWaystoneMessage(menu.getWaystoneFrom().getWaystoneUid()));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
        for (ITooltipProvider tooltipProvider : tooltipProviders) {
            if (tooltipProvider.shouldShowTooltip()) {
                renderTooltip(matrixStack, tooltipProvider.getTooltip(), Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(PoseStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        Font fontRenderer = Minecraft.getInstance().font;

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        IWaystone fromWaystone = menu.getWaystoneFrom();
        drawCenteredString(matrixStack, fontRenderer, getTitle(), imageWidth / 2, headerY + (fromWaystone != null ? 20 : 0), 0xFFFFFF);
        if (fromWaystone != null) {
            drawLocationHeader(matrixStack, fromWaystone, mouseX, mouseY, imageWidth / 2, headerY);
        }

        if (waystones.size() == 0) {
            drawCenteredString(matrixStack, fontRenderer, ChatFormatting.RED + I18n.get("gui.waystones.waystone_selection.no_waystones_activated"), imageWidth / 2, imageHeight / 2 - 20, 0xFFFFFF);
        }
    }

    private void drawLocationHeader(PoseStack matrixStack, IWaystone waystone, int mouseX, int mouseY, int x, int y) {
        Font fontRenderer = Minecraft.getInstance().font;

        String locationPrefix = ChatFormatting.YELLOW + I18n.get("gui.waystones.waystone_selection.current_location") + " ";
        int locationPrefixWidth = fontRenderer.width(locationPrefix);

        String effectiveName = waystone.getName();
        if (effectiveName.isEmpty()) {
            effectiveName = I18n.get("gui.waystones.waystone_selection.unnamed_waystone");
        }
        int locationWidth = fontRenderer.width(effectiveName);

        int fullWidth = locationPrefixWidth + locationWidth;

        int startX = x - fullWidth / 2 + locationPrefixWidth;
        int startY = y + topPos;
        isLocationHeaderHovered = mouseX >= startX && mouseX < startX + locationWidth + 16
                && mouseY >= startY && mouseY < startY + fontRenderer.lineHeight;

        Player player = Minecraft.getInstance().player;
        WaystoneEditPermissions waystoneEditPermissions = PlayerWaystoneManager.mayEditWaystone(player, player.level, waystone);

        String fullText = locationPrefix + ChatFormatting.WHITE;
        if (isLocationHeaderHovered && waystoneEditPermissions == WaystoneEditPermissions.ALLOW) {
            fullText += ChatFormatting.UNDERLINE;
        }
        fullText += effectiveName;

        drawString(matrixStack, fontRenderer, fullText, x - fullWidth / 2, y, 0xFFFFFF);

        if (isLocationHeaderHovered && waystoneEditPermissions == WaystoneEditPermissions.ALLOW) {
            PoseStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushPose();
            modelViewStack.translate(x + fullWidth / 2f + 4, y, 0f);
            float scale = 0.5f;
            modelViewStack.scale(scale, scale, scale);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(new ItemStack(Items.WRITABLE_BOOK), 0, 0);
            modelViewStack.popPose();
        }
    }

    protected boolean allowSorting() {
        return true;
    }

    protected boolean allowDeletion() {
        return true;
    }
}
