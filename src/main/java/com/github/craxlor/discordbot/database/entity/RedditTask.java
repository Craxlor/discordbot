package com.github.craxlor.discordbot.database.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "redditTasks")
public class RedditTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private long id;

    @Column(name = "channel_id")
    @EqualsAndHashCode.Include
    private long channel_id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private Guild redditTasks_guild;

    @Column(name = "period")
    private long period; // period in millis

    @Column(name = "subreddit")
    @EqualsAndHashCode.Include
    private String subreddit;

    @Column(name = "firstTime")
    private String firstTime; // base64

}
