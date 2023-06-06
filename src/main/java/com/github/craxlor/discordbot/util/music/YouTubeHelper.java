package com.github.craxlor.discordbot.util.music;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.github.craxlor.discordbot.database.entity.YouTubeSearch;
import com.github.craxlor.discordbot.database.handler.DBYoutubeSearchHandler;
import com.github.craxlor.discordbot.util.Properties;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

public class YouTubeHelper {
    public static final String YOUTUBE_VIDEO_PREFIX = "https://www.youtube.com/watch?v=";
    public static final String YOUTUBE_CHANNEL_PREFIX = "https://www.youtube.com/channel/";

    private static YouTube getService() throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
            }
        };
        YouTubeRequestInitializer youTubeRequestInitializer = new YouTubeRequestInitializer(
                Properties.get("YOUTUBE_API_KEY"));
        return new YouTube.Builder(httpTransport, GsonFactory.getDefaultInstance(), httpRequestInitializer)
                .setApplicationName("selfmadecrapcode")
                .setYouTubeRequestInitializer(youTubeRequestInitializer).build();

    }

    /**
     * 
     * @param searchTerm
     * @return null on error
     *         <p>
     *         String array with 5 entries (searchTerm, videoTitle, videoId,
     *         channelId, thumbnailUrl)
     *         <p>
     *         String array with 1 entry (quota) if quota limit has been
     *         reached
     */
    @Nullable
    public static YouTubeSearch findVideo(@Nonnull String searchTerm) {
        DBYoutubeSearchHandler dbYoutubeSearchHandler = new DBYoutubeSearchHandler();
        YouTubeSearch youTubeSearch = dbYoutubeSearchHandler.getYouTubeSearchBySearchTerm(searchTerm);
        // found searchTerm in database -> reuse reult to save on quotas
        if (youTubeSearch != null)
            return youTubeSearch;

        try {
            YouTube.Search.List request = getService().search().list("snippet");
            SearchListResponse response = request
                    .setQ(searchTerm)
                    .setOrder("relevance")
                    .setSafeSearch("none")
                    .setType("video")
                    .execute();
            // get all searchresults
            List<SearchResult> SearchResults = response.getItems();
            for (SearchResult searchResult : SearchResults) {
                int searchTermPartMatches = 0;
                for (String searchTermPart : searchTerm.split(" ")) {
                    for (String titlePart : searchResult.getSnippet().getTitle().split(" ")) {
                        if (titlePart.equalsIgnoreCase(searchTermPart)) {
                            searchTermPartMatches++;
                        }
                    }
                }
                // save result if more than half of the searchTermParts have been found in the
                // videoTitle
                if (searchTermPartMatches >= searchTerm.split(" ").length / 2f) {
                    youTubeSearch = new YouTubeSearch();
                    youTubeSearch.setVideo_id(searchResult.getId().getVideoId());
                    youTubeSearch.setVideo_title(searchResult.getSnippet().getTitle());
                    youTubeSearch.setChannel_id(searchResult.getSnippet().getChannelId());
                    youTubeSearch.setSearchTerm(searchTerm);
                    dbYoutubeSearchHandler.insert(youTubeSearch);
                    return youTubeSearch;
                }
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            MDC.put("filename", "youtube");
            LoggerFactory.getLogger("sift").warn(e.getMessage());
        }
        return null;
    }
}
