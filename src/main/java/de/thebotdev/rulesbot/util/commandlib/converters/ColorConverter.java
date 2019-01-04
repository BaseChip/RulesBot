package de.thebotdev.rulesbot.util.commandlib.converters;

import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.ConversionException;
import de.thebotdev.rulesbot.util.commandlib.Converter;

import java.awt.*;

public class ColorConverter implements Converter<Color> {

    public static final ColorConverter INSTANCE = new ColorConverter();

    protected ColorConverter() {

    }

    @Override
    public Color convert(Context context, String arg) throws ConversionException {
        if (arg.length() == 7 && arg.startsWith("#")) {
            return convertHex(arg.substring(1));
        }
        if (arg.length() == 8 && arg.startsWith("0x")) {
            return convertHex(arg.substring(2));
        }
        if (arg.length() == 6) {
            return convertHex(arg);
        }
        throw new ConversionException("Invalid color code. Use a hex code like `#ffafe0`");
    }

    private Color convertHex(String hex) {
        return new Color(Integer.parseInt(hex, 16));
    }
}
