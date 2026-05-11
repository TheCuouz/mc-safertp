package com.cristian.safertp.config;

import com.cristian.safertp.SafeRtpPlugin;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class WorldConfigRegistry {

    private final SafeRtpPlugin plugin;
    private final Map<String, WorldConfig> configs = new LinkedHashMap<>();
    private int defaultCooldown = 300;
    private int warmupSeconds = 5;

    public WorldConfigRegistry(SafeRtpPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        configs.clear();
        File file = new File(plugin.getDataFolder(), "worlds.yml");
        if (!file.exists()) plugin.saveResource("worlds.yml", false);

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        defaultCooldown = cfg.getInt("cooldown-default", 300);
        warmupSeconds   = cfg.getInt("warmup-seconds", 5);

        ConfigurationSection worlds = cfg.getConfigurationSection("worlds");
        if (worlds == null) return;

        for (String name : worlds.getKeys(false)) {
            ConfigurationSection sec = worlds.getConfigurationSection(name);
            if (sec == null) continue;

            Set<Biome> whitelist = parseBiomes(sec.getStringList("biome-whitelist"));
            Set<Biome> blacklist = parseBiomes(sec.getStringList("biome-blacklist"));

            configs.put(name, new WorldConfig(
                name,
                sec.getBoolean("enabled", true),
                sec.getInt("min-radius", 1000),
                sec.getInt("max-radius", 10000),
                sec.getInt("center-x", 0),
                sec.getInt("center-z", 0),
                whitelist,
                blacklist,
                sec.getInt("max-attempts", 50),
                sec.getDouble("cost", 0)
            ));
        }
        plugin.getSLF4JLogger().info("Loaded {} world configs.", configs.size());
    }

    private Set<Biome> parseBiomes(List<String> names) {
        Set<Biome> result = new HashSet<>();
        for (String name : names) {
            try {
                result.add(Biome.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                plugin.getSLF4JLogger().warn("Unknown biome '{}' in worlds.yml, skipping.", name);
            }
        }
        return result;
    }

    public Optional<WorldConfig> get(String worldName) {
        return Optional.ofNullable(configs.get(worldName));
    }

    public int getDefaultCooldown() { return defaultCooldown; }
    public int getWarmupSeconds()   { return warmupSeconds; }
    public int size()               { return configs.size(); }
}
