package com.github.craxlor.discordbot.database.entity;

public class AutoroomChannel {
    private long channel_id, trigger_id, guild_id;

    // SETTER
    public void setChannel_id(long channel_id) {
        this.channel_id = channel_id;
    }

    public void setTrigger_id(long trigger_id) {
        this.trigger_id = trigger_id;
    }

    public void setGuild_id(long guild_id) {
        this.guild_id = guild_id;
    }

    // GETTER
    public long getChannel_id() {
        return channel_id;
    }

    public long getTrigger_id() {
        return trigger_id;
    }

    public long getGuild_id() {
        return guild_id;
    }

}
