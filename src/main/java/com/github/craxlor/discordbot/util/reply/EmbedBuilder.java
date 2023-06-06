package com.github.craxlor.discordbot.util.reply;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.github.craxlor.discordbot.database.entity.YouTubeSearch;
import com.github.craxlor.discordbot.database.handler.DBGuildHandler;
import com.github.craxlor.jReddit.RedditPost;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public class EmbedBuilder extends net.dv8tion.jda.api.EmbedBuilder {

    @SuppressWarnings("null")
    public EmbedBuilder addMusicFields(@Nullable AudioTrackInfo trackInfo, @Nullable YouTubeSearch youTubeSearch) {
        // track name field
        if (youTubeSearch != null) {
            setThumbnail(youTubeSearch.getVideoThumbnailURL());
            addField("Track", "[" + youTubeSearch.getVideo_title() + "](" + youTubeSearch.getVideoURL() + ")", false);
        } else if (trackInfo != null)
            addField("Track", "[" + trackInfo.title + "](" + trackInfo.uri + ")", false);

        // track length field
        if (trackInfo != null) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(trackInfo.length);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) % 60;
            String second = String.valueOf(seconds);
            if (second.length() < 2)
                second = "0" + second;
            addField("Length", minutes + ":" + second + " minutes", true);
        }
        // channel field
        if (trackInfo != null) {
            if (youTubeSearch != null)
                addField("Youtube channel", "[" + trackInfo.author + "](" + youTubeSearch.getChannelURL() + ")", true);
            else
                addField("Youtube channel", trackInfo.author, true);
        }
        return this;
    }

    public EmbedBuilder setRedditFormat(Guild guild, RedditPost redditPost) {
        setColor(guild);
        setImage(redditPost.getUrl_overridden_by_dest());
        setTitle(redditPost.getTitle(), redditPost.getPermalink());
        setFooter(" 👍 " + redditPost.getUps() + " |" + " 💬 " + redditPost.getNum_comments() + " - "
                + "r/" + redditPost.getSubReddit());
        return this;
    }

    public EmbedBuilder setCommandFormat(GenericCommandInteractionEvent event, Status status, String statusDetail) {
        setDescription(statusDetail);
        switch (status) {
            case SUCCESS -> {
                setTitle(event.getFullCommandName());
                setThumbnail("https://raw.githubusercontent.com/twitter/twemoji/master/assets/72x72/2705.png");
            }
            default -> {
                setTitle(status.toString().toLowerCase());
                setThumbnail("https://raw.githubusercontent.com/twitter/twemoji/master/assets/72x72/274c.png");
            }
        }
        setFooter(event.getUser().getName() + " used " + event.getFullCommandName() + " | "
                + event.getJDA().getSelfUser().getName(), event.getUser().getEffectiveAvatarUrl());
        return this;
    }

    public EmbedBuilder setColor(@Nullable Guild guild) {
        Color color;
        Random random = new Random();
        if (guild != null) {
            String hex = new DBGuildHandler().getEntity(guild.getIdLong()).getColorHex();
            if (hex != null) {
                color = Color.decode(hex);
                setColor(color);
                return this;
            }
        }
        color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        setColor(color);
        return this;
    }
}
