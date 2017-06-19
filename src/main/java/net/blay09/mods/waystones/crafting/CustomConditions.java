package net.blay09.mods.waystones.crafting;

import com.google.gson.JsonObject;
import net.blay09.mods.waystones.Waystones;
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
			return () -> Waystones.getConfig().allowReturnScrolls;
		}
	}

	public static class AllowWarpScroll implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			return () -> Waystones.getConfig().allowWarpScrolls;
		}
	}

	public static class AllowWarpStone implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			return () -> Waystones.getConfig().allowWarpStone;
		}
	}

	public static class AllowWaystone implements IConditionFactory {
		@Override
		public BooleanSupplier parse(JsonContext context, JsonObject json) {
			return () -> !Waystones.getConfig().creativeModeOnly;
		}
	}
}
