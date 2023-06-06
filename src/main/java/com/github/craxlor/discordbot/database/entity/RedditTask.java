package com.github.craxlor.discordbot.database.entity;

public class RedditTask {
    private long channel_id, guild_id, period; // period in millis
    private String subreddit, firstTime; // base64

    // public RedditTask(long channel_id, String subreddit, String firstTime, long period, long guild_id) {
    //     this.channel_id = channel_id;
    //     this.guild_id = guild_id;
    //     this.subreddit = subreddit;
    //     this.firstTime = firstTime;
    //     this.period = period;
    // }

    // SETTER
    public void setChannel_id(long channel_id) {
        this.channel_id = channel_id;
    }

    public void setGuild_id(long guild_id) {
        this.guild_id = guild_id;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    // GETTER
    public long getChannel_id() {
        return channel_id;
    }

    public long getGuild_id() {
        return guild_id;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public long getPeriod() {
        return period;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public boolean equals(RedditTask redditTask) {
        if (guild_id == redditTask.getGuild_id() && channel_id == redditTask.getChannel_id()
                && subreddit.equalsIgnoreCase(redditTask.getSubreddit()))
            return true;

        return false;
    }
}
