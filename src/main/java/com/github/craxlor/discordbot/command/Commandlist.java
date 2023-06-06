package com.github.craxlor.discordbot.command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.craxlor.discordbot.util.AccessingAllClassesInPackage;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@SuppressWarnings("rawtypes")
public class Commandlist extends ArrayList<Command> {
    private static final String MODULE_PACKAGE = "com.github.craxlor.discordbot.command.module.";
    private static final String SLASH = ".slash";
    private static final String CONTEXT = ".context";

    public static final String CORE = "core";
    public static final String AUTOROOM = "autoroom";
    public static final String MUSIC = "music";
    public static final String REDDIT = "reddit";
    public static final String NEWWORLD = "newworld";

    private static final Commandlist CORE_SLASH = getCommands(CORE + SLASH);
    private static final Commandlist CORE_CONTEXT = getCommands(CORE + CONTEXT);
    private static final Commandlist AUTOROOM_SLASH = getCommands(AUTOROOM + SLASH);
    private static final Commandlist MUSIC_SLASH = getCommands(MUSIC + SLASH);
    private static final Commandlist REDDIT_SLASH = getCommands(REDDIT + SLASH);
    private static final Commandlist NEWWORLD_SLASH = getCommands(NEWWORLD + SLASH);

    public boolean add(String module) {
        boolean b = false;
        switch (module) {
            case CORE -> {
                b = addAll(CORE_SLASH) && addAll(CORE_CONTEXT);
            }
            case AUTOROOM -> b = addAll(AUTOROOM_SLASH);
            case MUSIC -> b = addAll(MUSIC_SLASH);
            case REDDIT -> b = addAll(REDDIT_SLASH);
            case NEWWORLD -> b = addAll(NEWWORLD_SLASH);
        }
        return b;
    }

    public boolean remove(String module) {
        boolean b = false;
        switch (module) {
            case CORE -> {
                b = removeAll(CORE_SLASH) || removeAll(CORE_CONTEXT);
            }
            case AUTOROOM -> b = removeAll(AUTOROOM_SLASH);
            case MUSIC -> b = removeAll(MUSIC_SLASH);
            case REDDIT -> b = removeAll(REDDIT_SLASH);
            case NEWWORLD -> b = removeAll(NEWWORLD_SLASH);
        }
        return b;
    }

    @Nullable
    public Command get(String name) {
        for (Command command : this) {
            if (command.getCommandData().getName().equals(name))
                return command;
        }
        return null;
    }

    @Nonnull
    public Commandlist getGlobalCommands() {
        Commandlist globalCommands = new Commandlist();
        for (Command command : this) {
            if (command.isGuildOnly() == false)
                globalCommands.add(command);
        }
        return globalCommands;
    }

    @Nonnull
    public Commandlist getGuildCommands() {
        Commandlist guildCommands = new Commandlist();
        for (Command slashCommand : this) {
            if (slashCommand.isGuildOnly())
                guildCommands.add(slashCommand);
        }
        return guildCommands;
    }

    public static Commandlist getEveryCommand() {
        Commandlist commands = new Commandlist();
        commands.addAll(CORE_SLASH);
        commands.addAll(CORE_CONTEXT);
        commands.addAll(AUTOROOM_SLASH);
        commands.addAll(MUSIC_SLASH);
        commands.addAll(REDDIT_SLASH);
        commands.addAll(NEWWORLD_SLASH);
        return commands;
    }

    @Nonnull
    public List<CommandData> getCommandData() {
        ArrayList<CommandData> result = new ArrayList<>();
        for (Command command : this) {
            result.add(command.getCommandData());
        }
        return result;
    }

    // -------------------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private static Commandlist getCommands(String module) {
        Commandlist commands = new Commandlist();
        try {
            Set<Class> classes = AccessingAllClassesInPackage
                    .findAllClassesUsingReflectionsLibrary(MODULE_PACKAGE + module);
            for (Class class1 : classes) {
                commands.add((Command) class1.getConstructor().newInstance());
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return commands;
    }
}
