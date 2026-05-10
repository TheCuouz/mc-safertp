package com.cristian.safertp.config;

import org.bukkit.block.Biome;
import java.util.Set;

public record WorldConfig(
    String worldName,
    boolean enabled,
    int minRadius,
    int maxRadius,
    int centerX,
    int centerZ,
    Set<Biome> biomeWhitelist,
    Set<Biome> biomeBlacklist,
    int maxAttempts,
    double cost
) {}
