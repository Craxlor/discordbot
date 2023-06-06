package com.github.craxlor.discordbot.command.module.core.slash;

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.Commandlist;
import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.database.entity.DiscordServer;
import com.github.craxlor.discordbot.database.handler.DBGuildHandler;
import com.github.craxlor.discordbot.util.Properties;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class Configurate extends SlashCommand {

    private static final String EMBEDCOLOR_NAME = "embed-color";
    private static final String EMBEDCOLOR_DESCRIPTION = "Configure the color for all embedded messages from the bot";
    private static final String EMBEDCOLOR_SET_NAME = "set";
    private static final String EMBEDCOLOR_SET_DESCRIPTION = "Configure the color for all embedded messages from the bot";
    private static final String EMBEDCOLOR_SET_OPT_NAME = "hex";
    private static final String EMBEDCOLOR_SET_OPT_DESCRIPTION = "Insert a hex code. Example: #c7b299";
    private static final String EMBEDCOLOR_REMOVE_NAME = "remove";
    private static final String EMBEDCOLOR_REMOVE_DESCRIPTION = "Removes the set color for all embedded messages from the bot";

    private static final String MODULE_NAME = "module";
    private static final String MODULE_DESCRIPTION = "add or remove a module of the bot";
    private static final String MODULE_ADD_NAME = "add";
    private static final String MODULE_ADD_DESCRIPTION = "Add a module.";
    private static final String MODULE_REMOVE_NAME = "remove";
    private static final String MODULE_REMOVE_DESCRIPTION = "Remove a module.";
    private static final String MODULE_OPT_NAME = "module-option";
    private static final String MODULE_OPT_DESCRIPTION = "Select a module.";

    private static final String MUSICLOG_NAME = "musiclog";
    private static final String MUSICLOG_DESCRIPTION = "set or remove a music log to log music command usage";
    private static final String MUSICLOG_SET_NAME = "set";
    private static final String MUSICLOG_SET_DESCRIPTION = "Sets a textchannel as a music log.";
    private static final String MUSICLOG_SET_OPT_NAME = "music-channel";
    private static final String MUSICLOG_SET_OPT_DESCRIPTION = "Select the textchannel where music commands should be logged.";
    private static final String MUSICLOG_REMOVE_NAME = "remove";
    private static final String MUSICLOG_REMOVE_DESCRIPTION = "Removes a textchannel as a music log.";
    private static final String MUSICLOG_REMOVE_OPT_NAME = "delete";
    private static final String MUSICLOG_REMOVE_OPT_DESCRIPTION = "Select if the channel should be deleted.";

    public Configurate() {
        SubcommandGroupData embedColor = new SubcommandGroupData(EMBEDCOLOR_NAME, EMBEDCOLOR_DESCRIPTION);
        SubcommandData embedColorSet = new SubcommandData(EMBEDCOLOR_SET_NAME, EMBEDCOLOR_SET_DESCRIPTION);
        embedColorSet.addOption(OptionType.STRING, EMBEDCOLOR_SET_OPT_NAME, EMBEDCOLOR_SET_OPT_DESCRIPTION, true);
        SubcommandData embedColorRemove = new SubcommandData(EMBEDCOLOR_REMOVE_NAME, EMBEDCOLOR_REMOVE_DESCRIPTION);
        embedColor.addSubcommands(embedColorSet, embedColorRemove);

        SubcommandGroupData module = new SubcommandGroupData(MODULE_NAME, MODULE_DESCRIPTION);
        SubcommandData moduleAdd = new SubcommandData(MODULE_ADD_NAME, MODULE_ADD_DESCRIPTION);
        SubcommandData moduleRemove = new SubcommandData(MODULE_REMOVE_NAME, MODULE_REMOVE_DESCRIPTION);
        OptionData moduleOption = new OptionData(OptionType.STRING, MODULE_OPT_NAME, MODULE_OPT_DESCRIPTION, true,
                false);
        moduleOption.addChoice(Commandlist.AUTOROOM, Commandlist.AUTOROOM);
        moduleOption.addChoice(Commandlist.MUSIC, Commandlist.MUSIC);
        moduleOption.addChoice(Commandlist.REDDIT, Commandlist.REDDIT);
        moduleOption.addChoice(Commandlist.NEWWORLD, Commandlist.NEWWORLD);
        moduleAdd.addOptions(moduleOption);
        moduleRemove.addOptions(moduleOption);
        module.addSubcommands(moduleAdd, moduleRemove);

        SubcommandGroupData musicLog = new SubcommandGroupData(MUSICLOG_NAME, MUSICLOG_DESCRIPTION);
        SubcommandData musicSet = new SubcommandData(MUSICLOG_SET_NAME, MUSICLOG_SET_DESCRIPTION);
        musicSet.addOption(OptionType.CHANNEL, MUSICLOG_SET_OPT_NAME, MUSICLOG_SET_OPT_DESCRIPTION, true);
        SubcommandData musicRemove = new SubcommandData(MUSICLOG_REMOVE_NAME, MUSICLOG_REMOVE_DESCRIPTION);
        musicRemove.addOption(OptionType.BOOLEAN, MUSICLOG_REMOVE_OPT_NAME, MUSICLOG_REMOVE_OPT_DESCRIPTION);
        musicLog.addSubcommands(musicSet, musicRemove);

        commandData.addSubcommandGroups(embedColor, module, musicLog);
    }

    @SuppressWarnings("null")
    @Override
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        String subcommandGroup = event.getSubcommandGroup();
        Guild guild = event.getGuild();
        DBGuildHandler dbGuildHandler = new DBGuildHandler();
        DiscordServer discordServer = dbGuildHandler.getEntity(guild.getIdLong());
        Reply reply = new Reply(event).onCommand(Status.ERROR, "Something went wrong.");
        switch (subcommandGroup) {
            case EMBEDCOLOR_NAME -> {
                reply = configEmbedColor(event, discordServer);
            }
            case MODULE_NAME -> {
                reply = configModule(event, discordServer);
            }
            case MUSICLOG_NAME -> {
                if (discordServer.getModulesAsList().contains("music"))
                    reply = configMusicLog(event, discordServer);
                else
                    return reply.onCommand(Status.FAIL,
                            "you cannot set a music log, until the music module is enabled");
            }
        }
        
        dbGuildHandler.update(discordServer);
        return reply;
    }

    @Override
    @Nonnull
    public String getName() {
        return "config";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Customize the bot for this guild as you want";
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

    @SuppressWarnings("null")
    public Reply configEmbedColor(SlashCommandInteractionEvent event, DiscordServer discordServer) {
        Reply reply = new Reply(event);
        switch (event.getSubcommandName()) {
            case EMBEDCOLOR_SET_NAME -> {
                String hexCode = event.getOption(EMBEDCOLOR_SET_OPT_NAME).getAsString();
                // verify input
                final Pattern pattern = Pattern.compile("^#[0-9A-F]{6}$", Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(hexCode).matches() == false)
                    return reply.onCommand(Status.FAIL, "Your input is probably wrong");

                discordServer.setColorHex(hexCode);
                reply.setEphemeral(false);
                return reply.onCommand(Status.SUCCESS,
                        "The color for embedded messages has been set to " + hexCode + ".");
            }
            case EMBEDCOLOR_REMOVE_NAME -> {
                discordServer.setColorHex(null);
                reply.setEphemeral(false);
                return reply.onCommand(Status.SUCCESS,
                        "The color for embedded messages will be random now.");
            }
            default -> {
                return reply.onCommand(Status.ERROR, "something went wrong");
            }
        }
    }

    @SuppressWarnings("null")
    public Reply configModule(SlashCommandInteractionEvent event, DiscordServer discordServer) {
        // module that shall be toggled
        String module = event.getOption(MODULE_OPT_NAME).getAsString();
        List<String> modules = discordServer.getModulesAsList();
        // commandlist for the guild
        Guild guild = event.getGuild();
        Commandlist commandlist = GuildManager.getGuildManager(guild).getCommandlist();
        Reply reply = new Reply(event);
        switch (event.getSubcommandName()) {
            case MODULE_ADD_NAME -> {
                // check if module has already been added
                if (modules != null && modules.contains(module))
                    return reply.onCommand(Status.FAIL, "the module " + module + " is already enabled");

                if (module.equalsIgnoreCase(Commandlist.MUSIC)) {
                    // check if all necessary entries exist in properties file
                    String yak = Properties.get("YOUTUBE_API_KEY");
                    String sci = Properties.get("SPOTIFY_CLIENT_ID");
                    String scs = Properties.get("SPOTIFY_CLIENT_SECRET");
                    if (yak == null || sci == null || scs == null)
                        return reply.onCommand(Status.FAIL,
                                "the music module requires a Youtube API Key and Spotify API Access (set tokens in .properties file)");

                    PrivateChannel privateChannel = event.getUser().openPrivateChannel().complete();
                    // music log channel notification
                    privateChannel.sendMessage(
                            "A log channel can be set up to better view the playlist activities of the bot.\n/musicLog bind")
                            .queue();
                }
                // edit database entry
                if (modules != null)
                    discordServer.setModules(discordServer.getModules() + "," + module);
                else
                    discordServer.setModules(module);
                commandlist.add(module);
                // update commandlist
                guild.updateCommands().addCommands(commandlist.getCommandData()).queue();

                reply.setEphemeral(false);
                return reply.onCommand(Status.SUCCESS, "Added the module: **" + module + "**");
            }
            case MODULE_REMOVE_NAME -> {
                if (modules == null || modules.contains(module) == false)
                    return reply.onCommand(Status.FAIL,
                            "the module " + module + " is not enbaled, therefore it cannot be disabled.");

                modules.remove(module);
                commandlist.remove(module);
                // update commandlist
                guild.updateCommands().addCommands(commandlist.getCommandData()).queue();
                
                String modulesString = null;
                for (String moduleString : modules) {
                    modulesString = modulesString + moduleString + ",";
                }
                // remove last ","
                modulesString.substring(0, modulesString.length() - 1);
                discordServer.setModules(modulesString);
                reply.setEphemeral(false);
                return reply.onCommand(Status.SUCCESS, "Removed the module: **" + module + "**");
            }
            default -> {
                return reply.onCommand(Status.ERROR, "something went wrong");
            }
        }
    }

    @SuppressWarnings("null")
    public Reply configMusicLog(SlashCommandInteractionEvent event, DiscordServer discordServer) {
        String subcommandName = event.getSubcommandName();
        Reply reply = new Reply(event);
        switch (subcommandName) {
            case MUSICLOG_SET_NAME -> {
                // set music channel id in config
                TextChannel tc = event.getOption(MUSICLOG_SET_OPT_NAME).getAsChannel().asTextChannel();
                discordServer.setMusicLog_id(tc.getIdLong());
                reply.setEphemeral(false);
                return reply.onCommand(Status.SUCCESS,
                        "The music log has been set to " + tc.getAsMention() + ".");
            }
            case MUSICLOG_REMOVE_NAME -> {
                long id = discordServer.getMusicLog_id();
                if (discordServer.getMusicLog_id() < 0)
                    return reply.onCommand(Status.FAIL, "There is no music log to remove!");

                discordServer.setMusicLog_id(-1);
                OptionMapping optionMapping = event.getOption(MUSICLOG_REMOVE_OPT_NAME);
                if (optionMapping != null && optionMapping.getAsBoolean())
                    event.getGuild().getTextChannelById(id).delete().queue();

                reply.setEphemeral(false);
                return reply.onCommand(Status.SUCCESS, "The music log has been removed.");
            }
            default -> {
                return reply.onCommand(Status.ERROR, "something went wrong");
            }
        }
    }
}
