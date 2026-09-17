package com.cristian.safertp.finder;

import com.cristian.safertp.config.WorldConfig;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Background refill of the location cache, called once per tick.
 *
 * Searches load (and may generate) chunks and take many ticks to finish, so the
 * slots already being searched count towards the cache size: cached + in-flight
 * never exceeds size-per-world. A world where a search finds no safe spot is
 * left alone for {@code failureBackoffMillis} instead of being retried every tick.
 */
public final class CacheRefiller {

    public static final long DEFAULT_FAILURE_BACKOFF_MILLIS = 5L * 60L * 1000L;

    /** Starts a search for the world, or returns null when the world isn't loaded. */
    @FunctionalInterface
    public interface Searcher {
        @Nullable CompletableFuture<Location> start(WorldConfig config);
    }

    private final Supplier<LocationCache> cache;
    private final Supplier<List<WorldConfig>> worlds;
    private final IntSupplier sizePerWorld;
    private final Searcher searcher;
    private final LongSupplier clock;
    private final long failureBackoffMillis;

    private final AtomicInteger refillIndex = new AtomicInteger(0);
    private final Map<String, AtomicInteger> inFlight = new ConcurrentHashMap<>();
    private final Map<String, Long> retryAfter = new ConcurrentHashMap<>();

    public CacheRefiller(Supplier<LocationCache> cache, Supplier<List<WorldConfig>> worlds,
                         IntSupplier sizePerWorld, Searcher searcher) {
        this(cache, worlds, sizePerWorld, searcher, System::currentTimeMillis,
            DEFAULT_FAILURE_BACKOFF_MILLIS);
    }

    public CacheRefiller(Supplier<LocationCache> cache, Supplier<List<WorldConfig>> worlds,
                         IntSupplier sizePerWorld, Searcher searcher,
                         LongSupplier clock, long failureBackoffMillis) {
        this.cache = cache;
        this.worlds = worlds;
        this.sizePerWorld = sizePerWorld;
        this.searcher = searcher;
        this.clock = clock;
        this.failureBackoffMillis = failureBackoffMillis;
    }

    /** Starts at most one search per call, on the next world that needs one. */
    public void tick() {
        var list = new ArrayList<>(worlds.get());
        if (list.isEmpty()) return;
        for (int i = 0; i < list.size(); i++) {
            WorldConfig wc = list.get(Math.floorMod(refillIndex.getAndIncrement(), list.size()));
            if (tryStart(wc)) return;
        }
    }

    public int inFlight(String worldName) {
        AtomicInteger n = inFlight.get(worldName);
        return n == null ? 0 : n.get();
    }

    private boolean tryStart(WorldConfig wc) {
        if (!wc.enabled()) return false;
        String name = wc.worldName();
        Long waitUntil = retryAfter.get(name);
        if (waitUntil != null && clock.getAsLong() < waitUntil) return false;

        AtomicInteger running = inFlight.computeIfAbsent(name, k -> new AtomicInteger());
        if (cache.get().size(name) + running.get() >= sizePerWorld.getAsInt()) return false;

        running.incrementAndGet();
        CompletableFuture<Location> search;
        try {
            search = searcher.start(wc);
        } catch (RuntimeException e) {
            running.decrementAndGet();
            throw e;
        }
        if (search == null) {
            running.decrementAndGet();
            return false;
        }
        search.whenComplete((loc, ex) -> {
            running.decrementAndGet();
            if (ex == null && loc != null) {
                retryAfter.remove(name);
                cache.get().offer(name, loc);
            } else {
                retryAfter.put(name, clock.getAsLong() + failureBackoffMillis);
            }
        });
        return true;
    }
}
