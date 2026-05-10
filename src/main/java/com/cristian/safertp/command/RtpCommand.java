package com.cristian.safertp.command;

import com.cristian.safertp.SafeRtpPlugin;
import com.cristian.safertp.config.WorldConfig;
import com.cristian.safertp.finder.LocationFinder;
import com.cristian.safertp.finder.NoSafeLocationException;
import com.cristian.safertp.integration.VaultHook;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RtpCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final SafeRtpPlugin plugin;

    public RtpCommand(SafeRtpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        // /rtp reload
        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("safertp.admin")) {
                sender.sendMessage(MM.deserialize(msg("rtp-no-permission")));
                return true;
            }
            plugin.reload();
            sender.sendMessage(MM.deserialize(msg("reload-success")));
            return true;
        }

        // /rtp other <player>
        if (args.length >= 2 && args[0].equalsIgnoreCase("other")) {
            if (!sender.hasPermission("safertp.admin")) {
                sender.sendMessage(MM.deserialize(msg("rtp-no-permission")));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(MM.deserialize(
                    msg("rtp-player-not-found").replace("<player>", args[1])));
                return true;
            }
            doRtp(target, target.getWorld(), true);
            return true;
        }

        // /rtp [world]
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use /rtp.");
            return true;
        }
        if (!player.hasPermission("safertp.use")) {
            player.sendMessage(MM.deserialize(msg("rtp-no-permission")));
            return true;
        }

        World world = player.getWorld();
        if (args.length >= 1) {
            World requested = Bukkit.getWorld(args[0]);
            if (requested == null) {
                player.sendMessage(MM.deserialize(msg("rtp-world-disabled")));
                return true;
            }
            world = requested;
        }

        if (!player.hasPermission("safertp.world." + world.getName())
                && !player.hasPermission("safertp.admin")) {
            player.sendMessage(MM.deserialize(msg("rtp-world-no-permission")));
            return true;
        }

        doRtp(player, world, false);
        return true;
    }

    private void doRtp(Player player, World world, boolean bypassCooldown) {
        var optConfig = plugin.getWorldConfigRegistry().get(world.getName());
        if (optConfig.isEmpty() || !optConfig.get().enabled()) {
            player.sendMessage(MM.deserialize(msg("rtp-world-disabled")));
            return;
        }
        WorldConfig config = optConfig.get();

        // Cooldown check
        if (!bypassCooldown && !player.hasPermission("safertp.bypass.cooldown")) {
            long remaining = plugin.getCooldownManager().getRemaining(player.getUniqueId());
            if (remaining > 0) {
                player.sendMessage(MM.deserialize(
                    msg("rtp-cooldown").replace("<seconds>", String.valueOf(remaining))));
                return;
            }
        }

        // Cost pre-check
        VaultHook vault = plugin.getVaultHook();
        if (config.cost() > 0 && vault != null && !player.hasPermission("safertp.bypass.cost")) {
            if (!vault.has(player, config.cost())) {
                player.sendMessage(MM.deserialize(
                    msg("rtp-not-enough-money").replace("<amount>",
                        String.format("%.2f", config.cost()))));
                return;
            }
        }

        // Warmup → search → teleport
        final World finalWorld = world;
        plugin.getWarmupManager().start(player,
            plugin.getWorldConfigRegistry().getWarmupSeconds(), () -> {

                player.sendActionBar(MM.deserialize(msg("rtp-searching")));

                LocationFinder.findSafe(finalWorld, config).thenAccept(loc -> {
                    PaperLib.teleportAsync(player, loc).thenAccept(success -> {
                        if (!success) return;

                        // Withdraw cost
                        if (config.cost() > 0 && vault != null
                                && !player.hasPermission("safertp.bypass.cost")) {
                            vault.withdraw(player, config.cost());
                            player.sendMessage(MM.deserialize(
                                msg("rtp-cost").replace("<amount>",
                                    String.format("%.2f", config.cost()))));
                        }

                        // Apply cooldown
                        if (!bypassCooldown) {
                            int secs = plugin.getCooldownManager().resolvePlayerCooldown(
                                player, plugin.getWorldConfigRegistry().getDefaultCooldown());
                            if (secs > 0) {
                                plugin.getCooldownManager().setCooldown(player.getUniqueId(), secs);
                            }
                        }

                        player.sendMessage(MM.deserialize(
                            msg("rtp-success")
                                .replace("<world>", finalWorld.getName())
                                .replace("<x>", String.valueOf(loc.getBlockX()))
                                .replace("<y>", String.valueOf(loc.getBlockY()))
                                .replace("<z>", String.valueOf(loc.getBlockZ()))));
                    });
                }).exceptionally(ex -> {
                    if (ex.getCause() instanceof NoSafeLocationException) {
                        player.sendMessage(MM.deserialize(msg("rtp-no-safe-location")));
                    } else {
                        plugin.getSLF4JLogger().error("RTP search error", ex);
                    }
                    return null;
                });
            });
    }

    private String msg(String key) {
        return plugin.getMessagesConfig().getString(key, "<red>Missing: " + key);
    }
}
