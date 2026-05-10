package com.cristian.safertp;

import com.cristian.safertp.command.RtpCommand;
import com.cristian.safertp.config.WorldConfigRegistry;
import com.cristian.safertp.integration.PapiHook;
import com.cristian.safertp.integration.VaultHook;
import com.cristian.safertp.listener.WarmupListener;
import com.cristian.safertp.manager.CooldownManager;
import com.cristian.safertp.manager.WarmupManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class SafeRtpPlugin extends JavaPlugin {

    private WorldConfigRegistry worldConfigRegistry;
    private CooldownManager cooldownManager;
    private WarmupManager warmupManager;
    private VaultHook vaultHook;
    private FileConfiguration messagesConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("worlds.yml", false);
        saveResource("messages.yml", false);

        loadMessages();

        worldConfigRegistry = new WorldConfigRegistry(this);
        worldConfigRegistry.load();

        cooldownManager = new CooldownManager();
        warmupManager = new WarmupManager(this);

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

        getSLF4JLogger().info("SafeRTP enabled.");
    }

    @Override
    public void onDisable() {
        warmupManager.cancelAll();
        getSLF4JLogger().info("SafeRTP disabled.");
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
    public VaultHook getVaultHook()                     { return vaultHook; }
    public FileConfiguration getMessagesConfig()        { return messagesConfig; }
}
