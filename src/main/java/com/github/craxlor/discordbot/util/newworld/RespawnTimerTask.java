package com.github.craxlor.discordbot.util.newworld;

import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.music.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class RespawnTimerTask extends TimerTask {

    private final Guild guild;
    private final VoiceChannel voiceChannel;
    private final long offsetSeconds;
    private final int COUNTDOWNDURATION = 10000;
    private final int[] times = { 60, 80, 100, 120, 140, 160, 180, 200, 220, 240, 260, 280, 300, 336, 365, 392, 413,
            448, 476, 505, 532, 561, 589, 611, 653, 688, 725, 760, 798, 833, 868, 904, 940, 985, 1030, 1072, 1118, 1160,
            1205, 1250, 1300, 1352, 1402, 1457, 1501, 1570, 1629, 1689, 1750, 1800 };

    public RespawnTimerTask(Guild guild, VoiceChannel voiceChannel, long offsetSeconds) {
        this.guild = guild;
        this.voiceChannel = voiceChannel;
        this.offsetSeconds = offsetSeconds;

    }

    @Override
    public void run() {
        GuildManager.getGuildManager(guild).setRespawnTimerTask(this);
        int index = 0;
        for (int time : times) {
            if (time < offsetSeconds + TimeUnit.MILLISECONDS.toSeconds(COUNTDOWNDURATION))
                index++;
            else
                break;
        }
        long sleeptime = times[index] - offsetSeconds;
        // join voice channel
        final AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(voiceChannel);
        final MusicManager musicManager = GuildManager.getGuildManager(guild).getMusicManager();
        final AudioPlayerManager audioPlayerManager = GuildManager.getAudioPlayerManager();
        final String dir = "./resources/soundfiles/";
        String respawnType = "";
        try {
            MyAudioLoadResultHandler myAudioLoadResultHandler = new MyAudioLoadResultHandler(musicManager);
            while (index < times.length - 1) {
                System.out.println("index: " + index);
                Thread.sleep(TimeUnit.SECONDS.toMillis(sleeptime) - COUNTDOWNDURATION);
                audioPlayerManager.loadItemOrdered(musicManager, dir + "10_3_2_1.mp3", myAudioLoadResultHandler).get();
                Thread.sleep(COUNTDOWNDURATION);
                sleeptime = times[index + 1] - times[index];

                if (index == times.length - 4)
                    respawnType = "third_last_respawn";
                else if (index == times.length - 3)
                    respawnType = "second_last_respawn";
                else if (index == times.length - 2)
                    respawnType = "last_respawn";
                else
                    respawnType = sleeptime > 30 ? "long_respawn" : "respawn";

                audioPlayerManager.loadItemOrdered(musicManager, dir + respawnType + ".mp3", myAudioLoadResultHandler)
                        .get();

                index++;
            }
            System.out.println("war has ended");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean cancel() {
        final AudioManager audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
        GuildManager.getGuildManager(guild).setRespawnTimerTask(null);
        return super.cancel();
    }

    private class MyAudioLoadResultHandler implements AudioLoadResultHandler {
        private final MusicManager musicManager;

        public MyAudioLoadResultHandler(MusicManager musicManager) {
            this.musicManager = musicManager;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            musicManager.scheduler.queue(track);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            throw new UnsupportedOperationException("Unimplemented method 'playlistLoaded'");
        }

        @Override
        public void noMatches() {
            System.err.println("no matches");

        }

        @Override
        public void loadFailed(FriendlyException exception) {
            System.err.println("load failed");
        }

    }

}
