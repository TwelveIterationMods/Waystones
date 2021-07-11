package net.blay09.mods.waystones;

import net.blay09.mods.balm.event.BalmEventDispatcher;
import net.blay09.mods.balm.event.BalmEventHandler;
import net.blay09.mods.waystones.api.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;

public class ModEvents {

    public interface WaystoneActivatedHandler extends BalmEventHandler<WaystoneActivatedEvent> {
    }

    public interface KnownWaystonesHandler extends BalmEventHandler<KnownWaystonesEvent> {
    }

    public interface GenerateWaystoneNameHandler extends BalmEventHandler<GenerateWaystoneNameEvent> {
    }

    public static final BalmEventDispatcher<WaystoneActivatedEvent, WaystoneActivatedHandler> WAYSTONE_ACTIVATED = new BalmEventDispatcher<>(WaystoneActivatedHandler.class, (listeners) -> (event) -> {
        for (WaystoneActivatedHandler listener : listeners) {
            listener.handle(event);
        }
    });

    public static final BalmEventDispatcher<KnownWaystonesEvent, KnownWaystonesHandler> KNOWN_WAYSTONES = new BalmEventDispatcher<>(KnownWaystonesHandler.class, (listeners) -> (event) -> {
        for (KnownWaystonesHandler listener : listeners) {
            listener.handle(event);
        }
    });

    public static final BalmEventDispatcher<GenerateWaystoneNameEvent, GenerateWaystoneNameHandler> GENERATE_WAYSTONE_NAME = new BalmEventDispatcher<>(GenerateWaystoneNameHandler.class, (listeners) -> (event) -> {
        for (GenerateWaystoneNameHandler listener : listeners) {
            listener.handle(event);
        }
    });
}
