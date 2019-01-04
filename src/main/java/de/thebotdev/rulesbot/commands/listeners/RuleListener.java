package de.thebotdev.rulesbot.commands.listeners;

import de.thebotdev.rulesbot.util.RegisterListener;
import de.thebotdev.rulesbot.util.database.RulesData;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;

import static de.thebotdev.rulesbot.Constants.CROSS;
import static de.thebotdev.rulesbot.Constants.TICK;
import static de.thebotdev.rulesbot.Main.database;

@RegisterListener
public class RuleListener extends ListenerAdapter {


    private void reactionHandler(GenericGuildMessageReactionEvent event, String emoji, Consumer<RulesData> handler) {
        Optional<RulesData> rulesOpt = database.getRules(event.getGuild().getId());
        if (!rulesOpt.isPresent()) return;
        RulesData rules = rulesOpt.get();
        if (!rules.isSetupComplete()) return;
        if (rules.getRulesChannelId() != event.getChannel().getIdLong()) return;
        if (rules.getMessageId() != event.getMessageIdLong()) return;
        if (!event.getReactionEmote().getName().equals(emoji)) return;

        handler.accept(rules);
    }


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getMember().getUser().isBot()) return;
        database.getRules(event.getGuild().getId()).ifPresent(rules -> {
            if (rules.hasJoinMsg()) {
                event.getMember().getUser().openPrivateChannel().complete().sendMessage(new EmbedBuilder()
                        .setDescription(rules.getJoinMsg())
                        .setTitle(String.format("A join message from %s", event.getGuild().getName()))
                        .setColor(0x36393f)
                        .build()).queue();
            }
        });
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getMember().getUser().isBot()) return;
        try {
            reactionHandler(event, TICK, rules -> {
                event.getGuild().getController().addSingleRoleToMember(
                        event.getMember(), event.getGuild().getRoleById(rules.getRoleId()))
                        .reason("Accepted Rules").queue();
                event.getGuild().getController().removeSingleRoleFromMember(
                        event.getMember(), event.getGuild().getRoleById(rules.getRemoveRoleId())).reason("Accepted Rules").queue();
                event.getGuild().getTextChannelById(rules.getLogChannel()).sendMessage(new EmbedBuilder()
                        .setTitle("User Info:")
                        .setColor(0x2ecd71)
                        .setThumbnail(event.getMember().getUser().getAvatarUrl())
                        .addField("Username:", event.getMember().getUser().getName() + " - " + event.getMember().getAsMention(), false)
                        .addField("Discriminator:", event.getMember().getUser().getDiscriminator(), true)
                        .addField("User ID:", event.getMember().getUser().getId(), true)
                        .addField("Action:", "User accepted on the server", true)
                        .setTimestamp(Instant.now()).build()).queue();
            });
            reactionHandler(event, CROSS, rules -> {
                event.getReaction().removeReaction(event.getMember().getUser()).queue();
                if (rules.hasKickmsg()) {
                    event.getMember().getUser().openPrivateChannel().complete().sendMessage(rules.getKickmsg()).complete();
                }
                switch (rules.getAction()) {
                    case KICK:
                        event.getGuild().getController().kick(event.getMember()).reason("denied rules").queue();
                        break;
                    case BAN:
                        event.getGuild().getController().ban(event.getMember(), 7).reason("denied rules").queue();
                        break;
                }
                event.getGuild().getTextChannelById(rules.getLogChannel()).sendMessage(new EmbedBuilder()
                        .setTitle("User Info")
                        .setThumbnail(event.getMember().getUser().getAvatarUrl())
                        .setColor(0xe74c3c)
                        .addField("Username:", event.getMember().getUser().getName() + " - " + event.getMember().getAsMention(), true)
                        .addField("Discriminator:", event.getMember().getUser().getDiscriminator(), true)
                        .addField("User ID:", event.getMember().getUser().getId(), true)
                        .addField("Action:", rules.getAction().name(), true)
                        .setTimestamp(Instant.now()).build()).queue();
            });

            database.getReactionAction(event.getReactionEmote(),
                    event.getMessageId())
                    .ifPresent(roleId ->
                            event.getGuild().getController().addSingleRoleToMember(
                                    event.getMember(), event.getGuild().getRoleById(roleId))
                                    .reason("reaction role").queue()
                    );
        } catch (PermissionException e) {
        }
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        if (event.getMember().getUser().isBot()) return;
        try {
            reactionHandler(event, TICK, rules -> {
                event.getGuild().getController().removeSingleRoleFromMember(
                        event.getMember(), event.getGuild().getRoleById(rules.getRoleId()))
                        .reason("removed Rules reaction").queue();
                event.getGuild().getTextChannelById(rules.getLogChannel()).sendMessage(new EmbedBuilder()
                        .setTitle("User Info:")
                        .setThumbnail(event.getMember().getUser().getAvatarUrl())
                        .setColor(0xe74c3c)
                        .addField("Username:", event.getMember().getUser().getName() + " - " + event.getMember().getAsMention(), false)
                        .addField("Discriminator:", event.getMember().getUser().getDiscriminator(), true)
                        .addField("User ID:", event.getMember().getUser().getId(), true)
                        .addField("Action:", "removed role", true)
                        .setTimestamp(Instant.now()).build()).queue();
            });
            database.getReactionAction(event.getReactionEmote(),
                    event.getMessageId())
                    .ifPresent(roleId ->
                            event.getGuild().getController().removeSingleRoleFromMember(
                                    event.getMember(), event.getGuild().getRoleById(roleId))
                                    .reason("reaction role").queue());
        } catch (PermissionException e) {
        }
    }
}
