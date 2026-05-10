package com.cristian.safertp.manager;

import com.cristian.safertp.SafeRtpPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WarmupManager {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final SafeRtpPlugin plugin;
    private final ConcurrentHashMap<UUID, BukkitTask> active = new ConcurrentHashMap<>();

    public WarmupManager(SafeRtpPlugin plugin) {
        this.plugin = plugin;
    }

    public void start(Player player, int seconds, Runnable onComplete) {
        cancel(player.getUniqueId());

        BukkitTask task = new BukkitRunnable() {
            int remaining = seconds;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    active.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }
                if (remaining <= 0) {
                    active.remove(player.getUniqueId());
                    this.cancel();
                    onComplete.run();
                    return;
                }
                String titleStr = plugin.getMessagesConfig()
                    .getString("rtp-warmup-title", "<yellow><seconds>")
                    .replace("<seconds>", String.valueOf(remaining));
                String subtitleStr = plugin.getMessagesConfig()
                    .getString("rtp-warmup-subtitle", "<gray>No te muevas");
                player.showTitle(Title.title(
                    MM.deserialize(titleStr),
                    MM.deserialize(subtitleStr),
                    Title.Times.times(Duration.ZERO, Duration.ofMillis(1100), Duration.ZERO)
                ));
                remaining--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        active.put(player.getUniqueId(), task);
    }

    public boolean isInWarmup(UUID uuid) {
        return active.containsKey(uuid);
    }

    public void cancel(UUID uuid) {
        BukkitTask task = active.remove(uuid);
        if (task != null) task.cancel();
    }

    public void cancelAll() {
        active.values().forEach(BukkitTask::cancel);
        active.clear();
    }
}
