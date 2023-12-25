package net.blay09.mods.waystones.cost;

import com.mojang.datafixers.util.Pair;
import net.blay09.mods.waystones.api.IWaystoneTeleportContext;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.api.cost.*;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.core.WaystoneTeleportManager;
import net.blay09.mods.waystones.tag.ModItemTags;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CostRegistry {

    private static final Logger logger = LoggerFactory.getLogger(CostRegistry.class);

    private static final Map<ResourceLocation, CostType<?>> costTypes = new HashMap<>();
    private static final Map<ResourceLocation, CostModifier<?, ?>> costModifiers = new HashMap<>();
    private static final Map<Class<?>, CostParameterSerializer<?>> costParameterSerializers = new HashMap<>();
    private static final Map<ResourceLocation, CostVariableResolver> costVariableResolvers = new HashMap<>();
    private static final Map<ResourceLocation, CostConditionResolver> costConditionResolvers = new HashMap<>();

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
        registerSerializer(IdParameter.class, it -> new IdParameter(waystonesResourceLocation(it)));
        registerDefaultSerializer(VariableScaledParameter.class);
        registerDefaultSerializer(ConditionalFloatParameter.class);

        registerConditionResolver("is_interdimensional", IWaystoneTeleportContext::isDimensionalTeleport);
        registerConditionResolver("source_is_warp_plate",
                it -> it.getFromWaystone().map(waystone -> waystone.getWaystoneType().equals(WaystoneTypes.WARP_PLATE)).orElse(false));
        registerConditionResolver("source_is_portstone",
                it -> it.getFromWaystone().map(waystone -> waystone.getWaystoneType().equals(WaystoneTypes.PORTSTONE)).orElse(false));
        registerConditionResolver("source_is_waystone",
                it -> it.getFromWaystone().map(waystone -> waystone.getWaystoneType().equals(WaystoneTypes.WAYSTONE)).orElse(false));
        registerConditionResolver("source_is_sharestone",
                it -> it.getFromWaystone().map(waystone -> WaystoneTypes.isSharestone(waystone.getWaystoneType())).orElse(false));
        registerConditionResolver("source_is_inventory_button", it -> it.getWarpMode() == WarpMode.INVENTORY_BUTTON);
        registerConditionResolver("source_is_scroll", it -> it.getWarpItem().is(ModItemTags.SCROLLS));
        registerConditionResolver("source_is_bound_scroll", it -> it.getWarpItem().is(ModItemTags.BOUND_SCROLLS));
        registerConditionResolver("source_is_return_scroll", it -> it.getWarpItem().is(ModItemTags.RETURN_SCROLLS));
        registerConditionResolver("source_is_warp_scroll", it -> it.getWarpItem().is(ModItemTags.WARP_SCROLLS));
        registerConditionResolver("source_is_warp_stone", it -> it.getWarpItem().is(ModItemTags.WARP_STONES));
        registerConditionResolver("target_is_warp_plate", it -> it.getTargetWaystone().getWaystoneType().equals(WaystoneTypes.WARP_PLATE));
        registerConditionResolver("target_is_global", it -> it.getTargetWaystone().getVisibility() == WaystoneVisibility.GLOBAL);
        registerConditionResolver("target_is_sharestone", it -> WaystoneTypes.isSharestone(it.getTargetWaystone().getWaystoneType()));
        registerConditionResolver("target_is_waystone", it -> it.getTargetWaystone().getWaystoneType().equals(WaystoneTypes.WAYSTONE));
        registerConditionResolver("target_is_landing_stone", it -> it.getTargetWaystone().getWaystoneType().equals(WaystoneTypes.LANDING_STONE));
        registerConditionResolver("is_with_pets", it -> !WaystoneTeleportManager.findPets(it.getEntity()).isEmpty());
        registerConditionResolver("is_with_leashed", it -> !WaystoneTeleportManager.findLeashedAnimals(it.getEntity()).isEmpty());

        registerVariableResolver("distance", it -> (float) Math.sqrt(it.getEntity().distanceToSqr(it.getDestination().location())));
        registerVariableResolver("leashed", it -> (float) WaystoneTeleportManager.findLeashedAnimals(it.getEntity()).size());
        registerVariableResolver("pets", it -> (float) WaystoneTeleportManager.findPets(it.getEntity()).size());
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

    public static void register(CostVariableResolver costVariableResolver) {
        costVariableResolvers.put(costVariableResolver.getId(), costVariableResolver);
    }

    public static void register(CostConditionResolver costConditionResolver) {
        costConditionResolvers.put(costConditionResolver.getId(), costConditionResolver);
    }

    public static <T extends Cost, P> Optional<Pair<CostModifier<T, P>, P>> deserializeModifier(String modifier) {
        final var openParen = modifier.indexOf('(');
        final var closeParen = modifier.indexOf(')');
        if (openParen == -1 || closeParen == -1) {
            return Optional.empty();
        }

        final var modifierId = waystonesResourceLocation(modifier.substring(0, openParen));
        final var parameterString = modifier.substring(openParen + 1, closeParen);
        final var costModifier = CostRegistry.<T, P>getCostModifier(modifierId);
        if (costModifier == null) {
            return Optional.empty();
        }

        try {
            final var parameters = deserializeParameter(costModifier.getParameterType(), parameterString);
            return Optional.of(Pair.of(costModifier, parameters));
        } catch (Exception e) {
            logger.error("Failed to process waystone cost", e);
            return Optional.empty();
        }
    }

    private static ResourceLocation waystonesResourceLocation(String value) {
        final var colon = value.indexOf(':');
        final var namespace = colon != -1 ? value.substring(0, colon) : "waystones";
        final var path = colon != -1 ? value.substring(colon + 1) : value;
        return new ResourceLocation(namespace, path);
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
            parameterValues[i] = deserializeParameter(parameterTypes[i], parameters[i].trim());
        }

        try {
            return (T) constructor.newInstance(parameterValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerVariableResolver(String name, Function<IWaystoneTeleportContext, Float> resolver) {
        register(new CostVariableResolver() {
            @Override
            public ResourceLocation getId() {
                return waystonesResourceLocation(name);
            }

            @Override
            public float resolve(IWaystoneTeleportContext context) {
                return resolver.apply(context);
            }
        });
    }

    public static void registerConditionResolver(String name, Function<IWaystoneTeleportContext, Boolean> resolver) {
        register(new CostConditionResolver() {
            @Override
            public ResourceLocation getId() {
                return waystonesResourceLocation(name);
            }

            @Override
            public boolean matches(IWaystoneTeleportContext context) {
                return resolver.apply(context);
            }
        });

        final var index = name.indexOf("is_");
        final var notName = index != -1 ? name.substring(0, index + 3) + "not_" + name.substring(index + 3) : "not_" + name;
        register(new CostConditionResolver() {
            @Override
            public ResourceLocation getId() {
                return waystonesResourceLocation(notName);
            }

            @Override
            public boolean matches(IWaystoneTeleportContext context) {
                return !resolver.apply(context);
            }
        });
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

    private static <T extends Cost, P> void registerModifier(String name, CostType<T> costType, Class<P> parameterType, CostModifierFunction<T, P> function) {
        register(new CostModifier<T, P>() {
            @Override
            public ResourceLocation getId() {
                return waystonesResourceLocation(name);
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
    public static <T extends Cost> CostType<T> getCostType(ResourceLocation costType) {
        return (CostType<T>) costTypes.get(costType);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Cost, P> CostModifier<T, P> getCostModifier(ResourceLocation costModifier) {
        return (CostModifier<T, P>) costModifiers.get(costModifier);
    }

    public static CostVariableResolver getVariableResolver(ResourceLocation id) {
        return costVariableResolvers.get(id);
    }

    public static CostConditionResolver getConditionResolver(ResourceLocation id) {
        return costConditionResolvers.get(id);
    }
}
