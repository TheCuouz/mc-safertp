package com.cristian.safertp.manager;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final ConcurrentHashMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public void setCooldown(UUID uuid, long seconds) {
        cooldowns.put(uuid, System.currentTimeMillis() + seconds * 1000L);
    }

    public long getRemaining(UUID uuid) {
        Long expiry = cooldowns.get(uuid);
        if (expiry == null) return 0L;
        long remaining = (expiry - System.currentTimeMillis() + 999) / 1000L;
        if (remaining <= 0) {
            cooldowns.remove(uuid);
            return 0L;
        }
        return remaining;
    }

    public boolean isOnCooldown(UUID uuid) {
        return getRemaining(uuid) > 0;
    }

    public void clearCooldown(UUID uuid) {
        cooldowns.remove(uuid);
    }

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
