package com.cristian.safertp.listener;

import com.cristian.safertp.SafeRtpPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WarmupListener implements Listener {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final SafeRtpPlugin plugin;

    public WarmupListener(SafeRtpPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getWarmupManager().isInWarmup(player.getUniqueId())) return;

        Location from = event.getFrom();
        Location to   = event.getTo();
        if (to == null) return;
        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) return;

        plugin.getWarmupManager().cancel(player.getUniqueId());
        player.clearTitle();
        player.sendMessage(MM.deserialize(
            plugin.getMessagesConfig().getString("rtp-cancelled-move",
                "<red>Teletransporte cancelado al moverte.")));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.getWarmupManager().isInWarmup(player.getUniqueId())) return;

        plugin.getWarmupManager().cancel(player.getUniqueId());
        player.clearTitle();
        player.sendMessage(MM.deserialize(
            plugin.getMessagesConfig().getString("rtp-cancelled-damage",
                "<red>Teletransporte cancelado al recibir daño.")));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getWarmupManager().cancel(event.getPlayer().getUniqueId());
    }
}
