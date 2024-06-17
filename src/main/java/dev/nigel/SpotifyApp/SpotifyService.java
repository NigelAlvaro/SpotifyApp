package dev.nigel.SpotifyApp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private URI redirectUri;

    private static final Logger LOGGER = Logger.getLogger(SpotifyService.class.getName());

    private SpotifyApi spotifyApi;

    private void initializeSpotifyApi() {
        if (spotifyApi == null) {
            spotifyApi = new SpotifyApi.Builder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRedirectUri(redirectUri)
                    .build();
            authenticate();
        }
    }

    private void authenticate() {
        try {
            final String accessToken = spotifyApi.clientCredentials().build().execute().getAccessToken();
            spotifyApi.setAccessToken(accessToken);
        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to authenticate with Spotify API", e);
        }
    }

    public Optional<String> getAlbumCover(String trackName, String artistName) {
        initializeSpotifyApi();
        String query = String.format("track:%s artist:%s", trackName, artistName);

        try {
            SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(query).build();
            Paging<Track> trackPaging = searchTracksRequest.execute();

            if (trackPaging.getItems().length > 0) {
                Track track = trackPaging.getItems()[0];
                Image[] images = track.getAlbum().getImages();
                if (images.length > 0) {
                    return Optional.of(images[0].getUrl());
                }
            }
        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
