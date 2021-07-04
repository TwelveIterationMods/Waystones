package net.blay09.mods.waystones;

import net.blay09.mods.forbic.event.ForbicEventDispatcher;
import net.blay09.mods.forbic.event.ForbicEventHandler;
import net.blay09.mods.waystones.api.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;

public class ModEvents {

    public interface WaystoneActivatedHandler extends ForbicEventHandler<WaystoneActivatedEvent> {
    }

    public interface KnownWaystonesHandler extends ForbicEventHandler<KnownWaystonesEvent> {
    }

    public interface GenerateWaystoneNameHandler extends ForbicEventHandler<GenerateWaystoneNameEvent> {
    }

    public static final ForbicEventDispatcher<WaystoneActivatedEvent, WaystoneActivatedHandler> WAYSTONE_ACTIVATED = new ForbicEventDispatcher<>(WaystoneActivatedHandler.class, (listeners) -> (event) -> {
        for (WaystoneActivatedHandler listener : listeners) {
            listener.handle(event);
        }
    });

    public static final ForbicEventDispatcher<KnownWaystonesEvent, KnownWaystonesHandler> KNOWN_WAYSTONES = new ForbicEventDispatcher<>(KnownWaystonesHandler.class, (listeners) -> (event) -> {
        for (KnownWaystonesHandler listener : listeners) {
            listener.handle(event);
        }
    });

    public static final ForbicEventDispatcher<GenerateWaystoneNameEvent, GenerateWaystoneNameHandler> GENERATE_WAYSTONE_NAME = new ForbicEventDispatcher<>(GenerateWaystoneNameHandler.class, (listeners) -> (event) -> {
        for (GenerateWaystoneNameHandler listener : listeners) {
            listener.handle(event);
        }
    });
}
