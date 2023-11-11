package net.blay09.mods.waystones.api;

import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface IWaystoneTeleportContext {
    Entity getEntity();

    IWaystone getTargetWaystone();

    TeleportDestination getDestination();

    void setDestination(TeleportDestination destination);

    List<Mob> getLeashedEntities();

    List<Entity> getAdditionalEntities();

    void addAdditionalEntity(Entity additionalEntity);

    @Nullable IWaystone getFromWaystone();

    void setFromWaystone(@Nullable IWaystone fromWaystone);

    ItemStack getWarpItem();

    void setWarpItem(ItemStack warpItem);

    boolean isDimensionalTeleport();

    int getXpCost();

    void setXpCost(int xpCost);

    void setCooldown(int cooldown);

    int getCooldown();

    WarpMode getWarpMode();

    void setWarpMode(WarpMode warpMode);

    boolean playsSound();

    void setPlaysSound(boolean playsSound);

    boolean playsEffect();

    void setPlaysEffect(boolean playsEffect);

    default Predicate<? super ItemStack> getConsumeItemPredicate() {
        return stack -> getWarpMode().consumesItem();
    };

    default void setConsumeItemPredicate(Predicate<? super ItemStack> predicate) { }

    default BiPredicate<? super Entity, ? super IWaystone> getAllowTeleportPredicate() {
        return getWarpMode().getAllowTeleportPredicate();
    }

    default void setAllowTeleportPredicate(BiPredicate<? super Entity, ? super IWaystone> allowTeleportPredicate) { }
}
