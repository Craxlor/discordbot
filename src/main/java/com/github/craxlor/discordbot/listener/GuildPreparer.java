package com.github.craxlor.discordbot.listener;

import javax.annotation.Nonnull;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.entity.Guild;
import com.github.craxlor.discordbot.database.handler.DBGuildHandler;
import com.github.craxlor.discordbot.util.core.GuildManager;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildPreparer extends ListenerAdapter {

    @Override
    @SuppressWarnings("null")
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        // setup a GuildManager with its necessary components
        net.dv8tion.jda.api.entities.Guild guild = event.getGuild();
        // init database session
        Session session = Database.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        // create database entry object
        Guild guildDB = new Guild();
        guildDB.setId(guild.getIdLong());
        guildDB.setName(guild.getName());
        new DBGuildHandler().insert(session, guildDB);
        // close database session
        transaction.commit();
        Database.getSessionFactory().getCurrentSession().close();

        GuildManager guildManager = GuildManager.getGuildManager(guild);
        guild.updateCommands().addCommands(guildManager.getCommandlist().getGuildCommands().getCommandData()).queue();
        guildManager.getLogger().info("""
                joined a guild
                    Guild: %s
                    Owner: %s""".formatted(guild.getName(), guild.getOwner().getEffectiveName()));
    }

    @Override
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        net.dv8tion.jda.api.entities.Guild guild = event.getGuild();
        // init database session
        Session session = Database.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        DBGuildHandler guildHandler = new DBGuildHandler();
        Guild guildDB = guildHandler.getEntity(session, guild.getIdLong());
        if (guildDB == null) {
            guildDB = new Guild();
            guildDB.setId(guild.getIdLong());
            guildDB.setName(guild.getName());
            guildHandler.insert(session, guildDB);
        }
        // close database session
        transaction.commit();
        Database.getSessionFactory().getCurrentSession().close();

        GuildManager guildManager = GuildManager.getGuildManager(guild);
        // WIPING ALL COMMANDS RESULTS IN WIPING ALL CUSTOM OVERRIDES ON ALL GUILDS AS
        // WELL --> THAT WOULD BE BAD
        // guild.updateCommands().addCommands().queue();
        if (guildManager.getCommandlist().size() > 0)
            guild.updateCommands().addCommands(guildManager.getCommandlist().getGuildCommands().getCommandData())
                    .queue();
    }
}
