package com.cristian.safertp.command;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

/**
 * Per-world access to /rtp: safertp.world.&lt;world&gt;, open to everyone unless a permissions
 * plugin sets it to false. The node can't live in plugin.yml because world names are only
 * known at runtime, and a node the server has never seen is operators-only.
 */
public final class WorldPermission {

    static final String PREFIX = "safertp.world.";

    private WorldPermission() {
    }

    public static Permission of(String worldName) {
        return new Permission(PREFIX + worldName, "Use /rtp in " + worldName, PermissionDefault.TRUE);
    }

    /** Registers the world's node the first time it is checked. */
    public static String ensureRegistered(PluginManager pluginManager, String worldName) {
        String node = PREFIX + worldName;
        if (pluginManager.getPermission(node) == null) {
            pluginManager.addPermission(of(worldName));
        }
        return node;
    }
}
