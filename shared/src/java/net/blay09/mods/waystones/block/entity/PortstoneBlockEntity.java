package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.api.block.entity.BalmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PortstoneBlockEntity extends BalmBlockEntity {

    public PortstoneBlockEntity(BlockPos worldPosition, BlockState state) {
        super(ModBlockEntities.portstone.get(), worldPosition, state);
    }

    @Override
    public AABB balmGetRenderBoundingBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 2, worldPosition.getZ() + 1);
    }

}
