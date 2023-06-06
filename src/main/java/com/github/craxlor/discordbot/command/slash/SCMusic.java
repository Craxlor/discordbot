package com.github.craxlor.discordbot.command.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.module.music.slash.Play;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class SCMusic extends SlashCommand {

    @Override
    public boolean isGuildOnly() {
        return true;
    }

    @Override
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        if (canBeExecuted(event)) {
            return executeMusic(event);
        }
        return new Reply(event).onCommand(Status.ERROR, "something went wrong");
    }

    protected abstract Reply executeMusic(@Nonnull SlashCommandInteractionEvent event) throws Exception;

    @SuppressWarnings("null")
    public boolean canBeExecuted(@Nonnull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        Reply reply = new Reply(event);
        // is a respawntimer running at the moment
        if (GuildManager.getGuildManager(guild).getRespawnTimerTask() != null) {
            reply.onCommand(Status.FAIL,
                    "there is a respawntimer running at the moment, so the music module is blocked").send();
            return false;
        }
        // is member in a voiceChannel
        if (member.getVoiceState().inAudioChannel() == false) {
            reply.onCommand(Status.FAIL, "you have to be in a voice channel").send();
            return false;
        }

        /**
         * check if member and bot are in the same voiceChannel
         * for all musicCommands except Play
         */
        if (this instanceof Play == false) {
            // is bot in a voiceChannel
            Member bot = guild.getSelfMember();
            if (bot.getVoiceState().inAudioChannel() == false) {
                reply.onCommand(Status.FAIL, "the bot is not in a voice channel").send();
                return false;
            }
            // are member and bot in the same voiceChannel
            if (bot.getVoiceState().getChannel().getIdLong() != member.getVoiceState().getChannel().getIdLong()) {
                reply.onCommand(Status.FAIL, "you have to be in the same voice channel").send();
                return false;
            }
        }

        return true;
    }
}
