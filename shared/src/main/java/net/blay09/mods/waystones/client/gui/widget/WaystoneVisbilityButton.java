package net.blay09.mods.waystones.client.gui.widget;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WaystoneVisbilityButton extends Button implements ITooltipProvider {

    private static final ResourceLocation WAYSTONE_GUI_TEXTURES = new ResourceLocation(Waystones.MOD_ID, "textures/gui/menu/waystone.png");

    private final List<WaystoneVisibility> options;
    private final boolean canEdit;
    private WaystoneVisibility visibility;

    public WaystoneVisbilityButton(int x, int y, WaystoneVisibility visibility, List<WaystoneVisibility> options, boolean canEdit) {
        super(x, y, 18, 18, Component.empty(), button -> {
        }, Button.DEFAULT_NARRATION);
        this.options = options;
        this.visibility = visibility;
        this.canEdit = canEdit;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        guiGraphics.blit(WAYSTONE_GUI_TEXTURES, getX(), getY(), 176 + (isHovered ? 18 : 0), 14, 18, 18);
        guiGraphics.blit(WAYSTONE_GUI_TEXTURES, getX(), getY(), visibility.getIconX(), visibility.getIconY(), 18, 18);
    }

    @Override
    public boolean shouldShowTooltip() {
        return isHovered;
    }

    @Override
    public List<Component> getTooltipComponents() {
        final var visibilityValueComponent = Component.translatable("tooltip.waystones.visibility." + visibility.name().toLowerCase(Locale.ROOT))
                .withStyle(ChatFormatting.WHITE);
        final var result = new ArrayList<Component>();
        result.add(Component.translatable("tooltip.waystones.visibility", visibilityValueComponent).withStyle(ChatFormatting.YELLOW));
        if (!canEdit) {
            result.add(Component.translatable("tooltip.waystones.edit_restricted", visibilityValueComponent).withStyle(ChatFormatting.RED));
        }
        return result;
    }

    public WaystoneVisibility getVisibility() {
        return visibility;
    }

    @Override
    public void onPress() {
        if (canEdit) {
            final var index = options.indexOf(visibility);
            visibility = options.get((index + 1) % options.size());
        }
    }
}
