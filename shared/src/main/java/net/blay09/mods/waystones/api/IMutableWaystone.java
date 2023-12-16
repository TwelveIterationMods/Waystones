package net.blay09.mods.waystones.api;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

public interface IMutableWaystone {
    void setName(String name);

    /**
     * @deprecated Use {@link #setVisibility(WaystoneVisibility)} instead.
     */
    @Deprecated(forRemoval = true)
    void setGlobal(boolean global);

    void setVisibility(WaystoneVisibility visibility);

    void setDimension(ResourceKey<Level> dimension);

    void setPos(BlockPos pos);

    void setOwnerUid(UUID ownerUid);
}
