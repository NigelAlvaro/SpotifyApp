package dev.nigel.SpotifyApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

@RestController
public class SpotifyController {

    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/{trackName}/{artistName}")
    public ResponseEntity<byte[]> getAlbumCover(@PathVariable String trackName, @PathVariable String artistName) {
        Optional<String> albumCoverUrlOptional = spotifyService.getAlbumCover(trackName, artistName);
        if (albumCoverUrlOptional.isPresent()) {
            try {
                URL url = new URL(albumCoverUrlOptional.get());
                InputStream inputStream = url.openStream();
                byte[] imageBytes = StreamUtils.copyToByteArray(inputStream);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
