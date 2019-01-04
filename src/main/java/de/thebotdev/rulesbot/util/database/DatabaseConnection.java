package de.thebotdev.rulesbot.util.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private DatabaseConnection() {
    }

    public static Connection getConnection(String host, String database, String username, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            return DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s&characterEncoding=utf8&useUnicode=yes", host, database, username, password));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
