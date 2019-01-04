package de.thebotdev.rulesbot.commands.guild;

import de.thebotdev.rulesbot.util.commandlib.Converters;

import static de.thebotdev.rulesbot.Constants.BOOT;
import static de.thebotdev.rulesbot.Constants.HAMMER;

public enum ActionEnum {
    KICK, BAN;

    static {
        Converters.registerConverter(ActionEnum.class, (context, arg) -> ActionEnum.valueOf(arg.toUpperCase()));
    }

    public static ActionEnum fromEmote(String name) {
        if (name.equals(HAMMER)) return BAN;
        if (name.equals(BOOT)) return KICK;
        throw new RuntimeException("Invalid action emote " + name);
    }
}
