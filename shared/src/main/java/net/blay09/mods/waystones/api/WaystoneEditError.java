package net.blay09.mods.waystones.api;

import org.jetbrains.annotations.Nullable;

public class WaystoneEditError {

    private final String translationKey;

    public WaystoneEditError() {
        this.translationKey = null;
    }

    public WaystoneEditError(String translationKey) {
        this.translationKey = translationKey;
    }

    @Nullable
    public String getTranslationKey() {
        return translationKey;
    }

    public static class NotOwner extends WaystoneEditError {

        public NotOwner() {
            super("chat.waystones.only_owner_can_edit");
        }
    }

    public static class RequiresCreative extends WaystoneEditError {
        public RequiresCreative() {
            super("chat.waystones.only_creative_can_edit");
        }
    }

}
