package com.github.craxlor.discordbot.command.module.core.context;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.context.UserContextCommand;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public class Poke extends UserContextCommand {

    @Override
    @SuppressWarnings("null")
    public Reply execute(@Nonnull UserContextInteractionEvent event) throws Exception {
        PrivateChannel privateChannel = event.getTarget().openPrivateChannel().complete();
        privateChannel.sendMessage(
                "you got poked by **" + event.getUser().getName() + "** through the discord: **" + event.getGuild().getName()+"**").queue();

        Reply reply = new Reply(event).onCommand(Status.SUCCESS, "you poked " + event.getTarget().getAsMention());
        reply.setEphemeral(true);
        return reply;
    }

    @Override
    @Nonnull
    public String getName() {
        return "poke";
    }

}
