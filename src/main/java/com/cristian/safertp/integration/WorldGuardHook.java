package com.cristian.safertp.integration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class WorldGuardHook {

    private final RegionContainer regionContainer;

    private WorldGuardHook(RegionContainer container) {
        this.regionContainer = container;
    }

    public static WorldGuardHook setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) return null;
        try {
            RegionContainer container =
                WorldGuard.getInstance().getPlatform().getRegionContainer();
            return new WorldGuardHook(container);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isInProtectedRegion(Location loc) {
        if (loc.getWorld() == null) return false;
        RegionManager regions = regionContainer.get(BukkitAdapter.adapt(loc.getWorld()));
        if (regions == null) return false;
        return !regions.getApplicableRegions(
            BukkitAdapter.asBlockVector(loc)).getRegions().isEmpty();
    }
}
