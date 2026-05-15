package com.cristian.safertp.finder;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LocationCacheTest {

    @Test
    void poll_emptyCache_returnsEmpty() {
        LocationCache cache = new LocationCache(5, 2);
        assertTrue(cache.poll("world").isEmpty());
    }

    @Test
    void offer_thenPoll_returnsEquivalentLocation() {
        LocationCache cache = new LocationCache(5, 2);
        Location loc = new Location(null, 100, 64, 200);
        cache.offer("world", loc);

        Optional<Location> result = cache.poll("world");
        assertTrue(result.isPresent());
        assertEquals(100.0, result.get().getX());
        assertEquals(64.0, result.get().getY());
        assertEquals(200.0, result.get().getZ());
    }

    @Test
    void offer_isolatesFromExternalMutation() {
        LocationCache cache = new LocationCache(5, 2);
        Location loc = new Location(null, 10, 64, 20);
        cache.offer("world", loc);
        loc.setX(999); // mutate original

        Optional<Location> result = cache.poll("world");
        assertTrue(result.isPresent());
        assertEquals(10.0, result.get().getX()); // cache holds clone
    }

    @Test
    void needsRefill_belowThreshold_returnsTrue() {
        LocationCache cache = new LocationCache(5, 2);
        assertTrue(cache.needsRefill("world"));
    }

    @Test
    void needsRefill_atOrAboveThreshold_returnsFalse() {
        LocationCache cache = new LocationCache(5, 2);
        cache.offer("world", new Location(null, 0, 64, 0));
        cache.offer("world", new Location(null, 1, 64, 1));
        assertFalse(cache.needsRefill("world"));
    }

    @Test
    void offer_rejectsWhenFull() {
        LocationCache cache = new LocationCache(2, 1);
        assertTrue(cache.offer("world", new Location(null, 0, 64, 0)));
        assertTrue(cache.offer("world", new Location(null, 1, 64, 1)));
        assertFalse(cache.offer("world", new Location(null, 2, 64, 2)));
        assertEquals(2, cache.size("world"));
    }

    @Test
    void worldsAreIndependent() {
        LocationCache cache = new LocationCache(5, 2);
        cache.offer("world", new Location(null, 0, 64, 0));
        assertEquals(1, cache.size("world"));
        assertEquals(0, cache.size("world_nether"));
    }

    @Test
    void clearAll_emptiesAllWorlds() {
        LocationCache cache = new LocationCache(5, 2);
        cache.offer("world", new Location(null, 0, 64, 0));
        cache.offer("world_nether", new Location(null, 50, 64, 50));
        cache.clearAll();
        assertEquals(0, cache.size("world"));
        assertEquals(0, cache.size("world_nether"));
    }
}
