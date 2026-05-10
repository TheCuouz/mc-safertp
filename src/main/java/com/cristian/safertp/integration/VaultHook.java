package com.cristian.safertp.integration;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public class VaultHook {

    private final Economy economy;

    private VaultHook(Economy economy) {
        this.economy = economy;
    }

    public static @Nullable VaultHook setup() {
        RegisteredServiceProvider<Economy> rsp =
            Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return null;
        return new VaultHook(rsp.getProvider());
    }

    public boolean has(Player player, double amount) {
        return economy.has(player, amount);
    }

    public void withdraw(Player player, double amount) {
        economy.withdrawPlayer(player, amount);
    }
}
