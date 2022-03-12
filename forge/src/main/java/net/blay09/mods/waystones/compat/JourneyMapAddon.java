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

import java.util.*;

@ClientPlugin
public class JourneyMapAddon implements IClientPlugin {

    private static final UUID WAYSTONE_GROUP_ID = UUID.fromString("005bdf11-2dbb-4a27-8aa4-0184e86fa33c");

    private IClientAPI api;
    private boolean journeyMapReady;
    private final List<Runnable> scheduledJobsWhenReady = new ArrayList<>();

    @Override
    public void initialize(IClientAPI iClientAPI) {
        api = iClientAPI;

        // This fires after all waypoints have been loaded
        api.subscribe(Waystones.MOD_ID, EnumSet.of(ClientEvent.Type.MAPPING_STARTED));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getModId() {
        return Waystones.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent clientEvent) {
        if (clientEvent.type == ClientEvent.Type.MAPPING_STARTED) {
            journeyMapReady = true;

            for (Runnable scheduledJob : scheduledJobsWhenReady) {
                scheduledJob.run();
            }
            scheduledJobsWhenReady.clear();
        }
    }

    @SubscribeEvent
    public void onKnownWaystones(KnownWaystonesEvent event) {
        if (WaystonesConfig.getActive().compatibility.displayWaystonesOnJourneyMap) {
            runWhenJourneyMapIsReady(() -> updateAllWaypoints(event.getWaystones()));
        }
    }

    @SubscribeEvent
    public void onWaystoneUpdateReceived(WaystoneUpdateReceivedEvent event) {
        if (WaystonesConfig.getActive().compatibility.displayWaystonesOnJourneyMap) {
            runWhenJourneyMapIsReady(() -> updateWaypoint(event.getWaystone()));
        }
    }

    private void runWhenJourneyMapIsReady(Runnable runnable) {
        if (journeyMapReady) {
            runnable.run();
        } else {
            scheduledJobsWhenReady.add(runnable);
        }
    }

    private void updateAllWaypoints(List<IWaystone> waystones) {
        Set<String> stillExistingIds = new HashSet<>();
        for (IWaystone waystone : waystones) {
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
                } else if (oldWaypoint.getIcon().getColor() != -1) { // TODO just temporary for testing, since color field is always null right now
                    waypoint.setColor(oldWaypoint.getIcon().getColor());
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
