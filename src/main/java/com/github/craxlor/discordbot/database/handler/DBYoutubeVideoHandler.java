package com.github.craxlor.discordbot.database.handler;

import javax.annotation.Nullable;

import org.hibernate.Session;

import com.github.craxlor.discordbot.database.entity.YouTubeSearchData;
import com.github.craxlor.discordbot.database.entity.YouTubeVideoData;

public class DBYoutubeVideoHandler implements EntityHandler<YouTubeVideoData> {

    @Override
    public YouTubeVideoData getEntity(Session session, long id) {
        throw new UnsupportedOperationException("Unimplemented method 'getEntity'");
    }

    public YouTubeVideoData getEntity(Session session, String id) {
        return session.get(YouTubeVideoData.class, id);
    }

    @Override
    public void remove(Session session, long id) {
        YouTubeVideoData youTubeVideoData = (YouTubeVideoData) session.get(YouTubeVideoData.class, id);
        session.remove(youTubeVideoData);
    }

    @Nullable
    public YouTubeVideoData getYouTubeVideoDataByYouTubeSearchData(Session session, String searchTerm) {
        YouTubeSearchData youTubeSearchData = (YouTubeSearchData) session.get(YouTubeSearchData.class, searchTerm);
        return youTubeSearchData != null ? youTubeSearchData.getYouTubeVideoData() : null;
    }

}
