package com.cristian.safertp.back;

import org.bukkit.Location;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * In-memory store of pre-teleport locations so players can {@code /rtp back}.
 *
 * <p>Entries expire after {@code ttlMillis}. Lookups past the TTL return empty and
 * lazily evict the stale entry. A {@link #purgeExpired()} hook is exposed so the
 * plugin can sweep the map periodically without waiting for a lookup.</p>
 *
 * <p>The clock is injectable for deterministic testing.</p>
 */
public class BackLocationStore {

    private final Map<UUID, Entry> data = new ConcurrentHashMap<>();
    private final long ttlMillis;
    private final LongSupplier clock;

    public BackLocationStore(long ttlMillis) {
        this(ttlMillis, System::currentTimeMillis);
    }

    BackLocationStore(long ttlMillis, LongSupplier clock) {
        this.ttlMillis = ttlMillis;
        this.clock = clock;
    }

    public void capture(UUID uuid, Location loc) {
        data.put(uuid, new Entry(loc.clone(), clock.getAsLong()));
    }

    public Optional<Location> get(UUID uuid) {
        Entry e = data.get(uuid);
        if (e == null) return Optional.empty();
        if (clock.getAsLong() - e.timestamp() > ttlMillis) {
            data.remove(uuid);
            return Optional.empty();
        }
        return Optional.of(e.location().clone());
    }

    public void clear(UUID uuid) {
        data.remove(uuid);
    }

    public void purgeExpired() {
        long now = clock.getAsLong();
        data.entrySet().removeIf(e -> now - e.getValue().timestamp() > ttlMillis);
    }

    public int size() {
        return data.size();
    }

    private record Entry(Location location, long timestamp) {}
}
