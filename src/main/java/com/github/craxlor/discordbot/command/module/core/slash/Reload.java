package com.github.craxlor.discordbot.command.module.core.slash;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.Commandlist;
import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.util.Properties;
import com.github.craxlor.discordbot.util.core.GuildManager;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Reload extends SlashCommand {

    @Override
    @Nonnull
    public String getName() {
        return "reload";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Reload guild commands. Don't overuse this command!";
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(SlashCommandInteractionEvent event) throws Exception {
        Member member = event.getMember();
        if (member.hasPermission(Permission.ADMINISTRATOR) == false || member.getIdLong() != Properties.DEV_ID) {
            return new Reply(event).onCommand(Status.FAIL, "you are not allowed to do that");
        }
        Guild guild = event.getGuild();
        Commandlist guildCommands = GuildManager.getGuildManager(guild).getCommandlist().getGuildCommands();
        
        // update commandlist
        // guild.updateCommands().queue();
        guild.updateCommands().addCommands(guildCommands.getCommandData()).queue();

        return new Reply(event).onCommand(Status.SUCCESS, "The Commandlist has been reloaded!");
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }
}
