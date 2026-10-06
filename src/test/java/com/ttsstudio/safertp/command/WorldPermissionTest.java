package com.ttsstudio.safertp.command;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class WorldPermissionTest {

    /** Spigot's Permission constructor asks the server who is subscribed to the node. */
    @BeforeAll
    static void fakeServer() {
        if (Bukkit.getServer() != null) return;
        PluginManager subscriptions = (PluginManager) Proxy.newProxyInstance(PluginManager.class.getClassLoader(),
            new Class<?>[]{PluginManager.class}, (proxy, method, args) -> switch (method.getName()) {
                case "getPermissionSubscriptions" -> Collections.emptySet();
                default -> null;
            });
        Bukkit.setServer((Server) Proxy.newProxyInstance(Server.class.getClassLoader(),
            new Class<?>[]{Server.class}, (proxy, method, args) -> switch (method.getName()) {
                case "getPluginManager" -> subscriptions;
                case "getLogger" -> Logger.getLogger("test");
                case "getName", "getVersion", "getBukkitVersion" -> "test";
                default -> null;
            }));
    }

    /** Only getPermission/addPermission are used; anything else would be a surprise. */
    private static PluginManager fakeManager(Map<String, Permission> registry) {
        return (PluginManager) Proxy.newProxyInstance(PluginManager.class.getClassLoader(),
            new Class<?>[]{PluginManager.class}, (proxy, method, args) -> switch (method.getName()) {
                case "getPermission" -> registry.get(((String) args[0]).toLowerCase(Locale.ROOT));
                case "addPermission" -> {
                    Permission p = (Permission) args[0];
                    registry.put(p.getName().toLowerCase(Locale.ROOT), p);
                    yield null;
                }
                default -> throw new UnsupportedOperationException(method.getName());
            });
    }

    @Test
    void worldNode_isOpenToEveryoneByDefault() {
        Permission p = WorldPermission.of("world");
        assertEquals("safertp.world.world", p.getName());
        assertEquals(PermissionDefault.TRUE, p.getDefault());
    }

    @Test
    void ensureRegistered_addsTheNodeOnce() {
        Map<String, Permission> registry = new HashMap<>();
        PluginManager pm = fakeManager(registry);

        String node = WorldPermission.ensureRegistered(pm, "world_nether");
        Permission first = registry.get(node);
        WorldPermission.ensureRegistered(pm, "world_nether");

        assertEquals("safertp.world.world_nether", node);
        assertSame(first, registry.get(node));
        assertEquals(PermissionDefault.TRUE, first.getDefault());
    }

    @Test
    void ensureRegistered_keepsANodeTheServerAlreadyHas() {
        Map<String, Permission> registry = new HashMap<>();
        Permission custom = new Permission("safertp.world.world", PermissionDefault.OP);
        registry.put("safertp.world.world", custom);

        WorldPermission.ensureRegistered(fakeManager(registry), "world");

        assertSame(custom, registry.get("safertp.world.world"));
    }
}
