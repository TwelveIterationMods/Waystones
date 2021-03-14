package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class WaystoneTeleportContext {
    private ServerWorld targetWorld;
    private Direction direction;
    private List<MobEntity> leashedEntities;
    
    private IWaystone fromWaystone;

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

    @Nullable
    public IWaystone getFromWaystone() {
        return fromWaystone;
    }

    public void setFromWaystone(@Nullable IWaystone fromWaystone) {
        this.fromWaystone = fromWaystone;
    }
}
