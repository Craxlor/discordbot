package com.github.craxlor.discordbot.command.module.reddit.slash;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.parser.ParseException;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.entity.RedditTask;
import com.github.craxlor.discordbot.database.handler.DBGuildHandler;
import com.github.craxlor.discordbot.database.handler.DBRedditTaskHandler;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;
import com.github.craxlor.jReddit.Reddit;
import com.github.craxlor.jReddit.subreddit.SubReddit;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class RedditGallery extends SlashCommand {

    private static final String CREATE_NAME = "create";
    private static final String CREATE_DESCRIPTION = "Creates a reddit gallery.";
    private static final String CREATE_OPT_SUBREDDIT_NAME = "subreddit";
    private static final String CREATE_OPT_SUBREDDIT_DESCRIPTION = "Define a subreddit.";
    private static final String CREATE_OPT_FIRSTTIME_NAME = "firsttime";
    private static final String CREATE_OPT_FIRSTTIME_DESCRIPTION = "Define the starting time for the first post.";
    private static final String CREATE_OPT_CHOICE_MINUTE = "MINUTE";
    private static final String CREATE_OPT_CHOICE_HOUR = "HOUR";
    private static final String CREATE_OPT_PERIOD_NAME = "period";
    private static final String CREATE_OPT_PERIOD_DESCRIPTION = "Define the posting interval (milliseconds). Values < 30000 will be set to 30000";
    private static final String CREATE_OPT_CHANNEL_NAME = "textchannel";
    private static final String CREATE_OPT_CHANNEL_DESCRIPTION = "bind a textchannel.";
    private static final String DELETE_NAME = "delete";
    private static final String DELETE_DESCRIPTION = "Deletes a reddit gallery.";
    private static final String DELETE_OPT_NAME = "subreddit";
    private static final String DELETE_OPT_DESCRIPTION = "Define which subreddit specific task shall be removed.";
    private static final String REMOVE_OPT_DELETE_NAME = "delete";
    private static final String REMOVE_OPT_DELETE_DESCRIPTION = "Select if the channel should be deleted.";

    public RedditGallery() {
        // create subCommand
        SubcommandData create = new SubcommandData(CREATE_NAME, CREATE_DESCRIPTION);
        create.addOption(OptionType.STRING, CREATE_OPT_SUBREDDIT_NAME, CREATE_OPT_SUBREDDIT_DESCRIPTION, true);
        OptionData firstTime = new OptionData(OptionType.STRING, CREATE_OPT_FIRSTTIME_NAME,
                CREATE_OPT_FIRSTTIME_DESCRIPTION, true); // firstTime
        firstTime.addChoice(CREATE_OPT_CHOICE_MINUTE, CREATE_OPT_CHOICE_MINUTE);
        firstTime.addChoice(CREATE_OPT_CHOICE_HOUR, CREATE_OPT_CHOICE_HOUR);
        create.addOptions(firstTime);
        create.addOption(OptionType.INTEGER, CREATE_OPT_PERIOD_NAME, CREATE_OPT_PERIOD_DESCRIPTION, true);// period
        create.addOption(OptionType.CHANNEL, CREATE_OPT_CHANNEL_NAME, CREATE_OPT_CHANNEL_DESCRIPTION);
        // delete subCommand
        SubcommandData delete = new SubcommandData(DELETE_NAME, DELETE_DESCRIPTION);
        delete.addOption(OptionType.STRING, DELETE_OPT_NAME, DELETE_OPT_DESCRIPTION, true);
        delete.addOption(OptionType.BOOLEAN, REMOVE_OPT_DELETE_NAME, REMOVE_OPT_DELETE_DESCRIPTION);
        commandData.addSubcommands(create, delete);
    }

    @Override
    @Nonnull
    public String getName() {
        return "redditgallery";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Post images from a subreddit to a text channel at an interval.";
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws ParseException, IOException {
        switch (event.getSubcommandName()) {
            case CREATE_NAME -> {
                return create(event);
            }
            case DELETE_NAME -> {
                return delete(event);
            }
        }
        return null;
    }

    @SuppressWarnings("null")
    private Reply create(SlashCommandInteractionEvent event) throws ParseException, IOException {
        Guild guild = event.getGuild();
        GuildManager guildManager = GuildManager.getGuildManager(guild);
        String subredditName = event.getOption(CREATE_OPT_SUBREDDIT_NAME).getAsString();
        Reddit reddit = new Reddit();
        SubReddit subReddit = reddit.getSubReddit(subredditName);
        if (subReddit == null) {
            return new Reply(event).onCommand(Status.FAIL,
                    "The subreddit: " + subredditName + " does not exist!\nPlease check for the correct spelling.");
        }
        // check if there's already a redditTask for this subreddit on this guild
        // stop execution if true
        DBRedditTaskHandler dbRedditTaskHandler = new DBRedditTaskHandler();
        // init database session
        Session session = Database.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        List<RedditTask> redditTasks = dbRedditTaskHandler.getRedditTasksByGuild(session, guild.getIdLong());
        for (RedditTask redditTask : redditTasks) {
            if (redditTask.getSubreddit().equalsIgnoreCase(subredditName)) {
                // close database session
                transaction.commit();
                Database.getSessionFactory().getCurrentSession().close();
                return new Reply(event).onCommand(Status.FAIL,
                        "There is already an ongoing task for the subreddit **" + subredditName + "** on this guild!");
            }
        }
        // check if the RedditScheduler already manages 10 tasks
        if (guildManager.getRedditScheduler().size() >= 10) {
            // close database session
            transaction.commit();
            Database.getSessionFactory().getCurrentSession().close();
            return new Reply(event).onCommand(Status.FAIL,
                    "You can only maintain a maximum of 10 reddit galleries per guild.");
        }

        // setup textchannel
        TextChannel textChannel = null;
        if (event.getOption(CREATE_OPT_CHANNEL_NAME) == null) {
            textChannel = guild.createTextChannel(subredditName).complete();
        } else {
            GuildChannelUnion channelUnion = event.getOption(CREATE_OPT_CHANNEL_NAME).getAsChannel();
            // validate channel type of provided channel
            if (channelUnion.getType() != ChannelType.TEXT) {
                // close database session
                transaction.commit();
                Database.getSessionFactory().getCurrentSession().close();
                return new Reply(event).onCommand(Status.FAIL,
                        "The provided channel has to be a textchannel!");
            }
            textChannel = channelUnion.asTextChannel();
        }
        // mark channel as nsfw if necessary
        if (subReddit.isOver18()) {
            textChannel.getManager().setNSFW(true).queue();
        }

        Long period = event.getOption(CREATE_OPT_PERIOD_NAME).getAsLong();
        if (period < 300000)
            period = 300000l;
        String firstTime = event.getOption(CREATE_OPT_FIRSTTIME_NAME).getAsString();
        // register redditTask in database
        DBGuildHandler dbGuildHandler = new DBGuildHandler();
        RedditTask redditTask = new RedditTask();
        redditTask.setChannel_id(textChannel.getIdLong());
        redditTask.setFirstTime(firstTime);
        redditTask.setRedditTasks_guild(dbGuildHandler.getEntity(session, guild.getIdLong()));
        redditTask.setPeriod(period);
        redditTask.setSubreddit(subredditName);
        dbRedditTaskHandler.insert(session, redditTask);

        // schedule the task
        guildManager.getRedditScheduler().schedule(redditTask);
        // close database session
        transaction.commit();
        Database.getSessionFactory().getCurrentSession().close();
        return new Reply(event).onCommand(Status.SUCCESS,
                "Created a gallery for the subreddit: " + subredditName + ".");
    }

    @SuppressWarnings("null")
    private Reply delete(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        String subreddit = event.getOption(DELETE_OPT_NAME).getAsString();
        DBRedditTaskHandler dbRedditTaskHandler = new DBRedditTaskHandler();
        // init database session
        Session session = Database.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        RedditTask redditTask = dbRedditTaskHandler.getEntity(session, guild.getIdLong(), subreddit);
        if (redditTask == null)
            return new Reply(event).onCommand(Status.FAIL,
                    "Couldn't find a database entry for the subreddit: " + subreddit);
        // remove redditTask from DB
        dbRedditTaskHandler.removeRedditTask(session, redditTask.getChannel_id(), subreddit);
        // close database session
        transaction.commit();
        Database.getSessionFactory().getCurrentSession().close();
        // stop task
        GuildManager.getGuildManager(guild).getRedditScheduler().stop(redditTask);
        // delete textChannel
        OptionMapping optionMapping = event.getOption(REMOVE_OPT_DELETE_NAME);
        if (optionMapping != null && optionMapping.getAsBoolean())
            guild.getTextChannelById(redditTask.getChannel_id()).delete().queue();
        return new Reply(event).onCommand(Status.SUCCESS, "Deleted the gallery for the subreddit: " + subreddit + ".");
    }
}
