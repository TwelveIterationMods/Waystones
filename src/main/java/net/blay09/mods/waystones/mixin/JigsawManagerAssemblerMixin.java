package net.blay09.mods.waystones.mixin;

import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@Mixin(targets = "net.minecraft.world.gen.feature.jigsaw.JigsawManager$Assembler")
public class JigsawManagerAssemblerMixin {

    @Final
    @Shadow
    protected List<? super AbstractVillagePiece> structurePieces;

    @Final
    @Shadow
    protected Registry<JigsawPattern> field_242839_a;

    /*@Inject(method = "func_236831_a_(Lnet/minecraft/world/gen/feature/structure/AbstractVillagePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IIZ)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z", shift = At.Shift.AFTER, ordinal = 1), require = 1, allow = 1)
    private void func_236831_a_(AbstractVillagePiece piece, MutableObject<VoxelShape> villageBounds, int boundsTop, int depth, boolean idk, CallbackInfo ci) {
        System.out.println("mixin works: " + villageBounds);
        System.out.println("pieces: " + structurePieces.size());
    }*/

    /*@Redirect(method = "func_236831_a_(Lnet/minecraft/world/gen/feature/structure/AbstractVillagePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IIZ)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/registry/Registry;getOptional(Lnet/minecraft/util/ResourceLocation;)Ljava/util/Optional;"))
    private Optional<JigsawPattern> getOptional(Registry<JigsawPattern> registry, ResourceLocation location) {
        if (registry == field_242839_a) {
            System.out.println("oof " + location);
            return Optional.empty();
        }

        return registry.getOptional(location);
    }*/

    @Redirect(method = "func_236831_a_(Lnet/minecraft/world/gen/feature/structure/AbstractVillagePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IIZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/jigsaw/JigsawPattern;getShuffledPieces(Ljava/util/Random;)Ljava/util/List;"))
    private List<JigsawPiece> getShuffledPieces(JigsawPattern pattern, Random rand) {
        boolean hasWaystone = structurePieces.stream()
                .map(piece -> ((AbstractVillagePiece) piece).getJigsawPiece().toString())
                .anyMatch(pieceName -> pieceName.contains("waystones:") && pieceName.contains("/waystone"));
        boolean forceWaystone = WaystonesConfig.COMMON.forceSpawnInVillages.get();
        if (hasWaystone) {
            return pattern.getShuffledPieces(rand).stream().filter(piece -> {
                String pieceName = piece.toString();
                return !pieceName.contains("waystones:") || !pieceName.contains("/waystone");
            }).collect(Collectors.toList());
        } else if (forceWaystone) {
            JigsawPiece waystonePiece = null;
            List<JigsawPiece> original = pattern.getShuffledPieces(rand);
            List<JigsawPiece> result = new ArrayList<>();
            for (JigsawPiece piece : original) {
                String pieceName = piece.toString();
                if (pieceName.contains("waystones:") && pieceName.contains("/waystone")) {
                    waystonePiece = piece;
                } else {
                    result.add(piece);
                }
            }
            if (waystonePiece != null) {
                result.add(0, waystonePiece);
            }
            return result;
        }

        return pattern.getShuffledPieces(rand);
    }

}
