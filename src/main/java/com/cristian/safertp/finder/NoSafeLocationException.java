package com.cristian.safertp.finder;

public class NoSafeLocationException extends RuntimeException {
    public NoSafeLocationException(String worldName, int attempts) {
        super("No safe location found in world '" + worldName + "' after " + attempts + " attempts.");
    }
}
