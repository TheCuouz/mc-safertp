package com.cristian.safertp;

import com.cristian.safertp.back.BackLocationStore;
import com.cristian.safertp.cfg.ConfigManager;
import com.cristian.safertp.cfg.MessageManager;
import com.cristian.safertp.command.RtpCommand;
import com.cristian.safertp.config.WorldConfigRegistry;
import com.cristian.safertp.finder.LocationCache;
import com.cristian.safertp.finder.LocationFinder;
import com.cristian.safertp.integration.PapiHook;
import com.cristian.safertp.integration.VaultHook;
import com.cristian.safertp.listener.WarmupListener;
import com.cristian.safertp.manager.CooldownManager;
import com.cristian.safertp.manager.WarmupManager;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.console.ConsoleBanner;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

public final class SafeRtpPlugin extends JavaPlugin {

    private static final long BACK_PURGE_INTERVAL_TICKS = 20L * 60L; // every 60s

    private ConfigManager configManager;
    private MessageManager messageManager;
    private WorldConfigRegistry worldConfigRegistry;
    private CooldownManager cooldownManager;
    private WarmupManager warmupManager;
    private BackLocationStore backLocationStore;
    private VaultHook vaultHook;
    private LocationCache locationCache;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        configManager = new ConfigManager(this);
        configManager.reload();
        saveResource("worlds.yml", false);
        messageManager = new MessageManager(this, configManager);
        messageManager.reload();

        worldConfigRegistry = new WorldConfigRegistry(this);
        worldConfigRegistry.load();

        cooldownManager = new CooldownManager();
        warmupManager = new WarmupManager(this);

        long ttlSeconds = configManager.backTtlSeconds();
        backLocationStore = new BackLocationStore(ttlSeconds * 1000L);
        Bukkit.getScheduler().runTaskTimer(this,
            () -> backLocationStore.purgeExpired(),
            BACK_PURGE_INTERVAL_TICKS, BACK_PURGE_INTERVAL_TICKS);

        locationCache = new LocationCache(
            configManager.cacheSizePerWorld(),
            configManager.cacheRefillThreshold());

        // Background cache refill — every 30s top up worlds below threshold.
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (!configManager.cacheEnabled()) return;
            for (var wc : worldConfigRegistry.all()) {
                if (!wc.enabled()) continue;
                org.bukkit.World w = org.bukkit.Bukkit.getWorld(wc.worldName());
                if (w == null) continue;
                int needed = configManager.cacheSizePerWorld() - locationCache.size(wc.worldName());
                for (int i = 0; i < needed; i++) {
                    LocationFinder.findSafe(w, wc)
                        .thenAccept(loc -> locationCache.offer(wc.worldName(), loc))
                        .exceptionally(ex -> null);
                }
            }
        }, 20L * 60L, 20L * 30L);

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
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

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PapiHook(this).register();
            getSLF4JLogger().info("PlaceholderAPI expansion registered.");
        }

        new Metrics(this, 12346);

        ConsoleBanner.enable(this, PluginIdentity.of(this))
            .status(worldConfigRegistry.size() + " world(s)")
            .hook(vaultHook != null ? "Vault" : null)
            .hook(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") ? "PAPI" : null)
            .hook(configManager.backEnabled() ? "Back" : null)
            .ready(Duration.ofMillis(System.currentTimeMillis() - startTime))
            .emit();
    }

    @Override
    public void onDisable() {
        if (warmupManager != null) warmupManager.cancelAll();
        ConsoleBanner.disable(this, PluginIdentity.of(this)).emit();
    }

    public void reload() {
        configManager.reload();
        messageManager.reload();
        worldConfigRegistry.load();
        locationCache = new LocationCache(
            configManager.cacheSizePerWorld(),
            configManager.cacheRefillThreshold());
    }

    public ConfigManager getConfigManager()            { return configManager; }
    public MessageManager getMessages()                { return messageManager; }
    /** Back-compat alias — call sites using .getString() compile unchanged. */
    public MessageManager getMessagesConfig()          { return messageManager; }
    public WorldConfigRegistry getWorldConfigRegistry(){ return worldConfigRegistry; }
    public CooldownManager getCooldownManager()        { return cooldownManager; }
    public WarmupManager getWarmupManager()            { return warmupManager; }
    public BackLocationStore getBackLocationStore()    { return backLocationStore; }
    public VaultHook getVaultHook()                    { return vaultHook; }
    public LocationCache getLocationCache()            { return locationCache; }
}
