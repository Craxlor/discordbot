package com.github.craxlor.discordbot.command.context;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.Command;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class UserContextCommand implements Command<UserContextInteractionEvent>{
    
    @Nonnull
    protected CommandData commandData;
    
    public UserContextCommand() {
        commandData = Commands.context(net.dv8tion.jda.api.interactions.commands.Command.Type.USER, getName());
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
