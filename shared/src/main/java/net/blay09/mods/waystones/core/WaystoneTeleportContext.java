package net.blay09.mods.waystones.core;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.IWaystoneTeleportContext;
import net.blay09.mods.waystones.api.TeleportDestination;
import net.blay09.mods.waystones.api.cost.Cost;
import net.blay09.mods.waystones.cost.NoCost;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WaystoneTeleportContext implements IWaystoneTeleportContext {
    private final Entity entity;
    private final IWaystone targetWaystone;

    private final List<Entity> additionalEntities = new ArrayList<>();
    private final List<Mob> leashedEntities = new ArrayList<>();
    private final Set<ResourceLocation> flags = new HashSet<>();

    private TeleportDestination destination;
    private IWaystone fromWaystone;

    private ItemStack warpItem = ItemStack.EMPTY;
    private boolean consumesWarpItem;

    private Cost xpCost = NoCost.INSTANCE;

    private int cooldown;
    private boolean playsSound = true;
    private boolean playsEffect = true;

    public WaystoneTeleportContext(Entity entity, IWaystone targetWaystone, TeleportDestination destination) {
        this.entity = entity;
        this.targetWaystone = targetWaystone;
        this.destination = destination;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public IWaystone getTargetWaystone() {
        return targetWaystone;
    }

    @Override
    public TeleportDestination getDestination() {
        return destination;
    }

    @Override
    public IWaystoneTeleportContext setDestination(TeleportDestination destination) {
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
    public IWaystoneTeleportContext addAdditionalEntity(Entity additionalEntity) {
        this.additionalEntities.add(additionalEntity);
        return this;
    }

    @Override
    @Nullable
    public Optional<IWaystone> getFromWaystone() {
        return Optional.ofNullable(fromWaystone);
    }

    @Override
    public IWaystoneTeleportContext setFromWaystone(@Nullable IWaystone fromWaystone) {
        this.fromWaystone = fromWaystone;
        return this;
    }

    @Override
    public ItemStack getWarpItem() {
        return warpItem;
    }

    @Override
    public IWaystoneTeleportContext setWarpItem(ItemStack warpItem) {
        this.warpItem = warpItem;
        return this;
    }

    @Override
    public boolean isDimensionalTeleport() {
        return targetWaystone.getDimension() != entity.level().dimension();
    }

    @Override
    public Cost getExperienceCost() {
        return xpCost;
    }

    @Override
    public IWaystoneTeleportContext setExperienceCost(Cost cost) {
        this.xpCost = cost;
        return this;
    }

    @Override
    public IWaystoneTeleportContext setCooldown(int cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public boolean playsSound() {
        return playsSound;
    }

    @Override
    public IWaystoneTeleportContext setPlaysSound(boolean playsSound) {
        this.playsSound = playsSound;
        return this;
    }

    @Override
    public boolean playsEffect() {
        return playsEffect;
    }

    @Override
    public IWaystoneTeleportContext setPlaysEffect(boolean playsEffect) {
        this.playsEffect = playsEffect;
        return this;
    }

    @Override
    public boolean consumesWarpItem() {
        return this.consumesWarpItem;
    }

    @Override
    public IWaystoneTeleportContext setConsumesWarpItem(boolean consumesWarpItem) {
        this.consumesWarpItem = consumesWarpItem;
        return this;
    }

    @Override
    public Set<ResourceLocation> getFlags() {
        return flags;
    }

    @Override
    public IWaystoneTeleportContext addFlag(ResourceLocation flag) {
        flags.add(flag);
        return this;
    }

    @Override
    public IWaystoneTeleportContext removeFlag(ResourceLocation flag) {
        flags.remove(flag);
        return this;
    }
}
