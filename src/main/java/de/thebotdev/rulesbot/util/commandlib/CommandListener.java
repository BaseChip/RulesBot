package de.thebotdev.rulesbot.util.commandlib;

import io.sentry.Sentry;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandListener extends ListenerAdapter {
    private List<RBCommand> commands = new ArrayList<>();
    private Function<Message, String[]> prefixes;
    private boolean mentionPrefix;

    public CommandListener(Function<Message, String[]> prefixes, boolean mentionPrefix) {
        this.prefixes = prefixes;
        this.mentionPrefix = mentionPrefix;
    }

    public void addCommand(RBCommand command) {
        commands.add(command);
    }

    public void findCommands(String pack) {
        Reflections reflections = new Reflections(pack);
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(CommandDescription.class)) {
            if (RBCommand.class.isAssignableFrom(clazz)) {
                try {
                    RBCommand command = (RBCommand) clazz.newInstance();
                    addCommand(command);
                } catch (InstantiationException | IllegalAccessException e) {
                    getLogger("loader").error("Failed to create command " + clazz.getSimpleName(), e);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String[] foundPrefixes = getPrefixes(event);
        String content = (event.getMessage().getContentRaw());
        Optional<String> anyPrefix = Stream.of(foundPrefixes).filter(content::startsWith).findAny();
        if (!anyPrefix.isPresent()) return;
        String prefix = anyPrefix.get();
        content = content.substring(prefix.length());
        String[] x = content.split(" ", 2);
        String command = x[0];
        String argText = "";
        if (x.length > 1) {
            argText = x[1];
        }
        Optional<RBCommand> anyCommand = commands.stream()
                .filter(cmd -> cmd.isTrigger(command))
                .findAny();
        if (!anyCommand.isPresent()) return;
        RBCommand cmd = anyCommand.get();
        String[] args;
        try {
            args = getArgs(cmd, argText);
        } catch (NotEnoughArgumentsException e) {
            Context ctx = new Context(this, event, null, prefix);
            ctx.send(new EmbedBuilder()
                    .setDescription("Missing arguments\n**Usage:**\n" + Arrays.stream(cmd.getDescription().usage()).map(y -> ctx.getPrefix() + y).collect(Collectors.joining("\n")))
                    .setFooter("[] = required | <> = optional", "https://thebotdev.de/img/bot_img.png")
                    .build()).queue();
            e.printStackTrace();
            return;
        }
        new Thread(() -> {
            Context ctx = new Context(this, event, args, prefix);
            List<Check> failedChecks = cmd.runChecks(ctx);
            if (!failedChecks.isEmpty()) {
                ctx.send(failedChecks.stream().map(Check::getDescription).collect(Collectors.joining("\n"))).queue();
                return;
            }
            try {
                cmd.runCommand(ctx, args);
            } catch (ConversionException e) {
                ctx.send(new EmbedBuilder()
                        .setDescription("Wrong argument passed\n**Usage:**\n" + Arrays.stream(cmd.getDescription().usage()).map(y -> ctx.getPrefix() + y).collect(Collectors.joining("\n")))
                        .setFooter("[] = required | <> = optional", "https://thebotdev.de/img/bot_img.png")
                        .build()).queue();
                e.printStackTrace();
            } catch (Exception e) {
                Sentry.capture(e);
            }
        }).start();
    }

    private String[] getPrefixes(MessageReceivedEvent event) {
        ArrayList<String> pre = new ArrayList<>(Arrays.asList(prefixes.apply(event.getMessage())));
        if (this.mentionPrefix) {
            String selfId = event.getJDA().getSelfUser().getId();
            pre.add(String.format("<@%s> ", selfId));
            pre.add(String.format("<@!%s> ", selfId));
        }
        return pre.toArray(new String[0]);
    }

    private String[] getArgs(RBCommand cmd, String argText) throws NotEnoughArgumentsException {
        String[] args;
        if (cmd.isVarArgs()) {
            args = argText.split("\\s+");
        } else
            args = argText.split("\\s+", cmd.getArgCount());
        args = Arrays.stream(args)
                .filter(x -> !x.isEmpty())
                .toArray(String[]::new);
        if (args.length < cmd.getArgCount()) {
            throw new NotEnoughArgumentsException();
        }
        return args;
    }

    public List<RBCommand> getCommands() {
        return this.commands;
    }

    public List<RBCommand> getVisibleCommands() {
        return getCommands().stream().filter(command -> !command.isHidden()).collect(Collectors.toList());
    }
}
