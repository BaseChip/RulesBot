package de.thebotdev.rulesbot.util.database;

import de.thebotdev.rulesbot.commands.guild.ActionEnum;

import java.sql.ResultSet;
import java.sql.SQLException;

import static de.thebotdev.rulesbot.util.RandomUtil.parseLongSafe;

public class RulesData {
    private long serverId;
    private String serverName;
    private long rulesChannelId;
    private String ruleText;
    private boolean hasJoinMsg;
    private String joinMsg;
    private long removeRoleId;
    private String kickmsg;
    private boolean hasKickmsg;
    private long logChannel;
    private long roleId;
    private long messageId;
    private ActionEnum action;
    private boolean setupComplete;
    private long reportChannel;

    // serverid, servername, ruleschannel, ruletext, joinmsg, shjoin, removeroleid,
    // kickmsg, kick, logchannel, roleid, messageid, action, setupcomplete, reportchannel
    public RulesData(ResultSet result) throws SQLException {
        serverId = parseLongSafe(result.getString(1));
        serverName = result.getString(2);
        rulesChannelId = parseLongSafe(result.getString(3));
        ruleText = result.getString(4);
        joinMsg = result.getString(5);
        hasJoinMsg = parseBoolean(result.getString(6));
        removeRoleId = parseLongSafe(result.getString(7));
        kickmsg = result.getString(8);
        hasKickmsg = parseBoolean(result.getString(9));
        logChannel = parseLongSafe(result.getString(10));
        roleId = parseLongSafe(result.getString(11));
        messageId = parseLongSafe(result.getString(12));
        action = parseActionEnum(result.getString(13));
        setupComplete = "YES".equals(result.getString(14));
        reportChannel = parseLongSafe(result.getString(15));
    }

    private ActionEnum parseActionEnum(String result) {
        if (result == null) return null;
        return ActionEnum.valueOf(result.toUpperCase());
    }

    private boolean parseBoolean(String result) {
        if (result == null) return false;
        return Boolean.parseBoolean(result.toLowerCase());
    }

    public String getJoinMsg() {
        return joinMsg;
    }

    public String getKickmsg() {
        return kickmsg;
    }

    public ActionEnum getAction() {
        return action;
    }

    public boolean isSetupComplete() {
        return setupComplete;
    }

    public long getReportChannel() {
        return reportChannel;
    }

    public long getMessageId() {
        return messageId;
    }

    public long getRoleId() {
        return roleId;
    }

    public long getLogChannel() {
        return logChannel;
    }

    public boolean hasKickmsg() {
        return hasKickmsg;
    }

    public long getRemoveRoleId() {
        return removeRoleId;
    }

    public boolean hasJoinMsg() {
        return hasJoinMsg;
    }


    public long getServerId() {
        return serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public long getRulesChannelId() {
        return rulesChannelId;
    }

    public String getRuleText() {
        return ruleText;
    }
}
