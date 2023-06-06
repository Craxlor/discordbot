package com.github.craxlor.discordbot.database.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class DiscordServer {

    private long guild_id = -1, musicLog_id = -1;
    private String name = null, modules = null, colorHex = null;

    // SETTER
    public void setGuild_id(long guild_id) {
        this.guild_id = guild_id;
    }

    public void setMusicLog_id(long musicLog_id) {
        this.musicLog_id = musicLog_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    // GETTER
    public long getGuild_id() {
        return guild_id;
    }

    public long getMusicLog_id() {
        return musicLog_id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getModules() {
        return modules;
    }

    @Nullable
    public List<String> getModulesAsList() {
        if (modules == null)
            return null;

        String[] array = modules.split(",");
        List<String> list = new ArrayList<>();
        for (String module : array) {
            list.add(module);
        }
        return list;
    }

    @Nullable
    public String getColorHex() {
        return colorHex;
    }

}
