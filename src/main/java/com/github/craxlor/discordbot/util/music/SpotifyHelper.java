package com.github.craxlor.discordbot.util.music;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.core5.http.ParseException;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.github.craxlor.discordbot.util.Properties;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

public class SpotifyHelper {

    public static SpotifyApi getSpotifyApi() throws ParseException, SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(Properties.get("SPOTIFY_CLIENT_ID"))
                .setClientSecret(Properties.get("SPOTIFY_CLIENT_SECRET"))
                .build();
        // clientcredentials
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        ClientCredentials clientCredentials = clientCredentialsRequest.execute();
        spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        return spotifyApi;

    }

    @Nullable
    public static Track getTrack(@Nonnull String url) {
        try {
            String trackID = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
            SpotifyApi spotifyApi = getSpotifyApi();
            GetTrackRequest trackRequest = spotifyApi.getTrack(trackID).build();
            return trackRequest.execute();
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
            MDC.put("filename", "spotify");
            LoggerFactory.getLogger("sift").warn(e.getMessage());
            return null;
        }

    }
}
