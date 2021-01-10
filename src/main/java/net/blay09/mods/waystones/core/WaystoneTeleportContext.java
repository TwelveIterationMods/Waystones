package net.blay09.mods.waystones.core;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class WaystoneTeleportContext {
    private ServerWorld targetWorld;
    private Direction direction;
    private List<MobEntity> leashedEntities;

    public ServerWorld getTargetWorld() {
        return targetWorld;
    }

    public void setTargetWorld(ServerWorld targetWorld) {
        this.targetWorld = targetWorld;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public List<MobEntity> getLeashedEntities() {
        return leashedEntities;
    }

    public void setLeashedEntities(List<MobEntity> leashedEntities) {
        this.leashedEntities = leashedEntities;
    }
}
