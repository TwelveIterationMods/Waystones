package net.blay09.mods.waystones.api.cost;

public interface CostParameterSerializer<T> {
    Class<T> getType();
    T deserialize(String value);
}
