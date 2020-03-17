package net.blay09.mods.waystones.compat;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.event.ClientEvent;
import net.blay09.mods.waystones.config.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@ClientPlugin
public class JourneyMapAddon implements IClientPlugin {

    private IClientAPI api;

    @Override
    public void initialize(IClientAPI iClientAPI) {
        api = iClientAPI;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getModId() {
        return Waystones.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent clientEvent) {
    }

    @SubscribeEvent
    public void onWaystoneActivated(WaystoneActivatedEvent event) {
        if (!WaystoneConfig.CLIENT.createJourneyMapWaypoint.get()) {
            return;
        }

        try {
            api.show(new Waypoint(Waystones.MOD_ID, event.getWaystone().getName(), event.getWaystone().getDimensionType().getId(), event.getWaystone().getPos()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
