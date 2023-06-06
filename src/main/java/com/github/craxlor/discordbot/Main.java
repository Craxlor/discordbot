package com.github.craxlor.discordbot;

import com.github.craxlor.discordbot.command.Commandlist;
import com.github.craxlor.discordbot.listener.AutoroomHandler;
import com.github.craxlor.discordbot.listener.ContextInteractionHandler;
import com.github.craxlor.discordbot.listener.GuildPreparer;
import com.github.craxlor.discordbot.listener.MusicVoiceConnectionHandler;
import com.github.craxlor.discordbot.listener.RedditHandler;
import com.github.craxlor.discordbot.listener.SlashCommandInteractionHandler;
import com.github.craxlor.discordbot.util.Properties;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;

public class Main {
    // TODO explain musiclog behaviour in wiki
    public static void main(String[] args) throws Exception {
        try {
            JDABuilder builder = JDABuilder.createDefault(Properties.get("BOT_TOKEN"));
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            builder.setChunkingFilter(ChunkingFilter.ALL);
            builder.setLargeThreshold(50);
            JDA jda = builder.build();
            jda.addEventListener(new GuildPreparer());
            jda.addEventListener(new SlashCommandInteractionHandler());
            jda.addEventListener(new ContextInteractionHandler());
            jda.addEventListener(new AutoroomHandler());
            jda.addEventListener(new MusicVoiceConnectionHandler());
            jda.addEventListener(new RedditHandler());
            jda.getPresence().setActivity(Activity.listening("Slashcommands!"));
            // WIPING ALL COMMANDS RESULTS IN WIPING ALL CUSTOM OVERRIDES ON ALL GUILDS AS WELL --> THAT WOULD BE BAD
            // jda.updateCommands().addCommands().queue();
            jda.updateCommands().addCommands(Commandlist.getEveryCommand().getGlobalCommands().getCommandData()).queue();
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
