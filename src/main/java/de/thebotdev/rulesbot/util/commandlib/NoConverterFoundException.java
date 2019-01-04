package de.thebotdev.rulesbot.util.commandlib;

public class NoConverterFoundException extends Exception {
    public <T> NoConverterFoundException(Class<T> clazz) {
        super("No converter found for class: " + clazz.getCanonicalName());
    }
}
