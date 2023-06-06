package com.github.craxlor.discordbot.util.music;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.craxlor.discordbot.util.reply.Status;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import com.github.craxlor.discordbot.command.module.music.slash.Play;

public class MyAudioLoadResultHandler implements AudioLoadResultHandler {

    AudioTrackInfo trackInfo = null;
    MusicManager musicManager;
    String mode, message, input;
    @Nonnull
    Status status = Status.ERROR;
    @Nonnull
    Member member;

    public MyAudioLoadResultHandler(MusicManager musicManager, @Nonnull Member member, String mode, String input) {
        this.musicManager = musicManager;
        this.member = member;
        this.mode = mode;
        this.input = input;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        connectTo(member);
        switch (mode) {
            case Play.MODE_CHOICE_NEXT -> {
                musicManager.scheduler.addOnTopOfQueue(track, true);
                message = track.getInfo().title + " is now at the top of the queue.";
            }
            case Play.MODE_CHOICE_NOW -> {
                musicManager.scheduler.addOnTopOfQueue(track, false);
                message = "Playing: " + track.getInfo().title;
            }
            default -> {
                musicManager.scheduler.queue(track);
                // check if the track is played immediately
                if (musicManager.player.getPlayingTrack().equals(track))
                    message = "Playing: " + track.getInfo().title;
                else
                    message = track.getInfo().title + " has been added to the queue.";
            }
        }
        trackInfo = track.getInfo();
        status = Status.SUCCESS;
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        connectTo(member);
        final List<AudioTrack> tracks = playlist.getTracks();
        switch (mode) {
            case Play.MODE_CHOICE_NEXT -> musicManager.scheduler.addOnTopOfQueue(tracks, true);
            case Play.MODE_CHOICE_NOW -> musicManager.scheduler.addOnTopOfQueue(tracks, false);
            default -> musicManager.scheduler.addToQueue(tracks);
        }
        message = "added playlist " + "[" + playlist.getName() + "](" + input + ")" + " to queue";

        status = Status.SUCCESS;
    }

    @Override
    public void noMatches() {
        message = ":x: Nothing found";
        status = Status.FAIL;
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        message = ":x: Could not load \n" + exception.getMessage();
        status = Status.FAIL;
    }

    @SuppressWarnings("null")
    private void connectTo(@Nonnull Member member) {
        final VoiceChannel myChannel = (VoiceChannel) member.getVoiceState().getChannel();
        final AudioManager audioManager = member.getGuild().getAudioManager();
        audioManager.openAudioConnection(myChannel);
    }

    @Nonnull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public AudioTrackInfo getTrackInfo() {
        return trackInfo;
    }

    @Nullable
    public String getMessage() {
        return message;
    }
}
