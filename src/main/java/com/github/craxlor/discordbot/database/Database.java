package com.github.craxlor.discordbot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.JournalMode;
import org.sqlite.SQLiteConfig.SynchronousMode;
import org.sqlite.SQLiteConfig.TempStore;

public class Database {
    private static Database INSTANCE;
    private static final String URL = "jdbc:sqlite:./resources/discordBotDB.db";
    Connection connection;
    Logger logger;

    public static Database getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Database();
        return INSTANCE;
    }

    private Database() {
        logger = LoggerFactory.getLogger("database");
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.setTempStoreDirectory("./resources/tmp");
            config.setBusyTimeout(30000);
            config.setSynchronous(SynchronousMode.NORMAL);
            config.setTempStore(TempStore.MEMORY);
            config.setJournalMode(JournalMode.MEMORY);

            connection = DriverManager.getConnection(URL, config.toProperties());
            logger.info("connected to database");

            setupTables();
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            System.exit(0);
        }
    }

    public void setupTables() throws SQLException {
        //  SQL statement for creating a new table  
        String guildTable = "CREATE TABLE IF NOT EXISTS guilds (guild_id INTEGER, name TEXT, modules TEXT, colorHex TEXT, musicLog_id INTEGER)";
        String autoroomTriggerTable = "CREATE TABLE IF NOT EXISTS autoroomTriggers (trigger_id INTEGER, category_id INTEGER, naming_pattern TEXT, inheritance TEXT)";
        String autoroomChannelTable = "CREATE TABLE IF NOT EXISTS autoroomChannels (channel_id INTEGER, trigger_id INTEGER, guild_id INTEGER)";
        String youtubeVideoTable = "CREATE TABLE IF NOT EXISTS ytVideos (video_id TEXT, channel_id TEXT, video_title TEXT)";
        String youtubeSearchTable = "CREATE TABLE IF NOT EXISTS ytSearches (searchTerm TEXT, video_id TEXT)";
        String redditTasks = "CREATE TABLE IF NOT EXISTS redditTasks (channel_id INTEGER, subreddit TEXT, firstTime TEXT, period INTEGER, guild_id INTEGER)";
        Statement statement = connection.createStatement();
        statement.execute(guildTable);
        statement.execute(autoroomTriggerTable);
        statement.execute(autoroomChannelTable);
        statement.execute(youtubeVideoTable);
        statement.execute(youtubeSearchTable);
        statement.execute(redditTasks);
        statement.close();
    }

    /**
     * DISCORDSERVER
     * public void insert(DiscordServer discordServer) {
     * String sql = "INSERT INTO guilds(guild_id, admin_id, dj_id, musicLog_id,
     * name, modules, colorHex) VALUES(?,?,?,?,?,?,?)";
     * try {
     * preparedStatement = connection.prepareStatement(sql);
     * preparedStatement.setLong(1, discordServer.getGuild_id());
     * preparedStatement.setLong(2, discordServer.getAdmin_id());
     * preparedStatement.setLong(3, discordServer.getDj_id());
     * preparedStatement.setLong(4, discordServer.getMusicLog_id());
     * preparedStatement.setString(5, discordServer.getName());
     * preparedStatement.setString(6, discordServer.getModules());
     * preparedStatement.setString(7, discordServer.getColorHex());
     * preparedStatement.executeUpdate();
     * logger.info("inserted new guild: " + discordServer.getName() + " | " +
     * discordServer.getGuild_id());
     * } catch (SQLException e) {
     * logger.warn(e.getMessage());
     * }
     * }
     * 
     * @Nullable
     *           public DiscordServer getDiscordServer(long guild_id) {
     *           String sql = "SELECT * FROM guilds WHERE guild_id = ?";
     *           try {
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setLong(1, guild_id);
     *           ResultSet resultSet = preparedStatement.executeQuery();
     *           return new DSMapper().map(resultSet);
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           }
     *           return null;
     *           }
     * 
     *           public void update(DiscordServer discordServer) {
     *           String sql = "UPDATE guilds SET admin_id = ?, dj_id = ?,
     *           musicLog_id = ?, name = ?, modules = ?, colorHex = ? WHERE guild_id
     *           = ?";
     *           try {
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setLong(1, discordServer.getAdmin_id());
     *           preparedStatement.setLong(2, discordServer.getDj_id());
     *           preparedStatement.setLong(3, discordServer.getMusicLog_id());
     *           preparedStatement.setString(4, discordServer.getName());
     *           preparedStatement.setString(5, discordServer.getModules());
     *           preparedStatement.setString(6, discordServer.getColorHex());
     *           preparedStatement.setLong(7, discordServer.getGuild_id());
     *           preparedStatement.executeUpdate();
     *           logger.info("updated guild: " + discordServer.getName() + " | " +
     *           discordServer.getGuild_id());
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           }
     *           }
     */

    /**
     * AUTOROOMCHANNEL
     * public void insert(AutoroomChannel autoroomChannel) {
     * String sql = "INSERT INTO autoroomChannels(channel_id, trigger_id, guild_id)
     * VALUES(?,?,?)";
     * try {
     * preparedStatement = connection.prepareStatement(sql);
     * preparedStatement.setLong(1, autoroomChannel.getChannel_id());
     * preparedStatement.setLong(2, autoroomChannel.getTrigger_id());
     * preparedStatement.setLong(3, autoroomChannel.getGuild_id());
     * preparedStatement.executeUpdate();
     * logger.info("inserted new autoroomChannel");
     * } catch (SQLException e) {
     * logger.warn(e.getMessage());
     * }
     * }
     * 
     * @Nullable
     *           public AutoroomChannel getAutoroomChannel(long channel_id) {
     *           String sql = "SELECT * FROM autoroomChannels WHERE channel_id = ?";
     *           try {
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setLong(1, channel_id);
     *           ResultSet resultSet = preparedStatement.executeQuery();
     *           return new ACMapper().map(resultSet);
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           }
     *           return null;
     *           }
     * 
     *           public int countAutoroomChannelsByTrigger(long trigger_id) {
     *           String sql = "SELECT COUNT(*) AS count FROM autoroomChannels WHERE
     *           trigger_id = ?";
     *           try {
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setLong(1, trigger_id);
     *           ResultSet resultSet = preparedStatement.executeQuery();
     *           return resultSet.getInt("count");
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           return -1;
     *           }
     *           }
     * 
     * @Nullable
     *           public List<AutoroomChannel> getAutoroomChannelsByGuild(long
     *           guild_id) {
     *           String sql = "SELECT * FROM autoroomChannels WHERE guild_id = ?";
     *           try {
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setLong(1, guild_id);
     *           ResultSet resultSet = preparedStatement.executeQuery();
     *           return new ACMapper().mapToList(resultSet);
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           return null;
     *           }
     *           }
     * 
     *           public void removeAutoroomChannel(long channel_id) {
     *           String sql = "DELETE FROM autoroomChannels WHERE channel_id = ?";
     *           try {
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setLong(1, channel_id);
     *           preparedStatement.executeUpdate();
     *           logger.info("deleted an autoroomChannel");
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           }
     *           }
     */

    /**
     * AUTOROOMTRIGGER
     * public void insert(AutoroomTrigger autoroomTrigger) {
     * String sql = "INSERT INTO autoroomTriggers(trigger_id, category_id,
     * naming_pattern, inheritance) VALUES(?,?,?,?)";
     * try {
     * preparedStatement = connection.prepareStatement(sql);
     * preparedStatement.setLong(1, autoroomTrigger.getTrigger_id());
     * preparedStatement.setLong(2, autoroomTrigger.getCategory_id());
     * preparedStatement.setString(3, autoroomTrigger.getNaming_pattern());
     * preparedStatement.setString(4, autoroomTrigger.getInheritance());
     * preparedStatement.executeUpdate();
     * logger.info("inserted new autoroomTrigger");
     * } catch (SQLException e) {
     * logger.warn(e.getMessage());
     * }
     * }
     * 
     * @Nullable
     *           public AutoroomTrigger getAutoroomTrigger(long trigger_id) {
     *           String sql = "SELECT * FROM autoroomTriggers WHERE trigger_id = ?";
     *           try {
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setLong(1, trigger_id);
     *           ResultSet resultSet = preparedStatement.executeQuery();
     *           return new ACTMapper().map(resultSet);
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           }
     *           return null;
     *           }
     * 
     *           public void update(AutoroomTrigger autoroomTrigger) {
     *           String sql_c = "UPDATE autoroomTriggers SET category_id = ? WHERE
     *           trigger_id = ?";
     *           String sql_n = "UPDATE autoroomTriggers SET naming_pattern = ?
     *           WHERE trigger_id = ?";
     *           String sql_i = "UPDATE autoroomTriggers SET inheritance = ? WHERE
     *           trigger_id = ?";
     *           try {
     *           if (autoroomTrigger.getCategory_id() > -1) {
     *           preparedStatement = connection.prepareStatement(sql_c);
     *           preparedStatement.setLong(1, autoroomTrigger.getCategory_id());
     *           preparedStatement.executeUpdate();
     *           }
     *           if (autoroomTrigger.getNaming_pattern() != null) {
     *           preparedStatement = connection.prepareStatement(sql_n);
     *           preparedStatement.setString(1,
     *           autoroomTrigger.getNaming_pattern());
     *           preparedStatement.executeUpdate();
     *           }
     *           if (autoroomTrigger.getInheritance() != null) {
     *           preparedStatement = connection.prepareStatement(sql_i);
     *           preparedStatement.setString(1, autoroomTrigger.getInheritance());
     *           preparedStatement.executeUpdate();
     *           }
     *           logger.info("updated guild");
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           }
     *           }
     * 
     *           public void removeAutoroomTrigger(long trigger_id) {
     *           String sql = "DELETE FROM autoroomTriggers WHERE trigger_id = ?";
     *           try {
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setLong(1, trigger_id);
     *           preparedStatement.executeUpdate();
     *           logger.info("deleted an autoroomTrigger");
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           }
     *           }
     */

    /**
     * YOUTUBESEARCH
     * public void insert(YouTubeSearch youTubeSearch) {
     * String sql_ytSearches = "INSERT INTO ytSearches(searchTerm, video_id)
     * VALUES(?,?)";
     * String sql_ytVideos = "INSERT INTO ytVideos(video_id, channel_id,
     * video_title) VALUES(?,?,?)";
     * try {
     * preparedStatement = connection.prepareStatement(sql_ytSearches);
     * preparedStatement.setString(1, youTubeSearch.getSearchTerm());
     * preparedStatement.setString(2, youTubeSearch.getVideo_id());
     * preparedStatement.executeUpdate();
     * 
     * preparedStatement = connection.prepareStatement(sql_ytVideos);
     * preparedStatement.setString(1, youTubeSearch.getVideo_id());
     * preparedStatement.setString(2, youTubeSearch.getChannel_id());
     * preparedStatement.setString(3, youTubeSearch.getVideo_title());
     * preparedStatement.executeUpdate();
     * } catch (SQLException e) {
     * logger.warn(e.getMessage());
     * }
     * }
     * 
     * @Nullable
     *           public YouTubeSearch getYouTubeSearchById(String video_id) {
     *           String sql = "SELECT * FROM ytVideos WHERE video_id = ?";
     *           try {
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setString(1, video_id);
     *           ResultSet resultSet = preparedStatement.executeQuery();
     *           return new YSMapper().map(resultSet);
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           }
     *           return null;
     *           }
     * 
     * @Nullable
     *           public YouTubeSearch getYouTubeSearchBySearchTerm(String
     *           searchTerm) {
     *           String sql = "SELECT * FROM ytSearches WHERE searchTerm = ?";
     *           try {
     *           // get video_id by searchTerm
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setString(1, searchTerm);
     *           ResultSet resultSet = preparedStatement.executeQuery();
     *           String video_id = resultSet.getString("video_id");
     *           if (video_id == null)
     *           return null;
     *           // get further information by videoId
     *           sql = "SELECT * FROM ytVideos WHERE video_id = ?";
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setString(1, video_id);
     *           resultSet = preparedStatement.executeQuery();
     *           YouTubeSearch youTubeSearch = new YSMapper().map(resultSet);
     *           youTubeSearch.setSearchTerm(searchTerm);
     *           return youTubeSearch;
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           }
     *           return null;
     *           }
     */

    /**
     * REDDITTASK
     * public void insert(RedditTask redditTask) {
     * String sql = "INSERT INTO redditTasks(channel_id, subreddit, firstTime,
     * period, guild_id) VALUES(?,?,?,?,?)";
     * String enc64 =
     * Base64.encodeBase64String(redditTask.getSubreddit().getBytes());
     * try {
     * preparedStatement = connection.prepareStatement(sql);
     * preparedStatement.setLong(1, redditTask.getChannel_id());
     * preparedStatement.setString(2, enc64);
     * preparedStatement.setString(3, redditTask.getFirstTime());
     * preparedStatement.setLong(4, redditTask.getPeriod());
     * preparedStatement.setLong(5, redditTask.getGuild_id());
     * preparedStatement.executeUpdate();
     * logger.info("inserted new redditTask");
     * } catch (SQLException e) {
     * logger.warn(e.getMessage());
     * }
     * }
     * 
     * public void removeRedditTask(long channel_id) {
     * String sql = "DELETE FROM redditTasks WHERE channel_id = ?";
     * try {
     * preparedStatement = connection.prepareStatement(sql);
     * preparedStatement.setLong(1, channel_id);
     * preparedStatement.executeUpdate();
     * logger.info("deleted a redditTask");
     * } catch (SQLException e) {
     * logger.warn(e.getMessage());
     * }
     * }
     * 
     * public void removeRedditTask(long channel_id, String subreddit) {
     * String sql = "DELETE FROM redditTasks WHERE channel_id = ? AND subreddit =
     * ?";
     * String enc64 = Base64.encodeBase64String(subreddit.getBytes());
     * try {
     * preparedStatement = connection.prepareStatement(sql);
     * preparedStatement.setLong(1, channel_id);
     * preparedStatement.setString(2, enc64);
     * preparedStatement.executeUpdate();
     * logger.info("deleted a redditTask");
     * } catch (SQLException e) {
     * logger.warn(e.getMessage());
     * }
     * }
     * 
     * 
     * @Nullable
     *           public RedditTask getRedditTask(Long guild_id, String subreddit) {
     *           String sql = "SELECT * FROM redditTasks WHERE guild_id = ? AND
     *           subreddit = ?";
     *           String enc64 = Base64.encodeBase64String(subreddit.getBytes());
     *           try {
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setLong(1, guild_id);
     *           preparedStatement.setString(2, enc64);
     *           ResultSet resultSet = preparedStatement.executeQuery();
     *           return new RTMapper().map(resultSet);
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           }
     *           return null;
     *           }
     * 
     * @Nullable
     *           public List<RedditTask> getRedditTasks(long guild_id) {
     *           String sql = "SELECT * FROM redditTasks WHERE guild_id = ?";
     *           try {
     *           preparedStatement = connection.prepareStatement(sql);
     *           preparedStatement.setLong(1, guild_id);
     *           ResultSet resultSet = preparedStatement.executeQuery();
     *           return new RTMapper().mapToList(resultSet);
     *           } catch (SQLException e) {
     *           logger.warn(e.getMessage());
     *           return null;
     *           }
     *           }
     */

    // OTHER
    public void closeConnection() {
        try {
            connection.close();
            INSTANCE = null;
            logger.info("closed connection to database");
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
