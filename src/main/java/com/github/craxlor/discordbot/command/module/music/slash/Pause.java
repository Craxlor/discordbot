package com.github.craxlor.discordbot.command.module.music.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCMusic;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.music.MusicManager;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Pause extends SCMusic {

    @Override
    @Nonnull
    public String getName() {
        return "pause";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Pauses the current track.";
    }

    @Override
    public Reply executeMusic(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        final MusicManager musicManager = GuildManager.getGuildManager(event.getGuild()).getMusicManager();
        AudioTrackInfo audioTrackInfo = null;
        String message = "";
        Status status = Status.FAIL;
        if (musicManager.player.getPlayingTrack() == null) {
            message = "There is no track playing at the moment!";
        } else {
            if (musicManager.player.isPaused()) {
                message = "The track was already paused.";
            } else {
                musicManager.scheduler.onPlayerPause(musicManager.player);
                message = ":pause_button: The current track has been paused!\nUse /resume to proceed with the playback.";
                status = Status.SUCCESS;
            }
            audioTrackInfo = musicManager.player.getPlayingTrack().getInfo();
        }
        return new Reply(event).onCommand(status, message).onMusic(audioTrackInfo, null);
    }

}
