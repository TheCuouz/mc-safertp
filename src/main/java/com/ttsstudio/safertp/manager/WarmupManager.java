package com.ttsstudio.safertp.manager;

import com.ttsstudio.safertp.SafeRtpPlugin;
import com.ttsstudio.sdk.text.Texts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WarmupManager {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final SafeRtpPlugin plugin;
    private final ConcurrentHashMap<UUID, com.ttsstudio.sdk.scheduler.Task> active = new ConcurrentHashMap<>();

    public WarmupManager(SafeRtpPlugin plugin) {
        this.plugin = plugin;
    }

    public void start(Player player, int seconds, Runnable onComplete) {
        cancel(player.getUniqueId());

        // Counts down where the player is (their region on Folia).
        final int[] remaining = {seconds};
        com.ttsstudio.sdk.scheduler.Task task = com.ttsstudio.sdk.scheduler.Scheduler.entityTimer(plugin, player, t -> {
            if (!player.isOnline()) {
                active.remove(player.getUniqueId());
                t.cancel();
                return;
            }
            if (remaining[0] <= 0) {
                active.remove(player.getUniqueId());
                t.cancel();
                Texts.actionBar(player, Component.empty());
                onComplete.run();
                return;
            }
            String raw = plugin.getMessagesConfig()
                .getString("rtp-warmup-action-bar", "<yellow>⏳ <seconds>s")
                .replace("<seconds>", String.valueOf(remaining[0]));
            Texts.actionBar(player, MM.deserialize(raw));
            remaining[0]--;
        }, 1L, 20L);

        active.put(player.getUniqueId(), task);
    }

    public boolean isInWarmup(UUID uuid) {
        return active.containsKey(uuid);
    }

    public void cancel(UUID uuid) {
        com.ttsstudio.sdk.scheduler.Task task = active.remove(uuid);
        if (task != null) task.cancel();
    }

    public void cancelAll() {
        active.values().forEach(com.ttsstudio.sdk.scheduler.Task::cancel);
        active.clear();
    }
}
