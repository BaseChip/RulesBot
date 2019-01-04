package de.thebotdev.rulesbot.util.database;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultTransformer<T> {

    T apply(ResultSet resultSet) throws SQLException;
}
