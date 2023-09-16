package com.github.craxlor.discordbot.util.newworld;

import java.util.Timer;
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

    private final AudioPlayerManager audioPlayerManager;
    private final MusicManager musicManager;
    private final Guild guild;
    private final VoiceChannel voiceChannel;
    private final long offsetSeconds;
    private final int COUNTDOWNDURATION = 10;
    private final String dir = "./resources/soundfiles/";

    /**
     * array contains all respawn times in seconds
     * measured with war start at 0 seconds
     */
    private final int[] times = { 60, 80, 100, 120, 140, 160, 180, 200, 220, 240, 260, 280, 300, 336, 365, 392, 413,
            448, 476, 505, 532, 561, 589, 611, 653, 688, 725, 760, 798, 833, 868, 904, 940, 985, 1030, 1072, 1118, 1160,
            1205, 1250, 1300, 1352, 1402, 1457, 1501, 1570, 1629, 1689, 1750, 1800 };
    private int timesIndex;

    public RespawnTimerTask(Guild guild, VoiceChannel voiceChannel, long offsetSeconds) {
        this.guild = guild;
        this.voiceChannel = voiceChannel;
        this.offsetSeconds = offsetSeconds;
        timesIndex = calculateTimesIndex();

        musicManager = GuildManager.getGuildManager(guild).getMusicManager();
        audioPlayerManager = GuildManager.getAudioPlayerManager();
    }

    @Override
    public void run() {
        // register this task in guildmanager
        GuildManager.getGuildManager(guild).setRespawnTimerTask(this);
        // join voice channel
        final AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(voiceChannel);

        String respawnType = "";
        Timer timer = new Timer();
        MyAudioLoadResultHandler myAudioLoadResultHandler = new MyAudioLoadResultHandler(musicManager);

        long sleeptime = times[timesIndex] - offsetSeconds;

        while (timesIndex < times.length - 1) {
            // schedule 10 seconds reminder
            timer.schedule(new AudioPlayerManagerTask(dir + "10_3_2_1.mp3", myAudioLoadResultHandler),
                    TimeUnit.SECONDS.toMillis(sleeptime - COUNTDOWNDURATION));
            // schedule respawn announcement
            if (timesIndex == times.length - 4)
                respawnType = "third_last_respawn";
            else if (timesIndex == times.length - 3)
                respawnType = "second_last_respawn";
            else if (timesIndex == times.length - 2)
                respawnType = "last_respawn";
            else
                respawnType = sleeptime > 30 ? "long_respawn" : "respawn";

            timer.schedule(new AudioPlayerManagerTask(dir + respawnType + ".mp3", myAudioLoadResultHandler),
                    TimeUnit.SECONDS.toMillis(sleeptime));

            sleeptime += times[timesIndex + 1] - times[timesIndex];
            timesIndex++;
        }
    }

    @Override
    public boolean cancel() {
        final AudioManager audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
        GuildManager.getGuildManager(guild).setRespawnTimerTask(null);
        GuildManager.getAudioPlayerManager().getSourceManagers().clear();
        return super.cancel();
    }

    /**
     * 
     * @return -1 if something went wrong
     */
    public int getStartingRespawn() {
        return timesIndex;
    }

    /**
     * calculate possible offset, since the timer can be called within a running war
     * 
     * @return starting index for times[] array
     */
    private int calculateTimesIndex() {
        int index = 0;
        for (int respawnTime : times) {
            if (respawnTime <= offsetSeconds + COUNTDOWNDURATION)
                index++;
            else
                break;
        }
        return index;
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

    private class AudioPlayerManagerTask extends TimerTask {
        private MyAudioLoadResultHandler myAudioLoadResultHandler;
        private String identifier;

        public AudioPlayerManagerTask(String identifier, MyAudioLoadResultHandler myAudioLoadResultHandler) {
            this.identifier = identifier;
            this.myAudioLoadResultHandler = myAudioLoadResultHandler;
        }

        @Override
        public void run() {
            try {
                audioPlayerManager.loadItemOrdered(musicManager, identifier, myAudioLoadResultHandler).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }

    }

}
