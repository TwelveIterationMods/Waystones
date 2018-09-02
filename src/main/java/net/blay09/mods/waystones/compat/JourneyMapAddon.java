package net.blay09.mods.waystones.compat;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.event.ClientEvent;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.util.WaystoneActivatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        try {
            api.show(new Waypoint(Waystones.MOD_ID, event.getWaystoneName(), event.getDimension(), event.getPos()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
