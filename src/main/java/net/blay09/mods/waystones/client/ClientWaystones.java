package net.blay09.mods.waystones.client;

import com.google.common.collect.Maps;
import net.blay09.mods.waystones.util.WaystoneEntry;

import javax.annotation.Nullable;
import java.util.Map;

public class ClientWaystones {

	private static final Map<String, WaystoneEntry> knownWaystones = Maps.newHashMap();

	public static void setKnownWaystones(WaystoneEntry[] entries) {
		knownWaystones.clear();
		for(WaystoneEntry entry : entries) {
			knownWaystones.put(entry.getName(), entry);
		}
	}

	@Nullable
	public static WaystoneEntry getKnownWaystone(String name) {
		return knownWaystones.get(name);
	}

	public static WaystoneEntry[] getKnownWaystones() {
		return knownWaystones.values().toArray(new WaystoneEntry[0]);
	}
}
