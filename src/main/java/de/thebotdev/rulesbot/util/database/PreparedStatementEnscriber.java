package de.thebotdev.rulesbot.util.database;

import java.sql.PreparedStatement;

@FunctionalInterface
public interface PreparedStatementEnscriber {

    void accept(PreparedStatement preparedStatement) throws Exception;
}
