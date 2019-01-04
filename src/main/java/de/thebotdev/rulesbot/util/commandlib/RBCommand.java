package de.thebotdev.rulesbot.util.commandlib;


import io.sentry.Sentry;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.thebotdev.rulesbot.util.commandlib.ArgumentUtil.getArguments;

public abstract class RBCommand {

    private Method invoke;
    private int argCount;
    private boolean varArgs;
    private CommandDescription description;
    private Checks checks;

    public RBCommand() {
        invoke = Stream.of(getClass().getMethods())
                .filter(method -> method.getName().equals("execute"))
                .filter(method -> Context.class.isAssignableFrom(method.getParameterTypes()[0]))
                .findAny().orElseThrow(() -> new RuntimeException("Please implement a method named `execute` in " + getClass().getSimpleName()));
        varArgs = invoke.isVarArgs();
        argCount = invoke.getParameterCount() - 1 - (varArgs ? 1 : 0);
        invoke.setAccessible(true);
        description = getClass().getAnnotation(CommandDescription.class);
        checks = getClass().getAnnotation(Checks.class);
    }

    public final void runCommand(Context context, String[] args) throws NoConverterFoundException, ConversionException, NotEnoughArgumentsException {
        Object[] finalArgs = getArguments(context, args, argCount, isVarArgs(), invoke);
        try {
            invoke.invoke(this, finalArgs);
        } catch (IllegalAccessException e) {
            Sentry.capture(e);
        } catch (InvocationTargetException e) {
            Sentry.capture(e);
            context.send(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("There occurred an error during command execution")
                    .setFooter("Please contact our developers.", null)
                    .build()).queue();
        }
    }


    public List<Check> runChecks(Context context) {
        return Stream.of(getChecks()).filter(func -> !func.check(context)).collect(Collectors.toList());
    }

    public Check[] getChecks() {
        if (this.checks == null) {
            return new Check[0];
        }
        return checks.value();
    }

    public String getName() {
        return description.name();
    }

    public int getArgCount() {
        return argCount;
    }

    public CommandDescription getDescription() {
        return description;
    }

    public String getShortDescription() {
        return description.description();
    }

    public String getLongDescription() {
        return description.longDescription();
    }

    public boolean isVarArgs() {
        return varArgs;
    }

    public String[] getTriggers() {
        return description.triggers();
    }

    public boolean isTrigger(String text) {
        return Stream.of(description.triggers()).anyMatch(text::equalsIgnoreCase);
    }

    public boolean isHidden() {
        return getDescription().hidden();
    }
}
