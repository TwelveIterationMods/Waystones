package net.blay09.mods.waystones.client.gui.widget;

import net.minecraft.util.text.ITextProperties;

import java.util.List;

public interface ITooltipProvider {
    boolean shouldShowTooltip();
    List<ITextProperties> getTooltip();
}
