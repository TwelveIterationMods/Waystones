package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.api.cost.Cost;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IWaystoneTeleportContext {
    Entity getEntity();

    IWaystone getTargetWaystone();

    TeleportDestination getDestination();

    IWaystoneTeleportContext setDestination(TeleportDestination destination);

    List<Mob> getLeashedEntities();

    List<Entity> getAdditionalEntities();

    IWaystoneTeleportContext addAdditionalEntity(Entity additionalEntity);

    Optional<IWaystone> getFromWaystone();

    IWaystoneTeleportContext setFromWaystone(@Nullable IWaystone fromWaystone);

    ItemStack getWarpItem();

    IWaystoneTeleportContext setWarpItem(ItemStack warpItem);

    boolean isDimensionalTeleport();

    Cost getExperienceCost();

    IWaystoneTeleportContext setExperienceCost(Cost cost);

    IWaystoneTeleportContext setCooldown(int cooldown);

    int getCooldown();

    boolean playsSound();

    IWaystoneTeleportContext setPlaysSound(boolean playsSound);

    boolean playsEffect();

    IWaystoneTeleportContext setPlaysEffect(boolean playsEffect);

    boolean consumesWarpItem();

    IWaystoneTeleportContext setConsumesWarpItem(boolean consumesWarpItem);

    Set<ResourceLocation> getFlags();

    IWaystoneTeleportContext addFlag(ResourceLocation flag);

    IWaystoneTeleportContext removeFlag(ResourceLocation flag);

    default IWaystoneTeleportContext addFlags(Set<ResourceLocation> flags) {
        for (ResourceLocation flag : flags) {
            addFlag(flag);
        }
        return this;
    }
}
