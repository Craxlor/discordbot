package com.github.craxlor.discordbot.command.module.music.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCMusic;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.music.MusicManager;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Skip extends SCMusic {

    @Override
    @Nonnull
    public String getName() {
        return "skip";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Skips the current track.";
    }

    @Override
    public Reply executeMusic(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        final MusicManager musicManager = GuildManager.getGuildManager(event.getGuild()).getMusicManager();
        String message = "";
        AudioTrackInfo audioTrackInfo = null;
        Status status = Status.FAIL;
        if (musicManager.player.getPlayingTrack() == null) {
            message = "There is nothing to skip at the moment!";
        } else {
            musicManager.scheduler.nextTrack();
            status = Status.SUCCESS;
            if (musicManager.player.getPlayingTrack() == null) {
                message = ":fast_forward: Skipped to nothing.\nThe Bot will be disconnected from the channel.";
                Disconnect.disconnect(event.getGuild());
            } else {
                message = ":fast_forward: Skipped to "
                        + musicManager.player.getPlayingTrack().getInfo().title + " :thumbsup:";
                audioTrackInfo = musicManager.player.getPlayingTrack().getInfo();
            }
        }
        return new Reply(event).onCommand(status, message).onMusic(audioTrackInfo, null);
    }

}
