package com.cristian.safertp.cfg;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Typed wrapper around {@code config.yml}.
 */
public final class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration cfg;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.cfg = plugin.getConfig();
    }

    public FileConfiguration raw() { return cfg; }

    /** Active locale (ISO-639-1 two-letter code, lowercase). Default "es". */
    public String language() { return cfg.getString("language", "es").toLowerCase(); }

    // ─── Back ─────────────────────────────────────────────────────────────────
    public boolean backEnabled()        { return cfg.getBoolean("back.enabled", true); }
    public long    backTtlSeconds()     { return cfg.getLong("back.ttl-seconds", 300L); }
    public long    backCooldownSeconds(){ return cfg.getLong("back.cooldown-seconds", 60L); }
}
