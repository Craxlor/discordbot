package com.github.craxlor.discordbot.database.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.craxlor.discordbot.database.entity.DiscordServer;

public class DBGuildHandler implements EntityHandler<DiscordServer> {

    @Override
    public boolean insert(DiscordServer entity) {
        String sql = "INSERT INTO guilds(guild_id, musicLog_id, name, modules, colorHex) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, entity.getGuild_id());
            preparedStatement.setLong(2, entity.getMusicLog_id());
            preparedStatement.setString(3, entity.getName());
            preparedStatement.setString(4, entity.getModules());
            preparedStatement.setString(5, entity.getColorHex());
            preparedStatement.executeUpdate();
            logger.info("inserted new guild: " + entity.getName() + " | " + entity.getGuild_id());
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public DiscordServer getEntity(long id) {
        String sql = "SELECT * FROM guilds WHERE guild_id = ?";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return map(resultSet);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean update(DiscordServer entity) {
        String sql = "UPDATE guilds SET musicLog_id = ?, name = ?, modules = ?, colorHex = ? WHERE guild_id = ?";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, entity.getMusicLog_id());
            preparedStatement.setString(2, entity.getName());
            preparedStatement.setString(3, entity.getModules());
            preparedStatement.setString(4, entity.getColorHex());
            preparedStatement.setLong(5, entity.getGuild_id());
            preparedStatement.executeUpdate();
            logger.info("updated guild: " + entity.getName() + " | " + entity.getGuild_id());
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    /**
     * not implemented
     */
    @Override
    public boolean remove(long id) {
        return false;
    }

    @Override
    public DiscordServer mapFrom(ResultSet rs) throws SQLException {
        DiscordServer discordServer = new DiscordServer();
        discordServer.setGuild_id(rs.getLong("guild_id"));
        discordServer.setMusicLog_id(rs.getLong("musicLog_id"));
        discordServer.setName(rs.getString("name"));
        discordServer.setModules(rs.getString("modules"));
        discordServer.setColorHex(rs.getString("colorHex"));
        return discordServer;
    }

}
