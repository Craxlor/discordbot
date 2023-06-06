package com.github.craxlor.discordbot.command.module.music.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCMusic;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.music.MusicManager;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class Disconnect extends SCMusic {

    @Override
    @Nonnull
    public String getName() {
        return "disconnect";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Resets the queue and disconnects the bot from the channel.";
    }

    @Override
    public Reply executeMusic(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        disconnect(event.getGuild());
        return new Reply(event).onCommand(Status.SUCCESS,"The queue has been reset and the playback will be stopped.\nThe bot will now disconnect from the channel.");
    }

    public static void disconnect(Guild guild) {
        final MusicManager musicManager = GuildManager.getGuildManager(guild).getMusicManager();
        musicManager.scheduler.clearQueue();
        musicManager.scheduler.nextTrack();
        final AudioManager audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
    }

}
