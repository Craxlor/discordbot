package com.github.craxlor.discordbot.command.module.core.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.util.Properties;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.reply.LogHelper;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Shutdown extends SlashCommand {

    @Override
    @Nonnull
    public String getName() {
        return "shutdown";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "shutdown discordbot";
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(SlashCommandInteractionEvent event) throws Exception {
        Reply reply = new Reply(event);
        reply.setEphemeral(true);
        if (event.getUser().getIdLong() != Properties.DEV_ID) {
            return reply.onCommand(Status.FAIL, "you cannot do this");
        }
        reply = new Reply(event).onCommand(Status.SUCCESS, "The Bot is going offline!");

        reply.send();
        Database.getInstance().closeConnection();
        GuildManager.getGuildManager(event.getGuild()).getLogger().info(LogHelper.logCommand(event, Status.SUCCESS, "successful execution"));
        Thread.sleep(1000);
        event.getJDA().shutdown();
        System.exit(0);
        return null;
    }

    @Override
    public boolean isGuildOnly() {
        return false;
    }

    public boolean userHasPermission(@Nonnull User user) {
        return user.getIdLong() == Properties.DEV_ID;
    }

}
