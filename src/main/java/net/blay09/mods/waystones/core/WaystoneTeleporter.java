package net.blay09.mods.waystones.core;

import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.function.Function;

public class WaystoneTeleporter implements ITeleporter {

    private final Vector3d targetPos;

    public WaystoneTeleporter(Vector3d targetPos) {
        this.targetPos = targetPos;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld current, ServerWorld dest, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(false);
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld dest, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        return new PortalInfo(this.targetPos, Vector3d.ZERO, entity.rotationYaw, entity.rotationPitch);
    }

}
