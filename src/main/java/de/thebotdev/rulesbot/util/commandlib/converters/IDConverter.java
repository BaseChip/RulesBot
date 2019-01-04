package de.thebotdev.rulesbot.util.commandlib.converters;

import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.ConversionException;
import de.thebotdev.rulesbot.util.commandlib.Converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDConverter implements Converter<Long> {
    private static final Pattern ID_PATTERN = Pattern.compile("[0-9]+");
    public static IDConverter INSTANCE = new IDConverter();

    protected IDConverter() {
    }

    @Override
    public Long convert(Context ctx, String arg) throws ConversionException {
        Matcher matcher = ID_PATTERN.matcher(arg);
        if (!matcher.find())
            throw new ConversionException();
        return Long.valueOf(matcher.group());
    }
}
