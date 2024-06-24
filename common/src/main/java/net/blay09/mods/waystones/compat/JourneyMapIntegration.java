package net.blay09.mods.waystones.compat;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.JourneyMapPlugin;
import journeymap.api.v2.client.event.MappingEvent;
import journeymap.api.v2.common.event.ClientEventRegistry;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import journeymap.api.v2.common.waypoint.WaypointGroup;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.event.WaystoneRemoveReceivedEvent;
import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class JourneyMapIntegration implements IClientPlugin {

    private IClientAPI api;
    private boolean journeyMapReady;
    private final List<Runnable> scheduledJobsWhenReady = new ArrayList<>();

    private static JourneyMapIntegration instance;

    public JourneyMapIntegration() {
        instance = this;
        Balm.getEvents().onEvent(WaystonesListReceivedEvent.class, this::onWaystonesListReceived);
        Balm.getEvents().onEvent(WaystoneUpdateReceivedEvent.class, this::onWaystoneUpdateReceived);
        Balm.getEvents().onEvent(WaystoneRemoveReceivedEvent.class, this::onWaystoneRemoveReceived);
    }

    @Override
    public void initialize(IClientAPI iClientAPI) {
        api = iClientAPI;

        // This fires after all waypoints have been loaded
        ClientEventRegistry.MAPPING_EVENT.subscribe(Waystones.MOD_ID, this::onMappingEvent);
    }

    /**
     * This will be null if Journeymap is not loaded.
     */
    @Nullable
    public static JourneyMapIntegration getInstance() {
        return instance;
    }

    @Override
    public String getModId() {
        return Waystones.MOD_ID;
    }

    public void onMappingEvent(MappingEvent event) {
        if (event.getStage() == MappingEvent.Stage.MAPPING_STARTED) {
            journeyMapReady = true;

            for (Runnable scheduledJob : scheduledJobsWhenReady) {
                scheduledJob.run();
            }
            scheduledJobsWhenReady.clear();
        }
    }

    public void onWaystonesListReceived(WaystonesListReceivedEvent event) {
        if (shouldManageWaypoints() && isSupportedWaystoneType(event.getWaystoneType())) {
            runWhenJourneyMapIsReady(() -> updateAllWaypoints(event.getWaystoneType(), event.getWaystones()));
        }
    }

    private boolean isSupportedWaystoneType(ResourceLocation waystoneType) {
        return waystoneType.equals(WaystoneTypes.WAYSTONE) || WaystoneTypes.isSharestone(waystoneType);
    }

    private static boolean shouldManageWaypoints() {
        WaystonesConfigData config = WaystonesConfig.getActive();
        if (config.compatibility.preferJourneyMapIntegrationMod && Balm.isModLoaded("jmi")) {
            return false;
        }

        return config.compatibility.journeyMap;
    }

    public void onWaystoneUpdateReceived(WaystoneUpdateReceivedEvent event) {
        if (shouldManageWaypoints() && isSupportedWaystoneType(event.getWaystone().getWaystoneType())) {
            runWhenJourneyMapIsReady(() -> updateWaypoint(event.getWaystone()));
        }
    }

    public void onWaystoneRemoveReceived(WaystoneRemoveReceivedEvent event) {
        if (shouldManageWaypoints() && isSupportedWaystoneType(event.getWaystoneType())) {
            runWhenJourneyMapIsReady(() -> removeWaypoint(event.getWaystoneType(), event.getWaystoneId()));
        }
    }

    private void runWhenJourneyMapIsReady(Runnable runnable) {
        if (journeyMapReady) {
            runnable.run();
        } else {
            scheduledJobsWhenReady.add(runnable);
        }
    }

    private void updateAllWaypoints(ResourceLocation waystoneType, List<Waystone> waystones) {
        final var idPrefix = waystoneType.getPath() + ":";
        final var stillExistingIds = new HashSet<String>();
        for (final var waystone : waystones) {
            stillExistingIds.add(getPrefixedWaystoneId(waystone));
            updateWaypoint(waystone);
        }

        final var waypoints = api.getWaypoints(Waystones.MOD_ID);
        for (final var waypoint : waypoints) {
            if (waypoint.getId().startsWith(idPrefix) && !stillExistingIds.contains(waypoint.getId())) {
                api.removeWaypoint(Waystones.MOD_ID, waypoint);
            }
        }
    }

    private void removeWaypoint(ResourceLocation waystoneType, UUID waystoneId) {
        final var prefixedId = getPrefixedWaystoneId(waystoneType, waystoneId);
        final var waypoint = api.getWaypoint(Waystones.MOD_ID, prefixedId);
        if (waypoint != null) {
            api.removeWaypoint(Waystones.MOD_ID, waypoint);
        }
    }

    private void updateWaypoint(Waystone waystone) {
        try {
            final var prefixedId = getPrefixedWaystoneId(waystone);
            final var oldWaypoint = api.getWaypoint(Waystones.MOD_ID, prefixedId);
            final var waystoneName = waystone.hasName() ? waystone.getName() : Component.translatable("waystones.map.untitled_waystone");
            final var waypoint = WaypointFactory.createClientWaypoint(Waystones.MOD_ID, waystone.getPos(), waystoneName.getString(), waystone.getDimension(), false);
            if (oldWaypoint != null) {
                waypoint.setEnabled(oldWaypoint.isEnabled());
                waypoint.setColor(oldWaypoint.getColor());
                waypoint.setIconColor(oldWaypoint.getIconColor());
                api.removeWaypoint(Waystones.MOD_ID, oldWaypoint);
            }
            api.addWaypoint(Waystones.MOD_ID, waypoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static WaypointGroup getWaystoneGroup(Waystone waystone) {
        if (WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
            return WaypointFactory.createWaypointGroup(Waystones.MOD_ID, "sharestones");
        } else {
            return WaypointFactory.createWaypointGroup(Waystones.MOD_ID, "waystones");
        }
    }

    private String getPrefixedWaystoneId(Waystone waystone) {
        return getPrefixedWaystoneId(waystone.getWaystoneType(), waystone.getWaystoneUid());
    }

    private String getPrefixedWaystoneId(ResourceLocation waystoneType, UUID waystoneId) {
        return waystoneType.getPath() + ":" + waystoneId.toString();
    }
}
