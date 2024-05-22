package net.blay09.mods.waystones.api.requirement;

public interface ParameterSerializer<T> {
    Class<T> getType();
    T deserialize(String value);
}
