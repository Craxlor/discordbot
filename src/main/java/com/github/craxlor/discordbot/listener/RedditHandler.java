package com.github.craxlor.discordbot.listener;

import java.util.List;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.database.entity.RedditTask;
import com.github.craxlor.discordbot.database.handler.DBRedditTaskHandler;
import com.github.craxlor.discordbot.util.core.GuildManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RedditHandler extends ListenerAdapter {
    /**
     * load all Reddit Tasks for the guild
     */
    @Override
    @SuppressWarnings("null")
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        DBRedditTaskHandler dbRedditTaskHandler = new DBRedditTaskHandler();
        List<RedditTask> redditTasks = dbRedditTaskHandler.getEntities(guild.getIdLong());
        TextChannel textChannel;
        /**
         * remove redditTasks if their channel has been removed,
         * during downtime
         */
        for (RedditTask redditTask : redditTasks) {
            if (redditTasks != null && redditTasks.size() > 0) {
                textChannel = guild.getTextChannelById(redditTask.getChannel_id());
                if (textChannel == null) {
                    dbRedditTaskHandler.remove(redditTask.getChannel_id());
                    redditTasks.remove(redditTask);
                }
            }
        }
        // schedule remaining tasks
        GuildManager guildManager = GuildManager.getGuildManager(guild);
        guildManager.getRedditScheduler().schedule(redditTasks);
    }
}
