package de.thebotdev.rulesbot.util.commandlib;

import de.thebotdev.rulesbot.commands.listeners.BotlistsListener;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.Member;

import static de.thebotdev.rulesbot.Constants.*;
import static de.thebotdev.rulesbot.Main.shardManager;

public enum Check {
    DEVELOPER_ONLY() {
        @Override
        public boolean check(Context context) {
            return ADMINS.contains(context.getAuthor().getId());
        }

        @Override
        public String getDescription() {
            return "Just for developer";
        }
    },
    ADMIN_ONLY() {
        @Override
        public boolean check(Context context) {
            return DEVELOPER_ONLY.check(context) ||
                    (context.getMember() != null && context.getMember().hasPermission(Permission.MANAGE_SERVER));
        }

        @Override
        public String getDescription() {
            return "For this command you need `MANAGE_SERVER` permissions on this discord to execute this command";
        }
    },
    BETA_ONLY() {
        @Override
        public boolean check(Context context) {
            Guild thebotdev = shardManager.getGuildById(THEBOTDEV_GUILD);
            Member member = thebotdev.getMember(context.getAuthor());
            if (member == null) return false;
            if (PREMIUM_ONLY.check(context)) return true;
            if (BotlistsListener.hasVoted(member.getUser()).join())
                return true;
            return member.getRoles().stream().map(ISnowflake::getIdLong).anyMatch(id -> BETA_ROLE == id);
        }

        @Override
        public String getDescription() {
            return "This command is just available for **beta** users. Checkout the beta command for more information";
        }
    },
    PREMIUM_ONLY() {
        @Override
        public boolean check(Context context) {
            Guild thebotdev = shardManager.getGuildById(THEBOTDEV_GUILD);
            Member member = thebotdev.getMember(context.getAuthor());
            if (member == null) return false;
            return member.getRoles().stream().map(ISnowflake::getIdLong).anyMatch(id -> PREMIUM_ROLE == id);
        }

        @Override
        public String getDescription() {
            return "This command is only available for **premium** users. Checkout the premium command for more information";
        }
    };

    public abstract boolean check(Context context);

    public abstract String getDescription();


}
