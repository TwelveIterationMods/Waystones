package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Comparator;

public class AdminSelectionScreen extends WaystoneSelectionScreenBase {

    public AdminSelectionScreen(WaystoneSelectionMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected boolean allowSorting() {
        return false;
    }

    @Override
    protected boolean allowDeletion() {
        return false;
    }

    @Override
    public Comparator<IWaystone> getSorting() {
        return null;
    }
}
