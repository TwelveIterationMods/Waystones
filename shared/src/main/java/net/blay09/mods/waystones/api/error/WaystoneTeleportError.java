package net.blay09.mods.waystones.api.error;

import net.blay09.mods.waystones.api.Waystone;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class WaystoneTeleportError {

    private final String translationKey;

    public WaystoneTeleportError() {
        this.translationKey = null;
    }

    public WaystoneTeleportError(String translationKey) {
        this.translationKey = translationKey;
    }

    @Nullable
    public String getTranslationKey() {
        return translationKey;
    }

    public static class NotOnServer extends WaystoneTeleportError {
    }

    public static class InvalidDimension extends WaystoneTeleportError {
        private final ResourceKey<Level> dimension;

        public InvalidDimension(ResourceKey<Level> dimension) {
            this.dimension = dimension;
        }

        public ResourceKey<Level> getDimension() {
            return dimension;
        }
    }

    public static class InvalidWaystone extends WaystoneTeleportError {
        private final Waystone waystone;

        public InvalidWaystone(Waystone waystone) {
            this.waystone = waystone;
        }

        public Waystone getWaystone() {
            return waystone;
        }
    }

    public static class MissingWaystone extends WaystoneTeleportError {
        private final Waystone waystone;

        public MissingWaystone(Waystone waystone) {
            super("chat.waystones.waystone_missing");
            this.waystone = waystone;
        }

        public Waystone getWaystone() {
            return waystone;
        }
    }

    public static class CancelledByEvent extends WaystoneTeleportError {
    }

    public static class DimensionalWarpDenied extends WaystoneTeleportError {
        public DimensionalWarpDenied() {
            super("chat.waystones.cannot_dimension_warp");
        }
    }

    public static class LeashedWarpDenied extends WaystoneTeleportError {
        public LeashedWarpDenied() {
            super("chat.waystones.cannot_transport_leashed");
        }
    }

    public static class SpecificLeashedWarpDenied extends WaystoneTeleportError {
        private final Entity entity;

        public SpecificLeashedWarpDenied(Entity entity) {
            super("chat.waystones.cannot_transport_this_leashed");
            this.entity = entity;
        }

        public Entity getEntity() {
            return entity;
        }
    }

    public static class LeashedDimensionalWarpDenied extends WaystoneTeleportError {
        public LeashedDimensionalWarpDenied() {
            super("chat.waystones.cannot_transport_leashed_dimensional");
        }
    }

    public static class NotEnoughXp extends WaystoneTeleportError {
        public NotEnoughXp() {
            super("chat.waystones.not_enough_xp");
        }
    }
}
