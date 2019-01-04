package de.thebotdev.rulesbot.util.database;


import de.thebotdev.rulesbot.DatabaseConfig;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class Database_Stats {
    Connection connection;

    public Database_Stats(String host, String database, String username, String password) {
        connection = DatabaseConnection.getConnection(host, database, username, password);

    }

    public Database_Stats(DatabaseConfig database) {
        this(database.getHost(), database.getDatabase2(), database.getUser(), database.getPassword());
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

    public void setServerData(long cpu, double ram) {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            upsert("server_stats", "timestamp", timestamp.toString(), new HashMap<String, String>() {{
                put("cpu", String.valueOf(cpu));
                put("ram", String.valueOf(ram));
            }});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDiscordData(long guilds, long users, double ping) {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            upsert("discord_stats", "timestamp", timestamp.toString(), new HashMap<String, String>() {{
                put("guilds", String.valueOf(guilds));
                put("user", String.valueOf(users));
                put("ping", String.valueOf(ping));
            }});
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}

