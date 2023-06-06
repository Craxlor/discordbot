package com.github.craxlor.discordbot.command.module.core.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Ping extends SlashCommand {

    @Override
    @Nonnull
    public String getName() {
        return "ping";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Pong!";
    }

    @Override
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        Reply reply = new Reply(event).onCommand(Status.SUCCESS, "Pong!");
        reply.setEphemeral(true);
        return reply;

    }

    @Override
    public boolean isGuildOnly() {
        return false;
    }

}
