package com.github.craxlor.discordbot.command.module.core.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class Clear extends SlashCommand {

    private static final String OPT_NAME = "amount";
    private static final String OPT_DESCRIPTION = "How many messages should be deleted? (Default 100)";

    public Clear() {
        commandData.addOption(OptionType.INTEGER, OPT_NAME, OPT_DESCRIPTION);
    }

    @Override
    @Nonnull
    public String getName() {
        return "clear";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Clears the last messages from this channel.";
    }

    @Override
    public Reply execute(@Nonnull SlashCommandInteractionEvent event) throws Exception {
        OptionMapping amountOption = event.getOption(OPT_NAME); // This is configured to be optional so
        // check for null
        int amount = amountOption == null
                ? 100 // default 100
                : (int) Math.min(200, Math.max(2, amountOption.getAsLong())); // enforcement: must be between 2-200

        event.getMessageChannel().getIterableHistory().takeAsync(amount)
                .thenAccept(event.getMessageChannel()::purgeMessages);

        Reply reply = new Reply(event).onCommand(Status.SUCCESS, "Deleting " + amount + " messages from this channel.");
        reply.setEphemeral(true);
        return reply;

    }

    @Override
    public boolean isGuildOnly() {
        return false;
    }

}
