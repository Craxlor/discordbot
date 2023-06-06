package com.github.craxlor.discordbot.command.context;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.Command;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class MessageContextCommand implements Command<MessageContextInteractionEvent> {

    @Nonnull
    protected CommandData commandData;

    public MessageContextCommand() {
        commandData = Commands.message(getName());
        commandData.setGuildOnly(isGuildOnly());
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return commandData;
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }
}
