package com.github.craxlor.discordbot.command.module.music.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SCMusic;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.music.MusicManager;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Queue extends SCMusic {
    private static final String CLEAR_NAME = "clear";
    private static final String CLEAR_DESCRIPTION = "Clears the current queue.";
    private static final String SHOW_NAME = "show";
    private static final String SHOW_DESCRIPTION = "Shows the current queue.";
    private static final String SHUFFLE_NAME = "shuffle";
    private static final String SHUFFLE_DESCRIPTION = "Shuffles the current queue.";
    private static final String SIZE_NAME = "size";
    private static final String SIZE_DESCRIPTION = "Shows the current queue size.";

    public Queue() {
        SubcommandData clear = new SubcommandData(CLEAR_NAME, CLEAR_DESCRIPTION);
        SubcommandData show = new SubcommandData(SHOW_NAME, SHOW_DESCRIPTION);
        SubcommandData shuffle = new SubcommandData(SHUFFLE_NAME, SHUFFLE_DESCRIPTION);
        SubcommandData size = new SubcommandData(SIZE_NAME, SIZE_DESCRIPTION);
        commandData.addSubcommands(show, clear, shuffle, size);
    }

    @Override
    @Nonnull
    public String getName() {
        return "queue";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "manage the tracks in the queue";
    }

    @Override
    @SuppressWarnings("null")
    public Reply executeMusic(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        String subcommandName = event.getSubcommandName();
        Guild guild = event.getGuild();
        Member member = event.getMember();
        final MusicManager musicManager = GuildManager.getGuildManager(guild).getMusicManager();
        String message = "";
        if (subcommandName.equals(CLEAR_NAME)) {
            musicManager.scheduler.clearQueue();
            message = "the queue has been cleared by " + member.getAsMention() + ".";
        } else if (subcommandName.equals(SHOW_NAME)) {
            PrivateChannel memberPrivateChanel = member.getUser().openPrivateChannel().complete();
            String trackNames = "";
            final java.util.Queue<AudioTrack> queue = musicManager.scheduler.getQueue();
            if (queue.isEmpty())
                message = "The queue is currently empty.";
            else {
                int tracknumber = 1;
                for (final AudioTrack track : queue) {
                    String title = track.getInfo().title.replaceAll("||", "");
                    trackNames += "**" + tracknumber + ".** " + title + "\n";
                    tracknumber++;
                    if (trackNames.length() > 1000) {
                        sendMessage(memberPrivateChanel, trackNames);
                        trackNames = "";
                    }
                }
                if (!trackNames.isBlank())
                    sendMessage(memberPrivateChanel, trackNames);

                message = "The current queue will be listed in your DM's.";
            }
        } else if (subcommandName.equals(SHUFFLE_NAME)) {
            if (musicManager.scheduler.getQueue().isEmpty()) {
                message = "You shuffled an empty queue :nerd:";
            } else {
                musicManager.scheduler.shuffle();
                message = ":twisted_rightwards_arrows: The queue has been shuffled.";
            }
        } else if (subcommandName.equals(SIZE_NAME)) {
            int size = musicManager.scheduler.getQueue().size();
            message = "There are " + size + " tracks in the current queue.";
        }
        return new Reply(event).onCommand(Status.SUCCESS, message);
    }

    private static void sendMessage(final PrivateChannel channel, @Nonnull final String message) {
        channel.sendMessage(message).queue();
    }

}
