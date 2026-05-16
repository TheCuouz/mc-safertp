package com.cristian.safertp.finder;

import com.cristian.safertp.config.WorldConfig;
import com.cristian.safertp.integration.WorldGuardHook;
import com.cristian.safertp.safety.SafetyChecker;
import io.papermc.lib.PaperLib;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public final class LocationFinder {

    private static final Random RANDOM = new Random();

    private LocationFinder() {}

    public static CompletableFuture<Location> findSafe(World world, WorldConfig config) {
        return findSafe(world, config, null);
    }

    public static CompletableFuture<Location> findSafe(World world, WorldConfig config,
                                                        @Nullable WorldGuardHook wgHook) {
        CompletableFuture<Location> future = new CompletableFuture<>();
        attemptFind(world, config, wgHook, 0, future);
        return future;
    }

    private static void attemptFind(World world, WorldConfig config,
                                     @Nullable WorldGuardHook wgHook,
                                     int attempt, CompletableFuture<Location> future) {
        if (future.isDone()) return;
        if (attempt >= config.maxAttempts()) {
            future.completeExceptionally(
                new NoSafeLocationException(world.getName(), config.maxAttempts()));
            return;
        }

        double angle = RANDOM.nextDouble() * 2 * Math.PI;
        double minR2 = (double) config.minRadius() * config.minRadius();
        double maxR2 = (double) config.maxRadius() * config.maxRadius();
        double radius = Math.sqrt(minR2 + RANDOM.nextDouble() * (maxR2 - minR2));

        int x = config.centerX() + (int) (radius * Math.cos(angle));
        int z = config.centerZ() + (int) (radius * Math.sin(angle));

        PaperLib.getChunkAtAsync(world, x >> 4, z >> 4, true)
            .thenAccept(chunk -> {
                int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
                Location candidate = new Location(world, x + 0.5, y + 1, z + 0.5);
                if (SafetyChecker.isSafe(candidate, config, wgHook)) {
                    future.complete(candidate);
                } else {
                    attemptFind(world, config, wgHook, attempt + 1, future);
                }
            })
            .exceptionally(ex -> {
                attemptFind(world, config, wgHook, attempt + 1, future);
                return null;
            });
    }
}
