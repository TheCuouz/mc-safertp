package com.cristian.safertp.discovery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BiomeDiscoveryTrackerTest {

    @TempDir
    Path tempDir;

    @Test
    void discover_newBiome_returnsTrue() {
        var tracker = new BiomeDiscoveryTracker(tempDir.toFile());
        UUID uuid = UUID.randomUUID();
        assertTrue(tracker.discover(uuid, "plains"));
    }

    @Test
    void discover_sameBiomeTwice_returnsFalse() {
        var tracker = new BiomeDiscoveryTracker(tempDir.toFile());
        UUID uuid = UUID.randomUUID();
        tracker.discover(uuid, "plains");
        assertFalse(tracker.discover(uuid, "plains"));
    }

    @Test
    void discover_differentBiomes_bothReturnTrue() {
        var tracker = new BiomeDiscoveryTracker(tempDir.toFile());
        UUID uuid = UUID.randomUUID();
        assertTrue(tracker.discover(uuid, "plains"));
        assertTrue(tracker.discover(uuid, "forest"));
    }

    @Test
    void discover_independentPerPlayer() {
        var tracker = new BiomeDiscoveryTracker(tempDir.toFile());
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        tracker.discover(a, "plains");
        assertTrue(tracker.discover(b, "plains"));
    }

    @Test
    void getDiscovered_unknownPlayer_returnsEmptySet() {
        var tracker = new BiomeDiscoveryTracker(tempDir.toFile());
        assertTrue(tracker.getDiscovered(UUID.randomUUID()).isEmpty());
    }

    @Test
    void saveAndLoad_preservesDiscoveries() {
        File dataFolder = tempDir.toFile();
        UUID uuid = UUID.randomUUID();

        var tracker1 = new BiomeDiscoveryTracker(dataFolder);
        tracker1.discover(uuid, "plains");
        tracker1.discover(uuid, "forest");
        tracker1.save();

        var tracker2 = new BiomeDiscoveryTracker(dataFolder);
        tracker2.load();

        assertTrue(tracker2.getDiscovered(uuid).contains("plains"));
        assertTrue(tracker2.getDiscovered(uuid).contains("forest"));
        assertEquals(2, tracker2.getDiscovered(uuid).size());
    }

    @Test
    void load_withNoFile_doesNotThrow() {
        var tracker = new BiomeDiscoveryTracker(tempDir.toFile());
        assertDoesNotThrow(tracker::load);
    }
}
