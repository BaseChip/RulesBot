package de.thebotdev.rulesbot.util.commandlib.converters;

import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.ConversionException;
import de.thebotdev.rulesbot.util.commandlib.Converter;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

public class TextChannelConverter implements Converter<TextChannel> {
    public static final TextChannelConverter INSTANCE = new TextChannelConverter();

    protected TextChannelConverter() {
    }


    @Override
    public TextChannel convert(Context context, String arg) throws ConversionException {
        try {
            long id = IDConverter.INSTANCE.convert(context, arg);
            TextChannel chan = context.getJDA().getTextChannelById(id);
            if (chan != null) {
                return chan;
            }
        } catch (ConversionException e) {
        }
        return context.getJDA().getTextChannelsByName(arg, true).stream().findFirst().orElseThrow(ConversionException::new);

    }
}
