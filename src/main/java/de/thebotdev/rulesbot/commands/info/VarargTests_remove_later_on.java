package de.thebotdev.rulesbot.commands.info;

import de.thebotdev.rulesbot.util.commandlib.CommandDescription;
import de.thebotdev.rulesbot.util.commandlib.Context;
import de.thebotdev.rulesbot.util.commandlib.RBCommand;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;

@CommandDescription(
        name = "Vararg dinger", triggers = {"var_arg"},
        description = "Vararg", usage = {"v 1 2 3", "v 1"},
        longDescription = "dicks sucken. wo ist das problem",
        hidden = true
)
public class VarargTests_remove_later_on extends RBCommand {
    public void execute(Context context, User arg, String... args) {
        context.send("Arg: " + arg).queue();
        context.send("Args: " + Arrays.toString(args)).queue();

    }
}
