package com.cristian.safertp.back;

import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class BackLocationStoreTest {

    private static final long TTL = 5_000L;
    private final UUID uuid = UUID.randomUUID();
    private AtomicLong now;
    private BackLocationStore store;

    @BeforeEach
    void setUp() {
        now = new AtomicLong(1_000_000L);
        store = new BackLocationStore(TTL, now::get);
    }

    @Test
    void capture_thenGet_returnsLocation() {
        Location loc = new Location(null, 10, 64, -20);
        store.capture(uuid, loc);

        Optional<Location> back = store.get(uuid);
        assertTrue(back.isPresent());
        assertEquals(10, back.get().getX());
        assertEquals(64, back.get().getY());
        assertEquals(-20, back.get().getZ());
    }

    @Test
    void get_unknownUuid_returnsEmpty() {
        assertTrue(store.get(uuid).isEmpty());
    }

    @Test
    void get_afterTtl_returnsEmpty() {
        store.capture(uuid, new Location(null, 1, 2, 3));
        now.addAndGet(TTL + 1);
        assertTrue(store.get(uuid).isEmpty());
    }

    @Test
    void get_exactlyAtTtl_stillReturnsLocation() {
        store.capture(uuid, new Location(null, 1, 2, 3));
        now.addAndGet(TTL);
        assertTrue(store.get(uuid).isPresent());
    }

    @Test
    void capture_secondTime_overwrites() {
        store.capture(uuid, new Location(null, 1, 2, 3));
        store.capture(uuid, new Location(null, 100, 200, 300));
        Location back = store.get(uuid).orElseThrow();
        assertEquals(100, back.getX());
        assertEquals(200, back.getY());
        assertEquals(300, back.getZ());
    }

    @Test
    void clear_removesEntryImmediately() {
        store.capture(uuid, new Location(null, 1, 2, 3));
        store.clear(uuid);
        assertTrue(store.get(uuid).isEmpty());
    }

    @Test
    void capture_clonesInputLocation_mutationsDontAffectStore() {
        Location loc = new Location(null, 1, 2, 3);
        store.capture(uuid, loc);
        loc.setX(999);
        Location back = store.get(uuid).orElseThrow();
        assertEquals(1, back.getX());
    }

    @Test
    void get_returnsClone_mutationsDontAffectStore() {
        store.capture(uuid, new Location(null, 1, 2, 3));
        Location back1 = store.get(uuid).orElseThrow();
        back1.setX(999);
        Location back2 = store.get(uuid).orElseThrow();
        assertEquals(1, back2.getX());
    }

    @Test
    void purgeExpired_removesStaleEntries() {
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        store.capture(u1, new Location(null, 1, 2, 3));
        now.addAndGet(TTL + 10);
        store.capture(u2, new Location(null, 4, 5, 6));

        store.purgeExpired();

        assertEquals(1, store.size());
        assertTrue(store.get(u1).isEmpty());
        assertTrue(store.get(u2).isPresent());
    }
}
