package net.blay09.mods.waystones.compat;

import net.minecraft.client.ClientBrandRetriever;

import java.util.Locale;

public class Vivecraft {

    private static boolean checked;
    private static boolean isInstalled;

    public static boolean isInstalled() {
        if (!checked) {
            checked = true;
            isInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains("vivecraft");
        }

        return isInstalled;
    }

}
