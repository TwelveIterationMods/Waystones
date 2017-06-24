package net.blay09.mods.waystones.crafting;

import com.google.gson.JsonObject;
import net.blay09.mods.waystones.WaystoneConfig;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

/**
 * Hopefully temporary class used by _factories.json. A hook to register conditions would be a much better solution since it's literally just a mapping to classes.
 */
public class CustomConditions {

	public static class AllowReturnScroll implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			return () -> WaystoneConfig.general.allowReturnScrolls;
		}
	}

	public static class AllowWarpScroll implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			return () -> WaystoneConfig.general.allowWarpScrolls;
		}
	}

	public static class AllowWarpStone implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			return () -> WaystoneConfig.general.allowWarpStone;
		}
	}

	public static class AllowWaystone implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			return () -> !WaystoneConfig.general.creativeModeOnly;
		}
	}
}
