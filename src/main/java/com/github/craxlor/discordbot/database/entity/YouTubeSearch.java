package com.github.craxlor.discordbot.database.entity;

public class YouTubeSearch {
    private String video_id, video_title, channel_id, searchTerm;

    // SETTER
    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    // GETTER

    public String getVideo_id() {
        return video_id;
    }

    public String getVideo_title() {
        return video_title;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public String getVideoThumbnailURL() {
        return "https://i.ytimg.com/vi/" + video_id + "/hqdefault.jpg";
    }

    public String getVideoURL() {
        return "https://www.youtube.com/watch?v=" + video_id;
    }

    public String getChannelURL() {
        return "https://www.youtube.com/channel/" + channel_id;
    }
}
