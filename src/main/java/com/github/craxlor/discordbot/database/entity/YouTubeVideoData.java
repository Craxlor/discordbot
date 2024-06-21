package com.github.craxlor.discordbot.database.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ytVideos")
public class YouTubeVideoData {

    @Id
    private String id;

    @Column(name = "channel_id")
    private String channel_id;

    @Column(name = "video_title")
    private String video_title;

    @OneToMany(mappedBy = "youTubeVideoData")
    private List<YouTubeSearchData> youTubeSearchDataList;

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
