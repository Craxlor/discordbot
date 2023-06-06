package com.github.craxlor.discordbot.util.core;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.github.craxlor.discordbot.command.Commandlist;
import com.github.craxlor.discordbot.database.handler.DBGuildHandler;
import com.github.craxlor.discordbot.util.music.MusicManager;
import com.github.craxlor.discordbot.util.newworld.RespawnTimerTask;
import com.github.craxlor.discordbot.util.reddit.RedditScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.api.entities.Guild;

public class GuildManager {
    private static HashMap<Long, GuildManager> guildManagerMap = new HashMap<>();
    private static AudioPlayerManager playerManager;
    private String guild_id;
    private Logger logger;
    private MusicManager musicManager;
    private RedditScheduler redditScheduler;
    private Commandlist commandlist;
    private RespawnTimerTask respawnTimerTask;

    protected GuildManager(Guild guild) {
        this.guild_id = guild.getId();
        logger = LoggerFactory.getLogger("sift");
        // commandlist
        List<String> modules = new DBGuildHandler().getEntity(guild.getIdLong()).getModulesAsList();
        commandlist = new Commandlist();
        commandlist.add(Commandlist.CORE);
        if (modules != null) {
            for (String module : modules) {
                commandlist.add(module);
            }
        }
        commandlist = commandlist.getGuildCommands();
        commandlist.addAll(Commandlist.getEveryCommand().getGlobalCommands());

        // reddit
        redditScheduler = new RedditScheduler(guild);
        // music
        musicManager = new MusicManager(getAudioPlayerManager());
        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
    }

    public static GuildManager getGuildManager(Guild guild) {
        long id = guild.getIdLong();
        if (guildManagerMap.containsKey(id))
            return guildManagerMap.get(id);
        GuildManager guildManager = new GuildManager(guild);
        guildManagerMap.put(id, guildManager);
        return guildManager;
    }

    public static AudioPlayerManager getAudioPlayerManager() {
        if (playerManager == null) {
            playerManager = new DefaultAudioPlayerManager();
            AudioSourceManagers.registerRemoteSources(playerManager);
            AudioSourceManagers.registerLocalSource(playerManager);
        }
        return playerManager;
    }

    public Logger getLogger() {
        MDC.put("filename", guild_id);
        return logger;
    }

    public Commandlist getCommandlist() {
        return commandlist;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public RedditScheduler getRedditScheduler() {
        return redditScheduler;
    }

    public RespawnTimerTask getRespawnTimerTask() {
        return respawnTimerTask;
    }

    public void setRespawnTimerTask(RespawnTimerTask respawnTimerTask) {
        this.respawnTimerTask = respawnTimerTask;
    }
}
