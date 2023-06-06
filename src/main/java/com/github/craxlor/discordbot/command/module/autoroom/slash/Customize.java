package com.github.craxlor.discordbot.command.module.autoroom.slash;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nonnull;

import com.github.craxlor.discordbot.command.slash.SlashCommand;
import com.github.craxlor.discordbot.database.handler.DBAutoroomChannelHandler;
import com.github.craxlor.discordbot.util.reply.Reply;
import com.github.craxlor.discordbot.util.reply.Status;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.managers.channel.concrete.VoiceChannelManager;

public class Customize extends SlashCommand {

    private static final String NAME_NAME = "name";
    private static final String NAME_DESCRIPTION = "Renames the current channel. This can only be called 2 times within 10 minutes per channel!";
    private static final String NAME_OPT_NAME = "name";
    private static final String NAME_OPT_DESCRIPTION = "Insert the new channel name.";
    private static final String SIZE_NAME = "size";
    private static final String SIZE_DESCRIPTION = "Sets the channel size.";
    private static final String SIZE_OPT_NAME = "size";
    private static final String SIZE_OPT_DESCRIPTION = "Enter a number between 1 and 99.";
    private static final String LOCK_NAME = "lock";
    private static final String LOCK_DESCRIPTION = "Toggles whether the channel is locked or unlocked.";
    private static final String LOCK_OPT_NAME = "lock";
    private static final String LOCK_OPT_DESCRIPTION = "Select True to lock or False to unlock the channel.";

    public Customize() {
        // rename subcommand
        SubcommandData name = new SubcommandData(NAME_NAME, NAME_DESCRIPTION);
        name.addOption(OptionType.STRING, NAME_OPT_NAME, NAME_OPT_DESCRIPTION, true);
        // size subcommand
        SubcommandData size = new SubcommandData(SIZE_NAME, SIZE_DESCRIPTION);
        size.addOption(OptionType.INTEGER, SIZE_OPT_NAME, SIZE_OPT_DESCRIPTION, true);
        SubcommandData lock = new SubcommandData(LOCK_NAME, LOCK_DESCRIPTION);
        lock.addOption(OptionType.BOOLEAN, LOCK_OPT_NAME, LOCK_OPT_DESCRIPTION, true);
        commandData.addSubcommands(name, size, lock);
    }

    @Override
    @Nonnull
    public String getName() {
        return "channel";
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "customize the autoroom channel you're in";
    }

    @Override
    @SuppressWarnings("null")
    public Reply execute(SlashCommandInteractionEvent event) throws Exception {
        if (canBeExecuted(event.getMember(), event.getGuild()) == false) {
            return new Reply(event).onCommand(Status.FAIL, "you have to be in an autoroom channel");
        }
        String subcommandName = event.getSubcommandName();
        Guild guild = event.getGuild();
        // check if user is in a dynamic channel
        VoiceChannel autoroom = event.getMember().getVoiceState().getChannel().asVoiceChannel();
        VoiceChannelManager autoroomManager = autoroom.getManager();
        String msg = "";
        if (subcommandName.equals(NAME_NAME)) {
            String name = event.getOption(NAME_OPT_NAME).getAsString();
            String oldName = autoroom.getName();
            autoroomManager.setName(name);
            msg = "The channel name has been updated from **" + oldName + "** to **" + name + "**."
                    + "\nThis can only be called 2 times within 10 minutes per channel!";
        } else if (subcommandName.equals(SIZE_NAME)) {
            int size = event.getOption(SIZE_OPT_NAME).getAsInt();
            if (size < 2)
                size = 2;
            else if (size > 99)
                size = 99;
            autoroomManager.setUserLimit(size);
            msg = "The channel size has been updated to " + size + ".";
        } else if (subcommandName.equals(LOCK_NAME)) {
            boolean bool = event.getOption(LOCK_OPT_NAME).getAsBoolean();
            List<PermissionOverride> rolePermissionOverrides = autoroom.getRolePermissionOverrides();
            if (bool) {
                // allow bot to join/see the channel
                autoroomManager.putMemberPermissionOverride(event.getJDA().getSelfUser().getIdLong(),
                        EnumSet.of(Permission.VOICE_CONNECT), null);
                // deny channel access for @everyone
                autoroomManager.putRolePermissionOverride(guild.getPublicRole().getIdLong(), null,
                        EnumSet.of(Permission.VOICE_CONNECT));
                // deny channel access for every role that has special permissions at the moment
                for (PermissionOverride permissionOverride : rolePermissionOverrides) {
                    permissionOverride.getManager().setDenied(Permission.VOICE_CONNECT).queue();
                }
                msg = "The channel has been **locked** for everyone.\nAdmins are still able to join this channel.";
            } else {
                // reset access for @everyone
                autoroom.getPermissionOverride(guild.getPublicRole()).delete().queue();
                // reset access for every other role
                for (PermissionOverride permissionOverride : rolePermissionOverrides) {
                    permissionOverride.getManager().clear(Permission.VOICE_CONNECT).queue();
                }
                msg = "The channel has been **unlocked**.\nEveryone will be able to join this channel.";
            }
        }
        autoroomManager.queue();
        return new Reply(event).onCommand(Status.SUCCESS, msg);
    }

    public boolean canBeExecuted(@Nonnull Member member, @Nonnull Guild guild) {
        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null)
            return false;
        VoiceChannel vc = (VoiceChannel) voiceState.getChannel();
        if (vc == null)
            return false;
        if (new DBAutoroomChannelHandler().getEntity(vc.getIdLong()) != null)
            return true;
        return false;
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

}
