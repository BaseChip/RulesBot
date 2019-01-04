package de.thebotdev.rulesbot.util.commandlib.converters;

import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.ConversionException;
import de.thebotdev.rulesbot.util.commandlib.Converter;
import net.dv8tion.jda.core.entities.Role;

public class RoleConverter implements Converter<Role> {
    public static final RoleConverter INSTANCE = new RoleConverter();

    protected RoleConverter() {
    }

    @Override
    public Role convert(Context context, String arg) throws ConversionException {
        try {
            long id = IDConverter.INSTANCE.convert(context, arg);
            Role r = context.getJDA().getRoleById(id);
            if (r != null) {
                return r;
            }
        } catch (ConversionException e) {
        }
        return context.getJDA().getRolesByName(arg, true).stream().findFirst().orElseThrow(ConversionException::new);
    }
}
