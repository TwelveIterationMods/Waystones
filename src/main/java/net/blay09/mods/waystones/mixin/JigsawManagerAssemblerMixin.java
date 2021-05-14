package net.blay09.mods.waystones.mixin;

import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@Mixin(targets = "net.minecraft.world.gen.feature.jigsaw.JigsawManager$Assembler")
public class JigsawManagerAssemblerMixin {

    @Final
    @Shadow
    protected List<? super AbstractVillagePiece> structurePieces;

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
        } else if (forceWaystone || Math.random() <= structurePieces.size() / 200f) {
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

        System.out.println("No waystone yet, with " + structurePieces.size() + " pieces");
        return pattern.getShuffledPieces(rand);
    }

}
