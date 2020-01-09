package net.blay09.mods.waystones.client.gui.widget;

import java.util.List;

public interface ITooltipProvider {
    boolean shouldShowTooltip();
    List<String> getTooltip();
}
