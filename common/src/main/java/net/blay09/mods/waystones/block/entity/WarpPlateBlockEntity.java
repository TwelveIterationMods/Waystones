package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.*;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.error.WaystoneTeleportError;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.client.gui.screen.WaystoneEditScreen;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.menu.WaystoneEditMenu;
import net.blay09.mods.waystones.menu.WaystoneModifierMenu;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.blay09.mods.waystones.worldgen.namegen.NameGeneratorManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;


public class WarpPlateBlockEntity extends WaystoneBlockEntityBase implements Nameable {

    private final WeakHashMap<Entity, Integer> ticksPassedPerEntity = new WeakHashMap<>();

    private final Random random = new Random();

    private int lastAttunementSlot;

    protected int attunementTicks;
    private Component customName;

    public WarpPlateBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.warpPlate.get(), blockPos, blockState);
    }

    @Override
    public void initializeWaystone(ServerLevelAccessor world, @Nullable LivingEntity player, WaystoneOrigin origin) {
        super.initializeWaystone(world, player, origin);

        // Warp Plates generate a name on placement always
        final var waystone = getWaystone();
        if (waystone instanceof MutableWaystone) {
            final var name = NameGeneratorManager.get(world.getServer()).getName(world, waystone, world.getRandom(), NameGenerationMode.RANDOM_ONLY);
            ((MutableWaystone) waystone).setName(name);
        }

        WaystoneSyncManager.sendWaystoneUpdateToAll(world.getServer(), waystone);
    }

    @Override
    protected ResourceLocation getWaystoneType() {
        return WaystoneTypes.WARP_PLATE;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        if (customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(customName, provider));
        }

        tag.putInt("LastAttunementSlot", lastAttunementSlot);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);

        if (compound.contains("CustomName")) {
            customName = Component.Serializer.fromJson(compound.getString("CustomName"), provider);
        }

        lastAttunementSlot = compound.getInt("LastAttunementSlot");
    }

    @Override
    public Optional<MenuProvider> getSettingsMenuProvider() {
        return Optional.of(new BalmMenuProvider<WaystoneEditMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return WarpPlateBlockEntity.this.getDisplayName();
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
                final var error = WaystonePermissionManager.mayEditWaystone(player, player.level(), getWaystone());
                return new WaystoneEditMenu(i, getWaystone(), WarpPlateBlockEntity.this, playerInventory, error.isEmpty());
            }

            @Override
            public WaystoneEditMenu.Data getScreenOpeningData(ServerPlayer serverPlayer) {
                final var error = WaystonePermissionManager.mayEditWaystone(serverPlayer, serverPlayer.level(), getWaystone());
                return new WaystoneEditMenu.Data(worldPosition, getWaystone(), error.isEmpty());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, WaystoneEditMenu.Data> getScreenStreamCodec() {
                return WaystoneEditMenu.STREAM_CODEC;
            }
        });
    }

    public boolean hasPotentialWarpTarget() {
        final var shardItem = getShardItem();
        return !shardItem.isEmpty() && !shardItem.is(ModItems.deepslateShard);
    }

    public void onEntityCollision(Entity entity) {
        if (WaystonePermissionManager.isEntityDeniedTeleports(entity)) {
            return;
        }

        final var ticksPassed = ticksPassedPerEntity.putIfAbsent(entity, 0);
        if ((ticksPassed == null || ticksPassed != -1) && hasPotentialWarpTarget()) {
            final var targetWaystone = getTargetWaystone().orElse(InvalidWaystone.INSTANCE);
            final var status = targetWaystone.isValid() ? WarpPlateBlock.WarpPlateStatus.WARPING : WarpPlateBlock.WarpPlateStatus.WARPING_INVALID;
            final var canAfford = WaystonesAPI.createDefaultTeleportContext(entity, targetWaystone, it -> it.setFromWaystone(getWaystone()))
                    .mapLeft(WaystoneTeleportContext::getRequirements)
                    .mapLeft(it -> !(entity instanceof Player player) || player.getAbilities().instabuild || it.canAfford(player))
                    .left().orElse(true);
            level.setBlock(worldPosition, getBlockState()
                    .setValue(WarpPlateBlock.STATUS, canAfford ? status : WarpPlateBlock.WarpPlateStatus.WARPING_INVALID), 3);
        }
    }

    private boolean isEntityOnWarpPlate(Entity entity) {
        return entity.getX() >= worldPosition.getX() && entity.getX() < worldPosition.getX() + 1
                && entity.getY() >= worldPosition.getY() && entity.getY() < worldPosition.getY() + 1
                && entity.getZ() >= worldPosition.getZ() && entity.getZ() < worldPosition.getZ() + 1;
    }

    public BlockState getIdleState() {
        final var shardItem = getShardItem();
        if (shardItem.isEmpty()) {
            return getBlockState().setValue(WarpPlateBlock.STATUS, WarpPlateBlock.WarpPlateStatus.EMPTY);
        } else if (shardItem.is(ModItems.deepslateShard)) {
            return getBlockState().setValue(WarpPlateBlock.STATUS, WarpPlateBlock.WarpPlateStatus.LOCKED);
        } else if (shardItem.is(ModItems.dormantShard)) {
            return getBlockState().setValue(WarpPlateBlock.STATUS, WarpPlateBlock.WarpPlateStatus.ATTUNING);
        }
        return getBlockState().setValue(WarpPlateBlock.STATUS, WarpPlateBlock.WarpPlateStatus.IDLE);
    }

    public void serverTick() {
        attuneShard();

        final var status = getBlockState().getValue(WarpPlateBlock.STATUS);
        if (status == WarpPlateBlock.WarpPlateStatus.WARPING || status == WarpPlateBlock.WarpPlateStatus.WARPING_INVALID) {
            AABB boundsAbove = new AABB(worldPosition.getX(),
                    worldPosition.getY(),
                    worldPosition.getZ(),
                    worldPosition.getX() + 1,
                    worldPosition.getY() + 1,
                    worldPosition.getZ() + 1);
            List<Entity> entities = level.getEntities((Entity) null, boundsAbove, EntitySelector.ENTITY_STILL_ALIVE);
            if (entities.isEmpty()) {
                level.setBlock(worldPosition, getIdleState(), 3);
                ticksPassedPerEntity.clear();
            }
        }

        if (hasPotentialWarpTarget()) {
            final var useTime = getWarpPlateUseTime();
            Iterator<Map.Entry<Entity, Integer>> iterator = ticksPassedPerEntity.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Entity, Integer> entry = iterator.next();
                Entity entity = entry.getKey();
                Integer ticksPassed = entry.getValue();
                if (!entity.isAlive() || !isEntityOnWarpPlate(entity)) {
                    iterator.remove();
                } else if (ticksPassed > useTime) {
                    ItemStack targetAttunementStack = getTargetAttunementStack();
                    Waystone targetWaystone = WaystonesAPI.getBoundWaystone(null, targetAttunementStack).orElse(null);
                    if (targetWaystone != null && targetWaystone.isValid()) {
                        teleportToTarget(entity, targetWaystone, targetAttunementStack);
                    }

                    if (entity instanceof Player) {
                        if (targetWaystone == null) {
                            var chatComponent = Component.translatable("chat.waystones.warp_plate_has_no_target");
                            chatComponent.withStyle(ChatFormatting.DARK_RED);
                            ((Player) entity).displayClientMessage(chatComponent, true);
                        } else if (!targetWaystone.isValid()) {
                            var chatComponent = Component.translatable("chat.waystones.warp_plate_has_invalid_target");
                            chatComponent.withStyle(ChatFormatting.DARK_RED);
                            ((Player) entity).displayClientMessage(chatComponent, true);
                        }
                    }

                    iterator.remove();
                } else if (ticksPassed != -1) {
                    entry.setValue(ticksPassed + 1);
                }
            }
        }
    }

    private int getWarpPlateUseTime() {
        float useTimeMultiplier = 1;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.getItem() == Items.AMETHYST_SHARD) {
                useTimeMultiplier -= 0.016f * itemStack.getCount();
            } else if (itemStack.getItem() == Items.SLIME_BALL) {
                useTimeMultiplier += 0.016f * itemStack.getCount();
            }
        }

        int configuredUseTime = WaystonesConfig.getActive().general.warpPlateUseTime;
        return Mth.clamp((int) (configuredUseTime * useTimeMultiplier), 1, configuredUseTime * 2);
    }

    private void teleportToTarget(Entity entity, Waystone targetWaystone, ItemStack targetAttunementStack) {
        WaystonesAPI.createDefaultTeleportContext(entity, targetWaystone, it -> {
                    it.setFromWaystone(getWaystone());
                    it.setWarpItem(targetAttunementStack);
                })
                .flatMap(WaystonesAPI::tryTeleport)
                .ifRight(informRejectedTeleport(entity))
                .ifLeft(entities -> {
                    if (targetAttunementStack.is(ModItemTags.SINGLE_USE_WARP_SHARDS)) {
                        if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
                            targetAttunementStack.shrink(1);
                        }
                    }
                })
                .ifLeft(entities -> entities.forEach(this::applyWarpPlateEffects))
                .left();
    }

    private Consumer<WaystoneTeleportError> informRejectedTeleport(final Entity entityToInform) {
        return error -> {
            if (error.getComponent() != null && entityToInform instanceof Player player) {
                var chatComponent = error.getComponent().copy().withStyle(ChatFormatting.DARK_RED);
                player.displayClientMessage(chatComponent, true);
            }
        };
    }

    private void applyWarpPlateEffects(Entity entity) {
        int fireSeconds = 0;
        int poisonSeconds = 0;
        int blindSeconds = 0;
        int featherFallSeconds = 0;
        int fireResistanceSeconds = 0;
        int witherSeconds = 0;
        int potency = 1;
        List<ItemStack> curativeItems = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.getItem() == Items.BLAZE_POWDER) {
                fireSeconds += itemStack.getCount();
            } else if (itemStack.getItem() == Items.POISONOUS_POTATO) {
                poisonSeconds += itemStack.getCount();
            } else if (itemStack.getItem() == Items.INK_SAC) {
                blindSeconds += itemStack.getCount();
            } else if (itemStack.getItem() == Items.MILK_BUCKET || itemStack.getItem() == Items.HONEY_BLOCK) {
                curativeItems.add(itemStack);
            } else if (itemStack.getItem() == Items.DIAMOND) {
                potency = Math.min(4, potency + itemStack.getCount());
            } else if (itemStack.getItem() == Items.FEATHER) {
                featherFallSeconds = Math.min(8, featherFallSeconds + itemStack.getCount());
            } else if (itemStack.getItem() == Items.MAGMA_CREAM) {
                fireResistanceSeconds = Math.min(8, fireResistanceSeconds + itemStack.getCount());
            } else if (itemStack.getItem() == Items.WITHER_ROSE) {
                witherSeconds += itemStack.getCount();
            }
        }

        if (entity instanceof LivingEntity) {
            if (fireSeconds > 0) {
                entity.setRemainingFireTicks(fireSeconds * 20);
            }
            if (poisonSeconds > 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, poisonSeconds * 20, potency));
            }
            if (blindSeconds > 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, blindSeconds * 20, potency));
            }
            if (featherFallSeconds > 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, featherFallSeconds * 20, potency));
            }
            if (fireResistanceSeconds > 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, fireResistanceSeconds * 20, potency));
            }
            if (witherSeconds > 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.WITHER, witherSeconds * 20, potency));
            }
            for (ItemStack curativeItem : curativeItems) {
                Balm.getHooks().curePotionEffects((LivingEntity) entity, curativeItem);
            }
        }
    }

    public ItemStack getTargetAttunementStack() {
        boolean shouldRoundRobin = false;
        boolean shouldPrioritizeSingleUseShards = false;
        List<ItemStack> attunedShards = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.is(ModItemTags.WARP_SHARDS)) {
                Waystone waystoneAttunedTo = WaystonesAPI.getBoundWaystone(null, itemStack).orElse(null);
                if (waystoneAttunedTo != null && !waystoneAttunedTo.getWaystoneUid().equals(getWaystone().getWaystoneUid())) {
                    attunedShards.add(itemStack);
                }
            } else if (itemStack.getItem() == Items.QUARTZ) {
                shouldRoundRobin = true;
            } else if (itemStack.getItem() == Items.SPIDER_EYE) {
                shouldPrioritizeSingleUseShards = true;
            }
        }
        if (shouldPrioritizeSingleUseShards && attunedShards.stream().anyMatch(stack -> stack.is(ModItemTags.SINGLE_USE_WARP_SHARDS))) {
            attunedShards.removeIf(stack -> !stack.is(ModItemTags.SINGLE_USE_WARP_SHARDS));
        }

        if (!attunedShards.isEmpty()) {
            lastAttunementSlot = (lastAttunementSlot + 1) % attunedShards.size();
            return shouldRoundRobin ? attunedShards.get(lastAttunementSlot) : attunedShards.get(random.nextInt(attunedShards.size()));
        }

        return ItemStack.EMPTY;
    }

    public Optional<Waystone> getTargetWaystone() {
        return WaystonesAPI.getBoundWaystone(null, getTargetAttunementStack());
    }

    public void markEntityForCooldown(Entity entity) {
        ticksPassedPerEntity.put(entity, -1);
    }

    @Override
    public Component getDisplayName() {
        return hasCustomName() ? getCustomName() : getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return customName;
    }

    public void setCustomName(@Nullable Component customName) {
        this.customName = customName;
    }

    @Override
    public Component getName() {
        return Component.translatable("container.waystones.warp_plate");
    }

    public void setShardItem(ItemStack itemStack) {
        container.setItem(0, itemStack);
        if (level != null) {
            level.setBlock(worldPosition, getIdleState(), 3);
        }
        setChanged();
    }

    public ItemStack getShardItem() {
        return container.getItem(0);
    }

    public void attuneShard() {
        final var shardItem = getShardItem();
        if (shardItem.is(ModItems.dormantShard)) {
            attunementTicks++;

            if (attunementTicks >= getMaxAttunementTicks()) {
                attunementTicks = 0;
                final var attunedShard = new ItemStack(ModItems.attunedShard);
                WaystonesAPI.setBoundWaystone(attunedShard, getWaystone());
                setShardItem(attunedShard);
            }
        } else {
            attunementTicks = 0;
        }
    }

    public int getMaxAttunementTicks() {
        return 30;
    }
}
