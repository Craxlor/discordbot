package com.github.craxlor.discordbot.util.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class MusicManager {


	/**
	 * Audio player for the guild.
	 */
	public final AudioPlayer player;
	/**
	 * Track scheduler for the player.
	 */
	public final TrackScheduler scheduler;

	public MusicManager(AudioPlayerManager audioPlayerManager) {
		player = audioPlayerManager.createPlayer();
		scheduler = new TrackScheduler(player);
		player.addListener(scheduler);
	}

	/**
	 * Gets the send handler.
	 *
	 * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
	 */
	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(player);
	}

	/**
	 * Checks if is playing.
	 *
	 * @return true, if is playing
	 */
	public boolean isPlaying() {
		return player.getPlayingTrack() != null;
	}


}