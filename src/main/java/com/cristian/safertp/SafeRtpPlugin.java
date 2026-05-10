package com.cristian.safertp;

import org.bukkit.plugin.java.JavaPlugin;

public final class SafeRtpPlugin extends JavaPlugin {

    private static SafeRtpPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getSLF4JLogger().info("SafeRTP enabled.");
    }

    @Override
    public void onDisable() {
        getSLF4JLogger().info("SafeRTP disabled.");
    }

    public static SafeRtpPlugin getInstance() { return instance; }
}
