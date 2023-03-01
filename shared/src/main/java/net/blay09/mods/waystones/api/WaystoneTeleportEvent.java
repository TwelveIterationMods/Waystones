package net.blay09.mods.waystones.api;

import net.blay09.mods.balm.api.event.BalmEvent;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class WaystoneTeleportEvent extends BalmEvent {

    public static class Pre extends WaystoneTeleportEvent {
        private final IWaystoneTeleportContext context;
        private EventResult dimensionalTeleportResult = EventResult.DEFAULT;
        private EventResult consumeItemResult = EventResult.DEFAULT;

        public Pre(IWaystoneTeleportContext context) {
            this.context = context;
        }

        public IWaystoneTeleportContext getContext() {
            return context;
        }

        int getXpCost() {
            return context.getXpCost();
        }

        void setXpCost(int xpCost) {
            context.setXpCost(xpCost);
        }

        int getCooldown() {
            return context.getCooldown();
        }

        void setCooldown(int cooldown) {
            context.setCooldown(cooldown);
        }

        TeleportDestination getDestination() {
            return context.getDestination();
        }

        void setDestination(ServerLevel level, Vec3 location, Direction direction) {
            context.setDestination(new TeleportDestination(level, location, direction));
        }

        void addAdditionalEntity(Entity additionalEntity) {
            context.addAdditionalEntity(additionalEntity);
        }

        public EventResult getDimensionalTeleportResult() {
            return dimensionalTeleportResult;
        }

        public void setDimensionalTeleportResult(EventResult dimensionalTeleportResult) {
            this.dimensionalTeleportResult = dimensionalTeleportResult;
        }

        public EventResult getConsumeItemResult() {
            return consumeItemResult;
        }

        public void setConsumeItemResult(EventResult consumeItemResult) {
            this.consumeItemResult = consumeItemResult;
        }
    }

    public static class Post extends WaystoneTeleportEvent {
        private final List<Entity> teleportedEntities;

        public Post(List<Entity> teleportedEntities) {
            this.teleportedEntities = teleportedEntities;
        }

        public List<Entity> getTeleportedEntities() {
            return teleportedEntities;
        }
    }

}
