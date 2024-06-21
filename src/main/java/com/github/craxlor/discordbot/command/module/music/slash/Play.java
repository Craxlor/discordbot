package com.github.craxlor.discordbot.command.module.music.slash;

import javax.annotation.Nonnull;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.github.craxlor.discordbot.command.slash.SCMusic;
import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.entity.YouTubeVideoData;
import com.github.craxlor.discordbot.database.handler.DBYoutubeVideoHandler;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.music.MusicManager;
import com.github.craxlor.discordbot.util.music.MyAudioLoadResultHandler;
import com.github.craxlor.discordbot.util.music.SpotifyHelper;
import com.github.craxlor.discordbot.util.music.YouTubeHelper;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class Play extends SCMusic {

    private static final String INPUT_OPT_NAME = "input";
    private static final String INPUT_OPT_DESCRIPTION = "Provide a Youtube video/playlist; Spotify track url; searchTerm(yt-Search) to play.";
    private static final String MODE_OPT_NAME = "mode";
    private static final String MODE_OPT_DESCRIPTION = "Provide a Youtube video/playlist; Spotify track url; searchTerm(yt-Search) to play.";
    public static final String MODE_CHOICE_NOW = "now";
    public static final String MODE_CHOICE_NEXT = "next";

    public Play() {
        OptionData searchTerm = new OptionData(OptionType.STRING, INPUT_OPT_NAME, INPUT_OPT_DESCRIPTION, true);
        OptionData mode = new OptionData(OptionType.STRING, MODE_OPT_NAME, MODE_OPT_DESCRIPTION);
        mode.addChoice(MODE_CHOICE_NOW, MODE_CHOICE_NOW);
        mode.addChoice(MODE_CHOICE_NEXT, MODE_CHOICE_NEXT);
        commandData.addOptions(searchTerm, mode);
    }

    @Override
    @Nonnull
    public String getName() {
        return "play";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "play music";
    }

    @Override
    @SuppressWarnings("null")
    public Reply executeMusic(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        String input = event.getOption(INPUT_OPT_NAME).getAsString();
        String mode = event.getOption(MODE_OPT_NAME, OptionMapping::getAsString);
        if (mode == null)
            mode = "default";
        Member member = event.getMember();
        Guild guild = event.getGuild();
        final MusicManager musicManager = GuildManager.getGuildManager(guild).getMusicManager();

        // check if bot is already playing
        Member bot = guild.getSelfMember();
        if (bot.getVoiceState().inAudioChannel() && musicManager.isPlaying()) {
            // check if member & bot are not in the same channel
            if (bot.getVoiceState().getChannel().getIdLong() != member.getVoiceState().getChannel().getIdLong()) {
                return new Reply(event).onCommand(Status.FAIL, "You have to be in the same voice channel as the bot!");

            }
        }

        YouTubeVideoData youTubeVideoData = null;

        if (input.contains("http") == false && input.contains("www.") == false) {
            // assume that input is not containing an url but a searchTerm to look up
            youTubeVideoData = YouTubeHelper.findVideo(input);
            input = youTubeVideoData.getVideoURL();
        }
        // check if the YT-Video is known in YouTubeStorage
        else if (input.contains("youtube.com/watch?v=")) {
            String videoId;
            // get videoId
            if (input.contains("&list"))
                videoId = input.substring(input.lastIndexOf("?v=") + 3, input.indexOf("&list"));
            else
                videoId = input.substring(input.lastIndexOf("?v=") + 3, input.length());
            // init database session
            Session session = Database.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            // retrieve YouTubeStorage entry to provide more information in command-reply
            youTubeVideoData = new DBYoutubeVideoHandler().getEntity(session, videoId);
            // close database session
            transaction.commit();
            Database.getSessionFactory().getCurrentSession().close();
        }
        // check if provided url is from spotify
        else if (input.contains("open.spotify")) {
            if (input.contains("track") == false)
                return new Reply(event).onCommand(Status.FAIL, "I just support spotify **tracks**!");
            String searchTerm = convertSpotifyUrlToSearchTerm(input);

            youTubeVideoData = YouTubeHelper.findVideo(searchTerm);
            input = youTubeVideoData.getVideoURL();
        }
        System.out.println(input);
        MyAudioLoadResultHandler audioLoadResultHandler = new MyAudioLoadResultHandler(musicManager, member, mode,
                input);

        GuildManager.getAudioPlayerManager().loadItemOrdered(musicManager, input, audioLoadResultHandler).get();


        return new Reply(event)
                .onCommand(audioLoadResultHandler.getStatus(), audioLoadResultHandler.getMessage())
                .onMusic(audioLoadResultHandler.getTrackInfo(), youTubeVideoData);
    }

    @SuppressWarnings("null")
    private String convertSpotifyUrlToSearchTerm(@Nonnull String url) {
        // get track from spotify
        Track track = SpotifyHelper.getTrack(url);
        // build searchTerm
        String artists = "";
        for (ArtistSimplified artistSimplified : track.getArtists()) {
            artists += " " + artistSimplified.getName();
        }
        return track.getName() + artists;
    }
}
