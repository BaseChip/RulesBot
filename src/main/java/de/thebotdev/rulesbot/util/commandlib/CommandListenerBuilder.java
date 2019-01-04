package de.thebotdev.rulesbot.util.commandlib;

import net.dv8tion.jda.core.entities.Message;

import java.util.function.Function;

public class CommandListenerBuilder {

    private Function<Message, String[]> prefixes;
    private boolean mentionPrefix;

    public CommandListenerBuilder setPrefixes(Function<Message, String[]> prefixes) {
        this.prefixes = prefixes;
        return this;
    }

    public CommandListenerBuilder setPrefix(Function<Message, String> prefixes) {
        return setPrefixes(message -> new String[]{prefixes.apply(message)});
    }

    public CommandListenerBuilder setAllowMentionPrefix(boolean mentionPrefix) {
        this.mentionPrefix = mentionPrefix;
        return this;
    }

    public CommandListener build() {
        return new CommandListener(prefixes, mentionPrefix);
    }

    public CommandListenerBuilder setPrefixes(String... prefixes) {
        this.prefixes = ignored -> prefixes;
        return this;
    }
}
