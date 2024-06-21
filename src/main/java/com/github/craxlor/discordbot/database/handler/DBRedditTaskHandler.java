package com.github.craxlor.discordbot.database.handler;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Session;

import com.github.craxlor.discordbot.database.entity.Guild;
import com.github.craxlor.discordbot.database.entity.RedditTask;

public class DBRedditTaskHandler implements EntityHandler<RedditTask> {

    @Override
    public RedditTask getEntity(Session session,long id) {
        RedditTask redditTask = session.get(RedditTask.class, id);
        return redditTask;
    }

    @Override
    public void remove(Session session,long id) {
        RedditTask redditTask = (RedditTask) session.get(RedditTask.class, id);
        session.remove(redditTask);
    }

    @Nullable
    public RedditTask getEntity(Session session, long guild_id, String subreddit) {
        String enc64 = Base64.encodeBase64String(subreddit.getBytes());
        String sql = "SELECT * FROM redditTasks WHERE guild_id = " + guild_id + " AND subreddit = " + enc64;
        RedditTask redditTask = session.createQuery(sql, RedditTask.class).getSingleResult();
        return redditTask;
    }

    @Nullable
    public List<RedditTask> getRedditTasksByGuild(Session session,long guild_id) {
        Guild guild = (Guild) session.get(Guild.class, guild_id);
        return guild != null ? guild.getRedditTasks() : null;
    }

    public void removeRedditTask(Session session,long channel_id, String subreddit) {
        String enc64 = Base64.encodeBase64String(subreddit.getBytes());
        String sql = "SELECT * FROM redditTasks WHERE channel_id = " + channel_id + " AND subreddit = " + enc64;
        RedditTask redditTask = session.createQuery(sql, RedditTask.class).getSingleResult();
        session.remove(redditTask);
    }

}
