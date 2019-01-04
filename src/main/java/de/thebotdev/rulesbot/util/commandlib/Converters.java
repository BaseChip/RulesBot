package de.thebotdev.rulesbot.util.commandlib;

import de.thebotdev.rulesbot.util.commandlib.converters.*;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static de.thebotdev.rulesbot.util.RandomUtil.createSnowflake;

public class Converters {
    private static Map<Class, Converter> converters = new HashMap<>();


    static {
        registerConverter(String.class, (context, arg) -> arg);
        registerConverter(TextChannel.class, TextChannelConverter.INSTANCE);
        registerConverter(Boolean.class, (context, arg) -> convertBoolean(arg));
        registerConverter(Boolean.TYPE, (context, arg) -> convertBoolean(arg));
        registerConverter(Long.class, (context, arg) -> Long.parseLong(arg));
        registerConverter(Long.TYPE, (context, arg) -> Long.parseLong(arg));
        registerConverter(Integer.class, (context, arg) -> Integer.valueOf(arg));
        registerConverter(Integer.TYPE, (context, arg) -> Integer.parseInt(arg));
        registerConverter(Color.class, ColorConverter.INSTANCE);
        registerConverter(User.class, UserConverter.INSTANCE);
        registerConverter(ISnowflake.class, (context, arg) -> createSnowflake(IDConverter.INSTANCE.convert(context, arg)));
        registerConverter(Role.class, RoleConverter.INSTANCE);
    }

    @NotNull
    private static Boolean convertBoolean(String arg) throws ConversionException {
        if (arg.equalsIgnoreCase("yes")) return true;
        if (arg.equalsIgnoreCase("y")) return true;
        if (arg.equalsIgnoreCase("no")) return false;
        if (arg.equalsIgnoreCase("n")) return false;
        throw new ConversionException();
    }


    public static <T> void registerConverter(Class<T> clazz, Converter<T> converter) {
        converters.put(clazz, converter);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Converter<T> findConverter(Class<T> clazz) throws NoConverterFoundException {
        Converter<T> converter = ((Converter<T>) converters.get(clazz));
        if (converter == null) {
            throw new NoConverterFoundException(clazz);
        }
        return converter;
    }


    public static <T> T convert(Class<T> clazz, Context context, String arg) throws NoConverterFoundException, ConversionException {
        return findConverter(clazz).convert(context, arg);
    }
}
