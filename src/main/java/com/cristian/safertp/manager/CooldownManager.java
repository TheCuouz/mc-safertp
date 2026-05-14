package com.cristian.safertp.manager;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    /** Default key used by the legacy single-cooldown API (the main /rtp cooldown). */
    private static final String DEFAULT_KEY = "rtp";

    /** Composite key = uuid:cooldownKey, so a player can be on cooldown for /rtp AND /rtp back independently. */
    private final ConcurrentHashMap<String, Long> cooldowns = new ConcurrentHashMap<>();

    // ----------------------------------------------------------------------
    // Legacy API (single cooldown bucket per player) — preserved unchanged.
    // ----------------------------------------------------------------------

    public void setCooldown(UUID uuid, long seconds) {
        setCooldown(uuid, DEFAULT_KEY, seconds);
    }

    public long getRemaining(UUID uuid) {
        return getRemainingSeconds(uuid, DEFAULT_KEY);
    }

    public boolean isOnCooldown(UUID uuid) {
        return isOnCooldown(uuid, DEFAULT_KEY);
    }

    public void clearCooldown(UUID uuid) {
        clearCooldown(uuid, DEFAULT_KEY);
    }

    // ----------------------------------------------------------------------
    // Multi-key API (added in 1.1.0 for /rtp back).
    // ----------------------------------------------------------------------

    public void setCooldown(UUID uuid, String key, long seconds) {
        cooldowns.put(composite(uuid, key), System.currentTimeMillis() + seconds * 1000L);
    }

    public long getRemainingSeconds(UUID uuid, String key) {
        Long expiry = cooldowns.get(composite(uuid, key));
        if (expiry == null) return 0L;
        long remaining = (expiry - System.currentTimeMillis() + 999) / 1000L;
        if (remaining <= 0) {
            cooldowns.remove(composite(uuid, key));
            return 0L;
        }
        return remaining;
    }

    public boolean isOnCooldown(UUID uuid, String key) {
        return getRemainingSeconds(uuid, key) > 0;
    }

    public void clearCooldown(UUID uuid, String key) {
        cooldowns.remove(composite(uuid, key));
    }

    private static String composite(UUID uuid, String key) {
        return uuid + ":" + key;
    }

    // ----------------------------------------------------------------------
    // Permission-resolved cooldown for the main /rtp action.
    // ----------------------------------------------------------------------

    public int resolvePlayerCooldown(Player player, int defaultCooldown) {
        if (player.hasPermission("safertp.bypass.cooldown")) return 0;
        int best = defaultCooldown;
        for (var perm : player.getEffectivePermissions()) {
            String name = perm.getPermission();
            if (name.startsWith("safertp.cooldown.") && perm.getValue()) {
                try {
                    int val = Integer.parseInt(name.substring("safertp.cooldown.".length()));
                    if (val < best) best = val;
                } catch (NumberFormatException ignored) {}
            }
        }
        return best;
    }
}
