package com.cristian.safertp;

import com.cristian.safertp.finder.LocationFinder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** One test per defect of the 5 October 2026 code review. */
class ReviewFixesTest {

    /** A Nether column: bedrock at 0 and 127, netherrack up to 40, a cave above it, rock again from 60. */
    private static boolean solid(int y) {
        return y <= 40 || y >= 60;
    }

    @Test
    void aNetherTeleportLandsUnderTheRoof() {
        int feet = LocationFinder.standableBelowCeiling(
            ReviewFixesTest::solid, y -> !solid(y), 0, 127);

        assertEquals(41, feet, "on the cave floor, not on top of the bedrock roof at 128");
    }

    @Test
    void aSolidNetherColumnHasNowhereToStand() {
        assertEquals(Integer.MIN_VALUE,
            LocationFinder.standableBelowCeiling(y -> true, y -> false, 0, 127));
    }
}
