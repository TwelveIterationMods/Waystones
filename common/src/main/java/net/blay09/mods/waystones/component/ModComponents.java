package net.blay09.mods.waystones.component;

import com.mojang.serialization.Codec;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.component.BalmComponents;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

import java.util.UUID;

public class ModComponents {
    public static DeferredObject<DataComponentType<UUID>> waystone;
    public static DeferredObject<DataComponentType<UUID>> attunement;

    public static void initialize(BalmComponents components) {
        waystone = components.registerComponent(() -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).build(), ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "waystone"));
        attunement = components.registerComponent(() -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).build(), ResourceLocation.fromNamespaceAndPath(Waystones.MOD_ID, "attunement"));
    }
}
