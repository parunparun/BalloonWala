package com.example.balloonwala;

/**
 * Constants used for passing data between screens via Intents.
 * <p>
 * Keeps Intent extra keys in one place so both the sending
 * screen (MainActivity) and receiving screen (PuzzleActivity)
 * reference a neutral shared location — no hidden dependencies.
 */
public class NavigationConstants {

    /** Intent extra key for passing the puzzle column count. */
    public static final String COLUMNS = "com.example.balloonwala.EXTRA_COLUMNS";

    // Private constructor to prevent instantiation
    private NavigationConstants() {}
}
