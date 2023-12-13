package net.blay09.mods.waystones.compat;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.world.level.Level;

public class WaystonesBlueMapPlugin {

    private BlueMapAPI api;

    private static class BlueMapLevel {
        private final MarkerSet waystoneMarkers = MarkerSet.builder()
                .label("Waystones")
                .build();

        private final MarkerSet sharestoneMarkers = MarkerSet.builder()
                .label("Sharestones")
                .build();

        private final Level level;

        private BlueMapLevel(Level level) {
            this.level = level;
        }

        public void setupMarkerSets(BlueMapAPI api) {
            api.getWorld(level).ifPresent(world -> {
                for (var map : world.getMaps()) {
                    map.getMarkerSets().put("waystones:waystones", waystoneMarkers);
                    map.getMarkerSets().put("waystones:sharestones", sharestoneMarkers);
                }
            });
        }

        public void setupMarkers() {
            waystoneMarkers.getMarkers().clear();
            sharestoneMarkers.getMarkers().clear();

            // TODO update markersets with current waystones for this level
        }
    }

    public WaystonesBlueMapPlugin() {
        BlueMapAPI.onEnable(api -> this.api = api);
        BlueMapAPI.onDisable(api -> this.api = null);
    }

    public void onWaystonesChanged() {
        // TODO group all waystones by level
        // TODO computeifabsent a bluemaplevel
        // TODO some method for refreshing bluemap level once api is enabled
        // TODO trigger refresh whenever waystones change
        // TODO call this refresh method from some event
    }

    public POIMarker createWaystoneMarker(IWaystone waystone) {
        return POIMarker.builder()
                .label(waystone.getName())
                .position((double) waystone.getPos().getX(), waystone.getPos().getY(), waystone.getPos().getZ())
                .maxDistance(1000)
                .build();
    }

}
