package com.github.craxlor.discordbot.listener;

import java.util.List;

import javax.annotation.Nonnull;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.github.craxlor.discordbot.command.module.autoroom.slash.Setup;
import com.github.craxlor.discordbot.database.Database;
import com.github.craxlor.discordbot.database.entity.AutoroomChannel;
import com.github.craxlor.discordbot.database.entity.AutoroomTrigger;
import com.github.craxlor.discordbot.database.handler.DBAutoroomChannelHandler;
import com.github.craxlor.discordbot.database.handler.DBAutoroomTriggerHandler;
import com.github.craxlor.discordbot.database.handler.DBGuildHandler;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoroomHandler extends ListenerAdapter {
    DBAutoroomChannelHandler dbAutoroomChannelHandler;
    DBAutoroomTriggerHandler dbAutoroomTriggerHandler;
    DBGuildHandler dbGuildHandler;

    public AutoroomHandler() {
        dbAutoroomChannelHandler = new DBAutoroomChannelHandler();
        dbAutoroomTriggerHandler = new DBAutoroomTriggerHandler();
        dbGuildHandler = new DBGuildHandler();
    }

    @Override
    @SuppressWarnings("null")
    public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {
        // init database session
        Session session = Database.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        VoiceChannel voiceChannel;
        // create an autoroomChannel whenever a member joins in an autoroomTrigger
        ChannelJoined: if ((voiceChannel = (VoiceChannel) event.getChannelJoined()) != null) {
            AutoroomTrigger autoroomTrigger;
            // check if channel is an autoroomTrigger
            if ((autoroomTrigger = dbAutoroomTriggerHandler.getEntity(session, voiceChannel.getIdLong())) == null)
                break ChannelJoined;
            Member member = event.getMember();
            Guild guild = event.getGuild();
            // parse name
            String namingPattern = autoroomTrigger.getNaming_pattern();
            if (namingPattern.contains("number")) {
                int autoroomChannels = dbAutoroomChannelHandler
                        .getAutoroomChannelsByAutoroomTrigger(session, autoroomTrigger.getId()).size();
                namingPattern = namingPattern.replace("number", Integer.toString(autoroomChannels + 1));
            }
            if (namingPattern.contains("username")) {
                // use username as dynamicName
                namingPattern = namingPattern.replace("username",
                        member.getNickname() != null ? member.getNickname() : member.getEffectiveName());
            }
            Category category = guild.getCategoryById(autoroomTrigger.getCategory_id());
            // create new autoroom
            VoiceChannel autoroom = null;
            switch (autoroomTrigger.getInheritance()) {
                case Setup.CHOICE_TRIGGER -> {
                    autoroom = guild.createCopyOfChannel(voiceChannel).setName(namingPattern).setParent(category)
                            .complete();
                }
                case Setup.CHOICE_CATEGORY -> {
                    autoroom = guild.createVoiceChannel(namingPattern, category).complete();
                }
                default -> {
                    autoroom = guild.createVoiceChannel(namingPattern, category).complete();
                }
            }
            // move member to new voiceChannel
            guild.moveVoiceMember(member, autoroom).queue();
            // channel author is allowed to customize the channel
            autoroom.upsertPermissionOverride(member).setAllowed(Permission.MANAGE_CHANNEL).queue();
            // inherit user limit from trigger channel
            autoroom.getManager().setUserLimit(voiceChannel.getUserLimit()).queue();
            // add created voiceChannel to database
            AutoroomChannel autoroomChannel = new AutoroomChannel();
            autoroomChannel.setAutoroomChannels_guild(dbGuildHandler.getEntity(session, guild.getIdLong()));
            autoroomChannel.setId(autoroom.getIdLong());
            autoroomChannel.setAutoroomTrigger(autoroomTrigger);
            dbAutoroomChannelHandler.insert(session, autoroomChannel);
        }

        // delete dynamic voice channel if it is empty
        if ((voiceChannel = (VoiceChannel) event.getChannelLeft()) != null) {
            // check if channel is an autoroom
            if (dbAutoroomChannelHandler.getEntity(session, voiceChannel.getIdLong()) != null) {
                // delete autoroom if the last member left
                if (voiceChannel.getMembers().size() < 1) {
                    dbAutoroomChannelHandler.remove(session, voiceChannel.getIdLong());
                    voiceChannel.delete().queue();
                }
            }
        }
        // close database session
        transaction.commit();
        Database.getSessionFactory().getCurrentSession().close();
    }

    @Override
    public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {
        // init database session
        Session session = Database.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        long channelID = event.getChannel().getIdLong();
        if (dbAutoroomTriggerHandler.getEntity(session, channelID) != null)
            dbAutoroomTriggerHandler.remove(session, channelID);
        if (dbAutoroomChannelHandler.getEntity(session, channelID) != null)
            dbAutoroomChannelHandler.remove(session, channelID);
        // close database session
        transaction.commit();
        Database.getSessionFactory().getCurrentSession().close();
    }

    @Override
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        // init database session
        Session session = Database.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Guild guild = event.getGuild();
        long autoroomID;
        VoiceChannel autoroom;
        List<AutoroomChannel> autoroomChannels = dbAutoroomChannelHandler.getAutoroomChannelsByGuild(session, guild.getIdLong());
        if (autoroomChannels != null && !autoroomChannels.isEmpty() && autoroomChannels.size() >= 1) {
            for (AutoroomChannel autoroomChannel : autoroomChannels) {
                autoroomID = autoroomChannel.getId();
                autoroom = guild.getVoiceChannelById(autoroomID);
                if (autoroom == null) // remove db entry if the channel has been deleted manually
                    dbAutoroomChannelHandler.remove(session, autoroomID);
                else if (autoroom.getMembers().size() < 1) {
                    autoroom.delete().queue();
                    dbAutoroomChannelHandler.remove(session, autoroomID);
                }
            }
        }
        // close database session
        transaction.commit();
        Database.getSessionFactory().getCurrentSession().close();
    }
}
