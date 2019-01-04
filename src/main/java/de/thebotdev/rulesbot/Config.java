package de.thebotdev.rulesbot;

public class Config {

    private String token;
    private DatabaseConfig database;
    private StatuspageConfig statuspage;
    private BotlistsConfig botlists;

    public DatabaseConfig getDatabase() {
        return database;
    }

    public BotlistsConfig getBotlists() {
        return botlists;
    }

    public StatuspageConfig getStatuspage() {
        return statuspage;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
