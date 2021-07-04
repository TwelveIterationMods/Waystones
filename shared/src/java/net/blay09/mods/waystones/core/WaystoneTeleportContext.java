package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WaystoneTeleportContext {
    private ServerLevel targetWorld;
    private Direction direction;
    private List<Mob> leashedEntities;
    
    private IWaystone fromWaystone;

    public ServerLevel getTargetWorld() {
        return targetWorld;
    }

    public void setTargetWorld(ServerLevel targetWorld) {
        this.targetWorld = targetWorld;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public List<Mob> getLeashedEntities() {
        return leashedEntities;
    }

    public void setLeashedEntities(List<Mob> leashedEntities) {
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
