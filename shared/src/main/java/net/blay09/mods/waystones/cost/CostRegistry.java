package net.blay09.mods.waystones.cost;

import net.blay09.mods.waystones.api.cost.*;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CostRegistry {

    private static final Map<ResourceLocation, CostType<?>> costTypes = new HashMap<>();
    private static final Map<ResourceLocation, CostModifier<?, ?>> costModifiers = new HashMap<>();
    private static final Map<Class<?>, CostParameterSerializer<?>> costParameterSerializers = new HashMap<>();

    public record IntParameter(int value) {
    }

    public record FloatParameter(float value) {
    }

    public record IdParameter(ResourceLocation value) {
    }

    public record VariableScaledParameter(IdParameter id, FloatParameter scale) {
    }

    public record ConditionalFloatParameter(IdParameter id, FloatParameter scale) {
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
        registerModifier("conditional_add_levels", levels, ConditionalFloatParameter.class, (cost, context, parameters) -> {
            if (context.matchesCondition(parameters.id.value)) {
                cost.setLevels((int) (cost.getLevels() + parameters.scale.value));
            }
            return cost;
        });
        registerModifier("multiply_levels", levels, FloatParameter.class, (cost, context, parameters) -> {
            cost.setLevels((int) (cost.getLevels() * parameters.value));
            return cost;
        });
        registerModifier("conditional_multiply_levels", levels, ConditionalFloatParameter.class, (cost, context, parameters) -> {
            if (context.matchesCondition(parameters.id.value)) {
                cost.setLevels((int) (cost.getLevels() * parameters.scale.value));
            }
            return cost;
        });
        registerModifier("scaled_add_levels", levels, VariableScaledParameter.class, (cost, context, parameters) -> {
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
        registerModifier("conditional_add_xp", experiencePoints, ConditionalFloatParameter.class, (cost, context, parameters) -> {
            if (context.matchesCondition(parameters.id.value)) {
                cost.setPoints((int) (cost.getPoints() + parameters.scale.value));
            }
            return cost;
        });
        registerModifier("multiply_xp", experiencePoints, FloatParameter.class, (cost, context, parameters) -> {
            cost.setPoints((int) (cost.getPoints() * parameters.value));
            return cost;
        });
        registerModifier("conditional_multiply_xp", experiencePoints, ConditionalFloatParameter.class, (cost, context, parameters) -> {
            if (context.matchesCondition(parameters.id.value)) {
                cost.setPoints((int) (cost.getPoints() * parameters.scale.value));
            }
            return cost;
        });
        registerModifier("scaled_add_xp", experiencePoints, VariableScaledParameter.class, (cost, context, parameters) -> {
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

        registerSerializer(IntParameter.class, it -> new IntParameter(Integer.parseInt(it)));
        registerSerializer(FloatParameter.class, it -> new FloatParameter(Float.parseFloat(it)));
        registerSerializer(IdParameter.class, it -> {
            final var colon = it.indexOf(':');
            final var namespace = colon != -1 ? it.substring(0, colon) : "waystones";
            final var path = colon != -1 ? it.substring(colon + 1) : it;
            return new IdParameter(new ResourceLocation(namespace, path));
        });
        registerDefaultSerializer(VariableScaledParameter.class);
        registerDefaultSerializer(ConditionalFloatParameter.class);
    }

    public static void register(CostType<?> costType) {
        costTypes.put(costType.getId(), costType);
    }

    public static void register(CostModifier<?, ?> costModifier) {
        costModifiers.put(costModifier.getId(), costModifier);
    }

    public static void register(CostParameterSerializer<?> costParameterSerializer) {
        costParameterSerializers.put(costParameterSerializer.getType(), costParameterSerializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserializeParameter(Class<T> type, String value) {
        final var serializer = costParameterSerializers.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer registered for type " + type);
        }
        return (T) serializer.deserialize(value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserializeParameterList(Class<T> type, String commaSeparatedParameters) {
        final var constructor = type.getConstructors()[0];
        final var parameterTypes = constructor.getParameterTypes();
        final var parameters = commaSeparatedParameters.split(",");
        if (parameters.length != parameterTypes.length) {
            throw new IllegalArgumentException("Parameter count mismatch for type " + type);
        }

        final var parameterValues = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterValues[i] = deserializeParameter(parameterTypes[i], parameters[i]);
        }

        try {
            return (T) constructor.newInstance(parameterValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void registerDefaultSerializer(Class<T> type) {
        registerSerializer(type, it -> deserializeParameterList(type, it));
    }

    public static <T> void registerSerializer(Class<T> type, Function<String, T> deserializer) {
        register(new CostParameterSerializer<T>() {
            @Override
            public Class<T> getType() {
                return type;
            }

            @Override
            public T deserialize(String value) {
                return deserializer.apply(value);
            }
        });
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
