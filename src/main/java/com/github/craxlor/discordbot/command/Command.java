package com.github.craxlor.discordbot.command;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.util.reply.Reply;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface Command<Event> {

    @Nonnull
    CommandData getCommandData();

    Reply execute(@Nonnull Event event) throws Exception;

    @Nonnull
    String getName();

    boolean isGuildOnly();
}
