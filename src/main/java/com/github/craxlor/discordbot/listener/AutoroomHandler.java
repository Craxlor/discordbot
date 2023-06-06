package com.github.craxlor.discordbot.listener;

import java.util.List;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.module.autoroom.slash.Setup;
import com.github.craxlor.discordbot.database.entity.AutoroomChannel;
import com.github.craxlor.discordbot.database.entity.AutoroomTrigger;
import com.github.craxlor.discordbot.database.handler.DBAutoroomChannelHandler;
import com.github.craxlor.discordbot.database.handler.DBAutoroomTriggerHandler;

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

    public AutoroomHandler() {
        dbAutoroomChannelHandler = new DBAutoroomChannelHandler();
        dbAutoroomTriggerHandler = new DBAutoroomTriggerHandler();
    }

    @Override
    @SuppressWarnings("null")
    public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {
        VoiceChannel voiceChannel;
        // create an autoroomChannel whenever a member joins in an autoroomTrigger
        ChannelJoined:
        if ((voiceChannel = (VoiceChannel) event.getChannelJoined()) != null) {
            AutoroomTrigger autoroomTrigger;
            // check if channel is an autoroomTrigger
            if ((autoroomTrigger = new DBAutoroomTriggerHandler().getEntity(voiceChannel.getIdLong())) == null)
            break ChannelJoined;
            Member member = event.getMember();
            Guild guild = event.getGuild();
            // parse name
            String namingPattern = autoroomTrigger.getNaming_pattern();
            if (namingPattern.contains("number")) {
                int autoroomChannels = dbAutoroomChannelHandler
                        .countAutoroomChannelsByTrigger(autoroomTrigger.getTrigger_id());
                namingPattern = namingPattern.replace("number", Integer.toString(autoroomChannels + 1));
            }
            if (namingPattern.contains("username")) {
                // use username as dynamicName
                namingPattern = namingPattern.replace("username", member.getEffectiveName());
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
            autoroomChannel.setGuild_id(guild.getIdLong());
            autoroomChannel.setChannel_id(autoroom.getIdLong());
            autoroomChannel.setTrigger_id(autoroomTrigger.getTrigger_id());
            dbAutoroomChannelHandler.insert(autoroomChannel);
        }

        // delete dynamic voice channel if it is empty
        if ((voiceChannel = (VoiceChannel) event.getChannelLeft()) != null) {
            // check if channel is an autoroom
            if (dbAutoroomChannelHandler.getEntity(voiceChannel.getIdLong()) == null)
                return;
            // delete autoroom if the last member left
            if (voiceChannel.getMembers().size() < 1) {
                dbAutoroomChannelHandler.remove(voiceChannel.getIdLong());
                voiceChannel.delete().queue();
            }
        }
    }

    @Override
    public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {
        long channelID = event.getChannel().getIdLong();
        if (dbAutoroomTriggerHandler.getEntity(channelID) != null)
            dbAutoroomTriggerHandler.remove(channelID);
        if (dbAutoroomChannelHandler.getEntity(channelID) != null)
            dbAutoroomChannelHandler.remove(channelID);

    }

    @Override
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        long autoroomID;
        VoiceChannel autoroom;
        List<AutoroomChannel> autoroomChannels = dbAutoroomChannelHandler.getAutoroomChannelsByGuild(guild.getIdLong());
        if (autoroomChannels == null || autoroomChannels.isEmpty() || autoroomChannels.size() < 1)
            return;

        for (AutoroomChannel autoroomChannel : autoroomChannels) {
            autoroomID = autoroomChannel.getChannel_id();
            autoroom = guild.getVoiceChannelById(autoroomID);
            if (autoroom == null) // remove db entry if the channel has been deleted manually
                dbAutoroomChannelHandler.remove(autoroomID);
            else if (autoroom.getMembers().size() < 1) {
                autoroom.delete().queue();
                dbAutoroomChannelHandler.remove(autoroomID);
            }
        }
    }
}
