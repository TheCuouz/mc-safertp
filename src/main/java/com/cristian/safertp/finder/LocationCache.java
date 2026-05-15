package com.cristian.safertp.finder;

import org.bukkit.Location;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LocationCache {

    private final Map<String, Deque<Location>> cache = new ConcurrentHashMap<>();
    private final int maxPerWorld;
    private final int refillThreshold;

    public LocationCache(int maxPerWorld, int refillThreshold) {
        this.maxPerWorld = maxPerWorld;
        this.refillThreshold = refillThreshold;
    }

    public Optional<Location> poll(String worldName) {
        Deque<Location> deque = cache.get(worldName);
        if (deque == null) return Optional.empty();
        synchronized (deque) {
            Location loc = deque.poll();
            return Optional.ofNullable(loc);
        }
    }

    public boolean offer(String worldName, Location loc) {
        Deque<Location> deque = cache.computeIfAbsent(worldName, k -> new ArrayDeque<>());
        synchronized (deque) {
            if (deque.size() >= maxPerWorld) return false;
            deque.offer(loc.clone());
            return true;
        }
    }

    public int size(String worldName) {
        Deque<Location> deque = cache.get(worldName);
        if (deque == null) return 0;
        synchronized (deque) {
            return deque.size();
        }
    }

    public boolean needsRefill(String worldName) {
        return size(worldName) < refillThreshold;
    }

    public void clear(String worldName) {
        cache.remove(worldName);
    }

    public void clearAll() {
        cache.clear();
    }
}
