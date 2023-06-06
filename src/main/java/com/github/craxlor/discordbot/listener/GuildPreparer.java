package com.github.craxlor.discordbot.listener;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.database.entity.DiscordServer;
import com.github.craxlor.discordbot.database.handler.DBGuildHandler;
import com.github.craxlor.discordbot.util.core.GuildManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildPreparer extends ListenerAdapter {

    @Override
    @SuppressWarnings("null")
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        // setup a GuildManager with its necessary components
        Guild guild = event.getGuild();

        DiscordServer discordServer = new DiscordServer();
        discordServer.setGuild_id(guild.getIdLong());
        discordServer.setName(guild.getName());
        new DBGuildHandler().insert(discordServer);

        GuildManager guildManager = GuildManager.getGuildManager(guild);
        guild.updateCommands().addCommands(guildManager.getCommandlist().getGuildCommands().getCommandData()).queue();
        guildManager.getLogger().info("""
                joined a guild
                    Guild: %s
                    Owner: %s""".formatted(guild.getName(), guild.getOwner().getEffectiveName()));  
    }

    @Override
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        GuildManager guildManager = GuildManager.getGuildManager(guild);
        // WIPING ALL COMMANDS RESULTS IN WIPING ALL CUSTOM OVERRIDES ON ALL GUILDS AS WELL --> THAT WOULD BE BAD
        // guild.updateCommands().addCommands().queue();
        guild.updateCommands().addCommands(guildManager.getCommandlist().getGuildCommands().getCommandData()).queue();
    }
}
