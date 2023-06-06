package com.github.craxlor.discordbot.database.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Nullable;

import com.github.craxlor.discordbot.database.entity.AutoroomChannel;

public class DBAutoroomChannelHandler implements EntityHandler<AutoroomChannel> {

    @Override
    public boolean insert(AutoroomChannel entity) {
        String sql = "INSERT INTO autoroomChannels(channel_id, trigger_id, guild_id) VALUES(?,?,?)";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, entity.getChannel_id());
            preparedStatement.setLong(2, entity.getTrigger_id());
            preparedStatement.setLong(3, entity.getGuild_id());
            preparedStatement.executeUpdate();
            logger.info("inserted new autoroomChannel");
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public AutoroomChannel getEntity(long id) {
        String sql = "SELECT * FROM autoroomChannels WHERE channel_id = ?";
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

    /**
     * not implemented
     */
    @Override
    public boolean update(AutoroomChannel entity) {
        return false;
    }

    @Override
    public boolean remove(long id) {
        String sql = "DELETE FROM autoroomChannels WHERE channel_id = ?";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            logger.info("deleted an autoroomChannel");
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public AutoroomChannel mapFrom(ResultSet rs) throws SQLException {
        AutoroomChannel autoroomChannel = new AutoroomChannel();
        autoroomChannel.setChannel_id(rs.getLong("channel_id"));
        autoroomChannel.setGuild_id(rs.getLong("guild_id"));
        autoroomChannel.setTrigger_id(rs.getLong("trigger_id"));
        return autoroomChannel;
    }

    public int countAutoroomChannelsByTrigger(long trigger_id) {
        String sql = "SELECT COUNT(*) AS count FROM autoroomChannels WHERE trigger_id = ?";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, trigger_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getInt("count");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return -1;
        }
    }

    @Nullable
    public List<AutoroomChannel> getAutoroomChannelsByGuild(long guild_id) {
        String sql = "SELECT * FROM autoroomChannels WHERE guild_id = ?";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, guild_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return mapToList(resultSet);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

}
