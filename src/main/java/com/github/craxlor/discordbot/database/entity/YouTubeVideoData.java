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
@Table(name = "ytVideos")
public class YouTubeVideoData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "channel_id")
    private String channel_id;

    @Column(name = "video_title")
    private String video_title;

    public String getVideoThumbnailURL() {
        return "https://i.ytimg.com/vi/" + id + "/hqdefault.jpg";
    }

    public String getVideoURL() {
        return "https://www.youtube.com/watch?v=" + id;
    }

    public String getChannelURL() {
        return "https://www.youtube.com/channel/" + channel_id;
    }
}
