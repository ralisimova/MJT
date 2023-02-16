package bg.sofia.uni.fmi.mjt.spotify.exception;

public class PlaylistNotFound extends SpotifyException{
    public PlaylistNotFound() {
    }

    public PlaylistNotFound(String message) {
        super(message);
    }
}
