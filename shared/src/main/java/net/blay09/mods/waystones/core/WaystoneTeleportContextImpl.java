package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.cost.Cost;
import net.blay09.mods.waystones.cost.NoCost;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WaystoneTeleportContextImpl implements WaystoneTeleportContext {
    private final Entity entity;
    private final Waystone targetWaystone;

    private final List<Entity> additionalEntities = new ArrayList<>();
    private final List<Mob> leashedEntities = new ArrayList<>();
    private final Set<ResourceLocation> flags = new HashSet<>();

    private TeleportDestination destination;
    private Waystone fromWaystone;

    private ItemStack warpItem = ItemStack.EMPTY;

    private Cost cost = NoCost.INSTANCE;

    private boolean playsSound = true;
    private boolean playsEffect = true;

    public WaystoneTeleportContextImpl(Entity entity, Waystone targetWaystone, TeleportDestination destination) {
        this.entity = entity;
        this.targetWaystone = targetWaystone;
        this.destination = destination;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public Waystone getTargetWaystone() {
        return targetWaystone;
    }

    @Override
    public TeleportDestination getDestination() {
        return destination;
    }

    @Override
    public WaystoneTeleportContext setDestination(TeleportDestination destination) {
        this.destination = destination;
        return this;
    }

    @Override
    public List<Mob> getLeashedEntities() {
        return leashedEntities;
    }

    @Override
    public List<Entity> getAdditionalEntities() {
        return additionalEntities;
    }

    @Override
    public WaystoneTeleportContext addAdditionalEntity(Entity additionalEntity) {
        this.additionalEntities.add(additionalEntity);
        return this;
    }

    @Override
    @Nullable
    public Optional<Waystone> getFromWaystone() {
        return Optional.ofNullable(fromWaystone);
    }

    @Override
    public WaystoneTeleportContext setFromWaystone(@Nullable Waystone fromWaystone) {
        this.fromWaystone = fromWaystone;
        return this;
    }

    @Override
    public ItemStack getWarpItem() {
        return warpItem;
    }

    @Override
    public WaystoneTeleportContext setWarpItem(ItemStack warpItem) {
        this.warpItem = warpItem;
        return this;
    }

    @Override
    public boolean isDimensionalTeleport() {
        return targetWaystone.getDimension() != entity.level().dimension();
    }

    @Override
    public Cost getCost() {
        return cost;
    }

    @Override
    public WaystoneTeleportContext setCost(Cost cost) {
        this.cost = cost;
        return this;
    }

    @Override
    public boolean playsSound() {
        return playsSound;
    }

    @Override
    public WaystoneTeleportContext setPlaysSound(boolean playsSound) {
        this.playsSound = playsSound;
        return this;
    }

    @Override
    public boolean playsEffect() {
        return playsEffect;
    }

    @Override
    public WaystoneTeleportContext setPlaysEffect(boolean playsEffect) {
        this.playsEffect = playsEffect;
        return this;
    }

    @Override
    public Set<ResourceLocation> getFlags() {
        return flags;
    }

    @Override
    public WaystoneTeleportContext addFlag(ResourceLocation flag) {
        flags.add(flag);
        return this;
    }

    @Override
    public WaystoneTeleportContext removeFlag(ResourceLocation flag) {
        flags.remove(flag);
        return this;
    }
}
