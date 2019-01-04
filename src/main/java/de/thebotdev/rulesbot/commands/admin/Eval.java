package de.thebotdev.rulesbot.commands.admin;

import de.thebotdev.rulesbot.util.apis.Hastebin;
import de.thebotdev.rulesbot.util.commandlib.*;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.requests.RestAction;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static de.thebotdev.rulesbot.Main.shardManager;
import static org.slf4j.LoggerFactory.getLogger;

@CommandDescription(
        name = "eval", triggers = {"eval", "evaluate", "exec", "berechne"},
        longDescription = "Nope", description = "nope",
        usage = {},
        hidden = true
)
@Checks(Check.DEVELOPER_ONLY)
public class Eval extends RBCommand {
    ScriptEngineFactory sef = new NashornScriptEngineFactory();

    public void execute(Context ctx, String code) {
        ScriptEngine se = sef.getScriptEngine();
        try {
            se.eval("var imports = new JavaImporter(" +
                    "java.nio.file," +
                    "java.lang," +
                    "java.lang.management," +
                    "java.text," +
                    "java.sql," +
                    "java.util," +
                    "java.time," +
                    "java.time.format," +
                    "Packages.net.dv8tion.jda.core," +
                    "Packages.net.dv8tion.jda.core.entities," +
                    "Packages.de.romjaki.discord.jda" +
                    ");");
        } catch (Throwable e) {
            replyCode(ctx, "Error during init", stringifyError(e));
        }

        se.put("sm", shardManager);
        se.put("jda", ctx.getEvent().getJDA());
        se.put("event", ctx.getEvent());
        se.put("message", ctx.getEvent().getMessage());
        se.put("guild", ctx.getEvent().getGuild());
        se.put("channel", ctx.getEvent().getChannel());
        se.put("author", ctx.getEvent().getAuthor());
        Object ret;
        try {
            ret = se.eval("" +
                    "{" +
                    "   with (imports) {" +
                    "       " + code +
                    "   }" +
                    "}");
        } catch (ScriptException e) {
            replyCode(ctx, "Error during execution", stringifyError(e));
            return;
        }

        if (ret instanceof RestAction) {
            ret = ((RestAction) ret).complete();
        }
        if (ret instanceof Stream) {
            ret = ((Stream) ret).toArray();
        }

        if (ret instanceof Object[]) {
            ret = Arrays.deepToString((Object[]) ret);
        }

        replyCode(ctx, "Success", Objects.toString(ret));

    }

    private void replyCode(Context ctx, String title, String code) {
        try {
            ctx.send(new EmbedBuilder()
                    .setTitle(title)
                    .setDescription(String.format("```java\n%s\n```", code.substring(0, Math.min(500, code.length())) + (
                            code.length() > 500 ? "\n[...]" : "")))
                    .addField("Hastebin url", String.format("https://hastebin.com/%s.java", Hastebin.publish(code)), true)
                    .build()).queue();
        } catch (IOException e) {
            ctx.send(new EmbedBuilder()
                    .setTitle("Unable to upload code block to hastebin.")
                    .build()).queue();
            getLogger("database").error("Unable to upload to hastebin", e);
        }
    }

    private String stringifyError(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        t.printStackTrace(writer);
        sw.flush();
        return sw.getBuffer().toString();
    }
}
