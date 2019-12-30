package net.blay09.mods.waystones.core;

import javax.annotation.Nullable;

public enum WaystoneEditPermissions {
    ALLOW(null),
    NOT_CREATIVE(null),
    NOT_THE_OWNER("waystones:notTheOwner"),
    GET_CREATIVE("waystones:creativeRequired");

    private final String langKey;

    WaystoneEditPermissions(@Nullable String langKey) {
        this.langKey = langKey;
    }

    @Nullable
    public String getLangKey() {
        return langKey;
    }
}
