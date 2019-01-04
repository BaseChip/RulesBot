package de.thebotdev.rulesbot.util;

import net.dv8tion.jda.core.entities.ISnowflake;

public class RandomUtil {

    public static ISnowflake createSnowflake(long id) {
        return () -> id;
    }

    public static long parseLongSafe(String text) {
        try {
            return Long.parseUnsignedLong(text);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
