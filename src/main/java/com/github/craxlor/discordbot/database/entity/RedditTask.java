package com.github.craxlor.discordbot.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "redditTasks")
public class RedditTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "channel_id")
    private long channel_id;

    @Column(name = "guild_id")
    private long guild_id;

    @Column(name = "period")
    private long period; // period in millis

    @Column(name = "subreddit")
    private String subreddit;

    @Column(name = "firstTime")
    private String firstTime; // base64

    public boolean equals(RedditTask redditTask) {
        if (guild_id == redditTask.getGuild_id() && channel_id == redditTask.getChannel_id()
                && subreddit.equalsIgnoreCase(redditTask.getSubreddit()))
            return true;

        return false;
    }
}
