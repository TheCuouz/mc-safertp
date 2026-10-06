package com.ttsstudio.safertp.finder;

import com.ttsstudio.safertp.config.WorldConfig;
import com.ttsstudio.safertp.integration.WorldGuardHook;
import com.ttsstudio.safertp.safety.SafetyChecker;
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
                Location candidate;
                if (world.getEnvironment() == World.Environment.NETHER) {
                    // The highest block of a Nether column is the bedrock roof:
                    // every teleport landed on top of it.
                    int feet = standableBelowCeiling(
                        y -> world.getBlockAt(x, y, z).getType().isSolid(),
                        y -> world.getBlockAt(x, y, z).getType().isAir(),
                        world.getMinHeight(), Math.min(world.getMaxHeight(), 127));
                    candidate = feet == Integer.MIN_VALUE ? null : new Location(world, x + 0.5, feet, z + 0.5);
                } else {
                    int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
                    candidate = new Location(world, x + 0.5, y + 1, z + 0.5);
                }
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

    /**
     * Where to stand in a column that has a ceiling: the highest spot with two
     * blocks of air over solid ground, strictly under the roof.
     *
     * @return the Y of the feet, or {@code Integer.MIN_VALUE} if the column has none
     */
    public static int standableBelowCeiling(java.util.function.IntPredicate solid,
                                            java.util.function.IntPredicate air,
                                            int minY, int roofY) {
        for (int feet = roofY - 3; feet > minY + 1; feet--) {
            if (air.test(feet) && air.test(feet + 1) && solid.test(feet - 1)) return feet;
        }
        return Integer.MIN_VALUE;
    }
}
