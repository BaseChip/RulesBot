package de.thebotdev.rulesbot.util.commandlib;

import net.dv8tion.jda.core.entities.Message;

@FunctionalInterface
public interface Converter<T> {
    default T convert(Context context, Message mes) throws ConversionException {
        return convert(context, mes.getContentRaw());
    }

    T convert(Context context, String arg) throws ConversionException;
}
