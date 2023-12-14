package net.blay09.mods.waystones.compat;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.display.WaypointGroup;
import journeymap.client.api.event.ClientEvent;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.WaystoneUpdateReceivedEvent;
import net.blay09.mods.waystones.api.WaystonesListReceivedEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.config.WaystonesConfigData;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ClientPlugin
public class JourneyMapIntegration implements IClientPlugin {

    private static final UUID WAYSTONE_GROUP_ID = UUID.fromString("005bdf11-2dbb-4a27-8aa4-0184e86fa33c");
    private static final UUID SHARESTONE_GROUP_ID = UUID.fromString("199e2989-df63-4ab4-bd5d-2fa24e72b4fc");

    private IClientAPI api;
    private boolean journeyMapReady;
    private final List<Runnable> scheduledJobsWhenReady = new ArrayList<>();

    private static JourneyMapIntegration instance;

    public JourneyMapIntegration() {
        instance = this;
        Balm.getEvents().onEvent(WaystonesListReceivedEvent.class, this::onWaystonesListReceived);
        Balm.getEvents().onEvent(WaystoneUpdateReceivedEvent.class, this::onWaystoneUpdateReceived);
    }

    @Override
    public void initialize(IClientAPI iClientAPI) {
        api = iClientAPI;

        // This fires after all waypoints have been loaded
        api.subscribe(Waystones.MOD_ID, EnumSet.of(ClientEvent.Type.MAPPING_STARTED));
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
        if (config.compatibility.preferJourneyMapIntegration && Balm.isModLoaded("jmi")) {
            return false;
        }

        return config.compatibility.displayWaystonesOnJourneyMap;
    }

    public void onWaystoneUpdateReceived(WaystoneUpdateReceivedEvent event) {
        if (shouldManageWaypoints() && isSupportedWaystoneType(event.getWaystone().getWaystoneType())) {
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

    private void updateAllWaypoints(ResourceLocation waystoneType, List<IWaystone> waystones) {
        final var idPrefix = waystoneType.getPath() + ":";
        final var stillExistingIds = new HashSet<String>();
        for (final var waystone : waystones) {
            stillExistingIds.add(getPrefixedWaystoneId(waystone));
            updateWaypoint(waystone);
        }

        final var waypoints = api.getWaypoints(Waystones.MOD_ID);
        for (final var waypoint : waypoints) {
            if (waypoint.getId().startsWith(idPrefix) && !stillExistingIds.contains(waypoint.getId())) {
                api.remove(waypoint);
            }
        }
    }

    private void updateWaypoint(IWaystone waystone) {
        try {
            final var prefixedId = getPrefixedWaystoneId(waystone);
            final var oldWaypoint = api.getWaypoint(Waystones.MOD_ID, prefixedId);
            final var waystoneName = waystone.hasName() ? waystone.getName() : I18n.get("waystones.map.untitled_waystone");
            Waypoint waypoint = new Waypoint(Waystones.MOD_ID, prefixedId, waystoneName, waystone.getDimension(), waystone.getPos());
            waypoint.setName(waystoneName);
            waypoint.setGroup(getWaystoneGroup(waystone));
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

    private static WaypointGroup getWaystoneGroup(IWaystone waystone) {
        if (WaystoneTypes.isSharestone(waystone.getWaystoneType())) {
            return new WaypointGroup(Waystones.MOD_ID, SHARESTONE_GROUP_ID.toString(), "Sharestones");
        } else {
            return new WaypointGroup(Waystones.MOD_ID, WAYSTONE_GROUP_ID.toString(), "Waystones");
        }
    }

    private String getPrefixedWaystoneId(IWaystone waystone) {
        return waystone.getWaystoneType().getPath() + ":" + waystone.getWaystoneUid().toString();
    }

}
