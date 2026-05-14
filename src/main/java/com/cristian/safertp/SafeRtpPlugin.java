package com.cristian.safertp;

import com.cristian.safertp.back.BackLocationStore;
import com.cristian.safertp.command.RtpCommand;
import com.cristian.safertp.config.WorldConfigRegistry;
import com.cristian.safertp.integration.PapiHook;
import com.cristian.safertp.integration.VaultHook;
import com.cristian.safertp.listener.WarmupListener;
import com.cristian.safertp.manager.CooldownManager;
import com.cristian.safertp.manager.WarmupManager;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.console.ConsoleBanner;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.time.Duration;

public final class SafeRtpPlugin extends JavaPlugin {

    private static final long BACK_PURGE_INTERVAL_TICKS = 20L * 60L; // every 60s

    private WorldConfigRegistry worldConfigRegistry;
    private CooldownManager cooldownManager;
    private WarmupManager warmupManager;
    private BackLocationStore backLocationStore;
    private VaultHook vaultHook;
    private FileConfiguration messagesConfig;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        saveDefaultConfig();
        saveResource("worlds.yml", false);
        saveResource("messages.yml", false);

        loadMessages();

        worldConfigRegistry = new WorldConfigRegistry(this);
        worldConfigRegistry.load();

        cooldownManager = new CooldownManager();
        warmupManager = new WarmupManager(this);

        long ttlSeconds = getConfig().getLong("back.ttl-seconds", 300L);
        backLocationStore = new BackLocationStore(ttlSeconds * 1000L);
        Bukkit.getScheduler().runTaskTimer(this,
            () -> backLocationStore.purgeExpired(),
            BACK_PURGE_INTERVAL_TICKS, BACK_PURGE_INTERVAL_TICKS);

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            vaultHook = VaultHook.setup();
            if (vaultHook != null) {
                getSLF4JLogger().info("Vault economy hooked.");
            }
        }

        getServer().getPluginManager().registerEvents(new WarmupListener(this), this);

        var rtpExecutor = new RtpCommand(this);
        var cmd = getCommand("rtp");
        if (cmd != null) {
            cmd.setExecutor(rtpExecutor);
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiHook(this).register();
            getSLF4JLogger().info("PlaceholderAPI expansion registered.");
        }

        new Metrics(this, 12346);

        ConsoleBanner.enable(this, PluginIdentity.of(this))
            .status(worldConfigRegistry.size() + " world(s)")
            .hook(vaultHook != null ? "Vault" : null)
            .hook(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") ? "PAPI" : null)
            .hook(getConfig().getBoolean("back.enabled", true) ? "Back" : null)
            .ready(Duration.ofMillis(System.currentTimeMillis() - startTime))
            .emit();
    }

    @Override
    public void onDisable() {
        if (warmupManager != null) warmupManager.cancelAll();
        ConsoleBanner.disable(this, PluginIdentity.of(this)).emit();
    }

    public void reload() {
        reloadConfig();
        loadMessages();
        worldConfigRegistry.load();
    }

    private void loadMessages() {
        File file = new File(getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    public WorldConfigRegistry getWorldConfigRegistry() { return worldConfigRegistry; }
    public CooldownManager getCooldownManager()         { return cooldownManager; }
    public WarmupManager getWarmupManager()             { return warmupManager; }
    public BackLocationStore getBackLocationStore()     { return backLocationStore; }
    public VaultHook getVaultHook()                     { return vaultHook; }
    public FileConfiguration getMessagesConfig()        { return messagesConfig; }
}
