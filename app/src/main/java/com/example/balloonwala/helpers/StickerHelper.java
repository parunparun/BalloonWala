package com.example.balloonwala.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Manages the Sticker Book reward system.
 * Stickers are represented as Emojis and persisted in SharedPreferences.
 */
public class StickerHelper {
    private static final String PREF_NAME = "BalloonWalaStickers";
    private static final String KEY_UNLOCKED = "unlocked_stickers";

    private final List<String> ALL_STICKERS = Arrays.asList(
            "🦁", "🐯", "🐻", "🐼", "🐨", "🐮", "🐷", "🐸", "🐵", "🐔",
            "🐧", "🐦", "🐤", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗", "🐴",
            "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜", "🦗", "🕷", "🐢",
            "🐍", "🦎", "🦖", "🦕", "🐙", "🦑", "🦐", "🦞", "🦀", "🐡",
            "🐠", "🐟", "🐬", "🐳", "🐋", "🦈", "🐊", "🐅", "🐆", "🦓",
            "🐘", "🦏", "🦛", "🐪", "🐫", "🦒", "🐃", "🐂", "🐄", "🐎",
            "🐕", "🐩", "🐈", "🐓", "🦃", "🕊", "🐇", "🐁", "🐀", "🐿",
            "🚀", "🛸", "🪐", "🌍", "🌕", "☄️", "✨", "🍎", "🍕", "🍦"
    );

    private final SharedPreferences prefs;

    public StickerHelper(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns the set of stickers already earned by the player.
     */
    public Set<String> getUnlockedStickers() {
        return prefs.getStringSet(KEY_UNLOCKED, new HashSet<>());
    }

    /**
     * Unlocks a random sticker that the player doesn't have yet.
     * Returns the new sticker, or null if all stickers are already unlocked.
     */
    public String unlockRandomSticker() {
        List<String> locked = getLockedStickers();
        if (locked.isEmpty()) return null;

        String newSticker = locked.get(new Random().nextInt(locked.size()));
        unlockSticker(newSticker);
        return newSticker;
    }

    /**
     * Unlocks a specific sticker.
     */
    public void unlockSticker(String sticker) {
        Set<String> unlocked = new HashSet<>(getUnlockedStickers());
        unlocked.add(sticker);
        prefs.edit().putStringSet(KEY_UNLOCKED, unlocked).apply();
    }

    /**
     * Returns a list of stickers that are NOT yet unlocked.
     */
    public List<String> getLockedStickers() {
        Set<String> unlocked = getUnlockedStickers();
        List<String> locked = new ArrayList<>();
        for (String s : ALL_STICKERS) {
            if (!unlocked.contains(s)) {
                locked.add(s);
            }
        }
        return locked;
    }

    /**
     * Returns 3 random locked stickers for the player to choose from.
     */
    public List<String> getRewardOptions(int count) {
        List<String> locked = getLockedStickers();
        Collections.shuffle(locked);
        return locked.subList(0, Math.min(count, locked.size()));
    }

    public List<String> getAllStickers() {
        return ALL_STICKERS;
    }
}
