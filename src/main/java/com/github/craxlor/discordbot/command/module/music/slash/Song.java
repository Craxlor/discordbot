package com.github.craxlor.discordbot.command.module.music.slash;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCMusic;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.music.MusicManager;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Song extends SCMusic {

    private static final String SOURCE_NAME = "source";
    private static final String SOURCE_DESCRIPTION = "Returns the title of the playing song.";
    private static final String TIMESTAMP_NAME = "timestamp";
    private static final String TIMESTAMP_DESCRIPTION = "Returns the current timestamp of the playing song.";

    public Song() {
        SubcommandData source = new SubcommandData(SOURCE_NAME, SOURCE_DESCRIPTION);
        SubcommandData timestamp = new SubcommandData(TIMESTAMP_NAME, TIMESTAMP_DESCRIPTION);
        commandData.addSubcommands(source, timestamp);
    }

    @Override
    @Nonnull
    public String getName() {
        return "song";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "provide song information";
    }

    @Override
    @SuppressWarnings("null")
    public Reply executeMusic(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        String subcommandName = event.getSubcommandName();
        final MusicManager musicManager = GuildManager.getGuildManager(event.getGuild()).getMusicManager();
        String message = "";
        AudioTrackInfo audioTrackInfo = null;
        Status status = Status.FAIL;
        if (subcommandName.equals(SOURCE_NAME)) {
            if (musicManager.player.getPlayingTrack() == null) {
                message = "There is nothing playing at the moment.";
            } else {
                audioTrackInfo = musicManager.player.getPlayingTrack().getInfo();
                message = "You are listening to " + audioTrackInfo.title + ".";
                status = Status.SUCCESS;
            }
        } else if (subcommandName.equals(TIMESTAMP_NAME)) {
            if (musicManager.player.getPlayingTrack() == null) {
                message = "There is nothing playing at the moment.";
            } else {
                audioTrackInfo = musicManager.player.getPlayingTrack().getInfo();
                long timestamp = musicManager.player.getPlayingTrack().getPosition();
                long minutes = TimeUnit.MILLISECONDS.toMinutes(timestamp);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(timestamp) % 60;
                String second = String.valueOf(seconds);
                if (second.length() < 2) {
                    second = "0" + second;
                }
                message = "Current timestamp: " + minutes + ":" + second + " minutes.";
                status = Status.SUCCESS;
            }
        }
        return new Reply(event).onCommand(status, message).onMusic(audioTrackInfo, null);
    }

}
