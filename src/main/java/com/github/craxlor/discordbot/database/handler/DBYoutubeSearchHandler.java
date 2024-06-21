package com.github.craxlor.discordbot.database.handler;

import org.hibernate.Session;

import com.github.craxlor.discordbot.database.entity.YouTubeSearchData;

public class DBYoutubeSearchHandler implements EntityHandler<YouTubeSearchData> {

    @Override
    public YouTubeSearchData getEntity(Session session,long id) {
        YouTubeSearchData youTubeSearchData = session.get(YouTubeSearchData.class, id);
        return youTubeSearchData;
    }

    @Override
    public void remove(Session session,long id) {
        YouTubeSearchData youTubeSearchData = (YouTubeSearchData) session.get(YouTubeSearchData.class, id);
        session.remove(youTubeSearchData);
    }


}
