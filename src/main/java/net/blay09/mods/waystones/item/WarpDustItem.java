package net.blay09.mods.waystones.item;

import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.container.WaystoneSelectionContainer;
import net.blay09.mods.waystones.core.WarpMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class WarpDustItem extends Item {

    public static final String name = "warp_dust";
    public static final ResourceLocation registryName = new ResourceLocation(Waystones.MOD_ID, name);

    public WarpDustItem() {
        super(new Properties().group(Waystones.itemGroup));
    }

    @Override
    public boolean hasEffect(ItemStack itemStack) {
        return false;
    }

}
