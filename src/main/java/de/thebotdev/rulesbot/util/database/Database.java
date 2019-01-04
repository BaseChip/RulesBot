package de.thebotdev.rulesbot.util.database;


import de.thebotdev.rulesbot.DatabaseConfig;
import de.thebotdev.rulesbot.commands.guild.ActionEnum;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.thebotdev.rulesbot.util.RandomUtil.parseLongSafe;
import static org.slf4j.LoggerFactory.getLogger;

public class Database {
    public static final String FIND_PREFIX = "SELECT prefix FROM server_settings WHERE gid = ?;";
    private static final String FIND_RULES_META = "SELECT serverid, servername, ruleschannel, ruletext, joinmsg, " +
            "shjoin, removeroleid, kickmsg, kick, logchannel, roleid, messageid, action, setupcomplete, " +
            "reportchannel FROM server_data WHERE serverid = ? LIMIT 1;";
    private static final String FIND_reactionactionS = "SELECT role_id FROM reactionaction WHERE emoji = ? AND message_id = ?;";

    Connection connection;

    public Database(String host, String database, String username, String password) {
        connection = DatabaseConnection.getConnection(host, database, username, password);

    }

    public Database(DatabaseConfig database) {
        this(database.getHost(), database.getDatabase(), database.getUser(), database.getPassword());
    }

    public <T> T findSingleRow(String query, PreparedStatementEnscriber prepare, ResultTransformer<T> mapper) {
        PreparedStatement preparedStatement = prepareStatement(query);
        try {
            prepare.accept(preparedStatement);
        } catch (Exception e) {
            getLogger("database").error("", e);
            return null;
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next())
                return mapper.apply(resultSet);
        } catch (SQLException e) {
            getLogger("database").error("", e);
        }
        return null;
    }


    public PreparedStatement prepareStatement(String text) {
        try {
            return connection.prepareStatement(text);
        } catch (SQLException e) {
            getLogger("database").error(String.format("Invalid SQL \"%s\"", text), e);
        }
        return null;
    }

    public void setRules(String gid, TextChannel rulesChannel, String rulesText, ActionEnum action, TextChannel logs, Role role, Message rulesmessage, boolean joinNotification, String joinNotificationText, boolean declineNotification, String declineNotificationText, Role removerole, String servername) {
        try {
            upsert("server_data", "serverid", gid, new HashMap<String, String>() {{
                put("servername", servername);
                put("ruleschannel", rulesChannel.getId());
                put("ruletext", rulesText);
                put("joinmsg", joinNotificationText);
                put("shjoin", "" + joinNotification);
                put("removeroleid", removerole != null ? removerole.getId() : "0");
                put("kickmsg", declineNotificationText);
                put("kick", "" + declineNotification);
                put("logchannel", logs.getId());
                put("roleid", role.getId());
                put("messageid", rulesmessage.getId());
                put("action", action.name().toLowerCase());
                put("setupcomplete", "YES");
            }});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setRulesmessage(String gid, String message) {
        try {
            upsert("server_data", "serverid", gid, new HashMap<String, String>() {{
                put("messageid", message);
            }});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setRulestext(String gid, String rulesText) {
        try {
            upsert("server_data", "serverid", gid, new HashMap<String, String>() {{
                put("ruletext", rulesText);
            }});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Long> getReactionAction(MessageReaction.ReactionEmote emoji, String message) {
        String dbEmoji;
        if (emoji.isEmote()) {
            dbEmoji = emoji.getId();
        } else {
            dbEmoji = Base64.getEncoder().encodeToString(emoji.getName().getBytes());
        }
        return Optional.ofNullable(findSingleRow(FIND_reactionactionS, stmt -> {
            stmt.setString(1, dbEmoji);
            stmt.setString(2, message);
        }, res -> parseLongSafe(res.getString(1))));
    }

    public void addReactionAction(String message, MessageReaction.ReactionEmote emoji, String role) {
        try {
            String dbEmoji;
            if (emoji.isEmote()) {
                dbEmoji = emoji.getId();
            } else {
                dbEmoji = Base64.getEncoder().encodeToString(emoji.getName().getBytes());
            }
            PreparedStatement select = prepareStatement("SELECT id FROM reactionaction WHERE emoji = ? AND message_id = ?");
            select.setString(1, dbEmoji);
            select.setString(2, message);
            try (ResultSet resultSet = select.executeQuery()) {
                resultSet.next();
                String id = resultSet.getString(1);
                System.out.println("idi" + id);
                PreparedStatement update = prepareStatement("DELETE FROM reactionaction WHERE id = ?");
                update.setString(1, id);
                update.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            PreparedStatement insert = prepareStatement("INSERT INTO reactionaction (emoji, message_id, role_id) VALUES (?, ?, ?);");
            insert.setString(1, dbEmoji);
            insert.setString(2, message);
            insert.setString(3, role);
            insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Optional<String> getPrefix(String gid) {
        return Optional.ofNullable(findSingleRow(FIND_PREFIX, stmt -> stmt.setString(1, gid), res -> res.getString("prefix")));
    }

    public Optional<RulesData> getRules(String gid) {
        return Optional.ofNullable(findSingleRow(FIND_RULES_META, stmt -> stmt.setString(1, gid), RulesData::new));
    }

    public void upsert(String tablename, String idName, String idValue, Map<String, String> values) throws
            SQLException {
        String[] names = Stream.concat(Stream.of(idName), values.keySet().stream().sorted()).map(String::valueOf).toArray(String[]::new);
        String upsert = String.format("INSERT INTO %s (%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s",
                tablename,
                String.join(", ", names),
                Arrays.stream(names).map(ignored -> "?").collect(Collectors.joining(", ")),
                values.keySet().stream().sorted().map(name -> name + " = ?").collect(Collectors.joining(", "))
        );
        PreparedStatement preparedStatement = prepareStatement(upsert);
        preparedStatement.setString(1, idValue);
        String[] vals = values.keySet().stream().sorted().map(values::get).toArray(String[]::new);
        for (int i = 0; i < vals.length; i++) {
            preparedStatement.setString(i + 2, vals[i]);
            preparedStatement.setString(i + 2 + vals.length, vals[i]);
        }
        preparedStatement.execute();
    }

    public void setPrefix(String gid, String prefix) {
        try {
            upsert("server_settings", "gid", gid, new HashMap<String, String>() {{
                put("prefix", prefix);
            }});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

