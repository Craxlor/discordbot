package com.github.craxlor.discordbot.util.reddit;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

import com.github.craxlor.discordbot.util.reply.EmbedBuilder;
import com.github.craxlor.jReddit.RedditPost;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class RedditHelper {

    public static boolean canBePosted(RedditPost redditPost) {
        String post_hint, url = null, mimeType = null;
        FileNameMap fileNameMap = null;
        // check if post contains usable content
        if ((post_hint = redditPost.getPost_hint()) != null && post_hint.equals("image"))
            url = redditPost.getUrl_overridden_by_dest();
        // bot cannot post files bigger than 8 mb
        if (url == null || new File(url).length() > 8388608)
            return false;
        // check mimetype of file
        fileNameMap = URLConnection.getFileNameMap();
        mimeType = fileNameMap.getContentTypeFor(url);
        if (mimeType == null || mimeType.indexOf("image") == -1)
            return false;
        return true;
    }

    public static void sendRedditPost(TextChannel channel, RedditPost redditPost) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setRedditFormat(channel.getGuild(), redditPost);
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
