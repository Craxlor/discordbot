package com.github.craxlor.discordbot.listener;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.github.craxlor.discordbot.command.Commandlist;
import com.github.craxlor.discordbot.command.context.MessageContextCommand;
import com.github.craxlor.discordbot.command.context.UserContextCommand;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.reply.LogHelper;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ContextInteractionListener extends ListenerAdapter {

    private static final Commandlist globalCommandlist = Commandlist.getEveryCommand().getGlobalCommands();

    @Override
    @SuppressWarnings("null")
    public void onUserContextInteraction(@Nonnull UserContextInteractionEvent event) {
        Logger logger = LoggerFactory.getLogger("sift");
        UserContextCommand command = null;
        if (event.getGuild() == null) {
            // global command
            MDC.put("filename", "global");
            command = (UserContextCommand) globalCommandlist.get(event.getName());
        } else {
            // guild command
            Guild guild = event.getGuild();
            MDC.put("filename", guild.getId());
            Commandlist guildCommandlist = GuildManager.getGuildManager(guild).getCommandlist();
            command = (UserContextCommand) guildCommandlist.get(event.getName());
        }

        // command has not been found
        if (command == null) {
            logger.warn(LogHelper.logCommand(event, Status.ERROR, "unknown command"));
            new Reply(event).onCommand(Status.ERROR, "unknown command").send();
            return;
        }
        // try to execute command
        try {
            Reply reply = command.execute(event);
            reply.send();
            logger.info(LogHelper.logCommand(event, reply.getStatus(), reply.getMessage()));
            return;

        } catch (Exception e) {
            String statusDetail = """
                    fatal error on command execution
                    error message:
                    %s
                    --------------------------------
                    localizied error message:
                    %s
                    """.formatted(e.getMessage(), e.getLocalizedMessage());
            new Reply(event).onCommand(Status.ERROR, statusDetail).send();
            logger.warn(LogHelper.logCommand(event, Status.ERROR, statusDetail));
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("null")
    public void onMessageContextInteraction(@Nonnull MessageContextInteractionEvent event) {
        Logger logger = LoggerFactory.getLogger("sift");
        MessageContextCommand command = null;
        if (event.getGuild() == null) {
            // global command
            MDC.put("filename", "global");
            command = (MessageContextCommand) globalCommandlist.get(event.getName());
        } else {
            // guild command
            Guild guild = event.getGuild();
            MDC.put("filename", guild.getId());
            Commandlist guildCommandlist = GuildManager.getGuildManager(guild).getCommandlist();
            command = (MessageContextCommand) guildCommandlist.get(event.getName());
        }

        // command has not been found
        if (command == null) {
            logger.warn(LogHelper.logCommand(event, Status.ERROR, "unknown command"));
            new Reply(event).onCommand(Status.ERROR, "unknown command").send();
            return;
        }
        // try to execute command
        try {
            Reply reply = command.execute(event);
            reply.send();
            logger.info(LogHelper.logCommand(event, reply.getStatus(), reply.getMessage()));
            return;

        } catch (Exception e) {
            String statusDetail = """
                    fatal error on command execution
                    error message:
                    %s
                    --------------------------------
                    localizied error message:
                    %s
                    """.formatted(e.getMessage(), e.getLocalizedMessage());
            new Reply(event).onCommand(Status.ERROR, statusDetail).send();
            logger.warn(LogHelper.logCommand(event, Status.ERROR, statusDetail));
            e.printStackTrace();
        }
    }
}
