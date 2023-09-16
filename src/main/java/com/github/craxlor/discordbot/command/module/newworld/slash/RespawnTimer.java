package com.github.craxlor.discordbot.command.module.newworld.slash;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.newworld.RespawnTimerTask;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class RespawnTimer extends SlashCommand {

    private static final String START_NAME = "start";
    private static final String START_DESCRIPTION = "start respawntimer";
    private static final String STOP_NAME = "stop";
    private static final String STOP_DESCRIPTION = "stop respawntimer";
    private static final String VOICECHANNEL_OPT_NAME = "voicechannel";
    private static final String VOICECHANNEL_OPT_DESCRIPTION = "missing";
    private static final String TIME_OPT_NAME = "time";
    private static final String TIME_OPT_DESCRIPTION = "missing";
    private static final String TIME_CHOICE_NOW = "now";

    public RespawnTimer() {
        SubcommandData start = new SubcommandData(START_NAME, START_DESCRIPTION);
        OptionData voiceChannel = new OptionData(OptionType.CHANNEL, VOICECHANNEL_OPT_NAME,
                VOICECHANNEL_OPT_DESCRIPTION);
        OptionData time = new OptionData(OptionType.STRING, TIME_OPT_NAME, TIME_OPT_DESCRIPTION);
        time.addChoice(TIME_CHOICE_NOW, TIME_CHOICE_NOW);
        int hour = 13;
        String choice;
        while (hour < 24) {
            choice = String.valueOf(hour) + ":00";
            time.addChoice(choice, choice);
            choice = String.valueOf(hour) + ":30";
            time.addChoice(choice, choice);
            hour++;
        }
        time.addChoice("00:00", "00:00");
        start.addOptions(time, voiceChannel);
        SubcommandData stop = new SubcommandData(STOP_NAME, STOP_DESCRIPTION);
        commandData.addSubcommands(start, stop);
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        String subcommandName = event.getSubcommandName();
        GuildManager guildManager = GuildManager.getGuildManager(event.getGuild());
        Reply reply = new Reply(event);
        switch (subcommandName) {
            case START_NAME -> {
                if (guildManager.getRespawnTimerTask() != null)
                    return reply.onCommand(Status.FAIL, "there is a respawntimer running currently");

                reply = start(event);
            }
            case STOP_NAME -> {
                if (guildManager.getRespawnTimerTask() == null)
                    return reply.onCommand(Status.FAIL, "there is no respawntimer running currently");

                guildManager.getRespawnTimerTask().cancel();
                reply.onCommand(Status.SUCCESS, "stopped the respawntimer");
            }
        }
        return reply;
    }

    @SuppressWarnings("null")
    private Reply start(@Nonnull SlashCommandInteractionEvent event) throws ParseException {
        String time = event.getOption(TIME_OPT_NAME, OptionMapping::getAsString);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        if (time == null) {
        } else if (time != null && time.equalsIgnoreCase(TIME_CHOICE_NOW) == false) {
            date = new SimpleDateFormat("hh:mm").parse(time);
            calendar.setTime(date);
        }

        VoiceChannel voiceChannel;
        GuildChannelUnion guildChannelUnion = event.getOption(VOICECHANNEL_OPT_NAME, OptionMapping::getAsChannel);
        if (guildChannelUnion == null) {
            GuildVoiceState guildVoiceState = event.getMember().getVoiceState();
            if (guildVoiceState.inAudioChannel() == false)
                return new Reply(event).onCommand(Status.FAIL,
                        "You have to be in a voice channel or provide one using the given parameter.");

            voiceChannel = guildVoiceState.getChannel().asVoiceChannel();
        } else {
            if (guildChannelUnion.getType() != ChannelType.VOICE)
                return new Reply(event).onCommand(Status.FAIL, "The given channel has to be a voice channel.");

            voiceChannel = guildChannelUnion.asVoiceChannel();
        }

        Guild guild = event.getGuild();

        Timer timer = new Timer(event.getGuild().getName() + "-RespawnTimer-Thread", true);
        long offsetSeconds = calendar.get(Calendar.SECOND) + TimeUnit.MINUTES.toSeconds(calendar.get(Calendar.MINUTE) % 30);
        RespawnTimerTask respawnTimerTask = new RespawnTimerTask(guild, voiceChannel, offsetSeconds);
        timer.schedule(respawnTimerTask, date);

        Reply reply = new Reply(event).onCommand(Status.SUCCESS, String.format("""
            Scheduled task for the date: 
            **%s**

            Starting respawn announcements with respawn number: **%s**""", calendar.getTime(),respawnTimerTask.getStartingRespawn()));
        reply.setEphemeral(false);
        return reply;
    }

    @Override
    @Nonnull
    public String getName() {
        return "respawntimer";
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "set a respawn timer";
    }
}
