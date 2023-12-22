package net.blay09.mods.waystones.cost;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class CostRegistry {

    private static final Map<ResourceLocation, CostType<?>> costTypes = new HashMap<>();
    private static final Map<ResourceLocation, CostModifier<?, ?>> costModifiers = new HashMap<>();

    public record IntParameter(int value) {
    }

    public record FloatParameter(float value) {
    }

    public record IdParameter(ResourceLocation value) {
    }

    public record ScaledParameters(IdParameter id, FloatParameter scale) {
    }

    public record ConditionalParameters(IdParameter id, FloatParameter scale) {
    }

    public static void registerDefaults() {
        final var experiencePoints = new ExperiencePointsCostType();
        final var levels = new ExperienceLevelCostType();

        register(experiencePoints);
        register(levels);

        registerModifier("add_levels", levels, FloatParameter.class, (cost, context, parameters) -> {
            cost.setLevels((int) (cost.getLevels() + parameters.value));
            return cost;
        });
        registerModifier("conditional_add_levels", levels, ConditionalParameters.class, (cost, context, parameters) -> {
            if (context.matchesCondition(parameters.id.value)) {
                cost.setLevels((int) (cost.getLevels() + parameters.scale.value));
            }
            return cost;
        });
        registerModifier("multiply_levels", levels, FloatParameter.class, (cost, context, parameters) -> {
            cost.setLevels((int) (cost.getLevels() * parameters.value));
            return cost;
        });
        registerModifier("conditional_multiply_levels", levels, ConditionalParameters.class, (cost, context, parameters) -> {
            if (context.matchesCondition(parameters.id.value)) {
                cost.setLevels((int) (cost.getLevels() * parameters.scale.value));
            }
            return cost;
        });
        registerModifier("scaled_add_levels", levels, ScaledParameters.class, (cost, context, parameters) -> {
            final var sourceValue = context.getContextValue(parameters.id.value);
            cost.setLevels((int) (cost.getLevels() + sourceValue * parameters.scale.value));
            return cost;
        });
        registerModifier("multiply_levels", levels, FloatParameter.class, (cost, context, parameters) -> {
            cost.setLevels((int) (cost.getLevels() * parameters.value));
            return cost;
        });
        registerModifier("min_levels", levels, IntParameter.class, (cost, context, parameters) -> {
            cost.setLevels(Math.max(cost.getLevels(), parameters.value));
            return cost;
        });
        registerModifier("max_levels", levels, IntParameter.class, (cost, context, parameters) -> {
            cost.setLevels(Math.min(cost.getLevels(), parameters.value));
            return cost;
        });

        registerModifier("add_xp", experiencePoints, IntParameter.class, (cost, context, parameters) -> {
            cost.setPoints(cost.getPoints() + parameters.value);
            return cost;
        });
        registerModifier("conditional_add_xp", experiencePoints, ConditionalParameters.class, (cost, context, parameters) -> {
            if (context.matchesCondition(parameters.id.value)) {
                cost.setPoints((int) (cost.getPoints() + parameters.scale.value));
            }
            return cost;
        });
        registerModifier("multiply_xp", experiencePoints, FloatParameter.class, (cost, context, parameters) -> {
            cost.setPoints((int) (cost.getPoints() * parameters.value));
            return cost;
        });
        registerModifier("conditional_multiply_xp", experiencePoints, ConditionalParameters.class, (cost, context, parameters) -> {
            if (context.matchesCondition(parameters.id.value)) {
                cost.setPoints((int) (cost.getPoints() * parameters.scale.value));
            }
            return cost;
        });
        registerModifier("scaled_add_xp", experiencePoints, ScaledParameters.class, (cost, context, parameters) -> {
            final var sourceValue = context.getContextValue(parameters.id.value);
            cost.setPoints((int) (cost.getPoints() + sourceValue * parameters.scale.value));
            return cost;
        });
        registerModifier("min_xp", experiencePoints, IntParameter.class, (cost, context, parameters) -> {
            cost.setPoints(Math.max(cost.getPoints(), parameters.value));
            return cost;
        });
        registerModifier("max_xp", experiencePoints, IntParameter.class, (cost, context, parameters) -> {
            cost.setPoints(Math.min(cost.getPoints(), parameters.value));
            return cost;
        });
    }

    public static void register(CostType<?> costType) {
        costTypes.put(costType.getId(), costType);
    }

    public static void register(CostModifier<?, ?> costModifier) {
        costModifiers.put(costModifier.getId(), costModifier);
    }

    private static <T, P> void registerModifier(String name, CostType<T> costType, Class<P> parameterType, CostModifierFunction<T, P> function) {
        register(new CostModifier<T, P>() {
            @Override
            public ResourceLocation getId() {
                return new ResourceLocation("waystones", name);
            }

            @Override
            public ResourceLocation getCostType() {
                return costType.getId();
            }

            @Override
            public Class<P> getParameterType() {
                return parameterType;
            }

            @Override
            public T apply(T cost, CostContext context, P parameters) {
                return function.apply(cost, context, parameters);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> CostType<T> getCostType(ResourceLocation costType) {
        return (CostType<T>) costTypes.get(costType);
    }

    public static CostModifier<?, ?> getCostModifier(ResourceLocation costModifier) {
        return costModifiers.get(costModifier);
    }
}
