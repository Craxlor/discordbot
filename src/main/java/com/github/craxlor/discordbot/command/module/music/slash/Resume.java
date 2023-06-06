package com.github.craxlor.discordbot.command.module.music.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCMusic;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.music.MusicManager;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Resume extends SCMusic {
    @Override
    @Nonnull
    public String getName() {
        return "resume";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Resumes the paused track";
    }

    @Override
    public Reply executeMusic(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        final MusicManager musicManager = GuildManager.getGuildManager(event.getGuild()).getMusicManager();
        String message = "";
        Status status = Status.FAIL;
        AudioTrackInfo audioTrackInfo = null;
        if (musicManager.player.getPlayingTrack() == null) {
            message = "There is nothing to play at the moment!";
        } else {
            if (musicManager.player.isPaused()) {
                musicManager.scheduler.onPlayerResume(musicManager.player);
                message = ":play_pause: Resuming the playback of the current track.";
                status = Status.SUCCESS;
            } else
                message = "There is already a track playing!";
            audioTrackInfo = musicManager.player.getPlayingTrack().getInfo();
        }
        return new Reply(event).onCommand(status, message).onMusic(audioTrackInfo, null);
    }

}
