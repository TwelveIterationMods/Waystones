package net.blay09.mods.waystones.compat;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.display.WaypointGroup;
import journeymap.client.api.event.ClientEvent;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

@ClientPlugin
public class JourneyMapAddon implements IClientPlugin {

    private static final UUID WAYSTONE_GROUP_ID = UUID.fromString("005bdf11-2dbb-4a27-8aa4-0184e86fa33c");

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
    public void onKnownWaystones(KnownWaystonesEvent event) {
        if (!WaystonesConfig.CLIENT.displayWaystonesOnJourneyMap.get()) {
            return;
        }

        api.removeAll(Waystones.MOD_ID);

        for (IWaystone waystone : event.getWaystones()) {
            try {
                Waypoint waypoint = new Waypoint(Waystones.MOD_ID, waystone.getName(), waystone.getDimension(), waystone.getPos());
                waypoint.setEditable(false);
                waypoint.setPersistent(false);
                waypoint.setGroup(new WaypointGroup(Waystones.MOD_ID, WAYSTONE_GROUP_ID.toString(), "Waystones"));
                api.show(waypoint);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
