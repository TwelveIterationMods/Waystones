package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;


public class WaystoneSelectionScreen extends WaystoneSelectionScreenBase {

    public WaystoneSelectionScreen(WaystoneSelectionContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

}
