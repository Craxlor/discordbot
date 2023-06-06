package com.github.craxlor.discordbot.util.reply;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.craxlor.discordbot.database.entity.YouTubeSearch;
import com.github.craxlor.discordbot.database.handler.DBGuildHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public class Reply {

    private GenericCommandInteractionEvent event;
    private EmbedBuilder embedBuilder;
    /**
     * true if the reply should be invisible for other users
     */
    private boolean isEphemeral;
    @Nullable
    private Status status;

    @Nullable
    private String message;

    public Reply(GenericCommandInteractionEvent event) {
        this.event = event;
        isEphemeral = true;
        embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(event.getGuild());
    }

    /**
     * 
     * @param isEphemeral true if the reply should be invisible for other users
     */
    public void deferReply(boolean isEphemeral) {
        this.isEphemeral = isEphemeral;
        event.deferReply(isEphemeral).queue();

    }

    public Reply onCommand(Status status, String message) {
        embedBuilder.setCommandFormat(event, status, message);
        this.status = status;
        this.message = message;
        return this;
    }

    @SuppressWarnings("null")
    public Reply onMusic(@Nullable AudioTrackInfo audioTrackInfo, @Nullable YouTubeSearch youTubeSearch) {
        embedBuilder.addMusicFields(audioTrackInfo, youTubeSearch);
        // send reply message in music log channel
        Guild guild = event.getGuild();
        long id = new DBGuildHandler().getEntity(guild.getIdLong()).getMusicLog_id();
        // musiclog exists
        if (id > -1) {
            // current channel is musicLog
            if (event.getChannel().getIdLong() == id)
                isEphemeral = false;
            else
                guild.getTextChannelById(id).sendMessageEmbeds(embedBuilder.build()).queue();
        }
        return this;
    }

    public void send() {
        if (event.isAcknowledged() == false)
            event.deferReply().setEphemeral(isEphemeral).queue();
        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

    }

    public void setEmbedBuilder(EmbedBuilder embedBuilder) {
        this.embedBuilder = embedBuilder;
    }

    /**
     * 
     * @param isEphemeral True, if the reply should be invisible for other users
     */
    public void setEphemeral(boolean isEphemeral) {
        this.isEphemeral = isEphemeral;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Nonnull
    @SuppressWarnings("null")
    public Status getStatus() {
        if (status == null)
            return Status.ERROR;
        else
            return status;
    }

    @Nonnull
    @SuppressWarnings("null")
    public String getMessage() {
        if (message == null)
            return "something went wrong";
        else
            return message;
    }
}
