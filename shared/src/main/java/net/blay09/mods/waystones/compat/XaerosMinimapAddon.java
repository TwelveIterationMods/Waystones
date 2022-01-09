package net.blay09.mods.waystones.compat;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

import xaero.common.XaeroMinimapSession;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.common.settings.ModSettings;
import xaero.minimap.XaeroMinimap;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Random;

public class XaerosMinimapAddon {
  public static String invalid = "invalid";
  private static boolean invalidWarned = false;
  private static String setNameInitiated;

  public static void initialize() {
    if (WaystonesConfig.getActive().displayWaystonesOnXaeros()) {
      Balm.getEvents().onEvent(KnownWaystonesEvent.class, XaerosMinimapAddon::onKnownWaystones);
    }
  }

  // assign same color to waypoints based on name
  static double randomColor(String waypointName){
    Random randnum = new Random();
    randnum.setSeed(stringToSeed(waypointName));
    return randnum.nextDouble() * (double) ModSettings.ENCHANT_COLORS.length;
  }

  static long stringToSeed(String s) {
      if (s == null) {
          return 0;
      }
      long hash = 0;
      for (char c : s.toCharArray()) {
          hash = 31L*hash + c;
      }
      return hash;
  }

  public static WaypointsManager getWaypointsManager() {
    Minecraft mc = Minecraft.getInstance();
    XaeroMinimapSession session = ((IXaeroMinimapClientPlayNetHandler) mc.player.connection)
        .getXaero_minimapSession();
    return session.getWaypointsManager();
  }

  public static void onKnownWaystones(KnownWaystonesEvent event) {
    if (!Compat.isXaerosMinimapInstalled) {
      return;
    }
    WaypointsManager wm = getWaypointsManager();
    // if world is not loaded yet, wait
    if (wm.getCurrentWorld() == null) {
      // if world is not loaded yet, wait half a second and try again
      CompletableFuture.delayedExecutor(500, TimeUnit.MILLISECONDS).execute(() -> {
        onKnownWaystones(event);
      });
    } else {
      addKnownWaypoints(event);
    }
  }

  public static void addKnownWaypoints(KnownWaystonesEvent event) {
    WaypointsManager wm = getWaypointsManager();
    String setName = WaystonesConfig.getActive().waystonesSetNameXaeros();
    WaypointSet originalSet = wm.getCurrentWorld().getSets().get(setName);
    // filter out any waypoints added to the set
    List<Waypoint> nonWaystones = originalSet.getList().stream().filter(e -> {
      return !(e instanceof WaystoneWaypoint && e.isTemporary());
    }).toList();
    // Call addSet to reset existing set with setName
    wm.getCurrentWorld().addSet(setName);
    // prevent selected set from toggling back to Waystones on each event
    if (setNameInitiated != setName && WaystonesConfig.getActive().waystonesSetDefaultXaeros()) {
      setNameInitiated = setName;
      wm.getCurrentWorld().setCurrent(setName);
    }

    WaypointSet set = wm.getCurrentWorld().getSets().get(setName);
    set.getList().addAll(nonWaystones);
    // add waystones to set
    for (IWaystone waystone : event.getWaystones()) {
      try {
        makeWaypoint(waystone.getPos(), waystone.hasName() ? waystone.getName() : "TEMP", set);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    try {
      XaeroMinimap.instance.getSettings().saveWaypoints(wm.getCurrentWorld());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void makeWaypoint(BlockPos pos, String name, WaypointSet set) {
    if (name.equals(invalid)) {
      invalidNameWarning();
      return;
    }
    WaystoneWaypoint instant = new WaystoneWaypoint(pos.getX(), pos.getY() + 2, pos.getZ(), name,
        name.substring(0, 1), (int) randomColor(name));
    set.getList().add(instant);
  }

  // inform player about bad waystone name once per session
  private static void invalidNameWarning() {
    if (!invalidWarned) {
      Minecraft mc = Minecraft.getInstance();
      Player entity = mc.player;
      TranslatableComponent chatComponent = new TranslatableComponent("chat.waystones.invalid_name_xaeros");
      chatComponent.withStyle(ChatFormatting.DARK_RED);
      entity.displayClientMessage(chatComponent, true);
      invalidWarned = true;
    }
  }

  // helper class to track which Waypoints were created by Waystones
  private static class WaystoneWaypoint extends Waypoint {
    WaystoneWaypoint(int x, int y, int z, String name, String symbol, int color) {
      // temporary=true to allow all Waystone points to be re-created in new sessions
      super(x, y, z, name, symbol, color, 0, true);
    }
  }
}
