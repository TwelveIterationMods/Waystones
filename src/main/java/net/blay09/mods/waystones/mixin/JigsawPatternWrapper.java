package net.blay09.mods.waystones.mixin;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class JigsawPatternWrapper extends JigsawPattern {
    private final JigsawPattern delegate;

    public JigsawPatternWrapper(JigsawPattern delegate) {
        super(delegate.getName(), delegate.getFallback(), delegate.rawTemplates);
        this.delegate = delegate;
    }

    @Override
    public List<JigsawPiece> getShuffledPieces(Random rand) {
        List<JigsawPiece> list = delegate.getShuffledPieces(rand);
        return list.subList(0, Math.max(1, list.size()));
    }

    @Override
    public int getNumberOfPieces() {
        return delegate.getNumberOfPieces();
    }

    @Override
    public ResourceLocation getFallback() {
        return delegate.getFallback();
    }

    @Override
    public int getMaxSize(TemplateManager templateManagerIn) {
        return delegate.getMaxSize(templateManagerIn);
    }

    @Override
    public JigsawPiece getRandomPiece(Random rand) {
        return delegate.getRandomPiece(rand);
    }

    @Override
    public ResourceLocation getName() {
        return delegate.getName();
    }
}
