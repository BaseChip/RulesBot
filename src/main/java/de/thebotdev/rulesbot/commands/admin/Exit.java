package de.thebotdev.rulesbot.commands.admin;

import de.thebotdev.rulesbot.util.commandlib.*;

@CommandDescription(
        name = "E X I T",
        triggers = {"exit", "shutdown", "stahpit"},
        description = "stops the bot",
        longDescription = "STOPS THE BOT REALLY FAST WITH AN EXIT STATUS",
        usage = {
                "exit 0",
                "exit 1"
        },
        hidden = true
)
@Checks({Check.DEVELOPER_ONLY})
public class Exit extends RBCommand {
        public void execute(Context context, Integer i){
                System.exit(i);
        }
}
