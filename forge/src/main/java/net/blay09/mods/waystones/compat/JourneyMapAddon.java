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
import net.blay09.mods.waystones.api.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        if (!WaystonesConfig.getActive().compatibility.displayWaystonesOnJourneyMap) {
            return;
        }

        Set<String> stillExistingIds = new HashSet<>();
        for (IWaystone waystone : event.getWaystones()) {
            stillExistingIds.add(waystone.getWaystoneUid().toString());
            updateWaypoint(waystone);
        }

        List<Waypoint> waypoints = api.getWaypoints(Waystones.MOD_ID);
        for (Waypoint waypoint : waypoints) {
            if (!stillExistingIds.contains(waypoint.getId())) {
                api.remove(waypoint);
            }
        }
    }

    @SubscribeEvent
    public void onWaystoneUpdateReceived(WaystoneUpdateReceivedEvent event) {
        if (!WaystonesConfig.getActive().compatibility.displayWaystonesOnJourneyMap) {
            return;
        }

        updateWaypoint(event.getWaystone());
    }

    private void updateWaypoint(IWaystone waystone) {
        try {
            String waystoneName = waystone.hasName() ? waystone.getName() : I18n.get("waystones.map.untitled_waystone");
            Waypoint oldWaypoint = api.getWaypoint(Waystones.MOD_ID, waystone.getWaystoneUid().toString());
            Waypoint waypoint = new Waypoint(Waystones.MOD_ID, waystone.getWaystoneUid().toString(), waystoneName, waystone.getDimension(), waystone.getPos());
            waypoint.setName(waystoneName);
            waypoint.setGroup(new WaypointGroup(Waystones.MOD_ID, WAYSTONE_GROUP_ID.toString(), "Waystones"));
            if (oldWaypoint != null) {
                waypoint.setEnabled(oldWaypoint.isEnabled());
                if (oldWaypoint.hasColor()) {
                    waypoint.setColor(oldWaypoint.getColor());
                }
                if (oldWaypoint.hasBackgroundColor()) {
                    waypoint.setBackgroundColor(oldWaypoint.getBackgroundColor());
                }
                api.remove(oldWaypoint);
            }
            api.show(waypoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
