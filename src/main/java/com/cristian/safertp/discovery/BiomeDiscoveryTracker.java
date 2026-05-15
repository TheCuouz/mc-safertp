package com.cristian.safertp.discovery;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BiomeDiscoveryTracker {

    private final File dataFile;
    private final Map<UUID, Set<String>> discovered = new ConcurrentHashMap<>();

    public BiomeDiscoveryTracker(File dataFolder) {
        this.dataFile = new File(dataFolder, "discoveries.yml");
    }

    public boolean discover(UUID playerId, String biomeName) {
        Set<String> playerBiomes = discovered.computeIfAbsent(
            playerId, k -> ConcurrentHashMap.newKeySet());
        return playerBiomes.add(biomeName);
    }

    public Set<String> getDiscovered(UUID playerId) {
        return Collections.unmodifiableSet(
            discovered.getOrDefault(playerId, Collections.emptySet()));
    }

    public void load() {
        if (!dataFile.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : yaml.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                Set<String> set = ConcurrentHashMap.newKeySet();
                set.addAll(yaml.getStringList(key));
                discovered.put(uuid, set);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, Set<String>> entry : discovered.entrySet()) {
            yaml.set(entry.getKey().toString(), new ArrayList<>(entry.getValue()));
        }
        try {
            yaml.save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save discoveries.yml", e);
        }
    }
}
