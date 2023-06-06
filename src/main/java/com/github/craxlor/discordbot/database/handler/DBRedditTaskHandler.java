package com.github.craxlor.discordbot.database.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.codec.binary.Base64;

import com.github.craxlor.discordbot.database.entity.RedditTask;

public class DBRedditTaskHandler implements EntityHandler<RedditTask> {

    @Override
    public boolean insert(RedditTask entity) {
        String sql = "INSERT INTO redditTasks(channel_id, subreddit, firstTime, period, guild_id) VALUES(?,?,?,?,?)";
        String enc64 = Base64.encodeBase64String(entity.getSubreddit().getBytes());
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, entity.getChannel_id());
            preparedStatement.setString(2, enc64);
            preparedStatement.setString(3, entity.getFirstTime());
            preparedStatement.setLong(4, entity.getPeriod());
            preparedStatement.setLong(5, entity.getGuild_id());
            preparedStatement.executeUpdate();
            logger.info("inserted new redditTask");
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
    public RedditTask getEntity(long id) {
        return null;
    }

    @Nullable
    public RedditTask getEntity(Long guild_id, String subreddit) {
        String sql = "SELECT * FROM redditTasks WHERE guild_id = ? AND subreddit = ?";
        String enc64 = Base64.encodeBase64String(subreddit.getBytes());
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, guild_id);
            preparedStatement.setString(2, enc64);
            ResultSet resultSet = preparedStatement.executeQuery();
            return map(resultSet);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    @Nullable
    public List<RedditTask> getEntities(long guild_id) {
        String sql = "SELECT * FROM redditTasks WHERE guild_id = ?";
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

    /**
     * not implemented
     */
    @Override
    public boolean update(RedditTask entity) {
        return false;
    }

    @Override
    public boolean remove(long id) {
        String sql = "DELETE FROM redditTasks WHERE channel_id = ?";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            logger.info("deleted a redditTask");
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return false;
        }

    }

    @Override
    public RedditTask mapFrom(ResultSet rs) throws SQLException {
        RedditTask redditTask = new RedditTask();
        redditTask.setChannel_id(rs.getLong("channel_id"));
        redditTask.setFirstTime(rs.getString("firstTime"));
        redditTask.setGuild_id(rs.getLong("guild_id"));
        redditTask.setPeriod(rs.getLong("period"));
        redditTask.setSubreddit(new String(Base64.decodeBase64(rs.getString("subreddit"))));
        return redditTask;
    }

    public void removeRedditTask(long channel_id, String subreddit) {
        String sql = "DELETE FROM redditTasks WHERE channel_id = ? AND subreddit = ?";
        String enc64 = Base64.encodeBase64String(subreddit.getBytes());
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setLong(1, channel_id);
            preparedStatement.setString(2, enc64);
            preparedStatement.executeUpdate();
            logger.info("deleted a redditTask");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }
}
