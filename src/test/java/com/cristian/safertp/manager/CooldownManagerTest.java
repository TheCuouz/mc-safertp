package com.cristian.safertp.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CooldownManagerTest {

    private CooldownManager manager;
    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        manager = new CooldownManager();
    }

    @Test
    void noActiveCooldown_returnsZero() {
        assertEquals(0L, manager.getRemaining(uuid));
    }

    @Test
    void activeCooldown_returnsPositive() {
        manager.setCooldown(uuid, 300);
        assertTrue(manager.getRemaining(uuid) > 0);
        assertTrue(manager.getRemaining(uuid) <= 300);
    }

    @Test
    void isOnCooldown_trueWhenActive() {
        manager.setCooldown(uuid, 300);
        assertTrue(manager.isOnCooldown(uuid));
    }

    @Test
    void isOnCooldown_falseWithNoCooldown() {
        assertFalse(manager.isOnCooldown(uuid));
    }

    @Test
    void clearCooldown_removesEntry() {
        manager.setCooldown(uuid, 300);
        manager.clearCooldown(uuid);
        assertFalse(manager.isOnCooldown(uuid));
    }

    @Test
    void zeroCooldown_notOnCooldown() throws InterruptedException {
        manager.setCooldown(uuid, 0);
        Thread.sleep(50);
        assertFalse(manager.isOnCooldown(uuid));
    }
}
