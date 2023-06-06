package com.github.craxlor.discordbot.database.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Nullable;

import com.github.craxlor.discordbot.database.entity.YouTubeSearch;

public class DBYoutubeSearchHandler implements EntityHandler<YouTubeSearch> {

    @Override
    public boolean insert(YouTubeSearch entity) {
        String sql_ytSearches = "INSERT INTO ytSearches(searchTerm, video_id) VALUES(?,?)";
        String sql_ytVideos = "INSERT INTO ytVideos(video_id, channel_id, video_title) VALUES(?,?,?)";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql_ytSearches);
            preparedStatement.setString(1, entity.getSearchTerm());
            preparedStatement.setString(2, entity.getVideo_id());
            preparedStatement.executeUpdate();

            preparedStatement = prepareStatement(sql_ytVideos);
            preparedStatement.setString(1, entity.getVideo_id());
            preparedStatement.setString(2, entity.getChannel_id());
            preparedStatement.setString(3, entity.getVideo_title());
            preparedStatement.executeUpdate();
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
    public YouTubeSearch getEntity(long id) {
        return null;
    }

    @Nullable
    public YouTubeSearch getYouTubeSearchById(String video_id) {
        String sql = "SELECT * FROM ytVideos WHERE video_id = ?";
        try {
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setString(1, video_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return map(resultSet);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    @Nullable
    public YouTubeSearch getYouTubeSearchBySearchTerm(String searchTerm) {
        String sql = "SELECT * FROM ytSearches WHERE searchTerm = ?";
        try {
            // get video_id by searchTerm
            PreparedStatement preparedStatement = prepareStatement(sql);
            preparedStatement.setString(1, searchTerm);
            ResultSet resultSet = preparedStatement.executeQuery();
            String video_id = resultSet.getString("video_id");
            if (video_id == null)
                return null;
            // get further information by videoId
            sql = "SELECT * FROM ytVideos WHERE video_id = ?";
            preparedStatement = prepareStatement(sql);
            preparedStatement.setString(1, video_id);
            resultSet = preparedStatement.executeQuery();
            YouTubeSearch youTubeSearch = map(resultSet);
            youTubeSearch.setSearchTerm(searchTerm);
            return youTubeSearch;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    /**
     * not implemented
     */
    @Override
    public boolean update(YouTubeSearch entity) {
        return false;
    }

    /**
     * not implemented
     */
    @Override
    public boolean remove(long id) {
        return false;
    }

    @Override
    public YouTubeSearch mapFrom(ResultSet rs) throws SQLException {
        YouTubeSearch youTubeSearch = new YouTubeSearch();
        youTubeSearch.setChannel_id(rs.getString("channel_id"));
        youTubeSearch.setSearchTerm(rs.getString("searchTerm"));
        youTubeSearch.setVideo_id(rs.getString("video_id"));
        youTubeSearch.setVideo_title(rs.getString("video_title"));
        return youTubeSearch;
    }
    
}
